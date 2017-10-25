package com.wuch1k1n.exrate;

import android.content.Context;
import android.view.View;

/**
 * Created by Administrator on 2017/10/13.
 */

public class PagerNews {

    private View mView;

    public PagerNews(Context context) {
        mView = View.inflate(context, R.layout.layout_news, null);
    }

    public View getView() {
        return mView;
    }
}
