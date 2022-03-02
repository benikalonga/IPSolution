package com.epsilonpros.ipsolution.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.AnimationListener;
import com.easyandroidanimations.library.FadeOutAnimation;
import com.easyandroidanimations.library.ScaleInAnimation;
import com.epsilonpros.ipsolution.R;
import com.epsilonpros.ipsolution.utils.Keys;
import com.epsilonpros.ipsolution.utils.Plus;
import com.epsilonpros.ipsolution.views.FancyButtonBK;

import net.qiujuer.genius.ui.widget.EditText;
import net.qiujuer.genius.ui.widget.Loading;

import java.security.Key;
import java.util.concurrent.TimeUnit;

import static com.epsilonpros.ipsolution.utils.Plus.hasEmptyText;

public class ConnectActivity extends AppCompatActivity implements View.OnClickListener {

    FancyButtonBK fcyAnnuler, fcySeConnecter, fcyVideId, fcyVideMdp;
    EditText editTextId, editTextMdp;

    Loading loadingConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        fcyAnnuler = findViewById(R.id.fcyAnnuler);
        fcySeConnecter = findViewById(R.id.fcySeConnecter);
        fcyVideId = findViewById(R.id.fcyVideId);
        fcyVideMdp = findViewById(R.id.fcyVideMdp);
        editTextId = findViewById(R.id.editId);
        editTextMdp = findViewById(R.id.editMdp);
        loadingConnection = findViewById(R.id.loadingConnect);

        fcyAnnuler.setEnabled(false);
        fcySeConnecter.setEnabled(false);

        initCallbacks();
    }

    private void initCallbacks() {
        fcySeConnecter.setOnClickListener(this);
        fcyAnnuler.setOnClickListener(this);
        fcyVideId.setOnClickListener(this);
        fcyVideMdp.setOnClickListener(this);

        editTextMdp.addTextChangedListener(new TextWatch() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);

                if (hasEmptyText(editTextId) && hasEmptyText(editTextMdp)) {
                    fcySeConnecter.setEnabled(false);
                    fcyAnnuler.setEnabled(false);
                } else {
                    fcySeConnecter.setEnabled(true);
                    fcyAnnuler.setEnabled(true);
                }

                //si mdp vide
                if (TextUtils.isEmpty(s)) {

                    if (fcyVideMdp.getVisibility() == View.VISIBLE)
                        new FadeOutAnimation(fcyVideMdp)
                                .setDuration(Animation.DURATION_SHORT)
                                .setListener(animation -> {
                                    fcyVideMdp.setEnabled(false);
                                })
                                .animate();
                } else {

                    fcyVideMdp.setEnabled(true);
                    if (fcyVideMdp.getVisibility() != View.VISIBLE)
                        new ScaleInAnimation(fcyVideMdp)
                                .setInterpolator(new AnticipateOvershootInterpolator())
                                .setDuration(Animation.DURATION_SHORT)
                                .animate();
                }
            }
        });

        editTextId.addTextChangedListener(new TextWatch() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);

                if (hasEmptyText(editTextId) && hasEmptyText(editTextMdp)) {
                    fcySeConnecter.setEnabled(false);
                    fcyAnnuler.setEnabled(false);
                } else {
                    fcySeConnecter.setEnabled(true);
                    fcyAnnuler.setEnabled(true);
                }

                if (TextUtils.isEmpty(s)) {
                    if (fcyVideId.getVisibility() == View.VISIBLE)
                        new FadeOutAnimation(fcyVideId)
                                .setListener(animation -> {
                                    fcyVideId.setEnabled(false);
                                })
                                .setDuration(Animation.DURATION_SHORT)
                                .animate();
                } else {
                    fcyVideId.setEnabled(true);
                    if (fcyVideId.getVisibility() != View.VISIBLE)
                        new ScaleInAnimation(fcyVideId)
                                .setInterpolator(new AnticipateOvershootInterpolator())
                                .setDuration(Animation.DURATION_SHORT)
                                .animate();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fcyAnnuler) {
            annule();
        } else if (v.getId() == R.id.fcySeConnecter) {
            connecte();
        } else if (v.getId() == R.id.fcyVideId) {
            editTextId.setText("");
        } else if (v.getId() == R.id.fcyVideMdp) {
            editTextMdp.setText("");
        }
    }

    private void annule() {
        if (!TextUtils.isEmpty(editTextId.getText()) || !TextUtils.isEmpty(editTextMdp.getText())) {
            new AlertDialog.Builder(this)
                    .setTitle("Annuler la connexion")
                    .setMessage("Voulez-vous vraiment annuler la connexion?\nAucun service ne sera disponible")
                    .setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Nothing to do
                        }
                    }).show();
        } else if (loadingConnection.isRunning()) {
            loadingConnection.stop();
            new FadeOutAnimation(loadingConnection)
                    .setDuration(Animation.DURATION_DEFAULT)
                    .animate();
        } else {
            finish();
        }
    }

    private void connecte() {
        String id = editTextId.getText().toString();
        String mdp = editTextMdp.getText().toString();

        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(mdp)) {
            Snackbar.make(fcySeConnecter, "Veuilez entrer des informations correctes", Snackbar.LENGTH_LONG).show();
        } else {
            loadingConnection.start();

            new ScaleInAnimation(loadingConnection)
                    .setInterpolator(new AnticipateOvershootInterpolator())
                    .setDuration(Animation.DURATION_SHORT)
                    .setListener(new AnimationListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            new CountDownTimer(TimeUnit.SECONDS.toMillis(5), TimeUnit.SECONDS.toMillis(1)) {
                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish() {
                                    loadingConnection.stop();
                                    new FadeOutAnimation(loadingConnection)
                                            .animate();

                                    Plus.sharedPreferencePut(ConnectActivity.this, "id", id, Keys.TYPE_STRING);
                                    Plus.sharedPreferencePut(ConnectActivity.this, "mdp", mdp, Keys.TYPE_STRING);

                                    Plus.sharedPreferencePut(ConnectActivity.this, Keys.profilCreer, true, Keys.TYPE_BOOLEAN);

                                    finish();
                                }
                            }.start();
                        }
                    })
                    .animate();
        }
    }

    class TextWatch implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
