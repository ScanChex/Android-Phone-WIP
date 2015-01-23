package com.scanchex.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;
import com.squareup.picasso.Picasso;

public class SCAdminTapToScanScreen extends BaseActivity{
	
	public static boolean isFromAssetDetail;
	private ImageView companylogoImg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_admin_tapto_scan_screen);
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.adminTapScreen);
		 companylogoImg = (ImageView)findViewById(R.id.logo);
		 companylogoImg.setPadding(20, 5, 20, 5);
			
			Picasso.with(SCAdminTapToScanScreen.this) //
			.load(SCPreferences.getCompanyLogo(SCAdminTapToScanScreen.this)) //
			.placeholder(R.drawable.scan_chexs_logo) //
			.error(R.drawable.scan_chexs_logo) //
			.into(companylogoImg);
		layout.setBackgroundColor(SCPreferences.getColor(SCAdminTapToScanScreen.this));
		
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
	
	public void onTaptoScanClick(View view){
		isFromAssetDetail = false;
		Intent intent = new Intent(this, SCAdminCameraPeviewScreen.class);
		startActivity(intent);
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		if(isFromAssetDetail){
			isFromAssetDetail = false;
			this.finish();
		}
		
	}

}
