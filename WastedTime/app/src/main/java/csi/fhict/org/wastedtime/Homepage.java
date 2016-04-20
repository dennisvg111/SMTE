package csi.fhict.org.wastedtime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.Character.getType;

public class Homepage extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private float oldAX = 9001, oldAY = 9001, oldAZ = 9001;
    private ArrayList<TextView> URLlist;
    private long lastShuffle = 0;
    private Activity self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        URLlist = new ArrayList<TextView>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = clipboard.getPrimaryClip();
                if (clip != null) {

                    // Gets the first item from the clipboard data
                    ClipData.Item item = clip.getItemAt(0);

                    // Tries to get the item's contents as a URI
                    String pasteUri = "" + item.getText();
                    if (pasteUri != null) {
                        Log.d("values", pasteUri);
                        addURL(pasteUri);
                        MyUtility.addFavoriteItem(self, pasteUri);
                    }
                }
            }
        });

        LinearLayout sv = (LinearLayout) findViewById(R.id.mainScrollView);
        makeChildrenTextViewsClickable(sv);

        String[] sites = MyUtility.getFavoriteList(this);
        for (String url : sites)
        {
            addURL(url);
        }

        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void addURL(String url) {
        LinearLayout sv = (LinearLayout) findViewById(R.id.mainScrollView);
        LinearLayout child = new LinearLayout(this);
        child.setOrientation(LinearLayout.HORIZONTAL);
        TextView grandChild1 = new TextView(this);
        grandChild1.setText(url);
        child.addView(grandChild1);
        sv.addView(child);
        makeChildrenTextViewsClickable(sv);
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
        if (v instanceof  TextView) {
            final TextView tv = (TextView) v;
            tv.setPadding(0,0,0,10);
            if (URLlist == null)
            {
                URLlist = new ArrayList<TextView>();
            }
            if (!URLlist.contains(tv)) {
                URLlist.add(tv);
                tv.setLongClickable(true);
                tv.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent intent = new Intent(Homepage.this, viewSite.class);
                        intent.putExtra("URL", tv.getText());
                        startActivity(intent);
                    }
                });
                tv.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View view) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        if (MyUtility.removeFavoriteItem(self, ""+tv.getText()))
                                        {
                                            ((ViewGroup)tv.getParent()).removeView(tv);
                                        }
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(self);
                        builder.setMessage("Delete this URL?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
                        return true;
                    }
                });
            }
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
                long time = System.currentTimeMillis();
                if (time - lastShuffle > 3000)
                {
                    Log.d("values", "Max: " + maxD);
                    int URLint = new Random().nextInt(URLlist.size());
                    Intent intent = new Intent(Homepage.this, viewSite.class);
                    intent.putExtra("URL", URLlist.get(URLint).getText());
                    startActivity(intent);
                    lastShuffle = time;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

abstract class MyUtility {

    public static boolean addFavoriteItem(Activity activity,String favoriteItem){
        //Get previous favorite items
        String favoriteList = getStringFromPreferences(activity,null,"favorites");
        // Append new Favorite item
        if(favoriteList!=null && favoriteList != ""){
            favoriteList = favoriteList+","+favoriteItem;
        }else{
            favoriteList = favoriteItem;
        }
        // Save in Shared Preferences
        return putStringInPreferences(activity,favoriteList,"favorites");
    }
    public static boolean removeFavoriteItem(Activity activity,String favoriteItem){
        //Get previous favorite items
        String favoriteList = getStringFromPreferences(activity,null,"favorites");
        // Append new Favorite item
        if(favoriteList!=null){
            String[] favoriteArray = convertStringToArray(favoriteList);
            if (favoriteArray.length > 0)
            {
                List<String> list = new ArrayList<String>(Arrays.asList(favoriteArray));
                list.remove(favoriteItem);
                boolean first = false;
                favoriteList = "";
                for (String item : list)
                {
                    if (!first)
                    {
                        favoriteList += ",";
                    }
                    favoriteList += item;
                }
                // Save in Shared Preferences
                return putStringInPreferences(activity, favoriteList,"favorites");
            }
        }
        return false;
    }
    public static String[] getFavoriteList(Activity activity){
        String favoriteList = getStringFromPreferences(activity,null,"favorites");
        return convertStringToArray(favoriteList);
    }
    private static boolean putStringInPreferences(Activity activity,String nick,String key){
        SharedPreferences sharedPreferences = activity.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, nick);
        editor.commit();
        return true;
    }
    private static String getStringFromPreferences(Activity activity,String defaultValue,String key){
        SharedPreferences sharedPreferences = activity.getPreferences(Activity.MODE_PRIVATE);
        String temp = sharedPreferences.getString(key, defaultValue);
        return temp;
    }

    private static String[] convertStringToArray(String str) {
        if (str == null || str == "") {
            return new String[0];
        }
        String[] arr = str.split(",");
        return arr;
    }
}