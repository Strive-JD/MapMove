package com.test.mapmove.utils;


/**
 * 坐标转换器
 */
public class LocationUtils {
    private final static double X_PI = 3.14159265358979324 * 3000.0 / 180.0;
    private final static double A = 6378245.0;
    private final static double EE = 0.00669342162296594323;
    public final static String bd09 = "bd09";
    public final static String gps84 = "gps84";
    public final static String gcj02 = "gcj02";

    /**
     * BD09 坐标转 GCJ02 坐标
     *
     * @param lng BD09 坐标纬度
     * @param lat BD09 坐标经度
     * @return GCJ02 坐标：[经度，纬度]
     */
    public static double[] bd09ToGcj02(double lng, double lat) {
        double x = lng - 0.0065;
        double y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
        double gg_lng = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new double[]{gg_lng, gg_lat};
    }

    /**
     * GCJ02 坐标转 BD09 坐标
     *
     * @param lng GCJ02 坐标经度
     * @param lat GCJ02 坐标纬度
     * @return BD09 坐标：[经度，纬度]
     */
    public static double[] gcj02ToBd09(double lng, double lat) {
        double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * X_PI);
        double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * X_PI);
        double bd_lng = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new double[]{bd_lng, bd_lat};
    }

    /**
     * GCJ02 坐标转 WGS84 坐标
     *
     * @param lng GCJ02 坐标经度
     * @param lat GCJ02 坐标纬度
     * @return WGS84 坐标：[经度，纬度]
     */
    public static double[] gcj02ToWGS84(double lng, double lat) {
        if (outOfChina(lng, lat)) {
            return new double[]{lng, lat};
        }
        double dlat = transformLat(lng - 105.0, lat - 35.0);
        double dlng = transformLng(lng - 105.0, lat - 35.0);
        double radlat = lat / 180.0 * Math.PI;
        double magic = Math.sin(radlat);
        magic = 1 - EE * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((A * (1 - EE)) / (magic * sqrtmagic) * Math.PI);
        dlng = (dlng * 180.0) / (A / sqrtmagic * Math.cos(radlat) * Math.PI);
        double mglat = lat + dlat;
        double mglng = lng + dlng;
        return new double[]{lng * 2 - mglng, lat * 2 - mglat};
    }

    /**
     * WGS84 坐标转 GCJ02 坐标
     *
     * @param lng WGS84 坐标经度
     * @param lat WGS84 坐标纬度
     * @return GCJ02 坐标：[经度，纬度]
     */
    public static double[] wgs84ToGcj02(double lng, double lat) {
        if (outOfChina(lng, lat)) {
            return new double[]{lng, lat};
        }
        double dlat = transformLat(lng - 105.0, lat - 35.0);
        double dlng = transformLng(lng - 105.0, lat - 35.0);
        double radlat = lat / 180.0 * Math.PI;
        double magic = Math.sin(radlat);
        magic = 1 - EE * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((A * (1 - EE)) / (magic * sqrtmagic) * Math.PI);
        dlng = (dlng * 180.0) / (A / sqrtmagic * Math.cos(radlat) * Math.PI);
        double mglat = lat + dlat;
        double mglng = lng + dlng;
        return new double[]{mglng, mglat};
    }

    /**
     * BD09 坐标转 WGS84 坐标
     *
     * @param lng BD09 坐标经度
     * @param lat BD09 坐标纬度
     * @return WGS84 坐标：[经度，纬度]
     */
    public static double[] bd09ToWGS84(double lng, double lat) {
        double[] gcj = bd09ToGcj02(lng, lat);
        return gcj02ToWGS84(gcj[0], gcj[1]);
    }


    /**
     * WGS84 坐标转 BD09 坐标
     *
     * @param lng WGS84 坐标经度
     * @param lat WGS84 坐标纬度
     * @return BD09 坐标：[经度，纬度]
     */
    public static double[] wgs84ToBd09(double lng, double lat) {
        double[] gcj = wgs84ToGcj02(lng, lat);
        return gcj02ToBd09(gcj[0], gcj[1]);
    }

    /**
     * Mercator 坐标转 WGS84 坐标
     *
     * @param lng Mercator 坐标经度
     * @param lat Mercator 坐标纬度
     * @return WGS84 坐标：[经度，纬度]
     */
    public static double[] mercatorToWGS84(double lng, double lat) {
        double x = lng / 20037508.34d * 180.;
        double y = lat / 20037508.34d * 180.;
        y = 180 / Math.PI * (2 * Math.atan(Math.exp(y * Math.PI / 180.0)) - Math.PI / 2);
        return new double[]{x, y};
    }

    /**
     * WGS84 坐标转 Mercator 坐标
     *
     * @param lng WGS84 坐标经度
     * @param lat WGS84 坐标纬度
     * @return Mercator 坐标：[经度，纬度]
     */
    public static double[] wgs84ToMercator(double lng, double lat) {
        double x = lng * 20037508.34D / 180.0;
        double y = Math.log(Math.tan((90.0 + lat) * Math.PI / 360.0)) / (Math.PI / 180.);
        y = y * 20037508.34D / 180.0;
        return new double[]{x, y};
    }

    private static double transformLat(double lng, double lat) {
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * Math.PI) + 20.0 * Math.sin(2.0 * lng * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * Math.PI) + 40.0 * Math.sin(lat / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * Math.PI) + 320 * Math.sin(lat * Math.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLng(double lng, double lat) {
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * Math.PI) + 20.0 * Math.sin(2.0 * lng * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * Math.PI) + 40.0 * Math.sin(lng / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * Math.PI) + 300.0 * Math.sin(lng / 30.0 * Math.PI)) * 2.0 / 3.0;
        return ret;
    }

    private static boolean outOfChina(double lng, double lat) {
        return lng < 72.004 || lng > 137.8347 || lat < 0.8293 || lat > 55.8271;
    }
}
