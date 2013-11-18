package jp.ac.ehime_u.cite.sasaki.SensorUdp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SensorUdp extends Activity {
    MySensorEventListener mySensorEventListener;
    MyLocationListener myLocationListener;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_udp); // ビューの生成
        mySensorEventListener = MySensorEventListener.GetSingleton(this);
        myLocationListener = MyLocationListener.GetSingleton(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menu_infrator = getMenuInflater();
        menu_infrator.inflate(R.menu.common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
        case R.id.itemSensorSettings: {
            Intent intent = new Intent(this, SensorUdp.class);
            startActivity(intent);
            return true;
        }
        case R.id.itemTransmissionSettings: {
            Intent intent = new Intent(this, TransmissionSettings.class);
            startActivity(intent);
            return true;
        }
        case R.id.itemAbout: {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
            return true;
        }
        }
        return false;
    }
}
