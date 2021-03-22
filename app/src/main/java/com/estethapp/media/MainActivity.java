package com.estethapp.media;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;

import com.estethapp.media.covid.BlueToothActivity;
import com.estethapp.media.covid.device.SaveData;
import com.estethapp.media.covid.ui.HubFragment;
import com.estethapp.media.covid.ui.MainFragment;
import com.estethapp.media.covid.ui.OnboardingFragment;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.estethapp.media.fragment.AbouteSteth;
import com.estethapp.media.fragment.ContactFragment;
import com.estethapp.media.fragment.ExaminingFragment;
import com.estethapp.media.fragment.MyRecordingFragment;
import com.estethapp.media.fragment.OldRecordingFragment;
import com.estethapp.media.fragment.PairingFragment;
import com.estethapp.media.fragment.ProfileFragment;
import com.estethapp.media.helper.ExaminatingHelper;
import com.estethapp.media.helper.ExminatingHelper;
import com.estethapp.media.listener.ActivityResultListener;
import com.estethapp.media.mSparrow.mSparrowMainActivity;
import com.estethapp.media.mSparrow.mSparrowMainActivityCovid;
import com.estethapp.media.util.App;
import com.estethapp.media.util.Constants;
import com.estethapp.media.util.Helper;
import com.estethapp.media.util.Prefs;
import com.estethapp.media.util.Util;
import com.estethapp.media.web.HttpCaller;
import com.franmontiel.localechanger.utils.ActivityRecreationHelper;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import io.fabric.sdk.android.Fabric;

import static com.estethapp.media.util.Constants.PIC_CROP;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    View container;
    androidx.appcompat.app.ActionBar actionBar;
    TextView mTitle;
    MenuItem notificationMenu, friendRequestMenu;
    public MenuItem appActionBarBadge;
    private SharedPreferences prefsInstance;
    private Prefs prefs;
    private static MainActivity instance;
    boolean doubleBackToExitPressedOnce = false;
    Uri mCapturedImageURI;
    public static int mREQUEST_CODE_CROP_IMAGE = 103;
    PairingFragment.MusicIntentReceiver myReceiver;
    // ExaminingFragment.MusicIntentReceiver myreceiver;
    ExminatingHelper exminatingHelper = ExminatingHelper.getInstance();
    ExaminatingHelper examinatingHelper = ExaminatingHelper.getInstance();
    private NavigationView navigationView;

    public static MainActivity getInstance() {
        return instance;
    }

    static ActivityResultListener activityResultListener;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, ExaminingFragment.newInstance(), null)
                    .commit();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getApplicationContext(), new Crashlytics());
        setContentView(R.layout.activity_activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        toolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.ic_launcher));
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
            }
        }

        if (Helper.checkPermissionsArray(this)) {

            Log.d("Allowed All Permissions", "true");
        } else {
            Helper.requestPermissions(this, 1);
        }

        prefs = Prefs.getInstance();
        prefsInstance = prefs.init(MainActivity.this);

        mTitle.setText(getString(R.string.app_name));
        App.context = instance = this;


        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (refreshedToken != null) {
            JSONObject jsonbody = new JSONObject();
            try {
                jsonbody.put("UserID", App.profileModel.UserID);
                jsonbody.put("GCMToken", refreshedToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (refreshedToken != null)
                HttpCaller
                        .getInstance()
                        .updateGCMToken(
                                jsonbody,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject o) {
                                        try {
                                            if (!o.getBoolean("Status")) {
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
                                }, true);
        }
        PairingFragment.fragmentManager = getSupportFragmentManager();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, PairingFragment.newInstance(false), null).commit();
        ExaminingFragment.fragmentManager = getSupportFragmentManager();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;

        //OnBack press from other fragment navigate to Home fragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);

        if (!(currentFragment instanceof PairingFragment)) {
            mTitle.setText(getString(R.string.fragment_pairing));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, PairingFragment.newInstance(false), null)
                    .commit();
        }
        //Code end

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            mTitle.setText(getString(R.string.fragment_pairing));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, PairingFragment.newInstance(false), null)
                    .commit();


        } else if (id == R.id.nav_user_profile) {
            mTitle.setText(getString(R.string.fragment_profile));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, ProfileFragment.newInstance(null), null)
                    .commit();
        } else if (id == R.id.my_recording) {
            mTitle.setText(getString(R.string.fragment_myRecording));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, MyRecordingFragment.newInstance(null), null)
                    .disallowAddToBackStack()
                    .commit();
        } else if (id == R.id.my_old_recording) {
            mTitle.setText("Old Recordings");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, OldRecordingFragment.newInstance(null), null)
                    .disallowAddToBackStack()
                    .commit();
        } else if (id == R.id.nav_examining) {
            mTitle.setText(getString(R.string.fragment_auscultation));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, ExaminingFragment.newInstance(), null)
                    .commit();
        } else if (id == R.id.nav_conversation) {

            Intent intent = new Intent(MainActivity.this, ChatActivity.class);//put it for displaying the title.
            startActivity(intent);


        } else if (id == R.id.nav_contact) {

            mTitle.setText(getString(R.string.fragment_myContacts));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, ContactFragment.newInstance(null), null)
                    .commit();

        } else if (id == R.id.nav_logout) {
            prefsInstance.edit().clear().commit();
            removeProfileImage();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FirebaseInstanceId.getInstance().deleteInstanceId();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            /*String refreshedToken = "";
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
*/
            //SQLiteUtils.execSql("truncate ChatGroup");
            //SQLiteUtils.execSql("truncate Comments");
            //SQLiteUtils.execSql("truncate JoinGroupContact");
            // deleteDatabase("old_db.db");

            // prefsInstance.edit().remove("estethPrefs").commit();
            Intent intent = new Intent(MainActivity.this, ActivityLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_about) {
            mTitle.setText(getString(R.string.fragment_about));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, AbouteSteth.newInstance(null), null)
                    .commit();
        } else if (id == R.id.nav_about) {
            mTitle.setText(getString(R.string.fragment_about));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, AbouteSteth.newInstance(null), null)
                    .commit();
//        }else if (id == R.id.nav_covid){
//            Intent myIntent = new Intent(MainActivity.this, mSparrowMainActivityCovid.class);
//            MainActivity.this.startActivity(myIntent);
//        }
        }else if (id == R.id.nav_covid){
//            Intent myIntent = new Intent(MainActivity.this, BlueToothActivity.class);
//            MainActivity.this.startActivity(myIntent);
            final MainFragment fragment = new MainFragment();

            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commitNow();
            fragment.setChild(new HubFragment(fragment));
        }

        else if (id == R.id.nav_newpneu)
        {
            Intent myIntent = new Intent(MainActivity.this, mSparrowMainActivity.class);
            MainActivity.this.startActivity(myIntent);

            /*
            mTitle.setText("NewPneu");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, AbouteSteth.newInstance(null), null)
                    .commit();
                    */


        }
//        else if (id == R.id.nav_change_language) {
//            mTitle.setText(getString(R.string.change_language));
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.container, ChangeLanguageFragment.newInstance(null), null)
//                    .commit();
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void removeProfileImage() {
        File profileDirect = new File(Util.getProfileDirectory());
        File mypath = new File(profileDirect, "profile.jpg");
        if (mypath != null) {
            if (mypath.exists()) {
                try {
                    Picasso.with(MainActivity.this).invalidate(mypath);
                    boolean yesno = mypath.delete();
                    System.out.println(yesno);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isBluetoothEnabled() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            return mBluetoothAdapter.isEnabled();
        } else {
            return false;
        }

    }

    private void bluetToothIntent() {
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivityForResult(intentOpenBluetoothSettings, 100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor =
                    managedQuery(mCapturedImageURI, projection, null,
                            null, null);
            int column_index_data = cursor.getColumnIndexOrThrow(
                    MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String picturePath = cursor.getString(column_index_data);
            String imagePath = picturePath;

            if (mCapturedImageURI != null) {
                performCrop(mCapturedImageURI);
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.error_cropping), Toast.LENGTH_SHORT).show();
            }

/*
            System.out.println("CAMERA PATHHHHHHHHHHHHHHHHH" + picturePath);
            yourSelectedImage = BitmapFactory.decodeFile(picturePath);
            circularImageView.setImageBitmap(yourSelectedImage);
            prefManager = new PrefManager(SignUpActivity.this);
            prefManager.saveImagepath(imagePath);
*/

//            cameraActivityResult(resultCode, data);

        } else if (requestCode == Constants.GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            galleryActivityResult(resultCode, data);
        }

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            try {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
            } catch (Exception e) {
            }
        }

        if (requestCode == PIC_CROP && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap thePic = extras.getParcelable("data");

            if (resultCode == Activity.RESULT_OK)
                if (activityResultListener != null) {
                    activityResultListener.OnActivityResult(thePic);
                }

        }

/*
        if(activityResultListener != null) {
            activityResultListener.OnActivityResult(mphoto);
        }
*/

        if (requestCode == 70536) {

            if (!isFinishing()) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                PairingFragment dummyFragment = PairingFragment.newInstance(true);
                ft.add(R.id.container, dummyFragment);
                ft.commitAllowingStateLoss();
            }


        /*    new Handler().post(new Runnable() {
                public void run() {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, PairingFragment.newInstance(null), null)
                            .commit();
                }
            });*/


        }
    }

    @Override
    public void onResume() {
        ActivityRecreationHelper.onResume(this);
        registerReceiver(receiver, new IntentFilter("fragmentChange"));

        if (PairingFragment.getInstance().bluetooThSearchingInstance != null) {
            PairingFragment.BluetoothSearch bluetoothSearch = PairingFragment.getInstance().bluetooThSearchingInstance;
            bluetoothSearch.registerReceiver(MainActivity.this);
        }
        int getRecrodingDevice = exminatingHelper.getRecordingDevice();
        if (getRecrodingDevice != 2) {
            myReceiver = new PairingFragment.MusicIntentReceiver();
            String action;

            if (Build.VERSION.SDK_INT >= 21) {
                action = AudioManager.ACTION_HEADSET_PLUG;
            } else {
                action = Intent.ACTION_HEADSET_PLUG;
            }
            try {
                IntentFilter filter = new IntentFilter(action);
                registerReceiver(myReceiver, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onResume();
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void takePictureFromCamera(Intent takePictureIntent, ActivityResultListener activityResultListener) {
        this.activityResultListener = activityResultListener;

        String fileName = "temp.jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        mCapturedImageURI = getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values);
        takePictureIntent
                .putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

        startActivityForResult(takePictureIntent, Constants.CAMERA_REQUEST_CODE);
    }

    public void takePictureFromGallery(Intent galleryIntent, ActivityResultListener activityResultListener) {
        this.activityResultListener = activityResultListener;
        startActivityForResult(galleryIntent, Constants.GALLERY_REQUEST_CODE);
    }

    void cameraActivityResult(int resultCode, Intent data) {
        Bitmap mphoto = (Bitmap) data.getExtras().get("data");
        if (resultCode == Activity.RESULT_OK) {
            performCrop(mCapturedImageURI);
        }

    }

    void galleryActivityResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            try {
                Uri uri = data.getData();

                performCrop(uri);

            } catch (Exception e) {

            }

        }
    }

    private void performCrop(Uri picUri) {

        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri
        cropIntent.setDataAndType(picUri, "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        //start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, PIC_CROP);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {

        try {
            unregisterReceiver(receiver);
            if (PairingFragment.getInstance().bluetooThSearchingInstance != null) {
                PairingFragment.BluetoothSearch sf = PairingFragment.getInstance().bluetooThSearchingInstance;
                // sf.unRegisterReceiver(MainActivity.this);
            }

            if (myReceiver != null) {
                //unregisterReceiver(myReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        ActivityRecreationHelper.onDestroy(this);
        super.onDestroy();
    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        newBase = LocaleChanger.configureBaseContext(newBase);
//        super.attachBaseContext(newBase);
//    }

}