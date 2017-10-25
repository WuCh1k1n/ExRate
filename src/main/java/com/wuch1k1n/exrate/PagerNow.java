package com.wuch1k1n.exrate;

import android.content.Context;
import android.view.View;

import com.wuch1k1n.exrate.ui.CustomEditText;

/**
 * Created by Administrator on 2017/10/13.
 */

public class PagerNow {

    private View mView;

    public PagerNow(Context context) {
        mView = View.inflate(context, R.layout.layout_now, null);

    }

    public View getView() {
        return mView;
    }
}
