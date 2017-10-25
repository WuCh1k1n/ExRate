package com.wuch1k1n.exrate;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;

import com.wuch1k1n.exrate.ui.CustomBottomButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<View> viewList;
    private PagerExrate pagerExrate;
    private PagerNow pagerNow;
    private PagerNews pagerNews;
    private PagerMe pagerMe;

    private ViewPager viewPager;

    private List<CustomBottomButton> buttomList;
    private CustomBottomButton bt_exrate;
    private CustomBottomButton bt_now;
    private CustomBottomButton bt_news;
    private CustomBottomButton bt_me;

    private int[] buttonNormalIcons = {R.mipmap.ic_exrate_normal, R.mipmap.ic_now_normal,
            R.mipmap.ic_news_normal, R.mipmap.ic_me_normal};
    private int[] buttonPressedIcons = {R.mipmap.ic_exrate_pressed, R.mipmap.ic_now_pressed,
            R.mipmap.ic_news_pressed, R.mipmap.ic_me_pressed};

    private static final int CHOOSE_BASE_CURRENCY = 1;
    private static final int CHOOSE_TARGET_CURRENCY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pagerExrate = new PagerExrate(this);
        pagerNow = new PagerNow(this);
        pagerNews = new PagerNews(this);
        pagerMe = new PagerMe(this);

        viewList = new ArrayList<View>();
        viewList.add(pagerExrate.getView());
        viewList.add(pagerNow.getView());
        viewList.add(pagerNews.getView());
        viewList.add(pagerMe.getView());

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new mPagerAdaper());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setButtonsState(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bt_exrate = (CustomBottomButton) findViewById(R.id.bt_exrate);
        bt_now = (CustomBottomButton) findViewById(R.id.bt_now);
        bt_news = (CustomBottomButton) findViewById(R.id.bt_news);
        bt_me = (CustomBottomButton) findViewById(R.id.bt_me);
        buttomList = new ArrayList<CustomBottomButton>();
        buttomList.add(bt_exrate);
        buttomList.add(bt_now);
        buttomList.add(bt_news);
        buttomList.add(bt_me);

        // 初始化底部按钮选中状态
        bt_exrate.setButtonIcon(R.mipmap.ic_exrate_pressed);
        bt_exrate.setButtonTextColor(getResources().getColor(R.color.pressed_red));

        // 为底部按钮设置事件监听器
        for (int i = 0; i < buttomList.size(); i++) {
            buttomList.get(i).setTag(i);
            buttomList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (Integer) v.getTag();
                    viewPager.setCurrentItem(index);
                    setButtonsState(index);
                }
            });
        }
    }

    private void setButtonsState(int selectedIndex) {
        for (int i = 0; i < buttomList.size(); i++) {
            if (i == selectedIndex) {
                buttomList.get(i).setButtonIcon(buttonPressedIcons[i]);
                buttomList.get(i).setButtonTextColor(getResources().getColor(R.color.pressed_red));
            } else {
                buttomList.get(i).setButtonIcon(buttonNormalIcons[i]);
                buttomList.get(i).setButtonTextColor(getResources().getColor(R.color.normal_grey));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_BASE_CURRENCY:
                if (resultCode == RESULT_OK) {
                    int imageId = data.getIntExtra("image_id", -1);
                    String currencyCode = data.getStringExtra("currency_code");
                    String currencyName = data.getStringExtra("currency_name");
                    if (imageId != -1) {
                        pagerExrate.refreshEditText(imageId, currencyCode, currencyName);
                    }
                }
                break;
            case CHOOSE_TARGET_CURRENCY:
                if (resultCode == RESULT_OK) {
                    String currencyCode = data.getStringExtra("currency_code");
                    String currencyName = data.getStringExtra("currency_name");
                    pagerExrate.addTargetCurrency(currencyName, currencyCode);
                }
                break;
            default:
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
                pagerExrate.clearEditTextFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    //给菜单项添加事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //info.id得到listview中选择的条目绑定的id
        String id = String.valueOf(info.id);
        switch (item.getItemId()) {
            // 货币走势
            case 0:
                return true;
            // 删除货币
            case 1:
                System.out.println("删除" + info.id);
                pagerExrate.deleteTargetCurrency(Integer.parseInt(id));
            default:
                return super.onContextItemSelected(item);
        }
    }

    // 多种隐藏软件盘方法的其中一种
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    class mPagerAdaper extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}
