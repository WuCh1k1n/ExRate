package com.wuch1k1n.exrate.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wuch1k1n.exrate.R;

/**
 * Created by Administrator on 2017/10/5.
 */

public class CustomBottomButton extends RelativeLayout {

    private Button buttonIcon;
    private TextView buttonText;

    public CustomBottomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.custom_bottom_button, this, true);
        buttonIcon = (Button) findViewById(R.id.button_icon);
        buttonText = (TextView) findViewById(R.id.button_text);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomBottomButton);
        // 设置按钮图标
        int buttonIconDrawable = attributes.getResourceId(R.styleable.CustomBottomButton_button_icon, -1);
        if (buttonIconDrawable != -1) {
            buttonIcon.setBackgroundResource(buttonIconDrawable);
        }
        // 设置按钮文字
        String buttonName = attributes.getString(R.styleable.CustomBottomButton_button_text);
        if (!TextUtils.isEmpty(buttonName)) {
            buttonText.setText(buttonName);
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        if (onClickListener != null) {
            buttonIcon.setOnClickListener(onClickListener);
            buttonText.setOnClickListener(onClickListener);
        }
    }

    public void setTag(Object tag){
        buttonIcon.setTag(tag);
        buttonText.setTag(tag);
    }

    public  void setButtonIcon(int drawable){
        buttonIcon.setBackgroundResource(drawable);
    }

    public void setButtonTextColor(int color){
        buttonText.setTextColor(color);
    }
}
