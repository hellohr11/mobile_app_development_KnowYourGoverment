package com.example.goverment;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    TextView name;


    MyViewHolder(View view) {
        super(view);
        title = view.findViewById(R.id.title);
        name = view.findViewById(R.id.text);

    }

}
