package ua.xbterminal.bitcoin;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import ua.xbterminal.bitcoin.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	/*
	POST data:
	amount: <fiat_amount>
	device_key: <device_key>*/
	private String url = Util.URL+"/api/payments/init";
	EditText editText;TextView textview;	RelativeLayout.LayoutParams params;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
		
		textview = (TextView)findViewById(R.id.textView7);
		params = (RelativeLayout.LayoutParams)textview.getLayoutParams();
		editText = (EditText) findViewById(R.id.editText1);
		editText.addTextChangedListener(new TextWatcher()
        {
			String text;
            @Override 
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

				params.setMargins(8, 0, 8, 0); //substitute parameters for left, top, right, bottom
				textview.setLayoutParams(params);
            	text = addCurrencySign(s.toString());
            	//editText.setText(text);
            	textview.setText(text);
            	//setText("");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override 
            public void afterTextChanged(Editable s)
            {
            	
            }
        });
          
		Button payNow = (Button) findViewById(R.id.button1);
		payNow.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(textview.length()==0 || Double.valueOf(textview.getText().toString())==0){
					editText.setError("Enter the value!");
					
					params.setMargins(8, 0, 50, 0); //substitute parameters for left, top, right, bottom
					textview.setLayoutParams(params);
				}
				else
					new RequestTask().execute(url,textview.getText().toString(),getIntent().getStringExtra("key"));
			}
			
		});
	}

	private String addCurrencySign(String digits)
    {
	    String string = ""; // Your currency

        // Amount length greater than 2 means we need to add a decimal point
        if (digits.length() > 2)
        {
        	//Log.d("Main", "this "+digits);
            String pound = digits.substring(0, digits.length() - 2); // Pound part
        	//Log.d("Main", "pound "+pound);

            String pence = digits.substring(digits.length() - 2); // Pence part
           // Log.d("Main", "pence "+pence);
            string = pound + "." + pence;
        }
        else if (digits.length() == 1)
        {
            string = "0.0" + digits;
          //  Log.d("Main", "length 1 " + string);
        }
        else if (digits.length() == 2)
        {
            string = "0." + digits;
           // Log.d("Main", "length 2 " + string);
        }  
        else if (digits.length() == 0)
        {
            string = "0.00";
        }  

        return string;
    }
	class RequestTask extends AsyncTask<String, String, String> {
		ProgressDialog	dialog;
		String response;
		boolean error;
		/*Начало выполнения background*/
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			dialog = new ProgressDialog(MainActivity.this);
			dialog.setMessage("Loading...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.show();

		}
		
		/*Сам background*/
		@Override
		protected String doInBackground(String... params) {

			try {
				DefaultHttpClient hc = new DefaultHttpClient();
				ResponseHandler<String> res = new BasicResponseHandler();

				HttpPost postMethod = new HttpPost(params[0]);
				HttpParams param = hc.getParams();
			    try {
			        HttpConnectionParams.setConnectionTimeout(param, 10000);
			        HttpConnectionParams.setSoTimeout(param, 10000);
			    } catch (Exception e) {
			        e.printStackTrace();
			        throw e;
			    }
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				  nameValuePairs.add(new BasicNameValuePair("amount", params[1]));	
		          nameValuePairs.add(new BasicNameValuePair("device_key", params[2]));
		         // Log.d("Main", "nameValuePairs = "+nameValuePairs);
				postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));

				response = hc.execute(postMethod, res);
				Log.d("Main", "Answer = "+response);
				error = false;
			} catch (Exception e) {
				error = true;	
				//Log.e("Main", "e.getMessage() = "+e.getMessage());
			}
			
			return response;
		}
	/*Конец выполнения background*/
		@Override
		protected void onPostExecute(String result) {

			dialog.dismiss();
			
			if(error)
				Toast.makeText(getBaseContext(), "Error convert", Toast.LENGTH_LONG).show();
			else{
				JSONencoding JS = new JSONencoding();
				startActivity(new Intent(MainActivity.this, PrePayment.class).putExtra("value", JS.DecodingPay(JS.toJSONOject(result))));
				finish();
			}
				
			super.onPostExecute(result);
		}
		
	}
}