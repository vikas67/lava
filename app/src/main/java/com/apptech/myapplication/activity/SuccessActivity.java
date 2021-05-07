package com.apptech.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apptech.myapplication.R;

public class SuccessActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        getSupportActionBar().hide();

        String amt = getIntent().getStringExtra("amt");
        TextView amount = findViewById(R.id.amount);
        amount.setText(amt);

        LinearLayout btn = findViewById(R.id.btn);
        btn.setOnClickListener(v -> {
            startActivity(new Intent(SuccessActivity.this , MainActivity.class));
            finish();
        });

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}