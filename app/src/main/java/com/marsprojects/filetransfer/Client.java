package com.marsprojects.filetransfer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.net.Socket;

import me.itangqi.waveloadingview.WaveLoadingView;

public class Client extends AppCompatActivity {
    
    final int portNumber = 8080;

    String address;
    private int requestCode;
    private int grantResults[];

    RelativeLayout meowLayout;
    WaveLoadingView mWaveLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_or_receive_progress_bar);
        initialise();

        address = GolgolActivity.getIp();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            //if you dont have required permissions ask for it (only required for API 23+)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
            onRequestPermissionsResult(requestCode, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, grantResults);
        }
        ClientThread clientThread = new ClientThread(address, portNumber);
        clientThread.start();}
    @Override // android recommended class to handle permissions
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("permission", "granted");
                } else {

                    // permission denied.
                    Client.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(Client.this,
                                    "Permission Denied",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                    //app cannot function without this permission for now so close it...
                    onDestroy();
                }
                return;
            }
        }
    }

    private void initialise()
    {
        meowLayout = (RelativeLayout) findViewById(R.id.meowlayout);
        meowLayout.setVisibility(View.INVISIBLE);

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
    public class ClientThread extends Thread {
        String address;
        int port;

        public ClientThread(String address, int port) {
            this.address = address;
            this.port = port;
        }

        @Override
        public void run() {
            Socket socket = null;
            try {
                socket = new Socket(address, portNumber);
                File fileDir = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "LetsShare");
                if (fileDir.exists() == false) {
                    fileDir.mkdirs();
                    Client.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mWaveLoadingView.setProgressValue(10);
                        }
                    });

                }
                File file = new File(fileDir, "test.jpg");
                file.createNewFile();
                Client.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mWaveLoadingView.setProgressValue(25);
                    }
                });
                InputStream is = socket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(is);
                FileOutputStream fos = null;
                byte[] bytes = null;

                try {
                    bytes = (byte[]) ois.readObject();
                    Client.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mWaveLoadingView.setProgressValue(55);
                        }
                    });
                } catch (ClassNotFoundException e) {

                }catch (EOFException e)
                {

                }
                finally {
                    ois.close();
                }

                final Bitmap myBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                Client.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mWaveLoadingView.setProgressValue(65);
                    }
                });

                try {

                    Client.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mWaveLoadingView.setProgressValue(80);
                        }
                    });
                    fos = new FileOutputStream(file);
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    Client.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mWaveLoadingView.setProgressValue(90);
                        }
                    });
                    fos.flush();
                    fos.close();



                    scanMedia(file.toString());
                    Client.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mWaveLoadingView.setProgressValue(100);
                        }
                    });
                    //File toSet = new File(file.toString());
                    //imageView.setImageBitmap(BitmapFactory.decodeFile(toSet.getAbsolutePath()));
                } catch (Exception e) {
                    Client.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(Client.this,
                                    "sorry",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        fos.close();
                        Client.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                meowLayout.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                final String eMsg = "Something wrong from client: " + e.getMessage();
                Client.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(Client.this,
                                eMsg,
                                Toast.LENGTH_LONG).show();
                    }
                });

            } finally {
                if (socket != null) {
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

        private void scanMedia(String path) {
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            Intent scanFileIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
            sendBroadcast(scanFileIntent);
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
    private void deletePersistentGroups(){
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
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
