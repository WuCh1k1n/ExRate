package com.wuch1k1n.exrate.util;

import android.text.TextUtils;

import com.wuch1k1n.exrate.model.Currency;
import com.wuch1k1n.exrate.model.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    public static List<News> handleNewsListResponse(String response) {
        List<News> newsList = new ArrayList<>();
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getInt("error_code") == 0) {
                    JSONObject resultObject = jsonObject.getJSONObject("result");
                    JSONArray dataArray = resultObject.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject tempObject = dataArray.getJSONObject(i);
                        News news=new News();
                        news.setId(tempObject.getString("uniquekey"));
                        news.setTitle(tempObject.getString("title"));
                        news.setWebUrl(tempObject.getString("url"));
                        news.setImageUrl(tempObject.getString("thumbnail_pic_s"));
                        newsList.add(news);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return newsList;
    }
}
