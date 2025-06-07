package com.pethoemilia.client.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pethoemilia.client.Repository.ChatRepository;
import com.pethoemilia.client.entity.Message;
import java.util.List;
import android.os.Handler;
import android.os.Looper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatViewModel extends AndroidViewModel {

    private ChatRepository repo;
    private MutableLiveData<List<Message>> messages;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pollingRunnable;
    private static final long POLLING_INTERVAL = 2000; // 2 másodpercenként frissít

    public ChatViewModel(Application application) {
        super(application);
        repo = ChatRepository.getInstance(application.getApplicationContext());
        messages = new MutableLiveData<>();
    }

    public LiveData<List<Message>> getMessages(Long groupId) {
        repo.loadMessages(groupId, messages);
        startPolling(groupId); // Indítja a folyamatos frissítést
        return messages;
    }

    public void sendMessage(Message message) {
        repo.sendMessage(message, new ChatRepository.MessageCallback() {
            @Override
            public void onSuccess() {
                repo.loadMessages(message.getGroup().getId(), messages);
            }

            @Override
            public void onFailure() {
                // Hiba kezelése
            }
        });
    }

    private void startPolling(Long groupId) {
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                repo.loadMessages(groupId, messages);
                handler.postDelayed(this, POLLING_INTERVAL);
            }
        };
        handler.postDelayed(pollingRunnable, POLLING_INTERVAL);
    }

    public void addUserToGroup(long groupId, long userId, Runnable onSuccess, Runnable onFailure) {
        repo.addUserToGroup(groupId, userId, new ChatRepository.GroupCallback() {
            @Override
            public void onSuccess() {
                onSuccess.run();
            }

            @Override
            public void onFailure() {
                onFailure.run();
            }
        });
    }

    public void addUserToGroupByEmail(long groupId, String email, Runnable onSuccess, Runnable onFailure) {
        repo.findUserIdByEmail(email, new ChatRepository.UserIdCallback() {
            @Override
            public void onSuccess(long userId) {
                addUserToGroup(groupId, userId, onSuccess, onFailure);
            }

            @Override
            public void onFailure() {
                onFailure.run();
            }
        });
    }

    public void removeUserFromGroupByEmail(long groupId, String email, Runnable onSuccess, Runnable onFailure) {
        repo.removeUserByEmailFromGroup(email, groupId, new ChatRepository.GroupCallback() {
            @Override
            public void onSuccess() {
                onSuccess.run();
            }

            @Override
            public void onFailure() {
                onFailure.run();
            }
        });
    }



    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacks(pollingRunnable); // Leállítja a pollingot, ha az Activity/Fragment megszűnik
    }
}