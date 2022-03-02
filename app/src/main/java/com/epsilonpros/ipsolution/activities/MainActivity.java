package com.epsilonpros.ipsolution.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.FadeInAnimation;
import com.easyandroidanimations.library.FadeOutAnimation;
import com.easyandroidanimations.library.ScaleInAnimation;
import com.easyandroidanimations.library.ScaleOutAnimation;
import com.epsilonpros.ipsolution.R;
import com.epsilonpros.ipsolution.services.ServiceBackground;
import com.epsilonpros.ipsolution.utils.Connectivity;
import com.epsilonpros.ipsolution.utils.Constants;
import com.epsilonpros.ipsolution.utils.Keys;
import com.epsilonpros.ipsolution.utils.Log;
import com.epsilonpros.ipsolution.utils.Plus;
import com.epsilonpros.ipsolution.utils.Utils;
import com.epsilonpros.ipsolution.views.FancyLayoutBK;
import com.epsilonpros.ipsolution.views.TextViewTeach;
import com.github.anastr.speedviewlib.DeluxeSpeedView;
import com.rey.material.app.Dialog;
import com.stephentuso.welcome.WelcomeHelper;

import net.qiujuer.genius.ui.widget.EditText;
import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.itangqi.waveloadingview.WaveLoadingView;

public class MainActivity extends AppCompatActivity {

    private WelcomeHelper sampleWelcomeScreen;

    FancyLayoutBK fcyWifiState, fcyWifiEteint, fcyHelp, fcySettings, fcyProfil;
    WaveLoadingView waveConsommation;
    Loading loadingWifi;
    TextViewTeach tvLoadingWifi, tvWifiState;
    FrameLayout frameLoading;
    ImageView ivWifiOff;

    DeluxeSpeedView speedView;
    TextViewTeach tvDataEnvoye, tvDataRecu;


    @BindView(R.id.fcyChargerJeton) FancyLayoutBK fcyChargerJeton;
    @BindView(R.id.loadingJeton) Loading loadingJeton;
    @BindView(R.id.img_profile) ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initWelcomeScreen(savedInstanceState);
        initViews();
        initCallbacks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction(Constants.ACTION_DATA_STATS);

        registerReceiver(broadcastReceiver, intentFilter);

        initComponents();
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        sampleWelcomeScreen.onSaveInstanceState(outState);
    }

    private void initWelcomeScreen(Bundle savedInstanceState) {
        sampleWelcomeScreen = new WelcomeHelper(this, WelcomeScreenActivity.class);

        if (Plus.getBooleanPref(this, Keys.welcomeScreen, true))
            sampleWelcomeScreen.forceShow();
        else
            sampleWelcomeScreen.show(savedInstanceState);

    }

    private void initViews() {
        fcyWifiState = findViewById(R.id.fcyWifiState);
        fcySettings = findViewById(R.id.fcySetting);
        fcyHelp = findViewById(R.id.fcyHelp);
        fcyProfil = findViewById(R.id.fcyLogIn);
        fcyWifiEteint = findViewById(R.id.fcyWifiEteint);
        ivWifiOff = findViewById(R.id.ivWifiOff);
        waveConsommation = findViewById(R.id.waveConsommation);
        loadingWifi = findViewById(R.id.loadingWifi);
        tvLoadingWifi = findViewById(R.id.tvLoadingWifi);
        tvWifiState = findViewById(R.id.tvWifiState);
        frameLoading = findViewById(R.id.frameDialog);

        speedView = findViewById(R.id.speedView);
        tvDataEnvoye = findViewById(R.id.tvDataEnvoye);
        tvDataRecu = findViewById(R.id.tvDataRecu);
    }
    private void initCallbacks(){
        frameLoading.setOnClickListener(v -> {

        });
        fcyWifiState.setOnClickListener(v -> {
            String stringState = tvWifiState.getText().toString();

            if(stringState.equals(getResources().getString(R.string.init_wifi_off))){
                showFrameDialog(R.string.init_demarrage_wifi);
                Connectivity.enableWifi(this);
            }
            else if(stringState.equals(getResources().getString(R.string.init_connectez_vous))){
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        fcyWifiEteint.setOnClickListener(v -> {
            Connectivity.disableWifi(this);
        });
        fcyProfil.setOnClickListener(v -> {
            showProfil();
        });
        fcyHelp.setOnClickListener(v -> {
            sampleWelcomeScreen.forceShow();
        });
        fcySettings.setOnClickListener(v -> {
            Utils.startActivity(MainActivity.this, SettingsActivity.class);
        });
    }
    private void showProfil(){
        if (Plus.getBooleanPref(this, Keys.profilCreer)){
            Utils.startActivity(this, ProfilActivity.class);
        }
        else {
            Utils.startActivity(this, ConnectActivity.class);
        }
    }

    private void initComponents() {
        if (!Utils.isServiceRunning(this, ServiceBackground.class))
            startService(new Intent(this, ServiceBackground.class));

        showFrameDialog(R.string.init_initialisation);
        checkWifi();
        initProfil();
    }

    private void checkWifi() {
        if (Connectivity.isWifiEnable(this)) {
            //wifi allumer et connected
            if (Connectivity.isConnectedWifi(this)) {
                //si connected au bon reseau
                if (Connectivity.isGoodWifi(this)) {

                   wifiAllumerBienConnecter();
                }
                //si connected au mauvais reseau
                else {
                    wifiAllumerConnecterAuMauvaisReseau();
                }
            }
            //wifi allumer mais pas connected
            else {
                wifiAllumerMaisPasConnected();
            }
        }
        //wifi eteint
        else {
            wifiEteint();
        }

    }
    private void wifiEteint(){
        hideWave();
        tvWifiState.setText(R.string.init_wifi_off);
        fcyWifiState.setBorderColor(getColorOf(R.color.white));
        fcyWifiState.setBackgroundColor(getColorOf(R.color.colorPrimary));

        ivWifiOff.setImageResource(R.drawable.ic_wifi_off);
        if (!Plus.isVisible(ivWifiOff))
            new FadeInAnimation(ivWifiOff)
                    .setDuration(Animation.DURATION_SHORT)
                    .setListener(animation -> {
                    })
                    .animate();
        hideFcyEteint();
        hideFrameDialog();
    }
    private void wifiAllumerMaisPasConnected(){
        hideWave();
        tvWifiState.setText(R.string.init_connectez_vous);
        fcyWifiState.setBorderColor(getColorOf(R.color.colorAccent));
        fcyWifiState.setBackgroundColor(getColorOf(R.color.colorPrimary));

        ivWifiOff.setImageResource(R.drawable.ic_wifi_info);
        if (!Plus.isVisible(ivWifiOff))
            new FadeInAnimation(ivWifiOff)
                    .setDuration(Animation.DURATION_SHORT)
                    .setListener(animation -> {

                    })
                    .animate();
        hideFcyEteint();
        hideFrameDialog();
    }
    private void wifiAllumerConnecterAuMauvaisReseau(){
        hideWave();
        tvWifiState.setText(R.string.init_connectez_vous);
        fcyWifiState.setBorderColor(getColorOf(R.color.colorAccent));
        fcyWifiState.setBackgroundColor(getColorOf(R.color.colorPrimary));

        ivWifiOff.setImageResource(R.drawable.ic_wifi_info);
        if (!Plus.isVisible(ivWifiOff))
            new FadeInAnimation(ivWifiOff)
                    .setDuration(Animation.DURATION_SHORT)
                    .setListener(animation -> {
                    })
                    .animate();
        hideFcyEteint();
        hideFrameDialog();
    }
    private void wifiAllumerBienConnecter(){
        tvWifiState.setText(Connectivity.getWifiName(this));
        fcyWifiState.setBorderColor(getColorOf(R.color.colorBluePrimary));
        fcyWifiState.setBackgroundColor(getColorOf(R.color.trans));

        if (Plus.isVisible(ivWifiOff))
            new FadeOutAnimation(ivWifiOff)
                    .setDuration(Animation.DURATION_SHORT)
                    .setListener(animation -> {

                    })
                    .animate();
        hideFrameDialog();
        showFcyEteint();
        showWave();
        enableOther();
        updateUi();
    }
    private void initProfil(){
        loadSavedProfil();
    }
    private void loadSavedProfil() {
        if (Plus.getStringPref(this, Keys.profilPath) != null) {
            loadProfile(Plus.getStringPref(this, Keys.profilPath));
        } else
            loadProfileDefault();
    }
    private void loadProfile(String url) {

        GlideApp.with(this).load(url)
                .circleCrop()
                .into(imgProfile);
        imgProfile.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }

    private void loadProfileDefault() {
        GlideApp.with(this).load(R.drawable.ic_log_in)
                .into(imgProfile);
    }
    private void showWave(){
        waveConsommation.startAnimation();
        waveConsommation.setProgressValue(0);
        waveConsommation.setCenterTitle("0 %");
        if (!Plus.isVisible(waveConsommation)){
            new FadeInAnimation(waveConsommation)
                    .setDuration(Animation.DURATION_SHORT)
                    .animate();
        }
    }
    private void hideWave(){
        waveConsommation.setProgressValue(0);
        if (Plus.isVisible(waveConsommation)){
            new FadeOutAnimation(waveConsommation)
                    .setDuration(Animation.DURATION_DEFAULT)
                    .setListener(animation -> {
                        waveConsommation.endAnimation();
                    })
                    .animate();
        }
    }
    private void hideFcyEteint(){
        if (Plus.isVisible(fcyWifiEteint))
            new ScaleOutAnimation(fcyWifiEteint)
                    .setDuration(Animation.DURATION_SHORT)
                    .setInterpolator(new AnticipateOvershootInterpolator())
                    .setListener(animationDialog -> {
                        fcyWifiEteint.setVisibility(View.GONE);
                    })
                    .animate();
    }
    private void showFcyEteint(){
        if (!Plus.isVisible(fcyWifiEteint))
            new ScaleInAnimation(fcyWifiEteint)
                    .setDuration(Animation.DURATION_SHORT)
                    .setInterpolator(new AnticipateOvershootInterpolator())
                    .animate();
    }

    private void hideFcyChargerJeton(){
        if (Plus.isVisible(fcyChargerJeton))
            new FadeOutAnimation(fcyChargerJeton)
                    .setDuration(Animation.DURATION_SHORT)
                    .setInterpolator(new LinearInterpolator())
                    .setListener(animationDialog -> {
                        fcyChargerJeton.setVisibility(View.GONE);
                    })
                    .animate();
    }
    private void showFcyChargerJeton(){
        if (!Plus.isVisible(fcyChargerJeton))
            new FadeInAnimation(fcyChargerJeton)
                    .setDuration(Animation.DURATION_SHORT)
                    .setInterpolator(new LinearInterpolator())
                    .animate();
    }

    @OnClick({R.id.fcyChargerJeton})
    void clickCharger(){
        EditText editText = new EditText(this);
        editText.setHint("Entrer le code du jeton");
        editText.setTextColor(ContextCompat.getColor(this, R.color.white));
        editText.setHintTextColor(ContextCompat.getColor(this, R.color.grey_400));
        editText.setTag("1");

        new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                .setTitle("Nouveau jeton")
                .setIcon(R.drawable.ic_loyalty_small)
                .setCancelable(true)
                .setView(editText)
                .setPositiveButton("Activer", (dialog, which) -> {
                    activeJeton(editText.getText().toString());
                })
                .setNegativeButton("Annuler", null)
                .create()
                .show();
    }
    private void enableOther(){
        showFcyChargerJeton();
    }
    private void activeJeton(String string){
        Log.i(string);
        waveConsommation.setProgressValue(50);
        waveConsommation.setCenterTitle("50 %");
    }
    private void updateUi(){
        if (!(TrafficStats.getTotalRxBytes() != TrafficStats.UNSUPPORTED && TrafficStats.getTotalTxBytes() != TrafficStats.UNSUPPORTED)) {
            tvWifiState.setText(R.string.init_connectez_vous);
        }
    }
    private void showFrameDialog(@StringRes int string){
        loadingWifi.start();
        tvLoadingWifi.setText(string);

        if (!Plus.isVisible(frameLoading)) {
            new FadeInAnimation(frameLoading)
                    .setDuration(Animation.DURATION_DEFAULT)
                    .animate();
        }
    }
    private void hideFrameDialog(){
        if (Plus.isVisible(frameLoading))
            new FadeOutAnimation(frameLoading)
                    .setDuration(Animation.DURATION_SHORT)
                    .setListener(animationDialog -> {
                        frameLoading.setVisibility(View.GONE);
                    })
                    .animate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private int getColorOf(@ColorRes int res){
        return ContextCompat.getColor(this, res);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTION_DATA_STATS)){

                long wifiTotalEnvoye = intent.getLongExtra(Keys.wifiTotalEnvoye,0);
                long wifiTotalRecu = intent.getLongExtra(Keys.wifiTotalRecu,0);

                tvDataEnvoye.setText("Envoyées \n"+Plus.getConvertedValue(wifiTotalEnvoye));
                tvDataRecu.setText("Reçues \n"+Plus.getConvertedValue(wifiTotalRecu));
            }
            else
                checkWifi();
        }
    };
}
