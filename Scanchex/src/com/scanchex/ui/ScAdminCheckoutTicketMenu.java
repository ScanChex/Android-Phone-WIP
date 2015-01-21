package com.scanchex.ui;

import com.scanchex.utils.SCPreferences;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class ScAdminCheckoutTicketMenu extends BaseActivity {

	private ImageView companylogoImg;
	Activity mActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_admin_checkout_ticket_menu);
        companylogoImg = (ImageView)findViewById(R.id.logo);
        companylogoImg.setPadding(20, 5, 20, 5);
		
		Picasso.with(ScAdminCheckoutTicketMenu.this) //
		.load(SCPreferences.getCompanyLogo(ScAdminCheckoutTicketMenu.this)) //
		.placeholder(R.drawable.scan_chexs_logo) //
		.error(R.drawable.scan_chexs_logo) //
		.into(companylogoImg);
		mActivity = this;
	}

	public void onClickScanCode(View v) {
		Intent scanCode = new Intent(mActivity,SCAdminScanScreen.class);
		scanCode.putExtra("scanCheck", "scanCheckOutCode");
		startActivityForResult(scanCode, 100);
	}

	public void onClickManualLookUp(View v) {

		Intent manualLook = new Intent(mActivity,ScAdminManualLookUp.class);
		startActivity(manualLook);
	}

	public void onClickTodayCheckout(View v) {
		Intent todaycheckout = new Intent(mActivity, ScAdminTodaycheckout.class);
		todaycheckout.putExtra("title", "TODAY'S CHECKOUT");
		startActivity(todaycheckout);
	}

	public void onClickBack(View v) {
		finish();
	}
	
 
}
