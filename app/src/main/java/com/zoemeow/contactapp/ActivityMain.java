package com.zoemeow.contactapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.SearchView;

import com.zoemeow.contactapp.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class ActivityMain extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ArrayList<Contact> contacts;
    private ContactsAdapter contactsAdapter;
    private DatabaseClient databaseClient;
    private ContactsDao contactsDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set up activity.
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setTitle("Contacts [Demo App]");

        // Set up database.
        databaseClient = DatabaseClient.getInstance(this);
        contactsDao = databaseClient.getAppDatabase().contactDao();

        // Set layout for recycler view
        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));

        contactsAdapter = new ContactsAdapter(contacts, new ContactsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Contact item) {
                // String text = String.format("ID = %d, Name: %s, Mobile: %s, Email: %s", item.getId(), item.getName(), item.getMobile(), item.getEmail());
                // Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ActivityMain.this, ActivityContactView.class);
                intent.putExtra("id", item.getId());
                rL.launch(intent);

                if (searchView != null)
                    searchView.setQuery("", false);
            }
        });

        binding.recycleView.setAdapter(contactsAdapter);

        refreshData();

        // Set onClick listener.
        binding.floatingActionButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(ActivityMain.this, ActivityContactModify.class);
            rL.launch(intent);
        });
    }

    // https://c1ctech.com/android-recyclerview-onitemclicklistener-example/

    ActivityResultLauncher<Intent> rL = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    refreshData();
                }
            }
    );


    private void refreshData() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                contacts = (ArrayList<Contact>) contactsDao.getAllContacts();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contactsAdapter.filterList(contacts);
                        contactsAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        return true;
    }

    private void filter(String query) {
        ArrayList<Contact> filteredlist = new ArrayList<>();

        if (!query.isEmpty() && query.length() > 0) {
            filteredlist = new ArrayList<>();
            for (Contact item : contacts) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredlist.add(item);
                }
            }
        }
        else filteredlist = this.contacts;

        contactsAdapter.filterList(filteredlist);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                contactsAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}