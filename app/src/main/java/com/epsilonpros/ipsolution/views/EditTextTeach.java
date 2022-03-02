package com.epsilonpros.ipsolution.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

import com.epsilonpros.ipsolution.R;

/**
 * Created by KADI on 18/03/2018.
 */

public class EditTextTeach extends EditText {

    Context context;
    String dirPolices  = "polices/";
    String police = "roboto_light.ttf";

    Resources res;

    public EditTextTeach(Context context){
        super(context);
        this.context = context;
        res = getResources();
        initAttrib(null,-1);
    }

    public EditTextTeach(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        res = getResources();
        initAttrib(attrs, -1);
        try {
            applyPolice();
        } catch (PoliceException e) {
            e.printStackTrace();
        }
    }

    public EditTextTeach(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        this.context = context;
        res = getResources();
        initAttrib(attrs, defStyleAttr);
        try {
            applyPolice();
        } catch (PoliceException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EditTextTeach(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        res = getResources();
        initAttrib(attrs, defStyleAttr);
        try {
            applyPolice();
        } catch (PoliceException e) {
            e.printStackTrace();
        }
    }
    private void initAttrib(AttributeSet attrs, int defStyleAttr){
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.EditTextTeach, defStyleAttr, 0);
        int policeInt = 0;
        try {
            policeInt = attr.getInt(attr.getIndex(R.styleable.EditTextTeach_police_ed),1);
        } catch (Exception e) {
            e.printStackTrace();
        }
              if(policeInt == 1){
            this.police = res.getString(R.string.police_roboto_bold);
        }else if(policeInt == 2){
            this.police = res.getString(R.string.police_roboto_bold2);
        }else if(policeInt == 3){
            this.police = res.getString(R.string.police_roboto_black);
        }else if(policeInt == 4){
            this.police = res.getString(R.string.police_roboto_light);
        }else if(policeInt == 5){
            this.police = res.getString(R.string.police_roboto_reg);
        }else if(policeInt == 6){
            this.police = res.getString(R.string.police_sail_regular);
        }else if(policeInt == 7){
            this.police = res.getString(R.string.police_sofia_regular);
        }else if(policeInt == 8){
            this.police = res.getString(R.string.police_pacifio);
        }else if(policeInt == 9){
            this.police = res.getString(R.string.police_lemonchi);
        }
    }

    public void setPolice(String police){
        this.police = police != null ? police : this.police;

        try {
            applyPolice();
        } catch (PoliceException e) {
            e.printStackTrace();
        }
    }

    private void applyPolice() throws PoliceException {

        Typeface typeface = null;
        AssetManager assetManager  = context.getAssets();

        try {
            typeface = Typeface.createFromAsset(assetManager,dirPolices+police);
        } catch (Exception e) {
            e.printStackTrace();
            throw new PoliceException();
        }

        this.setTypeface(typeface);
    }
}
