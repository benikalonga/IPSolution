package com.epsilonpros.ipsolution.services;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.text.format.Formatter;
import android.util.Log;

import com.epsilonpros.ipsolution.R;
import com.epsilonpros.ipsolution.activities.SettingsActivity;
import com.epsilonpros.ipsolution.receivers.PowerReceiver;
import com.epsilonpros.ipsolution.utils.Connectivity;
import com.epsilonpros.ipsolution.utils.Keys;
import com.epsilonpros.ipsolution.utils.Plus;

/**
 * Main service class that monitors the network speed and updates the notification every second
 */
public class ServiceBackground extends Service {

    /**
     * The constant defining the identifier of the notification that is to be shown
     */
    private static final int ID = 9000;
     /**
     * The instance of the handler that updates the notification
     */
    private static NotificationHandler hndNotifier;
    /**
     * The instance of the manager of the notification services
     */
    private static NotificationManager mgrNotifications;
    /**
     * The instance of the notification builder to rebuild the notification
     */
    private static NotificationCompat.Builder notBuilder;
    /**
     * The instance of the binder class used by the activity
     */
    private final IBinder mBinder = new LocalBinder();
    /**
     * The instance of the broadcast receiver to handle intents
     */
    private BroadcastReceiver recScreen;
    /**
     * The instance of the broadcast receiver to handle power saver mode intents
     */
    private final BroadcastReceiver recSaver = new PowerReceiver();

    /**
     * Initializes the service by getting instances of service managers and mainly setting up the
     * receiver to receive all the necessary intents that this service is supposed to handle.
     */
    @Override
    public void onCreate() {

        super.onCreate();

        Intent ittSettings = new Intent(this, SettingsActivity.class);
        PendingIntent pitSettings = PendingIntent.getActivity(this, 0, ittSettings, 0);
        notBuilder = new NotificationCompat.Builder(this);
        notBuilder.setSmallIcon(R.drawable.ic_signal_wifi_0_bar_black_24dp);
        notBuilder.setContentIntent(pitSettings);
        notBuilder.setOngoing(true);
        notBuilder.setWhen(0);
        notBuilder.setOnlyAlertOnce(true);
        notBuilder.setPriority(Integer.MAX_VALUE);
        notBuilder.setCategory(NotificationCompat.CATEGORY_SERVICE);

        setColor(Plus.getIntPref(this, Keys.notificationColorIcon, Color.TRANSPARENT));
        visibilityPublic(Plus.getBooleanPref(this, Keys.notificationVisibleInLockScreen, true));

        mgrNotifications = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        hndNotifier = new NotificationHandler(getApplicationContext());

        if (Plus.getBooleanPref(this, Keys.notificationEnabled, true)) {
            hndNotifier.sendEmptyMessage(1);
        }
        recScreen = new BroadcastReceiver() {

            @Override
            public void onReceive(Context ctcContext, Intent ittIntent) {

                if (ittIntent.getAction().equalsIgnoreCase(Intent.ACTION_SCREEN_OFF)) {

                    Log.d("ServiceBackground", "Screen off; hiding the notification");
                    hndNotifier.removeMessages(1);
                    mgrNotifications.cancel(ID);

                } else if (ittIntent.getAction().equalsIgnoreCase(Intent.ACTION_SCREEN_ON)) {

                    Log.d("ServiceBackground", "Screen on; showing the notification");
                    connectivityUpdate();
                } else if (ittIntent.getAction().equalsIgnoreCase(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {

                    if (ittIntent.getBooleanExtra("state", false)) {

                        Log.d("ServiceBackground", "Airplane mode; hiding the notification");
                        hndNotifier.removeMessages(1);
                        hndNotifier.sendEmptyMessage(1);
                    } else {

                        Log.d("ServiceBackground", "Airplane mode; showing the notification");
                        connectivityUpdate();
                    }
                } else {

                    Log.d("ServiceBackground", "Connectivity change; updating the notification");
                    connectivityUpdate();
                }
            }
        };

        IntentFilter ittScreen = new IntentFilter();
        ittScreen.addAction(Intent.ACTION_SCREEN_ON);
        ittScreen.addAction(Intent.ACTION_SCREEN_OFF);
        ittScreen.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        ittScreen.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(recScreen, ittScreen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            IntentFilter ittSaver = new IntentFilter();
            ittScreen.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);
            registerReceiver(recSaver, ittSaver);
        }
    }

    /**
     * Updates the notification with the new connectivity information. This method determines the type
     * of connectivity and updates the notification with the network type and name. If there is no
     * information about the active network, this will suppress the notification.
     */
    private void connectivityUpdate() {

        if (Connectivity.isGoodWifi(this)) {

            Log.d("ServiceBackground", "Connected to a wireless network");
            if (!Connectivity.getWifiName(this).trim().isEmpty()) {

                Log.d("ServiceBackground", Connectivity.getWifiName(this));
                notBuilder.setContentTitle(Connectivity.getWifiName(this));
                notBuilder.setContentText(Connectivity.getWifiName(this));
                showNotification();
            } else {

                Log.d("ServiceBackground", "Unknown network without SSID");
                hideNotification();
            }
        } else {

            Log.d("ServiceBackground", "Network disconnected; hiding the notification");
            hideNotification();
        }
    }

    /**
     * Called when the service is being stopped. It doesn't do much except clear the message queue of
     * the handler, hides the notification and unregisters the receivers.
     */
    @Override
    public void onDestroy() {

        Log.d("HardwareService", "Stopping the hardware service");
        unregisterReceiver(recScreen);
        unregisterReceiver(recSaver);
        hndNotifier.removeMessages(1);
        mgrNotifications.cancel(ID);
    }

    /**
     * Helper method that shows the notification by sending the handler a message and building the
     * notification. This is invoked when the preference is toggled.
     */
    public void showNotification() {

        Log.d("HardwareService", "Showing the notification");
        mgrNotifications.notify(ID, notBuilder.build());
        hndNotifier.removeMessages(1);
        hndNotifier.sendEmptyMessage(1);
    }

    /**
     * Helper method that hides the notification by clearing the handler messages and cancelling the
     * notification. This is invoked when the preference is toggled.
     */
    public void hideNotification() {

        Log.d("HardwareService", "Hiding the notification");
        mgrNotifications.cancel(ID);
        hndNotifier.removeMessages(1);
    }

    /**
     * Helper method that toggles the visibility of the notification on the locksreen depending on the
     * value of the preference in the activity
     *
     * @param visibility A boolean value indicating whether the notification should be visible on the
     *                   lockscreen
     */
    public void visibilityPublic(Boolean visibility) {
        if (visibility) {
            notBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        } else {
            notBuilder.setVisibility(NotificationCompat.VISIBILITY_SECRET);
        }
    }

    /**
     * Helper method that sets the background color of the notification icon by parsing the RGB value
     * into an int.
     *
     * @param color The internal int representation of the RGB color to set as the background colour
     */
    public void setColor(Integer color) {
        notBuilder.setColor(color);
    }

    /**
     * Binder method to allow the settings activity to bind to the service so the notification can be
     * configured and updated while the activity is being toggles.
     *
     * @see Service#onBind(Intent)
     */
    @Override
    public IBinder onBind(Intent intReason) {
        return mBinder;
    }

    /**
     * The handler class that runs every second to update the notification with the network speed. It
     * also runs every minute to save the amount of data-transferred to the preferences.
     */
    private static class NotificationHandler extends Handler {

        /**
         * The instance of the context of the parent service
         */
        private final Context ctxContext;
        /**
         * The value of the amount of data transferred in the previous invocation of the method
         */
        private long lngPrevious = 0L;

        /**
         * Simple constructor to initialize the initial value of the previous
         */
        @SuppressLint("CommitPrefEdits")
        public NotificationHandler(Context ctxContext) {
            this.ctxContext = ctxContext;
        }

        /**
         * Handler method that updates the notification icon with the current speed. It is a very
         * hackish method. We have icons for 1 KB/s to 999 KB/s and 1.0 MB/s to 99.9 MB/s. Every time
         * the method is invoked, we get the amount of data transferred. By subtracting this value with
         * the previous value, we get the delta. Since this method is invoked every second, this delta
         * value indicates the b/s. However, we need to convert this value into KB/s for values under 1
         * MB/s and we need to convert the value to MB/s for values over 1 MB/s. Since all our icon
         * images are numbered sequentially we can assume that the R class generated will contain the
         * integer references of the drawables in the sequential order.
         */
        @Override
        public void handleMessage(Message msgMessage) {

            ServiceBackground.hndNotifier.sendEmptyMessageDelayed(1, 1000L);

            long lngCurrent = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
            int lngSpeed = (int) (lngCurrent - lngPrevious);
            lngPrevious = lngCurrent;

            try {

                if (lngSpeed < 1024) {

                    ServiceBackground.notBuilder.setSmallIcon(R.drawable.ic_signal_wifi_0_bar_black_24dp);
                } else if (lngSpeed < 1048576L) {

                    ServiceBackground.notBuilder.setSmallIcon(R.drawable.ic_signal_wifi_1_bar_black_24dp);

                } else if (lngSpeed <= 10485760) {
                    ServiceBackground.notBuilder.setSmallIcon(R.drawable.ic_signal_wifi_2_bar_black_24dp);
                } else if (lngSpeed <= 103809024) {
                    ServiceBackground.notBuilder.setSmallIcon(R.drawable.ic_signal_wifi_3_bar_black_24dp);
                } else {
                    ServiceBackground.notBuilder.setSmallIcon(R.drawable.ic_signal_wifi_4_bar_black_24dp);
                }

                Long lngTotal = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
                String strTotal = Formatter.formatFileSize(this.ctxContext, lngTotal);
                ServiceBackground.notBuilder.setContentInfo(strTotal);
                ServiceBackground.mgrNotifications.notify(ID, ServiceBackground.notBuilder.build());
            } catch (Exception e) {
                Log.e("NotificationHandler", "Error creating notification for speed " + lngSpeed);
            }
        }
    }

    /**
     * Custom binder class used for allowing the preference activity to bind to this service so that it
     * may be configured on the fly
     */
    public class LocalBinder extends Binder {

        public ServiceBackground getServerInstance() {
            return ServiceBackground.this;
        }
    }
}