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

import ua.xbterminal.bitcoin.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SplashKey extends Activity {
	String keys = "";
	SharedPreferences prefs;
	String url = Util.URL+"/api/devices/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_load);

		 prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		 TextView tv4 = (TextView) findViewById(R.id.textView4);
			tv4.setText(prefs.getString("MERCHANT_NAME", "XBT Services LTD"));
			TextView tv5 = (TextView) findViewById(R.id.textView5);
			tv5.setText(prefs.getString("MERCHANT_DEVICE_NAME", "Incubator #1"));
			
		/* Calendar c = Calendar.getInstance();

	        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());
	        String formattedDate = df.format(c.getTime());
	        // formattedDate have current date/time
*/
	      /*/  if(formattedDate.equals("18-07-2014")||formattedDate.equals("19-07-2014")||formattedDate.equals("20-07-2014")		
	        	||formattedDate.equals("21-07-2014")||formattedDate.equals("22-07-2014")||formattedDate.equals("23-07-2014")
	        	||formattedDate.equals("24-07-2014")||formattedDate.equals("25-07-2014")||formattedDate.equals("26-07-2014")
	        	||formattedDate.equals("27-07-2014")||formattedDate.equals("28-07-2014")||formattedDate.equals("29-07-2014"))
	        	*/
			Comment(prefs.getString("key", "c8fc78cd583b4eadaab14400311f72dd"));
	       /* else{
		        Toast.makeText(this, "Application demo finished", Toast.LENGTH_SHORT).show();
		        finish();
	        }*/
		
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

				final EditText userInput = (EditText) promptsView
						.findViewById(R.id.editTextDialogUserInput);
				userInput.setText(key);

				// set dialog message
				alertDialogBuilder
					.setCancelable(false)
					.setTitle("App secret")
					.setPositiveButton("Accept",
					  new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog,int id) {
						// get user input and set it to result
					    	
					    }
					  });
				
				// create alert dialog
				final AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
				
				alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
			      {            
			          @Override
			          public void onClick(View v)
			          {
			        	  if(userInput.length()>0){
					    		keys = userInput.getText().toString();
					    		alertDialog.dismiss();
					    		prefs.edit().putString("key", keys).commit();
					    		new RequestTask().execute(url+prefs.getString("key", "c8fc78cd583b4eadaab14400311f72dd"));
	
			        	  }
					    	else
					    		Toast.makeText(getBaseContext(), "Enter key", Toast.LENGTH_SHORT).show();

			          }
			      });
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
		ProgressDialog	dialog;
		String response;
		boolean error;
		/*Начало выполнения background*/
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			dialog = new ProgressDialog(SplashKey.this);
			dialog.setMessage("Loading..");
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();

		}
		
		/*Сам background*/
		@Override
		protected String doInBackground(String... params) {
			try {
				response = connect(params[0]);
				System.out.println("response" + response);		
			} catch (Exception e) {
				error = true;
				System.out.println("Exp=" + e);				
			}
			
			return response;
		}
	/*Конец выполнения background*/
		@Override
		protected void onPostExecute(String result) {

			dialog.dismiss();
			
			if(error)
				Toast.makeText(getBaseContext(), "Error connection", Toast.LENGTH_LONG).show();
			else{
				JSONencoding JS = new JSONencoding();
				HashMap<String,String> hm = JS.DecodingInfo(JS.toJSONOject(result));
				prefs.edit().putString("MERCHANT_DEVICE_NAME", hm.get("MERCHANT_DEVICE_NAME")).commit();
				prefs.edit().putString("MERCHANT_NAME", hm.get("MERCHANT_NAME")).commit();
				prefs.edit().putString("BITCOIN_NETWORK", hm.get("BITCOIN_NETWORK")).commit();
				
				startActivity(new Intent(SplashKey.this, Splash.class).putExtra("key", keys));	      
			     finish();	
				
			}
				
			super.onPostExecute(result);
		}
		
	}
}
