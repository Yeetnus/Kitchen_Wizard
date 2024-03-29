package com.example.kitchenwizard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private Context mContext;
    private List<Recipe> mRecipes;
    private static LayoutInflater inflater = null;

    public CustomAdapter(Context context, List<Recipe> recipes) {
        mContext = context;
        mRecipes = recipes;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mRecipes.size();
    }

    @Override
    public Object getItem(int position) {
        return mRecipes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_recette, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView textView = convertView.findViewById(R.id.textView);

        String imageUrl = ((Recipe) getItem(position)).getImageURL();
        String recipeName =((Recipe) getItem(position)).getName();
        textView.setText(recipeName);
        // Load image from URL using Glide or Picasso
        Picasso.get().load(imageUrl).into(imageView);


        return convertView;
    }
}
