package com.pentaware.doorish;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

public class codPopup extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cod_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width * 0.8), (int) (height * 0.5));


        Button btnCod = findViewById(R.id.continueCOD);
        Button btnPrepaid = findViewById(R.id.prepaid);

        btnCod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show("1");
            }
        });

        btnPrepaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show("0");
            }
        });


    }

    private void show(String sVal){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("value", sVal);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}