package com.wuch1k1n.exrate.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wuch1k1n.exrate.R;

/**
 * Created by Administrator on 2017/10/5.
 */

public class CustomBar extends RelativeLayout {

    private ImageView leftIv;
    private TextView tv;
    private ImageView rightIv;

    public CustomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.custom_bar, this, true);
        leftIv = (ImageView) findViewById(R.id.bar_left_img);
        tv = (TextView) findViewById(R.id.bar_text);
        rightIv = (ImageView) findViewById(R.id.bar_right_img);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomBar);
        // 设置左边图标
        int leftImgDrawable = attributes.getResourceId(R.styleable.CustomBar_left_img, -1);
        if (leftImgDrawable != -1) {
            leftIv.setBackgroundResource(leftImgDrawable);
        }
        // 设置右边图标
        int rightImgDrawable = attributes.getResourceId(R.styleable.CustomBar_right_img, -1);
        if (rightImgDrawable != -1) {
            rightIv.setBackgroundResource(rightImgDrawable);
        }
        // 设置按钮文字
        String text = attributes.getString(R.styleable.CustomBar_text);
        if (!TextUtils.isEmpty(text)) {
            tv.setText(text);
        }
    }

}
