/*
 * AutoAnswer
 * Copyright (C) 2010 EverySoft
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.telegram.kleegram.autoanswer;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import org.telegram.kleegram.GetLastCallCostService;
import org.telegram.kleegram.QuickCallConstants;


public class AutoAnswerReceiver extends BroadcastReceiver {
//	private static String mNumber="";
	private Context mContext;
	@Override
	public void onReceive(Context context, Intent intent) {

		// Load preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		// Check phone state
        String action = intent.getAction();
		String phone_state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
	    boolean forClick = prefs.getBoolean(QuickCallConstants.CLICK_FLAG,false);
        boolean isAutoAnswer = prefs.getBoolean(QuickCallConstants.IS_AUTO_ANSWER,true);
		mContext = context;

		if (phone_state.equals(TelephonyManager.EXTRA_STATE_RINGING)){// && prefs.getBoolean("enabled", true)) {
			// Check for "second call" restriction
			if (prefs.getBoolean("no_second_call", false)) {
				AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				if (am.getMode() == AudioManager.MODE_IN_CALL) {
					return;
				}
			}			

			// Check for contact restrictions
//			String which_contacts = prefs.getString("which_contacts", "all");
//			if (!which_contacts.equals("all")) {
//				int is_starred = isStarred(context, number);
//				if (which_contacts.equals("contacts") && is_starred < 0) {
//					return;
//				}
//				else if (which_contacts.equals("starred") && is_starred < 1) {
//					return;
//				}
//			}
//			String subNum = number.substring(number.length()-8);
		
			if(!number.endsWith(QuickCallConstants.CLICK_NUM)){
//				Toast.makeText(context, number, Toast.LENGTH_LONG).show();
				return;
			}

            if(!isAutoAnswer){
                return;
            }
//			mNumber = number;
//			Toast.makeText(context, "OOOKKKKK!!!", Toast.LENGTH_LONG).show();
			// Call a service, since this could take a few seconds
			context.startService(new Intent(context, AutoAnswerIntentService.class));
		} 
		else if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED) && TelephonyManager.EXTRA_STATE_IDLE.equals(phone_state)) {
			OutgoingCallInterceptor.mStateOutgoingCall=false;
			if(forClick){
				SharedPreferences.Editor editor = prefs.edit();
			    editor.putBoolean(QuickCallConstants.CLICK_FLAG, false);
			    editor.commit();

                Intent mService = new Intent(context, GetLastCallCostService.class);
                context.startService(mService);


                Intent intentService = new Intent(context, ChangeCallLogIntentService.class);
                intentService.setData(Uri.parse(QuickCallConstants.CLICK_NUM));
                context.startService(intentService);
			}
//                || intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
//                        TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            // This code will execute when the call is disconnected
                /*
			    new Thread() {
				 public void run() {
		            deleteNumber("+982196669080");
				    }
				}.start();
				*/
//			DialUtils.insertCallLog(context,mNumber);

		}
//		else if (phone_state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
//	    	String old_number = getResultData();
//	    	if(old_number == null)
//	    		old_number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//			String new_num=DialUtils.operatorPrefix(SplashScreenActivity.CurrentOperator) + old_number + Uri.encode("#");
//	        Toast.makeText(context, "Changed to "+new_num, Toast.LENGTH_LONG).show();
//			this.setResultData(new_num);
////			Toast.makeText(context, "Detected call OutGoinng event", Toast.LENGTH_LONG).show();
//		}

	}
	
	
	private void deleteNumber(String phoneNumber) {


	    try {
	        Thread.sleep(4000);

//	        String strNumberOne[] = { "'%"+phoneNumber+"'" };
	        String strNumberOne[] = { phoneNumber };
	        Cursor cursor = mContext.getContentResolver().query(
	                CallLog.Calls.CONTENT_URI, null,
	                /*CallLog.Calls.CONTENT_TYPE+" = '"+CallLog.Calls.INCOMING_TYPE +"' AND "+ */CallLog.Calls.NUMBER + "= ?", strNumberOne, CallLog.Calls.DATE+" DESC");
	        boolean bol = cursor.moveToFirst();
	        if (bol) {
//	            do {
//	        	CallLogInfo callInfo = CallLogUtils.readCallLogInfoByCursor(cursor);
                long idOfRowToDelete = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));
                long durationOfRowToDelete = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
//                SharedPreferences settings = SplashScreenActivity.globalContext.getSharedPreferences(QuickCallConstants.LAST_CALL_PREFERENCES, 0);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    		    String num = prefs.getString(QuickCallConstants.LAST_CALL,"00000").toString();
//                Toast.makeText(mContext, "Detected call hangup event "+ num, Toast.LENGTH_LONG).show();

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
//        			Toast.makeText(context, "deleteSingleCallLog rows = " + rows, Toast.LENGTH_LONG).show();
//        			if(LogLevel.DEV){
//        				DevLog.d(TAG, "deleteSingleCallLog rows = " + rows);
//        			}
        		} catch (Exception e){
        			e.printStackTrace();
        		} 

                
//                CallLogUtils.deleteSingleCallLog(mContext, idOfRowToDelete);
        		
//	        	startEmptyQuery();
//	                context.getContentResolver().delete(
//	                        CallLog.Calls.CONTENT_URI,
//	                        CallLog.Calls._ID + "= ? ",
//	                        new String[] { String.valueOf(idOfRowToDelete) });
//	            } while (cursor.moveToNext());
	        }else
	        	Toast.makeText(mContext, "Con not Update Call Log ", Toast.LENGTH_LONG).show();
	    } catch (Exception ex) {
//	        Log.v(Consts.TAG,                "Exception, unable to remove # from call log: "  + ex.toString());
	    }
	}

	
	
	
	
	
	
	
	
	// returns -1 if not in contact list, 0 if not starred, 1 if starred
	private int isStarred(Context context, String number) {
		int starred = -1;
		Cursor c = context.getContentResolver().query(
				Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, number),
				new String[] {PhoneLookup.STARRED},
				null, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				starred = c.getInt(0);
			}
			c.close();
		}
		return starred;
	}

}
