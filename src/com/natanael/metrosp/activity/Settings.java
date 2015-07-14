package com.natanael.metrosp.activity;

import com.natanael.metrosp.R;

import android.app.Activity;
import android.os.Bundle;

public class Settings extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		getFragmentManager().beginTransaction()
			.replace(R.id.main_frame, new SettingsFragment())
			.commit();
	}

}
