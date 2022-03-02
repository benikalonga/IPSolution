package com.epsilonpros.ipsolution.utils;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;

import net.qiujuer.genius.ui.widget.EditText;

import java.util.Random;
import java.util.Set;

/**
 * Created by KADI on 11/12/2017.
 */

public class Plus {

    public static void animateToNormalSize(View view, AnimationListener animationListner){
        if (view.getVisibility() != View.VISIBLE)
            view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1)
                .scaleY(1f)
                .scaleX(1f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .setListener(animationListner)
                .start();
    }
    public static void animateToScaledSize(View view, float scaleX, float scaleY, AnimationListener animationListner){
        if (view.getVisibility() != View.VISIBLE)
            view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1)
                .scaleY(scaleY)
                .scaleX(scaleX)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .setListener(animationListner)
                .start();
    }

    public static int getRandomInt(int min, int max ){
        Random random = new Random();
        return random.nextInt(max- min+ 1) + min;
    }

    public static void sharedPreferencePut(Context context, String key, Object value, String type){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (type){
            case "boolean":{
                editor.putBoolean(key,(boolean)value);
                break;
            }
            case "String":{
                editor.putString(key, (String)value);
                break;
            }
            case "int":{
                editor.putInt(key, (int)value);
                break;
            }
            case "float":{
                editor.putFloat(key, (float)value);
                break;
            }
            case "long":{
                editor.putLong(key, (long)value);
                break;
            }
            case "set":{
                editor.putStringSet(key, (Set<String>)value);
                break;
            }
            default: break;
        }
        editor.commit();
     }

    public static void sharedPreferenceClear(Context context, String key){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(key);
        editor.commit();
    }

    public static boolean getBooleanPref(Context context, String key){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key,false);
    }

    public static boolean getBooleanPref(Context context, String key,boolean bool){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key,bool);
    }

    public static String getStringPref(Context context, String key){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key,null);
    }
    public static int getIntPref(Context context, String key){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(key, Keys.INT_DEFAULT);
    }
    public static int getIntPref(Context context, String key, int defaultInt){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(key,defaultInt);
    }
    public static float getFloatPref(Context context, String key){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getFloat(key,Keys.INT_DEFAULT);
    }
    public static long getLongPref(Context context, String key){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(key,Keys.INT_DEFAULT);
    }
    public static long getLongPref(Context context, String key, long defaultLong){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(key,defaultLong);
    }
    public static Set<String> getSetPref(Context context, String key){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getStringSet(key,null);
    }

    public static void setStatusbarColorRes(Context context, @ColorRes int color){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            int colorInt = ContextCompat.getColor(context, color);
            setStatusbarColorInt(context, colorInt);
        }
    }

    public static void setStatusbarColorInt(Context context, @ColorInt int color){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

        Window window = null;
        try {
            Activity activity = (Activity)context;
            window = activity.getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
                window.setStatusBarColor(color);
        } catch (Exception e) {
            e.printStackTrace();
        }
      }
    }

    public static void smoothlyChangeColor(@NonNull View view, int fromColor, int toColor, int duration){

        if (view==null){
            return;
        }

        ObjectAnimator animator = ObjectAnimator.ofObject(view, "backgroundColor",new ArgbEvaluator(),fromColor, toColor);
        animator.setDuration(duration);
        animator.start();
    }
    public static void smoothlyChangeColor(@NonNull View view, @ColorRes int fromColorRes, @ColorRes int toColorRes,String duration){

        if (view==null){
            return;
        }
        int fromColor = view.getResources().getColor(fromColorRes);
        int toColor = view.getResources().getColor(toColorRes);

        smoothlyChangeColor(view, fromColor, toColor, Integer.valueOf(duration));
    }
    public static void overlayDrawableColor(Context context, @NonNull Drawable drawable, @ColorRes int color){

        if (drawable == null || context == null) ;
        else {
            if (color != 0){
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable.mutate(), ContextCompat.getColor(context, color));

            }
        }
    }

    public static Drawable getDrawableImage(Context context, @DrawableRes int drawableRes){
        Resources res = context.getResources();
        Drawable drawable =  new BitmapDrawable(res, BitmapFactory.decodeResource(res, drawableRes));
        return drawable;
    }

    public static void overlayDrawableColor(@NonNull Drawable drawable, @ColorInt int color){

        if (drawable == null) return;
        else {
            if (color != 0) {
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable.mutate(), color);
            }
        }
    }

    public static void hideSoftInput(View view) {

        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    public static void showSoftInput(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }
    public static class AnimationListener implements Animator.AnimatorListener{

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    public static void vibrate(Context context, int timeInMillis){
        if (context == null)
            return;
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (v.hasVibrator()){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(timeInMillis, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(timeInMillis);
            }
        }

    }
    public static boolean isVisible(View view){
        return view.getVisibility() == View.VISIBLE;
    }

    public static String getConvertedValue(long bytes){

        if (bytes > 1024*1024*1024)
        {
            return (bytes / 1024 / 1024 / 1024) + " GB";
        }
        else if (bytes > 1024*1024)
        {
            return (bytes / 1024 / 1024) + " MB";
        }
        else if (bytes > 1024)
        {
            return (bytes / 1024) + " KB";
        }
        else
            return bytes + " b";

    }
    public static boolean hasEmptyText(EditText editText){
        return TextUtils.isEmpty(editText.getText());
    }
    public static boolean isEmptyText(String text){
        return TextUtils.isEmpty(text);
    }

}
