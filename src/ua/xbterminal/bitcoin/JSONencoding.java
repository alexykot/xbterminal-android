package ua.xbterminal.bitcoin;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONencoding {
	
	
	public  JSONObject toJSONOject(String json)
	{
		JSONObject jObj = null;
		try {
			 jObj = new JSONObject(json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1));
		} 
	    catch (JSONException e) {
			e.printStackTrace();
		}
	return jObj;
	}
	
	
	public  HashMap<String, String> DecodingPay(JSONObject json)
	{
		HashMap<String, String> autorization = null ;
 

	    try {
                autorization = new HashMap<String, String>();

                autorization.put("payment_uri", json.getString("payment_uri"));
                autorization.put("check_url", json.getString("check_url"));
                
                autorization.put("fiat_amount", String.valueOf(json.getDouble("fiat_amount")));
                autorization.put("exchange_rate", String.valueOf(json.getDouble("exchange_rate")));
                autorization.put("qr_code_src", json.getString("qr_code_src"));
                autorization.put("btc_amount", String.valueOf(json.getDouble("btc_amount")));
	
               }
	    			catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                
	    	return autorization;

	}
	
	public  HashMap<String, String> DecodingPaid(JSONObject json)
	{
		HashMap<String, String> autorization = null ;
 

	    try {
                autorization = new HashMap<String, String>();
                
                autorization.put("paid", String.valueOf(json.getInt("paid")));
                
                try{
                autorization.put("qr_code_src", json.getString("qr_code_src"));
                autorization.put("receipt_url", json.getString("receipt_url"));
                }catch(Exception e){
                	
                }

	
               }
	    			catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                
	    	return autorization;

	}
	public  HashMap<String, String> DecodingInfo(JSONObject json)
	{
		HashMap<String, String> autorization = null ;
 

	    try {
                autorization = new HashMap<String, String>();
                
                autorization.put("MERCHANT_DEVICE_NAME", json.getString("MERCHANT_DEVICE_NAME"));
                autorization.put("MERCHANT_NAME", json.getString("MERCHANT_NAME"));
                autorization.put("BITCOIN_NETWORK", json.getString("BITCOIN_NETWORK"));
                

	
               }
	    			catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                
	    	return autorization;

	}
}