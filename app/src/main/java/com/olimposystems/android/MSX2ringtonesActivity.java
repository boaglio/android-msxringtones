package com.olimposystems.android;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.olimposystems.android.util.RingtoneUtil;

public class MSX2ringtonesActivity extends Activity {
    
	private static final String CATEGORIA = "msx2";
	
	private MediaPlayer player;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);

		setContentView(R.layout.msx2_layout);
        
        // Aleste
        registraBotao("Aleste",R.id.botao_play_aleste,R.id.botao_use_ringtone_aleste,R.raw.aleste); 
		
        // Firehawk
        registraBotao("Firehawk",R.id.botao_play_firehawk,R.id.botao_use_ringtone_firehawk,R.raw.firehawk); 
        
        // Fray
        registraBotao("Fray",R.id.botao_play_fray,R.id.botao_use_ringtone_fray,R.raw.fray); 

        // Golvellius
        registraBotao("Golvellius",R.id.botao_play_golvellius1,R.id.botao_use_ringtone_golvellius1,R.raw.golvellius);

        // Metal Gear
        registraBotao("Metal Gear",R.id.botao_play_metalgear,R.id.botao_use_ringtone_metalgear,R.raw.metalgear1);

        // SD Snatcher
        registraBotao("SD Snatcher",R.id.botao_play_sdsnatcher,R.id.botao_use_ringtone_sdsnatcher,R.raw.sdsnatcher); 

        // Treasure of USAS
        registraBotao("Treasure of USAS",R.id.botao_play_usas,R.id.botao_use_ringtone_usas,R.raw.usas); 

		Log.i(CATEGORIA, "tela MSX2 ok");
		
    }
    
    private void registraBotao(final String nome,int botaoPlayId, final int botaoRingtoneId,final int arquivoId) {
    	
		ImageButton botaoImagem = (ImageButton) findViewById(botaoPlayId);
		botaoImagem.setImageResource(R.drawable.speaker_logo);
		botaoImagem.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				if (player!=null &&  player.isPlaying())
					player.stop();
				
				player = MediaPlayer.create(MSX2ringtonesActivity.this,arquivoId);
				Log.i(CATEGORIA, "tocando "+nome+"...");
				player.start();
				
				Toast.makeText(MSX2ringtonesActivity.this, "playing "+nome+" ringtone...", Toast.LENGTH_SHORT).show();
			}
		});


		ImageButton botao = (ImageButton) findViewById(botaoRingtoneId);
		botao.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				RingtoneUtil ringtoneManager = new RingtoneUtil();
				ringtoneManager.setRingtone(botaoRingtoneId, MSX2ringtonesActivity.this);
				
				Toast.makeText(MSX2ringtonesActivity.this, "using "+nome+" ringtone...", Toast.LENGTH_SHORT).show();
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
			startActivity(new Intent(MSX2ringtonesActivity.this,SobreActivity.class));
			
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