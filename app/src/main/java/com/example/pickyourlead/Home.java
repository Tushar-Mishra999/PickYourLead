package com.example.pickyourlead;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Home extends AppCompatActivity {
    static String nextpage;
    static String lastpage;

    public void login(View view) {
        Intent next = new Intent(this, Login.class);
        startActivity(next);
    }

    public void register(View view) {
        Intent next = new Intent(this, Register.class);
        startActivity(next);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}