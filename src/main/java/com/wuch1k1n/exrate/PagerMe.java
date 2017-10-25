package com.wuch1k1n.exrate;

import android.content.Context;
import android.view.View;

/**
 * Created by Administrator on 2017/10/13.
 */

public class PagerMe {

    private View mView;

    public PagerMe(Context context) {
        mView = View.inflate(context, R.layout.layout_me, null);
    }

    public View getView() {
        return mView;
    }
}
