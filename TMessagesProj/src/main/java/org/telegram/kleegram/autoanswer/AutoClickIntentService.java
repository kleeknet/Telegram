package org.telegram.kleegram.autoanswer;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;

public class AutoClickIntentService  extends IntentService {
    static final public String TAG = AutoClickIntentService.class.getSimpleName();

		public AutoClickIntentService() {
			super(TAG);
		}
		public AutoClickIntentService(String name) {
			super(name);
		}

/*
		@Override
		protected void onHandleIntent(Intent intent) {
			// Load preferences
//			SharedPreferences settings1;
//			SharedPreferences settings2;
//			SharedPreferences settings3;
			Context context = getBaseContext();
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//	    	settings1 = context.getSharedPreferences(QuickCallConstants.IS_GLOBAL_PREFERENCES, 0);
		    boolean isGlobal = prefs.getBoolean(QuickCallConstants.IS_GLOBAL,false);

//	    	settings2 = context.getSharedPreferences(QuickCallConstants.LAST_CALL_PREFERENCES, 0);
		    String mLastCall = prefs.getString(QuickCallConstants.LAST_CALL,"0000");
			
		    if(isGlobal){
//		    	settings3 = context.getSharedPreferences(QuickCallConstants.OPERATOR_PREFERENCES, 0);
			    String mOperator = prefs.getString(QuickCallConstants.OPERATOR,"IranCell");
				String new_num=DialUtils.operatorPrefix(mOperator) + mLastCall +"#";// Uri.encode("#");
		        Toast.makeText(context, "Changed to "+new_num+"-"+mOperator, Toast.LENGTH_LONG).show();


//		        AutoAnswerReceiver.calledNum(mLastCall);
				try {
					clickPhoneAidl(context,new_num);
				}
				catch (Exception e) {
					e.printStackTrace();
					Log.d("AutoAnswer","Error trying to answer using telephony service.  Falling back to headset.");
				}
	    	}
			
			
			
		}
*/
	    @Override
	    protected void onHandleIntent(Intent intent) {
	        Log.v(TAG, "onHandleIntent()");
	 
	        Intent intentActivity = new Intent(Intent.ACTION_CALL);
	        intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        Uri num = intent.getData();
	        intentActivity.setData(num);
	        startActivity(intentActivity);
	 
	    }		
	    
	    
		private void clickPhoneAidl(Context context,String num) throws Exception {
			// Set up communication with the telephony service (thanks to Tedd's Droid Tools!)
			TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			Class c = Class.forName(tm.getClass().getName());
			Method m = c.getDeclaredMethod("getITelephony");
			m.setAccessible(true);
			ITelephony telephonyService;
			telephonyService = (ITelephony)m.invoke(tm);

			// Silence the ringer and answer the call!
			telephonyService.silenceRinger();
			telephonyService.answerRingingCall();
			telephonyService.endCall();
            telephonyService.dial(num);
            telephonyService.abortCall();
		}
		
}
