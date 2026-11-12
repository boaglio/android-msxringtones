package com.olimposystems.android.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.olimposystems.android.R;

/**
 * Copia um .ogg de res/raw para a pasta pública "Ringtones", registra no MediaStore
 * e o define como toque padrão.
 */
public class RingtoneUtil {

	private static final String AUDIO_OGG = "audio/ogg";
	private static final String CATEGORY = "msx ringtone";
	private static final int REQUEST_STORAGE = 42;

	/**
	 * Define o recurso raw como toque padrão do telefone.
	 *
	 * @return true se o toque foi configurado; false se falta permissão ou houve erro
	 *         (o usuário já foi avisado em ambos os casos).
	 */
	public static boolean setRingtone(Activity activity, int rawId, String title) {

		if (!Settings.System.canWrite(activity)) {
			pedePermissaoConfiguracoes(activity);
			return false;
		}

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
				&& activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			activity.requestPermissions(
					new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_STORAGE);
			Toast.makeText(activity, R.string.permission_retry, Toast.LENGTH_LONG).show();
			return false;
		}

		String fileName = activity.getResources().getResourceEntryName(rawId) + ".ogg";

		try {
			Uri uri = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
					? salvaMediaStore(activity, rawId, fileName, title)
					: salvaLegado(activity, rawId, fileName, title);

			Log.i(CATEGORY, "ringtone uri = " + uri);
			RingtoneManager.setActualDefaultRingtoneUri(activity, RingtoneManager.TYPE_RINGTONE, uri);
			Log.i(CATEGORY, "ringtone " + fileName + " configurado!");
			return true;

		} catch (IOException | SecurityException | IllegalStateException e) {
			Log.w(CATEGORY, "Erro configurando " + fileName, e);
			Toast.makeText(activity, R.string.ringtone_error, Toast.LENGTH_LONG).show();
			return false;
		}
	}

	private static void pedePermissaoConfiguracoes(final Activity activity) {
		new AlertDialog.Builder(activity)
				.setTitle(R.string.permission_title)
				.setMessage(R.string.permission_write_settings)
				.setNegativeButton(android.R.string.cancel, null)
				.setPositiveButton(R.string.open_settings, (dialog, which) -> {
					Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
							Uri.parse("package:" + activity.getPackageName()));
					activity.startActivity(intent);
				})
				.show();
	}

	/** Android 10+: grava via MediaStore em Ringtones/, sem permissão de armazenamento. */
	@TargetApi(Build.VERSION_CODES.Q)
	private static Uri salvaMediaStore(Activity activity, int rawId, String fileName, String title)
			throws IOException {

		ContentResolver resolver = activity.getContentResolver();
		Uri collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
		String relativePath = Environment.DIRECTORY_RINGTONES + "/";

		// Reaproveita o arquivo se já foi criado por esta instalação do app.
		try (Cursor c = resolver.query(collection,
				new String[] { MediaStore.Audio.Media._ID },
				MediaStore.Audio.Media.DISPLAY_NAME + "=? AND " + MediaStore.Audio.Media.RELATIVE_PATH + "=?",
				new String[] { fileName, relativePath }, null)) {
			if (c != null && c.moveToFirst()) {
				Uri existente = Uri.withAppendedPath(collection, String.valueOf(c.getLong(0)));
				Log.i(CATEGORY, "reaproveitando " + existente);
				return existente;
			}
		}

		ContentValues values = new ContentValues();
		values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);
		values.put(MediaStore.Audio.Media.TITLE, title);
		values.put(MediaStore.Audio.Media.MIME_TYPE, AUDIO_OGG);
		values.put(MediaStore.Audio.Media.ARTIST, "MSX");
		values.put(MediaStore.Audio.Media.RELATIVE_PATH, relativePath);
		values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
		values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
		values.put(MediaStore.Audio.Media.IS_ALARM, false);
		values.put(MediaStore.Audio.Media.IS_MUSIC, false);
		values.put(MediaStore.Audio.Media.IS_PENDING, 1);

		Uri uri = resolver.insert(collection, values);
		if (uri == null) {
			throw new IOException("MediaStore insert falhou para " + fileName);
		}

		try (InputStream is = activity.getResources().openRawResource(rawId);
			 OutputStream os = resolver.openOutputStream(uri)) {
			if (os == null) {
				throw new IOException("Não foi possível abrir " + uri);
			}
			copia(is, os);
		} catch (IOException e) {
			resolver.delete(uri, null, null);
			throw e;
		}

		ContentValues pronto = new ContentValues();
		pronto.put(MediaStore.Audio.Media.IS_PENDING, 0);
		resolver.update(uri, pronto, null, null);

		return uri;
	}

	/** Android 6–9: grava o arquivo diretamente e registra pelo caminho (coluna DATA). */
	@SuppressWarnings("deprecation")
	private static Uri salvaLegado(Activity activity, int rawId, String fileName, String title)
			throws IOException {

		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES);
		if (!path.exists() && !path.mkdirs()) {
			throw new IOException("Não foi possível criar " + path);
		}
		File file = new File(path, fileName);

		try (InputStream is = activity.getResources().openRawResource(rawId);
			 OutputStream os = new FileOutputStream(file)) {
			copia(is, os);
		}

		ContentResolver resolver = activity.getContentResolver();
		Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		resolver.delete(collection, MediaStore.MediaColumns.DATA + "=?",
				new String[] { file.getAbsolutePath() });

		ContentValues values = new ContentValues();
		values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
		values.put(MediaStore.MediaColumns.TITLE, title);
		values.put(MediaStore.MediaColumns.SIZE, file.length());
		values.put(MediaStore.MediaColumns.MIME_TYPE, AUDIO_OGG);
		values.put(MediaStore.Audio.Media.ARTIST, "MSX");
		values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
		values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
		values.put(MediaStore.Audio.Media.IS_ALARM, false);
		values.put(MediaStore.Audio.Media.IS_MUSIC, false);

		Uri uri = resolver.insert(collection, values);
		if (uri == null) {
			throw new IOException("MediaStore insert falhou para " + file);
		}
		return uri;
	}

	private static void copia(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[8192];
		int lidos;
		long total = 0;
		while ((lidos = is.read(buffer)) != -1) {
			os.write(buffer, 0, lidos);
			total += lidos;
		}
		Log.i(CATEGORY, "Total em bytes: " + total);
	}
}
