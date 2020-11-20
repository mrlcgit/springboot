package com.ml.task;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ml.util.RedisUtils;
import com.ml.util.UtilDate;
import com.ml.util.UtilHttp;

@Component
public class Task {

	@Value("${fanwei.sysData}")
	private String strUrl;
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	@Async
	@Scheduled(cron="${cron.task}")
    public void reportCurrentTime() {
		String lastMonth = UtilDate.getLastMonth();
		String key = RedisUtils.getKey("deptIds");
		if(key != null) {
			for (String deptid : key.split(",")) {
				String requestParams = "month="+lastMonth+"&departid="+deptid;
				String sendGet = UtilHttp.sendGet(strUrl, requestParams);
				System.out.println("reback param : "+sendGet);
			}
			
		}
        System.out.println("现在时间：" + dateFormat.format(new Date()));
    }

	
}
