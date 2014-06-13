/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity_bak extends Activity {
    private final static String TAG = DeviceControlActivity_bak.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private HashSet<BluetoothGattCharacteristic> mNotifyCharacteristic = new HashSet<BluetoothGattCharacteristic>();

    private GraphViewSeries mADCseries;
    private GraphView mGraphView;
    private ArrayList<SerializableGraphView> dataArray;
    private Calendar mCalendar = Calendar.getInstance();
    SimpleDateFormat mTimeFormat = new SimpleDateFormat(
            "mm:ss");

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                mBluetoothLeService.close();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                proceedGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                appendData(intent.getIntExtra(BluetoothLeService.EXTRA_DATA, -1));
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("array", dataArray);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_charts);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);


        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        LinearLayout layout = (LinearLayout) findViewById(R.id.pager);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        if (savedInstanceState != null) {
            dataArray = (ArrayList<SerializableGraphView>) savedInstanceState.getSerializable("array");
        }
        if (dataArray == null) {
            dataArray = new ArrayList<SerializableGraphView>();
        }
        GraphViewSeries.GraphViewSeriesStyle style = new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(200, 100, 0), 5);
        mADCseries = new GraphViewSeries("ADC raw", style, dataArray.toArray(new SerializableGraphView[]{}));

        mGraphView = new LineGraphView(this, "Бисквит");


        mGraphView.getGraphViewStyle().setNumHorizontalLabels(10);
        mGraphView.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double v, boolean b) {
                if (b) {
                    return mTimeFormat.format(new Date((long)v));
                } else return ((long)v)+"";
            }
        });

        mGraphView.setViewPort(2, 40);
        mGraphView.setScrollable(true);
        mGraphView.setScalable(true);
        mGraphView.setManualYAxis(true);
        mGraphView.setManualYAxisBounds((double) 1024, (double) 0);
        mGraphView.addSeries(mADCseries);

//        for (int i = 1; i < 500; i++) {
//            mADCseries.appendData(new SerializableGraphView(i,5),true,1000);
//        }

//        mGraphView.setLayoutParams(new LinearLayout.LayoutParams(width - 50, height - 200));
        mGraphView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(mGraphView);
    }

    
    
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                if (!mBluetoothLeService.connect(mDeviceAddress)) {
                    if (mBluetoothLeService.initialize()) {
                        mBluetoothLeService.connect(mDeviceAddress);
                    } else
                        Toast.makeText(getApplicationContext(), "Ошибка соединения с устройством", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
    }


    private void appendData(int value) {
        SerializableGraphView point = new SerializableGraphView((double) System.currentTimeMillis(), value);
        dataArray.add(point);
        mADCseries.appendData(point, true, 1000);
        long timePeriod = 9 * 1000;
        long currentTime = System.currentTimeMillis();
        long pastTime = currentTime - (long) (timePeriod * 0.75);
        mGraphView.setViewPort((double) pastTime, (double) timePeriod);
        //mADCseries.
    }

    private void enableNotification(BluetoothGattCharacteristic characteristic) {
        if (characteristic == null) return;
        final int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            if (mNotifyCharacteristic.remove(characteristic)) {
                mBluetoothLeService.setCharacteristicNotification(
                        characteristic, false);
            }
            mBluetoothLeService.readCharacteristic(characteristic);
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mNotifyCharacteristic.add(characteristic);
            mBluetoothLeService.setCharacteristicNotification(characteristic, true);
        }
    }

    private void proceedGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        for (BluetoothGattService gattService : gattServices) {
            if (SampleGattAttributes.lookup(gattService.getUuid().toString())) {
                for (BluetoothGattCharacteristic gattCharacteristic : gattService.getCharacteristics()) {
                    if (SampleGattAttributes.lookup(gattCharacteristic.getUuid().toString())) {
//                        UUID pinEnableService = UUID.fromString("713D0003-503E-4C75-BA94-3148F18D941E");
//                        gattService.getCharacteristic(pinEnableService).setValue(new byte[]{'S', (char) 14, (char) 1});
                        enableNotification(gattCharacteristic);
                    }
                }
            }
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
