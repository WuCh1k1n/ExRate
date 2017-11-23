package com.wuch1k1n.exrate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.Arrays;
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
    private SharedPreferences pref;
    private List<News> resultList;
    private SwipeRefreshLayout swipeRefresh;

    public PagerNews(Context context) {
        mActivity = (Activity) context;
        mView = View.inflate(context, R.layout.layout_news, null);
        pref = PreferenceManager.getDefaultSharedPreferences(mActivity);

        // 下拉刷新
        swipeRefresh = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utility.isNetworkConnected(mActivity)) {
                    queryNews();
                } else {
                    Log.d("Test", "网络不可用");
                    Toast.makeText(mActivity, "网络不可用", Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);
                }
            }
        });

        newsAdapter = new NewsAdapter(context, R.layout.news_item, newsList);
        ListView listView = (ListView) mView.findViewById(R.id.lv_news);
        listView.setAdapter(newsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 判断该新闻条目是否已读
                if (!newsList.get(position).getRead()) {
                    newsList.get(position).setRead(true);
                    newsAdapter.notifyDataSetChanged();
                    // 已读新闻条目id
                    String ids = pref.getString("news_id", "");
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("news_id", ids + "#" + newsList.get(position).getId());
                    editor.commit();
                }
                // 跳转到新闻详情页面
                String webUrl = newsList.get(position).getWebUrl();
                Intent intent = new Intent(mActivity, NewsActivity.class);
                intent.putExtra("web_url", webUrl);
                mActivity.startActivity(intent);
            }
        });

        if (Utility.isNetworkConnected(mActivity)) {
            // 获取货币列表
            queryNews();
        } else {
            String pref_news = pref.getString("news", "");
            if (!pref_news.isEmpty()) {
                String ids = pref.getString("news_id", "");
                String[] newsReadId = ids.split("#");
                resultList = Utility.handleNewsListResponse(pref_news);
                for (News news : resultList) {
                    if (Arrays.asList(newsReadId).contains(news.getId())) {
                        news.setRead(true);
                    }
                    newsList.add(news);
                }
                newsAdapter.notifyDataSetChanged();
            }
        }
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
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("news", responseText);
                editor.commit();
                String ids = pref.getString("news_id", "");
                String[] newsReadId = ids.split("#");
                resultList = Utility.handleNewsListResponse(responseText);
                newsList.clear();
                for (News news : resultList) {
                    if (Arrays.asList(newsReadId).contains(news.getId())) {
                        news.setRead(true);
                    }
                    newsList.add(news);
                }
                // 通过runOnUiThread()方法回到主线程处理逻辑
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newsAdapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                        //Toast.makeText(mActivity, "加载成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
