package co.il.example.play_with_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class HistoryAdapter extends ArrayAdapter<History> {

    Context context;
    List<History> historyList;


    public HistoryAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<History> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.historyList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        // get the layout
        @SuppressLint("ViewHolder") View view = layoutInflater.inflate(R.layout.history_item, parent,false);
        TextView tvUser = (TextView)view.findViewById(R.id.tvUser);
        TextView tvCity = (TextView)view.findViewById(R.id.tvMsg);
        // set details to history details
        History history = historyList.get(position);
        tvUser.setText(history.getUsername());
        tvCity.setText(history.getLastMsg());
        return view;
    }
}
