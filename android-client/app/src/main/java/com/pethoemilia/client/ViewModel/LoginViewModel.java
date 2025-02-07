package com.pethoemilia.client.ViewModel;

import android.content.Context;
import android.util.Base64;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pethoemilia.client.Repository.LoginRepository;
import com.pethoemilia.client.entity.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {
    private LoginRepository userRepository;
    private MutableLiveData<String> loginResult;

    public LoginViewModel() {
        loginResult = new MutableLiveData<>();
    }

    public LiveData<String> getLoginResult() {
        return loginResult;
    }

    public void loginUser(Context context, String email, String password) {
        userRepository = LoginRepository.getInstance(context);

        if (email.isEmpty()) {
            loginResult.setValue("Email cannot be empty");
            return;
        }

        if (password.isEmpty()) {
            loginResult.setValue("Password cannot be empty");
            return;
        }

        userRepository.loginUser(email, password, new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    boolean rememberMe = true;  // Ezt a checkbox értéke alapján állíthatod
//                    String encodedCredentials = "Basic " + password;
                    String encoded = Base64.encodeToString((email + ":" + password).getBytes(), Base64.NO_WRAP);
                    String encodedCredentials = "Basic " + encoded;
                    userRepository.saveUser(user, encodedCredentials, rememberMe);
                    loginResult.setValue("Success");
                } else {
                    loginResult.setValue("Invalid email or password");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                loginResult.setValue("Error with login authentication");
            }
        });
    }

    // Betölti a felhasználót SharedPreferences-ből, ha létezik
    public User getSavedUser(Context context) {
        userRepository = LoginRepository.getInstance(context);
        return userRepository.getUserFromSharedPreferences();
    }
}
