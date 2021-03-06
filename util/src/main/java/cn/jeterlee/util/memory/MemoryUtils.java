package cn.jeterlee.util.memory;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;

import cn.jeterlee.util.content.Ctx;
import cn.jeterlee.util.service.ServiceUtils;

/**
 * 内存信息
 * Created by Jixiang_Li on 2017/2/5.
 */
public class MemoryUtils {

    private MemoryUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private static final int ERROR = -1;
    private static final int AVALIABLE_EXTERNAL_MEMORY_SIZE = 50 * 1024 * 1024; // 50MB

    /**
     * Judge whether external momory is available
     *
     * @return
     */
    public static boolean isExternalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * Judge whether external memory is full
     *
     * @return
     */
    public static boolean isExternalMemoryFull() {
        return getAvailableExternalMemorySize() - AVALIABLE_EXTERNAL_MEMORY_SIZE < 0;
    }

    /**
     * Get available internal memory size
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * Get internal memory size
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * Get available external memory size
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static long getAvailableExternalMemorySize() {
        if (isExternalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return ERROR;
        }
    }

    /**
     * Get external memory size
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static long getTotalExternalMemorySize() {
        if (isExternalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return ERROR;
        }
    }


    /**
     * Get total memory
     *
     * @return
     */
    public static String getTotalMemory() {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Logger.i("%1$s, %2$s%n", str2, num);
            }
            initial_memory = Integer.valueOf(arrayOfString[1]) * 1024;
            localBufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        // Byte转换为KB或者MB,内存大小规格化
        return Formatter.formatFileSize(Ctx.getApplicationContext(), initial_memory);
    }

    /**
     * Get available memory
     *
     * @return
     */
    public static String getAvailableMemory() {
        ActivityManager am = ServiceUtils.getActivityManager();
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return Formatter.formatFileSize(Ctx.getApplicationContext(), mi.availMem);
    }

    /**
     * Get all memory
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static String getAllMemory() {
        ActivityManager am = ServiceUtils.getActivityManager();
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return Formatter.formatFileSize(Ctx.getApplicationContext(), mi.totalMem);
    }

    /**
     * Get format size
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }
}
