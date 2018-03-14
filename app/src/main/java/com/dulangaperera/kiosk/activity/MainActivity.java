package com.dulangaperera.kiosk.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.dulangaperera.kiosk.R;
import com.dulangaperera.kiosk.network.KioskApiService;
import com.dulangaperera.kiosk.network.model.request.SupportRequest;
import com.dulangaperera.kiosk.network.model.response.SupportResponse;
import com.dulangaperera.kiosk.service.GPSTracker;
import com.teamviewer.sdk.screensharing.api.TVConfigurationID;
import com.teamviewer.sdk.screensharing.api.TVCreationError;
import com.teamviewer.sdk.screensharing.api.TVSession;
import com.teamviewer.sdk.screensharing.api.TVSessionCallback;
import com.teamviewer.sdk.screensharing.api.TVSessionConfiguration;
import com.teamviewer.sdk.screensharing.api.TVSessionCreationCallback;
import com.teamviewer.sdk.screensharing.api.TVSessionError;
import com.teamviewer.sdk.screensharing.api.TVSessionFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.fabric.sdk.android.BuildConfig;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import rx.Observable;

/**
 * Created by Mihira on 10/30/2017.
 */

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    //==========================================//
    //          Permissions required            //
    //==========================================//
    private static final String[] PERMS={
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_SMS
    };

    private static final int INITIAL_REQUEST=1340;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private int TAKE_PHOTO_CODE = 0;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private final int CAMERA_REQUEST = 2;
    private Uri fileUri; // file url to store image
    private static final String IMAGE_DIRECTORY_NAME = "kiosk_folder";
    private File mediaFileName;
    private String filename, screenShotDescription;

    private WebView mWebView;
    private PopupWindow mpopup;
    private EditText etScreenShot;

    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));

    private GPSTracker gps;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        // Set up Crashlytics, disabled for debug builds
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();

        // Fabric for app monitoring
        Fabric.with(this, crashlyticsKit);

        // realm instance
        Realm realm = Realm.getDefaultInstance();

        //==========================================//
        //              Window flags                //
        //==========================================//
        int windowFlags =
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

        // window no title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // add window flags
        getWindow().addFlags(windowFlags);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Read the mobile number
        /*String phone = PhoneUtil.getPhoneNumber(MainActivity.this);
        Log.e("Phone No >>>", phone);*/

        //==========================================//
        //          Camera floating button          //
        //==========================================//
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_camera);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Camera action
                cameraAction();
            }
        });

        // check android version for manual permission
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // check app permissions
            if (ContextCompat.checkSelfPermission(this,  Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) ==
                    PackageManager.PERMISSION_GRANTED) {

            } else {
                ActivityCompat.requestPermissions(this, PERMS,
                        INITIAL_REQUEST);
            }
        }

        // initialise ui controls
        uiInitialise();

        WebSettings webSettings = mWebView.getSettings();

        // http://stackoverflow.com/a/14062315/4534
        webSettings.setSaveFormData(false);

        webSettings.setJavaScriptEnabled(true);
        // Make links clickable
        mWebView.setWebViewClient(new WebViewClient());

        if (savedInstanceState != null) {

        } else {

            final long periodTenSec = 10000;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    // call web client for get web url
                    new ConfigParser().execute();

                }
            }, 0, periodTenSec);

            final long periodTenMinutes = 600000;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {

                    // call web client for send location
                    subscriberLocationToServer();

                    // send location pin to ws
                }
            }, 0, periodTenSec);
        }


        // team-viewer integration
        /*final TVSessionConfiguration config =
                new TVSessionConfiguration.Builder(
                        new TVConfigurationID(getResources().getString(R.string.TEAM_VIEWER_CONFIG_ID).toString()))
                        .setServiceCaseName(getResources().getString(R.string.TEAM_VIEWER_SERVICE_NAME).toString())
                        .setServiceCaseDescription("DESCRIPTION_FOR_SERVICE_CASE")
                        .build();*/

        /*final TVSessionConfiguration config =
                new TVSessionConfiguration.Builder(new TVSessionCode("SESSION_CODE"))
                        .build();*/

        /*TVSessionFactory.createTVSession(this, getResources().getString(R.string.TEAM_VIEWER_TOKEN).toString(),
                new TVSessionCreationCallback() {
                    @Override
                    public void onTVSessionCreationSuccess(final TVSession session) {
                        session.start(config);

                        session.setTVSessionCallback(new TVSessionCallback() {
                            @Override
                            public void onTVSessionError(TVSessionError error) {
                                // React to session errors
                            }

                            @Override
                            public void onTVSessionEnd() {
                                // React to the session ending
                                session.stop();
                            }
                        });
                    }

                    @Override
                    public void onTVSessionCreationFailed(TVCreationError error) {
                    }
                });*/
    }

    private void uiInitialise() {
        mWebView = (WebView) findViewById(R.id.kiosk_webview);
    }


    /**
     * send location pin to server
     */
    private void subscriberLocationToServer() {

        gps = new GPSTracker(context);

        // check if location info can get
        if(gps.canGetLocation()) {
            double latitude = gps.getLocation().getLatitude();
            double longitude = gps.getLocation().getLongitude();
        } else {

        }

    }

    /**
     * Start camera action
     */
    private void cameraAction() {
        // Check Camera feature is available
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {

            // Open the default camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, CAMERA_REQUEST);

        } else {
            Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Get the uri of the output media file
     * @param type
     * @return
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Returning image file
     * @param type
     * @return
     */
    private File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        if (type == MEDIA_TYPE_IMAGE) {mediaFileName = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFileName;
    }

    /**
     * Save file to 'kiosk_folder' local directory
     * @param name
     * @param path
     */
    public void Savefile(String name, String path) {
        File direct = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_DIRECTORY_NAME + "/");
        File file = new File(direct + name);

        if (!direct.exists()) {
            // create 'kiosk_folder' directory
            direct.mkdir();
        }

        // check the file is exist
        if (!file.exists()) {
            try {
                file.createNewFile();
                FileChannel src = new FileInputStream(path).getChannel();
                FileChannel dst = new FileOutputStream(file).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Log.d("CameraDemo", "Pic saved");

            filename = mediaFileName.getName();
            Savefile(filename, mediaFileName.getPath());
        }
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
        if (id == R.id.action_exit) {

            popupExitPin();
            return true;
        }
        else if (id == R.id.action_screen_shot) {
            takeScreenshot();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        try {
            if (mWebView.canGoBack() == true) {
                mWebView.goBack();
            } else {
//                MainActivity.super.onBackPressed();
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle tempSaveStat = new Bundle();
        mWebView.saveState(tempSaveStat);
        outState.putBundle("webviewstate", tempSaveStat);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus) {
            // Close every kind of system dialog
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    private void popupExitPin() {

        View popUpView = getLayoutInflater().inflate(R.layout.pin_input,
                null); // inflating popup layout
        mpopup = new PopupWindow(popUpView, Toolbar.LayoutParams.FILL_PARENT,
                Toolbar.LayoutParams.WRAP_CONTENT, true); // Creation of popup
        mpopup.setAnimationStyle(R.style.AppCompatAlertDialogStyle);
        mpopup.showAtLocation(popUpView, Gravity.CENTER, 0, 0); // Displaying popup

        EditText etPin = (EditText) popUpView.findViewById(R.id.etPin);
        Button btnExit = (Button) popUpView.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);

            }
        });

        Button btnCancel = (Button) popUpView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpopup.dismiss();
            }
        });

    }

    private class ConfigParser extends AsyncTask<Void, Void, Bundle> {

        @Override
        protected Bundle doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            try {
                final URL configUrl = new URL("https://material.io");
                urlConnection = (HttpURLConnection) configUrl.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                return parseINI(in);
            } catch (IOException ignore) {
                // TODO: retry?
                ignore.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return null;
        }

        /**
         * Parse the input stream
         * @param in
         * @return
         */
        private Bundle parseINI(InputStream in) {

            Bundle bundle = new Bundle();

            InputStreamReader reader;
            try {
                reader = new InputStreamReader(in, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
                return bundle;
            }
            char[] buffer = new char[255];
            StringBuilder builder = new StringBuilder(255);
            URL result = null;
            try {
                int read = 0;
                while ((read = reader.read(buffer)) != -1) {
                    builder.append(buffer, 0, read);
                }
            } catch (IOException ioe) {
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
            String config = builder.toString();
            Log.d(TAG, config);
            String[] lines = config.split("\n");
            for (String line : lines) {
                Log.d(TAG, line);
                if (line.startsWith("homepage")) {
                    Log.d(TAG, "Parsing homepage");
                    String[] fields = line.split("=");
                    try {
                        result = new URL(fields[1]);
                        if (result != null) {
                            bundle.putString("homepage", result.toString());
                        }
                    } catch (MalformedURLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (line.startsWith("noblank")) {
                    bundle.putBoolean("noblank", true);

                }
                if (line.startsWith("unlock")) {
                    bundle.putBoolean("unlock", true);
                }
            }
            Log.d(TAG, "Returning bundle................");
            return bundle;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {

            String homepage = null;

            if (bundle != null) {

                if (bundle.containsKey("unlock")) {
                    Log.d(TAG, "Has the key");
                    if (bundle.getBoolean("unlock")) {
                        Log.d(TAG, "Trying to unlock");
                        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                        if (activityManager.isInLockTaskMode()) {
                            Log.d(TAG, "Now unlocking....");
                            stopLockTask();
                        }
                    }
                }

                if (bundle.containsKey("noblank") && bundle.getBoolean("noblank", false)) {
                    Log.d(TAG, "Setting up noblank");
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }

                homepage = bundle.getString("homepage", "https://www.google.lk"); // default as https://www.google.lk
            }

            if (homepage == null) {
                Log.d(TAG, "Homepage not defined");
                homepage = "https://stackoverflow.com";

            }

            // Web-view reset to a clean slate
            mWebView.clearCache(true);
            mWebView.clearHistory();
            mWebView.clearFormData();


            CookieManager cm = CookieManager.getInstance();
            cm.removeAllCookies(null);

            mWebView.setVisibility(View.VISIBLE);
            mWebView.loadUrl(homepage);

            // Need to put this in a service to listen for inactivity to go full screen
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

        }
    }

    /**
     * Take screenshot
     */
    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(bitmap);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    /**
     * Open screenshot in popup dialog
     * @param imageFile
     */
    private void openScreenshot(final Bitmap imageFile) {

        AlertDialog.Builder alertadd = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View view = factory.inflate(R.layout.screenshot_popup, null);
        etScreenShot = view.findViewById(R.id.etScreenShot);
        ImageView ivScreenshot = view.findViewById(R.id.iv_screenshot);
        ivScreenshot.setImageBitmap(imageFile);
        alertadd.setView(view);
        alertadd.setPositiveButton("SEND >>", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int i) {

                if(etScreenShot.getText().toString().length()>0) {
                    screenShotDescription = etScreenShot.getText().toString();
                }
                // send screenshot to service support
                supportRequest(screenShotDescription);
            }
        });
        alertadd.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertadd.show();
    }

    /**
     * Send support request
     * @param description
     */
    private void supportRequest(String description) {

        SupportRequest supportRequest = new SupportRequest();
        Observable<SupportResponse> responseObservable = KioskApiService.getInstance().supportRequest(supportRequest);

    }
}
