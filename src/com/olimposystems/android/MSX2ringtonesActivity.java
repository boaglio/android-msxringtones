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
        registraBotao("Golvellius(1)",R.id.botao_play_golvellius1,R.id.botao_use_ringtone_golvellius1,R.raw.golvellius1); 
        registraBotao("Golvellius(2)",R.id.botao_play_golvellius2,R.id.botao_use_ringtone_golvellius2,R.raw.golvellius2); 
        
		// Higemaru
        registraBotao("Higemaru",R.id.botao_play_higemaru,R.id.botao_use_ringtone_higemaru,R.raw.higemaru); 

        // Metal Gear
        registraBotao("Metal Gear(1)",R.id.botao_play_metalgear,R.id.botao_use_ringtone_metalgear,R.raw.metalgear1); 
        registraBotao("Metal Gear(2)",R.id.botao_play_metalgear2,R.id.botao_use_ringtone_metalgear2,R.raw.metalgear1b); 
        
        // Psycho World
        registraBotao("Psycho World",R.id.botao_play_psychoworld,R.id.botao_use_ringtone_psychoworld,R.raw.psychoworld); 
        
        // SD Snatcher
        registraBotao("SD Snatcher",R.id.botao_play_sdsnatcher,R.id.botao_use_ringtone_sdsnatcher,R.raw.sdsnatcher); 

        // Treasure of USAS
        registraBotao("Treasure of USAS",R.id.botao_play_usas,R.id.botao_use_ringtone_usas,R.raw.usas); 
        
        // Undeadline
        registraBotao("Undeadline",R.id.botao_play_undeadline,R.id.botao_use_ringtone_undeadline,R.raw.undeadline); 
        
        // Vampire Killer
        registraBotao("Vampire Killer",R.id.botao_play_vampire,R.id.botao_use_ringtone_vampire,R.raw.vampire); 

		// xak
        registraBotao("Xak",R.id.botao_play_xak,R.id.botao_use_ringtone_xak,R.raw.xak); 
        
		Log.i(CATEGORIA, "tela MSX2 ok");
		
    }
    
    private void registraBotao(final String nome,int botaoPlayId, final int botaoRingtoneId,final int arquivoId) {
    	
		ImageButton botaoImagem = (ImageButton) findViewById(botaoPlayId);
		botaoImagem.setImageResource(R.drawable.msxmusic);
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
		

		Button botao = (Button) findViewById(botaoRingtoneId);
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
		item.setIcon(R.drawable.sobre);
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