package com.zoemeow.contactapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactsDao {
    @Query("SELECT * FROM Contact")
    List<Contact> getAllContacts();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Contact contact);

    @Query("SELECT * FROM Contact WHERE name LIKE :name")
    List<Contact> getAllContactsLikeName(String name);

    @Query("SELECT * FROM Contact WHERE id = :id LIMIT 1")
    Contact getContactById(Integer id);

    @Update
    void update(Contact... contacts);
}
