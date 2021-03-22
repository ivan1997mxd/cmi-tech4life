package com.estethapp.media.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.estethapp.media.util.App;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.estethapp.media.MainActivity;
import com.estethapp.media.R;
import com.estethapp.media.listener.ActivityResultListener;
import com.estethapp.media.util.Helper;
import com.estethapp.media.util.Prefs;
import com.estethapp.media.util.UserModel;
import com.estethapp.media.util.Util;
import com.estethapp.media.view.ProgressDialogView;
import com.estethapp.media.view.TakePictureOptionsDailog;
import com.estethapp.media.view.ToastStyle;
import com.estethapp.media.web.HttpCaller;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;

public class ProfileFragment extends Fragment {
    ProfileFragment fragment;
    Context context;
    static ProfileFragment instance;
    public UserModel currentUser;
    View root;
    private SharedPreferences prefsInstance;
    private Prefs prefs;
    private RadioGroup radioSexGroup;
    private RadioButton radioButtonMale;
    private RadioButton radioButtonFeMale;
    EditText textViewName, textViewEmail, textViewPassword, textViewConfirmPassword ;
    private ImageView profileImageView;
    UserModel profileModel;

    App app;

    private static ProgressDialogView dialogInstance = ProgressDialogView.getInstance();
    private static ProgressDialog progressDialog;



    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(Bundle bundle) {
        ProfileFragment instance = new ProfileFragment();
        return instance;
    }

    public static ProfileFragment getInstance() {
        return instance;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        instance = this;
        return inflater.inflate(R.layout.activity_profile_screen, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        root = view;
        fragment = this;
        Fabric.with(context, new Crashlytics());

        profileImageView = (ImageView) root.findViewById(R.id.profileImageView);
        textViewName = (EditText) root.findViewById(R.id.textViewName);
        textViewEmail = (EditText) root.findViewById(R.id.textViewEmail);
        textViewPassword = (EditText) root.findViewById(R.id.textViewPassword);
        textViewConfirmPassword = (EditText) root.findViewById(R.id.textViewConfirmPassword);
        radioSexGroup = (RadioGroup) root.findViewById(R.id.radioGrp);
        radioButtonMale = (RadioButton) root.findViewById(R.id.radioM);
        radioButtonFeMale = (RadioButton) root.findViewById(R.id.radioF);

        prefs = Prefs.getInstance();
        prefsInstance = prefs.init(context);

        Gson gson = new Gson();
        String json = prefsInstance.getString(prefs.USERMODEL, "");
        profileModel = gson.fromJson(json, UserModel.class);

        textViewName.setText("" + profileModel.PatientID);
        textViewEmail.setText("" + profileModel.Email);
        textViewPassword.setText("" + profileModel.Password);
        textViewConfirmPassword.setText("" + profileModel.Password);

        /*Glide.with(getActivity())
                .load(profileModel.ProfilePhoto)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(profileImageView);*/
        File profileDirect = new File(Util.getProfileDirectory());
        File mypath=new File(profileDirect,"profile.jpg");
        if(mypath!=null){
            if(mypath.exists()){
                Picasso.with(context).load(mypath).placeholder(R.drawable.loading_icon).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .error(R.mipmap.ic_icon).into(profileImageView);
            }else{
                if(!profileModel.ProfilePhoto.equals("")){
                    Picasso.with(context).
                            load(profileModel.ProfilePhoto).
                            placeholder(R.drawable.loading_icon)
                            .memoryPolicy(MemoryPolicy.NO_CACHE )
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(profileImageView);
                }else{
                    Picasso.with(context).load(R.mipmap.ic_icon).placeholder(R.drawable.loading_icon).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .into(profileImageView);
                }
            }
        }else{
            if(!profileModel.ProfilePhoto.equals("")){
                Picasso.with(context).load(profileModel.ProfilePhoto).placeholder(R.drawable.loading_icon).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .into(profileImageView);
            }else{
                Picasso.with(context).load(R.mipmap.ic_icon).placeholder(R.drawable.loading_icon).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .into(profileImageView);
            }

        }


        String gender = profileModel.Gender;
        if(!gender.equals("")){
            if (gender.equals("Male")) {
                radioButtonMale.setChecked(true);
                radioButtonFeMale.setChecked(false);
            } else {
                radioButtonMale.setChecked(false);
                radioButtonFeMale.setChecked(true);
            }
        }

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TakePictureOptionsDailog
                        .init(root, new TakePictureOptionsDailog.TakePictureOptionsDailogListener() {
                            @Override
                            public void camera() {
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    // Create the File where the photo should go
                                    try {
                                        final File photoFile = Util.createImageFile(getActivity());
                                        // Continue only if the File was successfully created
                                        if (photoFile != null) {




//                                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                                            ((MainActivity) getActivity())
                                                    .takePictureFromCamera(
                                                            takePictureIntent,
                                                            new ActivityResultListener() {
                                                                @Override
                                                                public void OnActivityResult(Bitmap imageBitmap) {
                                                                    progressDialogShow(getString(R.string.uploading_profile_image));
                                                                    if (imageBitmap != null) {
                                                                        saveProfileToInternalStorage(imageBitmap);
                                                                        progressDialog.dismiss();
                                                                        Log.d("CAMERA", "Picture Taked:" + imageBitmap);
                                                                        profileImageView.setImageBitmap(imageBitmap);
                                                                       // ((ImageView) root).setImageBitmap(imageBitmap);

                                                                        String fileName = profileModel.Name + "-" + profileModel.PatientID + "-P.jpg";
                                                                        uploadImage(
                                                                                fileName,
                                                                                "PROFILE_IMAGE",
                                                                                getImageViewIndex(root),
                                                                                imageBitmap,
                                                                                new Response.Listener() {
                                                                                    @Override
                                                                                    public void onResponse(Object o) {
                                                                                        try {
                                                                                            progressDialog.dismiss();
                                                                                            if (o instanceof JSONObject) {
                                                                                                if (((JSONObject) o).getBoolean("Status")) {
                                                                                                    profileModel.ProfilePhoto = ((JSONObject) o).getString("Response");

                                                                                                    JSONObject jsonbody = new JSONObject();
                                                                                                    jsonbody.put("UserID", profileModel.UserID);
                                                                                                    jsonbody.put("Name", profileModel.Name);
                                                                                                    jsonbody.put("PatientID", profileModel.PatientID);
                                                                                                    jsonbody.put("Email", profileModel.Email);
                                                                                                    jsonbody.put("Password", profileModel.Password);
                                                                                                    jsonbody.put("Gender", profileModel.Gender);
                                                                                                    jsonbody.put("ProfilePhoto", profileModel.ProfilePhoto);

                                                                                                    prefsInstance.edit().putString(prefs.USERMODEL, jsonbody.toString()).commit();

                                                                                                } else {
                                                                                                    // Show Error Message
                                                                                                }
                                                                                            } else if (o instanceof VolleyError) {
                                                                                                // Show Error Message
                                                                                            }

                                                                                        } catch (JSONException e) {
                                                                                            progressDialog.dismiss();
                                                                                            e.printStackTrace();
                                                                                        }
                                                                                    }
                                                                                });

                                                                    } else {
                                                                        progressDialog.dismiss();
                                                                        Log.e("CAMERA", "Failed to capture picture..");
                                                                    }
                                                                }
                                                            });
                                        }
                                    } catch (Exception e) {
                                        progressDialog.dismiss();
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void gallery() {
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                // Start the Intent
                                ((MainActivity) getActivity())
                                        .takePictureFromGallery(
                                                galleryIntent,
                                                new ActivityResultListener() {
                                                    @Override
                                                    public void OnActivityResult(Bitmap imageBitmap) {
                                                        if (imageBitmap != null) {
                                                            saveProfileToInternalStorage(imageBitmap);
                                                            progressDialogShow(getString(R.string.uploading_profile_image));
                                                            Log.d("CAMERA", "Picture catched:" + imageBitmap);
                                                           // ((ImageView) root).setImageBitmap(imageBitmap);
                                                            profileImageView.setImageBitmap(imageBitmap);
                                                            String fileName = profileModel.Name + "-" + profileModel.PatientID + "-P.jpg";
                                                            uploadImage(
                                                                    fileName,
                                                                    "PROFILE_IMAGE",
                                                                    getImageViewIndex(root),
                                                                    imageBitmap,
                                                                    new Response.Listener() {
                                                                        @Override
                                                                        public void onResponse(Object o) {
                                                                            try {
                                                                                progressDialog.dismiss();
                                                                                if (o instanceof JSONObject) {
                                                                                    if (((JSONObject) o).getBoolean("Status")) {
                                                                                        // Show Success Message
                                                                                        profileModel.ProfilePhoto = ((JSONObject) o).getString("Response");

                                                                                        JSONObject jsonbody = new JSONObject();
                                                                                        jsonbody.put("UserID", profileModel.UserID);
                                                                                        jsonbody.put("Name", profileModel.Name);
                                                                                        jsonbody.put("PatientID", profileModel.PatientID);
                                                                                        jsonbody.put("Email", profileModel.Email);
                                                                                        jsonbody.put("Password", profileModel.Password);
                                                                                        jsonbody.put("Gender", profileModel.Gender);
                                                                                        jsonbody.put("ProfilePhoto", profileModel.ProfilePhoto);

                                                                                        prefsInstance.edit().putString(prefs.USERMODEL, jsonbody.toString()).commit();
                                                                                        progressDialog.dismiss();
                                                                                    } else {
                                                                                        progressDialog.dismiss();
                                                                                        // Show Error Message
                                                                                    }
                                                                                } else if (o instanceof VolleyError) {
                                                                                    // Show Error Message
                                                                                    progressDialog.dismiss();
                                                                                }

                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                                progressDialog.dismiss();
                                                                            }
                                                                        }
                                                                    });

                                                        } else {
                                                            Log.e("CAMERA", "Failed to catch picture..");
                                                        }
                                                    }
                                                });
                            }
                        })
                        .show(getFragmentManager(), null);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
//        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (!Util.isInternetAvailable(context)) {
            Toast.makeText(context, "Internet connection has some problem", Toast.LENGTH_SHORT).show();
        } else {
            switch (item.getItemId()) {
                case R.id.saveProfile:
                    updateProfile();
                    break;
            }
        }

        return true;
    }

    public void updateProfile() {

        String gender = "";

        try{
            int selectedId = radioSexGroup.getCheckedRadioButtonId();
            RadioButton radioSexButton = (RadioButton) root.findViewById(selectedId);
            gender = radioSexButton.getText().toString();
        }catch(NullPointerException e){

        }

        String userName = textViewName.getText().toString();
        String userEmail = textViewEmail.getText().toString();
        String userPass = textViewPassword.getText().toString();
        String userConfirmPass = textViewConfirmPassword.getText().toString();
        profileModel.Gender = gender;
        profileModel.Name = userName;
        profileModel.Email = userEmail;
        profileModel.Password = userPass;

        JSONObject jsonbody = new JSONObject();

        if(!isValid(userPass)){
            setErrorOnEditText(textViewPassword, getString(R.string.please_put_eight_char));
        }else if(!userPass.equals(userConfirmPass)){
            Toast.makeText(getActivity(), R.string.pass_not_matched, Toast.LENGTH_LONG).show();
        }else if(gender.equals("")){
            Toast.makeText(getActivity(), R.string.please_select_gender, Toast.LENGTH_LONG).show();
        }else{
            try {
                jsonbody.put("UserID", profileModel.UserID);
                jsonbody.put("Name", profileModel.Name);
                jsonbody.put("PatientID", profileModel.PatientID);
                jsonbody.put("Email", profileModel.Email);
                jsonbody.put("Password", profileModel.Password);
                jsonbody.put("Gender", profileModel.Gender);
                jsonbody.put("ProfilePhoto", profileModel.ProfilePhoto);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            prefsInstance.edit().putString(prefs.USERMODEL, jsonbody.toString()).commit();
            requestServerLogin(jsonbody);

        }


    }

    void requestServerLogin(JSONObject jsonbody) {
        progressDialogShow(getString(R.string.txt_updating_profile));
        HttpCaller
                .getInstance()
                .updateProfileCall(context, jsonbody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();
                                try {
                                    if (response.getBoolean("Status")) {
                                        String res = response.getString("Message").toString();
                                        Helper.showToast("" + res, ToastStyle.SUCCESS);

                                    } else {
                                        String res = response.getString("Message").toString();
                                        Helper.showToast("" + res, ToastStyle.ERROR);
                                    }
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    e.printStackTrace();
                                }

                            }

                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                            }
                        }, true);

    }

    int getImageViewIndex(View v) {
        if (v.getId() == R.id.profileImageView) return 0;

        return -1;
    }

    void uploadImage(String fileName, String imageType, int index, Bitmap bitmap, final Response.Listener listener) {
        // Resize
        int perc = 400 * 100 / bitmap.getHeight();
        int h = 400;
        int w = bitmap.getWidth() * perc / 100;
        bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);

        // To Byte Array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteArray = stream.toByteArray();

        // Create JSON Body for Request
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UserID", profileModel.UserID);
            jsonObject.put("Index", index);
            jsonObject.put("FileName", index + "-" + fileName);
            jsonObject.put("ImageType", imageType);
            jsonObject.put("ImageData", Base64.encodeToString(byteArray, Base64.DEFAULT).replaceAll("\n", "").replaceAll("\r", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpCaller
                .getInstance()
                .uploadImage(
                        jsonObject,
                        listener,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                listener.onResponse(volleyError);
                            }
                        }
                );
    }
    public void progressDialogShow(String txt){
        progressDialog = dialogInstance.setProgressDialog(context,txt );
        progressDialog.show();
    }


    private String saveProfileToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(context);
        File directory = new File(Util.getProfileDirectory());
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public static boolean isValid(String password) {
        Boolean atleastOneUpper = false;
        Boolean atleastOneLower = false;
        Boolean atleastOneDigit = false;

        if (password.length() < 8) { // If its less then 8 characters, its automatically not valid
            return false;
        }

        for (int i = 0; i < password.length(); i++) { // Lets iterate over only once. Saving time
            if (Character.isUpperCase(password.charAt(i))) {
                atleastOneUpper = true;
            }
            else if (Character.isLowerCase(password.charAt(i))) {
                atleastOneLower = true;
            }
            else if (Character.isDigit(password.charAt(i))) {
                atleastOneDigit = true;
            }
        }

        return (atleastOneUpper && atleastOneLower && atleastOneDigit); // Return true IFF the password is atleast eight characters long, has atleast one upper, lower and digit
    }

    public void setErrorOnEditText(final EditText mEditText, String error) {
        mEditText.setError(error);
        mEditText.requestFocus();
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEditText.setError(null);
                mEditText.clearFocus();
                return false;
            }
        });
    }

}
