package com.olimposystems.android;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.olimposystems.android.type.MSX1Game;
import com.olimposystems.android.util.RingtoneUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MSX1ringtonesActivity extends Activity {
    
	private static final String CATEGORIA = "msx1";

	private static final String AUDIO_OGG = "audio/ogg";


	private MediaPlayer player = new MediaPlayer();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);

        setContentView(R.layout.msx1_layout);

		registraBotaoImagem(MSX1Game.antarticAdventure);
		registraBotaoImagem(MSX1Game.goonies);
		registraBotaoImagem(MSX1Game.guardic);
		registraBotaoImagem(MSX1Game.kingsvalley);
		registraBotaoImagem(MSX1Game.gradius2);
		registraBotaoImagem(MSX1Game.salamander);
		registraBotaoImagem(MSX1Game.pacmania);
		registraBotaoImagem(MSX1Game.pippols);
		registraBotaoImagem(MSX1Game.zanac);
        
		Log.i(CATEGORIA, "tela MSX1 ok");
		
    }

	private void registraBotaoImagem(final MSX1Game game) {

		ImageButton btnTocaRingTone = (ImageButton) findViewById(game.botaoPlayId());
		btnTocaRingTone.setImageResource(R.drawable.speaker_logo);
		btnTocaRingTone.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				if (player!=null &&  player.isPlaying()) {

					Log.i(CATEGORIA, "parando player...");
					player.stop();
				}

				player = MediaPlayer.create(MSX1ringtonesActivity.this,game.arquivoId());
				Log.i(CATEGORIA, "tocando "+game.nome()+"...");
				player.start();

				Toast.makeText(MSX1ringtonesActivity.this, "playing "+game.nome()+" ringtone...", Toast.LENGTH_SHORT).show();

			}
		});

		ImageButton btnConfiguraRingTone = (ImageButton) findViewById(game.botaoRingtoneId());
		btnConfiguraRingTone.setImageResource(R.drawable.checkmark);
		btnConfiguraRingTone.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				RingtoneUtil ringtoneManager = new RingtoneUtil();
				ringtoneManager.setRingtone(game.botaoRingtoneId(), MSX1ringtonesActivity.this);

				Toast.makeText(MSX1ringtonesActivity.this, "using "+game.nome()+" ringtone...", Toast.LENGTH_SHORT).show();
			}
		});
	}
    
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem item = menu.add(0, 1, 0,R.string.sobre);
		item.setIcon(R.drawable.about);
		return true;
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		switch (item.getItemId()) {
		case 1:
			startActivity(new Intent(MSX1ringtonesActivity.this,SobreActivity.class));
			
			return true;
		}
		return false;
	}
	

	@Override
	protected void onPause() {
		super.onPause();
		stopPlay();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		stopPlay();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopPlay();
	}

	private void stopPlay() {
		if (player!=null && player.isPlaying())
			player.stop();
	}

}