package de.theonlymarv.computermonitor.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import de.theonlymarv.computermonitor.Fragment.DeviceListFragment;
import de.theonlymarv.computermonitor.R;
import de.theonlymarv.computermonitor.Utility;

/**
 * Created by Marvin on 24.08.2016 for ComputerMonitor.
 */
public class NewMainActivity extends AppCompatActivity {

    private FrameLayout frameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        frameLayout = (FrameLayout) findViewById(R.id.mainFrameLayout);

        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout, new DeviceListFragment()).commit();
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }
        showLogoutDialog();
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_logout_title);
        builder.setMessage(R.string.dialog_logout_message);
        builder.setPositiveButton(R.string.dialog_logout_positiv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Utility.logout(NewMainActivity.this);
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


}
