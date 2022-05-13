package com.zoemeow.contactapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.zoemeow.contactapp.databinding.ActivityContactmodifyBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ActivityContactModify extends AppCompatActivity {

    private ActivityContactmodifyBinding binding;
    private DatabaseClient databaseClient = null;
    private ContactsDao contactsDao = null;
    private Contact contact;
    private Boolean isNew = true;
    private Integer id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactmodifyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setTitle("New Contact");

        // Set up database.
        databaseClient = DatabaseClient.getInstance(getApplicationContext());
        contactsDao = databaseClient.getAppDatabase().contactDao();

        refreshData();

        binding.btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        contact = new Contact(
                                binding.editTextName.getText().toString(),
                                binding.editTextMobile.getText().toString(),
                                binding.editTextEmail.getText().toString()
                        );

                        try {
                            BitmapDrawable bitmapDrawable = ((BitmapDrawable) binding.iv.getDrawable());
                            Bitmap bitmap = bitmapDrawable.getBitmap();
                            if (bitmap != null) {
                                byte[] byteArray = ImageProcessing.convert(bitmap);
                                contact.setAvatar(byteArray);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (!isNew) {
                            contact.setId(id);
                            contactsDao.update(contact);
                        }
                        else contactsDao.insertAll(contact);

                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        });

        binding.btnAvatarChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 100);
            }
        });
    }

    // https://www.tutorialspoint.com/how-to-pick-an-image-from-image-gallery-in-android

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100){
            try {
                Uri imageUri = data.getData();
                Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                Bitmap resized = Bitmap.createScaledBitmap(bmp, 64, 64, true);

                binding.iv.setImageBitmap(resized);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
                                id = contact.getId();
                                Bitmap bmp = ImageProcessing.convert(contact.getAvatar());
                                if (bmp != null)
                                    binding.iv.setImageBitmap(bmp);
                                binding.editTextName.setText(contact.getName());
                                binding.editTextMobile.setText(contact.getMobile());
                                binding.editTextEmail.setText(contact.getEmail());
                            }
                        });

                        isNew = false;
                        setTitle("Edit contact");
                    }
                }
            }
        });
    }
}