package com.vince.helping.util;

import java.io.IOException;
import java.io.StringReader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.JsonReader;

import cn.bmob.push.PushConstants;

import com.vince.helping.db.DatabaseAdapter;
import com.vince.helpingyou.PushMessageActivity;
import com.vince.to_help_you.R;

/**
 * 接收推送消息
 * @author Administrator
 *
 */
public class MyPushMessageReceiver extends BroadcastReceiver{
	
	private static final int NOTIFICATION_ID = 1;
	DatabaseAdapter da;

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
			String json = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
			String message = null;
			try {
				JsonReader jr = new JsonReader(new StringReader(json));
				jr.beginObject();
				if(jr.hasNext()){
					if(jr.nextName().equals("alert")){
						message = jr.nextString();
					}
				}
				jr.endObject();
				jr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Notification.Builder builder = new Notification.Builder(context);
			builder.setTicker("失物招领消息："+message);
			builder.setContentTitle("失物招领消息");
			builder.setContentText(message);
			builder.setSmallIcon(R.drawable.ic_launcher);
			Intent i = new Intent(context,PushMessageActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);
			builder.setContentIntent(pi);
			Notification n = builder.build();
			NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(NOTIFICATION_ID, n);
			//把推送的消息保存到数据库
			da = new DatabaseAdapter(context);
			da.save(message);
        }
	}
}
