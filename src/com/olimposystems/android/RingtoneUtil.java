package com.olimposystems.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class RingtoneUtil {

	private static final String AUDIO_OGG = "audio/ogg";
	private static final String CATEGORIA = "msx ringtone";

	public boolean saveas(int ressound, String filename, String mimeType,
			Activity activity) {

		Log.i(CATEGORIA, "setando ringtone " + filename);

		byte[] buffer = null;
		InputStream fIn = activity.getBaseContext().getResources()
				.openRawResource(ressound);
		int size = 0;

		try {
			size = fIn.available();
			buffer = new byte[size];
			fIn.read(buffer);
			fIn.close();
		} catch (IOException e) {
			return false;
		}

		Log.i(CATEGORIA, "Analisando...");

		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/media/audio/ringtones/";

		boolean exists = (new File(path)).exists();
		Log.i(CATEGORIA, "arquivo " + filename + " existe ? " + exists);
		if (!exists) {
			new File(path).mkdirs();
		}

		FileOutputStream save;
		try {
			save = new FileOutputStream(path + filename);
			save.write(buffer);
			save.flush();
			save.close();
		} catch (FileNotFoundException e) {
			Log.e(CATEGORIA, "arquivo " + filename + " nao encontrado!", e);
			return false;
		} catch (IOException e) {
			Log.e(CATEGORIA, "erro de E/S no arquivo " + filename, e);
			return false;
		}

		Log.i(CATEGORIA, "arquivo " + filename + "gravado no sdcard!");

		activity.sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
						+ path + filename)));

		File k = new File(path, filename);

		ContentValues values = new ContentValues();
		values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
		values.put(MediaStore.MediaColumns.TITLE, "MSX Ringtone [" + filename
				+ "]");
		values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
		values.put(MediaStore.Audio.Media.ARTIST, "pure MSX !");
		values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
		values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
		values.put(MediaStore.Audio.Media.IS_ALARM, false);
		values.put(MediaStore.Audio.Media.IS_MUSIC, true);

		Log.i(CATEGORIA, "cadastrando ringtone ...");

		Uri uri = MediaStore.Audio.Media.getContentUriForPath(k
				.getAbsolutePath());
		Uri newUri = activity.getBaseContext().getContentResolver()
				.insert(uri, values);

		Log.i(CATEGORIA, "ajustando o ringtone como padrao...");

		RingtoneManager.setActualDefaultRingtoneUri(activity.getBaseContext(),
				RingtoneManager.TYPE_RINGTONE, newUri);

		Log.i(CATEGORIA, "ringtone configurado!");

		return true;
	}

	public void setRingtone(int opt, Activity activity) {

		Log.i(CATEGORIA, "opt=" + opt);

		switch (opt) {

		case R.id.botao_use_ringtone_aleste:
			saveas(R.raw.aleste, "aleste", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_antartic:
			saveas(R.raw.antartic, "antartic", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_firehawk:
			saveas(R.raw.firehawk, "firehawk", AUDIO_OGG, activity);
			break;
			
		case R.id.botao_use_ringtone_fray:
			saveas(R.raw.fray, "fray", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_golvellius1:
			saveas(R.raw.golvellius1, "golvellius1", AUDIO_OGG, activity);
			break;
			
		case R.id.botao_use_ringtone_golvellius2:
			saveas(R.raw.golvellius2, "golvellius2", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_goonies:
			saveas(R.raw.goonies, "goonies", AUDIO_OGG, activity);
			break;
			
		case R.id.botao_use_ringtone_guardic:
			saveas(R.raw.guardic, "guardic", AUDIO_OGG, activity);
			break;
			
		case R.id.botao_use_ringtone_guardic2:
			saveas(R.raw.guardic2, "guardic2", AUDIO_OGG, activity);
			break;
			
		case R.id.botao_use_ringtone_higemaru:
			saveas(R.raw.higemaru, "higemaru", AUDIO_OGG, activity);
			break;
			
		case R.id.botao_use_ringtone_jetbomber:
			saveas(R.raw.jetbomber, "jetbomber", AUDIO_OGG, activity);
			break;
			
		case R.id.botao_use_ringtone_kings:
			saveas(R.raw.kingsvalley, "kings1", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_kings2:
			saveas(R.raw.kingsvalley2, "kings2", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_magicaltree:
			saveas(R.raw.magicaltree, "magicaltree", AUDIO_OGG, activity);
			break;
			
		case R.id.botao_use_ringtone_metalgear:
			saveas(R.raw.metalgear1, "metalgear1", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_metalgear2:
			saveas(R.raw.metalgear1b, "metalgear2", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_nemesis2:
			saveas(R.raw.nemesis2, "nemesis2", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_outrun:
			saveas(R.raw.outrun, "outrun", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_pacmania:
			saveas(R.raw.pacmania, "pacmania", AUDIO_OGG, activity);
			break;
			
		case R.id.botao_use_ringtone_pippols:
			saveas(R.raw.pippols, "pippols", AUDIO_OGG, activity);
			break;
			
		case R.id.botao_use_ringtone_psychoworld:
			saveas(R.raw.psychoworld, "psychoworld", AUDIO_OGG, activity);
			break;
			
		case R.id.botao_use_ringtone_sdsnatcher:
			saveas(R.raw.sdsnatcher, "sdsnatcher", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_starwars:
			saveas(R.raw.starwars, "starwars", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_undeadline:
			saveas(R.raw.undeadline, "undeadline", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_usas:
			saveas(R.raw.usas, "usas", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_vampire:
			saveas(R.raw.vampire, "vampire", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_xak:
			saveas(R.raw.xak, "xak", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_zanac:
			saveas(R.raw.zanac, "zanac", AUDIO_OGG, activity);
			break;

		}
	}

}
