package com.olimposystems.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SobreActivity extends Activity {

	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.sobre);

		Button botao1 = (Button) findViewById(R.id.botao_comments);
		botao1.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://boaglio.com/index.php/en/privacy-policy/"));

				startActivity(browserIntent);

			}
		});
	}

}
