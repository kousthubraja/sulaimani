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
import org.json.JSONObject;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Cancel extends Activity {

	Context context;
	OrderAdapter orderAdapter;
	SharedPreferences sharedPref;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_cancel);
	    
	    sharedPref = getSharedPreferences("userAuth",Context.MODE_PRIVATE);
	    context = getApplicationContext();
	    fetchOrder(null);
	    // TODO Auto-generated method stub
	}
	
	public void fetchOrder(View v){
    	//Toast.makeText(context, String.valueOf(sharedPref.getInt("userId", 0)), Toast.LENGTH_LONG).show();
		
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
						
						ArrayList<OrderItem> itemList = new ArrayList<OrderItem>();
						
						for(int i=0; i< reader.length(); i++){
							JSONObject obj = reader.getJSONObject(i);
							//Toast.makeText(context, obj.getString("date"), Toast.LENGTH_SHORT).show();
							OrderItem item = new OrderItem(obj.getInt("orderid"), obj.getString("date"), obj.getString("totalPrice"));

							itemList.add(item);
						}
						orderAdapter = new OrderAdapter(context, R.layout.list_order_item, itemList);
						ListView listView1 = (ListView) findViewById(R.id.orderListView);
						orderAdapter.notifyDataSetChanged();
						listView1.setAdapter(orderAdapter);
						
						for(OrderItem temp:itemList){
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
					HttpPost httppost = new HttpPost("http://sutest.comuv.com/listOrders.php");
					List<NameValuePair> par = new ArrayList<NameValuePair>(2);
					par.add(new BasicNameValuePair("userId", String.valueOf(sharedPref.getInt("userId", 0))));
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

	public void cancelOrder(final int orderId){
		new AsyncTask<Void, Void, Void>() {

    		HttpEntity entity = null;
    		String output = null;
    		
			@Override
			protected void onPostExecute(Void result) {
				
				
				if(output != null ){
					String lines[] = output.split("\\r?\\n");
					output = lines[0];
					Toast.makeText(context, "Order Cancelled" + output, Toast.LENGTH_SHORT).show();
					
				}
				
				super.onPostExecute(result);
			}

			@Override
			protected Void doInBackground(Void... params) {
				HttpClient httpclient = new DefaultHttpClient();
				try{
					HttpPost httppost = new HttpPost("http://sutest.comuv.com/cancelOrder.php");
					List<NameValuePair> par = new ArrayList<NameValuePair>(2);
					par.add(new BasicNameValuePair("orderId", String.valueOf(orderId)));
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
	
	private class OrderAdapter extends ArrayAdapter<OrderItem> {
		 
		ArrayList<OrderItem> orderList;
		Context context;
		
		public OrderAdapter(Context context, int textViewResourceId, ArrayList<OrderItem> orderList) {
			super(context, textViewResourceId, orderList);
			this.orderList = new ArrayList<OrderItem>();
			this.orderList.addAll(orderList);
			this.context = context;
		}

		private class ViewHolder {
			//TextView name;
			TextView date;
			TextView price;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			Log.v("ConvertView", String.valueOf(position));
			final int pos = position;

			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.list_order_item, null);

				holder = new ViewHolder();
				holder.date = (TextView) convertView.findViewById(R.id.txtOrderDate);
				holder.price = (TextView) convertView.findViewById(R.id.txtOrderPrice);
				convertView.setTag(holder);
				
				holder.date.setOnLongClickListener(new View.OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						TextView cb = (TextView) v ;
						OrderItem order = (OrderItem) cb.getTag();
						//Toast.makeText(context, order.date, Toast.LENGTH_SHORT).show();
						cancelOrder(order.orderId);
						finish();
						
						return true;
					}
				});
			}
			else {
				holder = (ViewHolder) convertView.getTag();
			}

			OrderItem order = orderList.get(position);
			holder.date.setText(order.date);
			holder.price.setText(order.totalPrice);
//			holder.quantity.setText(" x "+ String.valueOf(order.quantity));
			holder.date.setTag(order);

			return convertView;

		}
	}

}
