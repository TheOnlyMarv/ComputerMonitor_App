package de.theonlymarv.computermonitor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

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

        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout, new DevicesFragment()).commit();
    }
}
