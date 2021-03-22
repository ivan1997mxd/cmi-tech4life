//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.estethapp.media.helper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.display.VirtualDisplay;
import android.hardware.display.VirtualDisplay.Callback;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat.Action;
import androidx.core.app.NotificationCompat.Builder;
import androidx.core.content.ContextCompat;
import androidx.media.app.NotificationCompat.MediaStyle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.WindowManager;
import android.widget.Toast;


import com.estethapp.media.util.Prefs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecorderService extends Service {
  private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
  private int WIDTH;
  private int HEIGHT;
  private int FPS;
  private int DENSITY_DPI;

  private int BITRATE;
  private String audioRecSource;
  private String SAVEPATH;
  private AudioManager mAudioManager;
  private int screenOrientation;
  private String saveLocation;
  private boolean isRecording;
  private boolean useFloatingControls;
  private boolean showCameraOverlay;
  private boolean showTouches;
  private boolean isShakeGestureActive;
  private boolean showSysUIDemo = false;
  private NotificationManager mNotificationManager;
  private Intent data;
  private int result;
  private long startTime;
  private long elapsedTime = 0L;
  private SharedPreferences prefs, prefsInstance2;
  private Prefs prefss ;
  private Context context;

  private WindowManager window;
  private MediaProjection mMediaProjection;
  private VirtualDisplay mVirtualDisplay;
  private RecorderService.MediaProjectionCallback mMediaProjectionCallback;
  private MediaRecorder mMediaRecorder;
  private String fileName = "";
  private final IBinder binder = new RecorderService.LocalBinder();

  public RecorderService() {
  }

  public IBinder onBind(Intent intent) {
    return this.binder;
  }
//s
  public int getMaxAmplitude() {


       return this.mMediaRecorder != null ? this.mMediaRecorder.getMaxAmplitude() : 0;


  }
//public int getMaxAmplitude() {
//  try {
//    return mMediaRecorder.getMaxAmplitude();
//  } catch (Exception e) {
//    return 0;
//  }
//}
  @SuppressLint({"WrongConstant", "ResourceType"})
  @TargetApi(21)
  @RequiresApi(
    api = 19
  )
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (VERSION.SDK_INT >= 26) {
//      MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getApplication()
//        .getSystemService(Context.MEDIA_PROJECTION_SERVICE);
     // startForeground(ID, Notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
      this.createNotificationChannels();

    }
    prefss= Prefs.getInstance();
    prefsInstance2 = prefss.init(context);

    this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
    this.mAudioManager = (AudioManager)this.getSystemService("audio");
    prefss= Prefs.getInstance();
    prefsInstance2 = prefss.init(context);


    String var4 = intent.getAction();
    byte var5 = -1;
    switch(var4.hashCode()) {
      case -863723698:
        if (var4.equals("com.estethapp.media.services.action.startrecording")) {
          var5 = 0;
        }
        break;
      case 726307448:
        if (var4.equals("com.estethapp.media.services.action.destoryshakegesture")) {
          var5 = 4;
        }
        break;
      case 1011930320:
        if (var4.equals("com.estethapp.media.services.action.stoprecording")) {
          var5 = 3;
        }
        break;
      case 1370080314:
        if (var4.equals("com.estethapp.media.services.action.pauserecording")) {
          var5 = 1;
        }
        break;
      case 1754951877:
        if (var4.equals("com.estethapp.media.services.action.resumerecording")) {
          var5 = 2;
        }
    }

    switch(var5) {
      case 0:
        if (!this.isRecording) {
          this.screenOrientation = ((WindowManager)this.getSystemService("window")).getDefaultDisplay().getRotation();
          this.data = (Intent)intent.getParcelableExtra("recorder_intent_data");
          this.result = intent.getIntExtra("recorder_intent_result", -1);
          this.fileName = intent.getStringExtra("fileName");
          this.getValues();
           {






            this.startRecording(); }
        } else {
          Toast.makeText(this, "hello",Toast.LENGTH_SHORT).show();
          Toast.makeText(this, 2131624097, 0).show();
        }
        break;
      case 1:
        this.pauseScreenRecording();
        break;
      case 2:
        this.resumeScreenRecording();
        break;
      case 3:
        this.stopRecording();
        break;
      case 4:
        this.stopSelf();
    }

    return 1;
  }

  @RequiresApi(
    api = 19
  )
  public void stopRecording() {
    this.stopScreenSharing();
    this.stopForeground(true);
  }

  @SuppressLint("ResourceType")
  @TargetApi(24)
  private void pauseScreenRecording() {
    this.mMediaRecorder.pause();
    this.elapsedTime += System.currentTimeMillis() - this.startTime;
    Toast.makeText(this, this.getString(2131624162), 0).show();
    Intent recordResumeIntent = new Intent(this, RecorderService.class);
    recordResumeIntent.setAction("com.estethapp.media.services.action.resumerecording");
    PendingIntent precordResumeIntent = PendingIntent.getService(this, 0, recordResumeIntent, 0);
    Action action = new Action(2131165321, this.getString(2131624089), precordResumeIntent);
    this.updateNotification(this.createRecordingNotification(action).setUsesChronometer(false).build(), 5001);
  }

  @TargetApi(24)
  private void resumeScreenRecording() {
    this.mMediaRecorder.resume();
    this.startTime = System.currentTimeMillis();
    Intent recordPauseIntent = new Intent(this, RecorderService.class);
    recordPauseIntent.setAction("com.estethapp.media.services.action.pauserecording");
    PendingIntent precordPauseIntent = PendingIntent.getService(this, 0, recordPauseIntent, 0);
    @SuppressLint("ResourceType") Action action = new Action(2131165320, this.getString(2131624088), precordPauseIntent);
    this.updateNotification(this.createRecordingNotification(action).setUsesChronometer(true).setWhen(System.currentTimeMillis() - this.elapsedTime).build(), 5001);
  }

  @SuppressLint("ResourceType")
  @RequiresApi(
    api = 21
  )
  private void startRecording() {
//    startForeground(25, , ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
    if(mMediaRecorder==null){
    Toast.makeText(getApplicationContext(),"if ka ",Toast.LENGTH_SHORT).show();
//    this.mMediaRecorder.release();
    this.mMediaRecorder = new MediaRecorder(); }
    else {
      this.mMediaRecorder = new MediaRecorder();
      Toast.makeText(getApplicationContext(),"else ka  ",Toast.LENGTH_SHORT).show();
    }

    Toast.makeText(getApplicationContext(),"mein hun na",Toast.LENGTH_SHORT).show();
    this.mMediaRecorder.setOnErrorListener(new OnErrorListener() {
      public void onError(MediaRecorder mr, int what, int extra) {
        Log.e("SCREENRECORDER_LOG", "Screencam Error: " + what + ", Extra: " + extra);
        RecorderService.this.destroyMediaProjection();
      }
    });
    this.initRecorder();
  //  startForeground(1003, Notification , ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
    this.mMediaProjectionCallback = new RecorderService.MediaProjectionCallback();
    @SuppressLint("WrongConstant") MediaProjectionManager mProjectionManager = (MediaProjectionManager)
      this.getSystemService("media_projection");
//    MediaProjectionManager mProjectionManager = (MediaProjectionManager)
//    this.getSystemService(MEDIA_PROJECTION_SERVICE)  ;

    this.mMediaProjection = mProjectionManager.getMediaProjection(this.result, this.data);
    this.mMediaProjection.registerCallback(this.mMediaProjectionCallback, (Handler)null);
    this.mVirtualDisplay = this.createVirtualDisplay();

    try {
      this.mMediaRecorder.start();
      this.isRecording = true;
      Toast.makeText(this, 2131624095, 0).show();
    } catch (IllegalStateException var3) {
      Log.e("SCREENRECORDER_LOG", "Mediarecorder reached Illegal state exception. Did you start the recording twice?");
      Toast.makeText(this, 2131624081, 0).show();
      this.isRecording = false;
      this.mMediaProjection.stop();
      this.stopSelf();
    }

    if (VERSION.SDK_INT >= 24) {
      this.startTime = System.currentTimeMillis();
//      Toast.makeText(getApplicationContext(),"Greater than 24",Toast.LENGTH_SHORT).show();
      this.startNotificationForeGround(this.createRecordingNotification((Action)null).build(), 5001);
    } else {
      this.startNotificationForeGround(this.createRecordingNotification((Action)null).build(), 5001);
    }

  }

  @RequiresApi(
    api = 21
  )
  private VirtualDisplay createVirtualDisplay() {
    return this.mMediaProjection.createVirtualDisplay("MainActivity",
      this.WIDTH, this.HEIGHT, this.DENSITY_DPI, 16, this.mMediaRecorder.getSurface(), (Callback)null, (Handler)null);
  }

  @RequiresApi(
    api = 17
  )
  public int getBestSampleRate() {
    @SuppressLint("WrongConstant") AudioManager am = (AudioManager)this.getSystemService("audio");
    String sampleRateString = am.getProperty("android.media.property.OUTPUT_SAMPLE_RATE");
    int samplingRate = sampleRateString == null ? '걄' : Integer.parseInt(sampleRateString);
    Log.d("SCREENRECORDER_LOG", "Sampling rate: " + samplingRate);
    return samplingRate;
  }

  @RequiresApi(
    api = 21
  )
  private boolean getMediaCodecFor(String format) {
    MediaCodecList list = new MediaCodecList(0);
    MediaFormat mediaFormat = MediaFormat.createVideoFormat(format, this.WIDTH, this.HEIGHT);
    String encoder = list.findEncoderForFormat(mediaFormat);
    if (encoder == null) {
      Log.d("Null Encoder: ", format);
      return false;
    } else {
      Log.d("Encoder", encoder);
      return !encoder.startsWith("OMX.google");
    }
  }

  @RequiresApi(
    api = 21
  )
  private int getBestVideoEncoder() {
    int VideoCodec = 0;
    if (this.getMediaCodecFor("video/hevc")) {
      if (VERSION.SDK_INT >= 24) {
        VideoCodec = 5;
      }
    } else if (this.getMediaCodecFor("video/avc")) {
      VideoCodec = 2;
    }

    return VideoCodec;
  }

  @TargetApi(21)
  @RequiresApi(
    api = 18
  )
  private void initRecorder() {
    boolean mustRecAudio = false;

    try {
      String audioBitRate = "384000";
      String audioSamplingRate = this.getBestSampleRate() + "";
      String audioChannel = "1";
      String var5 = this.audioRecSource;
      byte var6 = -1;
      switch(var5.hashCode()) {
        case 49:
          if (var5.equals("1")) {
            var6 = 0;
          }
          break;
        case 50:
          if (var5.equals("2")) {
            var6 = 1;
          }
          break;
        case 51:
          if (var5.equals("3")) {
            var6 = 2;
          }
      }

      switch(var6) {
        case 0:
          this.mMediaRecorder.setAudioSource(1);
          mustRecAudio = true;
          break;
        case 1:
          this.mMediaRecorder.setAudioSource(0);
          this.mMediaRecorder.setAudioEncodingBitRate(Integer.parseInt(audioBitRate));
          this.mMediaRecorder.setAudioSamplingRate(Integer.parseInt(audioSamplingRate));
          this.mMediaRecorder.setAudioChannels(Integer.parseInt(audioChannel));
          mustRecAudio = true;
          Log.d("SCREENRECORDER_LOG", "bit rate: " + audioBitRate + " sampling: " + audioSamplingRate + " channel" + audioChannel);
          break;
        case 2:
          this.mAudioManager.setParameters("screenRecordAudioSource=8");
          this.mMediaRecorder.setAudioSource(8);
          this.mMediaRecorder.setAudioEncodingBitRate(Integer.parseInt(audioBitRate));
          this.mMediaRecorder.setAudioSamplingRate(Integer.parseInt(audioSamplingRate));
          this.mMediaRecorder.setAudioChannels(Integer.parseInt(audioChannel));
          mustRecAudio = true;
      }

      this.mMediaRecorder.setVideoSource(2);
      this.mMediaRecorder.setOutputFormat(2);
      this.mMediaRecorder.setOutputFile(this.SAVEPATH);
      this.mMediaRecorder.setVideoSize(this.WIDTH, this.HEIGHT);
      this.mMediaRecorder.setVideoEncoder(2);
      if (mustRecAudio) {
        this.mMediaRecorder.setAudioEncoder(3);
      }

      this.mMediaRecorder.setAudioEncodingBitRate(384000);
      this.mMediaRecorder.setAudioSamplingRate(44100);
      this.mMediaRecorder.setVideoEncodingBitRate(this.BITRATE);
      this.mMediaRecorder.setVideoFrameRate(this.FPS);
      this.mMediaRecorder.prepare();
    } catch (IOException var7) {
      var7.printStackTrace();
    }

  }

  @TargetApi(26)
  private void createNotificationChannels() {
    List<NotificationChannel> notificationChannels = new ArrayList();
    NotificationChannel recordingNotificationChannel =
      new NotificationChannel("recording_notification_channel_id1",
        "Shown Persistent notification when recording screen or when waiting for shake gesture", 3);
    recordingNotificationChannel.enableLights(true);
    recordingNotificationChannel.setSound((Uri)null, (AudioAttributes)null);
    recordingNotificationChannel.setLightColor(-65536);
    recordingNotificationChannel.setShowBadge(true);
    recordingNotificationChannel.enableVibration(true);
    recordingNotificationChannel.setLockscreenVisibility(1);
    notificationChannels.add(recordingNotificationChannel);


    NotificationChannel shareNotificationChannel = new NotificationChannel("share_notification_channel_id1", "Show Notification to share or edit the recorded video", 3);
    shareNotificationChannel.enableLights(true);
    shareNotificationChannel.setLightColor(-65536);
    shareNotificationChannel.setShowBadge(true);
    shareNotificationChannel.enableVibration(true);
    shareNotificationChannel.setLockscreenVisibility(1);
    notificationChannels.add(shareNotificationChannel);
    this.getManager().createNotificationChannels(notificationChannels);
  }

  private Builder createRecordingNotification(Action action) {
    Bitmap icon = BitmapFactory.decodeResource(this.getResources(), 2131558428);
    Builder notification = (new Builder(this, "recording_notification_channel_id1")).setContentTitle(this.getResources().getString(2131624091)).setTicker(this.getResources().getString(2131624091)).setSmallIcon(2131558428).setLargeIcon(icon).setUsesChronometer(true).setOngoing(true).setStyle(new MediaStyle()).setPriority(1);
    if (action != null) {
      notification.addAction(action);
    }

    return notification;
  }

  private void startNotificationForeGround(Notification notification, int ID) {
    this.startForeground(ID, notification);
  }

  private void updateNotification(Notification notification, int ID) {
    this.getManager().notify(ID, notification);
  }

  @SuppressLint("WrongConstant")
  private NotificationManager getManager() {
    if (this.mNotificationManager == null) {
      this.mNotificationManager = (NotificationManager)this.getSystemService("notification");
    }

    return this.mNotificationManager;
  }

  public void onDestroy() {
    Log.d("SCREENRECORDER_LOG", "Recorder service destroyed");
    super.onDestroy();
  }

  @RequiresApi(
    api = 17
  )
  public void getValues() {
    String res = this.getResolution();
    this.setWidthHeight(res);
    this.FPS = Integer.parseInt("30");
    this.BITRATE = Integer.parseInt("7130317");
    this.audioRecSource = "1";
    this.saveLocation = this.prefs.getString(this.getString(2131624086),
      Environment.getExternalStorageDirectory() + File.separator +
        "estethApp"+File.separator+ prefsInstance2.getString("USERNAME","eSteth"));
    File saveDir = new File(this.saveLocation);
  //  Toast.makeText(getApplicationContext(),""+saveDir,Toast.LENGTH_SHORT).show();
    if (Environment.getExternalStorageState().equals("mounted") && !saveDir.isDirectory()) {
      saveDir.mkdirs();
    }

    this.useFloatingControls = false;
    this.showSysUIDemo = false;
    this.showCameraOverlay = false;
    this.showTouches = false;
    this.SAVEPATH = this.saveLocation + File.separator + this.fileName;
    this.isShakeGestureActive = false;
  }

  private void setWidthHeight(String res) {
    String[] widthHeight = res.split("x");
    this.WIDTH = Integer.parseInt(widthHeight[0]);
    this.HEIGHT = Integer.parseInt(widthHeight[1]);
    Log.d("SCREENRECORDER_LOG", "Width: " + this.WIDTH + "Height:" + this.HEIGHT);
  }

  @SuppressLint("WrongConstant")
  @RequiresApi(
    api = 17
  )
  private String getResolution() {
    DisplayMetrics metrics = new DisplayMetrics();
    this.window = (WindowManager)this.getSystemService("window");
    this.window.getDefaultDisplay().getRealMetrics(metrics);
    this.DENSITY_DPI = metrics.densityDpi;
    int width = metrics.widthPixels;
    width = Integer.parseInt(Integer.toString(width));
    float aspectRatio = this.getAspectRatio(metrics);
    int height = this.calculateClosestHeight(width, aspectRatio);
    String res = width + "x" + height;
    Log.d("SCREENRECORDER_LOG", "resolution service: [Width: " + width + ", Height: " + (float)width * aspectRatio + ", aspect ratio: " + aspectRatio + "]");
    return res;
  }

  private int calculateClosestHeight(int width, float aspectRatio) {
    int calculatedHeight = (int)((float)width * aspectRatio);
    Log.d("SCREENRECORDER_LOG", "Calculated width=" + calculatedHeight);
    Log.d("SCREENRECORDER_LOG", "Aspect ratio: " + aspectRatio);
    if (calculatedHeight / 16 != 0) {
      int quotient = calculatedHeight / 16;
      Log.d("SCREENRECORDER_LOG", calculatedHeight + " not divisible by 16");
      calculatedHeight = 16 * quotient;
      Log.d("SCREENRECORDER_LOG", "Maximum possible height is " + calculatedHeight);
    }

    return calculatedHeight;
  }

  private float getAspectRatio(DisplayMetrics metrics) {
    float screen_width = (float)metrics.widthPixels;
    float screen_height = (float)metrics.heightPixels;
    float aspectRatio;
    if (screen_width > screen_height) {
      aspectRatio = screen_width / screen_height;
    } else {
      aspectRatio = screen_height / screen_width;
    }

    return aspectRatio;
  }

  @SuppressLint("ResourceType")
  @TargetApi(21)
  @RequiresApi(
    api = 19
  )
  private void destroyMediaProjection() {
    this.mAudioManager.setParameters("screenRecordAudioSource=0");
// is block pe masla kar raha hai
    try {
      this.mMediaRecorder.stop();
      this.indexFile();
      Log.i("SCREENRECORDER_LOG", "MediaProjection Stopped");
    } catch (RuntimeException var5) {
      Log.e("SCREENRECORDER_LOG", "Fatal exception! Destroying media projection failed.\n" + var5.getMessage());
      if ((new File(this.SAVEPATH)).delete()) {
        Log.d("SCREENRECORDER_LOG", "Corrupted file delete successful");
      }

      Toast.makeText(this, this.getString(2131624014), 0).show();
    } finally {

      this.mMediaRecorder.reset();
      this.mVirtualDisplay.release();
      this.mMediaRecorder.release();
      if (this.mMediaProjection != null) {
        this.mMediaProjection.unregisterCallback(this.mMediaProjectionCallback);
        this.mMediaProjection.stop();
        this.mMediaProjection = null;
      }

      this.stopSelf();
      this.stopForeground(true);
    }

    this.isRecording = false;
  }

  private void indexFile() {
    ArrayList<String> toBeScanned = new ArrayList();
    toBeScanned.add(this.SAVEPATH);
    String[] toBeScannedStr = new String[toBeScanned.size()];
    toBeScannedStr = (String[])toBeScanned.toArray(toBeScannedStr);
    MediaScannerConnection.scanFile(this, toBeScannedStr, (String[])null, new OnScanCompletedListener() {
      public void onScanCompleted(String path, Uri uri) {
        RecorderService.this.stopSelf();

        RecorderService.this.stopForeground(true);
      }
    });
  }

  @RequiresApi(
    api = 19
  )
  private void stopScreenSharing() {
    if (this.mVirtualDisplay == null) {
      Log.d("SCREENRECORDER_LOG", "Virtual display is null. Screen sharing already stopped");
    } else {

      this.destroyMediaProjection();

    }
  }

  static {
    ORIENTATIONS.append(0, 0);
    ORIENTATIONS.append(1, 90);
    ORIENTATIONS.append(2, 180);
    ORIENTATIONS.append(3, 270);
  }

  @RequiresApi(
    api = 21
  )
  private class MediaProjectionCallback extends android.media.projection.MediaProjection.Callback {
    private MediaProjectionCallback() {
    }

    public void onStop() {
      Log.v("SCREENRECORDER_LOG", "Recording Stopped");
      RecorderService.this.stopScreenSharing();
    }
  }

  public class LocalBinder extends Binder {
    public LocalBinder() {
    }

    public RecorderService getService() {
      return RecorderService.this;
    }
  }





}
