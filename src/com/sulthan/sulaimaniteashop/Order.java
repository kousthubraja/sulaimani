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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Order extends Activity {

	Context context;
	FoodAdapter foodAdapter;
	SharedPreferences sharedPref;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_order);
	    
	    context = getApplicationContext();
	    sharedPref = getSharedPreferences("userAuth",Context.MODE_PRIVATE);
	    fetchOrder(null);
//	    checkButtonClick();
	    // TODO Auto-generated method stub
	}
	
	public void pay(View v){
		List<FoodItem> itemList = foodAdapter.foodList;
		JSONObject order = new JSONObject();
		JSONArray items = new JSONArray();
		double totalPrice = 0;
		
		
		try {
			order.put("userName",sharedPref.getString("userName", ""));
			for(FoodItem temp:itemList){
				
				if(temp.quantity>0){
					JSONObject tmp = new JSONObject();
					tmp.put(String.valueOf(temp.id), temp.quantity);
					items.put(tmp);
					totalPrice += temp.price*temp.quantity;
				}
			}

			order.put("items", items);
			
			String orderJSON = order.toString();
			
			sendOrder(orderJSON, totalPrice);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	String getDateTime(){
		int day, month, year;
		DatePicker dp = (DatePicker) findViewById(R.id.datePicker1);
		TimePicker tp = (TimePicker) findViewById(R.id.timePicker1);
		day = dp.getDayOfMonth();
		month = dp.getMonth();
		year = dp.getYear();
		
		StringBuffer bdate = new StringBuffer();
		bdate.append(day);
		bdate.append("/");
		bdate.append(month);
		bdate.append("/");
		bdate.append(year);
		bdate.append("     ");
		
		
		
		bdate.append(tp.getCurrentHour());
		bdate.append(":");
		bdate.append(tp.getCurrentMinute());

		String date = bdate.toString();
		
		return date;
	}
	
	public void confirmPay(View v){
		Toast.makeText(context, "Payment done", Toast.LENGTH_LONG).show();
		
		
		finish();
		
	}
	
	public void chooseDate(View v){
		setContentView(R.layout.screen_choosedate);
		
	}
	
	public void fetchOrder(View v){
    	
		
		new AsyncTask<Void, Void, Void>() {

    		HttpEntity entity = null;
    		String output = null;
    		
			@Override
			protected void onPostExecute(Void result) {
				if(output != null ){
					//Toast.makeText(context, output, Toast.LENGTH_SHORT).show();
					
					try {
						String lines[] = output.split("\\r?\\n");
						output = lines[0];
						
						JSONArray reader = new JSONArray(output);
						
						ArrayList<FoodItem> itemList = new ArrayList<FoodItem>();
						
						for(int i=0; i< reader.length(); i++){
							JSONObject obj = reader.getJSONObject(i);
							//Toast.makeText(context, obj.getString("itemName"), Toast.LENGTH_SHORT).show();
							FoodItem item = new FoodItem(obj.getInt("id"), obj.getString("itemName"), obj.getDouble("price"));
							itemList.add(item);
						}
						foodAdapter = new FoodAdapter(context, R.layout.list_food_item, itemList);
						ListView listView1 = (ListView) findViewById(R.id.listView1);
						foodAdapter.notifyDataSetChanged();
						listView1.setAdapter(foodAdapter);
						
						for(FoodItem temp:itemList){
						//	Toast.makeText(context, temp.getItemName(), Toast.LENGTH_SHORT).show();
							
						}
						
						
						
					} catch (Exception e) {
						
						e.printStackTrace();
					}
					
				}
				
				super.onPostExecute(result);
			}

			@Override
			protected Void doInBackground(Void... params) {
				HttpClient httpclient = new DefaultHttpClient();
				try{
					HttpPost httppost = new HttpPost("http://sutest.comuv.com/listitems.php");
					List<NameValuePair> par = new ArrayList<NameValuePair>(2);
//					par.add(new BasicNameValuePair("name", txtName.getText().toString()));
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
	
	void sendOrder(String orderJSON, final double totalPrice){
		new AsyncTask<String, Void, Void>() {

    		HttpEntity entity = null;
    		String output = null;
    		
			@Override
			protected void onPostExecute(Void result) {
				String lines[] = output.split("\\r?\\n");
				output = lines[0];
				
				if(output != null ){
					
					Toast.makeText(context, output, Toast.LENGTH_LONG).show();

					finish();

				}
				
				super.onPostExecute(result);
			}

			@Override
			protected Void doInBackground(String... orderJSON) {
				//Toast.makeText(context, "Order JSON : ", Toast.LENGTH_LONG).show();
				HttpClient httpclient = new DefaultHttpClient();
				try{
					
					HttpPost httppost = new HttpPost("http://sutest.comuv.com/addOrder.php");
					List<NameValuePair> par = new ArrayList<NameValuePair>(2);
					par.add(new BasicNameValuePair("userId", String.valueOf(sharedPref.getInt("userId", 0))));
					par.add(new BasicNameValuePair("orderJSON", orderJSON[0]));
					par.add(new BasicNameValuePair("totalPrice", String.valueOf(totalPrice)));
					par.add(new BasicNameValuePair("date", getDateTime()));
					httppost.setEntity(new UrlEncodedFormEntity(par, "UTF-8"));
					HttpResponse response = httpclient.execute(httppost);
					entity = response.getEntity();
					output = EntityUtils.toString(entity);
				}
				catch(Exception e){
					Log.e("Error", e.getMessage());

				}
				return null;
			}
    		
		}.execute(orderJSON);
	}

//	private void checkButtonClick() {
//		 
//		  Button myButton = (Button) findViewById(R.id.findSelected);
//		  myButton.setOnClickListener(new OnClickListener() {
//		 
//		   @Override
//		   public void onClick(View v) {
//		 
//		    StringBuffer responseText = new StringBuffer();
//		    responseText.append("The following were selected...\n");
//		 
//		    ArrayList<FoodItem> countryList = foodAdapter.foodList;
//		    for(int i=0;i<countryList.size();i++){
//		     FoodItem country = countryList.get(i);
//		     if(country.isSelected()){
//		      responseText.append("\n" + String.valueOf(country.id));
//		     }
//		    }
//		 
//		    Toast.makeText(getApplicationContext(),
//		      responseText, Toast.LENGTH_LONG).show();
//		 
//		   }
//		  });
//		 
//		 }
	
	
}



