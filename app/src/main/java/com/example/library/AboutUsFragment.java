package com.example.library;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AboutUsFragment extends Fragment {

    private static final String CHANNEL_ID = "about_us_channel";
    private static final int NOTIFICATION_ID = 1;
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_us_fragment, container, false);

        Button exitButton = view.findViewById(R.id.exit_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainFragment
                MainFragment mainFragment = new MainFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, mainFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Initialize the MediaPlayer
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.music);
        mediaPlayer.setLooping(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Check if the app has permission to show notifications
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Create a notification channel for Android 8.0 (Oreo) and higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "О нас";
                String description = "Бесполезное уведомление";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            // Create a notification and show it
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.cat) // Make sure ic_notification is a valid drawable resource
                    .setContentTitle("О нас")
                    .setContentText("ЭХХХ, ПЕРЕСДАЧА")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } else {
            // Request permission to show notifications
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        // Start the music
        mediaPlayer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // Pause the music
        mediaPlayer.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Stop and release the MediaPlayer
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, show the notification
            showNotification();
        }
    }

    private void showNotification() {
        // Check if the app has permission to show notifications
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Create a notification channel for Android 8.0 (Oreo) and higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "о нас";
                String description = "Ну это уведомление, что с него взять";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            // Create a notification and show it
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.cat) // Make sure ic_notification is a valid drawable resource
                    .setContentTitle("О нас")
                    .setContentText("ХЗ!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } else {
            // Permission not granted, request permission or handle accordingly
        }
    }

}