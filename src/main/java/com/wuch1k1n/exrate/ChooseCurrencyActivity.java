package com.wuch1k1n.exrate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.wuch1k1n.exrate.model.Currency;
import com.wuch1k1n.exrate.util.CurrencyAdapter;
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

/**
 * Created by Administrator on 2017/10/15.
 */

public class ChooseCurrencyActivity extends AppCompatActivity {

    private static final String APPKEY = "af6aff0c22ed9ada35ee0d74f77b049b";
    private ListView currencyListView;
    private CurrencyAdapter currencyAdapter;
    private List<Currency> currencies = new ArrayList<Currency>();
    private List<Currency> dataList = new ArrayList<Currency>();
    private Button bt_back;
    private ProgressDialog progressDialog;
    private static final int BASE_CURRENCY = 0;
    private static final int TARGET_CURRENCY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_currency);

        currencyListView = (ListView) findViewById(R.id.currency_list_view);
        currencyAdapter = new CurrencyAdapter(ChooseCurrencyActivity.this, R.layout.currency_item, dataList);
        // 查询货币列表信息
        getCurrencyList();
        currencyListView.setAdapter(currencyAdapter);

        bt_back = (Button) findViewById(R.id.bt_back);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        currencyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Boolean flag = false;
                // 获取由哪个按钮跳转到该界面
                Intent intent = getIntent();
                int from = intent.getIntExtra("from", -1);

                if (from == TARGET_CURRENCY) {
                    // 是否已经添加过该货币
                    for (Currency currency : PagerExrate.targetCurrencies) {
                        if (currency.getCode().equals(currencies.get(position).getCode())) {
                            flag = true;
                            Toast.makeText(ChooseCurrencyActivity.this,
                                    "已添加该货币", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                if (!flag) {
                    // 将货币代号转换为对应的货币图标资源ID
                    String imageName;
                    if (currencies.get(position).getCode().equals("TRY")) {
                        // 解决土耳其货币代码TRY与关键字try冲突的问题
                        imageName = "_try";
                    } else {
                        imageName = currencies.get(position).getCode().toLowerCase();
                    }
                    Resources res = getResources();
                    int imageId = res.getIdentifier(imageName, "mipmap", "com.wuch1k1n.exrate");

                    String code = currencies.get(position).getCode();
                    String name = currencies.get(position).getName();

                    Intent intent1 = new Intent();
                    intent1.putExtra("image_id", imageId);
                    intent1.putExtra("currency_code", code);
                    intent1.putExtra("currency_name", name);
                    setResult(RESULT_OK, intent1);
                    finish();
                }

            }
        });
    }

    private void getCurrencyList() {
        currencies = DataSupport.findAll(Currency.class);
        if (currencies.size() > 0) {
            dataList.clear();
            for (Currency currency : currencies) {
                dataList.add(currency);
            }
            currencyAdapter.notifyDataSetChanged();
        } else {
            queryCurrencyList();
        }
    }

    private void queryCurrencyList() {
        showProgressDialog();
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
                        Toast.makeText(ChooseCurrencyActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
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
                            getCurrencyList();
                            Toast.makeText(ChooseCurrencyActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
