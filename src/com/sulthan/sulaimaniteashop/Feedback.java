package com.sulthan.sulaimaniteashop;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class Feedback extends Activity {

	private static final String SERVER_URL = "http://sutest.comuv.com/";
	Context context;
	private SharedPreferences sharedPref;
	
	private EditText txtfeedback;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("Send Feedback");
        setContentView(R.layout.activity_feedback);

        //sharedPref used to get the locally saved user details
        sharedPref = getSharedPreferences("userAuth",Context.MODE_PRIVATE);
        txtfeedback = (EditText) findViewById(R.id.txtfeedback);
    }


    
    public void sendFeedback(View v){
    	final String feedback = txtfeedback.getText().toString().trim();
    	if(feedback.equals("")){
    		Toast.makeText(getApplicationContext(), "Enter your feedback", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	Toast.makeText(getApplicationContext(), "Sending feedback", Toast.LENGTH_SHORT).show();
    	
    	new AsyncTask<Void, Void, Void>() {
    		
    		String output = null;

			@Override
			protected Void doInBackground(Void... params) {
				HttpClient httpclient = new DefaultHttpClient();
				HttpEntity entity = null;
				
				try{
					HttpPost httppost = new HttpPost(SERVER_URL + "sendfeedback.php");
					
					List<NameValuePair> par = new ArrayList<NameValuePair>(2);
					par.add(new BasicNameValuePair("name", sharedPref.getString("userName", "")));
					par.add(new BasicNameValuePair("feedback", feedback));
					httppost.setEntity(new UrlEncodedFormEntity(par, "UTF-8"));
					
					HttpResponse response = httpclient.execute(httppost);
					entity = response.getEntity();
					output = EntityUtils.toString(entity);
				}
				catch(Exception e){
					;
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				
				if(output != null){
					Toast.makeText(getApplicationContext(), "Feedback Sent", Toast.LENGTH_LONG).show();					
					finish();
				}
				else{
					Toast.makeText(getApplicationContext(), "Cannot send feedback. No internet connection.", Toast.LENGTH_LONG).show();
				}
			}

    		
		}.execute();
    }
    
    
}
