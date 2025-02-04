package com.pethoemilia.client.Repository;
import android.util.Base64;
import android.util.Log;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.pethoemilia.client.MyConst;
import com.pethoemilia.client.api.UserClient;
import com.pethoemilia.client.entity.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Repo {
    private static Repo instance;
    private UserClient userClient;
    private SharedPreferences sharedPref;

    private Repo(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyConst.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userClient = retrofit.create(UserClient.class);
        sharedPref = context.getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
    }

    public static Repo getInstance(Context context) {
        if (instance == null) {
            instance = new Repo(context);
        }
        return instance;
    }

    public void loginUser(String email, String password, Callback<User> callback) {
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
