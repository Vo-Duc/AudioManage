package com.example.audiomanage.activities;

import android.app.Dialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.audiomanage.R;
import com.example.audiomanage.activities.crud.AudioCRUD_Activity;
import com.example.audiomanage.adapters.AudioPlay_Adapter;
import com.example.audiomanage.models.AudioModel;
import com.example.audiomanage.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {
    ListView listView_Audio;
    TextView textViewEnd;
    SeekBar seekBar;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageReference;
    MediaPlayer mediaPlayer;
    AudioManager audioManager = null;
    AudioModel selected_Audio;

    AudioPlay_Adapter adapterAudio;
    //ArrayAdapter arrayAdapterAudio;
    ArrayList<AudioModel> audioModelList = new ArrayList<>();
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_icon_test);
        setSupportActionBar(toolbar);
        ViewBinding();
        LoadAllAudio();
        //select_Audio();

        final String[] audio = {""};

        final int[] dem = {0};
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                db.collection("User").whereEqualTo("id", auth.getUid()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            audio[0] = doc.toObject(UserModel.class).getSelect_Audio();
                        }
                        db.collection("Audio").whereEqualTo("id", audio[0]).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                for (QueryDocumentSnapshot doc : task1.getResult()) {
                                    AudioModel audioModel = doc.toObject(AudioModel.class);
                                    String status = audioModel.getStatus().toString();
                                    if(status.equals("1")){
                                        //Toast.makeText(MainActivity.this, "Trạng thái đã bật", Toast.LENGTH_SHORT).show();
                                        if(dem[0] ==0) {
                                            DialogPlay(audioModel);
                                            dem[0] =1;
                                        }
                                    }
                                    else{
                                        Stop();
                                        dem[0]=0;
                                    }
                                }
                            }
                        });
                    }
                });

            }
        },0,1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_audio_manage, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_audio_crud:
                startActivity(new Intent(MainActivity.this, AudioCRUD_Activity.class));
                break;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void ViewBinding() {
        listView_Audio=findViewById(R.id.listView_AudioPlay);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        audioModelList = new ArrayList<>();

        adapterAudio = new AudioPlay_Adapter(MainActivity.this,R.layout.item_audio_play, audioModelList);
        listView_Audio.setAdapter(adapterAudio);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }
    private void LoadAllAudio() {
        audioModelList.clear();
        db.collection("Audio").whereEqualTo("user_Id", auth.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    AudioModel audioModel = doc.toObject(AudioModel.class);
                    audioModelList.add(audioModel);
                }
                adapterAudio.notifyDataSetChanged();
            }
        });
    }
    public void DialogPlay(AudioModel audioModel){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_playing);

        TextView txt_Audio_Name = dialog.findViewById(R.id.textView_Audio_Name);
        ImageView img_Play = dialog.findViewById(R.id.imageViewPlay);
        textViewEnd = dialog.findViewById(R.id.textViewEnd);
        seekBar = dialog.findViewById(R.id.seekBar);
        txt_Audio_Name.setText(audioModel.getName());
        img_Play.setImageResource(R.drawable.icons8_pause_64);
        Play(audioModel);

        img_Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_Play.setImageResource(R.drawable.icons8_play_64);
                Stop();
            }
        });

        dialog.show();
    }
    private void Play(AudioModel audioModel){
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioModel.getUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        //imageViewPlay.setImageResource(R.drawable.icons8_pause_64);
        SetTime();

    }
    private void SetTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        textViewEnd.setText(simpleDateFormat.format(mediaPlayer.getDuration()));
        seekBar.setMax(mediaPlayer.getDuration());
    }
    private void Stop(){
        mediaPlayer.stop();
    }

    private void select_Audio(){
        ArrayList<AudioModel> selected = new ArrayList<>();
        db.collection("Audio").whereEqualTo("user_Id", auth.getUid())
                .whereEqualTo("status", "1").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    AudioModel audioModel = doc.toObject(AudioModel.class);
                    selected.add(audioModel);
                }
                selected_Audio = selected.get(0);
            }
        });
    }

}