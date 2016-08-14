package de.theonlymarv.computermonitor;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import de.theonlymarv.computermonitor.Database.ConnectionRepo;
import de.theonlymarv.computermonitor.Interfaces.WebSocketEvents;
import de.theonlymarv.computermonitor.Models.Connection;
import de.theonlymarv.computermonitor.RemoteClasses.Action;
import de.theonlymarv.computermonitor.RemoteClasses.Remote;
import de.theonlymarv.computermonitor.RemoteClasses.RemoteResponse;

public class MainActivity extends AppCompatActivity implements WebSocketEvents {
    WebSocket webSocket;
    SeekBar seekBar;
    FloatingActionButton fabQr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabQr = (FloatingActionButton)findViewById(R.id.fabQr);
        assert fabQr != null;
        fabQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCameraClick();
            }
        });

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
}
