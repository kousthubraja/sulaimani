package com.sulthan.sulaimaniteashop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class Menu extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_menu);
	    // TODO Auto-generated method stub
	}

	public void openFeedback(View v){
		Intent i = new Intent(getBaseContext(), Feedback.class);
		startActivity(i);
	}
	
	public void openOrder(View v){
		Intent i = new Intent(getBaseContext(), Order.class);
		startActivity(i);
	}
	
	public void openCancel(View v){
		Intent i = new Intent(getBaseContext(), Cancel.class);
		startActivity(i);
	}
	
	public void exit(View v){
		finish();
	}
}
