package com.wuch1k1n.exrate.util;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wuch1k1n.exrate.R;
import com.wuch1k1n.exrate.model.Currency;

import java.util.List;

import static com.wuch1k1n.exrate.R.id.iv_currency;

/**
 * Created by Administrator on 2017/10/15.
 */

public class CurrencyAdapter extends ArrayAdapter {

    private int resourceId;

    public CurrencyAdapter(@Nullable Context context, @Nullable int resource, @Nullable List objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Currency currency = (Currency) getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.currencyImage = (ImageView) view.findViewById(iv_currency);
            viewHolder.currencyCode = (TextView) view.findViewById(R.id.tv_currency_code);
            viewHolder.currencyName = (TextView) view.findViewById(R.id.tv_currency_name);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        // 将货币代号转换为对应的货币图标资源ID
        String imageName;
        if (currency.getCode().equals("TRY")) {
            // 解决土耳其货币代码TRY与关键字try冲突的问题
            imageName = "_try";
        } else {
            imageName = currency.getCode().toLowerCase();
        }
        Resources res = getContext().getResources();
        int imageId = res.getIdentifier(imageName, "mipmap", "com.wuch1k1n.exrate");

        viewHolder.currencyImage.setImageResource(imageId);
        viewHolder.currencyCode.setText(currency.getCode());
        viewHolder.currencyName.setText(currency.getName());
        return view;
    }

    class ViewHolder {
        ImageView currencyImage;
        TextView currencyCode;
        TextView currencyName;
    }
}
