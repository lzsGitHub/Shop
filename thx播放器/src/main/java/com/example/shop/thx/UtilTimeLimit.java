package com.example.shop.thx;

/**
 * Created by Administrator on 2016/12/29.
 */
public class UtilTimeLimit {
    
    public static final int MIAO = 1000;
    public static final int FEN = 60 * 1000;
    public static final int HOUR = 60 * 60 * 1000;
    
    // 1 40 0
    public static String format(long s) {
        long hour = s / HOUR;
        long totalLef1 = s - hour * HOUR;
        
        long minute = totalLef1 / FEN;
        long totalLeft2 = totalLef1 - minute * FEN;
        
        long seconed = totalLeft2 / MIAO;
        
        String mins=null;
        String seconds=null;
        
        if (minute<10){
            mins="0"+minute;
            
        }else {
            mins=minute+"";
        }
    
        if (seconed<10){
            seconds="0"+seconed;
        
        }else {
            seconds=seconed+"";
        }
        
        if (hour==0){
            return mins + ":" + seconds;
        }else {
            return hour + ":" + mins + ":" + seconds;
        }
  
        
    }
}
