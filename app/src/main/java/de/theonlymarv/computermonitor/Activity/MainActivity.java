package de.theonlymarv.computermonitor.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import de.theonlymarv.computermonitor.Database.ConnectionRepo;
import de.theonlymarv.computermonitor.Dialogs.ChooseConnectionDialog;
import de.theonlymarv.computermonitor.Interfaces.ChooseDialogEvents;
import de.theonlymarv.computermonitor.Interfaces.WebSocketEvents;
import de.theonlymarv.computermonitor.Models.Connection;
import de.theonlymarv.computermonitor.R;
import de.theonlymarv.computermonitor.Remote.WebSocket.Action;
import de.theonlymarv.computermonitor.Remote.WebSocket.Remote;
import de.theonlymarv.computermonitor.Remote.WebSocket.RemoteResponse;
import de.theonlymarv.computermonitor.WebSocket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private WebSocket webSocket;
    private SeekBar seekBar;
    private FloatingActionButton fab, fab1, fab2;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private Boolean isFabOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fabCamera);
        fab2 = (FloatingActionButton) findViewById(R.id.fabList);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);


        seekBar = (SeekBar)findViewById(R.id.seekBar);
        assert seekBar != null;
        seekBar.setEnabled(false);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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
        integrator.setPrompt(getResources().getString(R.string.scanner_qr));
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
                openWebSocketConnection(result.getContents());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openWebSocketConnection(String url) {
        if (webSocket != null && webSocket.isConnected()) {
            webSocket.closeConnection();
        }

        String title = getResources().getString(R.string.dialog_please_wait);
        String message = getResources().getString(R.string.dialog_try_to_connect);
        final ProgressDialog progressDialog = ProgressDialog.show(this, title, message, true, false);

        webSocket = new WebSocket(this, new WebSocketEvents() {
            @Override
            public void onMessage(Remote remote) {
                if (remote instanceof RemoteResponse) {
                    RemoteResponse rr = (RemoteResponse) remote;
                    if (rr.getStatus() == 100) {
                        ConnectionRepo repo = new ConnectionRepo(MainActivity.this);
                        repo.insertConnection(new Connection(rr.getMessage(), webSocket.getUrl()));
                    }
                }
            }

            @Override
            public void onOpened() {
                fab1.setImageResource(R.drawable.disconnect);
                animateFAB();
                progressDialog.cancel();
                progressDialog.dismiss();
                seekBar.setEnabled(true);
            }

            @Override
            public void onClosed() {
                fab1.setImageResource(R.drawable.camera);
                seekBar.setEnabled(false);
            }

            @Override
            public void onError(String error) {
                View view = findViewById(R.id.rootLayout);
                assert view != null;
                Snackbar.make(view, error, Snackbar.LENGTH_LONG).show();
                progressDialog.cancel();
                progressDialog.dismiss();
            }
        }, url);
    }

    @Override
    protected void onDestroy() {
        if (webSocket != null){
            webSocket.closeConnection();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:

                animateFAB();
                break;
            case R.id.fabCamera:

                if (webSocket == null || !webSocket.isConnected()) {
                    onCameraClick();
                } else {
                    onCancelClick();
                }
                break;
            case R.id.fabList:

                showConnectionList();
                break;
        }
    }

    private void showConnectionList() {
        new ChooseConnectionDialog(this, new ChooseDialogEvents<Connection>() {
            @Override
            public void OnChoose(Connection connection) {
                openWebSocketConnection(connection.getUrl());
            }

            @Override
            public void OnEmptyChooseList() {
                View view = findViewById(R.id.rootLayout);
                assert view != null;
                Snackbar.make(view, R.string.chooser_empty, Snackbar.LENGTH_LONG).show();
            }
        }).ShowDialog();
    }

    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;

        }
    }
}
