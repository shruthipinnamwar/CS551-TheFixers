package com.filestack.android.internal;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.filestack.FileLink;
import com.filestack.Sources;
import com.filestack.StorageOptions;
import com.filestack.android.FsConstants;
import com.filestack.android.R;
import com.filestack.android.Selection;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

public class UploadService extends IntentService {
    public static final String SERVICE_NAME = "uploadService";
    public static final String PREF_NOTIFY_ID_COUNTER = "notifyIdCounter";
    public static final String TAG = "uploadService";

    public UploadService() {
        super(SERVICE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onHandleIntent(Intent intent) {
        ArrayList<Selection> selections;
        StorageOptions storeOpts;

        selections = intent.getParcelableArrayListExtra(FsConstants.EXTRA_SELECTION_LIST);
        storeOpts = (StorageOptions) intent.getSerializableExtra(FsConstants.EXTRA_STORE_OPTS);
        if (storeOpts == null) {
            storeOpts = new StorageOptions.Builder().build();
        }

        SharedPreferences prefs = getSharedPreferences(getClass().getName(), MODE_PRIVATE);
        int notifyCounter = prefs.getInt(PREF_NOTIFY_ID_COUNTER, 0);
        int statusId = notifyCounter;
        notifyCounter++;
        int total = selections.size();

        int i = 0;
        for (Selection item : selections) {
            String name = item.getName();
            String provider = item.getProvider();

            sendProgressNotification(statusId, i, total, name);
            FileLink fileLink = upload(item, storeOpts);

            // If upload fails, decrease total count and show error notification
            if (fileLink == null) {
                int errorId = notifyCounter;
                notifyCounter++;
                sendErrorNotification(errorId, item.getName());
                total--;
            } else {
                i++;
            }

            sendProgressNotification(statusId, i, total, name);
            sendBroadcast(item, fileLink);
        }

        prefs.edit().putInt(PREF_NOTIFY_ID_COUNTER, notifyCounter).apply();
    }

    private FileLink upload(Selection selection, StorageOptions baseOptions) {
        String provider = selection.getProvider();
        String path = selection.getPath();
        Uri uri = selection.getUri();
        int size = selection.getSize();
        String name = selection.getName();
        String mimeType = selection.getMimeType();

        StorageOptions options = baseOptions.newBuilder()
                .filename(name)
                .mimeType(mimeType)
                .build();

        try {
            switch (selection.getProvider()) {
                case Sources.CAMERA:
                    // TODO This should maybe be unified into an InputStream upload
                    return Util.getClient().upload(path, false, options);
                case Sources.DEVICE:
                    InputStream input = getContentResolver().openInputStream(uri);
                    return Util.getClient().upload(input, size, false, options);
                default:
                    return Util.getClient().storeCloudItem(provider, path, options);
            }
        } catch (Exception e) {
            // TODO Update after fixing synchronous versions of upload methods in Java SDK
            // Currently these are "block mode" observables and don't properly pass up exceptions
            // correctly among other issues
            e.printStackTrace();
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        String id = FsConstants.NOTIFY_CHANNEL_UPLOAD;
        CharSequence name = getString(R.string.notify_channel_upload_name);
        String description = getString(R.string.notify_channel_upload_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setDescription(description);
        notificationManager.createNotificationChannel(channel);
    }

    private void sendProgressNotification(int id, int done, int total, String name) {
        Locale locale = Locale.getDefault();
        String channelId = FsConstants.NOTIFY_CHANNEL_UPLOAD;
        NotificationManager notificationManager;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder;

        if (total == 0) {
            notificationManager.cancel(id);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, channelId);
        } else {
            builder = new Notification.Builder(this);
        }

        if (total == done) {
            builder.setContentTitle(String.format(locale, "Uploaded %d files", done));
            builder.setSmallIcon(R.drawable.ic_menu_upload_done_white);
        } else {
            builder.setContentTitle(String.format(locale, "Uploading %d/%d files", done, total));
            builder.setSmallIcon(R.drawable.ic_menu_upload_white);
            builder.setContentText(name);
            builder.setProgress(total, done, false);
        }

        notificationManager.notify(id, builder.build());
    }

    private void sendErrorNotification(int id, String name) {
        String channelId = FsConstants.NOTIFY_CHANNEL_UPLOAD;
        NotificationManager notificationManager;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, channelId);
        } else {
            builder = new Notification.Builder(this);
        }

        builder.setContentTitle("Upload failed");
        builder.setContentText(name);
        builder.setSmallIcon(R.drawable.ic_menu_upload_fail_white);

        notificationManager.notify(id, builder.build());
    }

    private void sendBroadcast(Selection selection, FileLink fileLink) {
        Intent intent = new Intent(FsConstants.BROADCAST_UPLOAD);
        intent.putExtra(FsConstants.EXTRA_SELECTION, selection);
        if (fileLink == null) {
            intent.putExtra(FsConstants.EXTRA_STATUS, FsConstants.STATUS_FAILED);
        } else {
            intent.putExtra(FsConstants.EXTRA_STATUS, FsConstants.STATUS_COMPLETE);
        }
        intent.putExtra(FsConstants.EXTRA_FILE_LINK, fileLink);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
