package com.pethoemilia.client.Repository;
import android.util.Base64;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.pethoemilia.client.MyConst;
import com.pethoemilia.client.api.UserClient;
import com.pethoemilia.client.entity.User;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginRepository {
    private static LoginRepository instance;
    private UserClient userClient;
    private SharedPreferences sharedPref;

    private LoginRepository(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyConst.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userClient = retrofit.create(UserClient.class);
        sharedPref = context.getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
    }

    public static LoginRepository getInstance(Context context) {
        if (instance == null) {
            instance = new LoginRepository(context);
        }
        return instance;
    }

    public void loginUser(String email, String password, Callback<User> callback) {
        Log.d("error2",email+"  "+password);
        String encoded = Base64.encodeToString((email + ":" + password).getBytes(), Base64.NO_WRAP);
        String encodedcredentials = "Basic " + encoded;
        Call<User> idCall = userClient.findByEmail(email, encodedcredentials);
        idCall.enqueue(callback);
    }

    public void saveUser(User user, String encodedcredentials, boolean rememberMe) {
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String userString = gson.toJson(user);
        editor.putString(MyConst.AUTH, encodedcredentials);
        editor.putBoolean(MyConst.REMEMBER_ME, rememberMe);
        editor.putString(MyConst.USER, userString);
        String userId = sharedPref.getString(MyConst.CHANNEL_ID, null);
        if (userId == null) {
            userId = UUID.randomUUID().toString();
            sharedPref.edit().putString(MyConst.CHANNEL_ID, userId).apply();
        }
//        Log.d("chabbelid", userId);
        editor.apply();
    }

    public User getUserFromSharedPreferences() {
        String userJson = sharedPref.getString(MyConst.USER, null);
        if (userJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }
}