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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity {

	private static final String SERVER_URL = "http://sutest.comuv.com/";
	
	//SharedPreference to store the user details in the phone
	SharedPreferences sharedPref;
	
	EditText txtName;
	EditText txtCard;
	Context context;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);

	    //Remove notification bar
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

	    setContentView(R.layout.activity_register);
	
	    txtName = (EditText) findViewById(R.id.txtName);
	    txtCard = (EditText) findViewById(R.id.txtCard);
	    sharedPref = getSharedPreferences("userAuth",Context.MODE_PRIVATE);
	    context = getApplicationContext();
	    
	    //Check if user has already registered
	    if(sharedPref.contains("userId")){
	    	Intent i = new Intent(getBaseContext(), Menu.class);
			startActivity(i);
			finish();
	    }
	}
	
	public void registerUser(View v){
    	
    	final String name = txtName.getText().toString().trim();
    	final String card = txtCard.getText().toString().trim();
    	
    	if(name.equals("") || card.equals("")){
    		Toast.makeText(this, "Enter name and credit/debit card", Toast.LENGTH_SHORT).show();
    		return;
    	}

    	Toast.makeText(this, "Registering...", Toast.LENGTH_SHORT).show();
    	
    	new AsyncTask<Void, Void, Void>() {

    		String output = null;

			@Override
			protected Void doInBackground(Void... params) {
				HttpClient httpclient = new DefaultHttpClient();
				HttpEntity entity = null;
				try{
					HttpPost httppost = new HttpPost(SERVER_URL + "register.php");
					
					//Prepare arguments to pass in the HttpPost request
					List<NameValuePair> par = new ArrayList<NameValuePair>(2);
					par.add(new BasicNameValuePair("name", name));
					par.add(new BasicNameValuePair("creditCard", card));
					httppost.setEntity(new UrlEncodedFormEntity(par, "UTF-8"));
					
					//Send the request and get the response
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
				if(output != null ){
					String lines[] = output.split("\\r?\\n");
					output = lines[0];

					//Records the user credentials in sharedPreference locally
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putString("userName", name);
					editor.putString("userCard", card);
					editor.putInt("userId", Integer.parseInt(output));
					editor.commit();
					
					Toast.makeText(context, "Registered", Toast.LENGTH_LONG).show();

					//Starts the main menu
					Intent i = new Intent(getBaseContext(), Menu.class);
					startActivity(i);
					finish();

				}
				else{
					Toast.makeText(context, "Cannot register. No internet connection.", Toast.LENGTH_LONG).show();
				}
				
				super.onPostExecute(result);
			}

    		
		}.execute();

    }
}
