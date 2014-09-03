package ua.xbterminal.bitcoin;

import ua.xbterminal.bitcoin.R;
import ua.xbterminal.bitcoin.ServiceApi.RequestTask;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Splash extends Activity {
	 SharedPreferences prefs;
	 final Handler uiHandler = new Handler();
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_load);
		RelativeLayout image = (RelativeLayout) findViewById(R.id.Splash);
		
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		TextView tv4 = (TextView) findViewById(R.id.textView4);
		tv4.setText(prefs.getString("MERCHANT_NAME", ""));
		TextView tv5 = (TextView) findViewById(R.id.textView5);
		tv5.setText(prefs.getString("MERCHANT_DEVICE_NAME", ""));
		
		startService(new Intent(this, ServiceApi.class));
		uiHandler.postDelayed(Api,0);
		
		image.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				 startActivity(new Intent(Splash.this, MainActivity.class).putExtra("key", prefs.getString("key", "")));	      
			     finish();			
			}
			
		});
	}
	 @Override
		public void onBackPressed()
		    {
		 		stopService(new Intent(this, ServiceApi.class));
		 		finish();
		    }
	  private Runnable Api = new Runnable() {
		   public void run() {
			   uiHandler.post(new Runnable() {  // используя Handler, привязанный к UI-Thread
			        @Override
			        public void run() {
			        	TextView tv4 = (TextView) findViewById(R.id.textView4);
			    		tv4.setText(prefs.getString("MERCHANT_NAME", ""));
			    		TextView tv5 = (TextView) findViewById(R.id.textView5);
			    		tv5.setText(prefs.getString("MERCHANT_DEVICE_NAME", ""));         // выполним установку значения
			        }
			    });
			   uiHandler.postDelayed(Api, 1000);
		   }
		};
}
