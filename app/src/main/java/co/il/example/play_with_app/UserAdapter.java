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

public class UserAdapter extends ArrayAdapter<User> {

    Context context;
    List<User> users;


    public UserAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<User> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.users = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        // get the layout
        @SuppressLint("ViewHolder") View view = layoutInflater.inflate(R.layout.user_item, parent,false);
        TextView tvUsername = (TextView)view.findViewById(R.id.tvUsername);
        TextView tvCity = (TextView)view.findViewById(R.id.tvCity);
        TextView tvLevel = (TextView)view.findViewById(R.id.tvLevel);
        TextView tvGame =(TextView) view.findViewById(R.id.tvGame);
        // set details to user details
        User user = users.get(position);
        tvUsername.setText(user.getUsername());
        tvCity.setText(user.getCity());
        tvLevel.setText(user.getLevel());
        tvGame.setText(user.getGame());

        return view;
    }
}
