package app.timeserver.helper.permissions;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

public class PermissionsHelper {

    public static boolean permissionIsGranted(Activity activity, Permission permission) {
        return ContextCompat.checkSelfPermission(activity, permission.getAndroidPermissionName()) == PackageManager.PERMISSION_GRANTED;
    }

    public static Permission requestPermission(Activity activity, Permission permission) {
        if (ContextCompat.checkSelfPermission(activity, permission.getAndroidPermissionName()) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{permission.getAndroidPermissionName()}, permission.getKey());
        } else {
            permission.setPermissionStatus(Permission.PermissionStatus.GRANTED);
        }
        return permission;
    }

    /**
     * Use this in Fragment.onRequestPermissionsResult callback
     */
    public static Permission handlePermissionResult(int requestCode,
                                             @NonNull String permissions[],
                                             @NonNull int[] grantResults) {
        boolean granted = grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        Permission permission = Permission.fromKey(requestCode);
        permission.setPermissionStatus(granted ? Permission.PermissionStatus.GRANTED : Permission.PermissionStatus.DENIED);
        return permission;
    }
}

