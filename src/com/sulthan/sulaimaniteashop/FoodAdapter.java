package com.sulthan.sulaimaniteashop;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class FoodAdapter extends ArrayAdapter<FoodItem> {
	 
	ArrayList<FoodItem> foodList;
	Context context;
	
	public FoodAdapter(Context context, int textViewResourceId, ArrayList<FoodItem> foodList) {
		super(context, textViewResourceId, foodList);
		this.foodList = new ArrayList<FoodItem>();
		this.foodList.addAll(foodList);
		this.context = context;
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
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.list_food_item, null);

			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.itemName);
			holder.price = (TextView) convertView.findViewById(R.id.itemPrice);
			holder.quantity = (TextView) convertView.findViewById(R.id.quantity);
			convertView.setTag(holder);

			holder.name.setOnClickListener( new View.OnClickListener() {
				public void onClick(View v) {
					TextView cb = (TextView) v ;
					FoodItem food = (FoodItem) cb.getTag();

					food.setQuantity(food.getQuantity()+1);
					TextView tp;
					
					ViewGroup parent = (ViewGroup) v.getParent();
					tp = (TextView) parent.findViewById(R.id.quantity);
					tp.setText("x "+ String.valueOf(food.quantity));
//					Toast.makeText(context, String.valueOf(parent.getChildCount()), Toast.LENGTH_SHORT).show();
				}
			});  
			
			holder.name.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					TextView cb = (TextView) v ;
					FoodItem food = (FoodItem) cb.getTag();

					food.setQuantity(0);
					TextView tp;
					
					ViewGroup parent = (ViewGroup) v.getParent();
					tp = (TextView) parent.findViewById(R.id.quantity);
					tp.setText("x "+ String.valueOf(food.quantity));
//					Toast.makeText(context, String.valueOf(parent.getChildCount()), Toast.LENGTH_SHORT).show();
					return true;
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