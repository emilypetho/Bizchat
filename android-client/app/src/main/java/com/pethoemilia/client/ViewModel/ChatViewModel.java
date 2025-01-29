package com.pethoemilia.client.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pethoemilia.client.entity.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {
    private MutableLiveData<List<Message>> messages = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> newMessages) {
        messages.setValue(newMessages);
    }

    public void addMessage(Message newMessage) {
        List<Message> currentMessages = messages.getValue();
        if (currentMessages != null) {
            currentMessages.add(newMessage);
            messages.setValue(currentMessages);
        }
    }
}
