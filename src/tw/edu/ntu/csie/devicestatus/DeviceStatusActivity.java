package tw.edu.ntu.csie.devicestatus;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceStatusActivity extends Activity {
    private final static String TAG = "NTU-Android";

    private TextView batteryView;
    private ReceiveMessages receiver;

    class ReceiveMessages extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                showBattery(intent);
                return;
            }
            Log.e(TAG, "Unexpected action: " + action);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        receiver = new ReceiveMessages();
        registerReceiver(receiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(receiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        batteryView = (TextView) findViewById(R.id.battery);

        showBattery(batteryIntent());

        try {
            Log.i(TAG, "Activity started");
            Log.i(TAG, "FilesDir " + getFilesDir());
            Log.i(TAG, "ExternalFilesDir " + getExternalFilesDir(null));
            Log.i(TAG, "ExternalCacheDir " + getExternalCacheDir());
            Log.i(TAG, "ExternalFilesDir " + getExternalFilesDir(null));
            Log.i(TAG, "ExternalStorageState " + Environment.getExternalStorageState());
            Log.i(TAG, "DataDirectory " + Environment.getDataDirectory());
            Log.i(TAG, "DownloadCacheDirectory " + Environment.getDownloadCacheDirectory());
            Log.i(TAG, "ExternalStorageDirectory " + Environment.getExternalStorageDirectory());
            Log.i(TAG, "ExternalStoragePublicDirectory ALARMS " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS));
            Log.i(TAG, "ExternalStoragePublicDirectory DCIM " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
            Log.i(TAG, "ExternalStoragePublicDirectory DOWNLOADS " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
            Log.i(TAG, "ExternalStoragePublicDirectory MOVIES " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
            Log.i(TAG, "ExternalStoragePublicDirectory MUSIC " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
            Log.i(TAG, "ExternalStoragePublicDirectory NOTIFICATIONS " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS));
            Log.i(TAG, "ExternalStoragePublicDirectory PICTURES " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
            Log.i(TAG, "ExternalStoragePublicDirectory PODCASTS " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS));
            Log.i(TAG, "ExternalStoragePublicDirectory RINGTONES " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES));
            Log.i(TAG, "Environment.getRootDirectory " + Environment.getRootDirectory());
        }
        catch (Throwable thr) {
            Log.e(TAG, "Ouch!", thr);
        }
    }

    private Intent batteryIntent() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        return registerReceiver(null, ifilter);
    }

    private String batteryInfo(Intent bi) {
        StringBuffer sb = new StringBuffer();

        sb.append("** Android OS **\n");
	sb.append("Build ID: " + android.os.Build.ID + "\n");
        sb.append("Display ID: " + android.os.Build.DISPLAY + "\n");
        sb.append("Build date: " + android.os.Build.TIME + "\n");
        sb.append("Build type: " + android.os.Build.TYPE + "\n");
        sb.append("Fingerprint: " + android.os.Build.FINGERPRINT + "\n");

        return sb.toString();
    }

    public void sendBatteryInfo(View view) {
        Log.i(TAG, "sendBatteryInfo");
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, R.string.battery_label);
        i.putExtra(Intent.EXTRA_TEXT, batteryInfo(batteryIntent()));
        startActivity(Intent.createChooser(i, getString(R.string.battery_label)));
    }

    private void showBattery(Intent bi) {
        batteryView.setText(batteryInfo(bi));
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private final OnMenuItemClickListener runme =
            new OnMenuItemClickListener() {
        public boolean onMenuItemClick(MenuItem item) {
            Log.i(TAG, "runme");
            try {
                Process proc = Runtime.getRuntime().exec("/system/bin/sh /mnt/extSdCard/runme.sh >/mnt/extSdCard/runme.out 2>&1");
                proc.waitFor();
                toast("Run!");
            } catch (Exception e) {
                Log.e(TAG, "Exception", e);
                toast("Exception!");
            }
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        Log.i(TAG, "onCreateOptionsMenu");
        menu.add("runme.sh").setOnMenuItemClickListener(runme);
        return true;
    }

    protected void toast(String msg) {
        Toast.makeText(DeviceStatusActivity.this, msg,
                Toast.LENGTH_SHORT).show();
    }
}
