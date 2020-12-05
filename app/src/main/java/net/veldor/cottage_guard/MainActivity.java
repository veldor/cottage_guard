package net.veldor.cottage_guard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import net.veldor.cottage_guard.ui.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    private TextView mSocketStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
        setupObservers();
    }

    private void setupUI() {
        mSocketStatusView = findViewById(R.id.socketConnectionStatus);
    }

    private void setupObservers() {
        LiveData<Boolean> ld = App.getInstance().mLiveSocketConnected;
        ld.observe(this, state -> {
            Log.d("surprise", "MainActivity setupObservers 33: HAVE event " + state);
            if(state){
                mSocketStatusView.setText(getString(R.string.socket_connected_message));
            }
            else{
                mSocketStatusView.setText(getString(R.string.socket_connection_lost_message));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    public void openSettings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}