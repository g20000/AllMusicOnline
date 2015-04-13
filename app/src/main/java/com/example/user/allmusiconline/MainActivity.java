package com.example.user.allmusiconline;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKPhotoArray;
import com.vk.sdk.dialogs.VKCaptchaDialog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    TextView infoTextView = null;
    ListView vkImageList = null;
    String appId = "4814271";
    Button authButton = null;
    List<VKImage>vkImages = new ArrayList<>();
    public ItemListBaseAdapter listAdapter;
    private VKSdkListener listener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            infoTextView.setText("Hello from on captcha error!");
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            VKSdk.authorize(myVkScope);
            infoTextView.setText("Hello from on token expired!");
        }

        @Override
        public void onAccessDenied(VKError authorizationError) {
            infoTextView.setText("Hello from on access denied!");
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            //infoTextView.setText("Hello from on receive new token!");
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            infoTextView.setText("Hello from accept user token");
        }
    };

    private static String[] myVkScope = new String[]{VKScope.PHOTOS};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vkImageList = (ListView)findViewById(R.id.vkPlayList);
        listAdapter = new ItemListBaseAdapter(this, vkImages);
        vkImageList.setAdapter(listAdapter);
        vkImageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               }
           }

        );
        VKSdk.initialize(listener, appId);
        VKUIHelper.onCreate(this);
        infoTextView = (TextView)findViewById(R.id.textView);
        authButton = (Button) findViewById(R.id.authButton);

        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VKSdk.authorize(VKScope.PHOTOS);
            }
        });

        if (VKSdk.wakeUpSession()) {
            startLoading();
        } else {
            authButton.setVisibility(View.VISIBLE);
        }
    }

    public void startLoading(){
            VKRequest currentRequest = new VKRequest("photos.getAll", VKParameters.from(VKApiConst.FIELDS, "23240557,0,0,20,0,0"), VKRequest.HttpMethod.GET, VKPhotoArray.class);
            currentRequest.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    Log.d("VkDemoApp", "onComplete " + response);

                    VKPhotoArray usersArray = (VKPhotoArray) response.parsedModel;
                    vkImages.clear();

                    int showPhotosAmount = 0;
                    for (VKApiPhoto userFull : usersArray) {
                        ++showPhotosAmount;
                        vkImages.add(new VKImage(Uri.parse(userFull.photo_807)));
                        if(showPhotosAmount > 10){
                            break;
                        }
                    }
                    listAdapter.notifyDataSetChanged();
                }

                @Override
                public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                    super.attemptFailed(request, attemptNumber, totalAttempts);
                    Log.d("VkDemoApp", "attemptFailed " + request + " " + attemptNumber + " " + totalAttempts);
                }

                @Override
                public void onError(VKError error) {
                    super.onError(error);
                    Log.d("VkDemoApp", "onError: " + error);
                }

                @Override
                public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                    super.onProgress(progressType, bytesLoaded, bytesTotal);
                    Log.d("VkDemoApp", "onProgress " + progressType + " " + bytesLoaded + " " + bytesTotal);
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
    }
}
