package com.wuch1k1n.exrate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.wuch1k1n.exrate.model.Currency;
import com.wuch1k1n.exrate.util.HttpUtil;
import com.wuch1k1n.exrate.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {

    private static final String APPKEY = "af6aff0c22ed9ada35ee0d74f77b049b";
    private List<Currency> currencies = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        // 检查网络是否可用
        if (isNetworkConnected(this)) {
            // 获取货币列表
            getCurrencyList();
        } else {
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
            // 3s后跳转到主页面
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
        }
    }

    private void getCurrencyList() {
        currencies = DataSupport.findAll(Currency.class);
        if (currencies.size() > 0) {
            for(Currency currency:currencies){
                // 更新货币兑换美元汇率数据，受API请求次数限制暂不开启
                // queryCurrencyExrate(currency.getCode());
            }
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // 向服务器请求货币列表
            queryCurrencyList();
        }
    }

    private void queryCurrencyList() {
        showProgressDialog("正在加载...");
        String address = "http://op.juhe.cn/onebox/exchange/list";// 请求接口地址
        Map params = new HashMap();// 请求参数
        params.put("key", APPKEY);// 应用APPKEY(应用详细页查询)
        String url = address + "?" + HttpUtil.urlencode(params);

        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(SplashActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                result = Utility.handleCurrencyListResponse(responseText);
                if (result) {
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(SplashActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
                            getCurrencyList();
                        }
                    });
                }
            }
        });
    }

    public void queryCurrencyExrate(final String code) {
        String address = "http://op.juhe.cn/onebox/exchange/currency";
        Map params = new HashMap();// 请求参数
        params.put("key", APPKEY);
        params.put("from", code);
        params.put("to", "USD");
        String url = address + "?" + HttpUtil.urlencode(params);

        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Utility.handleCurrencyExrateResponse(responseText);
            }
        });
    }

    private void showProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(msg);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
