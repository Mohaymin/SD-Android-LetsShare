package com.marsprojects.filetransfer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class send_receiveActivity extends AppCompatActivity{

    Button sendButton, receiveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_receive);

        if (Build.VERSION.SDK_INT >= 22) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }
        sendButton = (Button) findViewById(R.id.sendB);
        receiveButton = (Button) findViewById(R.id.receiveB);
        checkButtonStatus();
    }

    private void checkButtonStatus() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findDevice();
            }
        });
        receiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findDevice();
            }
        });
    }

    /*@Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.sendB:
                findDevice();
                break;
            case R.id.receiveB:
                findDevice();
                break;
        }
    }*/

    void findDevice(){
        Intent intent = new Intent(this , MainActivity.class);
        startActivity(intent);
    }
}
