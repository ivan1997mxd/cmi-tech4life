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

package com.estethapp.media.web.addons;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.estethapp.media.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import org.json.JSONException;
import org.json.JSONObject;

import com.estethapp.media.util.App;
import com.estethapp.media.util.Prefs;
import com.estethapp.media.web.HttpCaller;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private SharedPreferences prefsInstance;
    private Prefs prefs;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "REFRESHED TOKEN: " + refreshedToken);

        prefs = Prefs.getInstance();
        prefsInstance = prefs.init(MyFirebaseInstanceIDService.this);

        prefsInstance.edit().putString(prefs.GCM_TOKEN , refreshedToken).commit();


        App.GCMToken = refreshedToken;

        if(App.profileModel.UserID > 0) {

            JSONObject jsonbody = new JSONObject();
            try {
                jsonbody.put("UserID",App.profileModel.UserID);
                jsonbody.put("GCMToken",App.profileModel.GCMToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            HttpCaller.
                    getInstance().
                    updateGCMToken(
                            jsonbody,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject o) {
                                    try {
                                        if (!o.getBoolean("Status")) {
                                            Toast.makeText(MyFirebaseInstanceIDService.this, getString(R.string.failed_update_token),Toast.LENGTH_SHORT).show();
                                    }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(MyFirebaseInstanceIDService.this, getString(R.string.failed_update_token),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    Toast.makeText(MyFirebaseInstanceIDService.this, getString(R.string.failed_update_token),Toast.LENGTH_SHORT).show();

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
