package com.ua.yuriihrechka.androiddrinkshop.Retrofit;

import com.ua.yuriihrechka.androiddrinkshop.Model.CheckUserResponse;
import com.ua.yuriihrechka.androiddrinkshop.Model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;



    public interface IDrinkShopAPI {
        @FormUrlEncoded
        @POST("checkuser.php")
        Call<CheckUserResponse> checkUserExists(@Field("phone") String phone);

        @FormUrlEncoded
        @POST("register.php")
        Call<User> registerNewUser(@Field("phone") String phone,
                                   @Field("name") String name,
                                   @Field("address") String address,
                                   @Field("birthdate") String birthdate);



        @FormUrlEncoded
        @POST("getuser.php")
        Call<User> getUserInformation(@Field("phone") String phone);
}
