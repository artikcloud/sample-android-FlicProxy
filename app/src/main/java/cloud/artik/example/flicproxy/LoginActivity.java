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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class LoginActivity extends Activity {
    static final String TAG = "LoginActivity";

    private View mLoginView;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mWebView = (WebView)findViewById(R.id.webview);
        mWebView.setVisibility(View.GONE);
        mLoginView = findViewById(R.id.ask_for_login);
        mLoginView.setVisibility(View.VISIBLE);
        Button button = (Button)findViewById(R.id.btn);

        Log.v(TAG, "::onCreate");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Log.v(TAG, ": button is clicked.");
                    loadWebView();
                } catch (Exception e) {
                    Log.v(TAG, "Run into Exception");
                    e.printStackTrace();
                }
            }
        });

        // Reset to start a new session cleanly
        ArtikCloudSession.getInstance().reset();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebView() {
        Log.v(TAG, "::loadWebView");
        mLoginView.setVisibility(View.GONE);
        mWebView.setVisibility(View.VISIBLE);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String uri) {
                if (uri.startsWith(Config.REDIRECT_URL)) {
                    // Login succeed or back to login after signup

                    String ACCESS_TOKEN = "access_token=";
                    if (uri.contains(ACCESS_TOKEN)) { //login succeed
                        // e.g. Redirect URL has format http://myapp.redirect#expires_in=1209600&token_type=bearer&access_token=xxxx
                        // Extract OAuth2 access_token in URL
                        String[] sArray = uri.split(ACCESS_TOKEN);
                        String strHavingAccessToken = sArray[1];
                        String accessToken = strHavingAccessToken.split("&")[0];
                        onGetAccessToken(accessToken);
                    } else { // No access token available. Signup finishes and user clicks "Back to login"
                        // Example of uri: http://myapp.redirect?origin=signup&status=login_request
                        //
                        eraseAuthThenLogin();
                    }
                    return true;
                }
                // Load the web page from URL (login and grant access)
                return super.shouldOverrideUrlLoading(view, uri);
            }
        });

        String url = ArtikCloudSession.getInstance().getAuthorizationRequestUri();
        Log.v(TAG, "webview loading url: " + url);
        mWebView.loadUrl(url);
    }


    private void onGetAccessToken(String accessToken)
    {
        Log.d(TAG, "onGetAccessToken(" + accessToken +")");
        ArtikCloudSession.getInstance().setAccessToken(accessToken);
        ArtikCloudSession.getInstance().setupArtikCloudRestApis();
        startNextActivity();
    }

    private void startNextActivity() {
        Intent activityIntent = new Intent(this, ProxyActivity.class);
        startActivity(activityIntent);
    }

    private void eraseAuthThenLogin() {
        CookieManager.getInstance().removeAllCookie();
        mWebView.loadUrl(ArtikCloudSession.getInstance().getAuthorizationRequestUri());
    }
}
