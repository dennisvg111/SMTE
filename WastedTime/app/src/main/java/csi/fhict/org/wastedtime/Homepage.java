package csi.fhict.org.wastedtime;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class Homepage extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private float oldAX = 9001, oldAY = 9001, oldAZ = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        LinearLayout sv = (LinearLayout) findViewById(R.id.mainScrollView);
        makeChildrenTextViewsClickable(sv);

        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void makeChildrenTextViewsClickable(View v)
    {
        if(v instanceof ViewGroup)
        {
            for (int i = 0; i < ((ViewGroup)v).getChildCount(); i++)
            {
                makeChildrenTextViewsClickable(((ViewGroup) v).getChildAt(i));
            }
        }
        if (v instanceof  TextView)
        {
            final TextView tv = (TextView) v;
            tv.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent(Homepage.this, viewSite.class);
                    intent.putExtra("URL", tv.getText());
                    startActivity(intent);
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_homepage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];
            if (oldAX == 9001)
            {
                oldAX = ax;
            }
            if (oldAY == 9001)
            {
                oldAY = ay;
            }
            if (oldAZ == 9001)
            {
                oldAZ = az;
            }
            float dx = Math.abs(Math.abs(ax) - Math.abs(oldAX));
            float dy = Math.abs(Math.abs(ay) - Math.abs(oldAY));
            float dz = Math.abs(Math.abs(az) - Math.abs(oldAZ));
            float maxD = Math.max(Math.max(dx, dy), dz);
            oldAX = ax;
            oldAY = ay;
            oldAZ = az;
            if (maxD > 2.5) {
                Log.d("values", "Max: " + maxD);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
