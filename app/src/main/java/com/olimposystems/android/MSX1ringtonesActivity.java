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

		// game_antartic adventure
		registraBotaoImagem("Antartic Adventure",R.id.botao_play_antartic,R.id.botao_use_ringtone_antartic,R.raw.antartic);
		
        // goonies
		registraBotaoImagem("Goonies",R.id.botao_play_goonies,R.id.botao_use_ringtone_goonies,R.raw.goonies);
		
        // guardic
		registraBotaoImagem("Guardic",R.id.botao_play_guardic,R.id.botao_use_ringtone_guardic,R.raw.guardic);

        // king's valley
		registraBotaoImagem("King's Valley",R.id.botao_play_kingsv,R.id.botao_use_ringtone_kings,R.raw.kingsvalley);

        // Nemesis 2
		registraBotaoImagem("Nemesis 2",R.id.botao_play_nemesis2,R.id.botao_use_ringtone_nemesis2,R.raw.gradius2);

		// Salamander
		registraBotaoImagem("Salamander",R.id.botao_play_salamander,R.id.botao_use_ringtone_salamander,R.raw.salamander);

		// pacmania
		registraBotaoImagem("Pacmania",R.id.botao_play_pacmania,R.id.botao_use_ringtone_pacmania,R.raw.pacmania);

        // pippols
		registraBotaoImagem("Pippols",R.id.botao_play_pippols,R.id.botao_use_ringtone_pippols,R.raw.pippols);

		// zanac
		registraBotaoImagem("Zanac",R.id.botao_play_zanac,R.id.botao_use_ringtone_zanac,R.raw.zanac);
        
		Log.i(CATEGORIA, "tela MSX1 ok");
		
    }

	private void registraBotaoImagem(final String nome,int botaoPlayId, final int botaoRingtoneId,final int arquivoId) {

		ImageButton btnTocaRingTone = (ImageButton) findViewById(botaoPlayId);
		btnTocaRingTone.setImageResource(R.drawable.speaker_logo);
		btnTocaRingTone.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				if (player!=null &&  player.isPlaying()) {

					Log.i(CATEGORIA, "parando player...");
					player.stop();
				}

				player = MediaPlayer.create(MSX1ringtonesActivity.this,arquivoId);
				Log.i(CATEGORIA, "tocando "+nome+"...");
				player.start();

				Toast.makeText(MSX1ringtonesActivity.this, "playing "+nome+" ringtone...", Toast.LENGTH_SHORT).show();

			}
		});

		ImageButton btnConfiguraRingTone = (ImageButton) findViewById(botaoRingtoneId);
		btnConfiguraRingTone.setImageResource(R.drawable.checkmark);
		btnConfiguraRingTone.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				RingtoneUtil ringtoneManager = new RingtoneUtil();
				ringtoneManager.setRingtone(botaoRingtoneId, MSX1ringtonesActivity.this);

				Toast.makeText(MSX1ringtonesActivity.this, "using "+nome+" ringtone...", Toast.LENGTH_SHORT).show();
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