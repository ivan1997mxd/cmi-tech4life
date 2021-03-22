package com.estethapp.media.covid.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.estethapp.media.R;
import com.estethapp.media.covid.device.APIData;
import com.estethapp.media.covid.device.BluetoothDeviceInfo;
import com.estethapp.media.covid.device.Callback;
import com.estethapp.media.covid.device.DeviceValues;
import com.estethapp.media.covid.device.SaveData;
import com.estethapp.media.covid.device.SystemInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class DeviceStatisticsFragment extends Fragment implements View.OnClickListener, Callback<DeviceValues> {
    private HubFragment Parent;
    private TextView BPM;
    private TextView SpO2;
    private TextView Temperature;
    private TextView DeviceName;
    private TextView UserID;
    private LinearLayout UserInfo;
    private AppCompatImageButton tempBtn;
    private AppCompatImageButton spO2Btn;
    private AppCompatImageButton BTBtn;
    private AppCompatImageButton UploadBtn;
    private RelativeLayout loading;
    private ImageView genderIcon;
    private String ErrorTitle;
    private String ErrorMessage;
    private String currentUser;
    private String currentID;
    private String currentAge;
    private String currentGender;
    private DeviceListAdapter Adapter;
    private final boolean isConnected = false;
    private static final String TAG = DeviceStatisticsFragment.class.getSimpleName();
    private static final ArrayList<String> age_group = new ArrayList<String>(Arrays.asList("Children (00-14 years)", "Youth (15-24 years)", "Adults (25-64 years)", "Seniors (65 years and over)"));

    public DeviceStatisticsFragment(HubFragment parent) {
        Parent = parent;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        final View fragment = inflater.inflate(R.layout.device_statistics_fragment, container, false);

        fragment.findViewById(R.id.BackButton).setOnClickListener(this);
        BTBtn = fragment.findViewById(R.id.BluetoothConfigButton);
        BTBtn.setOnClickListener(this);
        UploadBtn = fragment.findViewById(R.id.UploadButton);
        UploadBtn.setOnClickListener(this);
        UploadBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(), "Long Clicked ",
                        Toast.LENGTH_SHORT).show();
                export(v);
                return true;
            }
        });
        loading = fragment.findViewById(R.id.loadingPanel);
        tempBtn = fragment.findViewById(R.id.TemperatureButton);
        tempBtn.setOnClickListener(this);
        spO2Btn = fragment.findViewById(R.id.SpO2Button);
        spO2Btn.setOnClickListener(this);
        UserInfo = fragment.findViewById(R.id.userInfo);
        UserInfo.setOnClickListener(this);
        genderIcon = fragment.findViewById(R.id.genderIcon);
        UserID = fragment.findViewById(R.id.UserID);
        DeviceName = fragment.findViewById(R.id.DeviceName);
        BPM = fragment.findViewById(R.id.BpmText);
        SpO2 = fragment.findViewById(R.id.SpO2Text);
        Temperature = fragment.findViewById(R.id.TempText);
        currentUser = SaveData.getCurrentUser(getContext());
        if (!currentUser.equals("")){
            UserID.setText(String.format("User: %s", currentUser.split("-")[0]));
            genderIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), getGender(currentUser.split("-")[2]), null));
        }
        SetStatus(false);
        String deviceInfo = SaveData.GetBTDeviceInfo(getContext());
        Log.i("Existing Device", deviceInfo);
        if (!deviceInfo.equals("No Connection")) {
            SystemInterface.EnableBluetooth(this);
            if (!SystemInterface.CheckBluetooth()) {
                if (SystemInterface.InitializeBluetooth(getContext(), Adapter, null, this, null)) {
                    SystemInterface.StartBluetoothScan();
                    loading.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Stop Scan
                            SystemInterface.StopBluetoothScan();
//                                Toast.makeText(getActivity(), "Existing device not found, please check it the device", Toast.LENGTH_SHORT).show();
//                            SaveData.SetDeviceInfo(getContext(), "No Connection");
                            loading.setVisibility(View.GONE);

                        }
                    }, 1000 * 5);

                }
            }
        } else {
            Toast.makeText(getActivity(), "No Existing device, please search BLE device", Toast.LENGTH_SHORT).show();
            DeviceName.setText(SaveData.GetBTDeviceInfo(getContext()));
        }

        return fragment;
    }

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            Log.i("WriteToFile", "Data wrote!");
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString).append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.BackButton:
                SystemInterface.StopBluetoothScan();
                SystemInterface.DisconnectBluetooth();
                Parent.finishTask();
                break;
            case R.id.BluetoothConfigButton:
                SaveData.SetDeviceInfo(getContext(), "No Connection");
                SystemInterface.DisconnectBluetooth();
                Parent.startTask(new DeviceListFragment(Parent, this, null, this, null));
                break;
            case R.id.UploadButton:
                final APIData data = new APIData();
                data.temperature = Temperature.getText().toString();
                data.saturation = SpO2.getText().toString();
                data.pulse = BPM.getText().toString();
                if (!data.checkData()) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Error Occurred")
                            .setMessage("The data could not be empty to send.")
                            .show();
                    break;
                }
                currentUser = SaveData.getCurrentUser(getContext());
                final String existString = readFromFile(getContext());
                currentID = currentUser.split("-")[0];
                currentAge = currentUser.split("-")[1];
                currentGender = currentUser.split("-")[2];
                // add error message when there is no data input,
                new AlertDialog.Builder(getContext())
                        .setTitle("Patient Information")
                        .setMessage(String.format("Are you sure you want to send data %s, %s, %s, as user %s to the server?", data.temperature, data.saturation, data.pulse, currentID))
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String currentTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
                                data.case_code = currentID;
                                data.y_age = currentAge;
                                data.gender = currentGender;
                                data.created_at = currentTime;
//                                data.gender = sex.getText().toString();
                                String dataString = existString + data.getStringData() + "\n";
                                writeToFile(dataString, getContext());
                                SystemInterface.SendApiData(getContext(), data, new Callback<Boolean>() {
                                    @Override
                                    public void run(Boolean argument) {
                                        if (!argument) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new AlertDialog.Builder(getContext())
                                                            .setTitle("Error Occurred")
                                                            .setMessage("The data could not be submitted to the server.")
                                                            .show();
                                                }
                                            });
                                        } else {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getActivity(), String.format("Response received: %s", SaveData.getResponse(getContext())), Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(getActivity(), readFromFile(getContext()), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();

                break;
            case R.id.TemperatureButton:
                SystemInterface.SendDeviceMessage("t");
                SetStatus(false);
                loading.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "getting the temperature", Toast.LENGTH_SHORT).show();
                break;
            case R.id.SpO2Button:
                SystemInterface.SendDeviceMessage("s");
                SetStatus(false);
                loading.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "getting the SpO2, please put you finger on the sensor", Toast.LENGTH_LONG).show();
                break;
            case R.id.userInfo:
                currentUser = SaveData.getCurrentUser(getContext());
                final View dialogLayout = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_layout, null);
                final Spinner age_list = dialogLayout.findViewById(R.id.age_list);
                final Spinner user_list = dialogLayout.findViewById(R.id.user_list);
                final TextView current_id = dialogLayout.findViewById(R.id.current_user);
                final TextView current_age = dialogLayout.findViewById(R.id.current_age);
                final TextView current_gender = dialogLayout.findViewById(R.id.current_gender);
                final RadioGroup gender = dialogLayout.findViewById(R.id.radioGrp);
                final ArrayList<String> current_users = SaveData.GetUserList(getContext());
                final ArrayList<String> users = new ArrayList<>();

                ArrayAdapter<String> AgeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, age_group) {
                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View v = convertView;
                        if (v == null) {
                            Context mContext = this.getContext();
                            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            v = vi.inflate(R.layout.row, null);
                        }

                        TextView user_ID = v.findViewById(R.id.userID);
                        user_ID.setTypeface(null, Typeface.BOLD);
                        TextView user_age = v.findViewById(R.id.userAge);
                        ImageView user_gender = v.findViewById(R.id.userGender);
                        String current_group = age_group.get(position);
                        String age = current_group.split("\\s+")[0];
                        String group = current_group.replace(age,"");
                        user_ID.setText(age);
                        user_age.setText(group);
                        user_gender.setImageDrawable(ResourcesCompat.getDrawable(getResources(), getAge(age), null));

                        return v;
                    }
                };
                age_list.setAdapter(AgeAdapter);

                users.add("New User");
                users.add("Existing Users");
                users.addAll(current_users);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, users) {
                    @Override
                    public boolean isEnabled(int position) {
                        return !users.get(position).equals("Existing Users");
                    }

                    @Override
                    public boolean areAllItemsEnabled() {
                        return false;
                    }

                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View v = convertView;
                        if (v == null) {
                            Context mContext = this.getContext();
                            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            v = vi.inflate(R.layout.row2, null);
                        }

                        TextView userID = v.findViewById(R.id.userID);
                        ImageView userGender = v.findViewById(R.id.userGender);
                        if (position < users.size()) {
                            String userInfo = users.get(position);
                            if (!userInfo.equals("New User") & !userInfo.equals("Existing Users")) {
                                String[] user_list = userInfo.split("-");
                                userID.setText(String.format("%s - %s", user_list[0], user_list[1]));
                                userGender.setImageDrawable(ResourcesCompat.getDrawable(getResources(), getGender(user_list[2]), null));
                            } else {
                                userID.setText(userInfo);
                                userGender.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_iconfinder_plus_circle_2561291, null));
                            }
                        }
                        return v;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View v = convertView;
                        if (v == null) {
                            Context mContext = this.getContext();
                            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            v = vi.inflate(R.layout.row, null);
                        }

                        TextView user_ID = v.findViewById(R.id.userID);
                        user_ID.setTypeface(null, Typeface.BOLD);
                        TextView user_age = v.findViewById(R.id.userAge);
                        ImageView user_gender = v.findViewById(R.id.userGender);
                        String[] user_Info = users.get(position).split("-");
                        user_ID.setText(user_Info[0]);
                        if (user_Info.length > 1) {
                            user_age.setText(user_Info[1]);
                            user_gender.setImageDrawable(ResourcesCompat.getDrawable(getResources(), getGender(user_Info[2]), null));
                            user_age.setVisibility(View.VISIBLE);
                            user_gender.setVisibility(View.VISIBLE);
                        } else {
                            user_age.setText(R.string.add_message);
                            user_gender.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_iconfinder_plus_circle_2561291, null));
                            if (user_Info[0].equals("Existing Users")) {
                                user_age.setVisibility(View.GONE);
                                user_gender.setVisibility(View.GONE);
                            }
                        }

                        return v;
                    }
                };

                user_list.setAdapter(adapter);
                int currentIndex = users.indexOf(currentUser);
                Log.i("Current Index", String.valueOf(currentIndex));
                if (currentIndex == -1) {
                    user_list.setSelection(0);
                } else {
                    user_list.setSelection(currentIndex);
                }


                user_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String item = parent.getItemAtPosition(position).toString();
                        if (item.equals("New User")) {
                            age_list.setVisibility(View.VISIBLE);
                            gender.setVisibility(View.VISIBLE);
                            current_id.setText(String.format("Current ID: %s", (1000 + users.size() - 2)));
                            currentID = String.valueOf(1000 + users.size() - 2);
                        } else {
                            String[] user_data = item.split("-");
                            current_id.setText(String.format("Current ID: %s", user_data[0]));
                            current_age.setText(String.format("Current age: %s", user_data[1]));
                            current_gender.setText(String.format("Current gender: %s", user_data[2]));
                            currentID = user_data[0];
                            currentAge = user_data[1];
                            currentGender = user_data[2];
                            age_list.setVisibility(View.GONE);
                            gender.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                age_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String age = parent.getItemAtPosition(position).toString().split("\\s+")[0];
                        current_age.setText(String.format("Current age: %s", age));
                        currentAge = age;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton checkedRadioButton = group.findViewById(checkedId);
                        boolean isChecked = checkedRadioButton.isChecked();
                        if (isChecked) {
                            String gender = (String) checkedRadioButton.getText();
                            current_gender.setText(String.format("Current gender: %s", gender));
                            currentGender = gender;
                        }

                    }
                });
                new AlertDialog.Builder(getContext())
                        .setTitle("Patient Information")
                        .setView(dialogLayout)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String currentString = String.format("%s-%s-%s", currentID, currentAge, currentGender);
                                if (!current_users.contains(currentString)) {
                                    current_users.add(currentString);
                                    SaveData.SetUserList(getContext(), current_users);
                                }
                                Log.i("New user added %s", currentString);
                                SaveData.SetCurrentUser(getContext(), currentString);
//                                data.gender = sex.getText().toString();
                                UserID.setText(String.format("User: %s", currentID));
                                genderIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), getGender(currentGender), null));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();

                break;
        }
    }

    private static String convertStringArrayToString(String[] strArr, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (String str : strArr)
            sb.append(str).append(delimiter);
        return sb.substring(0, sb.length() - 1);
    }

    public int getGender(String gender) {
        int drawable;
        switch (gender) {
            case "Female":
                drawable = R.drawable.ic_iconfinder_user_female2_172622;
                break;
            case "Male":
                drawable = R.drawable.ic_iconfinder_user_male2_172626;
                break;
            default:
                drawable = R.drawable.ic_iconfinder_finance_profile_person_4229498;
                break;
        }
        return drawable;
    }
    public int getAge(String age) {
        int drawable;
        switch (age) {
            case "Children":
                drawable = R.drawable.ic_twins;
                break;
            case "Youth":
                drawable = R.drawable.ic_children;
                break;
            case "Adults":
                drawable = R.drawable.ic_parents;
                break;
            case "Seniors":
                drawable = R.drawable.ic_grandparents;
                break;
            default:
                drawable = R.drawable.ic_parents;
                break;
        }
        return drawable;
    }

    public void connectDevice(final String device) {
        if (!SystemInterface.ConnectBluetooth(device)) {
            Log.e("BLE", "Failed to connect to " + device);
        }
        Log.i(TAG, "Connect to " + device);
    }

    public void export(View view) {
        String data = "Time, Patient ID, Age group, Temperature, SpO2, Heart Rate\n" + readFromFile(getContext());
        try {
            //saving the file into device
            FileOutputStream out = getContext().openFileOutput("data.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();

            //exporting
            File filelocation = new File(getContext().getFilesDir(), "data.csv");
            Uri path = FileProvider.getUriForFile(getContext(), "com.estethapp.covid.fileprovider", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            Intent chooser = Intent.createChooser(fileIntent, "Share File");
            List<ResolveInfo> resInfoList = getContext().getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
//            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            fileIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                getContext().grantUriPermission(packageName, path, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            startActivity(chooser);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void run(final DeviceValues argument) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final String deviceInfo = argument.getDeviceName();

                if (!deviceInfo.equals("")) {
                    BluetoothDeviceInfo info = SystemInterface.GetDeviceInfo(deviceInfo);
                    final String deviceName = info.getName();
                    loading.setVisibility(View.GONE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("Existing Device Found");
                    builder.setMessage(String.format("Device: %s, Do you want to connect?", deviceName));

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
//                            SystemInterface.StopBluetoothScan();
                            connectDevice(deviceInfo);
                            DeviceName.setText(deviceName);
                            SetStatus(true);
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Connecting to device, Please wait until this message disappear", Toast.LENGTH_LONG).show();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                            SaveData.SetDeviceInfo(getContext(), "No Connection");
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Log.i(TAG, "We getting the arguments, type=" + argument.getPayloadType());
                    switch (argument.getPayloadType()) {
                        case DeviceValues.PayloadTypes.Error:
                            SetStatus(true);
                            loading.setVisibility(View.GONE);
                            switch (argument.getSubCode()) {
                                case DeviceValues.SubCodes.ErrorInitializingBluetooth:
                                    ErrorTitle = "Bluetooth Error";
                                    ErrorMessage = "Failed to initialize the Bluetooth";
                                    break;
                                case DeviceValues.SubCodes.ErrorConfiguringSensor:
                                    ErrorTitle = "Configuration Error";
                                    ErrorMessage = "Failed to configure the Sensor";
                                    break;
                                case DeviceValues.SubCodes.MAX32664CommError:
                                    ErrorTitle = "Command Error";
                                    ErrorMessage = "Failed to send the command";
                                    break;
                            }
                            new AlertDialog.Builder(getContext())
                                    .setTitle(ErrorTitle)
                                    .setMessage(ErrorMessage)
                                    .show();
                            break;
                        case DeviceValues.PayloadTypes.SamplingStatus:
                            switch (argument.getSubCode()) {
                                case DeviceValues.SubCodes.SamplingError:
                                    SetStatus(true);
                                    loading.setVisibility(View.GONE);
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Sampling Error")
                                            .setMessage("Error! Please check the board and sensor")
                                            .show();
                                    break;
                                case DeviceValues.SubCodes.SamplingOnCourse:
                                    Log.i(TAG, "Still in progress");
                                    break;
                                case DeviceValues.SubCodes.SuccessfulSampling:
                                    Toast.makeText(getActivity(), "Data Received", Toast.LENGTH_LONG).show();
                                    SetStatus(true);
                                    loading.setVisibility(View.GONE);
                                    break;
                            }
                            break;
                        case DeviceValues.PayloadTypes.SensorData:
                            switch (argument.getSubCode()) {
                                case DeviceValues.SubCodes.Temperature_BatteryLevel:
                                    String temp = FormatValue(argument.getTemperature());
                                    if (!temp.equals("0.0")) {
                                        if (temp.equals("254.0")) {
                                            Temperature.setText("LOW");
                                        } else if (temp.equals("255.0")) {
                                            Temperature.setText("HIGH");
                                        } else {
                                            Temperature.setText(String.format("%s C", temp));
                                        }

                                    }
                                    break;
                                case DeviceValues.SubCodes.HR_Confidence_SPO2_StatusBase_StatusEx_R_BatteryLevel:
                                    SpO2.setText(String.format("%s %%", FormatValue(argument.getSpO2())));
                                    BPM.setText(String.format("%s BPM", FormatValue(argument.getBPM())));
                                    break;
                            }
                            break;
                    }
                }
            }
        });
    }

    private void SetStatus(boolean status) {
        int bgColor = R.drawable.transparent_bg_bordered;
        if (!status) {
            bgColor = R.drawable.disable_button;
        }
        spO2Btn.setBackgroundResource(bgColor);
        tempBtn.setBackgroundResource(bgColor);
        UploadBtn.setBackgroundResource(bgColor);
        tempBtn.setEnabled(status);
        spO2Btn.setEnabled(status);
        UploadBtn.setEnabled(status);
    }

    private static String FormatValue(float value) {
        return Float.isNaN(value) ? "E" : Float.toString(value);
    }
}
