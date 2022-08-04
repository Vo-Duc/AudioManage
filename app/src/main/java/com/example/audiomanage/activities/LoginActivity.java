package com.example.audiomanage.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.audiomanage.R;
import com.example.audiomanage.activities.crud.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    TextView textViewSignUp, textViewForgotPassword;
    Button buttonSignIn;
    CheckBox checkBoxRemember;
    private FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferencesFirstTime, sharedPreferencesRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        viewBinding();
        listener();
    }

    private void listener() {

        textViewForgotPassword.setOnClickListener(view -> {
//            startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            Toast.makeText(LoginActivity.this, "Sẽ ra mắt sau!", Toast.LENGTH_SHORT).show();
        });


        buttonSignIn.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            if (email.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập email của bạn!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập mật khẩu của bạn!", Toast.LENGTH_SHORT).show();
                return;
            }
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (checkBoxRemember.isChecked()) {
                        SharedPreferences.Editor editor = sharedPreferencesRemember.edit();
                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.putBoolean("check", true);
                        editor.commit();
                    } else {
                        SharedPreferences.Editor editor = sharedPreferencesRemember.edit();
                        editor.putString("email", "");
                        editor.putString("password", "");
                        editor.putBoolean("check", false);
                        editor.commit();
                    }
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "Tài khoản không tồn tại hoặc đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
                }
            });
        });
        textViewSignUp.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void viewBinding(){
        firebaseAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextEmailLogin);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewSignUp = findViewById(R.id.textViewSignUp);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        buttonSignIn = findViewById(R.id.buttonSignUp);
        checkBoxRemember = findViewById(R.id.checkBoxRemember);

        sharedPreferencesFirstTime = getSharedPreferences("FirstTimeScreen", MODE_PRIVATE);
        boolean isFirstTime = sharedPreferencesFirstTime.getBoolean("firstTime", true);
        /*if (isFirstTime){

            SharedPreferences.Editor editor = sharedPreferencesFirstTime.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();

            Intent intent = new Intent(LoginActivity.this, FirstTimeActivity.class);
            startActivity(intent);
            finish();
        }*/

        sharedPreferencesRemember = getSharedPreferences("LoginData", MODE_PRIVATE);
        editTextEmail.setText(sharedPreferencesRemember.getString("email", ""));
        editTextPassword.setText(sharedPreferencesRemember.getString("password", ""));
        checkBoxRemember.setChecked(sharedPreferencesRemember.getBoolean("check", false));

    }
}