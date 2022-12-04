package com.pentaware.doorish;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class IfUserNotActive extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_if_user_not_active);

        Button btnGotoLoginPage = findViewById(R.id.btn_goto_login);
        btnGotoLoginPage.setOnClickListener(view -> {
            startActivity(new Intent(this, Login.class));
            finish();
        });
    }
}