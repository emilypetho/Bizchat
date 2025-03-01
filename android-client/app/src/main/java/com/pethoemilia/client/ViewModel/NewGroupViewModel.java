package com.pethoemilia.client.ViewModel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pethoemilia.client.MyConst;
import com.pethoemilia.client.Repository.GroupRepository;
import com.pethoemilia.client.api.UserClient;
import com.pethoemilia.client.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewGroupViewModel extends ViewModel {

    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> closeActivity = new MutableLiveData<>();
    private final GroupRepository groupRepository;
    private final UserClient userClient;

    public NewGroupViewModel() {
        groupRepository = new GroupRepository();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyConst.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        userClient = retrofit.create(UserClient.class);
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public LiveData<Boolean> getCloseActivity() {
        return closeActivity;
    }
    public void createGroup(User currentUser, String groupName, List<String> userEmails, String authHeader, Context context) {
        List<User> users = new ArrayList<>();
        AtomicInteger pendingRequests = new AtomicInteger(userEmails.size()); // Segít nyomon követni, hány lekérés maradt

        for (String email : userEmails) {
            getUserByEmail(email, authHeader, user -> {
                if (user != null) {
                    users.add(user);
                }

                // Ha minden lekérés megtörtént
                if (pendingRequests.decrementAndGet() == 0) {
                    if (users.size() == userEmails.size()) {
                        groupRepository.createGroupWithUsers(groupName, currentUser, users, authHeader, new GroupRepository.GroupCreationCallback() {
                            @Override
                            public void onSuccess() {
                                toastMessage.postValue("Csoport sikeresen létrehozva!");
                                closeActivity.postValue(true);
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                toastMessage.postValue("Hiba történt: " + errorMessage);
                                Log.e("NewGroupViewModel", "Error creating group: " + errorMessage);
                            }
                        });
                    } else {
                        toastMessage.postValue("Néhány e-mail cím nem található.");
                    }
                }
            });
        }
    }

    private void getUserByEmail(String email, String authHeader, NewChatViewModel.UserCallback callback) {
        Call<User> call = userClient.findByEmail(email, authHeader);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(response.body());
                } else {
                    callback.onResult(null);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("NewChatViewModel", "Error fetching user: " + t.getMessage());
                callback.onResult(null);
            }
        });
    }

    public interface UserCallback {
        void onResult(List<User> users);
    }
}
