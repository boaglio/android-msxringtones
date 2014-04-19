package com.olimposystems.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MSXringtonesActivity extends Activity {
    
	private static final String CATEGORIA = "msx";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);
 
        Button botaoMSX1 = (Button) findViewById(R.id.btn_msx1);
		botaoMSX1.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				Log.i(CATEGORIA, "chamando MSX1");
				Intent i = new Intent(getApplicationContext(), MSX1ringtonesActivity.class);
				startActivity(i);

			}
		});
		

		Button botaoMSX2 = (Button) findViewById(R.id.botao_msx2);
		botaoMSX2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				Log.i(CATEGORIA, "chamando MSX2");
				Intent i = new Intent(getApplicationContext(), MSX2ringtonesActivity.class);
				startActivity(i);
			}
		});


		Button botaoSair = (Button) findViewById(R.id.botao_saida);
		botaoSair.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				Log.i(CATEGORIA, "chamando Exit");
				exit(); 
				MSXringtonesActivity.this.finish();
				
			}
		});
		
		Log.i(CATEGORIA, "tela principal Ok");
		
    }
    
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem item = menu.add(0, 1, 0,R.string.sobre);
		item.setIcon(R.drawable.sobre);
		item = menu.add(0, 2, 0,R.string.sair);
		item.setIcon(R.drawable.exit_default);
		return true;
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		switch (item.getItemId()) {
		case 1:
			startActivity(new Intent(MSXringtonesActivity.this,SobreActivity.class));
			
			return true;
		case 2:
			this.finish();
			return true;			
		}
		return false;
	}

 
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		exit();
	}

	
	private void exit()  {
		Toast.makeText(MSXringtonesActivity.this, R.string.goodbye , Toast.LENGTH_SHORT).show();
	}
	
}