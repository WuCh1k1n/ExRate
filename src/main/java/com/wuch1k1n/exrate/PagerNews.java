package com.wuch1k1n.exrate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.wuch1k1n.exrate.model.News;
import com.wuch1k1n.exrate.util.HttpUtil;
import com.wuch1k1n.exrate.util.NewsAdapter;
import com.wuch1k1n.exrate.util.Utility;

import java.io.IOException;
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

public class PagerNews {

    private static final String APPKEY = "599f73a623451cdc182ae7f2287c21a7";
    private Activity mActivity;
    private View mView;
    private NewsAdapter newsAdapter;
    private List<News> newsList = new ArrayList<>();
    private ProgressDialog progressDialog;

    public PagerNews(Context context) {
        mActivity = (Activity) context;
        mView = View.inflate(context, R.layout.layout_news, null);

        newsAdapter = new NewsAdapter(context, R.layout.news_item, newsList);
        ListView listView = (ListView) mView.findViewById(R.id.lv_news);
        listView.setAdapter(newsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String webUrl = newsList.get(position).getWebUrl();
                Intent intent = new Intent(mActivity, NewsActivity.class);
                intent.putExtra("web_url", webUrl);
                mActivity.startActivity(intent);
            }
        });

        queryNews();
    }

    public View getView() {
        return mView;
    }

    private void queryNews() {
        String address = "http://v.juhe.cn/toutiao/index";// 请求接口地址
        Map params = new HashMap();// 请求参数
        params.put("type", "caijing");// 新闻类型
        params.put("key", APPKEY);// 应用APPKEY(应用详细页查询)
        String url = address + "?" + HttpUtil.urlencode(params);

        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(mActivity, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                List<News> resultList = Utility.handleNewsListResponse(responseText);
                for (News news : resultList) {
                    newsList.add(news);
                }
                Log.d("Test", newsList.get(0).getId());
                // 通过runOnUiThread()方法回到主线程处理逻辑
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newsAdapter.notifyDataSetChanged();
                        //Toast.makeText(mActivity, "加载成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mActivity);
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
