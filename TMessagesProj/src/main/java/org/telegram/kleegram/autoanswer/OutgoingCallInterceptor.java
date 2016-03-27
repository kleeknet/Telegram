package org.telegram.kleegram.autoanswer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.kleegram.DialUtils;
import org.telegram.kleegram.QuickCallConstants;
import org.telegram.messenger.R;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class OutgoingCallInterceptor extends BroadcastReceiver {
    private static final String TAG = OutgoingCallInterceptor.class.getSimpleName();

    public static boolean mStateOutgoingCall;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isGlobal = prefs.getBoolean(QuickCallConstants.IS_GLOBAL, false);

        String number = getResultData();
        if (number == null)
            number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

        if (!isGlobal || (number != null && number.startsWith("*"))) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(QuickCallConstants.CLICK_FLAG, true);

        Log.v(TAG, "tel:" + number);

        editor.putString(QuickCallConstants.LAST_CALL, number);
        editor.commit();


        cancelCurrentCall();

        if (!ApplicationLoader.check_net(context, false)) {
            Toast.makeText(ApplicationLoader.applicationContext, "تماس با کلیگرام به علت عدم اتصال به اینترنت میسر نمی باشد."
                    , Toast.LENGTH_LONG).show();
        } else {
            DialUtils.callWithInternet(number);
        }

        mStateOutgoingCall = true;

    }

    private void cancelCurrentCall() {
        setResultData(null);

        try {
            //String serviceManagerName = "android.os.IServiceManager";
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";

            Class telephonyClass;
            Class telephonyStubClass;
            Class serviceManagerClass;
            Class serviceManagerStubClass;
            Class serviceManagerNativeClass;
            Class serviceManagerNativeStubClass;

            Method telephonyCall;
            Method telephonyEndCall;
            Method telephonyAnswerCall;
            Method getDefault;

            Method[] temps;
            Constructor[] serviceManagerConstructor;

            // Method getService;
            Object telephonyObject;
            Object serviceManagerObject;

            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);

            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);

            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod(
                    "asInterface", IBinder.class);

            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");

            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);

            telephonyObject = serviceMethod.invoke(null, retbinder);
            //telephonyCall = telephonyClass.getMethod("call", String.class);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            //telephonyAnswerCall = telephonyClass.getMethod("answerRingingCall");

            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}