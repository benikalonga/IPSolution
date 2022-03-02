package com.epsilonpros.ipsolution.volleyAsyncTask;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by KADI on 03/02/2018.
 */

public class AsyncTaskVolley extends StringRequest {

    String tag = AsyncTaskVolley.class.getSimpleName();
    Context context;

    private RequestQueue mRequestQueue;

    public AsyncTaskVolley(String url, String tag,ArrayList<String []> strings, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        setParams(strings);
        setRetryPolicy(new DefaultRetryPolicy(1200000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.tag = tag!=null? tag : this.tag;
    }

    public AsyncTaskVolley(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
        this.tag = tag!=null? tag : this.tag;
        setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public AsyncTaskVolley(int method, String url, String tag, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.tag = tag!=null? tag : this.tag;
        setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public AsyncTaskVolley(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.tag = tag!=null? tag : this.tag;
        setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
    public AsyncTaskVolley(int method, String url, String tag,ArrayList<String []> strings, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        setParams(strings);
        setRetryPolicy(new DefaultRetryPolicy(1200000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.tag = tag!=null? tag : this.tag;
    }

    Map<String, String> map;

    public void setParams(ArrayList<String[]> params){
        if(params!=null && params.size()>0){

            map = new HashMap<>();
            for (int i = 0 ; i < params.size(); i++){
                String[] strings = params.get(i);

                map.put(strings[0], strings[1]);
            }
        }
    }

    Priority priority = Priority.NORMAL;

    public void setPriority(Priority priority){
        this.priority = priority;
    }

    @Override
    public Priority getPriority() {
        return priority;
    }

    public void execute(Context context){
        addToRequestQueue(context,this, tag);
    }

    @Override
    public void setTag(Object tag) {
        super.setTag(tag);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {

        return map!=null? map : super.getParams();
    }

    @Override
    public String getTag() {
        return tag;
    }

    public RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Context context, Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? tag : tag);
        getRequestQueue(context).add(req);
    }

    public <T> void addToRequestQueue(Context context, Request<T> req) {
        req.setTag(tag);
        getRequestQueue(context).add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
