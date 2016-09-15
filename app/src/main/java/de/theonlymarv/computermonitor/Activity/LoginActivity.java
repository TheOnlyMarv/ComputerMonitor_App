package de.theonlymarv.computermonitor.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.IOException;

import de.theonlymarv.computermonitor.Interfaces.OnNetworkAccess;
import de.theonlymarv.computermonitor.R;
import de.theonlymarv.computermonitor.Remote.WebServer.Request;
import de.theonlymarv.computermonitor.Remote.WebServer.ServerConnection;
import de.theonlymarv.computermonitor.Remote.WebServer.Status;
import de.theonlymarv.computermonitor.Utility;

/**
 * Created by Marvin on 24.08.2016 for ComputerMonitor.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogin, btnRegister;
    private EditText editUsername, editPassword;
    private CheckBox chkStorePassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.activitiy_login);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
        editUsername = (EditText) findViewById(R.id.editUsername);
        editPassword = (EditText) findViewById(R.id.editPassword);
        chkStorePassword = (CheckBox) findViewById(R.id.chkStorePassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        editUsername.setText(Utility.getFromPrefs(this, Utility.PREFS_LOGIN_USERNAME_KEY, ""));
        boolean credentialsSaved = Utility.isCredentialsSaved(this);
        if (credentialsSaved) {
            editPassword.setText(Utility.getFromPrefs(this, Utility.PREFS_LOGIN_PASSWORD_KEY, ""));
        }
        chkStorePassword.setChecked(credentialsSaved);

        String token = Utility.getFromPrefs(this, Utility.PREFS_TOKEN_KEY, "");
        if (token.compareTo("") != 0) {
            successfulLoggedIn(token);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                webLogin();
                break;
            case R.id.btnRegister:
                webRegister();
                break;
        }
    }

    private void setEnableControls(boolean enable) {
        btnLogin.setEnabled(enable);
        btnRegister.setEnabled(enable);
        editUsername.setEnabled(enable);
        editPassword.setEnabled(enable);
        chkStorePassword.setEnabled(enable);
        progressBar.setVisibility(enable ? View.GONE : View.VISIBLE);
    }

    private void showSnackBar(@StringRes int text, @Snackbar.Duration int duration) {
        Snackbar.make(findViewById(R.id.rootLayout), text, duration).show();
    }

    private void webRegister() {
        Request request = new Request(Request.Action.REGISTER, Request.getRegisterUrl(editUsername.getText().toString().trim(), editPassword.getText().toString()));
        ServerConnection serverConnection = new ServerConnection(new OnNetworkAccess() {
            @Override
            public void OnSuccessful(Object object) {
                setEnableControls(true);
                if (object instanceof Status) {
                    Status status = (Status) object;
                    if (status.isStatus()) {
                        webLogin();
                    } else {
                        showSnackBar(R.string.login_register_failed, Snackbar.LENGTH_LONG);
                    }
                } else {
                    showSnackBar(R.string.unkown_error, Snackbar.LENGTH_LONG);
                }
            }

            @Override
            public void OnError(Exception e) {
                if (e instanceof IOException) {
                    showSnackBar(R.string.unkown_network_error, Snackbar.LENGTH_LONG);
                } else {
                    showSnackBar(R.string.unkown_error, Snackbar.LENGTH_LONG);
                }
            }
        });
        setEnableControls(false);
        serverConnection.execute(request);
    }

    private void webLogin() {

        Utility.setCredentialsSaved(this, chkStorePassword.isChecked());
        Utility.saveToPrefs(this, Utility.PREFS_LOGIN_USERNAME_KEY, editUsername.getText().toString());
        if (chkStorePassword.isChecked()) {
            Utility.saveToPrefs(this, Utility.PREFS_LOGIN_PASSWORD_KEY, editPassword.getText().toString());
        } else {
            Utility.saveToPrefs(this, Utility.PREFS_LOGIN_PASSWORD_KEY, "");
        }

        Request request = new Request(Request.Action.LOGIN, Request.getLoginUrl(editUsername.getText().toString().trim(), editPassword.getText().toString()));
        ServerConnection serverConnection = new ServerConnection(new OnNetworkAccess() {
            @Override
            public void OnSuccessful(Object object) {
                setEnableControls(true);
                if (object instanceof Status) {
                    Status status = (Status) object;
                    if (status.isStatus()) {
                        successfulLoggedIn(status.getToken());
                    } else {
                        showSnackBar(R.string.login_wrong, Snackbar.LENGTH_LONG);
                    }
                } else {
                    showSnackBar(R.string.unkown_error, Snackbar.LENGTH_LONG);
                }
            }

            @Override
            public void OnError(Exception e) {
                if (e instanceof IOException) {
                    showSnackBar(R.string.unkown_network_error, Snackbar.LENGTH_LONG);
                } else {
                    showSnackBar(R.string.unkown_error, Snackbar.LENGTH_LONG);
                }
            }
        });
        setEnableControls(false);
        serverConnection.execute(request);
    }

    private void successfulLoggedIn(String token) {
        Utility.saveToPrefs(this, Utility.PREFS_TOKEN_KEY, token);
        startActivity(new Intent(this, NewMainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }
}
