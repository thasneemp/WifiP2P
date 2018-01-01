package example.muhammed.com.p2p;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.adroitandroid.near.connect.NearConnect;
import com.adroitandroid.near.discovery.NearDiscovery;
import com.adroitandroid.near.model.Host;

import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String MESSAGE_REQUEST_START_CHAT = "start_chat";
    public static final String MESSAGE_RESPONSE_DECLINE_REQUEST = "decline_request";
    public static final String MESSAGE_RESPONSE_ACCEPT_REQUEST = "accept_request";
    private static final String TAG = "TAG";
    private static final long DISCOVERABLE_TIMEOUT_MILLIS = 60000;
    private static final long DISCOVERY_TIMEOUT_MILLIS = 10000;
    private static final long DISCOVERABLE_PING_INTERVAL_MILLIS = 5000;
    private Button mTestButton;
    private NearDiscovery mNearDiscovery;
    private NearConnect mNearConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTestButton = findViewById(R.id.button);


        mTestButton.setOnClickListener(this);


        mNearDiscovery = new NearDiscovery.Builder()
                .setContext(this)
                .setDiscoverableTimeoutMillis(DISCOVERABLE_TIMEOUT_MILLIS)
                .setDiscoveryTimeoutMillis(DISCOVERY_TIMEOUT_MILLIS)
                .setDiscoverablePingIntervalMillis(DISCOVERABLE_PING_INTERVAL_MILLIS)
                .setDiscoveryListener(getNearDiscoveryListener(), Looper.getMainLooper())
                .build();

        mNearConnect = new NearConnect.Builder()
                .fromDiscovery(mNearDiscovery)
                .setContext(this)
                .setListener(getNearConnectListener(), Looper.getMainLooper())
                .build();
        mNearDiscovery.makeDiscoverable("help_test");
        mNearDiscovery.startDiscovery();
        mNearConnect.startReceiving();
    }

    private NearConnect.Listener getNearConnectListener() {
        return new NearConnect.Listener() {
            @Override
            public void onReceive(byte[] bytes, Host host) {
                String data = new String(bytes);
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSendComplete(long l) {

            }

            @Override
            public void onSendFailure(Throwable throwable, long l) {

            }

            @Override
            public void onStartListenFailure(Throwable throwable) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mNearDiscovery.stopDiscovery();
    }

    private NearDiscovery.Listener getNearDiscoveryListener() {
        return new NearDiscovery.Listener() {
            @Override
            public void onPeersUpdate(Set<Host> set) {
                Log.d(TAG, "onPeersUpdate: ");
                mNearConnect.send(MESSAGE_RESPONSE_DECLINE_REQUEST.getBytes(), set.iterator().next());
            }

            @Override
            public void onDiscoveryTimeout() {

            }

            @Override
            public void onDiscoveryFailure(Throwable throwable) {

            }

            @Override
            public void onDiscoverableTimeout() {

            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mNearConnect.startReceiving();
    }
}
