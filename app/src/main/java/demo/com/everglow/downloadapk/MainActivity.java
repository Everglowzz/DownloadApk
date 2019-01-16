package demo.com.everglow.downloadapk;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;

import java.io.File;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnPermissionCallback {

    private PermissionHelper permissionHelper;
    private boolean isSingle;
    private AlertDialog builder;
    private String[] neededPermission;
    private UpdateDialog mUpdateDialog;
    private static final String PERMISSION = "permissionHelper";
    private final static String SINGLE_PERMISSION = Manifest.permission.READ_PHONE_STATE;
    private String mDownloadUrl = "https://apps.cgsoft.net/xiaomishu.3.6.0.apk";

    String apkPath;

    private final static String[] MULTI_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initUpDate(mDownloadUrl, "版本更新测试", 119);
            }
        });
        requestPermissions();
    }

    private void requestPermissions() {
        permissionHelper = PermissionHelper.getInstance(this);
        permissionHelper
                .setForceAccepting(false) // default is false. its here so you know that it exists.
                .request(isSingle ? SINGLE_PERMISSION : MULTI_PERMISSIONS);
    }


    /**
     * @param downloadUrl   下载url
     * @param desc          更新描述
     * @param latestVersion 最新版本号
     */
    private void initUpDate(final String downloadUrl, String desc, final int latestVersion) {
        apkPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + "Everglowzz" + File.separator + "测试应用.apk";
        mUpdateDialog = new UpdateDialog(this);
        mUpdateDialog.setUpdateDialogCallBack(() -> DownloadManagerUtil.getInstance(this).downloadApk(apkPath, downloadUrl, latestVersion));
        mUpdateDialog.showDialog(desc);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        permissionHelper.onActivityForResult(requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionGranted(@NonNull String[] permissionName) {
        Log.i(PERMISSION, "Permission(s) " + Arrays.toString(permissionName) + " Granted");
    }

    @Override
    public void onPermissionDeclined(@NonNull String[] permissionName) {
        Log.i(PERMISSION, "Permission(s) " + Arrays.toString(permissionName) + " Declined");
        //权限拒绝 退出
        finish();
    }

    @Override
    public void onPermissionPreGranted(@NonNull String permissionsName) {
        Log.i(PERMISSION, "Permission( " + permissionsName + " ) preGranted");
    }

    @Override
    public void onPermissionNeedExplanation(@NonNull String permissionName) {
        Log.i(PERMISSION, "Permission( " + permissionName + " ) needs Explanation");
        if (!isSingle) {
            neededPermission = PermissionHelper.declinedPermissions(this, MULTI_PERMISSIONS);
            StringBuilder builder = new StringBuilder(neededPermission.length);
            if (neededPermission.length > 0) {
                for (String permission : neededPermission) {
                    builder.append(permission).append("\n");
                }
            }
            AlertDialog alert = getAlertDialog(neededPermission, builder.toString());
            if (!alert.isShowing()) {
                alert.show();
            }
        } else {
            getAlertDialog(permissionName).show();
        }
    }

    @Override
    public void onPermissionReallyDeclined(@NonNull String permissionName) {
        Log.i(PERMISSION, "Permission " + permissionName + " can only be granted from settingsScreen");
        /** you can call  {@link PermissionHelper#openSettingsScreen(Context)} to open the settings screen */

    }

    @Override
    public void onNoPermissionNeeded() {
        Log.i("onNoPermissionNeeded", "Permission(s) not needed");
    }

    public AlertDialog getAlertDialog(final String[] permissions, final String permissionName) {
        if (builder == null) {
            builder = new AlertDialog.Builder(this)
                    .setTitle("Permission Needs Explanation")
                    .create();
        }
        builder.setButton(DialogInterface.BUTTON_POSITIVE, "Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionHelper.requestAfterExplanation(permissions);
            }
        });
        builder.setMessage("Permissions need explanation (" + permissionName + ")");
        return builder;
    }

    public AlertDialog getAlertDialog(final String permission) {
        if (builder == null) {
            builder = new AlertDialog.Builder(this)
                    .setTitle("Permission Needs Explanation")
                    .create();
        }
        builder.setButton(DialogInterface.BUTTON_POSITIVE, "Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionHelper.requestAfterExplanation(permission);
            }
        });
        builder.setMessage("Permission need explanation (" + permission + ")");
        return builder;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();
        }
        return false;
    }

    private static Boolean isExit = false;

    private void exitBy2Click() {

        if (!isExit) {
            isExit = true;
            Toast.makeText(this, "再按一次返回键退出应用！！", Toast.LENGTH_SHORT).show();
            Timer tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            finish();
        }

    }
}
