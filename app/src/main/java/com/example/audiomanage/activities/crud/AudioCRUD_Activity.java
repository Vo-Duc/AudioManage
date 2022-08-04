package com.example.audiomanage.activities.crud;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.audiomanage.R;
import com.example.audiomanage.activities.MainActivity;
import com.example.audiomanage.adapters.AudioAdapter;
import com.example.audiomanage.models.AudioModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AudioCRUD_Activity extends AppCompatActivity {

    EditText editTextAudioName;
    ImageView imageViewFileAudio, btn_Back;
    Button buttonAdd, buttonUpdate, buttonRemove, buttonAll;
    ListView listView;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageReference;

    AudioAdapter adapterAudio;
    //ArrayAdapter arrayAdapterAudio;
    ArrayList<AudioModel> audioModelList;
    Toolbar toolbar;

    private Uri AudioFilePath;

    private final int PICK_AUDIO_REQUEST = 205;

    int currentPosition = -1;

    String audioUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_crud);

        toolbar = findViewById(R.id.toolbar_audio_crud);
        toolbar.setLogo(R.drawable.ic_icon_test);
        setSupportActionBar(toolbar);

        ViewBinding();

        LoadAllAudio();

        Listener();

    }

    private void LoadAudioFromStorage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            AudioFilePath = data.getData();
            if(AudioFilePath.getPath().endsWith(".mp3") || AudioFilePath.getPath().endsWith(".wav")){
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    imageViewFileAudio.setImageResource(R.drawable.icons8_ok_128);
                    //UploadAudioFileToFirestore();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(AudioCRUD_Activity.this, "Không đúng định dạng file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void UploadAudioFileToFirestore(){
        String audio_Name = editTextAudioName.getText().toString();
        if(!audio_Name.equals("")){
            if (AudioFilePath != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                StorageReference ref = storageReference.child("Audio/" + audio_Name);
                ref.putFile(AudioFilePath)
                        .addOnSuccessListener(taskSnapshot -> {
                            progressDialog.dismiss();
                            ref.getDownloadUrl().addOnCompleteListener(task -> {
                                audioUrl = task.getResult().toString();
                                UploadAudioToFirebase();
                            });
                            Toast.makeText(AudioCRUD_Activity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(AudioCRUD_Activity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        })
                        .addOnProgressListener(taskSnapshot -> {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        });
            } else {
                Toast.makeText(getApplicationContext(), "Vui lòng chọn lại bài hát", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập tên bài hát", Toast.LENGTH_SHORT).show();
        }
    }


    private void UploadAudioToFirebase() {

        String audio_Name = editTextAudioName.getText().toString();

        if (audio_Name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập tên file audio", Toast.LENGTH_SHORT).show();
            editTextAudioName.setFocusable(true);
            return;
        }

        StorageReference songRef = storageReference.child("Audio/" + audio_Name);

        songRef.getDownloadUrl().addOnCompleteListener(task -> {
            audioUrl = task.getResult().toString();
            DocumentReference doc = db.collection("Audio").document();
            //id
            AudioModel audioModel = new AudioModel(doc.getId(), audio_Name, audioUrl, auth.getUid(), "0");
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Đang tải...");
            progressDialog.show();

            doc.set(audioModel).addOnCompleteListener(task2 -> {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Thêm file audio thành công", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "Thêm file audio thất bại", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            });
        });
    }

    private void Listener() {

        imageViewFileAudio.setOnClickListener(view -> {
            LoadAudioFromStorage();
            //UploadAudioFileToFirestore();
        });

        buttonAdd.setOnClickListener(view -> {
            UploadAudioFileToFirestore();
            //UploadAudioToFirebase();
            LoadAllAudio();
        });

        buttonAll.setOnClickListener(view -> {
            LoadAllAudio();
        });

        buttonRemove.setOnClickListener(view -> {
            if (currentPosition != -1){
                db.collection("Audio").document(audioModelList.get(currentPosition).getId()).delete()
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(AudioCRUD_Activity.this, "Xoá thành công", Toast.LENGTH_SHORT).show();
                            LoadAllAudio();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(AudioCRUD_Activity.this, "Xoá thất bại", Toast.LENGTH_SHORT).show();
                        });
            }
            else {
                Toast.makeText(AudioCRUD_Activity.this, "Vui lòng chọn file", Toast.LENGTH_SHORT).show();
            }

        });

        buttonUpdate.setOnClickListener(view -> {
            UpdateAudio();
        });


        listView.setOnItemClickListener((adapterView, view, i, l) -> {

            String name = audioModelList.get(i).getName();
            editTextAudioName.setText(name);

            currentPosition = i;
        });
        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AudioCRUD_Activity.this, MainActivity.class));
            }
        });
    }

    private void UpdateAudio() {
        if (currentPosition != -1){
            String audio_Name = editTextAudioName.getText().toString();

            if (audio_Name.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Vui lòng nhập tên file audio", Toast.LENGTH_SHORT).show();
                editTextAudioName.setFocusable(true);
                return;
            }
            else{
                db.collection("Audio").document(audioModelList.get(currentPosition).getId()).update("name", audio_Name);
                Toast.makeText(getApplicationContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                LoadAllAudio();
            }
        }
        else {
            Toast.makeText(AudioCRUD_Activity.this, "Vui lòng chọn file", Toast.LENGTH_SHORT).show();
        }
    }


    private void LoadAllAudio() {
        audioModelList.clear();
        db.collection("Audio").whereEqualTo("user_Id", auth.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    AudioModel audioModel = doc.toObject(AudioModel.class);
                    audioModelList.add(audioModel);
                }
                //arrayAdapterAudio.notifyDataSetChanged();
                adapterAudio.notifyDataSetChanged();
            }
        });
    }

    private void ViewBinding() {

        editTextAudioName = findViewById(R.id.editTextAudioNameCRUD);

        imageViewFileAudio = findViewById(R.id.imageViewCRUDFileAudio);

        buttonAdd = findViewById(R.id.buttonAddAudioCRUD);
        buttonRemove = findViewById(R.id.buttonRemoveAudioCRUD);
        buttonUpdate = findViewById(R.id.buttonUpdateAudioCRUD);
        buttonAll = findViewById(R.id.buttonAllAudioCRUD);
        btn_Back = findViewById(R.id.btn_Back_audio_crud);

        listView = findViewById(R.id.listViewAudioCRUD);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        audioModelList = new ArrayList<>();

        adapterAudio = new AudioAdapter(AudioCRUD_Activity.this,R.layout.item_audio, audioModelList);
        listView.setAdapter(adapterAudio);

    }
}