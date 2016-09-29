package de.theonlymarv.computermonitor.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.theonlymarv.computermonitor.R;
import de.theonlymarv.computermonitor.Remote.WebServer.Device;
import de.theonlymarv.computermonitor.Remote.WebServer.Usage;

/**
 * Created by Marvin on 24.08.2016 for ComputerMonitor.
 */
public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder> {
    private List<Device> devices;
    private OnItemClickListener onItemClickListener;

    public DeviceListAdapter(List<Device> devices, OnItemClickListener onItemClickListener) {
        this.devices = devices;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_device, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DeviceViewHolder holder, int position) {
        holder.assignData(devices.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(devices.get(holder.getAdapterPosition()));
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClickListener.onLongItemClick(devices.get(holder.getAdapterPosition()));
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    protected class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeviceName, tvLastUsage, tvEmpty;
        HorizontalBarChart horizontalBarChart;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            tvDeviceName = (TextView) itemView.findViewById(R.id.tvDeviceName);
            tvLastUsage = (TextView) itemView.findViewById(R.id.tvLastUsed);
            horizontalBarChart = (HorizontalBarChart) itemView.findViewById(R.id.horizontalBarChart);
            tvEmpty = (TextView) itemView.findViewById(R.id.tvEmpty);
        }

        public void assignData(Device device) {
            Usage usage = device.getUsageList() == null || device.getUsageList().size() == 0 ? null : device.getUsageList().get(device.getUsageList().size() - 1);

            tvDeviceName.setText(device.getName());
            tvLastUsage.setText(device.getLast_used() == null ? " - " : new SimpleDateFormat("dd.MM.yyyy").format(device.getLast_used()));

            if (usage == null) {
                tvEmpty.setVisibility(View.VISIBLE);
                horizontalBarChart.setVisibility(View.INVISIBLE);
                return;
            } else {
                tvEmpty.setVisibility(View.GONE);
                horizontalBarChart.setVisibility(View.VISIBLE);
            }

            ArrayList<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(usage.getDownload() / 1024, 0));
            entries.add(new BarEntry(usage.getUpload() / 1024, 1));

            BarDataSet dataSet = new BarDataSet(entries, "in MB");


            ArrayList<String> labels = new ArrayList<String>();
            labels.add("Download");
            labels.add("Upload");

            BarData data = new BarData(labels, dataSet);
            dataSet.setColors(ColorTemplate.PASTEL_COLORS);
            horizontalBarChart.setData(data);
            horizontalBarChart.animateY(2000);
            horizontalBarChart.setDescription("");
            horizontalBarChart.setTouchEnabled(false);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Device device);

        void onLongItemClick(Device device);
    }
}
