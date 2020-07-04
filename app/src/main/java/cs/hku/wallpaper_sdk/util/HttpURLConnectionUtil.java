package cs.hku.wallpaper_sdk.util;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import cs.hku.wallpaper_sdk.service.WallPaperOrientationService;

public class HttpURLConnectionUtil {

    /**
     * 设置系统壁纸
     * 1、把网络图片设置系统壁纸
     * 2、因为谷歌不维护其他框架了，所以使用HttpURLConnection来下载和配置
     *
     * @param
     * @param imgUrl
     */
    public static void setWallpaper(final String imgUrl) {
        //Log.e("壁纸", "链接：" + imgUrl);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //创建一个写入ID卡的文件对象
                    FileOutputStream out = null;
                    File download = null;
                    File parent = Environment.getExternalStorageDirectory();//获取ID卡目录
                    download = new File(parent, imgUrl);//在父类的目录下创建一个以当前下载的系统时间为文件名的文件
                    if (!download.isFile()) {
                        download.delete();
                    }
                    if (! download.exists()) {
                        URL httpUrl = new URL(imgUrl);//获取传入进来的url地址 并捕获解析过程产生的异常
                        //使用是Http访问 所以用HttpURLConnection 同理如果使用的是https 则用HttpsURLConnection
                        HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();//通过httpUrl开启一个HttpURLConnection对象
                        conn.setReadTimeout(5000);//设置显示超市时间为5秒
                        conn.setRequestMethod("GET");//设置访问方式
                        conn.setDoInput(true);//设置可以获取输入流
                        InputStream in = conn.getInputStream();//获取输入流
                        // 写入文件
                        if (download.getParentFile().exists()) {
                            download.getParentFile().mkdirs();
                        }
                        download.createNewFile();
                        out = new FileOutputStream(download);
                        byte[] b = new byte[2 * 1024];
                        int len;
                        //id卡如果存在 则写入
                        while ((len = in.read(b)) != -1) {
                            out.write(b, 0, len);
                        }
                        out.close();
                    }

                    //读取该文件中的内容
                    final Bitmap bitmap = BitmapFactory.decodeFile(download.getAbsolutePath());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //设置图片为壁纸
                            try {
                                WallPaperOrientationService.wallpaperManager.setBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

}
