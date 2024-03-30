package com.example.kitchenwizard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecetteAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mData;

    public RecetteAdapter(Context context, List<String> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_ingredient, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.textView);

        String ingredientName =(String) getItem(position);
        textView.setText(ingredientName);

        return convertView;
    }

    static class ViewHolder {
        TextView textView;
    }
}
