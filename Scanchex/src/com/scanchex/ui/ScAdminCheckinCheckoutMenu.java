package com.scanchex.ui;

import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class ScAdminCheckinCheckoutMenu extends BaseActivity {

	private ImageView companylogoImg;
	Activity mActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_admin_checkin_menu_screen);
		 companylogoImg = (ImageView)findViewById(R.id.logo);
		 companylogoImg.setPadding(20, 5, 20, 5);
			
			Picasso.with(ScAdminCheckinCheckoutMenu.this) //
			.load(SCPreferences.getCompanyLogo(ScAdminCheckinCheckoutMenu.this)) //
			.placeholder(R.drawable.scan_chexs_logo) //
			.error(R.drawable.scan_chexs_logo) //
			.into(companylogoImg);
		    mActivity = this;
	}

	public void onClickCheckout(View v) {
		Intent checkOut = new Intent(mActivity,ScAdminCheckoutTicketMenu.class);
		startActivity(checkOut);
		finish();
	}

	public void onClickCheckin(View v) {
		Intent checkIn = new Intent(mActivity,
				SCAdminCheckinTicketViewScreen.class);
		checkIn.putExtra("title", "CHECK-IN ASSETS");
		startActivity(checkIn);
		finish();
		
	}
	
	 @Override
	    protected void onStart() {
	        super.onStart();       
	     
	        	if ( SCPreferences.getPreferences().getUserFullName(this).length()>0) {
	        		if (Resources.getResources().isLaunchloginactivity()  && Resources.getResources().isFromBackground())  {
	        	//	fireAlarm();
	        			Log.i("Base Activity", "App in foreground after 10 mins ");
	        			 Resources.getResources().setLaunchloginactivity(false);
	        			 Resources.getResources().setFromBackground(false);
	        			Intent i = new Intent(this, SCLoginScreen.class);
	        			i.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
	    		        
	        			startActivity(i);
	        		   
	        		}
	        	    	
	        		}
	      
	    }

	

	public void onClickBack(View v) {
		finish();
	}
}
