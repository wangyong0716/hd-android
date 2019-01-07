package fm.dian.hdui.view.blackboard;

/**
 * Created by song on 2015/5/8.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class GLFont {
    /*
     * 默认采用白色字体，宋体文字加粗
     */
    public static Bitmap getImage(int width, int height ,String mString, int size) {
        return getImage(width, height, mString, size, Color.RED, Typeface.create("宋体",Typeface.BOLD));
    }

    public static Bitmap getImage(int width, int height, String mString, int size, int color) {
        return getImage(width, height, mString, size, color, Typeface.create("宋体",Typeface.BOLD));
    }

    public static Bitmap getImage(int width, int height ,String mString,int size ,int color, String familyName) {
        return getImage(width, height, mString, size, color, Typeface.create(familyName,Typeface.BOLD));
    }

    public static Bitmap getImage(int width, int height ,String mString,int size, int color, Typeface font) {
        int x = width;
        int y = height;

        Bitmap bmp = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
        //图象大小要根据文字大小算下,以和文本长度对应
        Canvas canvasTemp = new Canvas(bmp);
        canvasTemp.drawColor(Color.BLACK);
        Paint p = new Paint();
        p.setColor(color);
        p.setTypeface(font);
        p.setAntiAlias(true);//去除锯齿
        p.setFilterBitmap(true);//对位图进行滤波处理
        p.setTextSize(scalaFonts(size));
        StringBuffer sb=new StringBuffer();
        if(mString.length()>30){
            for (int i = 0; i < mString.length(); i+=20) {
                if(i+20<mString.length()){
                    String s = mString.substring(i,i+20)+"\r\n";
                    sb.append(s);
                }
            }
        }else{
            sb.append(mString);
        }

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(10.0F);
        textPaint.setColor(color);
        textPaint.setAntiAlias(true);
        StaticLayout layout = new StaticLayout(sb.toString(),textPaint,220, Layout.Alignment.ALIGN_CENTER,1.5F,0,false);
        canvasTemp.save();
        if(mString.length()>30){
            canvasTemp.translate(0, 0);//从3，3开始画
        }else{
            canvasTemp.translate(0, 50);//从3，3开始画
        }
        layout.draw(canvasTemp);
        canvasTemp.restore();//别忘了restore

//        StaticLayout layout = new StaticLayout(context.getString(R.string.about),textPaint,(int)(300*fDensity),Alignment.ALIGN_CENTER,1.5F,0,false);
//        layout.draw(canvas);
//        参数含义：
//        1.字符串子资源
//        2 .画笔对象
//        3.layout的宽度，字符串超出宽度时自动换行。
//        4.layout的样式，有ALIGN_CENTER， ALIGN_NORMAL， ALIGN_OPPOSITE 三种。
//        5.相对行间距，相对字体大小，1.5f表示行间距为1.5倍的字体高度。
//        6.相对行间距，0表示0个像素。
//        实际行间距等于这两者的和。
//        7.还不知道是什么意思，参数名是boolean includepad。
//        需要指出的是这个layout是默认画在Canvas的(0,0)点的，如果需要调整位置只能在draw之前移Canvas的起始坐标
//        canvas.translate(x,y);

        return bmp;
    }

    /**
     * 根据屏幕系数比例获取文字大小
     * @return
     */
    private static float scalaFonts(int size) {
        //暂未实现
        return size;
    }

    /**
     * @return 返回指定笔和指定字符串的长度
     */
    public static float getFontlength(Paint paint, String str) {
        return paint.measureText(str);
    }
    /**
     * @return 返回指定笔的文字高度
     */
    public static float getFontHeight(Paint paint)  {
        FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }
    /**
     * @return 返回指定笔离文字顶部的基准距离
     */
    public static float getFontLeading(Paint paint)  {
        FontMetrics fm = paint.getFontMetrics();
        return fm.leading- fm.ascent;
    }

}