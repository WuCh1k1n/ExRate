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

import com.squareup.picasso.Picasso;
import com.wuch1k1n.exrate.R;
import com.wuch1k1n.exrate.model.Currency;
import com.wuch1k1n.exrate.model.News;

import java.util.List;

import static com.wuch1k1n.exrate.R.id.iv_currency;

/**
 * Created by Administrator on 2017/10/15.
 */

public class NewsAdapter extends ArrayAdapter {

    private int resourceId;

    public NewsAdapter(@Nullable Context context, @Nullable int resource, @Nullable List objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        News news = (News) getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.newsImage = (ImageView) view.findViewById(R.id.iv_news);
            viewHolder.newsTitle = (TextView) view.findViewById(R.id.tv_news_title);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        Picasso.with(getContext()).load(news.getImageUrl()).fit().into(viewHolder.newsImage);
        viewHolder.newsTitle.setText(news.getTitle());
        return view;
    }

    class ViewHolder {
        ImageView newsImage;
        TextView newsTitle;
    }
}
