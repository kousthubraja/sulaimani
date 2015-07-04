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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

	private static final String SERVER_URL = "http://sutest.comuv.com/";
	SharedPreferences sharedPref;
	Context context;
	
	OrderAdapter orderAdapter;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_cancel);
	    
	    sharedPref = getSharedPreferences("userAuth",Context.MODE_PRIVATE);
	    context = getApplicationContext();
	    fetchOrder(null);
	   
	}
	
	public void fetchOrder(View v){

		new AsyncTask<Void, Void, Void>() {

    		HttpEntity entity = null;
    		String output = null;
    		
			@Override
			protected Void doInBackground(Void... params) {
				HttpClient httpclient = new DefaultHttpClient();
				try{
					HttpPost httppost = new HttpPost(SERVER_URL + "listOrders.php");
					
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
			
			@Override
			protected void onPostExecute(Void result) {
				if(output != null ){
					try {
						String lines[] = output.split("\\r?\\n");
						output = lines[0];
						
						JSONArray reader = new JSONArray(output);
						ArrayList<OrderItem> itemList = new ArrayList<OrderItem>();
						
						for(int i=0; i< reader.length(); i++){
							JSONObject obj = reader.getJSONObject(i);
							OrderItem item = new OrderItem(obj.getInt("orderid"), obj.getString("date"), obj.getString("totalPrice"));
							itemList.add(item);
						}
						
						orderAdapter = new OrderAdapter(context, R.layout.list_order_item, itemList);
						ListView listView1 = (ListView) findViewById(R.id.orderListView);
						listView1.setAdapter(orderAdapter);
						
					} catch (Exception e) {
						
						e.printStackTrace();
					}
					
				}
				else{
					Toast.makeText(getApplicationContext(), "Cannot retrieve orders, No internet connection.", Toast.LENGTH_LONG).show();
					finish();
				}
				
				super.onPostExecute(result);
			}
    		
		}.execute();

    }

	public void confirmCancel(final int orderId){
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		        	cancelOrder(orderId);
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(Cancel.this);
		builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
		    .setNegativeButton("No", dialogClickListener).show();
		
		
				
	}
	
	public void cancelOrder(final int orderId){
		new AsyncTask<Void, Void, Void>() {

    		HttpEntity entity = null;
    		String output = null;

			@Override
			protected Void doInBackground(Void... params) {
				HttpClient httpclient = new DefaultHttpClient();
				try{
					HttpPost httppost = new HttpPost(SERVER_URL + "cancelOrder.php");
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
			
			@Override
			protected void onPostExecute(Void result) {
				if(output != null ){
					String lines[] = output.split("\\r?\\n");
					output = lines[0];
					Toast.makeText(context, "Order Cancelled", Toast.LENGTH_SHORT).show();
					finish();
				}
				else{
					Toast.makeText(getApplicationContext(), "Cannot cancel order, No internet connection.", Toast.LENGTH_LONG).show();
				}
				super.onPostExecute(result);
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
				
				holder.date.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						TextView cb = (TextView) v ;
						OrderItem order = (OrderItem) cb.getTag();
						//Toast.makeText(context, order.date, Toast.LENGTH_SHORT).show();
						confirmCancel(order.orderId);
						//return true;
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
