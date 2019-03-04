package app.timeserver.helper.permissions;

import android.Manifest;

public enum Permission {

    CAMERA(0, Manifest.permission.CAMERA),
    READ_STORAGE(1, Manifest.permission.READ_EXTERNAL_STORAGE),
    FINE_LOCATION(2,Manifest.permission.ACCESS_FINE_LOCATION);

    private int mKey;
    private String mAndroidPermissionName;
    private PermissionStatus mPermissionStatus;

    Permission(int key, String androidPermissionName) {
        this.mKey = key;
        this.mAndroidPermissionName = androidPermissionName;
        mPermissionStatus = PermissionStatus.WAITING_FOR_GRANT;
    }

    public static Permission fromKey(int key) {
        for (Permission p : Permission.class.getEnumConstants()) {
            if (p.mKey == key) {
                return p;
            }
        }
        throw new RuntimeException("No Android permission found for key: " + key);
    }

    public int getKey() {
        return mKey;
    }

    public boolean isGranted() {
        return this.getPermissionStatus() == PermissionStatus.GRANTED;
    }

    public PermissionStatus getPermissionStatus() {
        return mPermissionStatus;
    }

    public void setPermissionStatus(PermissionStatus status) {
        this.mPermissionStatus = status;
    }

    public String getAndroidPermissionName() {
        return mAndroidPermissionName;
    }

    public enum PermissionStatus {
        WAITING_FOR_GRANT,
        GRANTED,
        DENIED
    }
}
