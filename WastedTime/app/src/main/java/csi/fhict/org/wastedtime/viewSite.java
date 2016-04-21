package csi.fhict.org.wastedtime;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class viewSite extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_site);
        if (new Boolean(MyUtility.getStringFromPreferences(Homepage.getSelf(), new Boolean(false).toString(), "rotationLock")))
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent urlIntent = getIntent(); // gets the previously created intent
        String url = urlIntent.getStringExtra("URL"); // will return "FirstKeyValue"
        WebView wv = (WebView) findViewById(R.id.webView);
        wv.setWebViewClient(new WebViewController());
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_viewsite, menu);
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
            Intent intent = new Intent(viewSite.this, Settings.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_refresh) {
            WebView wv = (WebView) findViewById(R.id.webView);
            wv.loadUrl(wv.getUrl());
            return true;
        }
        if (id == R.id.action_add_current_url) {
            WebView wv = (WebView) findViewById(R.id.webView);
            MyUtility.addFavoriteItem(Homepage.getSelf(), wv.getUrl());
            Homepage.sensorManager.unregisterListener(Homepage.listener);
            Homepage.getSelf().finish();
            Intent intent = new Intent(viewSite.this, Homepage.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

class WebViewController extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}
