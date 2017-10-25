package com.wuch1k1n.exrate.ui;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wuch1k1n.exrate.R;

/**
 * Created by Administrator on 2017/10/5.
 */

public class CustomEditText extends LinearLayout {

    /**
     * 输入框最右边的货币图标
     */
    private ImageView et_currency_icon;
    private TextView et_currency_name;
    private EditText et_input;

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.custom_edittext, this, true);
        et_currency_icon = (ImageView) findViewById(R.id.et_currency_icon);
        et_currency_name = (TextView) findViewById(R.id.et_currency_name);
        et_input = (EditText) findViewById(R.id.et_input);
        this.setBackgroundResource(R.drawable.bg_edittext);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        if (onClickListener != null) {
            et_currency_icon.setOnClickListener(onClickListener);
            et_currency_name.setOnClickListener(onClickListener);
        }
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        et_input.addTextChangedListener(textWatcher);
    }

    public void setEditTextOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        et_input.setOnFocusChangeListener(onFocusChangeListener);
    }

    public void clearEditTextFocus() {
        et_input.clearFocus();
    }

    public void setEditTextColor(int color) {
        et_input.setTextColor(color);
    }

    public void setSelection(int index) {
        et_input.setSelection(index);
    }

    public void setCurrencyIcon(int drawable) {
        et_currency_icon.setBackgroundResource(drawable);
    }

    public void setCurrencyName(String name) {
        et_currency_name.setText(name);
    }

    public void setInputNum(String num) {
        et_input.setText(num);
    }

    public double getInputNum() {
        return Double.parseDouble(et_input.getText().toString());
    }
}
