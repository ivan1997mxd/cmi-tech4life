/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.estethapp.media.fcm;

import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.estethapp.media.util.App;
import com.estethapp.media.util.Prefs;
import com.estethapp.media.web.HttpCaller;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {


    private SharedPreferences userPrefsInance;
    private Prefs prefs;
    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "REFRESHED TOKEN: " + refreshedToken);
        JSONObject jsonbody = new JSONObject();
        try {
            jsonbody.put("UserID",App.profileModel.UserID);
            jsonbody.put("GCMToken",refreshedToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(refreshedToken != null)
            HttpCaller
                    .getInstance()
                    .updateGCMToken(
                            jsonbody,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject o) {
                                    try {
                                        if(!o.getBoolean("Status")) {
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                }
                            },true);




        prefs = Prefs.getInstance();
        Gson gson = new Gson();
        userPrefsInance = prefs.init(getApplicationContext());
        userPrefsInance.edit().putString(prefs.GCM_TOKEN,refreshedToken);

        App.profileModel.GCMToken = refreshedToken;

        if(App.profileModel.UserID > 0) {
            HttpCaller.
                    getInstance().
                    updateGCMToken(
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject o) {
                                    try {
                                        if (!o.getBoolean("Status")) {
                                            //Helper.showToast("Failed to update notification device token.", ToastStyle.ERROR);

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        //Helper.showToast("Failed to update notification device token.", ToastStyle.ERROR);
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    //Helper.showToast("Failed to update notification device token.", ToastStyle.ERROR);

                                }
                            },true);
        }

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }


}
