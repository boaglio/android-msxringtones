package com.olimposystems.android;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.olimposystems.android.type.MSX1Game;
import com.olimposystems.android.util.EdgeToEdge;
import com.olimposystems.android.util.RingtoneUtil;

public class MSX1ringtonesActivity extends Activity {

	private static final String CATEGORIA = "msx1";

	private MediaPlayer player;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.msx1_layout);
		EdgeToEdge.aplica(this);

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
		btnTocaRingTone.setContentDescription("play " + game.nome());
		btnTocaRingTone.setOnClickListener(v -> {

			stopPlay();

			player = MediaPlayer.create(MSX1ringtonesActivity.this, game.arquivoId());
			if (player == null) {
				Log.w(CATEGORIA, "não foi possível tocar " + game.nome());
				return;
			}
			player.setOnCompletionListener(mp -> stopPlay());
			Log.i(CATEGORIA, "tocando " + game.nome() + "...");
			player.start();

			Toast.makeText(MSX1ringtonesActivity.this, "playing " + game.nome() + " ringtone...", Toast.LENGTH_SHORT).show();
		});

		ImageButton btnConfiguraRingTone = (ImageButton) findViewById(game.botaoRingtoneId());
		btnConfiguraRingTone.setImageResource(R.drawable.checkmark);
		btnConfiguraRingTone.setContentDescription("use " + game.nome() + " as ringtone");
		btnConfiguraRingTone.setOnClickListener(v -> {

			if (RingtoneUtil.setRingtone(MSX1ringtonesActivity.this, game.arquivoId(), game.nome())) {
				Toast.makeText(MSX1ringtonesActivity.this, "using " + game.nome() + " ringtone...", Toast.LENGTH_SHORT).show();
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
			startActivity(new Intent(MSX1ringtonesActivity.this, SobreActivity.class));
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
