package cs.hku.wallpaper_sdk.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.io.IOException;

import cs.hku.wallpaper_sdk.R;
import cs.hku.wallpaper_sdk.model.Direction;


public class WallPaperOrientationService {
    private static int current = -1;
    private static final String TAG = "WallPaper";
    private static int CurrentWallPaper = -1;
    private static SensorManager sm;
    //需要两个Sensor
    private static Sensor aSensor;
    private static Sensor mSensor;
    private static float currentDirection = 0;
    private static float[] accelerometerValues = new float[3];
    private static float[] magneticFieldValues = new float[3];
    private static WallpaperManager wallpaperManager;

    final static SensorEventListener myListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticFieldValues = sensorEvent.values.clone();
//                Log.i(TAG, "onSensorChanged: "+Arrays.toString(sensorEvent.values));
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                accelerometerValues = sensorEvent.values.clone();
            try {
                calculateOrientation();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    public static void StartOrientationListener(Activity activity) {
        wallpaperManager = WallpaperManager.getInstance(activity);
        sm = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        assert sm != null;
        aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sm.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(myListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private static void calculateOrientation() throws IOException {
        float[] values = new float[3];
        float[] Res = new float[9];
        SensorManager.getRotationMatrix(Res, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(Res, values);
//        Log.i(TAG, "calculateOrientation: 旋转矩阵" + Res);
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                System.out.print(Res[i*3 + j] + ", ");
//            }
//            System.out.println();
//        }
        Direction direction = change(Res);
        Log.i(TAG, "calculateOrientation: "+direction);
        changeWallPaper(direction);
    }


    /**
     * 新的坐标系---android官方规定的世界坐标系
     * y轴正方向 --> 正北方
     * x轴正方向 --> 正东方
     * z轴正方向 --> 由地面指向天空
     * @param direction
     */
    @SuppressLint("ResourceType")
    private static void changeWallPaper(Direction direction) {
        // 北方
        if (direction.getY() >0.9 && -0.1 < direction.getX() && direction.getX() < 0.1 && -0.1 < direction.getZ() && direction.getZ() < 0.1) {
            if (current == 1) {
                return;
            }
            setWallPaper(R.drawable.wall05);
            current = 1;
            return;
        }

        // 南方
        if (direction.getY() < 0.9 && -0.1 < direction.getX() && direction.getX() < 0.1 && -0.1 < direction.getZ() && direction.getZ() < 0.1){
            if (current == 2) {
                return;
            }
            setWallPaper(R.drawable.wall04);
            current = 2;
            return;
        }

        //西方
        if (direction.getX() < -0.9 && -0.1 < direction.getY() && direction.getY() < 0.1 && -0.1 < direction.getZ() && direction.getZ() < 0.1){
            if (current == 3) {
                return;
            }
            setWallPaper(R.drawable.wall02);
            current = 3;
            return;
        }

        // 东方
        if (direction.getX() > 0.9 && -0.1 < direction.getY() && direction.getY() < 0.1 && -0.1 < direction.getZ() && direction.getZ() < 0.1){
            if (current == 4) {
                return;
            }
            setWallPaper(R.drawable.wall03);
            current = 4;
            return;
        }
    }
    private static void setWallPaper(int Resource){
        if (Resource == CurrentWallPaper) return;
        CurrentWallPaper = Resource;
        try {
            wallpaperManager.setResource(Resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Direction change(float[] R) {
        float x = 0;
        float y = 0;
        float z = -1;
        return new Direction(R[0]*x + R[1]*y + R[2]*z, R[3]*x + R[4]*y + R[5]*z, R[6]*x + R[7]*y + R[8]*z);
    }
}
