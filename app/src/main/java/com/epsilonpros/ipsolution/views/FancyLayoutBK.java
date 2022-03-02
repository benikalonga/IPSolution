package com.epsilonpros.ipsolution.views;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.epsilonpros.ipsolution.R;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("unused")
public class FancyLayoutBK  extends FrameLayout {

    public static final String TAG = FancyLayoutBK.class.getSimpleName();

    private Context mContext;

    // # Background Attributes
    private int mDefaultBackgroundColor 		= Color.BLACK;
    private int mFocusBackgroundColor 			= 0;
    private int mDisabledBackgroundColor        = Color.parseColor("#f6f7f9");
    private int mDisabledBorderColor            = Color.parseColor("#dddfe2");

    private int mBorderColor 					= Color.TRANSPARENT;
    private int mBorderWidth 					= 0;

    private int mRadius 						= 0;
    private int mRadiusTopLeft                  = 0;
    private int mRadiusTopRight                 = 0;
    private int mRadiusBottomLeft               = 0;
    private int mRadiusBottomRight              = 0;

    private boolean mEnabled                    = true;

    private boolean mGhost = false ; // Default is a solid button !
    private boolean mUseRippleEffect = true;

    /**
     * Default constructor
     * @param context : Context
     */
    public FancyLayoutBK(Context context){
        super(context);
        this.mContext   = context;

        initializeFancyButton();
    }



    /**
     * Default constructor called from Layouts
     * @param context : Context
     * @param attrs : Attributes Array
     */
    public FancyLayoutBK(Context context, AttributeSet attrs){
        super(context, attrs);
        this.mContext = context;

        TypedArray attrsArray 	= context.obtainStyledAttributes(attrs,R.styleable.FancyLayoutBK, 0, 0);
        initAttributesArray(attrsArray);
        attrsArray.recycle();

        initializeFancyButton();

    }
    /**
     * Initialize Button dependencies
     *  - Initialize Button Container : The LinearLayout
     *  - Initialize Button TextView
     *  - Initialize Button Icon
     *  - Initialize Button Font Icon
     */
    private void initializeFancyButton(){

        initializeButtonContainer();
        setupBackground();

    }

    /**
     * Initialize Attributes arrays
     *
     * @param attrsArray : Attributes array
     */
    private void initAttributesArray(TypedArray attrsArray){

        mDefaultBackgroundColor 		= attrsArray.getColor(R.styleable.FancyLayoutBK_fl_defaultColor,mDefaultBackgroundColor);
        mFocusBackgroundColor 			= attrsArray.getColor(R.styleable.FancyLayoutBK_fl_focusColor,mFocusBackgroundColor);
        mDisabledBackgroundColor        = attrsArray.getColor(R.styleable.FancyLayoutBK_fl_disabledColor, mDisabledBackgroundColor);

        mEnabled                        = attrsArray.getBoolean(R.styleable.FancyLayoutBK_android_enabled, true);

        mDisabledBorderColor            = attrsArray.getColor(R.styleable.FancyLayoutBK_fl_disabledBorderColor, mDisabledBorderColor);

        mBorderColor 					= attrsArray.getColor(R.styleable.FancyLayoutBK_fl_borderColor, mBorderColor);
        mBorderWidth					= (int) attrsArray.getDimension(R.styleable.FancyLayoutBK_fl_borderWidth,mBorderWidth);

        mRadius 						= (int)attrsArray.getDimension(R.styleable.FancyLayoutBK_fl_radius,mRadius);

        mRadiusTopLeft                  = (int) attrsArray.getDimension(R.styleable.FancyLayoutBK_fl_radiusTopLeft, mRadius);
        mRadiusTopRight                 = (int) attrsArray.getDimension(R.styleable.FancyLayoutBK_fl_radiusTopRight, mRadius);
        mRadiusBottomLeft               = (int) attrsArray.getDimension(R.styleable.FancyLayoutBK_fl_radiusBottomLeft, mRadius);
        mRadiusBottomRight              = (int) attrsArray.getDimension(R.styleable.FancyLayoutBK_fl_radiusBottomRight, mRadius);

        mGhost                          = attrsArray.getBoolean(R.styleable.FancyLayoutBK_fl_ghost, mGhost);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Drawable getRippleDrawable(Drawable defaultDrawable, Drawable focusDrawable, Drawable disabledDrawable){
        if (!mEnabled){
            return disabledDrawable;
        } else {
            return new RippleDrawable(ColorStateList.valueOf(mFocusBackgroundColor), defaultDrawable, focusDrawable);
        }

    }

    /**
     * This method applies radius to the drawable corners
     * Specify radius for each corner if radius attribute is not defined
     * @param drawable Drawable]
     */
    private void applyRadius(GradientDrawable drawable){
        if (mRadius > 0){
            drawable.setCornerRadius(mRadius);
        } else {
            drawable.setCornerRadii(new float[]{mRadiusTopLeft, mRadiusTopLeft, mRadiusTopRight, mRadiusTopRight,
                    mRadiusBottomRight, mRadiusBottomRight, mRadiusBottomLeft, mRadiusBottomLeft});
        }
    }

    @SuppressLint("NewApi")
    private void setupBackground(){
        // Default Drawable
        GradientDrawable defaultDrawable = new GradientDrawable();
        applyRadius(defaultDrawable);


        if (mGhost){
            defaultDrawable.setColor(getResources().getColor(android.R.color.transparent)); // Hollow Background
        } else {
            defaultDrawable.setColor(mDefaultBackgroundColor);
        }

        //Focus Drawable
        GradientDrawable focusDrawable = new GradientDrawable();
        applyRadius(focusDrawable);

        focusDrawable.setColor(mFocusBackgroundColor);

        // Disabled Drawable
        GradientDrawable disabledDrawable = new GradientDrawable();
        applyRadius(disabledDrawable);

        disabledDrawable.setColor(mDisabledBackgroundColor);
        disabledDrawable.setStroke(mBorderWidth, mDisabledBorderColor);

        // Handle Border
        if (mBorderColor != 0) {
            defaultDrawable.setStroke(mBorderWidth, mBorderColor);
        }

        // Handle disabled border color
        if (!mEnabled){
            defaultDrawable.setStroke(mBorderWidth, mDisabledBorderColor);
            if (mGhost){
                disabledDrawable.setColor(getResources().getColor(android.R.color.transparent));
            }
        }


        if (mUseRippleEffect && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            this.setBackground(getRippleDrawable(defaultDrawable, focusDrawable, disabledDrawable));

        } else {

            StateListDrawable states = new StateListDrawable();

            // Focus/Pressed Drawable
            GradientDrawable drawable2 = new GradientDrawable();
            applyRadius(drawable2);

            if (mGhost){
                drawable2.setColor(getResources().getColor(android.R.color.transparent)); // No focus color
            } else {
                drawable2.setColor(mFocusBackgroundColor);
            }

            // Handle Button Border
            if (mBorderColor != 0) {
                if (mGhost) {
                    drawable2.setStroke(mBorderWidth, mFocusBackgroundColor); // Border is the main part of button now
                }
                else {
                    drawable2.setStroke(mBorderWidth, mBorderColor);
                }
            }

            if (!mEnabled){
                if (mGhost){
                    drawable2.setStroke(mBorderWidth, mDisabledBorderColor);
                } else {
                    drawable2.setStroke(mBorderWidth, mDisabledBorderColor);
                }
            }

            if(mFocusBackgroundColor != 0){
                states.addState(new int[] { android.R.attr.state_pressed}, drawable2);
                states.addState(new int[] { android.R.attr.state_focused}, drawable2);
                states.addState(new int[]{ -android.R.attr.state_enabled }, disabledDrawable);
            }

            states.addState(new int[]{}, defaultDrawable);


            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                this.setBackgroundDrawable(states);
            } else {
                this.setBackground(states);
            }

        }



    }


    /**
     * Initialize button container
     */
    private void initializeButtonContainer(){

        if (this.getLayoutParams() == null){
            LayoutParams containerParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            this.setLayoutParams(containerParams);
        }

        this.setClickable(true);
        this.setFocusable(true);
    }

    /**
     * Set Background color of the button
     * @param color : use Color.parse('#code')
     */
    public void setBackgroundColor(int color){
        this.mDefaultBackgroundColor = color;
        this.setupBackground();
    }

    /**
     * Set Focus color of the button
     * @param color : use Color.parse('#code')
     */
    public void setFocusBackgroundColor(int color){
        this.mFocusBackgroundColor = color;
        this.setupBackground();

    }

    /**
     * Set Disabled state color of the button
     *
     * @param color : use Color.parse('#code')
     */
    public void setDisableBackgroundColor(int color) {
        this.mDisabledBackgroundColor = color;
        this.setupBackground();

    }

    /**
     * Set Disabled state color of the button border
     *
     * @param color : use Color.parse('#code')
     */
    public void setDisableBorderColor(int color) {
        this.mDisabledBorderColor = color;
        this.setupBackground();

    }

    /**
     * Set color of the button border
     * @param color : Color
     * use Color.parse('#code')
     */
    public void setBorderColor(int color){
        this.mBorderColor = color;
        this.setupBackground();
    }

    /**
     * Set Width of the button
     * @param width : Width
     */
    public void setBorderWidth(int width){
        this.mBorderWidth = width;
        this.setupBackground();
    }

    /**
     * Set Border Radius of the button
     * @param radius : Radius
     */
    public void setRadius(int radius){
        this.mRadius = radius;
        this.setupBackground();
    }

    /**
     * Set Border Radius for each button corner
     * Top Left, Top Right, Bottom Left, Bottom Right
     * @param radius : Array of int
     */
    public void setRadius(int[] radius){
        this.mRadiusTopLeft     = radius[0];
        this.mRadiusTopRight    = radius[1];
        this.mRadiusBottomLeft  = radius[2];
        this.mRadiusBottomRight = radius[3];

        this.setupBackground();
    }

    /**
     * Override setEnabled and rebuild the fancybutton view
     * To redraw the button according to the state : enabled or disabled
     * @param value
     */
    @Override
    public void setEnabled(boolean value){
        super.setEnabled(value);
        this.mEnabled = value;
        initializeFancyButton();

    }

    /**
     * Setting the button to have hollow or solid shape
     * @param ghost
     */
    public void setGhost(boolean ghost) {
        this.mGhost = ghost;
         this.setupBackground();

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class CustomOutline extends ViewOutlineProvider {

        int width;
        int height;

        CustomOutline(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public void getOutline(View view, Outline outline) {

            if (mRadius==0){
                outline.setRect(0,10,width,height);
            }
            else {
                outline.setRoundRect(0, 10, width, height, mRadius);
            }

        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new CustomOutline(w, h));
        }
    }

    //From UTILS

    private static Map<String, Typeface> cachedFontMap = new HashMap<String, Typeface>();

    public static int pxToSp(final Context context, final float px) {
        return Math.round(px / context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static int spToPx(final Context context, final float sp) {
        return Math.round(sp * context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static Typeface findFont(Context context, String fontPath, String defaultFontPath){

        if (fontPath == null){
            return Typeface.DEFAULT;
        }

        String fontName = new File(fontPath).getName();
        String defaultFontName = "";
        if (!TextUtils.isEmpty(defaultFontPath)){
            defaultFontName = new File(defaultFontPath).getName();
        }

        if (cachedFontMap.containsKey(fontName)){
            return cachedFontMap.get(fontName);
        }else{
            try{
                AssetManager assets = context.getResources().getAssets();

                if (Arrays.asList(assets.list("")).contains(fontPath)){
                    Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);
                    cachedFontMap.put(fontName, typeface);
                    return typeface;
                }else if (Arrays.asList(assets.list("fonts")).contains(fontName)){
                    Typeface typeface = Typeface.createFromAsset(context.getAssets(), String.format("fonts/%s",fontName));
                    cachedFontMap.put(fontName, typeface);
                    return typeface;
                }else if (Arrays.asList(assets.list("iconfonts")).contains(fontName)){
                    Typeface typeface = Typeface.createFromAsset(context.getAssets(), String.format("iconfonts/%s",fontName));
                    cachedFontMap.put(fontName, typeface);
                    return typeface;
                }else if (!TextUtils.isEmpty(defaultFontPath) && Arrays.asList(assets.list("")).contains(defaultFontPath)){
                    Typeface typeface = Typeface.createFromAsset(context.getAssets(), defaultFontPath);
                    cachedFontMap.put(defaultFontName, typeface);
                    return typeface;
                } else {
                    throw new Exception("Font not Found");
                }

            }catch (Exception e){
                Log.e(FancyLayoutBK.TAG, String.format("Unable to find %s font. Using Typeface.DEFAULT instead.", fontName));
                cachedFontMap.put(fontName, Typeface.DEFAULT);
                return Typeface.DEFAULT;
            }
        }
    }
}
