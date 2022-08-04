package com.example.audiomanage.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiomanage.R;
import com.example.audiomanage.activities.crud.AudioCRUD_Activity;
import com.example.audiomanage.models.AudioModel;

import java.util.ArrayList;
import java.util.List;

public class AudioAdapter extends BaseAdapter {

    AudioCRUD_Activity context;
    List<AudioModel> audioModelList;
    ArrayList<AudioModel> audioList;
    private int layout;

    public AudioAdapter(AudioCRUD_Activity context, int layout, ArrayList<AudioModel> audioList){
        this.context = context;
        this.audioList = audioList;
        this.layout = layout;
    }
    public AudioAdapter(AudioCRUD_Activity context, List<AudioModel> audioModelList) {
        this.context = context;
        this.audioModelList = audioModelList;
    }

    @Override
    public int getCount() {
        return audioList.size();
    }

    @Override
    public Object getItem(int i) {
        return audioList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if(view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getLayoutInflater();
            view = inflater.inflate(layout, null);

            holder.textViewName = view.findViewById(R.id.txt_Name);
            holder.textViewId = view.findViewById(R.id.txt_Id);
//            holder.imgDelete = view.findViewById(R.id.imgDelete_Phone);
//            holder.imgEdit = view.findViewById(R.id.imgEdit_Phone);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        AudioModel audioModel = audioList.get(i);
        holder.textViewName.setText(audioModel.getName());
        holder.textViewId.setText(audioModel.getId());

//        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                context.DialogDeletePhone(phone);
//            }
//        });
//        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                context.DialogEditPhone(phone);
//            }
//        });

        return view;
    }
    private class ViewHolder{
        TextView textViewName, textViewId;
    }
}