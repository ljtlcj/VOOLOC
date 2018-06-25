package test.xk_ys_VOOLOC.AboutFile;

import android.bluetooth.BluetoothAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 柯东煜 on 2017/10/14.
 */

public class LockInfo implements Parcelable {
    private String lockKey;//密钥
    private String bluetoothAddress;//蓝牙地址
    private String power;//权限
    private String lockName;//门锁名称
    private String startTime;//起效时间
    private String endTime;//失效时间
    private String bluetoothName;//蓝牙名称
    private String address="";
    private String lockId;
    private String lockId_2;

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLockKey() {
        return lockKey;
    }

    public void setLockKey(String lockKey) throws NumberFormatException {
        String pattern="[0-9]{8}";
        Log.e("lockKey",lockKey);
        Pattern p=Pattern.compile(pattern);
        Matcher matcher=p.matcher(lockKey);
        if(matcher.find()){
            this.lockKey = lockKey;
        }
        else{
            throw new NumberFormatException() ;
        }
    }

    public String getBluetoothAddress() {
        return bluetoothAddress;
    }

    public void setBluetoothAddress(String bluetoothAddress) {
        if(BluetoothAdapter.getDefaultAdapter().checkBluetoothAddress(bluetoothAddress))
        {
            this.bluetoothAddress = bluetoothAddress;
        }
        else{
            throw new IllegalArgumentException();
        }
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        if(power.equals("1")||power.equals("2")||power.equals("3"))
        {
            this.power = power;
        }
        else{
            throw new NumberFormatException();
        }
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getBluetoothName() {
        return bluetoothName;
    }


    public String getLockId_2() {
        return lockId_2;
    }

    public void setLockId_2(String lockId_2) {
        this.lockId_2 = lockId_2;
    }

    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName = bluetoothName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.lockKey);
        dest.writeString(this.bluetoothAddress);
        dest.writeString(this.power);
        dest.writeString(this.lockName);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeString(this.bluetoothName);
        dest.writeString(this.lockId);
        dest.writeString(this.lockId_2);
    }

    public LockInfo() {
    }

    protected LockInfo(Parcel in) {
        this.lockKey = in.readString();
        this.bluetoothAddress = in.readString();
        this.power = in.readString();
        this.lockName = in.readString();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.bluetoothName = in.readString();
        this.lockId=in.readString();
        this.lockId_2=in.readString();
    }

    public static final Creator<LockInfo> CREATOR = new Creator<LockInfo>() {
        public LockInfo createFromParcel(Parcel source) {
            return new LockInfo(source);
        }

        public LockInfo[] newArray(int size) {
            return new LockInfo[size];
        }
    };
}