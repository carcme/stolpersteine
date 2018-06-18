package me.carc.stolpersteine.data.db.blocks;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import me.carc.stolpersteine.data.remote.model.Stolpersteine;

/**
 * Created by bamptonm on 12/05/2018.
 */

@Dao
public interface StolpersteineDao {

    @Query("SELECT * FROM stolpersteine_table LIMIT 1")
    Stolpersteine getAnyBlock();


    @Query("SELECT * FROM stolpersteine_table WHERE " +
            "firstName LIKE :search OR " +
            "firstName LIKE :search OR " +
            "lastName LIKE :search OR " +
            "street LIKE :search OR " +
            "sublocality1 LIKE :search OR " +
            "sublocality2 LIKE :search OR " +
            "zipCode LIKE :search")
    DataSource.Factory<Integer, Stolpersteine> search(String search);


    @Query("SELECT * FROM stolpersteine_table WHERE type IS 'stolperstein' ORDER BY blockId ASC")
    DataSource.Factory<Integer, Stolpersteine> loadPagedList();


    @Query("SELECT * FROM stolpersteine_table WHERE latitude BETWEEN :lat1 AND :lat2 AND longitude BETWEEN :lon1 AND :lon2")
    LiveData<List<Stolpersteine>> getAroundLocation(Double lat1, Double lat2, Double lon1, Double lon2);

    @Query("SELECT * FROM stolpersteine_table WHERE biographyUrl LIKE :biographyUrl LIMIT 1")
    LiveData<Stolpersteine> getStumblingBlock(String biographyUrl);


    @Query("SELECT * FROM stolpersteine_table")
    LiveData<List<Stolpersteine>> getAllStumblingBlocks();

    @Query("SELECT * FROM stolpersteine_table WHERE blockId LIKE :blockId LIMIT 1")
    LiveData<Stolpersteine> getStumblingBlock(int blockId);

    @Query("SELECT * FROM stolpersteine_table")
    List<Stolpersteine> getTestAllStumblingBlocks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Stolpersteine entry);

    @Update
    void update(Stolpersteine entry);

    @Delete
    void delete(Stolpersteine entry);

    @Query("DELETE FROM stolpersteine_table")
    void nukeTable();

}