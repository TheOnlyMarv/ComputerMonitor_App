package de.theonlymarv.computermonitor.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.theonlymarv.computermonitor.Interfaces.OnNetworkAccess;
import de.theonlymarv.computermonitor.R;
import de.theonlymarv.computermonitor.Remote.WebServer.Request;
import de.theonlymarv.computermonitor.Remote.WebServer.ServerConnection;
import de.theonlymarv.computermonitor.Remote.WebServer.Usage;
import de.theonlymarv.computermonitor.RuntimeHolder;
import de.theonlymarv.computermonitor.Utility;

/**
 * Created by Marvin on 30.08.2016 for ComputerMonitor.
 */
public class DeviceFragment extends Fragment {
    private static final String TAG = DeviceFragment.class.getSimpleName();
    public static final String DEVICE_ID_KEY = "devIdKey";
    public static final String DEVICE_NAME_KEY = "devNameKey";

    private View view;
    private int deviceId;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BarChart barChart;
    private TextView emptyText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_device, container, false);
        deviceId = getArguments().getInt(DEVICE_ID_KEY, -1);
        getActivity().setTitle(getArguments().getString(DEVICE_NAME_KEY, ""));

        barChart = (BarChart) view.findViewById(R.id.barChart);
        emptyText = (TextView) view.findViewById(R.id.tvEmpty);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startLoadingUsageData();
            }
        });

        if (RuntimeHolder.getInstance().getUsagesList() != null) {
            List<Usage> usageList = Utility.getUsageWithId(deviceId, RuntimeHolder.getInstance().getUsagesList());
            if (usageList.size() != 0) {
                assignUsageList();
                setProgressBarVisibility(false);
            } else {
                startLoadingUsageData();
            }
        } else {
            startLoadingUsageData();
        }

        return this.view;
    }

    private void setProgressBarVisibility(boolean visibility) {
        progressBar.setVisibility(visibility ? View.VISIBLE : View.GONE);
        swipeRefreshLayout.setRefreshing(visibility);
    }

    private void startLoadingUsageData() {
        setProgressBarVisibility(true);
        ServerConnection serverConnection = new ServerConnection(new OnNetworkAccess() {
            @Override
            public void OnSuccessful(@NonNull Object object) {
                if (object instanceof List<?>) {
                    List<Usage> usageList = (List<Usage>) object;
                    assignUsageList(usageList);
                } else {
                    // TODO Access denied
                }
                setProgressBarVisibility(false);
            }

            @Override
            public void OnError(final Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "onNetworkException: ", e);
                        setProgressBarVisibility(false);
                    }
                });
            }
        });
        String token = Utility.getFromPrefs(getContext(), Utility.PREFS_TOKEN_KEY, "");
        Request request = new Request(Request.Action.LOAD_USAGE, Request.getLoadUsageUrl(token, deviceId));
        serverConnection.execute(request);
    }

    private void assignUsageList() {
        assignUsageList(null);
    }

    @SuppressLint("SimpleDateFormat")
    private void assignUsageList(@Nullable List<Usage> usageList) {
        if (usageList == null) {
            usageList = Utility.getUsageWithId(deviceId, RuntimeHolder.getInstance().getUsagesList());
        }

        if (usageList.size() == 0) {
            showEmptiness(true);
            return;
        } else {
            showEmptiness(false);
        }
        //Collections.sort(usageList);

        int anzEntries = usageList.size() >= 5 ? 5 : usageList.size();
        int startIndex = usageList.size() - anzEntries;


        ArrayList<BarEntry> downloadValues = new ArrayList<>();
        ArrayList<BarEntry> uploadValues = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = startIndex; i < usageList.size(); i++) {
            Usage usage = usageList.get(i);
            downloadValues.add(new BarEntry(usage.getDownload() / 1024, i - startIndex));
            uploadValues.add(new BarEntry(usage.getUpload() / 1024, i - startIndex));
            labels.add(new SimpleDateFormat("dd.MM").format(usage.getDate()));
        }

        BarDataSet set1, set2;
        set1 = new BarDataSet(downloadValues, "Download in MB");
        set2 = new BarDataSet(uploadValues, "Upload in MB");
        set1.setColor(Color.rgb(104, 241, 175));
        set2.setColor(Color.rgb(164, 228, 251));

        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);


        YAxis yl = barChart.getAxisLeft();
        yl.setEnabled(false);

        BarData data = new BarData(labels, dataSets);

        Legend l = barChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);

        barChart.setData(data);
        barChart.animateY(2000);
        barChart.setDescription("");
        barChart.setTouchEnabled(false);

    }

    private void showEmptiness(boolean show) {
        barChart.setVisibility(show ? View.GONE : View.VISIBLE);
        emptyText.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
