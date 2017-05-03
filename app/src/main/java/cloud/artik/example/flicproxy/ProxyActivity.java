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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import cloud.artik.client.ApiCallback;
import cloud.artik.client.ApiException;
import cloud.artik.model.Device;
import cloud.artik.model.DeviceEnvelope;
import cloud.artik.model.Message;
import cloud.artik.model.MessageIDEnvelope;
import cloud.artik.model.User;
import cloud.artik.model.UserEnvelope;
import io.flic.lib.FlicAppNotInstalledException;
import io.flic.lib.FlicBroadcastReceiver;
import io.flic.lib.FlicBroadcastReceiverFlags;
import io.flic.lib.FlicButton;
import io.flic.lib.FlicManager;
import io.flic.lib.FlicManagerInitializedCallback;


public class ProxyActivity extends Activity {
    private static final String TAG = "ProxyActivity";

    private TextView mWelcome;
    private TextView mArtikCloudDeviceInfo;
    private Button mBtnToGrabFlic;
    private TextView mFlicBtnInfo;
    private TextView mMsgSent;
    private TextView mSendResponse;

    private FlicEventReceiver mFlicEventReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);

        mWelcome = (TextView)findViewById(R.id.welcome);
        mArtikCloudDeviceInfo = (TextView)findViewById(R.id.artikcloudDevInfoView);
        mBtnToGrabFlic = (Button)findViewById(R.id.btnToGrabFlic);
        mMsgSent = (TextView)findViewById(R.id.postMsgView);
        mSendResponse = (TextView)findViewById(R.id.sendmsg_response);
        mFlicBtnInfo = (TextView)findViewById(R.id.flicBtnInfoView);

        getUserInfo();

        //Set the credentials which can be options from flic website
        Config.setFlicCredentials();

        mFlicEventReceiver = new FlicEventReceiver();

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mFlicEventReceiver, new IntentFilter("io.flic.FLICLIB_EVENT"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mFlicEventReceiver);
    }

    @Override
    public void onBackPressed()
    {
        // Disable going back to the previous screen
    }

    //////////////// FLIC Button ////////////////////////////////////

    //Get the button from the Flic app
    public void grabButton(View v) {
        try {
            FlicManager.getInstance(this, new FlicManagerInitializedCallback() {
                @Override
                public void onInitialized(FlicManager manager) {
                    manager.initiateGrabButton(ProxyActivity.this);
                }
            });
        } catch (FlicAppNotInstalledException err) {
            Toast.makeText(this, "Flic App is not installed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        FlicManager.getInstance(this, new FlicManagerInitializedCallback() {
            @Override
            public void onInitialized(FlicManager manager) {
                FlicButton button = manager.completeGrabButton(requestCode, resultCode, data);
                if (button != null) {
                    button.registerListenForBroadcast(FlicBroadcastReceiverFlags.CLICK_OR_DOUBLE_CLICK_OR_HOLD | FlicBroadcastReceiverFlags.REMOVED);
                    updateFlicButtonInfoViewOnUIThread(getString(R.string.flic_button_str) + "\n"
                            + getString(R.string.name_str) + " " + button.getName() + "\n"
                            + getString(R.string.id_str) + " " + button.getButtonId());
                    Toast.makeText(ProxyActivity.this, getString(R.string.grabbed_a_button_str), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProxyActivity.this, getString(R.string.failed_grab_str), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public class FlicEventReceiver extends FlicBroadcastReceiver {
        private static final String TAG = "FlicEventReceiver";

        @Override
        protected void onRequestAppCredentials(Context context) {
            Config.setFlicCredentials();
        }

        @Override
        public void onButtonSingleOrDoubleClickOrHold(Context context, FlicButton button, boolean wasQueued, int timeDiff, boolean isSingleClick, boolean isDoubleClick, boolean isHold) {
            try {
                Log.d(TAG, "Context: " + context + "button.color: " + button.getColor());
                postMsg(button, isSingleClick, isDoubleClick, isHold);
            } catch (Exception e) {
                Log.e("TAG", "FlicEventReceiver ran into exception", e);
            }
        }

        @Override
        public void onButtonRemoved(Context context, FlicButton button) {
            Log.d(TAG, "removed");
            Toast.makeText(context, "Button was removed", Toast.LENGTH_SHORT).show();
        }
    }

    //////////////// ARTIK CLOUD API Calls ////////////////////////////////////
    private void getUserInfo()
    {
        final String tag = TAG + " getSelfAsync";
        try {
             ArtikCloudSession.getInstance().getUsersApi().getSelfAsync(new ApiCallback<UserEnvelope>() {
                @Override
                public void onFailure(ApiException exc, int statusCode, Map<String, List<String>> map) {
                    processFailure(tag, exc);
                }

                @Override
                public void onSuccess(UserEnvelope result, int statusCode, Map<String, List<String>> map) {
                    Log.v(TAG, "getSelfAsync::setupArtikCloudApi self name = " + result.getData().getFullName());
                    handleUserInfoOnUIThread(result.getData());
                }

                @Override
                public void onUploadProgress(long bytes, long contentLen, boolean done) {
                }

                @Override
                public void onDownloadProgress(long bytes, long contentLen, boolean done) {
                }
            });
        } catch (ApiException exc) {
            processFailure(tag, exc);
        }
    }

    private void addDevice() {
        final String tag = TAG + " addDeviceAsync";
        cloud.artik.model.Device device = new cloud.artik.model.Device();
        device.setDtid(Config.DEVICE_TYPE_ID); //flic button device type in ARTIK Cloud
        device.setUid(ArtikCloudSession.getInstance().getUserId());
        device.setName(Config.DEVICE_NAME); //Note this is a limitation --the name is always this one.
        try {
            ArtikCloudSession.getInstance().getDevicesApi().addDeviceAsync(device, new ApiCallback<DeviceEnvelope>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    Log.e(tag, "onFailure: e = " + e + "; statusCode = " + statusCode);
                    processFailure(tag, e);
                }

                @Override
                public void onSuccess(DeviceEnvelope result, int statusCode, Map<String, List<String>> responseHeaders) {
                    Log.v(tag, " onSuccess " + result.toString());
                    handleDeviceCreationSuccessOnUIThread(result.getData());
                }

                @Override
                public void onUploadProgress(long bytes, long contentLen, boolean done) {
                }

                @Override
                public void onDownloadProgress(long bytes, long contentLen, boolean done) {
                }
            });
        } catch (ApiException exc) {
            processFailure(tag, exc);
        }
    }

    public void postMsg(FlicButton btn, boolean isSingleClick, boolean isDoubleClick, boolean isHold) {
        final String tag = TAG + " sendMessageAsync";

        String artikCloudDeviceId = ArtikCloudSession.getInstance().getDeviceId();
        if (artikCloudDeviceId == null || artikCloudDeviceId.isEmpty()) {
            processFailure(TAG, new ApiException("Cannot post message! ARTIK Cloud Device ID is invalid: "
                    + artikCloudDeviceId));
            return;
        }

        // Construct the message
        Message msg = new Message();
        Map<String, Object> data = msg.getData();
        msg.setSdid(artikCloudDeviceId);
        msg.setTs(System.currentTimeMillis());
        data.put("id", btn.getButtonId());
        data.put("color", btn.getColor());
        data.put("name", btn.getName());
        data.put("isSingleClick", isSingleClick);
        data.put("isDoubleClick", isDoubleClick);
        data.put("isHold", isHold);

        updateMsgSentOnUIThread(msg.toString());

        try {
            ArtikCloudSession.getInstance().getMessagesApi().sendMessageAsync(msg, new ApiCallback<MessageIDEnvelope>() {
                @Override
                public void onFailure(ApiException exc, int i, Map<String, List<String>> stringListMap) {
                    processFailure(tag, exc);
                }

                @Override
                public void onSuccess(MessageIDEnvelope result, int i, Map<String, List<String>> stringListMap) {
                    Log.v(tag, " onSuccess response to sending message = " + result.getData().toString());
                    updateSendResponseOnUIThread(result.getData().toString());
                }

                @Override
                public void onUploadProgress(long bytes, long contentLen, boolean done) {
                }

                @Override
                public void onDownloadProgress(long bytes, long contentLen, boolean done) {
                }
            });
        } catch (ApiException exc) {
            processFailure(tag, exc);
        }
    }

    //////////////// Helpers ////////////////////////////////////
    private void handleUserInfoOnUIThread(final User user) {
        if (user == null) {
            return;
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWelcome.setText(getString(R.string.welcome_txt) + " " + user.getFullName());
                ArtikCloudSession.getInstance().setUserId(user.getId());
                String deviceID = ArtikCloudSession.getInstance().getDeviceId();
                if (deviceID == null || deviceID.isEmpty()) {
                    addDevice();
                } else {  //Reuse the stored ARTIK Cloud device ID
                    mBtnToGrabFlic.setEnabled(true);
                }
            }
        });
    }

    private void handleDeviceCreationSuccessOnUIThread(final Device newDevice) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "ARTIK Cloud device " + newDevice.getId() + "creation succeeded!", Toast.LENGTH_SHORT).show();
                ArtikCloudSession.getInstance().setDeviceId(newDevice.getId());
                mArtikCloudDeviceInfo.setText(getString(R.string.artikcloud_device_info_str) + "\n"
                        + getString(R.string.name_str) + " " + newDevice.getName() + "\n"
                        + getString(R.string.id_str) + " " + newDevice.getId() );
                mBtnToGrabFlic.setEnabled(true);

            }
        });
    }

    static void showErrorOnUIThread(final String text, final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(activity.getApplicationContext(), text, duration);
                toast.show();
            }
        });
    }

    private void updateMsgSentOnUIThread(final String msgStr) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMsgSent.setText(getString(R.string.sent_msg_str) + "\n\n" + msgStr);
            }
        });
    }

    private void updateSendResponseOnUIThread(final String response) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSendResponse.setText(getString(R.string.response_str) + "\n\n" + response);
            }
        });
    }

    private void updateFlicButtonInfoViewOnUIThread(final String text) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFlicBtnInfo.setText(text);
            }
        });
    }



    private void processFailure(final String context, ApiException exc) {
        String errorDetail = getString(R.string.exception_str) + exc;
        Log.w(context, errorDetail);
        exc.printStackTrace();
        showErrorOnUIThread(context+errorDetail, ProxyActivity.this);
    }

} //ProxyActivity

