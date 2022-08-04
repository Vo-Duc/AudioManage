package com.example.audiomanage.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.audiomanage.R;
import com.example.audiomanage.activities.MainActivity;
import com.example.audiomanage.activities.crud.AudioCRUD_Activity;
import com.example.audiomanage.models.AudioModel;

import java.util.ArrayList;
import java.util.List;

public class AudioPlay_Adapter extends BaseAdapter {

    MainActivity context;
    List<AudioModel> audioModelList;
    ArrayList<AudioModel> audioList;
    private int layout;

    public AudioPlay_Adapter(MainActivity context, int layout, ArrayList<AudioModel> audioList){
        this.context = context;
        this.audioList = audioList;
        this.layout = layout;
    }
    public AudioPlay_Adapter(MainActivity context, List<AudioModel> audioModelList) {
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

            holder.textViewName = view.findViewById(R.id.txt_Name_Audio_Play);
            holder.textViewId = view.findViewById(R.id.txt_Id_Audio_Play);
            holder.img_Delete = view.findViewById(R.id.imgDelete_Audio);
            holder.img_Play = view.findViewById(R.id.img_Play);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        AudioModel audioModel = audioList.get(i);
        holder.textViewName.setText(audioModel.getName());
        holder.textViewId.setText(audioModel.getId());

        holder.img_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //context.DialogDeletePhone(phone);
            }
        });
        holder.img_Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.DialogPlay(audioModel);
            }
        });

        return view;
    }
    private class ViewHolder{
        TextView textViewName, textViewId;
        ImageView img_Play, img_Delete;
    }
}