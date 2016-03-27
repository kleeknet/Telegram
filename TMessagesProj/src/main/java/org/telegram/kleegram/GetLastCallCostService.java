package org.telegram.kleegram;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;


import org.json.JSONException;
import org.json.JSONObject;

public class GetLastCallCostService extends IntentService {


    static final public String TAG = GetLastCallCostService.class.getSimpleName();
    public GetLastCallCostService() {
        super(TAG);
    }
    public GetLastCallCostService(String name) {
        super(name);
    }

    Context mContext;
    @Override
		protected void onHandleIntent(Intent intent) {
			mContext = getBaseContext();
        JSONObject object = new JSONObject();
        TelephonyManager phn_mngr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String PhnNo= phn_mngr.getLine1Number();
        JSONObject result = null;
        try {
            object.put("action","last");
            object.put("from",PhnNo);
            JsonParser parser = new JsonParser();
            result = parser.makeHttpRequest("http://click.exxonet.com/customer/json.php","POST",object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if(result != null && result.getString("succes").equalsIgnoreCase("true")){
                MyNotification.notificationForLastCallCost(mContext, result.getString("last"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

		}
}
