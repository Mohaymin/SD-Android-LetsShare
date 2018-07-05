package com.marsprojects.filetransfer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

import me.itangqi.waveloadingview.WaveLoadingView;

public class ServerClass extends AppCompatActivity {
    RelativeLayout meowLayout;
    LinearLayout buttonLayout;
    ImageButton textIB;
    ImageButton pictureIB;
    ImageButton sendIB;

    WaveLoadingView mWaveLoadingView;

    Socket socketForServer;
    ServerSocket serverSocket;
    final int SocketServerPORT = 8080;
    final int REQUEST_CODE = 48;
    Uri uri;
    int height;
    int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_or_receive_progress_bar);
        initialise();
        addOnClickListeners();
        ServerThread serverThread = new ServerThread();
        serverThread.start();
    }


    private void initialise() {
        meowLayout = (RelativeLayout) findViewById(R.id.meowlayout);
        meowLayout.setVisibility(View.INVISIBLE);
        buttonLayout = (LinearLayout) findViewById(R.id.Layoutforbuttons);
        sendIB = (ImageButton) findViewById(R.id.sendIB);
        pictureIB = (ImageButton) findViewById(R.id.pictureIB);
        textIB = (ImageButton) findViewById(R.id.textIB);

        mWaveLoadingView = (WaveLoadingView) findViewById(R.id.waveLoadingView);
        mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
        mWaveLoadingView.setTopTitle("Done!");
        mWaveLoadingView.setBottomTitleSize(18);
        mWaveLoadingView.setProgressValue(0);
        //generatePercentage();
        mWaveLoadingView.setBorderWidth(10);
        mWaveLoadingView.setAmplitudeRatio(60);

        mWaveLoadingView.setTopTitleStrokeWidth(3);
        mWaveLoadingView.setAnimDuration(3000);
    }

    private void addOnClickListeners() {
        pictureIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearch();
            }
        });

        sendIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(uri == null)
                {
                    ServerClass.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ServerClass.this,R.style.MyDialogTheme);
                            dlgAlert.setMessage("You need to select something first");
                            dlgAlert.setPositiveButton("Ok",null);
                            dlgAlert.setTitle("Let's Share");
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
                        }
                    });
                }
                else {


                    FileTxThread fileTxThread = new FileTxThread(socketForServer);
                    fileTxThread.start();}
    }
    });
}

    private void startSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                uri = data.getData();
                //path = uri.toString();
            }
        }
    }

    public class ServerThread extends Thread {
        @Override
        public void run() {
            socketForServer = null;
            try {
                serverSocket = new ServerSocket(SocketServerPORT);

                while (true) {
                    socketForServer = serverSocket.accept();

                    ServerClass.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(ServerClass.this,
                                    "connected",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (socketForServer != null) {
                    try {
                        socketForServer.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public class FileTxThread extends Thread {
        Socket socket;
        FileTxThread(Socket socket){

            this.socket= socket;
            ServerClass.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mWaveLoadingView.setProgressValue(10);
                }});
        }
        @Override
        public void run() {
            ContentResolver cr = getContentResolver();

            try {
                InputStream is = cr.openInputStream(uri);
                final Bitmap bitmap= MediaStore.Images.Media.getBitmap( ServerClass.this.getContentResolver(),uri);


                ServerClass.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mWaveLoadingView.setProgressValue(25);
                    }});
                //converting bitmap to byte array
                byte[] bytes = null;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                ServerClass.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mWaveLoadingView.setProgressValue(45);
                    }});
                bytes = stream.toByteArray();
                bitmap.recycle();
                ServerClass.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mWaveLoadingView.setProgressValue(65);
                    }});
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ServerClass.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mWaveLoadingView.setProgressValue(85);
                    }});
                oos.writeObject(bytes);
                oos.flush();ServerClass.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mWaveLoadingView.setProgressValue(100);
                        meowLayout.setVisibility(View.VISIBLE);
                    }});
                final String sentMsg = "File sent to: " + socket.getInetAddress();
                ServerClass.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(ServerClass.this,
                                sentMsg,
                                Toast.LENGTH_LONG).show();
                    }});

            } catch (FileNotFoundException e) {
                //Toast.makeText(getApplicationContext(),"file not found",Toast.LENGTH_SHORT).show();
                ServerClass.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(ServerClass.this,
                                "File Found",
                                Toast.LENGTH_LONG).show();
                    }});
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                
                try {

                    socket.close();
                    deletePersistentGroups();
                    disconnect();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }


    private void deletePersistentGroups() {
        try {
            Method[] methods = WifiP2pManager.class.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals("deletePersistentGroup")) {
                    // Delete any persistent group
                    for (int netid = 0; netid < 32; netid++) {
                        methods[i].invoke(GolgolActivity.mWifiP2pManager, GolgolActivity.mChannel, netid, null);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        if (GolgolActivity.mWifiP2pManager != null && GolgolActivity.mChannel != null) {
            GolgolActivity.mWifiP2pManager.requestGroupInfo(GolgolActivity.mChannel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null && GolgolActivity.mWifiP2pManager != null && GolgolActivity.mChannel != null
                            && group.isGroupOwner()) {
                        GolgolActivity.mWifiP2pManager.removeGroup(GolgolActivity.mChannel, new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFailure(int reason) {

                            }
                        });
                    }
                }
            });
        }
    }
}

