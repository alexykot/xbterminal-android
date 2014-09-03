package ua.xbterminal.bitcoin;

import java.util.HashMap;

import ua.xbterminal.bitcoin.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Success extends Activity {

	private String SendToNFC;
	private String data;
	private Handler mHandler = new Handler();
	SharedPreferences prefs;
	final Handler uiHandler = new Handler();
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.success);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		TextView tv1 = (TextView) findViewById(R.id.textView1);
		if(!prefs.getString("BITCOIN_NETWORK", "testnet").equals("mainnet"))
			tv1.setText(getString(R.string.testnet));
		else
			tv1.setText("");
		TextView tv4 = (TextView) findViewById(R.id.textView4);
		tv4.setText(prefs.getString("MERCHANT_NAME", ""));
		TextView tv5 = (TextView) findViewById(R.id.textView5);
		tv5.setText(prefs.getString("MERCHANT_DEVICE_NAME", ""));
		
		@SuppressWarnings("unchecked")
		HashMap<String, String> order = (HashMap<String, String>) getIntent().getSerializableExtra("value");
		
		Button btn = (Button)findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(Success.this, Splash.class));
				finish();		
			}
			
		});
		ImageView qr = (ImageView)findViewById(R.id.qr_code);

		SendToNFC = order.get("receipt_url");
		
		data = order.get("qr_code_src");
		data = data.subSequence(data.lastIndexOf("data:image/png;base64,")+22, data.length()).toString();
		byte[] decodedString = Base64.decode(data, Base64.DEFAULT);
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
		qr.setImageBitmap(decodedByte);
		   NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
		   if(nfc == null) {
			   Toast.makeText(getBaseContext(),"NFC is not available on this device", Toast.LENGTH_SHORT).show();
			} else {
				 nfc.setNdefPushMessageCallback(new NfcAdapter.CreateNdefMessageCallback()
				    {
				        /*
				         * (non-Javadoc)
				         * @see android.nfc.NfcAdapter.CreateNdefMessageCallback#createNdefMessage(android.nfc.NfcEvent)
				         */
				        @Override
				        public NdefMessage createNdefMessage(NfcEvent event) 
				        {
				            NdefRecord uriRecord = NdefRecord.createUri(SendToNFC);
				            return new NdefMessage(new NdefRecord[] {
				            		uriRecord 
				            		});
				        }

				    }, this, this);  
			}
		   if(savedInstanceState!=null){
			   count = savedInstanceState.getInt("count");
		   }
		   mHandler.postDelayed(mTime, 0);
		   
		  // startService(new Intent(this, ServiceApi.class));
			uiHandler.postDelayed(Api,0);
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
			        	TextView tv1 = (TextView) findViewById(R.id.textView1);
			    		if(!prefs.getString("BITCOIN_NETWORK", "testnet").equals("mainnet"))
			    			tv1.setText(getString(R.string.testnet));
			    		else
			    			tv1.setText("");
			    		TextView tv4 = (TextView) findViewById(R.id.textView4);
			    		tv4.setText(prefs.getString("MERCHANT_NAME", ""));
			    		TextView tv5 = (TextView) findViewById(R.id.textView5);
			    		tv5.setText(prefs.getString("MERCHANT_DEVICE_NAME", ""));         // выполним установку значения
			        }
			    });
			   uiHandler.postDelayed(Api, 1000);
		   }
		};
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putInt("count", count);
	}
		
	private int count = 0;
	private Runnable mTime = new Runnable() {

		   public void run() {
			   if(count==600){
				    startActivity(new Intent(Success.this, Splash.class));
					finish();
			   }
			   count++;
			   
				 mHandler.postDelayed(mTime, 1000);
		   }
		};
	protected void onDestroy(){
		super.onDestroy();
		mHandler.removeCallbacks(mTime);	
	}
}