package co.il.example.play_with_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Tabs_Activity extends AppCompatActivity {

    // tabs titles
    String[] tabs = {"Friends", "History"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        // reference ViewPager
        ViewPager2 sampleViewPager = findViewById(R.id.viewpager2);
        // set Adapter
        sampleViewPager.setAdapter(
                new SampleAdapter(this)
        );

        // create the tabs
        TabLayout sampleTabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(
                sampleTabLayout,
                sampleViewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(tabs[position]);
                    }
                }
        ).attach();
    }

    // add menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.add_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        int id = item.getItemId();

        // for import contacts
        if (id == R.id.add){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Contacts");
            builder.setMessage("Are you sure you want to add friends from your contacts?");
            builder.setCancelable(true);
            builder.setPositiveButton("Agree", new HandleAlertDialogListener());
            builder.setNegativeButton("Disagree", new HandleAlertDialogListener());
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        // for update
        if (id == R.id.settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return true;
    }

    public class HandleAlertDialogListener implements DialogInterface.OnClickListener{

        Intent intent;

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Log.d("maor", String.valueOf(which));
            if (which == -1) {
                // start contacts activity
                intent = new Intent(Tabs_Activity.this, Contacts_Activity.class);
                startActivity(intent);
            }
        }
    }

    class SampleAdapter extends FragmentStateAdapter {

        public SampleAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        public SampleAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        public SampleAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        // show fragments
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Log.d("maor", String.valueOf(position));
            Fragment fragment = new HistoryFragment();
            switch (position){
                case 0:
                    fragment = new FriendsFragment();
                    break;
                case 1:
                    fragment = new HistoryFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getItemCount() {
            return tabs.length;
        }
    }
}