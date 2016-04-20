package csi.fhict.org.wastedtime;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class Settings extends AppCompatActivity {
    public static boolean RotationLocked;
    public static boolean ShakeShuffleEnabled;
    private Activity self = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (new Boolean(MyUtility.getStringFromPreferences(Homepage.getSelf(), new Boolean(false).toString(), "rotationLock")))
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        Boolean b = true;
        ShakeShuffleEnabled = new Boolean(MyUtility.getStringFromPreferences(Homepage.getSelf(), new Boolean(true).toString(), "shakeShuffle"));
        RotationLocked = new Boolean(MyUtility.getStringFromPreferences(Homepage.getSelf(), new Boolean(false).toString(), "rotationLock"));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Switch switchShakeShuffle = (Switch) findViewById(R.id.switchShakeShuffle);
        Switch switchRotationLock = (Switch) findViewById(R.id.switchRotationLock);
        switchShakeShuffle.setChecked(ShakeShuffleEnabled);
        switchRotationLock.setChecked(RotationLocked);
        switchShakeShuffle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyUtility.putStringInPreferences(Homepage.getSelf(), new Boolean(isChecked).toString(), "shakeShuffle");
                ShakeShuffleEnabled = isChecked;
            }
        });
        switchRotationLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyUtility.putStringInPreferences(Homepage.getSelf(), new Boolean(isChecked).toString(), "rotationLock");
                RotationLocked = isChecked;
                if (isChecked)
                {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    Homepage.getSelf().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                else
                {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    Homepage.getSelf().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            }
        });
    }
}
