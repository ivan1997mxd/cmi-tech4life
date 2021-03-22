package com.estethapp.media.web;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.estethapp.media.R;
import com.estethapp.media.util.App;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Irfan Ali on 12/29/2016.
 */


public class HttpCaller {

    public static HttpCaller instance;
    public static Context context;
    private static RetryPolicy retryPolicy;
    public static ProgressDialog progressDialog;

    public static HttpCaller getInstance() {
        if (instance == null) {
            instance = new HttpCaller();
        }
        return instance;
    }


    public static boolean init(Context context1) {
        context = context1;
        retryPolicy = new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        return true;
    }

    public void setProgressDialog(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.processing_request));
        progressDialog.setCancelable(false);

    }

    public void registerCall(Context context, JSONObject jsonbody, final Response.Listener successLisetenr, final Response.ErrorListener errorLisetenr, Boolean showWaitingDailog) {
        setProgressDialog(context);
        if (showWaitingDailog) {
            progressDialog.show();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                WebURL.SIGNUP,
                jsonbody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        progressDialog.dismiss();
                        successLisetenr.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        errorLisetenr.onErrorResponse(volleyError);
                    }
                });
        // Setting retry policy
        jsonObjectRequest.setRetryPolicy(retryPolicy).setShouldCache(false).setTag("registerCall");
        App.requestQueue.add(jsonObjectRequest);
    }


    public void updateGCMToken(JSONObject jsonbody, final Response.Listener successLisetenr, final Response.ErrorListener errorLisetenr, Boolean showWaitingDailog) {
        if (jsonbody == null) {
            jsonbody = new JSONObject();
            try {
                jsonbody.put("UserID", App.profileModel.UserID);
                jsonbody.put("GCMToken", App.profileModel.GCMToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                WebURL.UPDATE_GCM,
                jsonbody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        successLisetenr.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        errorLisetenr.onErrorResponse(volleyError);
                    }
                });

        // Setting retry policy
        jsonObjectRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        60000,
                        7,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                .setShouldCache(false).setTag("updateGCMToken");

        App.requestQueue.add(jsonObjectRequest);
    }

    public void loginCall(Context context, JSONObject jsonbody, final Response.Listener successLisetenr, final Response.ErrorListener errorLisetenr, Boolean showWaitingDailog) {
        setProgressDialog(context);
        if (showWaitingDailog)
            progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                WebURL.LOGIN,//LoginSuccessfull.json,
                jsonbody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        progressDialog.dismiss();
                        successLisetenr.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        errorLisetenr.onErrorResponse(volleyError);
                    }
                });
        // Setting retry policy
        jsonObjectRequest.setRetryPolicy(retryPolicy).setShouldCache(false).setTag("loginCall");
        App.requestQueue.add(jsonObjectRequest);
    }

    public void updateProfileCall(Context context, JSONObject jsonbody,
                                  final Response.Listener successLisetenr, final Response.ErrorListener errorLisetenr,
                                  Boolean showWaitingDailog) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                WebURL.PROFILE,//LoginSuccessfull.json,
                jsonbody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        successLisetenr.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        errorLisetenr.onErrorResponse(volleyError);
                    }
                });
        // Setting retry policy
        jsonObjectRequest.setRetryPolicy(retryPolicy).setShouldCache(false).setTag("updateprofile");
        App.requestQueue.add(jsonObjectRequest);
    }

    public void uploadImage(JSONObject jsonbody, final Response.Listener successLisetenr, final Response.ErrorListener errorLisetenr) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                WebURL.UPLOADIMAGE,
                jsonbody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        successLisetenr.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        errorLisetenr.onErrorResponse(volleyError);
                    }
                });
        // Setting retry policy
        jsonObjectRequest.setRetryPolicy(retryPolicy).setShouldCache(false).setTag("uploadImage");
        jsonObjectRequest.setTag("uploadImage");
        App.requestQueue.add(jsonObjectRequest);
    }

    public void contactCall(Context context, int userID, final Response.Listener successLisetenr, final Response.ErrorListener errorLisetenr, Boolean showWaitingDailog) {
        init(context);

        JSONObject jsonbody = new JSONObject();
        try {
            jsonbody.put("userid", userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                WebURL.CONTACT,
                jsonbody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //progressDialog.dismiss();
                        successLisetenr.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //progressDialog.dismiss();
                        errorLisetenr.onErrorResponse(volleyError);
                    }
                });
        // Setting retry policy
        jsonObjectRequest.setRetryPolicy(retryPolicy).setShouldCache(false).setTag("registerCall");
        App.requestQueue.add(jsonObjectRequest);
    }

    public void mycontactCall(Context context, String patientID, final Response.Listener successLisetenr, final Response.ErrorListener errorLisetenr, Boolean showWaitingDailog) {
        init(context);

        JSONObject jsonbody = new JSONObject();
        try {
            jsonbody.put("userid", "" + patientID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                WebURL.MYCONTACT,
                jsonbody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //progressDialog.dismiss();
                        successLisetenr.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //progressDialog.dismiss();
                        errorLisetenr.onErrorResponse(volleyError);
                    }
                });
        // Setting retry policy
        jsonObjectRequest.setRetryPolicy(retryPolicy).setShouldCache(false).setTag("registerCall");
        App.requestQueue.add(jsonObjectRequest);
    }

    public void addContatToNetwork(Context context, JSONObject jsodnbody, final Response.Listener successLisetenr, final Response.ErrorListener errorLisetenr, Boolean showWaitingDailog) {
        init(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                WebURL.ADDCONTACT,
                jsodnbody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //progressDialog.dismiss();
                        successLisetenr.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //progressDialog.dismiss();
                        errorLisetenr.onErrorResponse(volleyError);
                    }
                });
        // Setting retry policy
        jsonObjectRequest.setRetryPolicy(retryPolicy).setShouldCache(false).setTag("registerCall");
        App.requestQueue.add(jsonObjectRequest);
    }

    public void removeContatToNetwork(Context context, JSONObject jsodnbody, final Response.Listener successLisetenr, final Response.ErrorListener errorLisetenr, Boolean showWaitingDailog) {
        init(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                WebURL.REMOVECONTACT,
                jsodnbody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //progressDialog.dismiss();
                        successLisetenr.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //progressDialog.dismiss();
                        errorLisetenr.onErrorResponse(volleyError);
                    }
                });
        // Setting retry policy
        jsonObjectRequest.setRetryPolicy(retryPolicy).setShouldCache(false).setTag("registerCall");
        App.requestQueue.add(jsonObjectRequest);
    }

    public void forgetPassword(Context context, JSONObject jsodnbody, final Response.Listener successLisetenr, final Response.ErrorListener errorLisetenr, Boolean showWaitingDailog) {
        setProgressDialog(context);
        if (showWaitingDailog)
            progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                WebURL.FORGETPASS,
                jsodnbody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        progressDialog.dismiss();
                        successLisetenr.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        errorLisetenr.onErrorResponse(volleyError);
                    }
                });
        // Setting retry policy
        jsonObjectRequest.setRetryPolicy(retryPolicy).setShouldCache(false).setTag("registerCall");
        App.requestQueue.add(jsonObjectRequest);
    }

    public void sendNotification(JSONObject jsonbody, final Response.Listener successLisetenr, final Response.ErrorListener errorLisetenr) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                WebURL.INVITATION,
                jsonbody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        successLisetenr.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        errorLisetenr.onErrorResponse(volleyError);
                    }
                });

        // Setting retry policy
        jsonObjectRequest.setRetryPolicy(retryPolicy).setShouldCache(false).setTag("sendFriendRequest");
        App.requestQueue.add(jsonObjectRequest);
    }

    public void createGroup(JSONObject jsonbody, final Response.Listener successLisetenr, final Response.ErrorListener errorLisetenr, Boolean showWaitingDailog) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                WebURL.CREATEGROUP,//LoginSuccessfull.json,
                jsonbody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //progressDialog.dismiss();
                        successLisetenr.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // progressDialog.dismiss();
                        errorLisetenr.onErrorResponse(volleyError);
                    }
                });
        // Setting retry policy
        jsonObjectRequest.setRetryPolicy(retryPolicy).setShouldCache(false).setTag("loginCall");
        App.requestQueue.add(jsonObjectRequest);
    }

    public void loadChat(int UserId, final Response.Listener successLisetenr, final Response.ErrorListener errorLisetenr) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                WebURL.MYCHAT + "?UserID=" + UserId,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //progressDialog.dismiss();
                        successLisetenr.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // progressDialog.dismiss();
                        errorLisetenr.onErrorResponse(volleyError);
                    }
                });

        // Setting retry policy
        jsonObjectRequest.setRetryPolicy(retryPolicy).setShouldCache(false).setTag("loginCall");
        App.requestQueue.add(jsonObjectRequest);
    }

    public void removeChatToNetwork(int chatID, final Response.Listener successLisetenr, final Response.ErrorListener errorLisetenr, Boolean showWaitingDailog) {
        init(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                WebURL.REMOVECHAT+ "?chatID=" + chatID,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //progressDialog.dismiss();
                        successLisetenr.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //progressDialog.dismiss();
                        errorLisetenr.onErrorResponse(volleyError);
                    }
                });
        // Setting retry policy
        jsonObjectRequest.setRetryPolicy(retryPolicy).setShouldCache(false).setTag("registerCall");
        App.requestQueue.add(jsonObjectRequest);
    }

    public void mychatCall(Context context, String patientID, final Response.Listener successLisetenr, final Response.ErrorListener errorLisetenr, Boolean showWaitingDailog) {
        init(context);

        JSONObject jsonbody = new JSONObject();
        try {
            jsonbody.put("userid", "" + patientID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                WebURL.CHAT,
                jsonbody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //progressDialog.dismiss();
                        successLisetenr.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //progressDialog.dismiss();
                        errorLisetenr.onErrorResponse(volleyError);
                    }
                });
        // Setting retry policy
        jsonObjectRequest.setRetryPolicy(retryPolicy).setShouldCache(false).setTag("registerCall");
        App.requestQueue.add(jsonObjectRequest);
    }

}
