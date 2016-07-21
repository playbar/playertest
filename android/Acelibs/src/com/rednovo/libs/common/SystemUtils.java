package com.rednovo.libs.common;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.rednovo.libs.BaseApplication;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * 获取手机系统信息类
 * 
 * @author liyusheng
 * 
 */
public class SystemUtils {
	private static final String LOG_TAG = "SystemUtils";

	/** 获取android系统版本号 */
	public static String getOSVersion() {
		String release = android.os.Build.VERSION.RELEASE; // android系统版本号
		release = "android" + release;
		return release;
	}

	/** 获得android系统sdk版本号 */
	public static String getOSVersionSDK() {
		return android.os.Build.VERSION.SDK;
	}

	/** 获得android系统sdk版本号 */
	public static int getOSVersionSDKINT() {
		return android.os.Build.VERSION.SDK_INT;
	}

	/** 获取手机型号 */
	public static String getDeviceModel() {
		try {
			return android.os.Build.MODEL;
		} catch (Exception ex) {
			return "";
		}
	}

	/**
	 * 品牌
	 * 
	 * @return
	 */
	public static String getBrand() {
		try {
			return android.os.Build.BRAND;
		} catch (Exception ex) {
			return "";
		}
	}

	/**
	 * 获取 Channel
	 * 
	 * @param
	 * @return
	 */
	public static String getChannel() {

		try {
			Context mContext = BaseApplication.getApplication().getApplicationContext();
			ApplicationInfo ai = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);

			Bundle bundle = ai.metaData;
			if (bundle != null) {
				return bundle.getString("UMENG_CHANNEL");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取int型渠道
	 * @return
	 */
	public static int getIntegerChannel(){
		try {
			Context mContext = BaseApplication.getApplication().getApplicationContext();
			ApplicationInfo ai = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);

			Bundle bundle = ai.metaData;
			if (bundle != null) {
				return bundle.getInt("UMENG_CHANNEL");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/** 获取设备的IMEI */
	public static String getIMEI() {
		Context mContext = BaseApplication.getApplication().getApplicationContext();
		if (null == mContext) {
			return "";
		}
		String imei = "";
		try {
			TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			imei = tm.getDeviceId();
		} catch (Exception e) {
			return "";
		}
		return imei;
	}

	/** 检测手机是否已插入SIM卡 */
	public static boolean isCheckSimCardAvailable() {
		Context mContext = BaseApplication.getApplication().getApplicationContext();
		if (null == mContext) {
			return false;
		}
		final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimState() == TelephonyManager.SIM_STATE_READY;
	}

	/** sim卡是否可读 */
	public static boolean isCanUseSim() {
		Context mContext = BaseApplication.getApplication().getApplicationContext();
		if (null == mContext) {
			return false;
		}
		try {
			TelephonyManager mgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			return TelephonyManager.SIM_STATE_READY == mgr.getSimState();
		} catch (Exception e) {
			return false;
		}
	}

	/** 取得当前sim手机卡的imsi */
	public static String getIMSI() {
		Context mContext = BaseApplication.getApplication().getApplicationContext();
		if (null == mContext) {
			return null;
		}
		String imsi = null;
		try {
			TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			imsi = tm.getSubscriberId();
		} catch (Exception e) {
			return "";
		}
		return imsi;
	}

	/** 返回本地手机号码，这个号码不一定能获取到 */
	public static String getNativePhoneNumber() {
		Context mContext = BaseApplication.getApplication().getApplicationContext();
		if (null == mContext) {
			return "";
		}
		TelephonyManager telephonyManager;
		telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		String NativePhoneNumber = null;
		NativePhoneNumber = telephonyManager.getLine1Number();
		return NativePhoneNumber;
	}

	/** 返回手机服务商名字 */
	public static String getProvidersName() {
		String ProvidersName = null;
		// 返回唯一的用户ID;就是这张卡的编号神马的
		String IMSI = getIMSI();
		// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
			ProvidersName = "中国移动";
		} else if (IMSI.startsWith("46001")) {
			ProvidersName = "中国联通";
		} else if (IMSI.startsWith("46003")) {
			ProvidersName = "中国电信";
		} else {
			ProvidersName = "其他服务商:" + IMSI;
		}
		return ProvidersName;
	}

	/** 获取当前设备的SN */
	public static String getSimSN() {
		Context mContext = BaseApplication.getApplication().getApplicationContext();
		if (null == mContext) {
			return "";
		}
		String simSN = "";
		try {
			TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			simSN = tm.getSimSerialNumber();
		} catch (Exception e) {
			return "";
		}
		return simSN;
	}

	/**
	 * 宽度
	 * 
	 * @param
	 * @return
	 */
	public static String getWidth() {
		Context mContext = BaseApplication.getApplication().getApplicationContext();
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getWidth() + "";
		// WindowManager wm = (WindowManager)activity.getWindowManager();
		//
		// int width = wm.getDefaultDisplay().getWidth();
		// int height = wm.getDefaultDisplay().getHeight();
	}

	/**
	 * 高度
	 * 
	 * @param
	 * @return
	 */
	public static String getHeight() {
		Context mContext = BaseApplication.getApplication().getApplicationContext();
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getHeight() + "";
	}

	/** 获取当前设备的MAC地址 */
	public static String getMacAddress() {
		Context mContext = BaseApplication.getApplication().getApplicationContext();
		if (null == mContext) {
			return "";
		}
		String mac = "";
		try {
			WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wm.getConnectionInfo();
			mac = info.getMacAddress();
		} catch (Exception e) {
			return "";
		}
		return mac;
	}

	/** 获得设备ip地址 */
	public static String getLocalAddress() {
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				NetworkInterface intf = en.nextElement();
				Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
				while (enumIpAddr.hasMoreElements()) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			return "";
		}
		return "";
	}

	public static String getLocalHostIp() {
		String ipaddress = "";
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			// 遍历所用的网络接口
			while (en.hasMoreElements()) {
				NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
				Enumeration<InetAddress> inet = nif.getInetAddresses();
				// 遍历每一个接口绑定的所有ip
				while (inet.hasMoreElements()) {
					InetAddress ip = inet.nextElement();
					if (!ip.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ip.getHostAddress())) {
						return ip.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			Log.e("feige", "获取本地ip地址失败");
			e.printStackTrace();
		}
		return ipaddress;

	}


	/** 获取屏幕的分辨率 */
	@SuppressWarnings("deprecation")
	public static int[] getResolution() {
		Context mContext = BaseApplication.getApplication().getApplicationContext();
		if (null == mContext) {
			return null;
		}
		WindowManager windowMgr = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		int[] res = new int[2];
		res[0] = windowMgr.getDefaultDisplay().getWidth();
		res[1] = windowMgr.getDefaultDisplay().getHeight();
		return res;
	}

	/** 获得设备的横向dpi */
	public static float getWidthDpi() {
		Context mContext = BaseApplication.getApplication().getApplicationContext();
		if (null == mContext) {
			return 0;
		}
		DisplayMetrics dm = null;
		try {
			if (mContext != null) {
				dm = new DisplayMetrics();
				dm = mContext.getApplicationContext().getResources().getDisplayMetrics();
			}

			return dm.densityDpi;
		} catch (Exception e) {
			return 0;
		}
	}

	/** 获得设备的纵向dpi */
	public static float getHeightDpi() {
		Context mContext = BaseApplication.getApplication().getApplicationContext();
		if (null == mContext) {
			return 0;
		}
		DisplayMetrics dm = new DisplayMetrics();
		dm = mContext.getApplicationContext().getResources().getDisplayMetrics();
		return dm.ydpi;
	}

	// 获取CPU最大频率（单位KHZ）

	// "/system/bin/cat" 命令行

	// "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" 存储最大频率的文件的路径

	public static String getMaxCpuFreq() {
		String result = "";
		ProcessBuilder cmd;
		try {
			String[] args = { "/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" };
			cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[24];
			while (in.read(re) != -1) {
				result = result + new String(re);
			}
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			result = "N/A";
		}
		return result.trim();
	}

	// 获取CPU最小频率（单位KHZ）
	public static String getMinCpuFreq() {
		String result = "";
		ProcessBuilder cmd;
		try {
			String[] args = { "/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq" };
			cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[24];
			while (in.read(re) != -1) {
				result = result + new String(re);
			}
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			result = "N/A";
		}
		return result.trim();
	}

	// 实时获取CPU当前频率（单位KHZ）
	public static String getCurCpuFreq() {
		String result = "N/A";
		try {
			FileReader fr = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
			BufferedReader br = new BufferedReader(fr);
			String text = br.readLine();
			result = text.trim();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	// 获取CPU名字
	public static String getCpuName() {
		try {
			FileReader fr = new FileReader("/proc/cpuinfo");
			BufferedReader br = new BufferedReader(fr);
			String text = br.readLine();
			String[] array = text.split(":\\s+", 2);
			for (int i = 0; i < array.length; i++) {
			}
			return array[1];
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 获取设备信息 */
	public static String[] getDivceInfo() {
		String str1 = "/proc/cpuinfo";
		String str2 = "";
		String[] cpuInfo = { "", "" };
		String[] arrayOfString;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			for (int i = 2; i < arrayOfString.length; i++) {
				cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
			}
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			cpuInfo[1] += arrayOfString[2];
			localBufferedReader.close();
		} catch (IOException e) {
			return null;
		}
		return cpuInfo;
	}

	/** 判断手机CPU是否支持NEON指令集 */
	public static boolean isNEON() {
		boolean isNEON = false;
		String cupinfo = getCPUInfos();
		if (cupinfo != null) {
			cupinfo = cupinfo.toLowerCase();
			isNEON = cupinfo != null && cupinfo.contains("neon");
		}
		return isNEON;
	}

	/** 读取CPU信息文件，获取CPU信息 */
	@SuppressWarnings("resource")
	private static String getCPUInfos() {
		String str1 = "/proc/cpuinfo";
		String str2 = "";
		StringBuilder resusl = new StringBuilder();
		String resualStr = null;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			while ((str2 = localBufferedReader.readLine()) != null) {
				resusl.append(str2);
				// String cup = str2;
			}
			if (resusl != null) {
				resualStr = resusl.toString();
				return resualStr;
			}
		} catch (IOException e) {
			return resualStr;
		}
		return resualStr;
	}

	/** 获取当前设备cpu的型号 */
	public static int getCPUModel() {
		return matchABI(getSystemProperty("ro.product.cpu.abi")) | matchABI(getSystemProperty("ro.product.cpu.abi2"));
	}

	/** 匹配当前设备的cpu型号 */
	private static int matchABI(String abiString) {
		if (TextUtils.isEmpty(abiString)) {
			return 0;
		}
		LogUtils.i(LOG_TAG, "abiString" + abiString);
		if ("armeabi".equals(abiString)) {
			return 1;
		} else if ("armeabi-v7a".equals(abiString)) {
			return 2;
		} else if ("x86".equals(abiString)) {
			return 4;
		} else if ("mips".equals(abiString)) {
			return 8;
		}
		return 0;
	}

	/** 获取CPU核心数 */
	public static int getCpuCount() {
		return Runtime.getRuntime().availableProcessors();
	}

	/** 获取Rom版本 */
	public static String getRomversion() {
		String rom = "";
		try {
			String modversion = getSystemProperty("ro.modversion");
			String displayId = getSystemProperty("ro.build.display.id");
			if (modversion != null && !modversion.equals("")) {
				rom = modversion;
			}
			if (displayId != null && !displayId.equals("")) {
				rom = displayId;
			}
		} catch (Exception e) {
			return rom;
		}
		return rom;
	}

	/** 获取系统配置参数 */
	public static String getSystemProperty(String key) {
		String pValue = "";
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method m = c.getMethod("get", String.class);
			pValue = m.invoke(null, key).toString();
		} catch (Exception e) {
			return pValue;
		}
		return pValue;
	}

	/** 获取系统中的Library包 */
	public static List<String> getSystemLibs() {
		Context mContext = BaseApplication.getApplication().getApplicationContext();
		if (null == mContext) {
			return null;
		}
		PackageManager pm = mContext.getPackageManager();
		String[] libNames = pm.getSystemSharedLibraryNames();
		List<String> listLibNames = Arrays.asList(libNames);
		return listLibNames;
	}

	public static String getKernelVersion() {
		String kernelVersion = "";
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream("/proc/version");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return kernelVersion;
		}
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
		String info = "";
		String line = "";
		try {
			while ((line = bufferedReader.readLine()) != null) {
				info += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			if (info != "") {
				final String keyword = "version ";
				int index = info.indexOf(keyword);
				line = info.substring(index + keyword.length());
				index = line.indexOf(" ");
				kernelVersion = line.substring(0, index);
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return kernelVersion;
	}

	/** 获取手机内部空间大小，单位为byte */
	@SuppressWarnings("deprecation")
	public static long getTotalInternalSpace() {
		long totalSpace = -1L;
		try {
			String path = Environment.getDataDirectory().getPath();
			StatFs stat = new StatFs(path);
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();// 获取该区域可用的文件系统数
			totalSpace = totalBlocks * blockSize;
		} catch (Exception e) {
			return totalSpace;
		}
		return totalSpace;
	}

	/** 获取手机内部可用空间大小，单位为byte */
	@SuppressWarnings("deprecation")
	public static long getAvailableInternalMemorySize() {
		long availableSpace = -1l;
		try {
			String path = Environment.getDataDirectory().getPath();// 获取 Android 数据目录
			StatFs stat = new StatFs(path);// 一个模拟linux的df命令的一个类,获得SD卡和手机内存的使用情况
			long blockSize = stat.getBlockSize();// 返回 Int ，大小，以字节为单位，一个文件系统
			long availableBlocks = stat.getAvailableBlocks();// 返回 Int ，获取当前可用的存储空间
			availableSpace = availableBlocks * blockSize;
		} catch (Exception e) {
			return availableSpace;
		}
		return availableSpace;
	}

	/** 获取单个应用最大分配内存，单位为byte */
	public static long getOneAppMaxMemory() {
		Context mContext = BaseApplication.getApplication().getApplicationContext();
		if (mContext == null) {
			return -1;
		}
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		return activityManager.getMemoryClass() * 1024 * 1024;
	}

	/** 获取指定本应用占用的内存，单位为byte */
	public static long getUsedMemory() {
		return getUsedMemory(null);
	}

	/** 获取指定包名应用占用的内存，单位为byte */
	public static long getUsedMemory(String packageName) {
		Context mContext = BaseApplication.getApplication().getApplicationContext();
		if (mContext == null) {
			return -1;
		}
		if (TextUtils.isEmpty(packageName)) {
			packageName = mContext.getPackageName();
		}
		long size = 0;
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runapps = activityManager.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo runapp : runapps) { // 遍历运行中的程序
			if (packageName.equals(runapp.processName)) {// 得到程序进程名，进程名一般就是包名，但有些程序的进程名并不对应一个包名
				// 返回指定PID程序的内存信息，可以传递多个PID，返回的也是数组型的信息
				Debug.MemoryInfo[] processMemoryInfo = activityManager.getProcessMemoryInfo(new int[] { runapp.pid });
				// 得到内存信息中已使用的内存，单位是K
				size = processMemoryInfo[0].getTotalPrivateDirty() * 1024;
			}
		}
		return size;
	}

	/** 获取手机剩余内存，单位为byte */
	public static long getAvailableMemory() {
		Context mContext = BaseApplication.getApplication().getApplicationContext();
		if (mContext == null) {
			return -1;
		}
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(info);
		return info.availMem;
	}

	/** 手机低内存运行阀值，单位为byte */
	public static long getThresholdMemory() {
		Context mContext = BaseApplication.getApplication().getApplicationContext();
		if (mContext == null) {
			return -1;
		}
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(info);
		return info.threshold;
	}

	/** 手机是否处于低内存运行 */
	public static boolean isLowMemory() {
		Context mContext = BaseApplication.getApplication().getApplicationContext();
		if (mContext == null) {
			return false;
		}
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(info);
		return info.lowMemory;
	}

	/**
	 * 获取 AppKey
	 * 
	 * @param
	 * @return
	 */
	public static String getAppKey() {
		Context mContext = BaseApplication.getApplication().getApplicationContext();
		try {
			ApplicationInfo ai = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);

			Bundle bundle = ai.metaData;
			if (bundle != null) {
				return bundle.getString("UMENG_APPKEY");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 网络连接
	 * 
	 * @tags @return 是否有连接
	 */

	public static String checkNet(Context context) {
		try {
			// 获取手机所有连接管理对象（wi_fi,net等连接的管理）
			ConnectivityManager manger = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (manger != null) {
				NetworkInfo info[] = manger.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == State.CONNECTED) {
							String typeName = info[i].getSubtypeName();
							if (TextUtils.isEmpty(typeName)) {
								typeName = info[i].getTypeName();
							}
							return typeName;
						}
					}
				}
			}
		} catch (Exception e) {
			return "";
		}
		return "";

	}

	/**
	 * 网络连接
	 * 
	 * @tags @return 是否有连接
	 */

	public static boolean checkAllNet(Context context) {
		try {
			// 获取手机所有连接管理对象（wi_fi,net等连接的管理）
			ConnectivityManager manger = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (manger != null) {
				NetworkInfo info[] = manger.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == State.CONNECTED) {
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;

	}

	/**
	 * 检查WiFI
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkWiFi(Context context) {
		try {
			// 获取手机所有连接管理对象（wi_fi,net等连接的管理）
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (manager != null) {
				State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
				if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
