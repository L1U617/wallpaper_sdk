package cs.hku.wallpaper_sdk.service;

import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import cs.hku.wallpaper_sdk.constant.Var;
import cs.hku.wallpaper_sdk.model.AngelPicReq;
import cs.hku.wallpaper_sdk.model.AngelPicResp;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class Image {
    private static final WallpaperHandler handler = new WallpaperHandler();
    private static Gson gson = new Gson();
    static class FetchWallPaperThread extends Thread{
        private double[] direction;
        FetchWallPaperThread(double[] direction) {
            this.direction = direction;
        }

        @Override
        public void run() {
            String model = "pointarray";
            String url = Var.host + "/fetch";
            RequestBody body = RequestBody.create(gson.toJson(new AngelPicReq(model, direction)), JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                Message message = Message.obtain();
                message.what = WallpaperConstant.ChangeWallPaper;
                AngelPicResp resp = gson.fromJson(Objects.requireNonNull(response.body()).string(), AngelPicResp.class);
                message.obj = resp.getData();
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static OkHttpClient client = new OkHttpClient.Builder().build();



    private static float[][] generateMatrix(float[] res) {
        float[][] matrix = new float[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(res, i * 3, matrix[i], 0, 3);
        }
        return matrix;
    }

    static void fetchImage(double[] direction) {
        new FetchWallPaperThread(direction).start();
    }
}
