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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class Feedback extends Activity {

	EditText txtfeedback;
	SharedPreferences sharedPref;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("Send Feedback");
        setContentView(R.layout.activity_feedback);
        sharedPref = getSharedPreferences("userAuth",Context.MODE_PRIVATE);
        txtfeedback = (EditText) findViewById(R.id.txtfeedback);
        Toast.makeText(this, String.valueOf(sharedPref.getInt("userId", 0)), Toast.LENGTH_SHORT).show();
        //registerUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void sendFeedback(View v){
    	
    	new AsyncTask<Void, Void, Void>() {

    		HttpEntity entity = null;
    		String output = null;
    		
			@Override
			protected void onPostExecute(Void result) {
				txtfeedback.setText("finish");
				if(output != null){
					txtfeedback.setText(output);
				}
				
				super.onPostExecute(result);
			}

			@Override
			protected Void doInBackground(Void... params) {
				HttpClient httpclient = new DefaultHttpClient();
				try{
					HttpPost httppost = new HttpPost("http://sutest.comuv.com/sendfeedback.php");
					List<NameValuePair> par = new ArrayList<NameValuePair>(2);
					par.add(new BasicNameValuePair("name", sharedPref.getString("userName", "")));
					par.add(new BasicNameValuePair("feedback", txtfeedback.getText().toString()));
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
