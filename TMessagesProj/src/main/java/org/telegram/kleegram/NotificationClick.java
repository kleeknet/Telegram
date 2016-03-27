package org.telegram.kleegram;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.telegram.messenger.R;


/**
 * Created by MHP.
 */
public class NotificationClick extends BroadcastReceiver {

    public static final String BROADCAST_ACTION = "com.mhp.broadcast.displayevent";
    private final Handler handler = new Handler();
    Intent intent;
    private boolean checkBox;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isGlobal = prefs.getBoolean(QuickCallConstants.IS_GLOBAL, false);
        if(isGlobal){
            Toast.makeText(context, context.getResources().getString(R.string.is_global_off), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context, context.getResources().getString(R.string.is_global_on), Toast.LENGTH_LONG).show();
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(QuickCallConstants.IS_GLOBAL, !isGlobal);
        editor.commit();

        this.intent = new Intent(BROADCAST_ACTION);
        checkBox = !isGlobal;
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 10);

        MyNotification.startNotification(context);
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            display();
        }
    };

    private void display() {
        intent.putExtra("isChecked", checkBox);
        context.sendBroadcast(intent);
    }
}
