package com.ua.yuriihrechka.androiddrinkshop.Adapter;

import android.content.Context;
import android.content.DialogInterface;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.ua.yuriihrechka.androiddrinkshop.Database.ModelDB.Cart;
import com.ua.yuriihrechka.androiddrinkshop.Database.ModelDB.Favorite;
import com.ua.yuriihrechka.androiddrinkshop.Interface.IItemClickListener;
import com.ua.yuriihrechka.androiddrinkshop.Model.Drink;
import com.ua.yuriihrechka.androiddrinkshop.R;
import com.ua.yuriihrechka.androiddrinkshop.Utils.Common;

import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkViewHolder> {

    private Context context;
    private List<Drink> drinkList;

    public DrinkAdapter(Context context, List<Drink> drinkList) {
        this.context = context;
        this.drinkList = drinkList;
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.drink_item_layout, null);
        return new DrinkViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final DrinkViewHolder holder, final int position) {

        Picasso.with(context).load(drinkList.get(position).link)
                .into(holder.img_product);

        holder.txt_drink_name.setText(drinkList.get(position).name);
        holder.txt_price.setText(new StringBuilder("$").append(drinkList.get(position).price).toString());


        holder.btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddToCartDialog(position);
            }
        });

        holder.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_LONG).show();
            }
        });


        // favorite
        if(Common.favoriteRepository.isFavorite(Integer.parseInt(drinkList.get(position).ID)) == 1){
            holder.btn_add_to_favorite.setImageResource(R.drawable.ic_favorite_white_24dp);
        }else {
            holder.btn_add_to_favorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }

        holder.btn_add_to_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(Common.favoriteRepository.isFavorite(Integer.parseInt(drinkList.get(position).ID)) != 1){
                    addOrRemoveFavorite(drinkList.get(position), true);
                    holder.btn_add_to_favorite.setImageResource(R.drawable.ic_favorite_white_24dp);
                }else {
                    addOrRemoveFavorite(drinkList.get(position), false);
                    holder.btn_add_to_favorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                }

            }
        });
    }

    private void addOrRemoveFavorite(Drink drink, boolean isAdd) {

        Favorite favorite = new Favorite();
        favorite.id = Integer.parseInt(drink.ID);
        favorite.link = drink.link;
        favorite.name = drink.name;
        favorite.price = drink.price;
        favorite.menuId = drink.menuId;

        if (isAdd){
            Common.favoriteRepository.insertToFavorite(favorite);
        }else {
            Common.favoriteRepository.delete(favorite);
        }




    }

    private void showAddToCartDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.add_to_cart_layout, null);

        ImageView img_product_dialog = (ImageView)itemView.findViewById(R.id.img_cart_product);
        final ElegantNumberButton txt_count = (ElegantNumberButton)itemView.findViewById(R.id.txt_count);
        TextView txt_product_dialog = (TextView)itemView.findViewById(R.id.txt_cart_product_name);
        EditText edt_coment = (EditText)itemView.findViewById(R.id.edt_comment);

        RadioButton rdi_size_M = (RadioButton)itemView.findViewById(R.id.rdi_size_M);
        RadioButton rdi_size_L = (RadioButton)itemView.findViewById(R.id.rdi_size_L);

        rdi_size_L.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Common.sizeOfCup = 0;
                }
            }
        });

        rdi_size_M.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Common.sizeOfCup = 1;
                }
            }
        });


        RadioButton rdi_sugar_100 = (RadioButton)itemView.findViewById(R.id.rdi_sugar_100);
        RadioButton rdi_sugar_70 = (RadioButton)itemView.findViewById(R.id.rdi_sugar_70);
        RadioButton rdi_sugar_50 = (RadioButton)itemView.findViewById(R.id.rdi_sugar_50);
        RadioButton rdi_sugar_30 = (RadioButton)itemView.findViewById(R.id.rdi_sugar_30);
        RadioButton rdi_sugar_free = (RadioButton)itemView.findViewById(R.id.rdi_sugar_free);

        rdi_sugar_100.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Common.sugar = 100;
                }
            }
        });

        rdi_sugar_70.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Common.sugar = 70;
                }
            }
        });

        rdi_sugar_50.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Common.sugar = 50;
                }
            }
        });

        rdi_sugar_30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Common.sugar = 30;
                }
            }
        });

        rdi_sugar_free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Common.sugar = 0;
                }
            }
        });


        RadioButton rdi_ice_100 = (RadioButton)itemView.findViewById(R.id.rdi_ice_100);
        RadioButton rdi_ice_70 = (RadioButton)itemView.findViewById(R.id.rdi_ice_70);
        RadioButton rdi_ice_50 = (RadioButton)itemView.findViewById(R.id.rdi_ice_50);
        RadioButton rdi_ice_30 = (RadioButton)itemView.findViewById(R.id.rdi_ice_30);
        RadioButton rdi_ice_free = (RadioButton)itemView.findViewById(R.id.rdi_ice_free);

        rdi_ice_100.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Common.ice = 100;
                }
            }
        });

        rdi_ice_70.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Common.ice = 70;
                }
            }
        });

        rdi_ice_50.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Common.ice = 50;
                }
            }
        });

        rdi_ice_30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Common.ice = 30;
                }
            }
        });

        rdi_ice_free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Common.ice = 0;
                }
            }
        });

        RecyclerView recycler_topping = (RecyclerView)itemView.findViewById(R.id.recycler_topping);
        recycler_topping.setLayoutManager(new LinearLayoutManager(context));
        recycler_topping.setHasFixedSize(true);

        MultiChoiceAdapter adapter = new MultiChoiceAdapter(context, Common.toppingList);
        recycler_topping.setAdapter(adapter);

        Picasso.with(context)
                .load(drinkList.get(position).link)
                .into(img_product_dialog);

        txt_product_dialog.setText(drinkList.get(position).name);

        builder.setView(itemView);
        builder.setNegativeButton("ADD TO CART", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (Common.sizeOfCup == -1){
                    Toast.makeText(context, "Please choose size of cup", Toast.LENGTH_LONG).show();
                    return;
                }
                if (Common.sugar == -1){
                    Toast.makeText(context, "Please choose sugar", Toast.LENGTH_LONG).show();
                    return;
                }
                if (Common.ice == -1){
                    Toast.makeText(context, "Please choose ice", Toast.LENGTH_LONG).show();
                    return;
                }


                showConfirmDialog(position, txt_count.getNumber());
                dialogInterface.dismiss();
            }
        });
        builder.show();





    }

    private void showConfirmDialog(final int position, final String number) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.confirm_add_to_cart_layout, null);

        // view
        ImageView img_product_dialog = (ImageView)itemView.findViewById(R.id.img_product);
        final TextView txt_product_dialog = (TextView)itemView.findViewById(R.id.txt_cart_product_name);
        TextView txt_product_price = (TextView)itemView.findViewById(R.id.txt_cart_product_price);
        TextView txt_sugar = (TextView)itemView.findViewById(R.id.txt_sugar);
        TextView txt_ice = (TextView)itemView.findViewById(R.id.txt_ice);
        final TextView txt_topping_extra = (TextView)itemView.findViewById(R.id.txt_topping_extra);

        // set data

        Picasso.with(context).load(drinkList.get(position).link).into(img_product_dialog);

        txt_product_dialog.setText(new StringBuilder(drinkList.get(position).name).append(" x")
        .append(number)
        .append(Common.sizeOfCup == 0 ? " Size M": " Size L"));

        txt_ice.setText(new StringBuilder("Ice: ").append(Common.ice).append("%").toString());
        txt_sugar.setText(new StringBuilder("Sugar: ").append(Common.sugar).append("%").toString());

        double price = (Double.parseDouble(drinkList.get(position).price) * Double.parseDouble(number) * Common.toppingPrice);

        if (Common.sizeOfCup == 1){
            price+=3.0;
        }
        txt_product_price.setText(new StringBuilder("$").append(price));

        StringBuilder topping_final_comment = new StringBuilder("");
        for (String line:Common.toppingAdded){
            topping_final_comment.append(line).append("\n");
        }

        txt_topping_extra.setText(topping_final_comment);

        final double finalPrice = price;
        builder.setNegativeButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {




                dialogInterface.dismiss();

                // add in sql

                try {

                    Cart cartItem = new Cart();
                    cartItem.name = txt_product_dialog.getText().toString();
                    cartItem.amount = Integer.parseInt(number);
                    cartItem.ice = Common.ice;
                    cartItem.sugar = Common.sugar;
                    cartItem.price = finalPrice;
                    cartItem.toppingExtras = txt_topping_extra.getText().toString();
                    cartItem.link = drinkList.get(position).link;

                    // add to db
                    Common.cartRepository.insertToCart(cartItem);

                    Log.d("YH_DEBUG", new Gson().toJson(cartItem));

                    Toast.makeText(context, "Save item to cart success", Toast.LENGTH_LONG).show();

                }
                catch (Exception ex){
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

        builder.setView(itemView);
        builder.show();



    }


    @Override
    public int getItemCount() {
        return drinkList.size();
    }
}
