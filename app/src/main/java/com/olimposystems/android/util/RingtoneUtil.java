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
	private static final String CATEGORY = "msx ringtone";


	public boolean saveAs(int ressound, String filename, String mimeType,Activity activity ) {

		Log.i(CATEGORY, "setando ringtone " + filename);


		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES);
		File file = new File(path,filename);

		try {

			Log.i(CATEGORY,"Acessando: " + path.toString());

			if (!path.exists()) {
				path.mkdirs();
			}

			// save file
			InputStream is = activity.getBaseContext().getResources().openRawResource(ressound);
			OutputStream os = new FileOutputStream(file);
			byte[] data = new byte[is.available()];
			is.read(data);
			os.write(data);
			Log.i(CATEGORY,"Total em bytes: " + data.length);
			is.close();
			os.close();

			Log.i(CATEGORY, "path = "+path);
			Log.i(CATEGORY, "filename = "+filename);

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

			Log.i(CATEGORY, "absolute path="+file.getAbsolutePath());

			Uri uri = MediaStore.Audio.Media.getContentUriForPath(file
					.getAbsolutePath());

			Log.i(CATEGORY, "cadastrando uri = "+uri);
			int del = activity.getContentResolver().delete(
					uri,
					MediaStore.MediaColumns.DATA + "=\""
							+ file.getAbsolutePath() + "\"", null);

			Log.i(CATEGORY, "removendo uri = "+del);

			Uri newUri = activity.getContentResolver().insert(uri, values);

			Log.i(CATEGORY, "cadastrando new uri = "+newUri);

			RingtoneManager.setActualDefaultRingtoneUri(
					activity , RingtoneManager.TYPE_RINGTONE,
					newUri);

			Log.i(CATEGORY, "ringtone "+filename+ " configurado!");

		} catch (IOException e) {
			Log.w(CATEGORY, "Erro em " + file, e);
		}

		return true;
	}


	public void setRingtone(int opt, Activity activity) {

		Log.i(CATEGORY, "opt=" + opt);

		if (opt == R.id.botao_use_ringtone_aleste)      saveAs(R.raw.aleste, "aleste", AUDIO_OGG, activity);
		if (opt == R.id.botao_use_ringtone_antartic)    saveAs(R.raw.antartic, "antartic", AUDIO_OGG, activity);
		if (opt == R.id.botao_use_ringtone_firehawk) 	saveAs(R.raw.firehawk, "firehawk", AUDIO_OGG, activity);
		if (opt == R.id.botao_use_ringtone_fray)   		saveAs(R.raw.fray, "fray", AUDIO_OGG, activity);
		if (opt == R.id.botao_use_ringtone_golvellius1) saveAs(R.raw.golvellius, "golvellius", AUDIO_OGG, activity);
		if (opt == R.id.botao_use_ringtone_goonies) 	saveAs(R.raw.goonies, "goonies", AUDIO_OGG, activity);
		if (opt == R.id.botao_use_ringtone_guardic) 	saveAs(R.raw.guardic, "guardic", AUDIO_OGG, activity);
		if (opt == R.id.botao_use_ringtone_kings) 		saveAs(R.raw.kingsvalley, "kings1", AUDIO_OGG, activity);
		if (opt == R.id.botao_use_ringtone_metalgear) 	saveAs(R.raw.metalgear1, "metalgear1", AUDIO_OGG, activity);
		if (opt == R.id.botao_use_ringtone_pacmania)  	saveAs(R.raw.pacmania, "pacmania", AUDIO_OGG, activity);
		if (opt == R.id.botao_use_ringtone_pippols) 	saveAs(R.raw.pippols, "pippols", AUDIO_OGG, activity);
		if (opt == R.id.botao_use_ringtone_sdsnatcher) 	saveAs(R.raw.sdsnatcher, "sdsnatcher", AUDIO_OGG, activity);
		if (opt == R.id.botao_use_ringtone_zanac) 		saveAs(R.raw.zanac, "zanac", AUDIO_OGG, activity);
		if (opt == R.id.botao_use_ringtone_usas) 		saveAs(R.raw.usas, "usas", AUDIO_OGG, activity);

//		if (opt == R.id.botao_use_ringtone_nemesis2) 	saveAs(R.raw.nemesis2, "nemesis2", AUDIO_OGG, activity);
//		if (opt == R.id.botao_use_ringtone_magicaltree) saveAs(R.raw.magicaltree, "magicaltree", AUDIO_OGG, activity);
//		if (opt == R.id.botao_use_ringtone_metalgear2) 	saveAs(R.raw.metalgear1b, "metalgear2", AUDIO_OGG, activity);
//		if (opt == R.id.botao_use_ringtone_outrun) 		saveAs(R.raw.outrun, "outrun", AUDIO_OGG, activity);
//		if (opt == R.id.botao_use_ringtone_psychoworld) saveAs(R.raw.psychoworld, "psychoworld", AUDIO_OGG, activity);
//		if (opt == R.id.botao_use_ringtone_starwars) 	saveAs(R.raw.starwars, "starwars", AUDIO_OGG, activity);
//		if (opt == R.id.botao_use_ringtone_undeadline) 	saveAs(R.raw.undeadline, "undeadline", AUDIO_OGG, activity);
//		if (opt == R.id.botao_use_ringtone_vampire) 	saveAs(R.raw.vampire, "vampire", AUDIO_OGG, activity);
//		if (opt == R.id.botao_use_ringtone_xak) 		saveAs(R.raw.xak, "xak", AUDIO_OGG, activity);
//		if (opt == R.id.botao_use_ringtone_higemaru) 	saveAs(R.raw.higemaru, "higemaru", AUDIO_OGG, activity);
//		if (opt == R.id.botao_use_ringtone_jetbomber) 	saveAs(R.raw.jetbomber, "jetbomber", AUDIO_OGG, activity);

	}
}
