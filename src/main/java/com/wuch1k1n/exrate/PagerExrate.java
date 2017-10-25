package com.wuch1k1n.exrate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ListView;

import com.wuch1k1n.exrate.model.Currency;
import com.wuch1k1n.exrate.ui.CustomEditText;
import com.wuch1k1n.exrate.util.ExrateAdapter;
import com.wuch1k1n.exrate.util.HttpUtil;
import com.wuch1k1n.exrate.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/10/13.
 */

public class PagerExrate {

    private static final String APPKEY = "af6aff0c22ed9ada35ee0d74f77b049b";
    private AppCompatActivity mActivity;
    private View mView;
    public CustomEditText et_exrate;
    private FloatingActionButton fab;
    private Currency baseCurrency;
    public static List<Currency> targetCurrencies = new ArrayList<Currency>();
    private ListView lv_target_currency;
    private ExrateAdapter exrateAdapter;
    private SharedPreferences pref;
    private static final int BASE_CURRENCY = 0;
    private static final int TARGET_CURRENCY = 1;

    public PagerExrate(Context context) {
        mActivity = (AppCompatActivity) context;
        mView = View.inflate(mActivity, R.layout.layout_exrate, null);
        et_exrate = (CustomEditText) mView.findViewById(R.id.et_exrate);
        // 取消输入框自动获得焦点
        et_exrate.setFocusable(true);
        et_exrate.setFocusableInTouchMode(true);
        fab = (FloatingActionButton) mView.findViewById(R.id.fab_add_currency);

        lv_target_currency = (ListView) mView.findViewById(R.id.lv_target_currency);
        exrateAdapter = new ExrateAdapter(mActivity, R.layout.exrate_item, targetCurrencies);
        lv_target_currency.setAdapter(exrateAdapter);

        pref = mActivity.getSharedPreferences("config", mActivity.MODE_PRIVATE);
        // 应用是否第一次启动
        Boolean started = pref.getBoolean("started", false);
        if (!started) {
            //Toast.makeText(mActivity, "首次启动", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("started", true);

            // 默认基准货币为人民币，金额为100
            et_exrate.setCurrencyIcon(R.mipmap.cny);
            et_exrate.setCurrencyName("人民币");
            et_exrate.setInputNum("100");
            et_exrate.setSelection(3);
            baseCurrency = new Currency("人民币", "CNY");
            baseCurrency.setExrate(getCurrencyExrate("CNY"));
            editor.putString("et_code", "CNY");
            editor.putString("et_name", "人民币");
            editor.commit();

            // 默认目标货币为美元、欧元、港币
            addTargetCurrency("美元", "USD");
            addTargetCurrency("欧元", "EUR");
            addTargetCurrency("港币", "HKD");

            for (Currency currency : targetCurrencies) {
                currency.setAfterChange(refreshAfterChange(currency));
            }
            exrateAdapter.notifyDataSetChanged();
        } else {
            // 将货币代号转换为对应的货币图标资源ID
            String code = pref.getString("et_code", "");
            String name = pref.getString("et_name", "");
            String imageName;
            if (code.equals("TRY")) {
                // 解决土耳其货币代码TRY与关键字try冲突的问题
                imageName = "_try";
            } else {
                imageName = code.toLowerCase();
            }
            Resources res = mActivity.getResources();
            int imageId = res.getIdentifier(imageName, "mipmap", "com.wuch1k1n.exrate");
            // 初始化输入框
            et_exrate.setCurrencyIcon(imageId);
            et_exrate.setCurrencyName(name);
            et_exrate.setInputNum("100");
            et_exrate.setSelection(3);
            baseCurrency = new Currency("人民币", code);
            baseCurrency.setExrate(getCurrencyExrate(code));
            // 将数据库保存的选中货币赋值给目标货币
            List<Currency> returnList = DataSupport.where("selected = ?", "1").find(Currency.class);
            targetCurrencies.clear();
            for (Currency currency : returnList) {
                targetCurrencies.add(currency);
            }
            for (Currency currency : targetCurrencies) {
                currency.setAfterChange(refreshAfterChange(currency));
            }
            exrateAdapter.notifyDataSetChanged();
        }

        et_exrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ChooseCurrencyActivity.class);
                // CHOOSE_BASE_CURRENCY==1
                intent.putExtra("from", BASE_CURRENCY);
                mActivity.startActivityForResult(intent, 1);
            }
        });

        et_exrate.setEditTextOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_exrate.setEditTextColor(mActivity.getResources().getColor(R.color.pressed_red));
                    et_exrate.setInputNum("");
                } else {
                    et_exrate.setEditTextColor(mActivity.getResources().getColor(R.color.black));
                }
            }
        });

        // 为输入框设置文字变化监听器
        et_exrate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 输入框文本不为空时才刷新
                if (s.length() > 0) {
                    for (Currency currency : targetCurrencies) {
                        currency.setAfterChange(refreshAfterChange(currency));
                    }
                    exrateAdapter.notifyDataSetChanged();
                }
            }
        });

        // 添加长按listview弹出选择菜单
        lv_target_currency.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, "货币走势");
                menu.add(0, 1, 0, "删除货币");
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ChooseCurrencyActivity.class);
                // CHOOSE_TARGET_CURRENCY==2
                intent.putExtra("from", TARGET_CURRENCY);
                mActivity.startActivityForResult(intent, 2);
            }
        });
    }

    public View getView() {
        return mView;
    }

    public void clearEditTextFocus() {
        et_exrate.clearEditTextFocus();
    }

    public void refreshEditText(int drawable, String code, String name) {
        et_exrate.setCurrencyIcon(drawable);
        et_exrate.setCurrencyName(name);
        baseCurrency = new Currency(name, code);
        baseCurrency.setExrate(getCurrencyExrate(code));
        Log.d("Test", code + ":" + baseCurrency.getExrate());
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("et_code", code);
        editor.putString("et_name", name);
        editor.commit();

        for (Currency currency : targetCurrencies) {
            currency.setAfterChange(refreshAfterChange(currency));
        }
        exrateAdapter.notifyDataSetChanged();
    }

    public double refreshAfterChange(Currency target) {
        // 基础货币兑换目标货币的汇率 = 基础货币兑换美元的汇率除 / 目标货币兑换美元的汇率
        double result = (et_exrate.getInputNum() * baseCurrency.getExrate() / target.getExrate());
        // 把结果四舍五入并返回
        BigDecimal bd = new BigDecimal(result);
        double afterChange = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return afterChange;
    }

    public void addTargetCurrency(String name, String code) {
        double exrate = getCurrencyExrate(code);
        Currency currency = new Currency(name, code);
        currency.setSelected(true);
        currency.setExrate(exrate);
        currency.setAfterChange(refreshAfterChange(currency));
        targetCurrencies.add(currency);
        exrateAdapter.notifyDataSetChanged();

        // 保存被用户选中的货币
        currency = new Currency();
        currency.setSelected(true);
        currency.updateAll("code = ?", code);
    }

    // 尝试从本地数据库获取该货币兑换美元的汇率
    public double getCurrencyExrate(String code) {
        double exrate = DataSupport.where("code = ?", code).find(Currency.class).get(0).getExrate();
        if (exrate != -1) {
            return exrate;
        } else {
            // 若本地数据库无该货币汇率，则到服务器上查询
            if (code.equals("USD")) {
                exrate = 1;
                Currency currency = new Currency();
                currency.setExrate(exrate);
                currency.updateAll("code = ?", code);
            } else {
                queryCurrencyExrate(code);
            }
            while (exrate == -1) {
                exrate = DataSupport.where("code = ?", code).find(Currency.class).get(0).getExrate();
            }
            return exrate;
        }
    }

    // 查询该货币兑换美元的汇率
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

    // 删除目标货币
    public void deleteTargetCurrency(int position) {
        String code = targetCurrencies.get(position).getCode();
        Currency currency = new Currency();
        currency.setToDefault("selected");
        currency.updateAll("code = ?", code);

        targetCurrencies.remove(position);
        exrateAdapter.notifyDataSetChanged();
    }
}
