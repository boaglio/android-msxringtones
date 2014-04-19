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

public class MSX1ringtonesActivity extends Activity {
    
	private static final String CATEGORIA = "msx1";
	
	private MediaPlayer player;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.msx1_layout);
        
		// antartic adventure
        registraBotao("Antartic Adventure",R.id.botao_play_antartic,R.id.botao_use_ringtone_antartic,R.raw.antartic); 
		
        // goonies
        registraBotao("Goonies",R.id.botao_play_goonies,R.id.botao_use_ringtone_goonies,R.raw.goonies);
		
        // guardic
        registraBotao("Guardic(1)",R.id.botao_play_guardic,R.id.botao_use_ringtone_guardic,R.raw.guardic);
        registraBotao("Guardic(2)",R.id.botao_play_guardic2,R.id.botao_use_ringtone_guardic2,R.raw.guardic2);
        
        // jetbomber
        registraBotao("Jet Bomber",R.id.botao_play_jetbomber,R.id.botao_use_ringtone_jetbomber,R.raw.jetbomber);
        
        // king's valley
        registraBotao("King's Valley(1)",R.id.botao_play_kingsv,R.id.botao_use_ringtone_kings,R.raw.kingsvalley);
        registraBotao("King's Valley(2)",R.id.botao_play_kingsv2,R.id.botao_use_ringtone_kings2,R.raw.kingsvalley2);

        // Magical Tree
        registraBotao("Magical Tree",R.id.botao_play_magicaltree,R.id.botao_use_ringtone_magicaltree,R.raw.magicaltree);
        
        // Outrun
        registraBotao("Outrun",R.id.botao_play_outrun,R.id.botao_use_ringtone_outrun,R.raw.outrun);

        // Nemesis 2
        registraBotao("Nemesis 2",R.id.botao_play_nemesis2,R.id.botao_use_ringtone_nemesis2,R.raw.nemesis2);
        
        // pacmania
        registraBotao("Pacmania",R.id.botao_play_pacmania,R.id.botao_use_ringtone_pacmania,R.raw.pacmania);

        // pippols
        registraBotao("Pippols",R.id.botao_play_pippols,R.id.botao_use_ringtone_pippols,R.raw.pippols);

        // star wars
        registraBotao("Star Wars",R.id.botao_play_starwars,R.id.botao_use_ringtone_starwars,R.raw.starwars);
        
		// zanac
        registraBotao("Zanac",R.id.botao_play_zanac,R.id.botao_use_ringtone_zanac,R.raw.zanac);
        
		Log.i(CATEGORIA, "tela MSX1 ok");
		
    }

    
    private void registraBotao(final String nome,int botaoPlayId, final int botaoRingtoneId,final int arquivoId) {
    	
		ImageButton botaoImagem = (ImageButton) findViewById(botaoPlayId);
		botaoImagem.setImageResource(R.drawable.msxmusic);
		botaoImagem.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				if (player!=null &&  player.isPlaying())
					player.stop();
				
				player = MediaPlayer.create(MSX1ringtonesActivity.this,arquivoId);
				Log.i(CATEGORIA, "tocando "+nome+"...");
				player.start();
				
				Toast.makeText(MSX1ringtonesActivity.this, "playing "+nome+" ringtone...", Toast.LENGTH_SHORT).show();
			}
		});
		

		Button botao = (Button) findViewById(botaoRingtoneId);
		botao.setOnClickListener(new View.OnClickListener() {
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
		item.setIcon(R.drawable.sobre);
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