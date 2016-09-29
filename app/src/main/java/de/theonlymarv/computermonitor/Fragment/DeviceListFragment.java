package de.theonlymarv.computermonitor.Fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.theonlymarv.computermonitor.Adapter.DeviceListAdapter;
import de.theonlymarv.computermonitor.Interfaces.OnNetworkAccess;
import de.theonlymarv.computermonitor.R;
import de.theonlymarv.computermonitor.Remote.WebServer.Device;
import de.theonlymarv.computermonitor.Remote.WebServer.Request;
import de.theonlymarv.computermonitor.Remote.WebServer.ServerConnection;
import de.theonlymarv.computermonitor.Remote.WebServer.Status;
import de.theonlymarv.computermonitor.Remote.WebServer.Usage;
import de.theonlymarv.computermonitor.RuntimeHolder;
import de.theonlymarv.computermonitor.Utility;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceListFragment extends Fragment implements DeviceListAdapter.OnItemClickListener {

    private static final String TAG = DeviceListFragment.class.getSimpleName();
    private View view;
    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RuntimeHolder runtimeHolder;

    private int finishingCounter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_device_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        emptyTextView = (TextView) view.findViewById(R.id.emptyTextView);

        runtimeHolder = RuntimeHolder.getInstance();

        if (runtimeHolder.getDeviceList() != null) {
            downloadFinish(runtimeHolder.getDeviceList().size());
        } else {
            downloadDevices(Utility.getFromPrefs(getContext(), Utility.PREFS_TOKEN_KEY, ""));
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadDevices(Utility.getFromPrefs(getContext(), Utility.PREFS_TOKEN_KEY, ""));
            }
        });

        getActivity().setTitle(R.string.app_name);

        return view;
    }

    private void downloadDevices(final String token) {

        runtimeHolder.setDeviceList(new ArrayList<Device>());
        finishingCounter = 0;

        Request request = new Request(Request.Action.LOAD_DEVICE, Request.getLoadDevicesUrl(token));
        ServerConnection serverConnection = new ServerConnection(new OnNetworkAccess() {
            @Override
            public void OnSuccessful(Object object) {
                if (object instanceof List<?>) {
                    if (((List<Device>) object).size() != 0) {
                        runtimeHolder.getDeviceList().addAll((List<Device>) object);
                        for (Device device : runtimeHolder.getDeviceList()) {
                            downloadUsage(token, device);
                        }
                        if (runtimeHolder.getDeviceList().size() == 0) {
                            showProgressbar(false);
                        }
                    } else {
                        downloadFinish(finishingCounter);
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

    private void downloadUsage(String token, final Device device) {
        Request request = new Request(Request.Action.LOAD_USAGE, Request.getLoadUsageUrl(token, device.getId()));
        ServerConnection serverConnection = new ServerConnection(new OnNetworkAccess() {
            @Override
            public void OnSuccessful(@NonNull Object object) {
                if (object instanceof List<?>) {
                    List<Usage> usages = (List<Usage>) object;
                    device.setUsageList(usages);
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
        if (i == runtimeHolder.getDeviceList().size()) {
            showProgressbar(false);

            DeviceListAdapter deviceListAdapter = new DeviceListAdapter(runtimeHolder.getDeviceList(), this);
            recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 1));
            recyclerView.setAdapter(deviceListAdapter);

            if (runtimeHolder.getDeviceList().size() == 0) {
                emptyTextView.setVisibility(View.VISIBLE);
            } else {
                emptyTextView.setVisibility(View.GONE);
            }
        }
    }

    private void showProgressbar(boolean visible) {
        swipeRefreshLayout.setRefreshing(visible);
        progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemClick(Device device) {
        Log.i(TAG, "onItemClick: " + device.toString());
        DeviceFragment deviceFragment = new DeviceFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DeviceFragment.DEVICE_ID_KEY, device.getId());
        bundle.putString(DeviceFragment.DEVICE_NAME_KEY, device.getName());
        deviceFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.mainFrameLayout, deviceFragment).addToBackStack("DevFrag").commit();
    }

    @Override
    public void onLongItemClick(final Device device) {
        Log.i(TAG, "onItemLongClick: " + device.toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.delete_dialog_title);
        builder.setMessage(R.string.delete_device_dialog_message);
        builder.setPositiveButton(R.string.delete_dialog_positiv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new ServerConnection(new OnNetworkAccess() {
                    @Override
                    public void OnSuccessful(@NonNull Object object) {
                        if (object instanceof Status) {
                            Status status = (Status) object;
                            if (status.isStatus()) {
                                showProgressbar(true);
                                downloadDevices(Utility.getFromPrefs(getContext(), Utility.PREFS_TOKEN_KEY, ""));
                                Utility.ShowSnackBarOnMainActivity(getActivity(), R.string.delete_device_successful, Snackbar.LENGTH_LONG);
                            } else {
                                Utility.ShowSnackBarOnMainActivity(getActivity(), status.getMessage(), Snackbar.LENGTH_LONG);
                            }
                            Log.i(TAG, "deleteClick: " + device.getName() + " deleted");
                        }
                    }

                    @Override
                    public void OnError(Exception e) {
                        Utility.ShowSnackBarOnMainActivity(getActivity(), "Unkown Error", Snackbar.LENGTH_LONG);
                    }
                }).execute(new Request(Request.Action.DELETE_DEVICE, Request.getDeleteDeviceUrl(Utility.getFromPrefs(getContext(), Utility.PREFS_TOKEN_KEY, ""), device.getId())));
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, null);
        builder.create().show();
    }

    private void showSnackBar(@StringRes int textRes, @Snackbar.Duration int duration) {
        Snackbar.make(view.getRootView(), textRes, duration).show();
    }
}