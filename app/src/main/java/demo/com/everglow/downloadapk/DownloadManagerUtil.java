package demo.com.everglow.downloadapk;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;

/**
 * Created by EverGlow on 2019/1/15 11:46
 */

public class DownloadManagerUtil {
    private Context mContext;
    private BroadcastReceiver mReceiver;

    private DownloadManagerUtil(Context context) {
        mContext = context;
    }

    private static DownloadManagerUtil instance;

    public static DownloadManagerUtil getInstance(Context context) {
        if (instance == null) {
            instance = new DownloadManagerUtil(context);
        }
        return instance;
    }

    /**
     * @param apkPath       apk存储路径
     * @param downloadUrl   下载url
     * @param latestVersion 最新版本号
     */
    public void downloadApk(String apkPath, String downloadUrl, int latestVersion) {
        final DownloadManager dManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        File apkFile = new File(apkPath);
        if (apkFile.exists()) {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                int versionCode = packageInfo.versionCode;
                //已下载最新安装包直接跳转安装界面
                if (versionCode == latestVersion) {
                    startInstall(apkPath);
                    return;
                }
            }
            apkFile.delete();
        }
        Uri uri = Uri.parse(downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationUri(Uri.fromFile(new File(apkPath)));
        request.setDescription(mContext.getString(R.string.app_name) + "版本更新");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
        // 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        // 设置为可见和可管理
        request.setVisibleInDownloadsUi(true);
        // 获取此次下载的ID
        final long refernece = dManager.enqueue(request);

        // 注册广播接收器，当下载完成时自动安装
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                long myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (refernece == myDwonloadID) {
                    startInstall(apkPath);
                }
            }
        };
        mContext.registerReceiver(mReceiver, filter);
    }


    private void startInstall(String apkPath) {
        Intent localIntent = new Intent(Intent.ACTION_VIEW);
        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        /**
         * Android7.0+禁止应用对外暴露file://uri，改为content://uri；具体参考FileProvider
         */
        if (Build.VERSION.SDK_INT >= 24) {

            uri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID+".fileprovider",
                    new File(apkPath));
            localIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(apkPath));
        }
        Log.d("BroadcastReceiver", "uri =" + uri);
        localIntent.setDataAndType(uri, "application/vnd.android.package-archive"); //打开apk文件
        try {
            mContext.startActivity(localIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
