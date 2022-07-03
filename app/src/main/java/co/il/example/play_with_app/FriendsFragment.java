package co.il.example.play_with_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class FriendsFragment extends Fragment {

    ListView lv;

    ArrayList<User> userList;
    UserAdapter userAdapter;
    User lastSelected;
    String strMessage="";
    String email, password, phone, username, gender, game, city, level, date;
    String contacts;
    ProgressDialog progressDialog;


    SharedPreferences sp, sp2;


    public FriendsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friends, container, false);

        lv = (ListView) root.findViewById(R.id.lv);

        // get shared preferences: savedData and fromContacts
        sp = requireContext().getSharedPreferences("savedData", 0);
        sp2 = requireContext().getSharedPreferences("fromContacts", 0);

        contacts = sp2.getString("Contacts", "");

        email = sp.getString("Email", "");
        password = sp.getString("Pass", "");
        phone = sp.getString("Phone", "");
        username = sp.getString("Username", "");
        gender = sp.getString("Gender", "");
        game = sp.getString("Game", "");
        city = sp.getString("City", "");
        level = sp.getString("Level", "");
        date = sp.getString("Date", "");

        // create message for server
        strMessage = username + "," + city + "," + level + "," + game + "3";
        // create users list
        userList = new ArrayList<User>();

        sendData();

        // create user adapter and set the adapter
        userAdapter = new UserAdapter(getContext(), 0, 0, userList);
        lv.setAdapter(userAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastSelected = userAdapter.getItem(position);
                Intent intent = new Intent(requireActivity().getApplication().getApplicationContext(), Chat_Activity.class);
                // pass the username to chat activity
                intent.putExtra("Username", lastSelected.getUsername());
                Log.d("maor", lastSelected.getUsername());
                // create progress dialog while client connect to server
                progressDialog = ProgressDialog.show(getContext(), "Please wait", "");
                progressDialog.setCancelable(true);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                // start chat activity
                startActivity(intent);
            }
        });

        return root;
    }

    private void sendData()
    {
        try {
            String strToSend = strMessage;
            // create SocketTask object
            System.out.println(strToSend);
            SocketTask send1 = new SocketTask(strToSend);
            // string received from server
            String strReceived = send1.execute().get();
            System.out.println(strReceived);
            // display the friends from server and from contacts
            if (strReceived != null){
                String[] arr = strReceived.split("@");
                System.out.println(Arrays.toString(arr));
                for (int i = 0;i < arr.length;i++){
                    String[] a = arr[i].split(",");
                    User u1 = new User(a[0], a[1], a[2], a[3]);
                    userList.add(u1);
                }
                System.out.println(contacts);
                if (!contacts.equals("") && !contacts.equals("falsŝ") && !contacts.equals("false")){
                    String[] arr2 = contacts.split("@");
                    System.out.println(Arrays.toString(arr2));
                    for (int i = 0;i < arr2.length;i++){
                        String[] a = arr2[i].split(",");
                        Arrays.toString(a);
                        User u1 = new User(a[0], a[1], a[2], a[3]);
                        userList.add(u1);
                    }
                }
            }
        }
        catch (ExecutionException | InterruptedException e)
        {
            Log.e("Exception", e.toString());
        }
    }



}