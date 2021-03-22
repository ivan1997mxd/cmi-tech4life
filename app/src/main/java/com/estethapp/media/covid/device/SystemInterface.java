package com.estethapp.media.covid.device;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.estethapp.media.covid.BlueToothActivity;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

public class SystemInterface {
    private static BLEUartInterface Bluetooth;
    private static final String TAG = SystemInterface.class.getSimpleName();
    private static String bluetoothDevice;

    public static void AcquirePermission(final BlueToothActivity activity, final String permission, final PermissionCallback callback) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean result = AcquirePermissionImpl(activity, permission).get();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.run(result);
                    }
                });
            }
        });

        thread.start();
    }

    public static Future<Boolean> AcquirePermission(BlueToothActivity activity, String permission) {
        return AcquirePermissionImpl(activity, permission);
    }

    public static boolean AssertPermissions(BlueToothActivity activity, String... permissions) {
        for (String perm : permissions) {
            if (!AcquirePermissionImpl(activity, perm).get()) return false;
        }

        return true;
    }

    public static void CallNumber(Context context, String number) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + number));
        context.startActivity(callIntent);
    }

    public static void SendApiData(final Context context, APIData data, Callback<Boolean> callback) {
        class ResultHandler implements Callback<String> {
            private Callback<Boolean> callback;

            public ResultHandler(Callback<Boolean> callback) {
                this.callback = callback;
            }

            @Override
            public void run(String response) {
                SaveData.SetResponse(context, response);
                callback.run(!response.equals(""));
            }
//            public void run(Integer code) {
//                callback.run(code >= 200 && code < 300);
//            }
        }
        APIService.Save(data, new ResultHandler(callback));
        Log.i(TAG, "Data saved");
    }

    public static boolean SetupBluetooth(Context context) {
        if (Bluetooth == null) {
            BLEUartService service = new BLEUartService();

            if (service.initialize(context)) {
                Bluetooth = new BLEUartInterface(service);

            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean InitializeBluetooth(Context context, Callback<String> onDeviceFound, Runnable onConnected, Callback<DeviceValues> onDataReceived, Runnable onDisconnected) {
        if (Bluetooth == null) {
            BLEUartService service = new BLEUartService();

            if (service.initialize(context)) {
                Bluetooth = new BLEUartInterface(service);
            } else {
                return false;
            }
        }
        Bluetooth.OnDeviceFound = onDeviceFound;
        Bluetooth.OnConnect = onConnected;
        Bluetooth.OnDataReceived = onDataReceived;
        Bluetooth.OnDisconnect = onDisconnected;
        bluetoothDevice = SaveData.GetBTDeviceInfo(context);
        return true;
    }

    public static void EnableBluetooth(Fragment fragment) {
        fragment.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0);
    }

    public static void StartBluetoothScan() {
        if (Bluetooth != null) Bluetooth.startScan();
    }

    public static void StopBluetoothScan() {
        if (Bluetooth != null) Bluetooth.stopScan();
    }

    public static boolean CheckBluetooth() {
        if (Bluetooth != null) return Bluetooth.isConnected();
        return false;
    }

    public static boolean ConnectBluetooth(String device) {
        if (Bluetooth != null) return Bluetooth.connect(device);
        return false;
    }

    public static boolean SendDeviceMessage(String message) {
        if (Bluetooth != null) {
            Bluetooth.sendMessage(message);
            return true;
        } else {
            return false;
        }
    }

    public static BluetoothDeviceInfo GetDeviceInfo(String device) {
        return new BluetoothDeviceInfo(device, Bluetooth.Service);
    }

    public static void DisconnectBluetooth() {
        if (Bluetooth != null) Bluetooth.disconnect();
    }

    private static PendingPermission.PermissionFuture AcquirePermissionImpl(BlueToothActivity activity, String permission) {
        if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
            return new PendingPermission(true).getFuture();
        } else {
            int id = permission.hashCode();

            PendingPermission pending = new PendingPermission((id >= 0 ? id : -id) & 0xFFFF);

            activity.addPendingPermission(pending);

            ActivityCompat.requestPermissions(activity, new String[]{permission}, pending.getId());

            return pending.getFuture();
        }
    }



    private static class BLEUartInterface implements BLEUartEventHandler {
        private BLEUartService Service;
        private Callback<String> OnDeviceFound;
        private Runnable OnConnect;
        private Runnable OnDisconnect;
        private Callback<DeviceValues> OnDataReceived;
        private ArrayList<Character> Buffer = new ArrayList<>();

        public BLEUartInterface(BLEUartService service) {
            Service = service;
            Service.addEventHandler(this);
        }

        public void startScan() {
            Service.startScan();
        }

        public void stopScan() {
            Service.stopScan();
        }

        public boolean connect(String device) {
            return Service.connect(device);
        }

        public boolean isConnected() {
            return Service.isConnected();
        }

        public void sendMessage(String message) {
            Service.writeRXCharacteristic(message.getBytes(StandardCharsets.UTF_8));
        }

        public void disconnect() {
            Service.disconnect(true);
        }

        // transparent button
        // add dialog for input
        // change to zero

        @Override
        public void onEventReceived(BLEUartEvent e) {
            switch (e.getAction()) {

                case BLEUartService.GATT_CONNECTED:
                    if (OnConnect != null) {
                        OnConnect.run();
                    }
                    break;
                case BLEUartService.GATT_DISCONNECTED:
                    if (OnDisconnect != null) {
                        OnDisconnect.run();
                        Service.disconnect(true);
                    }
                    break;
                case BLEUartService.GATT_SERVICES_DISCOVERED:
                    Service.enableTXNotification();
                    break;
                case BLEUartService.DATA_AVAILABLE:
                    appendBuffer(e.getData());
                    break;
                case BLEUartService.REMOTE_DEVICE_DISCOVERED:
                    String device = new String(e.getData(), StandardCharsets.UTF_8);
                    if (device.equals(bluetoothDevice)) {
                        Log.i(TAG, "Existing Device Found: " + bluetoothDevice);
                        stopScan();
                        OnDataReceived.run(new DeviceValues(bluetoothDevice));
                    } else {
//                        Log.i(TAG,"New Device Found");
                        if (OnDeviceFound != null) {
                            OnDeviceFound.run(device);
                        }
                    }
                    break;

            }
        }

        private void appendBuffer(byte[] data) {
            Buffer.ensureCapacity(Buffer.size() + data.length);

            for (Character c : new String(data, StandardCharsets.UTF_8).toCharArray()) {
                Buffer.add(c);
            }

            String next = getNextLine();

            if (next != null && !next.isEmpty()) {
                float[] values = parseDeviceData(next);
                Log.i(TAG, "Data Received:" + Arrays.toString(values));
                if (values.length > 0) {
                    OnDataReceived.run(new DeviceValues(values));
                }
            }
        }

        private String getNextLine() {
            int index = Buffer.indexOf('\n');

            if (index < 0) return null;

            List<Character> lineCharsRegion = Buffer.subList(0, index + 1);

            char[] result = new char[lineCharsRegion.size()];

            for (int i = 0; i < lineCharsRegion.size(); i++) {
                result[i] = lineCharsRegion.get(i);
            }

            lineCharsRegion.clear();

            return new String(result).trim();
        }

        private float[] parseDeviceData(String line) {
            String[] parameters = line.split(",");
            float[] result = new float[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                try {
                    result[i] = Float.parseFloat(parameters[i].trim());
                } catch (NumberFormatException ex) {
                    result[i] = Float.NaN;
                }
            }

            return result;
        }
    }
}
