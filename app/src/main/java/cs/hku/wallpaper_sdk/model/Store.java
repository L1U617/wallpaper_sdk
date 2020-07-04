package cs.hku.wallpaper_sdk.model;


import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Store {
    public static Map<String, String> DirectionMatrix = new HashMap<>();
    static {
        for (double i = 0; i < Math.PI; i += 0.3) {
            for (double j= 0;  j < Math.PI; j +=0.3) {
                for (double k= 0;  k < Math.PI; k +=0.3) {
                    double x = new BigDecimal(Math.cos(i)).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
                    double y = new BigDecimal(Math.cos(j)).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
                    double z = new BigDecimal(Math.cos(k)).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
                    DirectionMatrix.put(new Direction(x, y ,z).toString(), "");
                }
            }
        }
        File file = new File(Environment.getExternalStorageDirectory(), "source.txt");
        if (! file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        InputStreamReader read = null;// 考虑到编码格式
        try {
            read = new InputStreamReader(
                    new FileInputStream(file), "GBK");
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while ((lineTxt = bufferedReader.readLine()) != null)
            {
                if (lineTxt.length() == 0) continue;
                String[] s = lineTxt.split("-->");
                DirectionMatrix.put(s[0], s[1]);
            }
            bufferedReader.close();
            read.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void storeDirectionWithMatrix(Direction d, float[] R) throws IOException {
        File file = new File(Environment.getExternalStorageDirectory(), "source.txt");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file, true)));
        Log.i("BAGA", "storeDirectionWithMatrix: "+DirectionMatrix.keySet());
        Log.i("BAGA", "storeDirectionWithMatrix: "+d.toString());
        if (!DirectionMatrix.containsKey(d.toString())) {
            DirectionMatrix.put(d.toString(), Arrays.toString(R));
        }
        Log.i("BAGA", "storeDirectionWithMatrixWWWWWWWWWW: "+d.toString()+"-->"+Arrays.toString(R)+"\n");
        out.write(d.toString()+"-->"+Arrays.toString(R)+"\n");
        out.close();
    }

    public static String toMatrix(float [][]R){
        String s = "[";
        s += Arrays.toString(R[0]) + ",";
        s += Arrays.toString(R[1]) + ",";
        s += Arrays.toString(R[2]) + "]";
        return s;
    }

}
