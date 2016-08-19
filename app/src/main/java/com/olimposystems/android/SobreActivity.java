package com.olimposystems.android;

import android.app.Activity;
import android.content.Intent;
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
				
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);  
				  
				emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"contato@olymposystems.com"}); 
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "comments about MSX ringtone"); 
				emailIntent.setType("plain/text");  
				  
				startActivity(emailIntent); 
				
			}
		});
	}
		
}
