package co.il.example.play_with_app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;


public class HistoryFragment extends Fragment {

    ListView listView;

    ArrayList<History> historyList;
    HistoryAdapter historyAdapter;
    History lastSelected;
    ProgressDialog progressDialog;


    SharedPreferences sp;

    public HistoryFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_history, container, false);
        listView = (ListView) root.findViewById(R.id.historyList);
        // get shared preference chat_history
        sp = getActivity().getSharedPreferences("chat_history", 0);

        // messages keys
        String keys = Arrays.toString(sp.getAll().entrySet().toArray());
        keys = keys.substring(1, keys.length() - 1);
        String[] arr = keys.split(",");
        System.out.println(Arrays.toString(arr));

        historyList = new ArrayList<History>();
        System.out.println(arr.length);
        if (!arr[0].equals("")) {
            for (int i = 0; i < arr.length; i++) {
                // get the username and last message
                String name = arr[i].substring(arr[i].indexOf('_') + 1, arr[i].indexOf('='));
                String msg = arr[i].substring(arr[i].indexOf('=') + 1, arr[i].length() - 1);
                String[] arrMsg = msg.split("@");
                String lastMessage = arrMsg[arrMsg.length - 1];
                String stat = lastMessage.substring(lastMessage.length() - 1);
                lastMessage = lastMessage.substring(0, lastMessage.length() - 1);
                if (stat.equals("0")) {
                    History h1 = new History(name, "you: " + lastMessage);
                    historyList.add(h1);
                }
                if (stat.equals("1")) {
                    History h1 = new History(name, name + ": " + lastMessage);
                    historyList.add(h1);
                }
            }
        }


        historyAdapter = new HistoryAdapter(getContext(), 0, 0, historyList);
        listView.setAdapter(historyAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastSelected = historyAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), Chat_Activity.class);
                // pass the username to chat activity
                intent.putExtra("Username", lastSelected.getUsername());
                // create progress dialog while client connect to server
                progressDialog = ProgressDialog.show(getContext(), "Please wait", "");
                progressDialog.setCancelable(true);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                // start chat activity
                startActivity(intent);
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                lastSelected = historyAdapter.getItem(position);
                String username = lastSelected.getUsername().replace(' ', '_');
                System.out.println(username);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Delete Chat");
                builder.setMessage("Are you sure you want to delete the chat with " + username + "?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new HistoryFragment.HandleAlertDialogListener(username, lastSelected));
                builder.setNegativeButton("No", new HistoryFragment.HandleAlertDialogListener(username, lastSelected));
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });

        return root;
    }

    public class HandleAlertDialogListener implements DialogInterface.OnClickListener{

        String username;
        History lastSelected;

        public HandleAlertDialogListener (String username, History lastSelected){
            this.username = username;
            this.lastSelected = lastSelected;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Log.d("maor", String.valueOf(which));
            if (which == -1) {
                sp.edit().remove("Messages_" + username).commit();
                historyAdapter.remove(lastSelected);
                historyAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        }
    }



}