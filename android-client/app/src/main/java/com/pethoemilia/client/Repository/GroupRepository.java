package com.pethoemilia.client.Repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.pethoemilia.client.MyConst;
import com.pethoemilia.client.api.GroupClient;
import com.pethoemilia.client.api.MessageClient;
import com.pethoemilia.client.entity.Group;
import com.pethoemilia.client.entity.Message;
import com.pethoemilia.client.entity.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupRepository {
    private final GroupClient groupClient;
    private final MessageClient messageClient;
    private final SharedPreferences sharedPreferences;
    private final MutableLiveData<List<Group>> groupsLiveData = new MutableLiveData<>();

    public GroupRepository(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyConst.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        groupClient = retrofit.create(GroupClient.class);
        messageClient = retrofit.create(MessageClient.class);
        sharedPreferences = context.getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
    }

    public LiveData<List<Group>> getGroupsLiveData() {
        return groupsLiveData;
    }

    public void loadGroups(long userId) {
        String encodedCredentials = sharedPreferences.getString(MyConst.AUTH, null);
        Call<List<Group>> call = groupClient.findByUserId(userId, encodedCredentials);

        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    groupsLiveData.setValue(response.body());
                } else {
                    Log.e("GroupRepository", "Hiba a csoportok betöltésekor: " + response.code());
                    if (response.code() == 401) {
                        // Jogosultsági hiba, lehet kezelni itt
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Log.e("GroupRepository", "Hálózati hiba: ", t);
            }
        });
    }

    public void loadMessagesForGroup(Long groupId) {
        String encodedCredentials = sharedPreferences.getString(MyConst.AUTH, null);
        Call<List<Message>> call = messageClient.findByGroupId(groupId, encodedCredentials);

        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Message> messages = response.body();
                    List<Group> groups = groupsLiveData.getValue();
                    if (groups != null) {
                        for (Group group : groups) {
                            if (group.getId().equals(groupId)) {
                                group.setMessages(messages);
                                break;
                            }
                        }
                        groupsLiveData.postValue(groups);
                    }
                } else {
                    Log.e("GroupRepository", "Hiba az üzenetek betöltésekor: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Log.e("GroupRepository", "Hálózati hiba az üzenetek lekérésekor", t);
            }
        });
    }
}
