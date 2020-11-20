package com.ml.task;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ml.service.DataListService;
import com.ml.util.RedisUtils;
import com.ml.util.UtilHttp;
import com.ml.util.UtilProperties;

@Component
public class ApplicationRunnerImpl implements ApplicationContextAware {

	@Value("fanwei.depts")
	private String deptidUrl;
	
	@Autowired
	private DataListService dataListService;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("我在项目启动的时候执行");
		/**
		 * 项目启动的时候,获取需要定时加载到缓存中的部门id，将获取到的id放到redis中
		 * 
		 * 保证每次重启springboot项目的时候，获取到的部门id都是最新的
		 * */
//		String url = "http://192.168.0.86:8086/ml/api/jira/bmId";
		String url = UtilProperties.getConfig("fanwei.depts");
		String sendGet = UtilHttp.sendGet(url, null);
		JSONObject json = JSONObject.parseObject(sendGet);
		String key = RedisUtils.getKey("deptIds");
		if(key!=null) {
			RedisUtils.delKey("deptIds");
		}
		RedisUtils.addValue("deptIds", json.getString("model"));
		
		List<Object[]> viewJiraBh = dataListService.getViewJiraBh();
		String jsonString = JSONArray.toJSONString(viewJiraBh);
		String jirabh = RedisUtils.getKey("viewJiraBh");
		if(jirabh!=null) {
			RedisUtils.delKey("jirabh");
		}
		RedisUtils.addValue("jirabh", jsonString);
		System.out.println(applicationContext);
	}

	
	
}
