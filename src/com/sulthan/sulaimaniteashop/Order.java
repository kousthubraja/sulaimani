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
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Order extends Activity {

	private static final String SERVER_URL = "http://sutest.comuv.com/";
	private SharedPreferences sharedPref;
	Context context;
	
	FoodAdapter foodAdapter;
	String orderJSON;
	double totalPrice = 0;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	  //Remove notification bar
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
	    setContentView(R.layout.activity_order);
	    
	    context = getApplicationContext();
	    sharedPref = getSharedPreferences("userAuth",Context.MODE_PRIVATE);
	    
	  
	    fetchItems(null);

	}
	
	//Get all the orders the user have placed and display it
	public void fetchItems(View v){
		Toast.makeText(context,"Retrieving items...", Toast.LENGTH_SHORT).show();;
		
		new AsyncTask<Void, Void, Void>() {

    		HttpEntity entity = null;
    		String output = null;

			@Override
			protected Void doInBackground(Void... params) {
				HttpClient httpclient = new DefaultHttpClient();
				try{
					HttpPost httppost = new HttpPost(SERVER_URL + "listitems.php");
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
						ListView listView1 = (ListView) findViewById(R.id.lstOrders);
						foodAdapter.notifyDataSetChanged();
						listView1.setAdapter(foodAdapter);
						
					} catch (Exception e) {
						
						e.printStackTrace();
					}
					
				}
				else{
					Toast.makeText(getApplicationContext(), "Cannot retrieve items, No internet connection.", Toast.LENGTH_LONG).show();
					finish();
				}
				
				super.onPostExecute(result);
			}
    		
		}.execute();
		
		

    }
	
	public void pay(View v){
		sendOrder(orderJSON, totalPrice);
		
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
		List<FoodItem> itemList = foodAdapter.foodList;
		JSONObject order = new JSONObject();
		JSONArray items = new JSONArray();
		totalPrice = 0;
		
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
			
			if(totalPrice == 0){
				Toast.makeText(context, "No item selected", Toast.LENGTH_SHORT).show();
				return;
			}

			order.put("items", items);
			
			orderJSON= order.toString();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		setContentView(R.layout.screen_choosedate);
		
	}
	
	
	
	void sendOrder(final String orderJSON, final double totalPrice){
		new AsyncTask<String, Void, Void>() {

    		String output = null;
    		
			@Override
			protected Void doInBackground(String... orderJSON) {
				HttpClient httpclient = new DefaultHttpClient();
				HttpEntity entity = null;
				
				try{
					
					HttpPost httppost = new HttpPost(SERVER_URL + "addOrder.php");
					
					//Sets the POST request parameters
					List<NameValuePair> par = new ArrayList<NameValuePair>(2);
					par.add(new BasicNameValuePair("userId", String.valueOf(sharedPref.getInt("userId", 0))));
					par.add(new BasicNameValuePair("orderJSON", orderJSON[0]));
					par.add(new BasicNameValuePair("totalPrice", String.valueOf(totalPrice)));
					par.add(new BasicNameValuePair("date", getDateTime()));
					httppost.setEntity(new UrlEncodedFormEntity(par, "UTF-8"));
					
					//Send the order JSON and other details
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
					
					Toast.makeText(context, "Order placed", Toast.LENGTH_LONG).show();
					finish();
				}
				else{
					Toast.makeText(context, "Cannot place order, No internet connection.", Toast.LENGTH_LONG).show();
				}
				
				super.onPostExecute(result);
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



