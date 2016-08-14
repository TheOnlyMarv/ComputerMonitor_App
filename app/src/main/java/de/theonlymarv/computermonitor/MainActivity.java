package de.theonlymarv.computermonitor;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import de.theonlymarv.computermonitor.Database.ConnectionRepo;
import de.theonlymarv.computermonitor.Interfaces.WebSocketEvents;
import de.theonlymarv.computermonitor.Models.Connection;
import de.theonlymarv.computermonitor.RemoteClasses.Action;
import de.theonlymarv.computermonitor.RemoteClasses.Remote;
import de.theonlymarv.computermonitor.RemoteClasses.RemoteResponse;

public class MainActivity extends AppCompatActivity implements WebSocketEvents, View.OnClickListener {
    WebSocket webSocket;
    SeekBar seekBar;
    FloatingActionButton fabQr;
    private FloatingActionButton fab, fab1, fab2;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private Boolean isFabOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

//        fabQr = (FloatingActionButton)findViewById(R.id.fabQr);
//        assert fabQr != null;
//        fabQr.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onCameraClick();
//            }
//        });

        seekBar = (SeekBar)findViewById(R.id.seekBar);
        assert seekBar != null;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Log.i("SeekBar", "onProgressChanged: " + progress);
                if (fromUser){
                    if (webSocket != null){
                        progress = (int)(Math.round(progress / 5d) * 5);
                        sendSeekBarProgress(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void onCameraClick(){
        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan QR-Code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    private void onCancelClick(){
        if (webSocket != null)
            webSocket.closeConnection();
    }

    private void sendSeekBarProgress(int progress){
        if (webSocket != null && webSocket.isConnected())
            webSocket.sendMessage(Action.Volumn, progress);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int progress = (int)(Math.round(seekBar.getProgress() / 5d) * 5);
        switch (keyCode){
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (event.getAction() == KeyEvent.ACTION_DOWN){
                    seekBar.setProgress(progress < 100 ? progress + 5 : progress);
                    sendSeekBarProgress(seekBar.getProgress());
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    seekBar.setProgress(progress > 0 ? progress - 5 : progress);
                    sendSeekBarProgress(seekBar.getProgress());
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() != null){
                webSocket = new WebSocket(this, this, result.getContents());
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (webSocket != null){
            webSocket.closeConnection();
        }
        super.onDestroy();
    }

    @Override
    public void onMessage(Remote remote) {
        if (remote instanceof RemoteResponse){
            RemoteResponse rr = (RemoteResponse)remote;
            if (rr.getStatus() == 100){
                ConnectionRepo repo = new ConnectionRepo(this);
                repo.insertConnection(new Connection(rr.getMessage(), webSocket.getUrl()));
            }
        }
    }

    @Override
    public void onOpened() {
        int imageResource = getResources().getIdentifier("@android:drawable/ic_menu_close_clear_cancel", null, getPackageName());
        Drawable res = getResources().getDrawable(imageResource);
        fabQr.setImageDrawable(res);
        fabQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClick();
            }
        });
        Log.i("OnFabClick", "onClick: cancel now");
    }

    @Override
    public void onClosed() {
        int imageResource = getResources().getIdentifier("@android:drawable/ic_menu_camera", null, getPackageName());
        Drawable res = getResources().getDrawable(imageResource);
        fabQr.setImageDrawable(res);
        fabQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCameraClick();
            }
        });
        Log.i("OnFabClick", "onClick: camera now");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:

                animateFAB();
                break;
            case R.id.fab1:

                Log.d("Raj", "Fab 1");
                break;
            case R.id.fab2:

                Log.d("Raj", "Fab 2");
                break;
        }
    }

    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
            Log.d("Raj", "close");

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
            Log.d("Raj", "open");

        }
    }
}
