package com.scanchex.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
