package cs.hku.wallpaper_sdk.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

import cs.hku.wallpaper_sdk.R;
import cs.hku.wallpaper_sdk.model.Direction;
import cs.hku.wallpaper_sdk.model.Store;

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
    public static WallpaperManager wallpaperManager;
    private static Direction oldDirection = new Direction(0, 1, 0);


    final static SensorEventListener myListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticFieldValues = sensorEvent.values.clone();
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

    private static double calCosine(Direction d1, Direction d2){
        double d1_dot_d2 = d1.getX() * d2.getX() + d1.getY() * d2.getY() + d1.getZ() * d2.getZ();
        double len_d1 = Math.sqrt(Math.pow(d1.getX(), 2) + Math.pow(d1.getY(), 2) + Math.pow(d1.getZ(), 2));
        double len_d2 = Math.sqrt(Math.pow(d2.getX(), 2) + Math.pow(d2.getY(), 2) + Math.pow(d2.getZ(), 2));
        return d1_dot_d2 / (len_d1 * len_d2);
    }
    private static void calculateOrientation() throws IOException {
        float[] values = new float[3];
        float[] Res = new float[9];
        SensorManager.getRotationMatrix(Res, null, accelerometerValues, magneticFieldValues);
        System.out.println(Arrays.toString(Res));
        SensorManager.getOrientation(Res, values);
        // 得到当前相对于世界坐标系的空间向量
        Direction direction = change(Res);

        // 非测试情况下这行代码注释掉
//        Store.storeDirectionWithMatrix(direction, Res);

        double cosine = calCosine(oldDirection, direction);
        if (cosine < 0.5) {
            double[] d = {direction.getX(), direction.getY(), direction.getZ()};
            Image.fetchImage(d);
            oldDirection = direction;
        }
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

        return new Direction(new BigDecimal(R[0]*x + R[1]*y + R[2]*z).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue(),
                new BigDecimal(R[3]*x + R[4]*y + R[5]*z).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue()
                , new BigDecimal(R[6]*x + R[7]*y + R[8]*z).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue());
    }
}
