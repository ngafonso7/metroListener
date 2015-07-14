package com.natanael.metrosp.service;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

public class UpdateService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Notification not = new Notification.Builder(getBaseContext())
			.setSmallIcon(R.drawable.btn_minus)
			.setContentTitle("alarm")
			.setContentText("aaaaaaaaaaaaaa")
			.build();
			
		NotificationManager nm = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(0, not);
		
		Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(2000);
		
		return super.onStartCommand(intent, flags, startId);
		

	}
}
