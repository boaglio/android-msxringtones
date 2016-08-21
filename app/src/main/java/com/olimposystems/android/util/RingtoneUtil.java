package com.olimposystems.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.olimposystems.android.R;
public class RingtoneUtil {

	private static final String AUDIO_OGG = "audio/ogg";
	private static final String CATEGORIA = "msx ringtone";


	public boolean saveas(int ressound, String filename, String mimeType,Activity activity ) {

		Log.i(CATEGORIA, "setando ringtone " + filename);


		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES);
		File file = new File(path,filename);

		try {

			Log.i(CATEGORIA,"Acessando: " + path.toString());

			if (!path.exists()) {
				path.mkdirs();
			}

			// save file
			InputStream is = activity.getBaseContext().getResources().openRawResource(ressound);
			OutputStream os = new FileOutputStream(file);
			byte[] data = new byte[is.available()];
			is.read(data);
			os.write(data);
			Log.i(CATEGORIA,"Total em bytes: " + data.length);
			is.close();
			os.close();

			Log.i(CATEGORIA, "path = "+path);
			Log.i(CATEGORIA, "filename = "+filename);

			ContentValues values = new ContentValues();
			values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
			values.put(MediaStore.MediaColumns.TITLE,filename);
			values.put(MediaStore.MediaColumns.SIZE, file.length()  );
			values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
			values.put(MediaStore.Audio.Media.ARTIST, "MSX");
			values.put(MediaStore.Audio.Media.DURATION, 30);
			values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
			values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
			values.put(MediaStore.Audio.Media.IS_ALARM, false);
			values.put(MediaStore.Audio.Media.IS_MUSIC, false);

			Log.i(CATEGORIA, "absolute path="+file.getAbsolutePath());

			Uri uri = MediaStore.Audio.Media.getContentUriForPath(file
					.getAbsolutePath());

			Log.i(CATEGORIA, "cadastrando uri = "+uri);
			int del = activity.getContentResolver().delete(
					uri,
					MediaStore.MediaColumns.DATA + "=\""
							+ file.getAbsolutePath() + "\"", null);

			Log.i(CATEGORIA, "removendo uri = "+del);

			Uri newUri = activity.getContentResolver().insert(uri, values);

			Log.i(CATEGORIA, "cadastrando new uri = "+newUri);

			RingtoneManager.setActualDefaultRingtoneUri(
					activity , RingtoneManager.TYPE_RINGTONE,
					newUri);

			Log.i(CATEGORIA, "ringtone "+filename+ " configurado!");

		} catch (IOException e) {
			Log.w(CATEGORIA, "Erro em " + file, e);
		}

		return true;
	}



	public void setRingtone(int opt, Activity activity) {

		Log.i(CATEGORIA, "opt=" + opt);

		switch (opt) {

		case R.id.botao_use_ringtone_aleste:
			saveas(R.raw.aleste, "aleste", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_antartic:
			saveas(R.raw.antartic, "game_antartic", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_firehawk:
			saveas(R.raw.firehawk, "firehawk", AUDIO_OGG, activity);
			break;
			
		case R.id.botao_use_ringtone_fray:
			saveas(R.raw.fray, "fray", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_golvellius1:
			saveas(R.raw.golvellius, "golvellius", AUDIO_OGG, activity);
			break;
			
		case R.id.botao_use_ringtone_goonies:
			saveas(R.raw.goonies, "goonies", AUDIO_OGG, activity);
			break;
			
		case R.id.botao_use_ringtone_guardic:
			saveas(R.raw.guardic, "guardic", AUDIO_OGG, activity);
			break;
			

		case R.id.botao_use_ringtone_salamander:
			saveas(R.raw.salamander, "salamander", AUDIO_OGG, activity);
			break;
			
		case R.id.botao_use_ringtone_kings:
			saveas(R.raw.kingsvalley, "kings1", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_metalgear:
			saveas(R.raw.metalgear1, "metalgear1", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_nemesis2:
			saveas(R.raw.gradius2, "nemesis2", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_pacmania:
			saveas(R.raw.pacmania, "pacmania", AUDIO_OGG, activity);
			break;
			
		case R.id.botao_use_ringtone_pippols:
			saveas(R.raw.pippols, "pippols", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_sdsnatcher:
			saveas(R.raw.sdsnatcher, "sdsnatcher", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_usas:
			saveas(R.raw.usas, "usas", AUDIO_OGG, activity);
			break;

		case R.id.botao_use_ringtone_zanac:
			saveas(R.raw.zanac, "zanac", AUDIO_OGG, activity);
			break;

		}
	}

}
