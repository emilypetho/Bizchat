package com.pethoemilia.client.ViewModel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.pethoemilia.client.MyConst;
import com.pethoemilia.client.Repository.GroupRepository;
import com.pethoemilia.client.entity.Group;
import com.pethoemilia.client.entity.User;

import java.util.List;

public class GroupViewModel extends ViewModel {
    private final GroupRepository repository;
    private final MutableLiveData<List<Group>> groupsLiveData = new MutableLiveData<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pollingRunnable;
    private static final long POLLING_INTERVAL = 2000;
    public GroupViewModel() {
        repository = new GroupRepository();
    }

    public LiveData<List<Group>> getGroups() {
        return groupsLiveData;
    }

    public void loadGroups(long userId, Context context) {
        repository.loadGroups(userId, context, groups -> {
            groupsLiveData.postValue(groups);
        });
//        startPolling(userId, context);
    }

    public User getUserFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString(MyConst.USER, null);
        if (userJson != null) {
            return new Gson().fromJson(userJson, User.class);
        }
        return null;
    }

    public void saveGroupToSharedPreferences(Context context, Group group) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MyConst.GROUP, new Gson().toJson(group));
        editor.apply();
    }
//    private void startPolling(long userId, Context context) {
//        pollingRunnable = new Runnable() {
//            @Override
//            public void run() {
//                repository.loadGroups(userId, context, groups -> {
//                    groupsLiveData.postValue(groups);
//                });
//                handler.postDelayed(this, POLLING_INTERVAL);
//            }
//        };
//        handler.postDelayed(pollingRunnable, POLLING_INTERVAL);
//    }
//
//    @Override
//    protected void onCleared() {
//        super.onCleared();
//        handler.removeCallbacks(pollingRunnable); // Leállítja a pollingot, ha az Activity/Fragment megszűnik
//    }
}
//