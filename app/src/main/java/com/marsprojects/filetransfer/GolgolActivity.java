package com.marsprojects.filetransfer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;


public class GolgolActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 4000;
    ImageView imageView;
    RelativeLayout newfoundDevice;
    RelativeLayout newfoundDevice1;
    AnimationDrawable animationDrawable;
    TextView searchorfoundTV;
    ImageButton imageButton1;

    ImageProperty image1;
    ImageProperty image2;
    ImageProperty image3;
    ImageProperty image4;

    TextView deviceName1;
    TextView deviceName2;

    WifiManager wifiManager;
    static WifiP2pManager mWifiP2pManager;
    static WifiP2pManager.Channel mChannel;
    BroadcastReceiver mBroadcastReceiver;
    IntentFilter mIntentFilter;

    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    Map<String,WifiP2pDevice> deviceMap;
    String serverOrClient="";
    static String serverIpAddress="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_golgol);
        makeTransperant();
        initialise();
       // setHandler();
        setOnclickListeners();
        createImages();
        setServerOrClient();
    }
    private void setServerOrClient()
    {
        serverOrClient = send_receiveActivity.getDecided();
    }
    private void createImages()
    {
        image1 = new ImageProperty();
        image2 = new ImageProperty();
        image3 = new ImageProperty();
        image4 = new ImageProperty();
    }
    private void initialise()
    {
        newfoundDevice = (RelativeLayout) findViewById(R.id.newDeviceRL);
        newfoundDevice.setVisibility(View.INVISIBLE);
        searchorfoundTV = (TextView) findViewById(R.id.searchorfoundTV);
        newfoundDevice1 = (RelativeLayout) findViewById(R.id.newDeviceRL2);
        newfoundDevice1.setVisibility(View.INVISIBLE);

        deviceName1 = (TextView)findViewById(R.id.newDeviceTV);
        deviceName1.setVisibility(View.INVISIBLE);
        deviceName2 = (TextView)findViewById(R.id.newDeviceTV2);
        deviceName2.setVisibility(View.INVISIBLE);

        PulsatorLayout pulsator = (PulsatorLayout) findViewById(R.id.pulsator);
        pulsator.start();

        imageView = (ImageView) findViewById(R.id.catanim);
        animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
        imageButton1 = (ImageButton) findViewById(R.id.newDeviceIB);

        //WiFi State change
        deviceMap = new HashMap<String, WifiP2pDevice>();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //for WifiDirectBroadcastReceiver
        mWifiP2pManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWifiP2pManager.initialize(this,getMainLooper(),null);
        mBroadcastReceiver = new WiFiDirectBroadcastReceiver(mWifiP2pManager,mChannel,this);
        mIntentFilter = new IntentFilter();
        addIntentFilterActions();
        startSearch();
    }
    private void setHandler()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //newfoundDevice.setVisibility(View.VISIBLE);
                searchorfoundTV.setText("Device Found!");
            }
        }, SPLASH_TIME_OUT);
    }
    private void setOnclickListeners()
    {
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final WifiP2pDevice device = image1.device;
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                if (serverOrClient.equals("client")) {
                    config.groupOwnerIntent = 0;
                } else {
                    config.groupOwnerIntent = 15;
                }

                mWifiP2pManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(GolgolActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(GolgolActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
            private void makeTransperant() {
                if (Build.VERSION.SDK_INT >= 22) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                }
            }

            public void addIntentFilterActions() {
                mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
                mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
                mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
                mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            }

            public static String getIp() {
                return serverIpAddress;
            }

            WifiP2pManager.ConnectionInfoListener mConnectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo info) {
                    final InetAddress address = info.groupOwnerAddress;
                    serverIpAddress = address.getHostAddress().toString();
                    if (info.groupFormed && info.isGroupOwner) {

                        Toast.makeText(GolgolActivity.this, "Server", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(GolgolActivity.this, ServerClass.class);
                        startActivity(intent);
                    } else if (info.groupFormed) {
                        Toast.makeText(GolgolActivity.this, "Client", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(GolgolActivity.this, Client.class);
                        startActivity(intent);
                    }
                }
            };

            public void startSearch() {
                mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int reason) {


                    }
                });
            }

            WifiP2pManager.PeerListListener peerlistListener = new WifiP2pManager.PeerListListener() {
                @Override
                public void onPeersAvailable(WifiP2pDeviceList peersList) {
                    if (!peersList.getDeviceList().equals(peers)) {
                        peers.clear();
                        peers.addAll(peersList.getDeviceList());


                        int index = 1;
                        for (WifiP2pDevice device : peersList.getDeviceList()) {
                            deviceMap.put(device.deviceName, device);
                            if (index == 1) {
                                image1.setDeviceName(device.deviceName);
                                image1.setDevice(device);
                                deviceName1.setText(device.deviceName.toString());
                                deviceName1.setVisibility(View.VISIBLE);
                                newfoundDevice.setVisibility(View.VISIBLE);
                            } else if (index == 2) {
                                image2.setDeviceName(device.deviceName);
                                image2.setDevice(device);
                                newfoundDevice1.setVisibility(View.VISIBLE);

                            } else if (index == 3) {
                                image3.setDeviceName(device.deviceName);
                                image3.setDevice(device);
                            } else {
                                image4.setDeviceName(device.deviceName);
                                image4.setDevice(device);
                            }
                            index += 1;
                        }

                    }
                    if (peers.size() == 0) {
                        Toast.makeText(GolgolActivity.this, "No peers available", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            @Override
            protected void onResume() {
                super.onResume();
                registerReceiver(mBroadcastReceiver, mIntentFilter);
            }

            @Override
            protected void onPause() {
                super.onPause();
                unregisterReceiver(mBroadcastReceiver);

            }

            public class ImageProperty {
                String deviceName;
                WifiP2pDevice device;

                public ImageProperty() {

                }

                public void setDeviceName(String deviceName) {
                    this.deviceName = deviceName;
                }

                public void setDevice(WifiP2pDevice device) {
                    this.device = device;
                }
            }
        }



