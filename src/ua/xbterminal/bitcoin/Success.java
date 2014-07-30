package ua.xbterminal.bitcoin;

import java.util.HashMap;

import ua.xbterminal.bitcoin.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
	boolean isRun;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.success);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		TextView tv1 = (TextView) findViewById(R.id.textView1);
		if(!prefs.getString("BITCOIN_NETWORK", "testnet").equals("mainnet"))
			tv1.setText(prefs.getString("BITCOIN_NETWORK", "testnet")+" active");
		else
			tv1.setText("");
		TextView tv4 = (TextView) findViewById(R.id.textView4);
		tv4.setText(prefs.getString("MERCHANT_NAME", "XBT Services LTD"));
		TextView tv5 = (TextView) findViewById(R.id.textView5);
		tv5.setText(prefs.getString("MERCHANT_DEVICE_NAME", "Incubator #1"));
		
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
		   
		   mHandler.postDelayed(mTime, 0);
	}
	private Runnable mTime = new Runnable() {
		int count = 0;
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