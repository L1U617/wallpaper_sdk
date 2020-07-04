package cs.hku.wallpaper_sdk.service;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

import cs.hku.wallpaper_sdk.R;
import cs.hku.wallpaper_sdk.constant.Var;
import cs.hku.wallpaper_sdk.util.HttpURLConnectionUtil;

public class WallpaperHandler extends Handler {
    @SuppressLint("ResourceType")
    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case WallpaperConstant.ChangeWallPaper:

//                InputStream stream = new ByteArrayInputStream(msg.obj.toString().getBytes(StandardCharsets.UTF_8));
                System.out.println(msg.obj.toString());
                String url = Var.host + "/" + msg.obj.toString();
                HttpURLConnectionUtil.setWallpaper(url);
                break;
        }
    }
}
