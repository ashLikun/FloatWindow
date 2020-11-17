package com.example.yhao.floatwindow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ashlikun.floatwindow.FloatWindow;
import com.example.yhao.fixedfloatwindow.R;

public class A_Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);
        setTitle("A");



    }

    public void change(View view) {
        startActivity(new Intent(this, B_Activity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        FloatWindow.get().show(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
