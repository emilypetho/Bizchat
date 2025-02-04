package com.pethoemilia.client.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pethoemilia.client.Repository.ChatRepository;
import com.pethoemilia.client.entity.Message;
import java.util.List;

public class ChatViewModel extends AndroidViewModel {

    private ChatRepository repo;
    private MutableLiveData<List<Message>> messages;

    public ChatViewModel(Application application) {
        super(application);
        repo = ChatRepository.getInstance(application.getApplicationContext());
        messages = new MutableLiveData<>();
    }

    public LiveData<List<Message>> getMessages(Long groupId) {
        // This will load messages for a specific group
        repo.loadMessages(groupId, messages);
        return messages;
    }

    public void sendMessage(Message message) {
        repo.sendMessage(message);
    }
}
