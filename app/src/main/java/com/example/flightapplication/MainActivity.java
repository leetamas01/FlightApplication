package com.example.flightapplication;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int SECRET_KEY = 99;
    FirebaseAuth mAuth;
    EditText ETUsername;
    EditText ETPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ETUsername = findViewById(R.id.ETUsername);
        ETPassword = findViewById(R.id.ETPassword);

        mAuth = FirebaseAuth.getInstance();


    }
    public void login(View view) {
        String userName = ETUsername.getText().toString();
        String password = ETPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(userName,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG,"A felhasznűló sikeresen belépett!");
                    StartFlight();
                }else{
                    Log.d(LOG_TAG, "Sikertelen felhasználó belépés!");
                    Toast.makeText(MainActivity.this, "Sikertelen felhasználó bejelentkezés!" + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    public void LoginWithGuest(View view) {
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG,"A vendég felhasznűló sikeresen belépett!");
                    StartFlight();
                }else{
                    Log.d(LOG_TAG, "Sikertelen a vendég felhasználó belépése!");
                    Toast.makeText(MainActivity.this, "Sikertelen vendég felhasználó bejelentkezés!" + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void regist(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY",SECRET_KEY);
        //startActivity(intent);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
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
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}