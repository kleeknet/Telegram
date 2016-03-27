package org.telegram.kleegram;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import org.telegram.messenger.R;

/**
 * Created by MHP .
 */
public class MyNotification {


    public  static  int TAG_NOTIFICATIONID = 100;


    public static void startNotification(Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isGlobal = prefs.getBoolean(QuickCallConstants.IS_GLOBAL, false);
        if(isGlobal){
            MyNotification.createDefaultNotification(context, R.drawable.mhp_status_on,R.string.nitification_click_content_disable);
        }else{
            MyNotification.createDefaultNotification(context, R.drawable.mhp_status_off,R.string.nitification_click_content_enable);
        }
    }




    public static void createDefaultNotification(Context context,int statusBarIcon,int contentStringId){
        Bitmap icon1 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.click_logo);


        Intent switchIntent = new Intent(context, NotificationClick.class);
        switchIntent.setAction("com.sipax.click.notification");
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(context, 110, switchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context)
                .setAutoCancel(false)
                .setContentTitle(context.getResources().getString(R.string.nitification_click_title))
                .setContentText(context.getResources().getString(contentStringId))
                .setSmallIcon(statusBarIcon)
                .setLargeIcon(icon1)
                .setContentIntent(resultPendingIntent);


        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(TAG_NOTIFICATIONID, notification);

    }


    public static void notificationForLastCallCost(Context context,String cost){
        Bitmap icon1 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.click_logo);


        Intent switchIntent = new Intent(context, NotificationClick.class);
        switchIntent.setAction("com.sipax.click.notification");
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(context, 120, switchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context)
                .setAutoCancel(false)
                .setContentTitle(context.getResources().getString(R.string.nitification_cost))
                .setContentText(cost)
                .setSmallIcon(R.drawable.click_logo)
                .setLargeIcon(icon1)
                .setContentIntent(resultPendingIntent);


        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(TAG_NOTIFICATIONID, notification);

    }
}
