package ua.xbterminal.bitcoin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import ua.xbterminal.bitcoin.SplashKey.RequestTask;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class ServiceApi extends Service{


	SharedPreferences prefs;
	String url = Util.URL+"/api/devices/";
	boolean isRun, isServiceStarted;
	 final Handler uiHandler = new Handler();
	 
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	  public int onStartCommand(Intent intent, int flags, int startId) {
		  if(!isServiceStarted){
			  isServiceStarted = true;
			    prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			    uiHandler.postDelayed(Api, 0);   
			  }
		    return super.onStartCommand(intent, flags, startId);
		  }
	  
	  private Runnable Api = new Runnable() {
		   public void run() {
			   if(!isRun)
		        	 new RequestTask().execute(url+prefs.getString("key", ""));

			   uiHandler.postDelayed(Api, 1000);
		   }
		};
		  public void onDestroy() {
		    super.onDestroy();
		    uiHandler.removeCallbacks(Api);
		    isServiceStarted = false;
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
				
				/*Сам background*/
				@Override
				protected String doInBackground(String... params) {
					isRun = true;
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

					if(error)
						Toast.makeText(getBaseContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
					else{
						JSONencoding JS = new JSONencoding();
						HashMap<String,String> hm = null;
						
						try{
							hm = JS.DecodingInfo(JS.toJSONOject(result));
						}catch(Exception e){
							return;
						}
						prefs.edit().putString("MERCHANT_DEVICE_NAME", hm.get("MERCHANT_DEVICE_NAME")).commit();
						prefs.edit().putString("MERCHANT_NAME", hm.get("MERCHANT_NAME")).commit();
						prefs.edit().putString("BITCOIN_NETWORK", hm.get("BITCOIN_NETWORK")).commit();
						prefs.edit().putString("MERCHANT_CURRENCY_SIGN_PREFIX", hm.get("MERCHANT_CURRENCY_SIGN_PREFIX")).commit();
						prefs.edit().putString("MERCHANT_CURRENCY_SIGN_POSTFIX", hm.get("MERCHANT_CURRENCY_SIGN_POSTFIX")).commit();
					    
					}
					isRun = false;
					super.onPostExecute(result);
				}
				
			}
}
