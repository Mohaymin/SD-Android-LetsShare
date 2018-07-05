package com.marsprojects.filetransfer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class send_receiveActivity extends AppCompatActivity{

    Button sendButton, receiveButton;
    static String decided="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_receive);

        makeTransparent();
        initialise();
        addOnclickListeners();

    }
    private void initialise()
    {

        sendButton = (Button) findViewById(R.id.sendB);
        receiveButton = (Button) findViewById(R.id.receiveB);
    }
    private void makeTransparent()
    {
        if (Build.VERSION.SDK_INT >= 22) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }
    }
    private void addOnclickListeners()
    {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decided = "send";
                findDevice();
            }
        });
        receiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decided = "receive";
                findDevice();
            }
        });
    }
    void findDevice(){
        Intent intent = new Intent(this , GolgolActivity.class);
        startActivity(intent);
    }
    public static String getDecided()
    {
        return decided;
    }
}
