package com.zfdang.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zfdang.multiple_images_selector.ImagesSelectorActivity;

public class DemoActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 732;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        Button bt = (Button) findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start multiple photos selector
                Intent intent = new Intent(DemoActivity.this, ImagesSelectorActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                // DO Things here
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
