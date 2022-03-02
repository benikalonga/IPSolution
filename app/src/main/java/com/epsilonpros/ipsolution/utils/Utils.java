package com.epsilonpros.ipsolution.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RawRes;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.epsilonpros.ipsolution.R;
import com.epsilonpros.ipsolution.interfaces.GeneralInterface;
import com.epsilonpros.ipsolution.volleyAsyncTask.AsyncTaskVolley;

import net.qiujuer.genius.graphics.Blur;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by Sri on 18/12/16.
 */

public class Utils {

    public static boolean isServiceRunning(Context ctx, Class serviceClass) {
        ActivityManager manager = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getClass().getCanonicalName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public static boolean isActivityRunning(Context context, Class activityClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }
    public static void startService(Context context, Class serviceClass){
        Intent intent = new Intent(context, serviceClass);
        context.startService(intent);
    }
    public static void startActivity(Context context, Class serviceClass){
        Intent intent = new Intent(context, serviceClass);
        context.startActivity(intent);
    }
    public static void openInBrowser(Context context, String url){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    public static void openUri(Context context, Uri uri){
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        context.startActivity(intent);
    }

    public static PointF getPointOfView(View view){
        if (view == null) return null;

        int[] twoLocation = new int[2];
        view.getLocationOnScreen(twoLocation);
        return new PointF(twoLocation[0] + view.getWidth() / 2f, twoLocation[1] + view.getHeight() / 2f);
    }

    public final static long ONE_SECOND = 1000;
    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long ONE_DAY = ONE_HOUR * 24;

    /**
     * converts time (in milliseconds) to human-readable format
     * "<w> days, <x> hours, <y> minutes and (z) seconds"
     */

    public static String getTimeInHMS(long time){
        String sep = ":";

        long timeInSec = time/1000l;
        String sec = String.format("%02d",timeInSec%60l);

        long timeMin = timeInSec / 60l;
        String min = String.format("%02d",timeMin%60l);

        long timeH = timeMin / 60l;
        String h = String.format("%02d",timeH);

        return h+sep+min+sep+sec;
    }

    public static short[] getAudioSample(Context context,@RawRes int rawRes){
        InputStream is = context.getResources().openRawResource(rawRes);
        return getAudioSample(is);
    }

    public static short[] getAudioSample(File file){
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            return getAudioSample(is);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static short[] getAudioSample(InputStream is){
        byte[] data = null;
        try {
                data = IOUtils.toByteArray(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        ShortBuffer sb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        short[] samples = new short[sb.limit()];
        sb.get(samples);
        return samples;
    }
    
    public static String millisToLongDHMS(long duration) {
        StringBuffer res = new StringBuffer();
        boolean isDay = false, isHr = false, isMin = false;
        long temp = 0;
        if (duration >= ONE_SECOND) {
            temp = duration / ONE_DAY;
            if (temp > 0) {
                isDay = true;
                duration -= temp * ONE_DAY;
                res.append(temp >= 10 ? temp : "0" + temp).append("d ");
            }
            temp = duration / ONE_HOUR;
            if (temp > 0) {
                isHr = true;
                duration -= temp * ONE_HOUR;
                res.append(temp >= 10 ? temp : "0" + temp).append("h ");
            }
            if (isDay)
                return res.toString() + ((temp > 0) ? "" : "00h");
            temp = duration / ONE_MINUTE;
            if (temp > 0) {
                isMin = true;
                duration -= temp * ONE_MINUTE;
                res.append(temp >= 10 ? temp : "0" + temp).append("m ");
            }
            if (isHr)
                return res.toString() + ((temp > 0) ? "" : "00m");

            temp = duration / ONE_SECOND;
            if (temp > 0) {
                res.append(temp >= 10 ? temp : "0" + temp).append("s");
            }
            return res.toString() + ((temp > 0) ? "" : "00s");
        } else {
            return "0s";
        }
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = !si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
                + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }
    public static boolean isMarshmallow(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static String[] toStringArray(String jArray) {
        if (jArray == null)
            return null;
        try {

            JSONArray array = new JSONArray(jArray);

            String[] arr = new String[array.length()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = array.optString(i);
            }
            return arr;
        } catch (JSONException jse) {
            return null;
        }
    }

    public static int getTargetSDKVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException nnf) {
            return -1;
        }
    }

    public static boolean isMobileDataEnabled(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ?
                Settings.Global.getInt(context.getContentResolver(), "mobile_data", 1) == 1 :
                Settings.Secure.getInt(context.getContentResolver(), "mobile_data", 1) == 1;
    }

    public static byte[] macAddressToByteArray(String macString) {
        String[] mac = macString.split("[:\\s-]");
        byte[] macAddress = new byte[6];
        for (int i = 0; i < mac.length; i++) {
            macAddress[i] = Integer.decode("0x" + mac[i]).byteValue();
        }
        return macAddress;
    }

        public  static String dateFormat =  "yyyy-MM-dd kk:mm:ss";
        public  static String dateFormatSansSeconde =  "yyyy-MM-dd kk:mm";

        private Utils() {
        }

        public static String encrypteStringMD5(String mdp){
            String newMDP = mdp;

        /*try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(mdp.getBytes());

            byte[] b =  m.digest();

            StringBuffer buf = new StringBuffer();

            for (int i = 0; i<b.length; i++){
                buf.append(Integer.toHexString(0xFF & b[i]));
            }

            newMDP = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
*/
            return newMDP ;
        }

        public static String encryoteMD5(String mdp ){

            String newMDP = mdp;

            //   newMDP = new String(Hex.encodeHex(Diges))

            return newMDP;
        }

        public static String decrypteNoms(String mdp){

            String newMDP = mdp;

            return newMDP ;
        }

        public static void decodeJsonElement(String fromServeur, GeneralInterface<JSONObject> generalInterface){

            try {

                JSONArray jsonArray = new JSONArray(fromServeur);
                JSONObject[] jsonObjects = new JSONObject[jsonArray.length()];

                for (int i = 0; i<jsonArray.length(); i++){
                    jsonObjects[i] = jsonArray.getJSONObject(i);
                }
                generalInterface.onDone(jsonObjects);

            } catch (JSONException e) {
                generalInterface.onDone(null);
                e.printStackTrace();
            }
        }
        public  static String writeToFile(Context context, String contentString,String dirFileName, String fileName){

            String  filePath = null;

            File dirRoot = createOrGetRootDir(context);

            if(dirRoot!=null){

                File dirFile = new File(dirRoot, dirFileName);
                if(!dirFile.exists()){
                    dirFile.mkdirs();
                }
                if (dirFile.exists()){
                    File file = new File(dirFile, fileName);

                    try {
                        OutputStream os = new FileOutputStream(file);
                        BufferedOutputStream bos = new BufferedOutputStream(os);
                        bos.write(contentString.getBytes());

                        bos.flush();
                        bos.close();
                        os.close();

                        filePath = file.getAbsolutePath();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
            return filePath;
        }

        public static boolean createRootDir(Context context){
            boolean isDone;
            Resources res = context.getResources();

            File file = new File(Environment.getExternalStorageDirectory(), "."+res.getString(R.string.app_name));
            if (!file.exists()) isDone = file.mkdir();
            else isDone = true;

            return isDone;
        }

        public static File createOrGetRootDir(Context context){

            File fileDir = null;

            boolean isDone;
            Resources res = context.getResources();

            File file = new File(Environment.getExternalStorageDirectory(), "."+res.getString(R.string.app_name));
            if (!file.exists()) isDone = file.mkdir();
            else isDone = true;

            if(file.exists()){
                fileDir = file;
            }

            return fileDir;
        }

        public static File fileDir(Context context){
            Resources res = context.getResources();

            File file = new File(Environment.getExternalStorageDirectory(), "."+res.getString(R.string.app_name));
            return file;

        }

  /*  public static void convertHTMLToXML(String html){

        XMLReader tagSoupReader = new org.ccil.cowan.tagsoup.Parser();
        Transformer identityTransformer = null;
        try {
            identityTransformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }

//        InputStream inputStream = "sdddd".getBytes();
        Reader sourceReader = new FileReader(sourceFile);

        InputSource sourceInputSource = new InputSource(sourceReader);

        Source xmlSource = new SAXSource(sourceInputSource);

        Result outputTarget = new StreamResult(outputFile);
        try {
            identityTransformer.transform(xmlSource, outputTarget);
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }
*/

        public static String saveToIntene(Context context, String serverReponse, String FileName){

            String localePath = serverReponse;

            return localePath;
        }


        public static String rendSize8(String string){

            String newText = null;


            if(string != null){

                int size = string.length();
                int rest = 8- size;
                int ilFallait = rest;

                String avant = "", apres = "";

                while (rest != 0 ){
                    if (rest*2>  ilFallait){
                        avant+= " ";
                    }
                    else apres += " ";
                    rest--;
                }
                newText = avant+string+apres;
                return newText;
            }
            return  string;
        }

        public static String rendSizeLongDe(String string, int l){

            String newText = null;


            if(string != null){

                int size = string.length();
                int rest = l- size;
                int ilFallait = rest;

                if (rest >0){

                    String avant = "", apres = "";

                    while (rest != 0 ){
                        if (rest*2<  ilFallait){
                            avant+= " ";
                        }
                        else apres += " ";
                        rest--;
                    }
                    newText = avant+string+apres;
                    return newText;
                }else {
                    return string;
                }

            }
            return  string;
        }


        public static Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int width) {

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            int height = width;

            try {
                float ratio = (float) w / (float) h;
                width = (int) (height * ratio);

            } catch (Exception e) {
            }

            Bitmap bitScaled = Bitmap.createScaledBitmap(bitmap, width, height, true);

            Bitmap output = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            paint.setColor(Color.parseColor("#BAB399"));
            paint.setStrokeWidth(2.0f);

            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawCircle(height / 2 + 0.7f, height / 2 + 0.7f, height / 2 + 0.1f, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            int left = (height - width) / 2;
            int top = 0;

            canvas.drawBitmap(bitScaled, left, top, paint);
            return output;
        }

          public static Bitmap getBlurBitmap(Bitmap bitmap, int color){
               Bitmap b = Blur.onStackBlur(bitmap.copy(Bitmap.Config.RGB_565,true),75);
               Canvas canvas = new Canvas(b);
               canvas.drawColor(color);

               return b;
           }

    public static void sendOnServer(Context context, String urlServeur, String TAG, ArrayList<String[]> stringsText,Response.Listener<String> listener, Response.ErrorListener errorListener){
        if (Looper.myLooper() == Looper.getMainLooper()) throw new RuntimeException("Connection on MainLooper");

            AsyncTaskVolley asyncTaskVolley = new AsyncTaskVolley(urlServeur,TAG,stringsText,listener,errorListener);
            asyncTaskVolley.setPriority(Request.Priority.IMMEDIATE);
            asyncTaskVolley.setShouldCache(false);
            asyncTaskVolley.setRetryPolicy(new DefaultRetryPolicy(1000*10,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            asyncTaskVolley.execute(context);
    }
    public static void sendOnServer(Context context,int method,  String urlServeur, String TAG, ArrayList<String[]> stringsText,Response.Listener<String> listener, Response.ErrorListener errorListener){
        if (Looper.myLooper() == Looper.getMainLooper()) throw new RuntimeException("Connection on MainLooper");

        AsyncTaskVolley asyncTaskVolley = new AsyncTaskVolley(method, urlServeur,TAG,stringsText,listener,errorListener);
        asyncTaskVolley.setPriority(Request.Priority.IMMEDIATE);
        asyncTaskVolley.setShouldCache(false);
        asyncTaskVolley.setRetryPolicy(new DefaultRetryPolicy(1000*10,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        asyncTaskVolley.execute(context);
    }

    public static void sendOnServer(String urlServeur, ArrayList<String[]> stringsText, ArrayList<String[]> fileText,Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (Looper.myLooper() == Looper.getMainLooper())
            throw new RuntimeException("Connection on MainLooper");

        String reponse = null;

        if (urlServeur != null) {

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            int i = 0;
            if (stringsText != null) {
                int tailleText = stringsText.size();

                while (i < tailleText) {

                    String[] item = stringsText.get(i);
                    try {
                        entity.addPart(item[0], new StringBody(item[1], Charset.forName("UTF-8")));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
                i = 0;
            }
            if (fileText != null) {
                int tailleText = fileText.size();

                while (i < tailleText) {
                    String[] item = fileText.get(i);
                    try {

                        entity.addPart(item[0], new FileBody(new File(item[1])));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    i++;
                }
            }
            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(urlServeur);
                httpPost.setEntity(entity);

                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity hEntity = response.getEntity();

                if (hEntity != null) {
                    reponse = EntityUtils.toString(hEntity);
                    android.util.Log.i("Rep ", reponse);
                    listener.onResponse(reponse);
                }

            } catch (Exception e) {
                e.printStackTrace();
                reponse = null;
                errorListener.onErrorResponse(new VolleyError(e.getMessage()));
                android.util.Log.i("Rep Catch", e.getMessage());
            }
        }
    }
        public static int getScreenHeight(Context context) {
            final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            final DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);
            return metrics.heightPixels;
        }

        public static int getScreenWidth(Context context) {
            final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            final DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);
            return metrics.widthPixels;
        }

        public static Bitmap getInputStreamBitmap(Context context, Uri uri, int width, int height){
            InputStream image_stream = null;

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                image_stream = context.getContentResolver().openInputStream(uri);

                BitmapFactory.decodeStream(image_stream,new Rect(),options);

                options.inSampleSize = calculateInSampleSize(options, width, height);
                options.inJustDecodeBounds = false;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return BitmapFactory.decodeStream(image_stream,new Rect(),options);
        }

        public static Bitmap getInputRawBitmap(Resources res, int rawImage, int width, int height){
            InputStream image_stream = null;

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                image_stream = res.openRawResource(rawImage) ;

                BitmapFactory.decodeStream(image_stream,new Rect(),options);

                options.inSampleSize = calculateInSampleSize(options, width, height);
                options.inJustDecodeBounds = false;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return BitmapFactory.decodeStream(image_stream,new Rect(),options);
        }

        public static Bitmap decodeFile(String filePath, int width, int height){

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            try {

                BitmapFactory.decodeFile(filePath,options);

                options.inSampleSize = calculateInSampleSize(options, width, height);

                options.inJustDecodeBounds = false;
                options.inPreferQualityOverSpeed = true;

                return   getScaledBitmap(getRotatedBitmap(BitmapFactory.decodeFile(filePath,options), filePath), width);

            } catch (Exception e) {
                options.inJustDecodeBounds = false;
                return  BitmapFactory.decodeFile(filePath,options);

            }
        }

        public static String getPath(Context context, Uri uri) {

            try {
                Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                String document_id = cursor.getString(0);
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
                cursor.close();

                cursor = context.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
                cursor.moveToFirst();
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();

                return path;
            }
            catch (Exception e){
                String filePath =  uri.toString();
                return  filePath.replaceFirst("file://","");
            }
        }


        public static Bitmap getScaledBitmap(Bitmap inBitmap, int reqSize) {

            int h = inBitmap.getHeight();
            int w = inBitmap.getWidth();

            float ratio =(float)w/(float)h;

            if(w > h){

                int nW = reqSize;
                int nH = Math.round((float)nW / ratio);

                return Bitmap.createScaledBitmap(inBitmap, nW, nH, true);

            }else {

                int nH = reqSize;
                int nW = Math.round((float)nH * ratio);

                return Bitmap.createScaledBitmap(inBitmap, nW, nH, true);

            }
        }

        public static Bitmap getRotatedBitmap(Bitmap img, String filePath) {

            try {

                ExifInterface ei;

                if (Build.VERSION.SDK_INT > 23)
                    ei = new ExifInterface(filePath);
                else
                    ei = new ExifInterface(filePath);

                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        return rotateImage(img, 90);
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        return rotateImage(img, 180);
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        return rotateImage(img, 270);
                    default: return img;
                }
            }
            catch (Exception e) {
                return img;
            }

        }

        public static Bitmap rotateImage(Bitmap img, int degree) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            img.recycle();
            return rotatedImg;
        }

        public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                             int reqWidth, int reqHeight) {

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            try {
                Bitmap bitmap = BitmapFactory.decodeResource(res, resId, options);
                try {
                    return getScaledBitmap(bitmap,reqHeight);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bitmap;
            }catch (OutOfMemoryError e){
                try {
                    return BitmapFactory.decodeResource(res, resId, options);
                }catch (OutOfMemoryError ee){
                    return null;
                }
            }
        }

        public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                // Calculate ratios of height and width to requested height and width
                final int heightRatio = Math.round((float) height / (float) reqHeight);
                final int widthRatio = Math.round((float) width / (float) reqWidth);

                int firsInSample = heightRatio < widthRatio ? heightRatio : widthRatio;

                int pow = 2 ;

                while (pow < firsInSample ){
                    pow = (int) Math.pow(pow, 2);
                }

                inSampleSize = pow;
            }
            return inSampleSize;
        }

        public static String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while(itr.hasNext()){

                String key= itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            return result.toString();
        }

        public static String getActualDate(){

            Calendar calendar = Calendar.getInstance();

            long milliSeconde = calendar.getTimeInMillis();

            return DateFormat.format(dateFormat,milliSeconde).toString();
        }

        public static String getActualDate(String dateFormat){

            Calendar calendar = Calendar.getInstance();

            long milliSeconde = calendar.getTimeInMillis();

            return DateFormat.format(dateFormat,milliSeconde).toString();
        }

        public static String getActualFrenchFormat(){

            String dateFormat = "dd-MM-yyyy kk:mm:ss";

            Calendar calendar = Calendar.getInstance();

            long milliSeconde = calendar.getTimeInMillis();

            return DateFormat.format(dateFormat,milliSeconde).toString();
        }

        public static String getDateUS(long time){

            String dateFormat = "yyyy-MM-dd kk:mm:ss";

            long milliSeconde =time;

            return DateFormat.format(dateFormat,milliSeconde).toString();
        }

        public static String getDateFR(long time){

            String dateFormat = "dd-MM-yyyy kk:mm:ss";

            long milliSeconde =time;

            return DateFormat.format(dateFormat,milliSeconde).toString();
        }

        public static long getTimeInMillis(String stringDate){
            long time = 0;
            try {
                Date date = new SimpleDateFormat(dateFormat).parse(stringDate);
                time = date.getTime();

            } catch (ParseException e) {
                e.printStackTrace();
            }
            return time;
        }

        public static long getTimeInMillis(String stringDate, String dateFormat){
            long time = 0;
            try {
                Date date = new SimpleDateFormat(dateFormat).parse(stringDate);
                time = date.getTime();

            } catch (ParseException e) {
                e.printStackTrace();
            }
            return time;
        }

        public static String getMois(Context context, int moisNumeric){

            String [] moiss = context.getResources().getStringArray(R.array.moisDeLannee);
            moisNumeric-=1;

            return moiss[moisNumeric];
        }
    public static String getMoisAbr(Context context, int moisNumeric){

        String [] moiss = context.getResources().getStringArray(R.array.moisDeLanneeAbrev);
        moisNumeric-=1;

        return moiss[moisNumeric];
    }

        public static String getGoodTimeFrench(Context context, String dateFrench){
            try {

                String annee = dateFrench.substring(0,4);
                String mois = dateFrench.substring(5,7);
                String jour = dateFrench.substring(8,10);
                String heures = dateFrench.substring(11);

                return jour+"/"+mois+"/"+annee+" "+heures;
            }catch (IndexOutOfBoundsException e){
                return dateFrench;
            }
        }

    public static HashMap<String, String>  getDateElements(long time){

        HashMap<String, String  > dates = new HashMap<>();

        String dateFormat = "yyyy-MM-dd kk:mm:ss";
        String date = DateFormat.format(dateFormat,time).toString();

        try {

            String annee = date.substring(0,4);
            String mois = date.substring(5,7);
            String jour = date.substring(8,10);
            String heures = date.substring(11);

            dates.put("annee", annee);
            dates.put("mois", mois);
            dates.put("jour", jour);
            dates.put("heures", heures);

            }catch (IndexOutOfBoundsException e){}

        return   dates;
    }

    public static HashMap<String, String>  getDateElements(String date){

        HashMap<String, String  > dates = new HashMap<>();
        try {

            String annee = date.substring(0,4);
            String mois = date.substring(5,7);
            String jour = date.substring(8,10);
            String heures = date.substring(11);
            String heuresSansSec = date.substring(11,16);

            dates.put("annee", annee);
            dates.put("mois", mois);
            dates.put("jour", jour);
            dates.put("heures", heures);
            dates.put("heures-sansSec", heuresSansSec);

        }catch (IndexOutOfBoundsException e){}

        return   dates;
    }

    public static String getFrencDateSansHeure(String date){

            String dateSH = date;
            try {
                dateSH = date.substring(0, date.indexOf(" "));
            }catch (IndexOutOfBoundsException e){
            }
            if(dateSH.length() < 8){
                dateSH = date.substring(0, 10);
            }

            return   dateSH;
        }

        public static String getDateArrange(String date){

            String dateSH = date;
            try {
                if(date.length() <19){
                    dateSH+=":00";
                }

            }catch (IndexOutOfBoundsException e){
            }
            if(dateSH.length() < 8){
                dateSH = date.substring(0, 10);
            }

            return   dateSH;
        }

        public static String getTextTestLength(int length){

            String m = "";

            for (int i = 0; i<length; i++) {
                m += "M";
            }

            return   m;
        }

        public static String getFrencFormatOfDate(String date){

            try {

                String annee = date.substring(0,4);
                String mois = date.substring(5,7);
                String jour = date.substring(8,10);
                String heures = date.substring(11);

                return jour+"-"+mois+"-"+annee+" "+heures;
            }catch (IndexOutOfBoundsException e){
                return date;
            }
        }

        public static String getFrencWithGoodMois(Context c, String date){

            try {

                String jour = date.substring(0,2);
                String mois = date.substring(3,5);
                String annee = date.substring(6,10);
                String heures = date.substring(11);

                return jour+" "+ Utils.getMois(c,Integer.parseInt(mois))+" "+annee+" "+heures;
            }catch (IndexOutOfBoundsException e){
                return date;
            }
        }

        public static String getFrencWithGoodMoisSansHeure(Context c, String date){

            try {

                String jour = date.substring(0,2);
                String mois = date.substring(3,5);
                String annee = date.substring(6,10);

                return jour+" "+ Utils.getMois(c,Integer.parseInt(mois))+" "+annee+"";
            }catch (IndexOutOfBoundsException e){
                return date;
            }
        }

        public static HashMap<String, String> getFrencWithGoodMoisSansHeureElement(Context c, String date){

            HashMap<String, String> hashMap = new HashMap<>();

            try {

                String jour = date.substring(0,2);
                String mois = date.substring(3,5);
                String annee = date.substring(6,10);
                String heures = date.substring(11);

                String dateGood = jour+" "+ Utils.getMois(c,Integer.parseInt(mois))+" "+annee;

                hashMap.put("date", dateGood);
                hashMap.put("heure", heures);

                return hashMap;
            }catch (Exception e){
                return null;
            }
        }

        public static String getUSFormatOfDate(String date){

            try {

                String jour = date.substring(0,2);
                String mois = date.substring(3,5);
                String annee = date.substring(6,10);
                String heures = date.substring(11);

                return annee+"-"+mois+"-"+jour+" "+heures;
            }catch (IndexOutOfBoundsException e){
                return date;
            }

        }
        public static String firtsToUpperCase(String s){
            String c = String.valueOf(s.charAt(0));
            String googString = s.replaceFirst(c, c.toUpperCase());

            return googString;
        }
        public static String cursorToJson(Cursor crs){
            JSONArray arr = new JSONArray();
                crs.moveToFirst();
                while (!crs.isAfterLast()) {
                    int nColumns = crs.getColumnCount();
                    JSONObject row = new JSONObject();
                    for (int i = 0 ; i < nColumns ; i++) {
                        String colName = crs.getColumnName(i);
                        if (colName != null) {
                            String val = "";
                            try {
                                switch (crs.getType(i)) {
                                    case Cursor.FIELD_TYPE_BLOB   : row.put(colName, crs.getBlob(i).toString()); break;
                                    case Cursor.FIELD_TYPE_FLOAT  : row.put(colName, crs.getDouble(i))         ; break;
                                    case Cursor.FIELD_TYPE_INTEGER: row.put(colName, crs.getLong(i))           ; break;
                                    case Cursor.FIELD_TYPE_NULL   : row.put(colName, null)                     ; break;
                                    case Cursor.FIELD_TYPE_STRING : row.put(colName, crs.getString(i))         ; break;
                                }
                            } catch (JSONException e) {
                            }
                        }
                    }
                    arr.put(row);
                    if (!crs.moveToNext())
                        break;
                }
                crs.close(); // close the cursor
                return arr.toString();
        }
}