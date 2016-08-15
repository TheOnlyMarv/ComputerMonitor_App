package de.theonlymarv.computermonitor.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import java.util.List;

import de.theonlymarv.computermonitor.Database.ConnectionRepo;
import de.theonlymarv.computermonitor.Interfaces.ChooseDialogEvents;
import de.theonlymarv.computermonitor.Models.Connection;

/**
 * Created by Marvin on 15.08.2016.
 */
public class ChooseConnectionDialog {
    private Context context;
    private ChooseDialogEvents<Connection> dialogEvents;
    private int selectedItem = -1;

    public ChooseConnectionDialog(Context context, ChooseDialogEvents<Connection> dialogEvents) {
        this.context = context;
        this.dialogEvents = dialogEvents;
    }

    public void ShowDialog() {
        ConnectionRepo connectionRepo = new ConnectionRepo(context);
        List<Connection> connectionList = connectionRepo.getAllConnections();

        if (connectionList.size() == 0) {
            dialogEvents.OnEmptyChooseList();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Device");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final ArrayAdapter<Connection> adapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_singlechoice);
        adapter.addAll(connectionList);

        builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedItem = which;
            }
        });
        builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectedItem != -1) {
                    dialogEvents.OnChoose(adapter.getItem(selectedItem));
                }
            }
        });
        builder.create().show();
    }
}
