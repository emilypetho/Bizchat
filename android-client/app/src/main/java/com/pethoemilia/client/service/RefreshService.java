package com.pethoemilia.client.service;

import android.Manifest;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.pethoemilia.client.GroupActivity;
import com.pethoemilia.client.LoginActivity;
import com.pethoemilia.client.MyConst;
import com.pethoemilia.client.R;
import com.pethoemilia.client.api.GroupClient;
import com.pethoemilia.client.entity.Group;
import com.pethoemilia.client.entity.User;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        configureRabbit();
        return super.onStartCommand(intent, flags, startId);
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
//        String userJson = sharedPreferences.getString(MyConst.USER, null);
//
//        if (userJson != null) {
//            Gson gson = new Gson();
//            User user = gson.fromJson(userJson, User.class);
//
//            if (user != null) {
//                long userId = user.getId();
//
//                loadGroups(userId, new GroupCallback() {
//                    @Override
//                    public void onGroupsLoaded(List<Group> groups) {
//                        for (Group group : groups) {
//                            Log.d("Groups", "Group: " + group.getName());
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable error) {
//                        Log.e("Groups", "Error loading groups", error);
//                    }
//                });
//            } else {
//                Log.e("RefreshService", "User not found");
//            }
//        }
//
//        return super.onStartCommand(intent, flags, startId);
//    }


    @Override
    public void onDestroy() {
        thread.interrupt();
        super.onDestroy();

    }

    private void configureRabbit() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ConnectionFactory factory = new ConnectionFactory();
                    factory.setUsername("guest");
                    factory.setPassword("guest");
                    //factory.setVirtualHost("/");
                    factory.setHost(MyConst.RABBIT_PORT);
                    factory.setPort(5672);
                    Connection conn = factory.newConnection();
                    boolean autoAck = false;
                    Channel channel = conn.createChannel();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(MyConst.URL) // Backend base URL
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    groupClient = retrofit.create(GroupClient.class);

                    SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
                    String userJson = sharedPreferences.getString(MyConst.USER, null);

                    if (userJson != null) {
                        Gson gson = new Gson();
                        User user = gson.fromJson(userJson, User.class);
                    loadGroups(user.getId());
                    }
                    //channel.queueBind("chatQueue", "newMessageExchange", severity);
                    for(Group g:groupk){
                        Log.d("RabbitMQ", "Error in queue binding:"+g.getName());
                    }
                    for (Group group : groupk) {
                        channel.queueBind("chatQueue", "newMessageExchange", group.getName());
                    }
                    channel.basicConsume("chatQueue", autoAck, "chatQueue",
                            new DefaultConsumer(channel) {
                                @Override
                                public void handleDelivery(String consumerTag,
                                                           Envelope envelope,
                                                           AMQP.BasicProperties properties,
                                                           byte[] body)
                                        throws IOException {

                                    long deliveryTag = envelope.getDeliveryTag();
                                    sendMessage(new String(body));
                                    Log.d("uzenet", Arrays.toString(body));
                                    channel.basicAck(deliveryTag, false);
                                }
                            });

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("RabbitMQ", "Error in queue binding: ", e);
                }
            }
        });

        thread.start();
    }

//    public interface GroupCallback {
//        void onGroupsLoaded(List<Group> groups);
//        void onError(Throwable error);
//    }

    private void loadGroups(long userId) {
        SharedPreferences sharedPreferences = getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String encodedcredentials = sharedPreferences.getString(MyConst.AUTH, null);

        Call<List<Group>> call = groupClient.findByUserId(userId, encodedcredentials);
        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    groupk = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                //
            }
        });
    }


    private void sendMessage(String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(RefreshService.this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        /*if (isAppRunning(this, "com.pethoemilia.client")) {
            return;
        }*/
        Intent intent = new Intent(this, GroupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyConst.CHANNEL_ID)
                .setSmallIcon(R.drawable.done_icon)
                .setContentTitle("Ertesites teeee")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(404, builder.build());
        }
    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
