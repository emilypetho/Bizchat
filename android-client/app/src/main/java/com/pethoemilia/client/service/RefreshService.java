package com.pethoemilia.client.service;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.pethoemilia.client.GroupActivity;
import com.pethoemilia.client.MyConst;
import com.pethoemilia.client.R;
import com.pethoemilia.client.api.GroupClient;
import com.pethoemilia.client.entity.Group;
import com.pethoemilia.client.entity.User;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RefreshService extends Service {

    private Thread thread;
    private GroupClient groupClient;
    private List<Group> groupk;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Channel buildRabbitChannel() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setHost(MyConst.RABBIT_PORT);
        factory.setPort(5672);
        Connection conn = factory.newConnection();
        return conn.createChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        thread = new Thread(() -> {
            try {
                Channel channel = buildRabbitChannel();
                boolean autoAck = false;

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(MyConst.URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                groupClient = retrofit.create(GroupClient.class);
                SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
                String userJson = sharedPreferences.getString(MyConst.USER, null);

                if (userJson != null) {
                    Gson gson = new Gson();
                    User user = gson.fromJson(userJson, User.class);

                    if (user != null) {
                        loadGroups(user.getId(), groups -> {
                            if (groups != null) {
                                groupk = groups;

                                new Thread(() -> {
                                    try {
                                        for (Group group : groupk) {
                                            channel.queueBind("chatQueue", "newMessageExchange", group.getName());
                                        }
                                    } catch (Exception e) {
                                        Log.e("RabbitMQ", "Queue binding error", e);
                                    }

                                    try {
                                        channel.basicConsume("chatQueue", autoAck, "chatQueue",
                                                new DefaultConsumer(channel) {
                                                    @Override
                                                    public void handleDelivery(String consumerTag, Envelope envelope,
                                                                               AMQP.BasicProperties properties, byte[] body)
                                                            throws IOException {
                                                        long deliveryTag = envelope.getDeliveryTag();
                                                        sendNotificationre(new String(body, StandardCharsets.UTF_8));
                                                        Log.d("uzenet", new String(body, StandardCharsets.UTF_8));
                                                        channel.basicAck(deliveryTag, false);
                                                    }
                                                });
                                    } catch (Exception e) {
                                        Log.e("RabbitMQ", "Error in consuming messages", e);
                                    }
                                }).start();
                            } else {
                                Log.e("RabbitMQ", "Group list is null");
                            }
                        });
                    } else {
                        Log.e("RefreshService", "User is null");
                    }
                } else {
                    Log.e("RefreshService", "User JSON is null");
                }
            } catch (Exception e) {
                Log.e("RabbitMQ", "Error in queue setup", e);
            }
        });
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }


//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        thread = new Thread(() -> {
//            try {
//                ConnectionFactory factory = new ConnectionFactory();
//                factory.setUsername("guest");
//                factory.setPassword("guest");
//                factory.setHost(MyConst.RABBIT_PORT);
//                factory.setPort(5672);
//                Connection conn = factory.newConnection();
//                boolean autoAck = false;
//                Channel channel = conn.createChannel();
//                Retrofit retrofit = new Retrofit.Builder()
//                        .baseUrl(MyConst.URL)
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .build();
//
//                groupClient = retrofit.create(GroupClient.class);
//                SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
//                String userJson = sharedPreferences.getString(MyConst.USER, null);
//
//                if (userJson != null) {
//                    Gson gson = new Gson();
//                    User user = gson.fromJson(userJson, User.class);
//
//                    if (user != null) {
//                        loadGroups(user.getId(), groups -> {
//                            if (groups != null) {
//                                groupk = groups;
//                                for (Group g : groupk) {
//                                    Log.d("RabbitMQ", "sima for: " + g.getName());
//                                }
//
//                                new Thread(() -> {
//                                    try {
//                                        for (Group group : groupk) {
//                                            Log.d("RabbitMQ", "kkkkkkkkkkkkkkk: " + group.getName());
//                                            channel.queueBind("chatQueue", "newMessageExchange",group.getName() );
//                                        }
//                                    } catch (Exception e) {
//                                        Log.e("RabbitMQ", "itt volt baj", e);
//                                    }
//
//                                    try {
//                                        channel.basicConsume("chatQueue", autoAck, "chatQueue",
//                                                new DefaultConsumer(channel) {
//                                                    @Override
//                                                    public void handleDelivery(String consumerTag, Envelope envelope,
//                                                                               AMQP.BasicProperties properties, byte[] body)
//                                                            throws IOException {
//                                                        long deliveryTag = envelope.getDeliveryTag();
//                                                        sendNotificationre(new String(body, StandardCharsets.UTF_8));
//                                                        Log.d("uzenet", new String(body, StandardCharsets.UTF_8));
//                                                        channel.basicAck(deliveryTag, false);
//                                                    }
//                                                });
//                                    } catch (Exception e) {
//                                        Log.e("RabbitMQ", "Error in consuming messages", e);
//                                    }
//                                }).start();
//                            } else {
//                                Log.e("RabbitMQ", "Group list is null");
//                            }
//                        });
//                    } else {
//                        Log.e("RefreshService", "User is null");
//                    }
//                } else {
//                    Log.e("RefreshService", "User JSON is null");
//                }
//            } catch (Exception e) {
//                Log.e("RabbitMQ", "Error in queue setup", e);
//            }
//        });
//        thread.start();
//        return super.onStartCommand(intent, flags, startId);
//
//    }

    @Override
    public void onDestroy() {
        thread.interrupt();
        super.onDestroy();
    }

    private void loadGroups(long userId, GroupCallback callback) {
        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String encodedCredentials = sharedPreferences.getString(MyConst.AUTH, null);

        Call<List<Group>> call = groupClient.findByUserId(userId, encodedCredentials);
        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onGroupsLoaded(response.body());
                } else {
                    callback.onGroupsLoaded(Collections.emptyList());
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                callback.onGroupsLoaded(Collections.emptyList());
                Log.e("loadGroups", "Failed to fetch groups", t);
            }
        });
    }

    interface GroupCallback {
        void onGroupsLoaded(List<Group> groups);
    }
    private void sendNotificationre(String message) {
        try {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(message, JsonObject.class);

            String senderName = jsonObject.has("sender") && jsonObject.getAsJsonObject("sender").has("name")
                    ? jsonObject.getAsJsonObject("sender").get("name").getAsString()
                    : "Ismeretlen";

            String content = jsonObject.has("content")
                    ? jsonObject.get("content").getAsString()
                    : "N/A";

            String notificationText = senderName + ": " + content;

            // Csoport felhasználóinak ellenőrzése
            JsonObject group = jsonObject.getAsJsonObject("group");
            if (group != null && group.has("users")) {
                List<User> groupUsers = new Gson().fromJson(group.getAsJsonArray("users"), new TypeToken<List<User>>(){}.getType());

                SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
                String userJson = sharedPreferences.getString(MyConst.USER, null);
                if (userJson != null) {
                    Gson gsonUser = new Gson();
                    User currentUser = gsonUser.fromJson(userJson, User.class);
                    // Ellenőrzés, hogy a felhasználó benne van-e a csoportban, és hogy nem a feladó
                    notification(groupUsers, currentUser, jsonObject, notificationText);
                }
            }
        } catch (Exception e) {
            Log.e("sendMessage", "Hiba az üzenet feldolgozásában", e);
        }
    }

    private void notification(List<User> groupUsers, User currentUser, JsonObject jsonObject, String notificationText) {
        for (User user : groupUsers) {
            if (currentUser.getId() == user.getId() && currentUser.getId() != jsonObject.getAsJsonObject("sender").get("id").getAsLong()) {
                // Ha benne van, és nem ő a feladó, akkor küldj értesítést
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(RefreshService.this.getApplicationContext(), notificationText, Toast.LENGTH_SHORT).show());

                Intent intent = new Intent(this, GroupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyConst.CHANNEL_ID)
                        .setSmallIcon(R.drawable.done_icon)
                        .setContentTitle("BizChat")
                        .setContentText(notificationText)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    notificationManager.notify(404, builder.build());
                }
            }
        }
    }
}
