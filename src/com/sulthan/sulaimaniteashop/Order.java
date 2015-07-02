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
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Order extends Activity {

	Context context;
	FoodAdapter foodAdapter;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_order);
	    
	    context = getApplicationContext();
	    checkButtonClick();
	    // TODO Auto-generated method stub
	}
	
	public void pay(View v){
		setContentView(R.layout.screen_payment);
	}
	
	public void confirmPay(View v){
		Toast.makeText(context, "Payment done", Toast.LENGTH_LONG);
		
		Intent i = new Intent(getBaseContext(), Menu.class);
		finish();
		startActivity(i);
	}
	
	public void fetchOrder(View v){
    	
		
		new AsyncTask<Void, Void, Void>() {

    		HttpEntity entity = null;
    		String output = null;
    		
			@Override
			protected void onPostExecute(Void result) {
				String lines[] = output.split("\\r?\\n");
				output = lines[0];
				
				if(output != null ){
					Toast.makeText(context, output, Toast.LENGTH_SHORT).show();
					
					try {
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
						
						
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
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

	private class FoodAdapter extends ArrayAdapter<FoodItem> {
		 
		  private ArrayList<FoodItem> foodList;
		 
		  public FoodAdapter(Context context, int textViewResourceId, ArrayList<FoodItem> foodList) {
		   super(context, textViewResourceId, foodList);
		   this.foodList = new ArrayList<FoodItem>();
		   this.foodList.addAll(foodList);
		  }
		 
		  private class ViewHolder {
		   //TextView name;
		   TextView price;
		   TextView name;
		   TextView quantity;
		  }
		 
		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
		 
		   ViewHolder holder = null;
		   Log.v("ConvertView", String.valueOf(position));
		 
		   if (convertView == null) {
		   LayoutInflater vi = (LayoutInflater)getSystemService(
		     Context.LAYOUT_INFLATER_SERVICE);
		   convertView = vi.inflate(R.layout.list_food_item, null);
		 
		   holder = new ViewHolder();
//		   holder.code = (TextView) convertView.findViewById(R.id.checkBox1);
		   holder.name = (TextView) convertView.findViewById(R.id.itemName);
		   holder.price = (TextView) convertView.findViewById(R.id.itemPrice);
		   holder.quantity = (TextView) convertView.findViewById(R.id.quantity);
		   convertView.setTag(holder);
		 
		    holder.name.setOnClickListener( new View.OnClickListener() {  
		     public void onClick(View v) {  
		      TextView cb = (TextView) v ;  
		      FoodItem food = (FoodItem) cb.getTag();  
//		      Toast.makeText(getApplicationContext(),
//		       "Clicked on Checkbox: " + cb.getText() +
//		       " is " + cb.isChecked(), 
//		       Toast.LENGTH_LONG).show();
//		      food.setSelected(cb.isChecked());
		      
		      food.setQuantity(food.getQuantity()+1);
		      
//		      cb.setText("x "+String.valueOf(food.getQuantity()));
		      //holder.quantity.setText("x "+String.valueOf(food.getQuantity()));
		     }  
		    });  
		   } 
		   else {
		    holder = (ViewHolder) convertView.getTag();
		   }
		 
		   FoodItem food = foodList.get(position);
		   holder.name.setText(food.itemName);
		   holder.price.setText(String.valueOf(food.price));
		   holder.quantity.setText(" x "+ String.valueOf(food.quantity));
		   holder.name.setTag(food);
		 
		   return convertView;
		 
		  }
		 
		 }
	
	private void checkButtonClick() {
		 
		  Button myButton = (Button) findViewById(R.id.findSelected);
		  myButton.setOnClickListener(new OnClickListener() {
		 
		   @Override
		   public void onClick(View v) {
		 
		    StringBuffer responseText = new StringBuffer();
		    responseText.append("The following were selected...\n");
		 
		    ArrayList<FoodItem> countryList = foodAdapter.foodList;
		    for(int i=0;i<countryList.size();i++){
		     FoodItem country = countryList.get(i);
		     if(country.isSelected()){
		      responseText.append("\n" + String.valueOf(country.id));
		     }
		    }
		 
		    Toast.makeText(getApplicationContext(),
		      responseText, Toast.LENGTH_LONG).show();
		 
		   }
		  });
		 
		 }
}

class FoodItem{
	int id;
	String itemName;
	double price;
	boolean selected = false;
	int quantity;
	
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public FoodItem(int id, String itemName, double price){
		this.id = id;
		this.itemName = itemName;
		this.price = price;
		this.quantity = 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	
	
}

