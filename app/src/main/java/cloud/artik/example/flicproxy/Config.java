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

import io.flic.lib.FlicManager;

class Config {
    // Copy them from the corresponding application in the ARTIK Cloud Developer Dashboard
    static final String CLIENT_ID = "YOUR_ARTIKCLOUD_CLIENT ID"; //aka application id

    static final String REDIRECT_URL = "cloud.artik.example.flicproxy://oauth2callback";

    // ARTIK Cloud device type id used by this app
    // device type name: "Example Flic Button"
    // You can get the device type id using the following ways
    //   -- login to https://api-console.artik.cloud/
    //   -- Click "Get Device Types" api
    //   -- Fill in device name as above
    //   -- Click "Try it"
    //   -- The device type id is "id" field in the response body
    //
    // You can also use device type unique name to replace device type id in most API call
    // To get device type unique name using the following way:
    //   -- Go to Developer Dashboard Application Permission screen
    //   -- Click Add Device Type
    //   -- Type "Example Flic Button" which shows the unique device type name.
    static final String DEVICE_TYPE_ID = "cloud.artik.example.flicbutton"; // you can use unique name as device type id

    static final String DEVICE_NAME = "My AKC Flic";

    static void setFlicCredentials() {
        // Get your Flic credentials at https://partners.flic.io/partners/developers/credentials
         FlicManager.setAppCredentials("YOUR_APP_KEY_FROM_FLIC", "YOUR_APP_SECRET_FROM_FLIC", "Proxy Flic To ARTIKCloud");
    }
}

