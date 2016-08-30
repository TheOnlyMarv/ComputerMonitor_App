package de.theonlymarv.computermonitor;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import de.theonlymarv.computermonitor.Adapter.DeviceListAdapter;
import de.theonlymarv.computermonitor.Interfaces.OnNetworkAccess;
import de.theonlymarv.computermonitor.Remote.WebServer.Device;
import de.theonlymarv.computermonitor.Remote.WebServer.Request;
import de.theonlymarv.computermonitor.Remote.WebServer.ServerConnection;
import de.theonlymarv.computermonitor.Remote.WebServer.Status;
import de.theonlymarv.computermonitor.Remote.WebServer.Usage;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceListFragment extends Fragment implements DeviceListAdapter.OnItemClickListener {

    private static final String TAG = DeviceListFragment.class.getSimpleName();
    private View view;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<Device> deviceList;
    private List<Usage> usageList;

    private int finishingCounter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_devices, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        downloadDevices(Utility.getFromPrefs(getContext(), Utility.PREFS_TOKEN_KEY, ""));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadDevices(Utility.getFromPrefs(getContext(), Utility.PREFS_TOKEN_KEY, ""));
            }
        });

        return view;
    }

    private void downloadDevices(final String token) {

        deviceList = new ArrayList<>();
        usageList = new ArrayList<>();
        finishingCounter = 0;

        Request request = new Request(Request.Action.LOAD_DEVICE, Request.getLoadDevicesUrl(token));
        ServerConnection serverConnection = new ServerConnection(new OnNetworkAccess() {
            @Override
            public void OnSuccessful(Object object) {
                if (object instanceof List<?>) {
                    deviceList.addAll((List<Device>) object);
                    for (Device device : deviceList) {
                        downloadUsage(token, device);
                    }
                    if (deviceList.size() == 0) {
                        showProgressbar(false);
                    }
                } else if (object instanceof Status) {
                    showProgressbar(false);
                    showAutomatedLoggedOutDialog();
                }
            }

            @Override
            public void OnError(Exception e) {
                Log.e("TAG", "OnError: ", e);
            }
        });
        showProgressbar(true);
        serverConnection.execute(request);
    }

    private void showAutomatedLoggedOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle(R.string.dialog_session_timed_out_title);
        builder.setMessage(R.string.dialog_session_timed_out_message);
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Utility.logout(getActivity());
            }
        });
        builder.create().show();
    }

    private void downloadUsage(String token, Device device) {
        Request request = new Request(Request.Action.LOAD_USAGE, Request.getLoadUsageUrl(token, device.getId()));
        ServerConnection serverConnection = new ServerConnection(new OnNetworkAccess() {
            @Override
            public void OnSuccessful(Object object) {
                if (object instanceof List<?>) {
                    List<Usage> usages = (List<Usage>) object;
                    if (usages.size() > 0) {
                        usageList.add(usages.get(usages.size() - 1));
                    } else {
                        usageList.add(null);
                    }
                    downloadFinish(++finishingCounter);
                }
            }

            @Override
            public void OnError(Exception e) {

            }
        });
        showProgressbar(true);
        serverConnection.execute(request);
    }

    private void downloadFinish(int i) {
        if (i == deviceList.size()) {
            showProgressbar(false);
            DeviceListAdapter deviceListAdapter = new DeviceListAdapter(deviceList, usageList, this);
            recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 1));
            recyclerView.setAdapter(deviceListAdapter);
        }
    }

    private void showProgressbar(boolean visible) {
        swipeRefreshLayout.setRefreshing(visible);
        progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemClick(Device device) {
        Log.i(TAG, "onItemClick: " + device.toString());
    }

    private void showSnackBar(@StringRes int textRes, @Snackbar.Duration int duration) {
        Snackbar.make(view.getRootView(), textRes, duration).show();
    }
}