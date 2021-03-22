package com.estethapp.media.helper;

import java.util.HashMap;
import java.util.Map;

public class Const {

    public enum ASPECT_RATIO {
        AR16_9(1.7777778f), AR18_9(2f);

        private static Map<Float, ASPECT_RATIO> map = new HashMap<Float, ASPECT_RATIO>();

        static {
            for (ASPECT_RATIO aspectRatio : ASPECT_RATIO.values()) {
                map.put(aspectRatio.numVal, aspectRatio);
            }
        }

        private float numVal;

        ASPECT_RATIO(float numVal) {
            this.numVal = numVal;
        }

        public static ASPECT_RATIO valueOf(float val) {
            return map.get(val) == null ? AR16_9 : map.get(val);
        }
    }

    public static final String TAG = "SCREENRECORDER_LOG";


    public static final int SCREEN_RECORD_REQUEST_CODE = 1003;
    public static final String SCREEN_RECORDING_START = "com.estethapp.media.services.action.startrecording";
    public static final String SCREEN_RECORDING_PAUSE = "com.estethapp.media.services.action.pauserecording";
    public static final String SCREEN_RECORDING_RESUME = "com.estethapp.media.services.action.resumerecording";
    public static final String SCREEN_RECORDING_STOP = "com.estethapp.media.services.action.stoprecording";
    public static final String SCREEN_RECORDING_DESTORY_SHAKE_GESTURE = "com.estethapp.media.services.action.destoryshakegesture";
    public static final int SCREEN_RECORDER_NOTIFICATION_ID = 5001;
    public static final String RECORDER_INTENT_DATA = "recorder_intent_data";
    public static final String RECORDER_INTENT_RESULT = "recorder_intent_result";
    public static final String RECORDING_NOTIFICATION_CHANNEL_ID = "recording_notification_channel_id1";
    public static final String SHARE_NOTIFICATION_CHANNEL_ID = "share_notification_channel_id1";
    public static final String RECORDING_NOTIFICATION_CHANNEL_NAME = "Shown Persistent notification when recording screen or when waiting for shake gesture";
    public static final String SHARE_NOTIFICATION_CHANNEL_NAME = "Show Notification to share or edit the recorded video";
    public static final String VIDEO_FILE_NAME = "fileName";
    //added by kk 5/5/2020

    public enum RecordingState {
        RECORDING, PAUSED, STOPPED
    }

    public enum analytics {
        CRASHREPORTING, USAGESTATS
    }

}
