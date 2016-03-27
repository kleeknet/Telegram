package org.telegram.kleegram.autoanswer;


import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.widget.Toast;

import org.telegram.kleegram.QuickCallConstants;

public class ChangeCallLogIntentService extends IntentService {
    static final public String TAG = ChangeCallLogIntentService.class.getSimpleName();

		public ChangeCallLogIntentService() {
			super(TAG);
		}
		public ChangeCallLogIntentService(String name) {
			super(name);
		}
		@Override
		protected void onHandleIntent(Intent intent) {
			// TODO Auto-generated method stub
			Context mContext = getBaseContext();
			String phoneNumber = intent.getData().toString();
		    try {
		        Thread.sleep(4000);

//		        String strNumberOne[] = { "'%"+phoneNumber+"'" };
		        String strNumberOne[] = { phoneNumber };
		        Cursor cursor = mContext.getContentResolver().query(
		                CallLog.Calls.CONTENT_URI, null,
		                /*CallLog.Calls.CONTENT_TYPE+" = '"+CallLog.Calls.INCOMING_TYPE +"' AND "+ */CallLog.Calls.NUMBER + "= ?", strNumberOne, CallLog.Calls.DATE+" DESC");
		        boolean bol = cursor.moveToFirst();
		        if (bol) {
//		            do {
//		        	CallLogInfo callInfo = CallLogUtils.readCallLogInfoByCursor(cursor);
	                long idOfRowToDelete = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));
	                long durationOfRowToDelete = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
//	                SharedPreferences settings = SplashScreenActivity.globalContext.getSharedPreferences(QuickCallConstants.LAST_CALL_PREFERENCES, 0);
	                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
	    		    String num = prefs.getString(QuickCallConstants.LAST_CALL,"00000").toString();
//	                Toast.makeText(mContext, "Detected call hangup event "+ num, Toast.LENGTH_LONG).show();

	        		ContentValues values = new ContentValues();
	        		values.put(CallLog.Calls.NUMBER, num);
	        		values.put(CallLog.Calls.DATE, System.currentTimeMillis());
	        		values.put(CallLog.Calls.DURATION, durationOfRowToDelete);
	        		values.put(CallLog.Calls.TYPE, CallLog.Calls.OUTGOING_TYPE);
	        		values.put(CallLog.Calls.NEW, 1);
	        		values.put(CallLog.Calls.CACHED_NAME, "");
	        		values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
	        		values.put(CallLog.Calls.CACHED_NUMBER_LABEL, "");

	        		int rows;
	        		try {
	        			String where = CallLog.Calls._ID + " = ?";
	        			rows = mContext.getContentResolver().update(CallLog.Calls.CONTENT_URI,values, where, new String[]{String.valueOf(idOfRowToDelete)});
//	        			Toast.makeText(context, "deleteSingleCallLog rows = " + rows, Toast.LENGTH_LONG).show();
//	        			if(LogLevel.DEV){
//	        				DevLog.d(TAG, "deleteSingleCallLog rows = " + rows);
//	        			}
	        		} catch (Exception e){
	        			e.printStackTrace();
	        		} 

	                
//	                CallLogUtils.deleteSingleCallLog(mContext, idOfRowToDelete);
	        		
//		        	startEmptyQuery();
//		                context.getContentResolver().delete(
//		                        CallLog.Calls.CONTENT_URI,
//		                        CallLog.Calls._ID + "= ? ",
//		                        new String[] { String.valueOf(idOfRowToDelete) });
//		            } while (cursor.moveToNext());
		        }else
		        	Toast.makeText(mContext, "Con not Update Call Log ", Toast.LENGTH_LONG).show();
		    } catch (Exception ex) {
//		        Log.v(Consts.TAG,                "Exception, unable to remove # from call log: "  + ex.toString());
		    }
			
		}

}
