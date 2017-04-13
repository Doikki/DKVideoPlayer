package com.devlin_n.magic_player;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * 
 * 类描述：网络链接类型判断
 * 
 * @author Super南仔
 * @time 2016-9-9
 */
public class NetUtil {

	/**
	 * 判断当前网络类型-1为未知网络0为没有网络连接1网络断开或关闭2为以太网3为WiFi4为2G5为3G6为4G
	 */
	public static int getNetworkType(Context context) {
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			/** 没有任何网络 */
			return 0;
		}
		if (!networkInfo.isConnected()) {
			/** 网络断开或关闭 */
			return 1;
		}
		if (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
			/** 以太网网络 */
			return 2;
		} else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			/** wifi网络，当激活时，默认情况下，所有的数据流量将使用此连接 */
			return 3;
		} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			/** 移动数据连接,不能与连接共存,如果wifi打开，则自动关闭 */
			switch (networkInfo.getSubtype()) {
			case TelephonyManager.NETWORK_TYPE_GPRS:
			case TelephonyManager.NETWORK_TYPE_EDGE:
			case TelephonyManager.NETWORK_TYPE_CDMA:
			case TelephonyManager.NETWORK_TYPE_1xRTT:
			case TelephonyManager.NETWORK_TYPE_IDEN:
				/** 2G网络 */
			case TelephonyManager.NETWORK_TYPE_UMTS:
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
			case TelephonyManager.NETWORK_TYPE_HSDPA:
			case TelephonyManager.NETWORK_TYPE_HSUPA:
			case TelephonyManager.NETWORK_TYPE_HSPA:
			case TelephonyManager.NETWORK_TYPE_EVDO_B:
			case TelephonyManager.NETWORK_TYPE_EHRPD:
			case TelephonyManager.NETWORK_TYPE_HSPAP:
				/** 3G网络 */
			case TelephonyManager.NETWORK_TYPE_LTE:
				/** 4G网络 */
				return 4;
			}
		}
		/** 未知网络 */
		return -1;
	}
}
