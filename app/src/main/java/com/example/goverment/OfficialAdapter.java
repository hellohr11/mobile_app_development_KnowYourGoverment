package com.example.goverment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter <MyViewHolder>{
    private static final String TAG = "OfficialAdapter";
    private List<Official> noteList;
    private MainActivity mainAct;
    private ViewGroup parent;
    private int viewType;

    OfficialAdapter(List<Official> notes, MainActivity ma){
        this.noteList=notes;
        this.mainAct=ma;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        this.viewType = viewType;

        Log.d(TAG, "onCreateViewHolder: MAKING NEW MyViewHolder");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official, parent, false);

        itemView.setOnClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: FILLING VIEW HOLDER Employee " + position);

        Official note = noteList.get(position);

        holder.title.setText(note.getTitle());
        holder.name.setText(note.getName());

    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
