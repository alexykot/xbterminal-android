package ua.xbterminal.bitcoin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import ua.xbterminal.bitcoin.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SplashKey extends Activity {
	String keys = "";
	SharedPreferences prefs;
	String url = Util.URL+"/api/devices/";
	int REQUEST_QR_CODE = 555;
	EditText userInput;
	ProgressDialog	dialog;
	
	//String KEY_DEFOULT = "0fe12ac5229e44a3a797e641d10b36fe";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_load);
		
		/*вот ключи
		0fe12ac5229e44a3a797e641d10b36fe - mainnet terminal
		
		c8fc78cd583b4eadaab14400311f72dd - android test
		e676348c42cb4701870f69dbe79108d9 - iOS test
		*/
		
		 prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		 TextView tv4 = (TextView) findViewById(R.id.textView4);
			tv4.setText(prefs.getString("MERCHANT_NAME", ""));
			TextView tv5 = (TextView) findViewById(R.id.textView5);
			tv5.setText(prefs.getString("MERCHANT_DEVICE_NAME", ""));
			
		/* Calendar c = Calendar.getInstance();

	        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());
	        String formattedDate = df.format(c.getTime());
	        // formattedDate have current date/time

	        if(formattedDate.equals("31-08-2014")||formattedDate.equals("01-09-2014")||formattedDate.equals("02-09-2014")
	        		||formattedDate.equals("03-09-2014")){ */
	        	if(savedInstanceState==null)
	        		Comment(prefs.getString("key", ""));
	        	else
	        		Comment(savedInstanceState.getString("key"));
	        	
	       /* }
	        else{
		        Toast.makeText(this, "Application demo finished", Toast.LENGTH_SHORT).show();
		        finish();
	        }*/
		
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putString("key", userInput.getText().toString());
	   // outState.putString("text1", text1.getText().toString());
	}
	
	@SuppressLint("InflateParams")
	private void Comment(String key){
		// get prompts.xml view
				LayoutInflater li = LayoutInflater.from(this);
				View promptsView = li.inflate(R.layout.promts, null);
				 
				 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						this);

				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(promptsView);
				 TextView t = (TextView) promptsView.findViewById(R.id.textView4);
				 t.setMovementMethod(LinkMovementMethod.getInstance());	
				userInput = (EditText) promptsView
						.findViewById(R.id.editTextDialogUserInput);
				final Button accept = (Button) promptsView
						.findViewById(R.id.button2);
				
				final Button qr = (Button) promptsView
						.findViewById(R.id.button1);
				qr.setOnClickListener(new View.OnClickListener()
			      {            
			          @Override
			          public void onClick(View v)
			          {
			        	  Intent intent = new Intent(SplashKey.this, DecoderActivity.class);
		                   startActivityForResult(intent, REQUEST_QR_CODE);

			          }
			      });
				userInput.setText(key);

				// set dialog message
				alertDialogBuilder
					.setCancelable(false);
					//.setTitle("App secret")
					/*.setPositiveButton("Accept",
					  new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog,int id) {
						// get user input and set it to result
					    	
					    }
					  });*/
				
				// create alert dialog
				final AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
				accept.setOnClickListener(new View.OnClickListener()
			      {            
			          @Override
			          public void onClick(View v)
			          {
			        	  if(userInput.length()>0){
					    		keys = userInput.getText().toString();	
					    		alertDialog.dismiss();
					    		prefs.edit().putString("key", keys).commit();
					    		new RequestTask().execute(url+prefs.getString("key", ""));
	
			        	  }
					    	else
					    		userInput.setError(getString(R.string.enter));

			          }
			      });

	}
	 public void onConfigurationChanged(Configuration newConfig) {
		    super.onConfigurationChanged(newConfig);
		   
		  }
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		  System.gc();
		  
		  if (resultCode == RESULT_CANCELED) {	       
			 // Toast.makeText(getBaseContext(), "Canceled", Toast.LENGTH_LONG).show();      
		    }
	   if(requestCode == REQUEST_QR_CODE){
		  	 if (resultCode == Activity.RESULT_OK) {
		  		 try{
			    		 String code = data.getStringExtra("code");  
			    		 userInput.setText(code);
			    		 
			    		 }catch(Exception r){
			    			 Toast.makeText(getBaseContext(), r.getMessage() , Toast.LENGTH_LONG).show();
			    			 r.getMessage();
			    		 }
		  	 }
		}
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
		/*Начало выполнения background*/
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			dialog = new ProgressDialog(SplashKey.this);
			dialog.setMessage(getString(R.string.load));
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			//dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				try{
				dialog.show();
				}catch(Exception w){}

		}
		
		/*Сам background*/
		@Override
		protected String doInBackground(String... params) {
			try {
				response = connect(params[0]);
				//System.out.println("response" + response);		
			} catch (Exception e) {
				error = true;
				//System.out.println("Exp=" + e);				
			}
			
			return response;
		}
	/*Конец выполнения background*/
		@Override
		protected void onPostExecute(String result) {
			try{
				dialog.dismiss();
			}catch(Exception e){}
			if(error)
				Toast.makeText(getBaseContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
			else{
				JSONencoding JS = new JSONencoding();
				HashMap<String,String> hm = null;
				
				try{
					hm = JS.DecodingInfo(JS.toJSONOject(result));
				}catch(Exception e){
					Toast.makeText(getBaseContext(), getString(R.string.invalid), Toast.LENGTH_LONG).show();
					Comment(keys);
					prefs.edit().putString("key", "").commit();
					return;
				}
				try {
					if(JS.toJSONOject(result).get("detail").equals("Not found")){
						Toast.makeText(getBaseContext(), getString(R.string.invalid), Toast.LENGTH_LONG).show();
						Comment(keys);
						prefs.edit().putString("key", "").commit();
						return;
					}
				} catch (Exception e1) {

				}
				prefs.edit().putString("MERCHANT_DEVICE_NAME", hm.get("MERCHANT_DEVICE_NAME")).commit();
				prefs.edit().putString("MERCHANT_NAME", hm.get("MERCHANT_NAME")).commit();
				prefs.edit().putString("BITCOIN_NETWORK", hm.get("BITCOIN_NETWORK")).commit();
				prefs.edit().putString("MERCHANT_CURRENCY_SIGN_PREFIX", hm.get("MERCHANT_CURRENCY_SIGN_PREFIX")).commit();
				prefs.edit().putString("MERCHANT_CURRENCY_SIGN_POSTFIX", hm.get("MERCHANT_CURRENCY_SIGN_POSTFIX")).commit();
				
				startActivity(new Intent(SplashKey.this, Splash.class).putExtra("key", keys));	      
			    finish();	
			    
			}
				
			super.onPostExecute(result);
		}
		
	}
}
