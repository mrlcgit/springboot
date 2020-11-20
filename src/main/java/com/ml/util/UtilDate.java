package com.ml.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Date和String转换
 * @author zzy
 *
 *  注意 格式 要使用 其他 均为错误 使用 大写的Y会导致 是按年周计算年 跨年会报错
 *  yyyy-MM-dd
 * yyyy-MM-dd HH:mm:ss
 * yyyy年MM月dd日 HH时mm分ss秒
 */
public class UtilDate {

	public static String getLastMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Date date = new Date();
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        aCalendar.setTime(date);
        aCalendar.add(Calendar.MONTH, -1);

        return sdf.format(aCalendar.getTime());
    }
	
	public static Integer getYear(){
		Calendar calendar = Calendar.getInstance();
		return  calendar.get(Calendar.YEAR);
	}
	public static Integer getMonth(){
		Calendar calendar = Calendar.getInstance();
		return ( calendar.get(Calendar.MONTH)+1 );
	}
	public static Integer getDay(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DATE);
	}

	public static String getYearMonth(){
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		return year+"-"+( month>9?""+month:"0"+month );
	}

	public static String getPreYearMonth(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH,-1);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		return year+"-"+( month>9?""+month:"0"+month );
	}

	public static String getNextYearMonth(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH,+1);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		return year+"-"+( month>9?""+month:"0"+month );
	}

	/**
	 * 获得某个月最大天数
	 * @param year 年份
	 * @param month 月份 (1-12)
	 * @return 某个月最大天数
	 */
	public  static int getMaxDayByYearMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year );
		//Calendar 的月份取值范围是 0 - 11 ， 0代表1月 11代表12月份
		month = month - 1 ;
		calendar.set(Calendar.MONTH, month);
		return calendar.getActualMaximum(Calendar.DATE);
	}

	public  static int getMaxDayNowMonth() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getActualMaximum(Calendar.DATE);
	}


	/**根据date获取*/
	public static int getMaxDayByYearMonth(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getActualMaximum(Calendar.DATE);
	}




	/**根据date获取星期几*/
	public static String getWeekDay(Date date){
		//获取当前星期几
		Calendar calendar;
		calendar = Calendar.getInstance();
		if(date!=null){
			calendar.setTime(date);
		}
		///System.out.println("当前月份："+calendar.DAY_OF_MONTH);
		String week;
		week = calendar.get(calendar.DAY_OF_WEEK) - 1 + "";
		if ("0".equals(week)) {
			week = "7";
		}
		week = "星期"+"一二三四五六日".charAt(Integer.valueOf(week)-1);
		return week;
	}

	/**根据date获取星期几简化版本*/
	public static String getWeekDayHh(Date date){
		//获取当前星期几
		Calendar calendar;
		calendar = Calendar.getInstance();
		if(date!=null){
			calendar.setTime(date);
		}
		///System.out.println("当前月份："+calendar.DAY_OF_MONTH);
		String week;
		week = calendar.get(calendar.DAY_OF_WEEK) - 1 + "";
		if ("0".equals(week)) {
			week = "7";
		}
		week = ""+"一二三四五六日".charAt(Integer.valueOf(week)-1);
		return week;
	}

	/**根据date获取星期几简化版本*/
	public static String getWeekDayHh(Integer year,Integer month,Integer day){
		//获取当前星期几
		Calendar calendar;
		calendar = Calendar.getInstance();
		if(year!=null && month!=null && day!=null){
			calendar.set(year,month-1,day);
		}
		///System.out.println("当前月份："+calendar.DAY_OF_MONTH);
		String week;
		week = calendar.get(calendar.DAY_OF_WEEK) - 1 + "";
		if ("0".equals(week)) {
			week = "7";
		}
		week = ""+"一二三四五六日".charAt(Integer.valueOf(week)-1);
		return week;
	}


	/**把String(yyyy-MM-dd)字符转换成Date方法1*/
	public static Date strToDate1(String strdate){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
		Date date = null;
		try {
			date = sdf.parse(strdate);
		} catch (ParseException e) {
			System.out.println("输出时间格式不正确！");
			e.printStackTrace();
		}
		return date;
	}
	public static Date strToDate1(String strdate, String format){
		if(format==null || "".equals(format)){
			return strToDate1( strdate);
		}
		SimpleDateFormat sdf=new SimpleDateFormat(format);//小写的mm表示的是分钟
		Date date = null;
		try {
			date = sdf.parse(strdate);
		} catch (ParseException e) {
			System.out.println("输出时间格式不正确！");
			e.printStackTrace();
		}
		return date;
	}
	
	/**把String字符转换成Date方法2(已过时,能用)(yyyy-MM-dd)*/
	@SuppressWarnings("deprecation")
	public static Date strToDate2(String strdate){
		Date date=null;
		try {
			date=new Date(strdate);
		} catch (Exception e) {
			System.out.println("输入时间格式不正确！");
		}
		return date;
	}

	/** 获取当前时间 (yyyy-MM-dd HH:mm:ss)*/
	public static String getNowTime(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str=sdf.format(new Date());
		return str;
	}
	/** 获取当前时间 (yyyy-MM-dd)*/
	public static String getNowDate(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String str=sdf.format(new Date());
		return str;
	}
	/** 获取当前时间 (yyyy-MM-dd)*/
	public static String getYesToday(){
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DATE, ca.get(Calendar.DATE) - 1);//昨天
		int month = ( ca.get(Calendar.MONTH)+1 );
		int day = ca.get(Calendar.DATE);
		String m = month > 9 ? ""+ month : "0"+ month;
		String d = day > 9 ? ""+ day : "0"+ day;
		String date = ca.get(Calendar.YEAR) + "-" + m + "-" + d;
		return date;
	}


	/**把Date格式转换成String方法1不设置格式(yyyy-MM-dd)*/
	public static String dateToStr1(Date date){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String str=sdf.format(date);
		return str;
	}

	/*** yyyy-MM-dd HH:mm:ss */
	public static String dateToStr2(Date date){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str=sdf.format(date);
		return str;
	}

	/**把Date格式转换成String方法2设置格式()
	 * yyyy-MM-dd
	 * yyyy-MM-dd HH:mm:ss
	 * yyyy年MM月dd日 HH时mm分ss秒
	 * */
	public static String dateToStr1(Date date, String format){
		String str="";
		try {
			SimpleDateFormat sdf=new SimpleDateFormat(format);
			str=sdf.format(date); 
		} catch (Exception e) {
			System.out.println("输入时间格式不正确！");
		}
		return str;
	}
	
	/**
	 * 计算[上午/下午]的方法 <br/>
	 * [若果date为null则是当前时间] <br/>
	 * 上午：am <br/>
	 * 下午：pm <br/>
	 * @param date
	 * */
	public static String getMorningOrAfternoon(Date date){
		String amOrpm = "";
		GregorianCalendar ca = new GregorianCalendar();
		if(date!=null){
			long millis = date.getTime();
			ca.setTimeInMillis(millis);
		}
		int ampm = ca.get(GregorianCalendar.AM_PM);
		if(ampm == 0){
			amOrpm = "am";
		} else if(ampm == 1) {
			amOrpm = "pm";
		}
		return amOrpm;
	}
	
			/**
			 * 时间段划分划分结果为汉字
			*@param date [date为null表示当前时间]
			*/
		@SuppressWarnings("deprecation")
		public static String getTimeQuantum(Date date){
			String TimeQuantum="";
			int hours = 0;
			if(date!=null){
				hours = date.getHours();
			}else{
				Date nowdate = new Date();
				hours = nowdate.getHours();
			}
			if(hours>=3 && hours < 6){//凌晨:3:00--6:00
				TimeQuantum="凌晨";
			}else if(hours>=6 && hours < 8){//早晨:6:00---8:00
				TimeQuantum="上午";
			}else if(hours>=8 && hours < 11){//上午:8:00--11:00
				TimeQuantum="上午";
			}else if(hours>=11 && hours < 13){//中午:11:00--13:00
				TimeQuantum="中午";
			}else if(hours>=13 && hours < 17){//下午:13:00--17:00
				TimeQuantum="下午";
			}else if(hours>=17 && hours < 19){//傍晚:17:00--19:00
				TimeQuantum="傍晚";
			}else if(hours>=19 && hours < 23){//晚上:19:00--23:00
				TimeQuantum="晚上";
			}else if(hours>=0 && hours < 3){//深夜:23:00--3:00
				TimeQuantum="深夜";
			}
			return TimeQuantum;
		}
	
}
