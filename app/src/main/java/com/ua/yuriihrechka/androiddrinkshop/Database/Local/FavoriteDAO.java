package com.ua.yuriihrechka.androiddrinkshop.Database.Local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;


import com.ua.yuriihrechka.androiddrinkshop.Database.ModelDB.Favorite;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface FavoriteDAO {

    @Query("SELECT * FROM Favorite")
    Flowable<List<Favorite>> getFavItems();

    @Query("SELECT EXISTS (SELECT 1 FROM Favorite WHERE id=:itemId)")
    int isFavorite(int itemId);

    @Insert
    void insertToFavorite(Favorite...favorites);

    @Delete
    void delete(Favorite favorite);

}


