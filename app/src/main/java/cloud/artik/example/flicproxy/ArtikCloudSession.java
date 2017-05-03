/*
 * Copyright (C) 2017 Samsung Electronics Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cloud.artik.example.flicproxy;

import android.util.Log;

import cloud.artik.api.DevicesApi;
import cloud.artik.api.MessagesApi;
import cloud.artik.api.UsersApi;
import cloud.artik.client.ApiClient;

class ArtikCloudSession {
    private final static String TAG = ArtikCloudSession.class.getSimpleName();

    private static final String ARTIK_CLOUD_AUTH_BASE_URL = "https://accounts.artik.cloud";

    private static ArtikCloudSession ourInstance = new ArtikCloudSession();

    private UsersApi mUsersApi = null;
    private MessagesApi mMessagesApi = null;
    private DevicesApi mDevicesApi = null;
    private String mAccessToken = null;
    private String mUserId = null;
    private String mDeviceId = null;

    static ArtikCloudSession getInstance() {
        return ourInstance;
    }

    void setAccessToken(String token) {
        if (token == null || token.length() <= 0) {
            Log.e(TAG, "Attempt to set an invalid token");
            mAccessToken = null;
            return;
        }
        mAccessToken = token;
    }

    void setupArtikCloudRestApis() {
        ApiClient apiClient = new ApiClient();
        apiClient.setAccessToken(mAccessToken);

        mUsersApi = new UsersApi(apiClient);
        mMessagesApi = new MessagesApi(apiClient);
        mDevicesApi = new DevicesApi(apiClient);
    }

    UsersApi getUsersApi() {
        return mUsersApi;
    }

    MessagesApi getMessagesApi() {
        return mMessagesApi;
    }

    DevicesApi getDevicesApi() {
        return mDevicesApi;
    }

    String getUserId() {
        return mUserId;
    }

    void setDeviceId(String deviceId) {mDeviceId = deviceId;}

    String getDeviceId() {return mDeviceId;}

    String getAuthorizationRequestUri() {
        //https://accounts.artik.cloud/authorize?client=mobile&client_id=xxxx&response_type=token&redirect_uri=android-app://redirect
        return ARTIK_CLOUD_AUTH_BASE_URL + "/authorize?client=mobile&response_type=token&" +
                "client_id=" + Config.CLIENT_ID + "&redirect_uri=" + Config.REDIRECT_URL;
    }

    void reset() {
        mUsersApi = null;
        mAccessToken = null;
        mUserId = null;
    }

    void setUserId(String uid) {
        if (uid == null || uid.length() <= 0) {
            Log.w(TAG, "setUserId() get null uid");
        }
        mUserId = uid;
    }

    public boolean canCallAKCApis() {
        return mUserId != null && mAccessToken != null;
    }

}