package jp.ac.ehime_u.cite.sasaki.SensorUdp;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView.OnEditorActionListener;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * ロケーションリスナを実装するクラス。
 * 
 * @author Takashi SASAKI
 * 
 */
public class MyLocationListener implements LocationListener {

    LocationManager locationManager;
    SenderThread senderThread;
    static MyLocationListener myLocationListener;

    CheckBox checkBoxGps;
    CheckBox checkBoxNetwork;
    EditText editTextGpsMinDistance;
    EditText editTextGpsMinInterval;
    EditText editTextNetworkMinDistance;
    EditText editTextNetworkMinInterval;
    TextView textViewGps;
    TextView textViewNetwork;

    // Activity activity;

    int counterNetwork;
    int counterGps;

    /**
     * Constructor is private in singleton pattern.
     **/
    MyLocationListener() {
    }

    /**
     * ロケーションリスナのシングルトンインスタンスを取得する。
     * 
     * @param activity_
     * @return
     */
    synchronized static MyLocationListener GetSingleton(Activity activity_) {
        if (myLocationListener == null) {
            myLocationListener = new MyLocationListener();
        }
        // myLocationListener.activity = activity_;
        myLocationListener.FindView(activity_);
        myLocationListener.SetListeners();
        myLocationListener.CreateLocationManager(activity_);
        return myLocationListener;
    }

    synchronized static MyLocationListener GetSingleton() {
        return myLocationListener;
    }

    void FindView(Activity activity_) {
        checkBoxGps = (CheckBox) activity_.findViewById(R.id.CheckBoxGps);
        checkBoxNetwork = (CheckBox) activity_
                .findViewById(R.id.CheckBoxNetwork);
        editTextGpsMinDistance = (EditText) activity_
                .findViewById(R.id.EditTextGpsMinDistance);
        editTextGpsMinInterval = (EditText) activity_
                .findViewById(R.id.EditTextGpsMinInterval);
        textViewGps = (TextView) activity_.findViewById(R.id.TextViewGps);
        editTextNetworkMinDistance = (EditText) activity_
                .findViewById(R.id.EditTextNetworkMinDistance);
        editTextNetworkMinInterval = (EditText) activity_
                .findViewById(R.id.EditTextNetworkMinInterval);
        textViewNetwork = (TextView) activity_
                .findViewById(R.id.TextViewNetwork);
    }

    void SetListeners() {
        // GPSによる測位の可否が変更された場合
        checkBoxGps.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                counterGps = 0;
                ChangeLocationProvider();
            }
        });
        // GPSによる測位の最短移動距離が変更された場合
        editTextGpsMinDistance
                .setOnEditorActionListener(new OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId,
                            KeyEvent event) {
                        counterGps = 0;
                        checkBoxGps.setChecked(false);
                        ChangeLocationProvider();
                        return true;
                    }
                });
        // GPSによる測位の最短時間間隔が変更された場合
        editTextGpsMinInterval
                .setOnEditorActionListener(new OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId,
                            KeyEvent event) {
                        counterGps = 0;
                        checkBoxGps.setChecked(false);
                        ChangeLocationProvider();
                        return true;
                    }
                });
        // Networkを使った測位の可否が変更された場合
        checkBoxNetwork
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        counterNetwork = 0;
                        ChangeLocationProvider();
                    }
                });
        // Networkによる測位の最短移動距離が変更された場合
        editTextNetworkMinDistance
                .setOnEditorActionListener(new OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId,
                            KeyEvent event) {
                        counterNetwork = 0;
                        checkBoxNetwork.setChecked(false);
                        ChangeLocationProvider();
                        return true;
                    }
                });
        // Networkによる測位の最短時間間隔が変更された場合
        editTextNetworkMinInterval
                .setOnEditorActionListener(new OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId,
                            KeyEvent event) {
                        counterNetwork = 0;
                        checkBoxNetwork.setChecked(false);
                        ChangeLocationProvider();
                        return true;
                    }
                });
    }

    void CreateLocationManager(Activity activity_) {
        locationManager = (LocationManager) activity_
                .getSystemService(Context.LOCATION_SERVICE);
    }

    void ChangeLocationProvider() {
        locationManager.removeUpdates(this);
        if (checkBoxGps.isChecked()) {
            try {
                int min_distance = Integer.parseInt(editTextGpsMinDistance
                        .getEditableText().toString());
                int min_interval = Integer.parseInt(editTextGpsMinInterval
                        .getEditableText().toString());
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, min_interval,
                        min_distance, this);
            } catch (NumberFormatException e) {
                checkBoxGps.setChecked(false);
                Log.d("SensorUdp", e.toString());
            } catch (IllegalArgumentException e) {
                checkBoxGps.setChecked(false);
                Log.d("SensorUdp", e.toString());
            }
        }
        if (checkBoxNetwork.isChecked()) {
            try {
                int min_distance = Integer.parseInt(editTextNetworkMinDistance
                        .getEditableText().toString());
                int min_interval = Integer.parseInt(editTextNetworkMinInterval
                        .getEditableText().toString());
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, min_interval,
                        min_distance, this);
            } catch (NumberFormatException e) {
                checkBoxNetwork.setChecked(false);
                Log.d("SensorUdp", e.toString());
            } catch (IllegalArgumentException e) {
                checkBoxNetwork.setChecked(false);
                Log.d("SensorUdp", e.toString());
            }
        }
    }

    // 10進数固定小数点表示するためのフォーマットを行うクラス DecimalFormat
    private static final DecimalFormat decimal_format = new DecimalFormat(
            "000.0000000");

    public void onLocationChanged(Location arg0) {
        if (arg0.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            Date date = new Date();
            String location_by_gps_cvs_string = "G, " + ++counterGps + ", "
                    + date.getTime() + ", "
                    + decimal_format.format(arg0.getAltitude()) + ", "
                    + decimal_format.format(arg0.getLatitude()) + ", "
                    + decimal_format.format(arg0.getLongitude()) + ", "
                    + arg0.getTime() + ", "
                    + decimal_format.format(arg0.getAccuracy()) + ", "
                    + decimal_format.format(arg0.getSpeed()) + "\n";
            textViewGps.setText(location_by_gps_cvs_string);
            senderThread.SendMessageByUdp(location_by_gps_cvs_string);
        } else if (arg0.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            Date date = new Date();
            String location_by_network_cvs_string = "N, " + ++counterNetwork
                    + ", " + date.getTime() + ", "
                    + decimal_format.format(arg0.getAltitude()) + ", "
                    + decimal_format.format(arg0.getLatitude()) + ", "
                    + decimal_format.format(arg0.getLongitude()) + ", "
                    + arg0.getTime() + ", "
                    + decimal_format.format(arg0.getAccuracy()) + ", "
                    + decimal_format.format(arg0.getSpeed()) + "\n";
            textViewNetwork.setText(location_by_network_cvs_string);
            senderThread.SendMessageByUdp(location_by_network_cvs_string);
        } else {
            Log.v("SensorUdp", "Unknown provider " + arg0.getProvider());
        }
    }

    public void onProviderDisabled(String arg0) {
    }

    public void onProviderEnabled(String arg0) {
    }

    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
    }

    public void UncheckAll() {
        checkBoxGps.setChecked(false);
        checkBoxNetwork.setChecked(false);
        ChangeLocationProvider();
    }
}
