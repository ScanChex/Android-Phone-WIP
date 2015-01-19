package com.scanchex.ui;

import com.scanchex.utils.SCPreferences;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

	

	public void onClickBack(View v) {
		finish();
	}
}
