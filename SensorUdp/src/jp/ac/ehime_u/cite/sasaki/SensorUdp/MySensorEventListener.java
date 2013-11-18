package jp.ac.ehime_u.cite.sasaki.SensorUdp;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class MySensorEventListener implements SensorEventListener {
    // 本物のセンターを使う場合
    SensorManager sensorManager;
    // センサーシミュレータを使う場合
    // private SensorManagerSimulator sensorManager;

    RadioButton radioButtonFastest;
    RadioButton radioButtonGame;
    RadioButton radioButtonNormal;
    RadioButton radioButtonUi;
    RadioGroup radioGroupDelay;
    CheckBox checkBoxAccelerometer;
    CheckBox checkBoxMagneticField;
    CheckBox checkBoxOrientation;
    TextView textViewAccelerometer;
    TextView textViewMagneticField;
    TextView textViewOrientation;

    int counterAccelerometer;
    int counterMagneticField;
    int counterOrientation;

    SenderThread senderThread;

    static MySensorEventListener mySensorEventListener;

    /**
     * Constructor is private in singleton pattern.
     */
    MySensorEventListener() {
    }

    synchronized public static MySensorEventListener GetSingleton(
            Activity activity_) {
        if (mySensorEventListener == null) {
            mySensorEventListener = new MySensorEventListener();
        }
        mySensorEventListener.FindViews(activity_);
        mySensorEventListener.GetSensorManager(activity_);
        mySensorEventListener.SetListeners(activity_);
        return mySensorEventListener;
    }

    synchronized public static MySensorEventListener GetSingleton() {
        return mySensorEventListener;
    }

    void FindViews(Activity activity_) {
        textViewAccelerometer = (TextView) activity_
                .findViewById(R.id.TextViewAccelerometer);
        textViewMagneticField = (TextView) activity_
                .findViewById(R.id.TextViewMagneticField);
        textViewOrientation = (TextView) activity_
                .findViewById(R.id.TextViewOrientation);
        checkBoxAccelerometer = (CheckBox) activity_
                .findViewById(R.id.CheckBoxAccelerometer);
        checkBoxMagneticField = (CheckBox) activity_
                .findViewById(R.id.CheckBoxMagneticField);
        checkBoxOrientation = (CheckBox) activity_
                .findViewById(R.id.CheckBoxOrientation);
        radioButtonFastest = (RadioButton) activity_
                .findViewById(R.id.RadioButtonFastest);
        radioButtonGame = (RadioButton) activity_
                .findViewById(R.id.RadioButtonGame);
        radioButtonNormal = (RadioButton) activity_
                .findViewById(R.id.RadioButtonNormal);
        radioButtonUi = (RadioButton) activity_
                .findViewById(R.id.RadioButtonUi);
        radioGroupDelay = (RadioGroup) activity_
                .findViewById(R.id.RadioGroupDelay);
    }

    void SetListeners(Activity activity_) {
        // 加速度センサーの使用可否が変更された場合
        checkBoxAccelerometer
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        counterAccelerometer = 0;
                    }
                });
        // 磁気センサーの使用可否が変更された場合
        checkBoxMagneticField
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        counterMagneticField = 0;
                    }
                });
        // 方向センサーの使用可否が変更された場合
        checkBoxOrientation
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        counterOrientation = 0;
                    }
                });
        // 六軸センサー情報の取得頻度が変更された場合
        radioGroupDelay
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        sensorManager.unregisterListener(MySensorEventListener.GetSingleton());
                        //UnregisterSensorListener();
                        RegisterSensorListener();
                        Log.v("SensorUdp", "Delay changed");
                    }
                });

        RegisterSensorListener();
    }

    public void UncheckAll() {
        checkBoxAccelerometer.setChecked(false);
        checkBoxMagneticField.setChecked(false);
        checkBoxOrientation.setChecked(false);
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    // 10進数固定小数点表示するためのフォーマットを行うクラス DecimalFormat
    private static final DecimalFormat decimal_format = new DecimalFormat(
            "000.0000000");

    public void onSensorChanged(SensorEvent sensor_event) {
        if (sensor_event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (checkBoxAccelerometer.isChecked()) {
                // 加速度センサーの値を表示
                Date date = new Date();
                String accelerometer_cvs_line = "A, " + ++counterAccelerometer
                        + ", " + date.getTime() + ", "
                        + decimal_format.format(sensor_event.values[0]) + ", "
                        + decimal_format.format(sensor_event.values[1]) + ", "
                        + decimal_format.format(sensor_event.values[2]);
                textViewAccelerometer.setText(accelerometer_cvs_line);
                senderThread.SendMessageByUdp(accelerometer_cvs_line + "\n");
            }
        } else if (sensor_event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            if (checkBoxMagneticField.isChecked()) {
                // 磁気センサーの値を表示
                Date date = new Date();
                String magnetic_field_cvs_line = "M, " + ++counterMagneticField
                        + ", " + date.getTime() + ", "
                        + decimal_format.format(sensor_event.values[0]) + ", "
                        + decimal_format.format(sensor_event.values[1]) + ", "
                        + decimal_format.format(sensor_event.values[2]);
                textViewMagneticField.setText(magnetic_field_cvs_line);
                senderThread.SendMessageByUdp(magnetic_field_cvs_line + "\n");
            }
        } else if (sensor_event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            if (checkBoxOrientation.isChecked()) {
                Date date = new Date();
                String orientation_cvs_line = "O, " + ++counterOrientation
                        + ", " + date.getTime() + ", "
                        + decimal_format.format(sensor_event.values[0]) + ", "
                        + decimal_format.format(sensor_event.values[1]) + ", "
                        + decimal_format.format(sensor_event.values[2]);
                textViewOrientation.setText(orientation_cvs_line);
                senderThread.SendMessageByUdp(orientation_cvs_line + "\n");
            }
        }
    }

    void GetSensorManager(Activity activity_) {
        // センサーマネージャーの生成
        // 本物のセンターを使う場合
        sensorManager = (SensorManager) activity_
                .getSystemService(Context.SENSOR_SERVICE);
        // センサーシミュレータを使う場合
        // sensorManager =
        // SensorManagerSimulator.getSystemService(context,Context.SENSOR_SERVICE);
        // sensorManager.connectSimulator();
    }

    int GetSensorDelay() {
        int sensor_delay;
        if (radioButtonFastest.isChecked()) {
            sensor_delay = SensorManager.SENSOR_DELAY_FASTEST;
        } else if (radioButtonGame.isChecked()) {
            sensor_delay = SensorManager.SENSOR_DELAY_GAME;
        } else if (radioButtonNormal.isChecked()) {
            sensor_delay = SensorManager.SENSOR_DELAY_NORMAL;
        } else if (radioButtonUi.isChecked()) {
            sensor_delay = SensorManager.SENSOR_DELAY_UI;
        } else {
            sensor_delay = SensorManager.SENSOR_DELAY_UI;
        }
        return sensor_delay;
    }

    void RegisterSensorListener() {
        // GetSensorManager();

        try {
            List<Sensor> accelerometer_sensors = sensorManager
                    .getSensorList(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer_sensors.get(0),
                    GetSensorDelay());
        } catch (IndexOutOfBoundsException e) {
            this.checkBoxAccelerometer.setChecked(false);
            this.checkBoxAccelerometer.setClickable(false);
            this.textViewAccelerometer.setText("No accelerometer");
        }
        try {
            List<Sensor> magnetic_field_sensors = sensorManager
                    .getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(this, magnetic_field_sensors.get(0),
                    GetSensorDelay());
        } catch (IndexOutOfBoundsException e) {
            this.checkBoxMagneticField.setChecked(false);
            this.checkBoxMagneticField.setClickable(false);
            this.textViewMagneticField.setText("No magnetic field sensor");
        }
        try {
            List<Sensor> orientation_sensors = sensorManager
                    .getSensorList(Sensor.TYPE_ORIENTATION);
            sensorManager.registerListener(this, orientation_sensors.get(0),
                    GetSensorDelay());
        } catch (IndexOutOfBoundsException e) {
            this.checkBoxOrientation.setChecked(false);
            this.checkBoxOrientation.setClickable(false);
            this.textViewOrientation.setText("No orientation sensor");
        }
        // SensorManager.SENSOR_DELAY_FASTEST 最高速度
        // SensorManager.SENSOR_DELAY_GAME ゲーム速度
        // SensorManager.SENSOR_DELAY_NORMAL 通常速度
        // SensorManager.SENSOR_DELAY_UI UI速度
    }

//    private void UnregisterSensorListener() {
//        sensorManager.unregisterListener(this);
//    }
}
