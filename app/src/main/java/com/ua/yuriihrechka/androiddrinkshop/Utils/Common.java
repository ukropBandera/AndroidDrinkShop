package com.ua.yuriihrechka.androiddrinkshop.Utils;

import com.ua.yuriihrechka.androiddrinkshop.Database.DataSource.CartRepository;


import com.ua.yuriihrechka.androiddrinkshop.Database.DataSource.FavoriteRepository;
import com.ua.yuriihrechka.androiddrinkshop.Database.Local.RoomCartDatabase;
import com.ua.yuriihrechka.androiddrinkshop.Model.Category;
import com.ua.yuriihrechka.androiddrinkshop.Model.Drink;
import com.ua.yuriihrechka.androiddrinkshop.Model.User;
import com.ua.yuriihrechka.androiddrinkshop.Retrofit.IDrinkShopAPI;
import com.ua.yuriihrechka.androiddrinkshop.Retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

public class Common {



    public static final String TOPPING_MENU_ID = "3";

    public static User currentUser = null;
    public static Category currentCategory = null;
    public static List<Drink> toppingList = new ArrayList<>();

    public static List<String> toppingAdded = new ArrayList<>();
    public static double toppingPrice = 0.0;

    public static int sizeOfCup = -1; // error
    public static int sugar = -1; // error
    public static int ice = -1; // error


    // database
    public static RoomCartDatabase roomCartDatabase;
    public static CartRepository cartRepository;
    public static FavoriteRepository favoriteRepository;


    public static final String BASE_URL = "http://192.168.0.110/drinkshop/";
    //private static final String BASE_URL = "http://localhost/drinkshop/";
    //private static final String BASE_URL = "http://53.103.50.42/drinkshop/";

    public static IDrinkShopAPI getApiDrinkShop(){
        return RetrofitClient.getClient(BASE_URL).create(IDrinkShopAPI.class);
    }
}
