package com.pethoemilia.client.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pethoemilia.client.entity.Group;
//import com.pethoemilia.client.Repository.GroupRepository;

import java.util.List;

//public class GroupViewModel extends AndroidViewModel {
//    private final GroupRepository repository;
//    private final LiveData<List<Group>> groupsLiveData;
//
//    public GroupViewModel(@NonNull Application application) {
//        super(application);
//        repository = new GroupRepository(application);
//        groupsLiveData = repository.getGroupsLiveData();
//    }
//
//    public LiveData<List<Group>> getGroupsLiveData() {
//        return groupsLiveData;
//    }
//
//    public void loadGroups(long userId) {
//        repository.loadGroups(userId);
//    }
//
//    public void loadMessagesForGroup(Long groupId) {
//        repository.loadMessagesForGroup(groupId);
//    }
//}
