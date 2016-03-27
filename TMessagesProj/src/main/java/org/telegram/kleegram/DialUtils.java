package org.telegram.kleegram;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class DialUtils {

    //	public static String mNumber="";
    private static final String TAG = "[ZHUANG]DialUtils";

    public static void sendMessage(Activity activity, String number) {

        try {
            Uri uri = Uri.parse("smsto:" + number);
            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
            activity.startActivity(it);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static String msisdn;
    public static void callWithInternet(final String input) {
        TLRPC.User user = MessagesController.getInstance().getUser(UserConfig.getClientUserId());
        msisdn = PhoneFormat.getInstance().format("+" + user.phone);
        msisdn = msisdn.replace(" ", "");
        msisdn = msisdn.replace("+98", "0");
        Toast.makeText(ApplicationLoader.applicationContext, ApplicationLoader.applicationContext.getResources().getString(R.string.callingWithKleegram)
                , Toast.LENGTH_LONG).show();







        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();

                    MediaType mediaType = MediaType.parse("application/json");
                    String content =  "{\"action\":\"call\",\"from\":\""+msisdn
                            +"\",\"dest\":\""+input+"\"}";
                    Log.i("data", content);
                    RequestBody body = RequestBody.create(mediaType, content);
                    Request request = new Request.Builder()
                            .url("http://96868696.ir/customer/json.php")
                            .post(body)
                            .addHeader("content-type", "application/json")
                            .addHeader("cache-control", "no-cache")
                            .build();

                    Response response = client.newCall(request).execute();
                    parseResponse(response.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();












    }

    public static void parseResponse(String responseBody) {
        try {
            final JSONObject response = new JSONObject(new String(responseBody));
            Log.i("resp", response.toString());
            if (!response.getBoolean("success")) {
                final String message = response.getString("resp");
                ApplicationLoader.applicationHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ApplicationLoader.applicationContext,
                                message, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String operatorPrefix(String name) {
        String selected = QuickCallConstants.USSD_NUM;
//		switch (name) {
//		case "Irancell":
//			selected="*2050*7*";
//			break;
//
//		case "Hamrahe Aval":
//			selected="*2051*7*";
//			break;
//		case "Rightel":
//			selected="*2050*7*";
//			break;
//		default:
//			break;
//		} 
        return selected;
    }

    // ********* KLEEGRAM ***********//
    public static void callContactNumber(Context activity, String number) {
        if (ApplicationLoader.check_net(activity, false)) {
            callWithInternet(number);
        } else {
//            callNumber(activity, number);
            Toast.makeText(ApplicationLoader.applicationContext, "تماس با کلیگرام به علت عدم اتصال به اینترنت میسر نمی باشد."
                    , Toast.LENGTH_LONG).show();
        }
    }

    public static void buyCredit(String amount, String myNumber, Activity activity) {
        String url = "http://www.96868696.ir/apppay/pay.php?amount=" + amount + "&number=" +
                myNumber + "&id=0";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        activity.startActivity(i);
    }

    public static void callNumber(Context activity, String number) {

        try {
            Uri uri1;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(QuickCallConstants.LAST_CALL, number);
            boolean isGlobal = prefs.getBoolean(QuickCallConstants.IS_GLOBAL, false);

            if (isGlobal)
                uri1 = Uri.parse("tel:" + number);
            else
                uri1 = Uri.parse("tel:" + operatorPrefix(ApplicationLoader.CurrentOperator) + number + Uri.encode("#"));
            editor.putBoolean(QuickCallConstants.CLICK_FLAG, true);
            editor.commit();
            Intent intent = new Intent(Intent.ACTION_CALL, uri1);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertCallLog(Activity context, String number) {
        ContentValues values = new ContentValues();
        values.put(CallLog.Calls.NUMBER, number);
        values.put(CallLog.Calls.DATE, System.currentTimeMillis());
        values.put(CallLog.Calls.DURATION, 0);
        values.put(CallLog.Calls.TYPE, CallLog.Calls.OUTGOING_TYPE);
        values.put(CallLog.Calls.NEW, 1);
        values.put(CallLog.Calls.CACHED_NAME, "");
        values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
        values.put(CallLog.Calls.CACHED_NUMBER_LABEL, "");
        context.getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
    }
}
