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
    private List<String> mData2;

    public RecetteAdapter(Context context, List<String> data,List<String> data2) {
        mContext = context;
        mData = data;
        mData2 = data2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    public Object getItem2(int position) {
        return mData2.get(position);
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
        TextView textView2 = convertView.findViewById(R.id.textView3);

        String ingredientName =(String) getItem(position);
        textView.setText(ingredientName);
        try{
            String ingredientName2 = (String) getItem2(position);
            textView2.setText(ingredientName2);
        }catch(Exception e){
            textView2.setText("");
        }
        return convertView;
    }

    static class ViewHolder {
        TextView textView;
    }
}
