package com.example.flightapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final int SECRET_KEY = 99;
    EditText ETUsername;
    EditText ETPassword;
    EditText ETConfirmPassword;
    EditText editTextEmailAddress;
    EditText editTextPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regisztracio);

        int secret_key = getIntent().getIntExtra("SECRET_KEY",0);

        if(secret_key != 99){
            finish();
        }

        ETUsername = findViewById(R.id.ETUsername);
        ETPassword = findViewById(R.id.ETPassword);
        ETConfirmPassword = findViewById(R.id.ETConfirmPassword);
        editTextEmailAddress = findViewById(R.id.editTextEmailAddress);
        editTextPhone = findViewById(R.id.editTextPhone);

        mAuth = FirebaseAuth.getInstance();


    }
    public void register(View view) {
        String userName = ETUsername.getText().toString();
        String email = editTextEmailAddress.getText().toString();
        String password = ETPassword.getText().toString();
        String passwordConf = ETConfirmPassword.getText().toString();
        String phoneNumb = editTextPhone.getText().toString();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "Sikeres felhasználó regisztráció");
                    StartFlight();
                }else{
                    Log.d(LOG_TAG, "Sikertelen felhasználó regisztráció");
                    Toast.makeText(RegisterActivity.this, "Sikertelen felhasználó regisztráció" + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void megse(View view) {
        finish();
    }
    public void StartFlight(){
        Intent intent = new Intent(this,FlightActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);

    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
