package com.estethapp.media.fragment;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.activeandroid.util.Log;
import com.crashlytics.android.Crashlytics;
import com.estethapp.media.ActivityPlayer;
import com.estethapp.media.R;
import com.estethapp.media.adapter.HealthViewPagerAdapter;
import com.estethapp.media.helper.Const;
import com.estethapp.media.helper.ExminatingHelper;
import com.estethapp.media.helper.RecorderService;
import com.estethapp.media.recording.PauseResumeAudioRecorder;
import com.estethapp.media.util.BluetoothStatus;
import com.estethapp.media.util.ExminingPrefs;
import com.estethapp.media.util.Helper;
import com.estethapp.media.util.Prefs;
import com.estethapp.media.util.SCODeviceHelper;
import com.estethapp.media.util.Util;
import com.estethapp.media.view.RecorderVisualizerView;
import com.estethapp.media.view.RippleAnimationView;
import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.fabric.sdk.android.Fabric;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_AUTO_CREATE;
//import static android.support.v4.media.session.MediaButtonReceiver.startForegroundService;


public class ExaminingFragment extends Fragment implements View.OnClickListener {
  ExaminingFragment fragment;
  Context context;
  static ExaminingFragment instance;
  View root;
  private static PauseResumeAudioRecorder mediaRecorder;
  private static RippleAnimationView rippleBackground1;
  private static Button btnRecord, btnStop;
  private static TextView txtRecordStatus, txtRecordingTime;
  private static ImageView heartBeahImg;
  private ToggleButton toggleButton;
  PairingFragment.BluetoothHelper mBluetoothHelper;

  ViewPager viewPager;
  private boolean isStart = false;
  private boolean isResume = false;
  boolean isDeviceConnected = true;
  boolean isRecordCompelted = false;


  private SharedPreferences prefsInstance ,prefsInstance2;
  private ExminingPrefs prefs;
  private Prefs prefs1;



  String recordFileDir;
  public static FragmentManager fragmentManager;
  public BluetoothStatus deviceStatus = BluetoothStatus.getInstance();
  public SCODeviceHelper ScoBluetToothInstance = SCODeviceHelper.getInstance();
  private static BluetoothSocket mBluetoothSocket;
  private static BluetoothAdapter bluetoothAdapter = null;
  public static String recordingStatus;
  public boolean isRecordingStart = false;

  public static boolean isRecodingStarted = false;
  public static boolean isFrontPositionsSelected = true;

  public static boolean isAnyPostionSelect = false;
  private BluetoothDevice bdDevice;
  private BluetoothDevice mBluetoothDevice;

  public static boolean recordingPostions[];

  boolean isSCODeviceStart = false;
  int recordingEndTime = 0;
  int recordingStartTime = 0;
  int tempPostion = -1;

  String fileName = "";
  String newFileName = "";
  boolean isPaus = false;
  boolean RecrodingFristTime = true;
  static String devname;

  private static final int PERMISSION_CODE = 1;
  private int mScreenDensity;
  private MediaProjectionManager mProjectionManager;
  private int DISPLAY_WIDTH = 480;
  private int DISPLAY_HEIGHT = 640;
  private MediaProjection mMediaProjection;
  private VirtualDisplay mVirtualDisplay;
  private MediaRecorder mMediaRecorder;
  private boolean isRecording;
  private RecorderVisualizerView visualizerView;
  public static final int REPEAT_INTERVAL = 40;

  private Handler handler = new Handler();

  ExminatingHelper exminatingHelper = ExminatingHelper.getInstance();

  private ArrayList<String> videosFilePaths = new ArrayList<>();
  private int REQUEST_PERMISSIONS = 1122;
  private boolean mBounded;
  private RecorderService myService;

  ServiceConnection mConnection = new ServiceConnection() {
    @Override
    public void onServiceDisconnected(ComponentName name) {
      Toast.makeText(getContext(), getString(R.string.txt_disconnected), Toast.LENGTH_SHORT).show();
      mBounded = false;
      myService = null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      Toast.makeText(getContext(), getString(R.string.txt_connected), Toast.LENGTH_SHORT).show();
      mBounded = true;
      RecorderService.LocalBinder mLocalBinder = (RecorderService.LocalBinder) service;
      myService = mLocalBinder.getService();
    }
  };


  public ExaminingFragment() {
  }

  public static ExaminingFragment newInstance() {
    ExaminingFragment instance = new ExaminingFragment();

    return instance;
  }

  public static ExaminingFragment getInstance() {
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
    return inflater.inflate(R.layout.activity_examining, container, false);
  }


  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    context = getActivity();
    root = view;
    fragment = this;
    Fabric.with(context, new Crashlytics());

    exminatingHelper.reset();
    mBluetoothHelper = ScoBluetToothInstance.getmBluetoothHelper();

    viewPager = (ViewPager) root.findViewById(R.id.viewpager);
    viewPager.setAdapter(new HealthViewPagerAdapter(ExaminingFragment.this));

    prefs = ExminingPrefs.getInstance();
    prefs1= Prefs.getInstance();
    prefsInstance = prefs.init(context);
    prefsInstance2 = prefs1.init(context);










    //  Toast.makeText(getContext(),prefsInstance2.getString("USERNAME","AHSAN"),Toast.LENGTH_SHORT).show();






    btnRecord = (Button) root.findViewById(R.id.btn_record);
    txtRecordStatus = (TextView) root.findViewById(R.id.txt_recordStatus);
    txtRecordingTime = (TextView) root.findViewById(R.id.recording_time);
    btnStop = (Button) root.findViewById(R.id.btn_stop);
    heartBeahImg = (ImageView) root.findViewById(R.id.heartBeatImg);
    visualizerView = (RecorderVisualizerView) root.findViewById(R.id.visualizer);
    txtRecordStatus.setText(getString(R.string.rec_status));

    ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(heartBeahImg,
            PropertyValuesHolder.ofFloat("scaleX", 1.2f),
            PropertyValuesHolder.ofFloat("scaleY", 1.2f));
    scaleDown.setDuration(300);

    scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
    scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

    scaleDown.start();

    toggleButton = (ToggleButton) root.findViewById(R.id.toggleButton);
    rippleBackground1 = (RippleAnimationView) root.findViewById(R.id.content);


    if (isDeviceConnected) {
      if (!rippleBackground1.isRippleAnimationRunning()) {
        rippleBackground1.startRippleAnimation();
      }
    }

    exminatingHelper.setFrontClick(true);

    toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton toggleButton, boolean isFront) {
        boolean isBackPress = exminatingHelper.isFrontClick();
        if (!isFront) {
          if (!isRecordingStart) {
            exminatingHelper.setFrontClick(true);
            viewPager.setCurrentItem(0, true);
            exminatingHelper.isFrontSelected = true;
          } else {
            txtRecordStatus.setText(getString(R.string.txt_position));
          }
        } else {
          viewPager.setCurrentItem(1, true);
          exminatingHelper.setFrontClick(false);
          exminatingHelper.isBackSelected = true;
        }
      }
    });


    viewPager.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return true;
      }
    });

    DisplayMetrics metrics = new DisplayMetrics();
    getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
    mScreenDensity = metrics.densityDpi;
    DISPLAY_WIDTH = metrics.widthPixels;
    DISPLAY_HEIGHT = metrics.heightPixels;

    if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

      ActivityCompat.requestPermissions(this.getActivity(), new String[]{android.Manifest.permission.RECORD_AUDIO,
                      android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
              2);

    } else {
      initRecorder();
      prepareRecorder();
    }

    mProjectionManager = (MediaProjectionManager) getContext().getSystemService
            (Context.MEDIA_PROJECTION_SERVICE);



    btnRecord.setOnClickListener(this);
    btnStop.setOnClickListener(this);
    super.onViewCreated(view, savedInstanceState);
  }

  private boolean isPausedAvailable = false;
  private boolean isResumeVideo = false;

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  public void onClick(View view) {
    int id = view.getId();

    if (!deviceStatus.isDeviceConnected() && !deviceStatus.isSCODeviceStart() && false) {
      showDialog(getString(R.string.txt_title), getString(R.string.txt_connection));
    } else if (!Helper.checkRecordPermissionsArray(getActivity())) {
      Toast.makeText(getActivity(), getString(R.string.txt_permission), Toast.LENGTH_SHORT).show();
      Helper.requestAudioRecordPermissions(getActivity(), 1);
    } else if (id == R.id.btn_record) {

      if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
              + ContextCompat.checkSelfPermission(getContext(),
              Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

        ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                REQUEST_PERMISSIONS);
      } else {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
          //  Toast.makeText(getContext(),"Nougat",Toast.LENGTH_SHORT).show();
          if (isPausedAvailable) {
            if (isResumeVideo) {
              btnRecord.setText(getString(R.string.txt_pause));
              isResumeVideo = false;
              // final int REQUIRED_PERMISSIONS_CODE = 200;
              //On post Nougat recording permission


//                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) !=
//                  PackageManager.PERMISSION_GRANTED ||
//
//                  ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
//                    PackageManager.PERMISSION_GRANTED ||
//                  ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
//                    PackageManager.PERMISSION_GRANTED ||
//                  ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) !=
//                    PackageManager.PERMISSION_GRANTED ||
//                  ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_NETWORK_STATE) !=
//                    PackageManager.PERMISSION_GRANTED ||
//                  ContextCompat.checkSelfPermission(getContext(), Manifest.permission.MODIFY_AUDIO_SETTINGS) !=
//                    PackageManager.PERMISSION_GRANTED ||
//                  //added by kanwal khan
//                  ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
//                    PackageManager.PERMISSION_GRANTED ||
//                  ContextCompat.checkSelfPermission(getContext(), Manifest.permission.FOREGROUND_SERVICE) !=
//                    PackageManager.PERMISSION_GRANTED
//
//                )
//
//                {
//
//
//                  requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_NETWORK_STATE,
//                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.FOREGROUND_SERVICE, Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.CAMERA}, REQUIRED_PERMISSIONS_CODE);
//                }



              Intent resumeIntent = new Intent(getContext(), RecorderService.class);
              resumeIntent.setAction(Const.SCREEN_RECORDING_RESUME);
              //  getContext().startService(resumeIntent);

              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getContext().startForegroundService(resumeIntent);
              } else {
                getContext().startService(resumeIntent);
              }

              soundVisualizer();
              startTime();
            } else {
              btnRecord.setText(getString(R.string.txt_resume));
              handler.removeCallbacks(updateVisualizer);

              Intent pauseIntent = new Intent(getContext(), RecorderService.class);
              pauseIntent.setAction(Const.SCREEN_RECORDING_PAUSE);
              // getContext().startService(pauseIntent);
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getContext().startForegroundService(pauseIntent);
              } else {
                getContext().startService(pauseIntent);
              }

              closeTimer();
              isResumeVideo = true;
              isRecordCompelted = true;

            }
          } else {
            if (mediaRecorder == null) {
              int bodyPostion = exminatingHelper.getFrontBodyPostionSelected();
              if (bodyPostion == -1) {
                Toast.makeText(getActivity(), getString(R.string.txt_sel_pos), Toast.LENGTH_SHORT).show();
              } else {
                showInputFileNameDailog();
              }
            }
          }

        } else {
          onRecordingPreNougat();
        }

      }
    } else if (id == R.id.btn_stop) {

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        if (isPausedAvailable) {
          // handler.removeCallbacks(updateVisualizer);
//Toast.makeText(getContext(),"got",Toast.LENGTH_SHORT).show();
          Intent recorderService = new Intent(getContext(), RecorderService.class);

          recorderService.setAction(Const.SCREEN_RECORDING_STOP);
//          getContext().startService(recorderService);
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getContext().startForegroundService(recorderService);
          } else {
            getContext().startService(recorderService);
          }

          if (mBounded) {
            Toast.makeText(getContext(),"if",Toast.LENGTH_SHORT).show();
            getContext().unbindService(mConnection);
            mBounded = false;


          }

          isPausedAvailable = false;
          closeTimer();
          openVideoPlayerPostNougat();
          Toast.makeText(getContext(),"else",Toast.LENGTH_SHORT).show();
          // after rename file it will not go into next screen
        }
        else
          Toast.makeText(context, getString(R.string.txt_norec), Toast.LENGTH_SHORT).show();
      } else {
        onStopRecordingPreNougat();
      }
    }

  }

  @RequiresApi(api = Build.VERSION_CODES.KITKAT)
  private void onStopRecordingPreNougat() {
    if (null != mMediaRecorder && isPausedAvailable) {
      exminatingHelper.saveEximatiningInfo(fileName, prefsInstance);
      exminatingHelper.reset();
      isStart = false;
      isResume = false;
      btnRecord.setText(getString(R.string.txt_start));
      closeTimer();
      txtRecordingTime.setText("00:00");
      mMediaRecorder.stop();
      mMediaRecorder.reset();
      mMediaProjection = null;
      isPausedAvailable = false;
      stopScreenSharing();
      isRecordCompelted = true;
      handler.removeCallbacks(updateVisualizer);

      openPlayerOnVideoComplete();
    } else {
      if (isResumeVideo)
        openPlayerOnVideoComplete();
      else
        Toast.makeText(context, getString(R.string.txt_error), Toast.LENGTH_SHORT).show();
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void onRecordingPreNougat() {
    if (isPausedAvailable) {
      if (isResumeVideo) {
        btnRecord.setText(getString(R.string.txt_pause));
        isResumeVideo = false;

        initRecorder();
        prepareRecorder();
        shareScreen();
      } else {
        btnRecord.setText(getString(R.string.txt_resume));
        closeTimer();
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder = null;
        mMediaProjection = null;
        stopScreenSharing();
        isResumeVideo = true;

        isRecordCompelted = true;

        handler.removeCallbacks(updateVisualizer);

      }
    } else {
      if (mediaRecorder == null) {
        int bodyPostion = exminatingHelper.getFrontBodyPostionSelected();
        if (bodyPostion == -1) {
          Toast.makeText(getActivity(), getString(R.string.txt_pos), Toast.LENGTH_SHORT).show();
        } else {
          showInputFileNameDailog();
        }
      }
    }
  }

  private String mergeVideoFiles() throws IOException {

    List<Track> videoTracks = new LinkedList<Track>();
    List<Track> audioTracks = new LinkedList<Track>();

    for (String filePath : videosFilePaths) {
      File f = new File(filePath);

      Movie m = MovieCreator.build(f.getAbsolutePath());
      for (Track t : m.getTracks()) {
        if (t.getHandler().equals("soun")) {
          audioTracks.add(t);
        }

        if (t.getHandler().equals("vide")) {
          videoTracks.add(t);
        }
      }
    }

    //DELETE FILES HERE
    for (String filePath : videosFilePaths) {

      File fdelete = new File(filePath);
      if (fdelete.exists()) {
        fdelete.delete();

      }
    }

    Movie result = new Movie();

    if (audioTracks.size() > 0) {
      result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
    }
    if (videoTracks.size() > 0) {
      result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
    }

    BasicContainer out = (BasicContainer) new DefaultMp4Builder().build(result);

    fileName = "capture_" + getCurSysDate() + ".mp4";
    FileChannel fc = new RandomAccessFile(Util.getDirectory() + File.separator + fileName, "rw").getChannel();
    out.writeContainer(fc);
    fc.close();

    return "";
  }

  private void openPlayerOnVideoComplete() {
    if (isRecordCompelted) {
      final ProgressDialog dialog = new ProgressDialog(getContext());
      dialog.setMessage(getString(R.string.aus_msg));
      dialog.setCancelable(false);
      dialog.show();

      try {
        //get final video path here
        if (videosFilePaths.size() > 1)
          mergeVideoFiles();
      } catch (IOException e) {
        e.printStackTrace();
      }


      if (!deviceStatus.isDeviceConnected() && !deviceStatus.isSCODeviceStart()) {
        new CountDownTimer(1000, 1000) {

          @Override
          public void onTick(long millisUntilFinished) {

          }

          @Override
          public void onFinish() {
            dialog.dismiss();
            openVideoPlayer();
          }
        }.start();

      } else {
        try {
          BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
          if (bluetoothAdapter != null)
            bluetoothAdapter.disable();
          mBluetoothHelper.stop();
          mBluetoothHelper.onScoAudioDisconnected();
        } catch (Exception e) {
          e.printStackTrace();
        }

        new CountDownTimer(1000, 1000) {

          @Override
          public void onTick(long millisUntilFinished) {

          }

          @Override
          public void onFinish() {
            dialog.dismiss();
            Toast.makeText(context, getString(R.string.aux_succes), Toast.LENGTH_SHORT).show();
            openVideoPlayer();
          }
        }.start();
      }
    } else {
      Toast.makeText(context, getString(R.string.txt_record), Toast.LENGTH_SHORT).show();
    }
  }

  private void openVideoPlayer() {

    android.util.Log.e("ahsan", "kanwal 6 " );
    MediaScannerConnection
            .scanFile(
                    getContext(),
                    new String[]{Util.getDirectory()},
                    null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                      public void onScanCompleted(
                              String path, Uri uri) {

                      }
                    });


    exminatingHelper.reset();
    Intent intent = new Intent(context, ActivityPlayer.class);
    File file = new File(Environment.getExternalStorageDirectory() + "/estethApp/" +
            File.separator + prefsInstance2.getString("USERNAME","AHSAN") + File.separator +
            fileName);
    File to = new File(Environment.getExternalStorageDirectory() + "/estethApp/"
            +
            File.separator+prefsInstance2.getString("USERNAME","AHSAN") + File.separator, newFileName);
    if (file.exists()) {
      if (file.renameTo(to)) {

        intent.putExtra("fileName", newFileName);
        intent.putExtra("dir", Environment.getExternalStorageDirectory() + "/estethApp/"
                +  File.separator + prefsInstance2.getString("USERNAME","AHSAN") + File.separator + newFileName);
        Log.e("filepath",""+file);
        Log.e("kanwalto",""+to);
        //     Toast.makeText(getContext(),"filepath"+file,Toast.LENGTH_SHORT).show();
        //  Toast.makeText(getContext(),"topath"+to,Toast.LENGTH_SHORT).show();
        //     Toast.makeText(getContext(),Environment.getExternalStorageDirectory() + "/estethApp/"
        //   +  File.separator + prefsInstance2.getString("USERNAME","AHSAN") + File.separator + newFileName,Toast.LENGTH_SHORT).show();
        startActivity(intent);

      }
    }

    videosFilePaths.clear();

  }

  private void openVideoPlayerPostNougat() {

    MediaScannerConnection
            .scanFile(
                    getContext(),
                    new String[]{Util.getDirectory()},
                    null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                      public void onScanCompleted(
                              String path, Uri uri) {

                      }
                    });

    exminatingHelper.reset();
    Intent intent = new Intent(context, ActivityPlayer.class);
    File file = new File(Environment.getExternalStorageDirectory() + "/estethApp/"  +
            File.separator + prefsInstance2.getString("USERNAME","AHSAN") + File.separator  ,
            newFileName);
    if (file.exists()) {
      intent.putExtra("fileName", newFileName);
      intent.putExtra("dir", Environment.getExternalStorageDirectory() + "/estethApp/"
              +
              File.separator + prefsInstance2.getString("USERNAME","AHSAN") + File.separator +
              newFileName);
      Log.e("scnd", "iam"+file);

      startActivity(intent);
    }
  }

  public void showDialog(String title, String content) {
    AlertDialog.Builder b = new AlertDialog.Builder(context)
            .setMessage(content)
            .setTitle(title)
            .setPositiveButton(context.getString(R.string.txt_ok),
                    new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int whichButton) {
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, PairingFragment.newInstance(true), null)
                                .commit();
                        dialog.dismiss();

                      }
                    }
            );
    b.show();
  }


  public static void enableRecordingButton() {
    rippleBackground1.startRippleAnimation();
    btnRecord.setBackgroundResource(R.drawable.circle_selected);
    btnRecord.setEnabled(true);
  }

  public static void delayFiveSecond() {
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        mediaRecorder.resumeRecording();
      }
    }, 1000);
  }

  int seconds;
  int minute;
  Timer timer;

  public void startTime() {
    timer = new Timer("hello", true);
    MyTimeCounter task = new MyTimeCounter();
    timer.schedule(task, 1000, 1000);
  }

  public void pauseTimer() {
    recordingStartTime = recordingEndTime;
    this.timer.cancel();
  }

  public void closeTimer() {
    if (timer != null)
      this.timer.cancel();
  }

  public void resumeTimer() {
    timer = new Timer();
    MyTimeCounter task = new MyTimeCounter();
    timer.schedule(task, 0, 1000);
  }

  private class MyTimeCounter extends TimerTask {
    public void run() {

      txtRecordingTime.post(new Runnable() {

        public void run() {
          seconds++;
          recordingEndTime++;
          if (seconds == 60) {
            seconds = 0;
            minute++;
          }
          if (minute == 60) {
            minute = 0;
          }
          txtRecordingTime.setText(""
                  + (minute > 9 ? minute : ("0" + minute))
                  + " : "
                  + (seconds > 9 ? seconds : "0" + seconds));

        }
      });

    }
  }

  public void showPostionsBeforeRecording() {

    boolean isFront = exminatingHelper.isFrontClick();
    if (isFront) {
      int bodyPostion = exminatingHelper.getFrontBodyPostionSelected();
      String positionNames = exminatingHelper.getFrontPostionName(bodyPostion);
      txtRecordStatus.setText(positionNames);
    } else {
      int bodyPostion = exminatingHelper.getBackBodyPostionSelected();
      String positionNames = exminatingHelper.getBackPostionName(bodyPostion);
      txtRecordStatus.setText(positionNames);
    }
  }


  public void resetTimer() {
    seconds = 0;
    minute = 0;
    recordingEndTime = 0;
  }

  public void showInputFileNameDailog() {
    LayoutInflater li = LayoutInflater.from(context);
    View promptsView = li.inflate(R.layout.prompts, null);

//        fileName = Util.getCurrentDateTime();
    recordFileDir = Util.getDirectory();


    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
            context);

    // set prompts.xml to alertdialog builder
    alertDialogBuilder.setView(promptsView);

    final EditText userInput = (EditText) promptsView
            .findViewById(R.id.editTextDialogUserInput);
    userInput.setText(fileName);
    userInput.setSelection(userInput.getText().length());
    // set dialog message
    alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton(context.getString(R.string.txt_ok),
                    new DialogInterface.OnClickListener() {
                      @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                      public void onClick(DialogInterface dialog, int id) {
                        if (!userInput.getText().toString().equals("")) {

                          newFileName = userInput.getText().toString();
                          if (!newFileName.contains(".mp4"))
                            newFileName = newFileName + ".mp4";

                          btnRecord.setText(context.getString(R.string.txt_pause));
                          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            isPausedAvailable = true;
                            startActivityForResult(mProjectionManager.createScreenCaptureIntent(),
                                    Const.SCREEN_RECORD_REQUEST_CODE);
                          } else
                            shareScreen();
                        } else {
                          Toast.makeText(context, getString(R.string.txt_file), Toast.LENGTH_SHORT).show();
                        }


                      }
                    })
            .setNegativeButton(context.getString(R.string.txt_cancel),
                    new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                      }
                    });

    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();

  }

  private void resetStartButtonText() {
    btnRecord.setText(context.getString(R.string.txt_start));
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  public void onDestroy() {
    if (mMediaProjection != null) {
      mMediaProjection.stop();
      mMediaProjection = null;
    }
    super.onDestroy();
  }

  @RequiresApi(api = Build.VERSION_CODES.KITKAT)
  private void stopScreenSharing() {
    if (mVirtualDisplay == null) {
      return;
    }
    mVirtualDisplay.release();
  }


  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private VirtualDisplay createVirtualDisplay() {
    return mMediaProjection.createVirtualDisplay("ExaminingFragment",
            DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mMediaRecorder.getSurface(), null /*Callbacks*/, null /*Handler*/);
  }


  private void prepareRecorder() {
    try {
      mMediaRecorder.prepare();

    } catch (IllegalStateException | IOException e) {
      e.printStackTrace();
      System.out.println("PREPARE RECORDER ERROR: " + e);
    }
  }

  public String getFilePath() {
    final String directory = Environment.getExternalStorageDirectory() + File.separator + "estethApp"  +
            File.separator + prefsInstance2.getString("USERNAME","AHSAN") ;

    final File folder = new File(directory);
    boolean success = true;
    if (!folder.exists()) {
      success = folder.mkdirs();
    }
    String filePath;
    if (success) {
      fileName = "capture_" + getCurSysDate() + ".mp4";
      String videoName = fileName;
      filePath = directory + File.separator + videoName;

    } else {
      Toast.makeText(getContext(), getString(R.string.error_rec), Toast.LENGTH_SHORT).show();
      return null;
    }
    return filePath;
  }

  public String getCurSysDate() {
    return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
  }

  private void initRecorder() {
    if (mMediaRecorder == null) {
      mMediaRecorder = new MediaRecorder();
      mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
      mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

      mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
      mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
      mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
      mMediaRecorder.setAudioEncodingBitRate(384000);
      mMediaRecorder.setAudioSamplingRate(44100);
      mMediaRecorder.setVideoEncodingBitRate(912 * 1000);
//            mMediaRecorder.setVideoEncodingBitRate(7130317);
      mMediaRecorder.setVideoFrameRate(30);
      mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);

      String videoPath = getFilePath();
      videosFilePaths.add(videoPath);
      mMediaRecorder.setOutputFile(videoPath);
      //  Toast.makeText(getContext(),"i am get" +videoPath,Toast.LENGTH_SHORT).show();



    }
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void shareScreen() {
    if (mMediaProjection == null) {
      startActivityForResult(mProjectionManager.createScreenCaptureIntent(), PERMISSION_CODE);
      return;
    }
    mVirtualDisplay = createVirtualDisplay();
    if (mVirtualDisplay != null)
      //  prepareRecorder();
      mMediaRecorder.start();
    isRecordCompelted = false;
    isPausedAvailable = true;
    soundVisualizer();
    startTime();

  }



  @Override
  public void onStop() {
    super.onStop();
    if (mBounded) {
      getContext().unbindService(mConnection);
      mBounded = false;
    }

  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    final int REQUIRED_PERMISSIONS_CODE = 200;
    //On post Nougat recording permission
    if (resultCode == RESULT_OK && requestCode == Const.SCREEN_RECORD_REQUEST_CODE) {


      if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) !=
              PackageManager.PERMISSION_GRANTED ||

              ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                      PackageManager.PERMISSION_GRANTED ||
              ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                      PackageManager.PERMISSION_GRANTED ||
              ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) !=
                      PackageManager.PERMISSION_GRANTED ||
              ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_NETWORK_STATE) !=
                      PackageManager.PERMISSION_GRANTED ||
              ContextCompat.checkSelfPermission(getContext(), Manifest.permission.MODIFY_AUDIO_SETTINGS) !=
                      PackageManager.PERMISSION_GRANTED ||
              //added by kanwal khan
              ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                      PackageManager.PERMISSION_GRANTED ||
              ContextCompat.checkSelfPermission(getContext(), Manifest.permission.FOREGROUND_SERVICE) !=
                      PackageManager.PERMISSION_GRANTED

      )

      {


        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.FOREGROUND_SERVICE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.CAMERA}, REQUIRED_PERMISSIONS_CODE);
      }












      Intent recorderService = new Intent(getContext(), RecorderService.class);
      recorderService.setAction(Const.SCREEN_RECORDING_START);
      recorderService.putExtra(Const.RECORDER_INTENT_DATA, data);
      recorderService.putExtra(Const.RECORDER_INTENT_RESULT, resultCode);
      recorderService.putExtra(Const.VIDEO_FILE_NAME, newFileName);
      if (getContext() != null) {
//        getContext().startService(recorderService);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          getContext().startForegroundService(recorderService);
        } else {
          getContext().startService(recorderService);
        }
        getContext().bindService(recorderService, mConnection, BIND_AUTO_CREATE);
        soundVisualizer();
        startTime();
      }
      return;
    }


    if (requestCode != PERMISSION_CODE) {
      return;
    }
    if (resultCode != RESULT_OK) {
      Toast.makeText(this.getContext(),
              getString(R.string.txt_per), Toast.LENGTH_SHORT).show();
      resetStartButtonText();
      return;
    }

    mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);

    mVirtualDisplay = createVirtualDisplay();
//    try {
//      mMediaRecorder.prepare();
//    } catch (IOException e) {
//      Log.e(TAG, "startRecording: ", e);
//    }
    //  prepareRecorder();
    mMediaRecorder.start();
    isPausedAvailable = true;

    soundVisualizer();
    startTime();
  }

  Runnable updateVisualizer = new Runnable() {
    @Override
    public void run() {

      // get the current amplitude
      int x;

      if (!mBounded) {
        x = mMediaRecorder.getMaxAmplitude();

        Log.e("msg",""+x);
//        Toast.makeText(getContext(),""+x,Toast.LENGTH_SHORT).show();
      }
      else
        x = myService.getMaxAmplitude();

      if (x > 4000) {
        visualizerView.addAmplitude(x); // update the VisualizeView
        visualizerView.invalidate(); // refresh the VisualizerView
      } else {
        visualizerView.addAmplitude(300); // update the VisualizeView
        visualizerView.invalidate(); // refresh the VisualizerView
      }
      // update in 40 milliseconds
      handler.postDelayed(this, REPEAT_INTERVAL);

    }
  };




  public void soundVisualizer() {

    try {
      handler.post(updateVisualizer);
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }
  }


//  public MediaRecorder getMediaRecorder() {
//    if (mMediaRecorder != null)
//      return mMediaRecorder;
//    return null;
//
//  }



//  public static boolean isMicrophoneAvailable() {
//  AudioManager audioManager = (AudioManager) MyApp.getAppContext().getSystemService(Context.AUDIO_SERVICE);
//    return audioManager.getMode() == MODE_NORMAL;
//  }


//ismn getContext change kia hai

}

