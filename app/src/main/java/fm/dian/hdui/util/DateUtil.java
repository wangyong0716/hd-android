package fm.dian.hdui.util;

import java.util.Date;

/**
 * ******************
 * fileName  :DateUtil.java
 * author    : song
 * createTime:2015-3-12 下午1:08:46
 * fileDes   :
 */
public class DateUtil {
    public static String formatDuring(Date begin, Date end) {
        return formatDuringMMss(end.getTime() - begin.getTime());
    }

    //	public static String formatDuring(long mss) {
//		long days = mss / (1000 * 60 * 60 * 24);
//		long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
//		long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
//		long seconds = (mss % (1000 * 60)) / 1000;
//		return days + " days " + hours + " hours " + minutes + " minutes "
//				+ seconds + " seconds ";
//	}
    public static String formatDuringMMss(long mss) {
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        String hou = hours + "";
        String min = minutes + "";
        String sec = seconds + "";
        if (hours < 10) {
            hou = "0" + hours;
        }
        if (minutes < 10) {
            min = "0" + minutes;
        }
        if (seconds < 10) {
            sec = "0" + seconds;
        }
        return hou + ":" + min + ":"
                + sec;
    }
}
