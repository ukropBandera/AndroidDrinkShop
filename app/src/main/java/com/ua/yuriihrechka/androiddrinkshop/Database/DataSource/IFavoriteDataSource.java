package com.ua.yuriihrechka.androiddrinkshop.Database.DataSource;

import com.ua.yuriihrechka.androiddrinkshop.Database.ModelDB.Favorite;

import java.util.List;

import io.reactivex.Flowable;

public interface IFavoriteDataSource {


    Flowable<List<Favorite>> getFavItems();


    int isFavorite(int itemId);


    void insertToFavorite(Favorite...favorites);

    void delete(Favorite favorite);

}
