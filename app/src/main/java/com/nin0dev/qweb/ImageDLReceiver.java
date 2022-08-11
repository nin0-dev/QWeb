package com.nin0dev.qweb;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class ImageDLReceiver {
    Context mContext;

    ImageDLReceiver(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void showDLButton() {
        MainActivity a = (MainActivity) mContext;
        a.showDLButton();
    }

    @JavascriptInterface
    public void hideDLButton() {
        MainActivity a = (MainActivity) mContext;
        a.hideDLButton();
    }

    @JavascriptInterface
    public void showDLButton2() {
        SeparateActivity a = (SeparateActivity) mContext;
        a.showDLButton();
    }

    @JavascriptInterface
    public void hideDLButton2() {
        SeparateActivity a = (SeparateActivity) mContext;
        a.hideDLButton();
    }

    @JavascriptInterface
    public void downloadImage(String url) {
        MainActivity a = (MainActivity) mContext;
        if(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((MainActivity) mContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        else{
            DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setVisibleInDownloadsUi(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            Random random = new Random();
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "QWeb-" + random.nextInt(9999) + ".jpg");
            request.setTitle("QWeb image download");
            request.setMimeType("image/jpeg");
            downloadManager.enqueue(request);
            Toast.makeText((MainActivity) mContext, "Download started, check notifications panel.", Toast.LENGTH_LONG).show();
        }
    }

    @JavascriptInterface
    public void downloadImage2(String url) {
        SeparateActivity a = (SeparateActivity) mContext;
        if(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((SeparateActivity) mContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        else{
            DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setVisibleInDownloadsUi(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            Random random = new Random();
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "QWeb-" + random.nextInt(9999) + ".jpg");
            request.setTitle("QWeb image download");
            request.setMimeType("image/jpeg");
            downloadManager.enqueue(request);
            Toast.makeText((SeparateActivity) mContext, "Download started, check notifications panel.", Toast.LENGTH_LONG).show();
        }
    }
}