package org.telegram.kleegram;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.volley.CacheDispatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SettingsFragment extends Activity {

    private static final String TAG = "[EASIIO]SettingsFragment";

    private View mSuggestionsView;
    private View mRateAppView;
    private ImageButton mSipax;
    private Button mBalance;
    private Button mSupport;
    private Button mCharge;
    private Button mGuid;
    private static CheckBox mGlobal;

    private static CheckBox isAutoAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_fragment_layout);

        mSuggestionsView = findViewById(R.id.settings_suggestions_view);
        mRateAppView = findViewById(R.id.settings_rate_app_view);
        mSipax = (ImageButton) findViewById(R.id.btn_sipax);
        mBalance = (Button) findViewById(R.id.btn_balance);
        mSupport = (Button) findViewById(R.id.btn_support);
        mCharge = (Button) findViewById(R.id.btn_charg);
        mGuid = (Button) findViewById(R.id.btn_guid);
        mGlobal = (CheckBox) findViewById(R.id.globalCallback);
        isAutoAnswer = (CheckBox) findViewById(R.id.isAutoAnswer);
//		addItemsOnOperators2(view);
        listenerButton();
//    	SharedPreferences settings = SplashScreenActivity.globalContext.getSharedPreferences(QuickCallConstants.IS_GLOBAL_PREFERENCES, 0);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isGlobal = prefs.getBoolean(QuickCallConstants.IS_GLOBAL, false);
        mGlobal.setChecked(isGlobal);

        boolean isAuto = prefs.getBoolean(QuickCallConstants.IS_AUTO_ANSWER, true);
        isAutoAnswer.setChecked(isAuto);

    }

    @Override
    public void onResume() {
        registerReceiver(broadcastReceiver, new IntentFilter(NotificationClick.BROADCAST_ACTION));
        super.onResume();
    }

    @Override
    public void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    private void listenerButton() {

        mRateAppView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Activity activity = SettingsFragment.this;
                    if (activity == null || activity.isFinishing()) {
                        return;
                    }
                    String packetName = activity.getPackageName();
                    Uri uri = Uri.parse("market://details?id=" + packetName);
                    Intent intent_market = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent_market);
                } catch (Exception e) {
                }
            }
        });
        mSipax.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Activity activity = SettingsFragment.this;
                    if (activity == null || activity.isFinishing()) {
//						if(LogLevel.MARKET){
//							MarketLog.e(TAG, "activity is null or finishing.");
//						}
                        return;
                    }
                    Uri uri = Uri.parse("http://www.sipax.net");
                    Intent intent_market = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent_market);
                } catch (Exception e) {
//					if(LogLevel.MARKET){
//						MarketLog.e(TAG, "rate app failed: " + e.toString());
//					}
                }
            }
        });
        mBalance.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                balanceClick();
            }
        });
        mSupport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//				mPopupWindow.dismiss();
//				if(LogLevel.DEV){
//					DevLog.d(TAG, "User click rate app view");
//				}

                try {
                    Activity activity = SettingsFragment.this;
                    if (activity.isFinishing()) {
                        return;
                    }
                    DialUtils.callContactNumber(activity, "02196860096");
                } catch (Exception e) {
                }
            }
        });
        mCharge.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                initDialogWindow();
            }
        });

//        mGuid.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), GuideScreenActivity.class);
//                intent.putExtra("fromSetting",true);
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });

        mGlobal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
//				OutgoingCallInterceptor.isGlobal(arg1);
//				SharedPreferences settings = SplashScreenActivity.globalContext.getSharedPreferences(QuickCallConstants.IS_GLOBAL_PREFERENCES, 0);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsFragment.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(QuickCallConstants.IS_GLOBAL, arg1);
                // Commit the edits!
                editor.commit();

                //update notification // FIXME: 1/22/2016 
//                MyNotification.startNotification(SettingsFragment.this);
            }
        });

        isAutoAnswer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsFragment.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(QuickCallConstants.IS_AUTO_ANSWER, arg1);
                // Commit the edits!
                editor.commit();
            }
        });
    }

    //call from receiver to update check box
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    private void updateUI(Intent intent) {
        boolean isChecked = intent.getBooleanExtra("isChecked", false);
        if (mGlobal != null)
            mGlobal.setChecked(isChecked);
    }

    private void balanceClick() {

        String url = "http://96868696.ir/customer/json.php";
//		RequestParams params = new RequestParams();
//		params.put("action" , "credit");
//		params.put("from", UserConfig.getCurrentUser().phone.replace("98", "0"));
//		Log.i("params" , params.toString());
//		ApplicationLoader.client.post(this, url, params, new AsyncHttpResponseHandler() {
//			@Override
//			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//				try {
//					JSONObject response = new JSONObject(new String((responseBody)));
//					Log.i("resp" , response.toString());
//					boolean success = response.getBoolean("success");
//					if (success) {
//						if (response.has("credit")) {
//							final String credit = response.getString("credit");
//							ApplicationLoader.applicationHandler.postDelayed(new Runnable() {
//								@Override
//								public void run() {
//									Toast.makeText(SettingsFragment.this ,
//											"موجودی شما"+ credit + "تومان است" , Toast.LENGTH_LONG).show();
//								}
//							}, 10);
//						} else {
//
//						}
//					} else {
//						Toast.makeText(SettingsFragment.this, "شماره شما در سیستم ثبت نشده است . ابتدا نسبت به فعال سازی اقدام نمایید",
//								Toast.LENGTH_LONG).show();
//					}
//				}catch (Exception e) {
//					e.printStackTrace();
//				}
//
//			}
//
//			@Override
//			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//				Toast.makeText(SettingsFragment.this, "خطا . بعدا تلاش نمایید", Toast.LENGTH_LONG).show();
//
//			}
//		});

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();

                    MediaType mediaType = MediaType.parse("application/json");
                    String from = "+" + UserConfig.getCurrentUser().phone;
                    String content =  "{\"action\":\"credit\",\"from\":\""+from.replace("+98", "0")+"\"}";
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

    private void parseResponse(final String responseBody) {
        ApplicationLoader.applicationHandler.post(new Runnable() {
            @Override
            public void run() {

                try {
                    JSONObject response = new JSONObject(new String((responseBody)));
                    Log.i("resp", response.toString());
                    boolean success = response.getBoolean("success");
                    if (success) {
                        if (response.has("credit")) {
                            final String credit = response.getString("credit");
                            ApplicationLoader.applicationHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SettingsFragment.this,
                                            "موجودی شما" + credit + " تومان است ", Toast.LENGTH_LONG).show();
                                }
                            }, 10);
                        } else {
                            Toast.makeText(SettingsFragment.this, "شماره شما در سیستم ثبت نشده است.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(SettingsFragment.this, "شماره شما در سیستم ثبت نشده است . ابتدا نسبت به فعال سازی اقدام نمایید",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void initDialogWindow() {
        final Activity act = SettingsFragment.this;
        View v = LayoutInflater.from(act).inflate(R.layout.dialog_buy_credit, null);
        QuickCallAlertDialog.Builder builder = new QuickCallAlertDialog.Builder(act);
        //builder.setIcon(R.drawable.icon_dialog_title_for_menu);
        //builder.setTitle(getString(R.string.charg_title_text));
        builder.setContentView(v);

        final QuickCallAlertDialog dialog = builder.create();
        Button yek = (Button) dialog.findViewById(R.id.yek);
        Button dow = (Button) dialog.findViewById(R.id.dow);
        Button panj = (Button) dialog.findViewById(R.id.panj);
        Button dah = (Button) dialog.findViewById(R.id.dah);
        Button bist = (Button) dialog.findViewById(R.id.bist);


        yek.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog.dismiss();
//				DialUtils.callNumber(act, "3*1");
                DialUtils.buyCredit("10", UserConfig.getCurrentUser().phone, SettingsFragment.this);
            }
        });


        dow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
//				DialUtils.callNumber(act, "3*2");
                DialUtils.buyCredit("20", UserConfig.getCurrentUser().phone, SettingsFragment.this);
//				createNewQuickCall(mContactInfo.contactId, mContactInfo.displayName);
            }
        });

        panj.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
//				DialUtils.callNumber(act, "3*3");
                DialUtils.buyCredit("50", UserConfig.getCurrentUser().phone, SettingsFragment.this);
            }

        });

        dah.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
//				DialUtils.callNumber(act, "3*4");
                DialUtils.buyCredit("100", UserConfig.getCurrentUser().phone, SettingsFragment.this);
            }
        });

        bist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog.dismiss();
//				DialUtils.callNumber(act, "3*5");
                DialUtils.buyCredit("200", UserConfig.getCurrentUser().phone, SettingsFragment.this);
            }
        });


        dialog.show();
        //DialogUtils.setDialogWidth(getActivity(), dialog);
        DialogUtils.setDialogWidthHeight(SettingsFragment.this, dialog);
    }

}
