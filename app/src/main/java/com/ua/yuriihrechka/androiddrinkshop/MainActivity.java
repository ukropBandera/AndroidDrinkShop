package com.ua.yuriihrechka.androiddrinkshop;

//14

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;
import com.ua.yuriihrechka.androiddrinkshop.Model.CheckUserResponse;
import com.ua.yuriihrechka.androiddrinkshop.Model.User;
import com.ua.yuriihrechka.androiddrinkshop.Retrofit.IDrinkShopAPI;
import com.ua.yuriihrechka.androiddrinkshop.Utils.Common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1000;
    private static final int REQUEST_PERMISSION = 1001;
    Button btn_continue;
    IDrinkShopAPI mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, REQUEST_PERMISSION);
        }

        Log.d("MY_APP_ERROR", "001");
       // printKeyHash();

        mService = Common.getApiDrinkShop();

        btn_continue = (Button) findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("MY_APP_ERROR", "002");
                startLoginPage(LoginType.PHONE);
            }
        });


        if (AccountKit.getCurrentAccessToken()!=null){

            Log.d("MY_APP_ERROR", "101");

            final android.app.AlertDialog alertDialog=new SpotsDialog(MainActivity.this);
            alertDialog.show();
            alertDialog.setMessage("Please Wait...");

            //Get user phone and check it exists on server

            Log.d("MY_APP_ERROR", "101.1");

            // autologin
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(final Account account) {

                    Log.d("MY_APP_ERROR", "102");

                    mService.checkUserExists(account.getPhoneNumber().toString())
                            .enqueue(new Callback<CheckUserResponse>() {

                                @Override
                                public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                                    CheckUserResponse userResponse=response.body();
                                    if(userResponse.isExists()){

                                        Log.d("MY_APP_ERROR", "103");

                                        // Fetch Information
                                        mService.getUserInformation(account.getPhoneNumber().toString())
                                                .enqueue(new Callback<User>() {
                                                    @Override
                                                    public void onResponse(Call<User> call, Response<User> response) {
                                                        // If user already exists just start new Activity
                                                        alertDialog.dismiss();

                                                        Log.d("MY_APP_ERROR", "104");

                                                        Common.currentUser=response.body();

                                                        startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                                        finish();  // Closes MainActivity
                                                    }

                                                    @Override
                                                    public void onFailure(Call<User> call, Throwable t) {

                                                        Log.d("MY_APP_ERROR", "105");
                                                       // alertDialog.dismiss();
                                                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                                                    }
                                                });


                                    }else {
                                        // need register
                                        alertDialog.dismiss();

                                        Log.d("MY_APP_ERROR", "106");

                                        showRegisterDialog(account.getPhoneNumber().toString());
                                    }
                                }

                                @Override
                                public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                                    Log.i("MY_APP_ERROR", t.getMessage().toString());
                                    Log.d("MY_APP_ERROR", "107");
                                }
                            });
                }

                @Override
                public void onError(AccountKitError accountKitError) {

                    Log.i("MY_APP_ERROR",accountKitError.getErrorType().getMessage());
                    Log.d("MY_APP_ERROR", "108");

                }
            });

        }




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case REQUEST_PERMISSION:

            {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "PERMISSION_GRANTED", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(this, "PERMISSION_DENIED", Toast.LENGTH_LONG).show();
                    Log.d("MY_APP_ERROR", "108_1");
                }
            }
            break;
            default:
                break;


        }

    }

    private void startLoginPage(LoginType loginType) {

        Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder builder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                builder.build());

        Log.d("MY_APP_ERROR", "003");

        startActivityForResult(intent, REQUEST_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("MY_APP_ERROR", "1");

        if(requestCode==REQUEST_CODE){
            AccountKitLoginResult result=data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

            Log.d("MY_APP_ERROR", "2");
            if(result.getError()!=null){
                Toast.makeText(this, ""+result.getError().getErrorType(), Toast.LENGTH_SHORT).show();
            }else if(result.wasCancelled()){
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }
            else {

                Log.d("MY_APP_ERROR", "3");

                if(result.getAccessToken()!=null){
                    final android.app.AlertDialog alertDialog=new SpotsDialog(MainActivity.this);
                    alertDialog.show();
                    alertDialog.setMessage("Please Wait...");

                    //Get user phone and check it exists on server

                    Log.d("MY_APP_ERROR", "4");

                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(final Account account) {

                            Log.d("MY_APP_ERROR", "5");

                            mService.checkUserExists(account.getPhoneNumber().toString())
                                    .enqueue(new Callback<CheckUserResponse>() {

                                        @Override
                                        public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                                            CheckUserResponse userResponse=response.body();
                                            if(userResponse.isExists()){

                                                Log.d("MY_APP_ERROR", "6");

                                                // Fetch Information
                                                mService.getUserInformation(account.getPhoneNumber().toString())
                                                        .enqueue(new Callback<User>() {
                                                            @Override
                                                            public void onResponse(Call<User> call, Response<User> response) {

                                                                Log.d("MY_APP_ERROR", "7");

                                                                // If user already exists just start new Activity
                                                                alertDialog.dismiss();

                                                                Common.currentUser=response.body();

                                                                startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                                                finish();  // Closes MainActivity
                                                            }

                                                            @Override
                                                            public void onFailure(Call<User> call, Throwable t) {
                                                                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                                                                Log.d("MY_APP_ERROR", "8");
                                                            }
                                                        });


                                            }else {
                                                // need register
                                                alertDialog.dismiss();

                                                showRegisterDialog(account.getPhoneNumber().toString());
                                                Log.d("MY_APP_ERROR", "1001");
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                                        }
                                    });
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {

                            Log.i("MY_APP_ERROR",accountKitError.getErrorType().getMessage());
                            Log.d("MY_APP_ERROR", "1002");

                        }
                    });
                }
            }
        }
    }



    private void showRegisterDialog(final String phone) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("REGISTER");

        Log.d("MY_APP_ERROR", "201");

        LayoutInflater inflater = this.getLayoutInflater();
        View register_layout = inflater.inflate(R.layout.register_layout, null);

        final MaterialEditText edt_name = (MaterialEditText) register_layout.findViewById(R.id.edt_name);
        final MaterialEditText edt_address = (MaterialEditText) register_layout.findViewById(R.id.edt_address);
        final MaterialEditText edt_birthdate = (MaterialEditText) register_layout.findViewById(R.id.edt_birthdate);

        Button btn_register = (Button) register_layout.findViewById(R.id.btn_register);
        edt_birthdate.addTextChangedListener(new PatternedTextWatcher("####-##-##"));

        builder.setView(register_layout);
        final AlertDialog dialog = builder.create();

        ////

        Log.d("MY_APP_ERROR", "202");

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                Log.d("MY_APP_ERROR", "203");

                if (TextUtils.isEmpty(edt_name.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Please enter name.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(edt_address.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Please enter address.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(edt_birthdate.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Please enter birthdate.", Toast.LENGTH_LONG).show();
                    return;
                }

                //final SpotsDialog waitingDialog = new SpotsDialog(MainActivity.this);
                final android.app.AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);

                Log.d("MY_APP_ERROR", "204");

                waitingDialog.show();
                waitingDialog.setMessage("Please waiting...");

                Log.d("MY_APP_ERROR", "204_1");

                mService.registerNewUser(phone,
                        edt_name.getText().toString(),
                        edt_address.getText().toString(),
                        edt_birthdate.getText().toString())
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {

                                Log.d("MY_APP_ERROR", "205");

                                waitingDialog.dismiss();

                                Log.d("MY_APP_ERROR", "206");

                                User user = response.body();
                                if (TextUtils.isEmpty(user.getError_msg())) {
                                    Toast.makeText(MainActivity.this, "User register successfully", Toast.LENGTH_LONG).show();

                                    Common.currentUser=response.body();
                                    //// Start new Activity
                                    startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                    finish();

                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {

                                waitingDialog.dismiss();

                            }
                        });

            }
        });

        Log.d("MY_APP_ERROR", "207");

        dialog.show();


    }




    // exit

    boolean isBackButtonClicked = false;

    @Override
    public void onBackPressed() {
        if (isBackButtonClicked) {
            super.onBackPressed();
            return;
        }

        this.isBackButtonClicked = true;
        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_LONG).show();

    }


    @Override
    protected void onResume() {
        super.onResume();
        this.isBackButtonClicked = false;
    }

    private void printKeyHash() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.d("hashKey", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.d("TAG", "printHashKey()", e);
        } catch (Exception e) {
            Log.d("TAG", "printHashKey()", e);
        }
    }
}


