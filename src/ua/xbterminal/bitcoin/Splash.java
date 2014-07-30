package ua.xbterminal.bitcoin;

import ua.xbterminal.bitcoin.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Splash extends Activity {
	SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_load);
		RelativeLayout image = (RelativeLayout) findViewById(R.id.Splash);
		
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		TextView tv4 = (TextView) findViewById(R.id.textView4);
		tv4.setText(prefs.getString("MERCHANT_NAME", "XBT Services LTD"));
		TextView tv5 = (TextView) findViewById(R.id.textView5);
		tv5.setText(prefs.getString("MERCHANT_DEVICE_NAME", "Incubator #1"));
		
		image.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				 startActivity(new Intent(Splash.this, MainActivity.class).putExtra("key", prefs.getString("key", "")));	      
			     finish();			
			}
			
		});
	}

}
