package fm.dian.hdui.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * 类说明
 *
 * @author 作者： song
 * @version 创建时间：2013-3-1 下午3:51:41
 */
public class TimeUtil {


    public static String getDate(String month, String day, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制
        java.util.Date d = new java.util.Date();
        ;
        String str = sdf.format(d);
        String nowmonth = str.substring(5, 7);
        String nowday = str.substring(8, 10);
        String result = null;

        int temp = Integer.parseInt(nowday) - Integer.parseInt(day);
        switch (temp) {
            case 0:
//            result = "今天";
//            result = "";
                int hour = Integer.parseInt(time.substring(0, 2));
                if (hour >= 6 && hour < 8) {
                    result = "早上";
                } else if (hour >= 8 && hour < 11) {
                    result = "上午";
                } else if (hour >= 11 && hour < 13) {
                    result = "中午";
                } else if (hour >= 13 && hour < 18) {
                    result = "下午";
                } else {
                    result = "晚上";
                }
                break;
            case 1:
                result = "昨天";
                break;
            case 2:
                result = "前天";
                break;
            default:
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.parseInt(month) + "月");
                sb.append(Integer.parseInt(day) + "日");
                result = sb.toString();
                break;
        }
        return result;
    }

    public static String getTime(int timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = null;
        try {
//            java.util.Date currentdate = new java.util.Date();// 当前时间

//            long i = (currentdate.getTime() / 1000 - timestamp) / (60);
//            System.out.println(currentdate.getTime());
//            System.out.println(i);
//            Timestamp now = new Timestamp(System.currentTimeMillis());// 获取系统当前时间
//            System.out.println("now-->" + now);// 返回结果精确到毫秒。

            String str = sdf.format(new Timestamp(IntToLong(timestamp)));
            time = str.substring(11, 16);

            String month = str.substring(5, 7);
            String day = str.substring(8, 10);
//            System.out.println(str);
//            System.out.println(time);
//            System.out.println(getDate(month, day));
            time = getDate(month, day, time) + time;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return time;
    }

    // java Timestamp构造函数需传入Long型
    public static long IntToLong(int i) {
        long result = (long) i;
        result *= 1000;
        return result;
    }
}
