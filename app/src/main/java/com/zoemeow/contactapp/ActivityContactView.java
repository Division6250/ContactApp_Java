package com.zoemeow.contactapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.zoemeow.contactapp.databinding.ActivityContactviewBinding;

public class ActivityContactView extends AppCompatActivity {

    private ActivityContactviewBinding binding;

    private DatabaseClient databaseClient;
    private ContactsDao contactsDao;

    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactviewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Set up database.
        databaseClient = DatabaseClient.getInstance(getApplicationContext());
        contactsDao = databaseClient.getAppDatabase().contactDao();

        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityContactView.this, ActivityContactModify.class);
                intent.putExtra("id", contact.getId());
                rL.launch(intent);
            }
        });

        refreshData();
    }

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
                Intent intent = getIntent();
                if (intent != null) {
                    Integer position = intent.getIntExtra("id", -1);
                    if (position != -1) {
                        contact = databaseClient.getAppDatabase().contactDao().getContactById(position);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bmp = ImageProcessing.convert(contact.getAvatar());
                                if (bmp != null)
                                    binding.iv.setImageBitmap(bmp);

                                setTitle(contact.getName().toString());
                                binding.editTextMobile.setText(contact.getMobile().toString());
                                binding.editTextEmail.setText(contact.getEmail().toString());
                            }
                        });
                    }
                }

                if (contact == null || intent == null) {
                    finish();
                }
            }
        });
    }
}