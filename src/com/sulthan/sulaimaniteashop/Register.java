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
	    
	    if(sharedPref.contains("userId")){
	    	Intent i = new Intent(getBaseContext(), Menu.class);
			startActivity(i);
			finish();
	    }
	}
	
	public void registerUser(View v){
    	Toast.makeText(this, "Registering...", Toast.LENGTH_SHORT).show();
    	
    	
		
		
		new AsyncTask<Void, Void, Void>() {

    		HttpEntity entity = null;
    		String output = null;
    		
			@Override
			protected void onPostExecute(Void result) {
				String lines[] = output.split("\\r?\\n");
				output = lines[0];
				
				if(output != null ){
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putString("userName", txtName.getText().toString());
					editor.putString("userCard", txtCard.getText().toString());
					editor.putInt("userId", Integer.parseInt(output));
					editor.commit();
					
					Toast.makeText(context, "id = " +output, Toast.LENGTH_SHORT).show();
					
					Intent i = new Intent(getBaseContext(), Menu.class);
					startActivity(i);
					finish();

				}
				
				
				super.onPostExecute(result);
			}

			@Override
			protected Void doInBackground(Void... params) {
				HttpClient httpclient = new DefaultHttpClient();
				try{
					HttpPost httppost = new HttpPost("http://sutest.comuv.com/register.php");
					List<NameValuePair> par = new ArrayList<NameValuePair>(2);
					par.add(new BasicNameValuePair("name", txtName.getText().toString()));
					par.add(new BasicNameValuePair("creditCard", txtCard.getText().toString()));
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
    		
		}.execute();
		
		

    }
	

}
