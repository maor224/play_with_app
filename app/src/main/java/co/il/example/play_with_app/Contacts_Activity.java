package co.il.example.play_with_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class Contacts_Activity extends AppCompatActivity implements ContactsAdapter.OnItemClickListener {

    RecyclerView recyclerView;
    ArrayList<Contact> arrayList = new ArrayList<Contact>();
    ContactsAdapter adapter;
    String serverStr = "", strMessage = "";
    SharedPreferences sp;

    String phoneNumber, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // get shared preference fromContacts
        sp = getSharedPreferences("fromContacts", 0);
        recyclerView = findViewById(R.id.recycler_view);


        checkPermission();
    }

    private void checkPermission() {
        // check if the is a permission to read contacts
        if (ContextCompat.checkSelfPermission(Contacts_Activity.this
                , Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Contacts_Activity.this
                    , new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            getContactList();
        }
    }

    private void  getContactList() {
        // get contacts list from phone
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        System.out.println(uri);

        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        System.out.println(sort);

        Cursor cursor = getContentResolver().query(
                uri, null, null, null, sort
        );

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                // get id and name
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts._ID
                ));

                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                ));

                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                System.out.println(uriPhone);
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                        + " =?";
                Cursor phoneCursor = getContentResolver().query(
                        uriPhone, null, selection
                        , new String[]{id}, null
                );

                if (phoneCursor.moveToNext()) {
                    // get phone number
                    @SuppressLint("Range") String number = phoneCursor.getString(phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    ));

                    // add contact details
                    Contact contact = new Contact();
                    contact.setName(name);
                    contact.setNumber(number);
                    arrayList.add(contact);
                    phoneCursor.close();
                }
            }
            cursor.close();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // set the recyclerView with contacts details
        adapter = new ContactsAdapter(this, arrayList, this::onItemClick);
        recyclerView.setAdapter(adapter);
    }

    // check permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0 && grantResults[0]
                == PackageManager.PERMISSION_GRANTED) {
            getContactList();
        } else {
            Toast.makeText(Contacts_Activity.this, "Permission denied"
                    , Toast.LENGTH_LONG).show();
            checkPermission();
        }
    }

    @Override
    public void onItemClick(int position) {
        Log.d("maor", String.valueOf(position));
        // message for contact
        message = "Hello,\nI want to add you as a friend in 'play with'.\nPlease download the app :)    ";
        phoneNumber = arrayList.get(position).getNumber();
        // message for server
        strMessage = phoneNumber + "4";
        if (sendData()) {
            Toast.makeText(this, arrayList.get(position).getName()
                    + " added to your friends", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, Tabs_Activity.class);
            // start tabs activity
            startActivity(intent);
        }
        else {
            // create alert dialog to select which platform to send the message
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Platform");
            builder.setMessage("Which platform you want to send the invite?");
            builder.setCancelable(true);
            builder.setPositiveButton("SMS", new AlertDialogListener());
            builder.setNegativeButton("Whatsapp", new AlertDialogListener());
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private boolean sendData() {
        try {
            String strToSend = strMessage;
            // create SocketTask object
            SocketTask send1 = new SocketTask(strToSend);
            // string received from server
            String strReceived = send1.execute().get();
            if (!strReceived.equals("false")) {
                // write to fromContacts the added contact
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sp.edit();
                editor.putString("Contacts", sp.getString("Contacts", "") + strReceived);
                editor.apply();
                return true;
            }
        }
        catch (ExecutionException | InterruptedException e) {
            Log.e("Exception", e.toString());
        }
        return false;
    }



    public class AlertDialogListener implements DialogInterface.OnClickListener{

        Intent intent;

        @SuppressLint("QueryPermissionsNeeded")
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // sms
            if (which == -1){
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
                intent.putExtra("sms_body", message);
                startActivity(intent);
            }

            // whatsapp
            if (which == -2){
                PackageManager packageManager = getPackageManager();
                Intent intent = new Intent(Intent.ACTION_VIEW);

                try {
                    String url = "https://api.whatsapp.com/send?phone="+ phoneNumber
                            +"&text=" + URLEncoder.encode(message, "UTF-8");
                    intent.setPackage("com.whatsapp");
                    intent.setData(Uri.parse(url));
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }
}

