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

import com.olimposystems.android.type.MSX2Game;
import com.olimposystems.android.util.RingtoneUtil;

public class MSX2ringtonesActivity extends Activity {
    
	private static final String CATEGORIA = "msx2";
	
	private MediaPlayer player;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);

		setContentView(R.layout.msx2_layout);
        
        registraBotao(MSX2Game.aleste);
        registraBotao(MSX2Game.firehawk);
        registraBotao(MSX2Game.fray);
        registraBotao(MSX2Game.golvellius);
        registraBotao(MSX2Game.metalgear1);
        registraBotao(MSX2Game.sdsnatcher);
        registraBotao(MSX2Game.usas);

		Log.i(CATEGORIA, "tela MSX2 ok");
		
    }
    
    private void registraBotao(final MSX2Game game) {
    	
		ImageButton botaoImagem = (ImageButton) findViewById(game.botaoPlayId());
		botaoImagem.setImageResource(R.drawable.speaker_logo);
		botaoImagem.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				if (player!=null &&  player.isPlaying())
					player.stop();
				
				player = MediaPlayer.create(MSX2ringtonesActivity.this,game.arquivoId());
				Log.i(CATEGORIA, "tocando "+game.nome()+"...");
				player.start();
				
				Toast.makeText(MSX2ringtonesActivity.this, "playing "+game.nome()+" ringtone...", Toast.LENGTH_SHORT).show();
			}
		});


		ImageButton botao = (ImageButton) findViewById(game.botaoRingtoneId());
		botao.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				RingtoneUtil ringtoneManager = new RingtoneUtil();
				ringtoneManager.setRingtone(game.botaoRingtoneId(), MSX2ringtonesActivity.this);
				
				Toast.makeText(MSX2ringtonesActivity.this, "using "+game.nome()+" ringtone...", Toast.LENGTH_SHORT).show();
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