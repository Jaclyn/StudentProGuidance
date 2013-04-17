package com.psm.StudentProGuidance.others;
/**
 * 
 */

/**
 * @author User
 *
 */


import com.psm.StudentProGuidance.activities.MainActivity;
import com.psm.StudentProGuidance.activities.R;
import com.psm.StudentProGuidance.controllers.AssignmentDataSource;
import com.psm.StudentProGuidance.controllers.ClassDataSource;
import com.psm.StudentProGuidance.controllers.TestDataSource;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class AlarmService extends Service 
{
	private Bundle receiveBundle;
	private NotificationManager notiManager;
	private ClassDataSource cladatasource;
	private AssignmentDataSource assigndatasource;
	private TestDataSource testdatasource;
	private long id;
	private  int category;

	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		cladatasource = new ClassDataSource(this);
		assigndatasource = new AssignmentDataSource(this);
		testdatasource = new TestDataSource(this);
		
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		receiveBundle = intent.getExtras();

		//alarm.SetAlarm(context);
		notiManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		String title =  receiveBundle.getString("source");
		category =  receiveBundle.getInt("category"); 
		int key = receiveBundle.getInt("key");
		id = receiveBundle.getLong("id");
		String text = receiveBundle.getString("descript");

		Notification notification = new Notification();
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if(alarmSound == null){
			alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			if(alarmSound == null){
				alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			}
		}

		long[] vibrate = { 0, 300, 500 };

		if(category==1){

			notification.icon = R.drawable.ic_class_48;
			notification.sound = alarmSound;
			notification.vibrate = vibrate;
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			Intent gotoIntent = new Intent(this, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity
					(this, key, gotoIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			notification.setLatestEventInfo(this, title, text,pendingIntent );

			try {
				cladatasource.open();
				cladatasource.setAlarmNull(id);
				cladatasource.close();
			/*	Toast.makeText(this, "Alarm Destroy"
						, Toast.LENGTH_LONG).show();*/
			} catch (Exception e) {
				Log.e("Alarm OnDestroy", ""+e.getMessage());
				cladatasource.close();
			}

		}else if(category==2){

			notification.icon = R.drawable.ic_assignment_52;
			notification.sound = alarmSound;
			notification.vibrate = vibrate;
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			Intent gotoIntent = new Intent(this, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity
					(this, key, gotoIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			notification.setLatestEventInfo(this, title, text,pendingIntent );

			try {
				assigndatasource.open();
				assigndatasource.setAlarmNull(id);
				assigndatasource.close();
			/*	Toast.makeText(this, "Alarm Destroy"
						, Toast.LENGTH_LONG).show();*/
			} catch (Exception e) {
				Log.e("Alarm OnDestroy", ""+e.getMessage());
				assigndatasource.close();
			}
		}else if(category==3){
			
			notification.icon = R.drawable.ic_test_52;
			notification.sound = alarmSound;
			notification.vibrate = vibrate;
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			Intent gotoIntent = new Intent(this, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity
					(this, key, gotoIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			notification.setLatestEventInfo(this, title, text,pendingIntent );

			try {
				testdatasource.open();
				testdatasource.setAlarmNull(id);
				testdatasource.close();
			/*	Toast.makeText(this, "Alarm Destroy"
						, Toast.LENGTH_LONG).show();*/
			} catch (Exception e) {
				Log.e("Alarm OnDestroy", ""+e.getMessage());
				testdatasource.close();
			}
		}

		notiManager.notify(key, notification);

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}