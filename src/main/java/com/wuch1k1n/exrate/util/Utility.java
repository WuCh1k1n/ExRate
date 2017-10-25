package com.wuch1k1n.exrate.util;

import android.text.TextUtils;

import com.wuch1k1n.exrate.model.Currency;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/10/6.
 */

public class Utility {

    public static boolean handleCurrencyListResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getInt("error_code") == 0) {
                    JSONObject resultObject = jsonObject.getJSONObject("result");
                    JSONArray list = resultObject.getJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject tempObject = list.getJSONObject(i);
                        Currency currency = new Currency(tempObject.getString("name")
                                , tempObject.getString("code"));
                        currency.save();
                    }
                    return true;
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCurrencyExrateResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getInt("error_code") == 0) {
                    JSONArray resultArray = jsonObject.getJSONArray("result");
                    JSONObject resultObject = resultArray.getJSONObject(0);
                    String code = resultObject.getString("currencyF");
                    double exrate = resultObject.getDouble("exchange");
                    Currency currency = new Currency();
                    currency.setExrate(exrate);
                    currency.updateAll("code = ?", code);
                    return true;
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }
}
