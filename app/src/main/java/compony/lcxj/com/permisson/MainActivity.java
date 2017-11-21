package compony.lcxj.com.permisson;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static int PERMISSION_REQUESTCODE = 1111;
    /**
     * 判断是否需要坚持，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedCheck){
            checkPermissions(needPermissions);
        }
    }

    private void checkPermissions(String... permissions){
        List<String> needRequestPermissionsList = findDeniedPermissions(permissions);
        if (null != needRequestPermissionsList && needRequestPermissionsList.size() > 0){
            ActivityCompat.requestPermissions(this, needRequestPermissionsList.toArray(
                    new String[needRequestPermissionsList.size()]
            ), PERMISSION_REQUESTCODE);
        }
    }

    private List<String> findDeniedPermissions(String[] permissions){
        List<String> needRequestPermissionsList = new ArrayList<String>();
        for (String perm : permissions){
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, perm)){
                needRequestPermissionsList.add(perm);
            }
        }
        return needRequestPermissionsList;
    }

    /**
     * 检测是否所有的权限都已经授权
     * @param grantResults
     * @return
     */
    private boolean verifyPermissions(int[] grantResults){
        for (int result : grantResults){
            if (result != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUESTCODE){
            //没有授权
            if (!verifyPermissions(grantResults)){
                showMissingPermissionDialog();
                isNeedCheck = false;
            }
        }
    }

    /**
     * 显示提示信息
     */
    private void showMissingPermissionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("我是权限");
        builder.setMessage("我正在申请权限");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 启动应用的设置
     */
    private void startAppSettings(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("packeage:" + getPackageName()));
        startActivity(intent);
    }
}
