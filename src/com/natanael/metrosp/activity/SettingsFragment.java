package com.natanael.metrosp.activity;

import java.util.Calendar;

import com.natanael.metrosp.R;
import com.natanael.metrosp.activity.Main.MainReceiver;
import com.natanael.metrosp.network.Constantes;
import com.natanael.metrosp.service.UpdateService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener {

	CheckBoxPreference checkUpdateEnabled;
	EditTextPreference timeText;
	SharedPreferences sharedPref;
	
	Calendar cal;
	AlarmManager am;
	PendingIntent pendingIntent;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settingsmenu);
        
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        
        timeText = (EditTextPreference) findPreference("pref_key_auto_update_time");
        checkUpdateEnabled = (CheckBoxPreference) findPreference("pref_key_auto_update_enable");
        
        checkUpdateEnabled.setOnPreferenceChangeListener(this);
        
        boolean updateEnable = sharedPref.getBoolean("pref_key_auto_update_enable", false);
    	checkUpdateEnabled.setChecked(updateEnable);
    	timeText.setEnabled(updateEnable);

        
        timeText.setOnPreferenceChangeListener(this);
        timeText.setSummary(String.format(getResources().getString(R.string.settings_menu_auto_update_time_hint), sharedPref.getString("pref_key_auto_update_time", "5")));
    }

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if ("pref_key_auto_update_time".equals(preference.getKey())) {
			timeText.setSummary(String.format(getResources().getString(R.string.settings_menu_auto_update_time_hint), (String) newValue));
			startAutoUpdate((Integer.parseInt((String)newValue)));
			return true;
		} else if ("pref_key_auto_update_enable".equals(preference.getKey())){
			checkUpdateEnabled.setChecked((Boolean) newValue);
			if ((Boolean) newValue) {
				timeText.setEnabled(true);
				startAutoUpdate(Integer.parseInt(sharedPref.getString("pref_key_auto_update_time", "5")));
			} else {
				timeText.setEnabled(false);
				stopAutoUpdate();
			}
			return true;
		}
		return false;
	}
	
	public void startAutoUpdate(int delay) {
		/*Context context = getActivity().getApplicationContext();
		
		cal = Calendar.getInstance();
		//cal.add(Calendar.SECOND, delay);

		//Intent intent = new Intent(Constantes.INTENT_ACTION_ALARM_UPDATE);
		Intent intent = new Intent(getActivity().getApplicationContext(), UpdateService.class);
		//PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pendingIntent);*/
		cal = Calendar.getInstance();
		stopAutoUpdate();
		am.setRepeating(AlarmManager.RTC, cal.getTimeInMillis() + (delay*1000), delay*1000, pendingIntent);
	}
	
	public void stopAutoUpdate() {
		Context context = getActivity().getApplicationContext();

		Intent intent = new Intent(getActivity().getApplicationContext(), UpdateService.class);
		pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pendingIntent);
	}
	

	
}
