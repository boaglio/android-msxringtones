package com.olimposystems.android;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.olimposystems.android.type.MSX2Game;
import com.olimposystems.android.util.EdgeToEdge;
import com.olimposystems.android.util.RingtoneUtil;

public class MSX2ringtonesActivity extends Activity {

	private static final String CATEGORIA = "msx2";

	private MediaPlayer player;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.msx2_layout);
		EdgeToEdge.aplica(this);

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

		ImageButton btnTocaRingTone = (ImageButton) findViewById(game.botaoPlayId());
		btnTocaRingTone.setImageResource(R.drawable.speaker_logo);
		btnTocaRingTone.setContentDescription("play " + game.nome());
		btnTocaRingTone.setOnClickListener(v -> {

			stopPlay();

			player = MediaPlayer.create(MSX2ringtonesActivity.this, game.arquivoId());
			if (player == null) {
				Log.w(CATEGORIA, "não foi possível tocar " + game.nome());
				return;
			}
			player.setOnCompletionListener(mp -> stopPlay());
			Log.i(CATEGORIA, "tocando " + game.nome() + "...");
			player.start();

			Toast.makeText(MSX2ringtonesActivity.this, "playing " + game.nome() + " ringtone...", Toast.LENGTH_SHORT).show();
		});

		ImageButton btnConfiguraRingTone = (ImageButton) findViewById(game.botaoRingtoneId());
		btnConfiguraRingTone.setImageResource(R.drawable.checkmark);
		btnConfiguraRingTone.setContentDescription("use " + game.nome() + " as ringtone");
		btnConfiguraRingTone.setOnClickListener(v -> {

			if (RingtoneUtil.setRingtone(MSX2ringtonesActivity.this, game.arquivoId(), game.nome())) {
				Toast.makeText(MSX2ringtonesActivity.this, "using " + game.nome() + " ringtone...", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem item = menu.add(0, 1, 0, R.string.sobre);
		item.setIcon(R.drawable.about);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			startActivity(new Intent(MSX2ringtonesActivity.this, SobreActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		super.onStop();
		stopPlay();
	}

	private void stopPlay() {
		if (player != null) {
			if (player.isPlaying()) {
				player.stop();
			}
			player.release();
			player = null;
		}
	}

}
