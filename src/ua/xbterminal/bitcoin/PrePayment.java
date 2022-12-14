package ua.xbterminal.bitcoin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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
import android.os.AsyncTask;
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

public class PrePayment extends Activity {
	
private String url;
private String SendToNFC;
private String data;
private Handler mHandler = new Handler();
boolean isRun;
SharedPreferences prefs;
final Handler uiHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pre_payments);
		
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
				startActivity(new Intent(PrePayment.this, Splash.class));
				finish();		
			}
			
		});
		
		
		TextView in = (TextView)findViewById(R.id.in);
		in.setText(prefs.getString("MERCHANT_CURRENCY_SIGN_POSTFIX", "")+
				prefs.getString("MERCHANT_CURRENCY_SIGN_PREFIX", "")+
				" "+addCurrencySign(order.get("fiat_amount")));
		
		TextView out = (TextView)findViewById(R.id.out);
		Double toBeTruncated = (Double.parseDouble(order.get("btc_amount"))*1000);
		Double truncatedDouble=new BigDecimal(toBeTruncated ).setScale(5, BigDecimal.ROUND_HALF_DOWN).doubleValue();
		out.setText("m??? "+truncatedDouble);
		
		TextView rate = (TextView)findViewById(R.id.rate);
		toBeTruncated = Double.parseDouble(order.get("exchange_rate"));
		truncatedDouble = new BigDecimal(toBeTruncated ).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
		rate.setText(getString(R.string.exchange)+" "+truncatedDouble);
		
		ImageView qr = (ImageView)findViewById(R.id.qr_code);
		
		url = order.get("check_url");
		SendToNFC = order.get("payment_uri");
		
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
			   isRun = savedInstanceState.getBoolean("isRun");
		   }
		   mHandler.postDelayed(mUpdateTimeTask, 0);
		   mHandler.postDelayed(mTime, 0);
		   
		   //startService(new Intent(this, ServiceApi.class));
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
			   uiHandler.post(new Runnable() {  // ?????????????????? Handler, ?????????????????????? ?? UI-Thread
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
			    		tv5.setText(prefs.getString("MERCHANT_DEVICE_NAME", ""));   // ???????????????? ?????????????????? ????????????????
			        }
			    });
			   uiHandler.postDelayed(Api, 1000);
		   }
		};
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putInt("count", count);
	    outState.putBoolean("isRun", isRun);
	   // outState.putString("text1", text1.getText().toString());
	}
	
	private String addCurrencySign(String digits)
    {
	    String string = ""; // Your currency
	    String temp = "";
	    temp = digits.substring(digits.indexOf("."), digits.length());
        // Amount length greater than 2 means we need to add a decimal point
        if (temp.length() == 2)
        {
        	string = digits.substring(0, digits.length()-2)+ temp + "0";
        }
        else if (temp.length() == 3)
        {
        	string = digits;
        }  

        return string;
    }
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
			   if(!isRun)
				   new RequestTask().execute(url);
			   
				 mHandler.postDelayed(mUpdateTimeTask, 2000);
		   }
		};
		private int count = 0;
		private Runnable mTime = new Runnable() {
			   public void run() {
				   if(count==600){
					    startActivity(new Intent(PrePayment.this, Splash.class));
						finish();
				   }
				   count++;
				   
					 mHandler.postDelayed(mTime, 1000);
			   }
			};
	protected void onDestroy(){
		super.onDestroy();
		mHandler.removeCallbacks(mTime);
		mHandler.removeCallbacks(mUpdateTimeTask);	
	}
			public String connect(String url)
			{
				String result = null;
			    HttpClient httpclient = new DefaultHttpClient();

			    // Prepare a request object
			    HttpGet httpget = new HttpGet(url); 

			    // Execute the request
			    HttpResponse response;
			    try {
			        response = httpclient.execute(httpget);

			        // Get hold of the response entity
			        HttpEntity entity = response.getEntity();
			        // If the response does not enclose an entity, there is no need
			        // to worry about connection release

			        if (entity != null) {

			            // A Simple JSON Response Read
			            InputStream instream = entity.getContent();
			            result = convertStreamToString(instream);

			            // now you have the string representation of the HTML request
			            instream.close();
			        }


			    } catch (Exception e) {
			    	e.getMessage();
			    }
				return result;
			}

			private static String convertStreamToString(InputStream is) {
			    /*
			     * To convert the InputStream to String we use the BufferedReader.readLine()
			     * method. We iterate until the BufferedReader return null which means
			     * there's no more data to read. Each line will appended to a StringBuilder
			     * and returned as String.
			     */
			    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			    StringBuilder sb = new StringBuilder();

			    String line = null;
			    try {
			        while ((line = reader.readLine()) != null) {
			            sb.append(line + "\n");
			        }
			    } catch (IOException e) {
			        e.printStackTrace();
			    } finally {
			        try {
			            is.close();
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
			    }
			    return sb.toString();
			}
			
			class RequestTask extends AsyncTask<String, String, String> {
					String response;
					boolean error;
					/*???????????? ???????????????????? background*/
					@Override
					protected void onPreExecute() {
						super.onPreExecute();
						
						isRun=true;
					}
					/*?????? background*/
					@Override
					protected String doInBackground(String... params) {
						
						try {
							response = connect(params[0]);
							//Log.i("Main", "response"+response);
						} catch (Exception e) {
							error = true;			
						}
						
						return response;
					}
				/*?????????? ???????????????????? background*/
					@Override
					protected void onPostExecute(String result) {
						
						if(error)
							Toast.makeText(getBaseContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
						else{
							if(result!=null){
								JSONencoding JS = new JSONencoding();
								HashMap<String, String> hash = JS.DecodingPaid(JS.toJSONOject(result));
								
								if(hash.get("paid").equals("1")){
									startActivity(new Intent(PrePayment.this, Success.class).putExtra("value", hash ));
									finish();
								}
							}
						}
						isRun = false;
						super.onPostExecute(result);
					}
					
				}
}