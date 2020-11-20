package com.ml.controller;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ml.dto.JiraProduce;
import com.ml.service.DataListService;
import com.ml.util.RedisUtils;

@RestController
public class IndexController {

	@Autowired
	private DataListService dataListService;

	/**
	*	获取jira数据，从数据库单独获取员工产出
	**/
	@RequestMapping(value = "/index")
	public JSONObject index(HttpServletRequest req) throws UnsupportedEncodingException {
		String user = req.getParameter("users");
		String users[] = JSONObject.parseObject(user, String[].class);
		String startDate = req.getParameter("startDate");
		String endDate = req.getParameter("endDate");
		String listStr = req.getParameter("list");
		String jirabh = req.getParameter("jirabh");
		String departid = req.getParameter("departid");
		
		List<JiraProduce> list = new ArrayList<JiraProduce>();
		list = JSONArray.parseArray(listStr, JiraProduce.class);
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String xzMaps = req.getParameter("xzMap");
		String userNameMaps = req.getParameter("userNameMap");
		
		Gson gson = new Gson();
		Map<String, String> xzMap = gson.fromJson(xzMaps, new TypeToken<Map<String, String>>() {}.getType());
		Map<String, String> userNameMap = gson.fromJson(userNameMaps, new TypeToken<Map<String, String>>() {}.getType());
		
		JSONObject json = new JSONObject();
		String m = startDate.replaceAll("-","");
        m=m.substring(0,m.length()-2);
		//获取闲置数据
		List<Object[]> xzdatas = dataListService.getXzData(startDate, endDate, jirabh);
		
		List<JiraProduce> fullBackHaveDateResult = fullBackHaveDateResult(users, userNameMap, list, startDate, endDate,xzdatas,xzMap);
		Object ary = JSONArray.toJSON(fullBackHaveDateResult);
		json.put("deal", JSONArray.toJSON(fullBackHaveDateResult).toString());
		//将数据放入redis缓存起来
		RedisUtils.addValue("departid"+m+departid, JSONArray.toJSON(fullBackHaveDateResult).toString());
		RedisUtils.disableTime("departid"+m+departid, RedisUtils.EXPIRE_TIME);
		return json;
	}

	public List<JiraProduce> fullBackHaveDateResult(String users[], Map<String, String> userNameMap,
			List<JiraProduce> list, String startDate, String endDate, List<Object[]> xzdatas,
			Map<String, String> xzMap) {
		List<JiraProduce> res = new ArrayList();
		for (String user : users) {
			JiraProduce model = new JiraProduce();
			double sumyg = 0.0;// 预估时间总和
			double sumsy = 0.0;// 剩余时间总和
			double sumsj = 0.0;// 实际工作时间总和
			double efficiency = 0.0;

			double ygend = 0.0;// 结束日期预估汇总
			double syend = 0.0;// 结束日期剩余汇总
			double ygstart = 0.0;// 开始日期预估汇总
			double systart = 0.0;// 开始日期剩余汇总
			Double fact = dataListService.getXHByUserAndData(user, startDate, endDate, null);
			for (JiraProduce j : list) {
				if (user.equals(j.getJirauser())) {// 名字相同 表示 一个人的
					//时间段消耗
					if (xzMap.get(j.getCreated()) != null) {
						continue;
					}
					//一条任务的end预估和剩余
					Object[] end = dataListService.getYSEndAndYSBegin(user, j.getCreated(), startDate, endDate);
					if (!String.valueOf(end[0]).equals("")) {
						ygend = ygend + Double.valueOf(String.valueOf(end[1]));
					} else if (!String.valueOf(end[2]).equals("")) {
						ygend = ygend + Double.valueOf(String.valueOf(end[3]));
					} else {
						ygend = ygend + Double.valueOf(String.valueOf(end[4]));
					}
					if (!String.valueOf(end[5]).equals("")) {
						syend = syend + Double.valueOf(String.valueOf(end[6]));
					} else if (!String.valueOf(end[7]).equals("")) {
						syend = syend + Double.valueOf(String.valueOf(end[8]));
					} else {
						syend = syend + Double.valueOf(String.valueOf(end[9]));
					}
					if (!String.valueOf(end[10]).equals("")) {
						ygstart = ygstart + Double.valueOf(String.valueOf(end[11]));
					} else if (!String.valueOf(end[12]).equals("")) {
						ygstart = ygstart + Double.valueOf(String.valueOf(end[13]));
					} else {
						ygstart = ygstart + Double.valueOf(String.valueOf(end[14]));
					}
					if (!String.valueOf(end[15]).equals("")) {
						systart = systart + Double.valueOf(String.valueOf(end[16]));
					} else if (!String.valueOf(end[17]).equals("")) {
						systart = systart + Double.valueOf(String.valueOf(end[18]));
					} else {
						systart = systart + Double.valueOf(String.valueOf(end[19]));
					}
				}
			}
			double timeProduce = (ygend - syend) - (ygstart - systart);// 结束产出 - 开始产出 = 时间段产出

			model.setProduce(Double.valueOf(String.format("%.2f", timeProduce / 3600 / 8)));
			model.setUsername(userNameMap.get(user));
			model.setJirauser(user);
			model.setSjgzsj(Double.valueOf(String.format("%.2f", fact / 3600 / 8)));
			if (fact == 0 || timeProduce == 0) {
				model.setEfficiencyPercent("0%");
			} else if (fact > 0 && timeProduce > 0) {
				double a = model.getProduce() / model.getSjgzsj() * 100;
				BigDecimal b = new BigDecimal(a);
				double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				if (f1 < 0) {
					model.setEfficiencyPercent("");
				} else {
					model.setEfficiencyPercent(f1 + "%");
				}
			}

			Double xzgs = 0d;
			for (Object obj[] : xzdatas) {
				//获取当前人的闲置工时
				if (user.equals(obj[4])) {
					xzgs = xzgs + Double.valueOf(String.valueOf(obj[0]));
				}
			}
			if (addDouble(model.getSjgzsj(), xzgs) > 0) {
				BigDecimal b = new BigDecimal(addDouble(model.getSjgzsj(), xzgs));
				double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				model.setXz(f1);// 含闲置
			} else {
				model.setXz(addDouble(model.getSjgzsj(), xzgs));// 含闲置
			}
			double countgs = model.getSjgzsj() + xzgs;
			;// 含闲置总工作时间
			if (countgs > 0) {
				double a = model.getProduce() / model.getXz() * 100;
				BigDecimal b = new BigDecimal(a);
				double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				if (f1 < 0) {
					model.setXzxl("");
				} else if (f1 > 0) {
					model.setXzxl(f1 + "%");
				} else {
					model.setXzxl(0 + "%");
				}
			} else {
				model.setXzxl("0" + "%");
			}

			res.add(model);
		}
		return res;
	}

	public static double addDouble(double m1, double m2) {
		BigDecimal p1 = new BigDecimal(Double.toString(m1));
		BigDecimal p2 = new BigDecimal(Double.toString(m2));
		return p1.add(p2).doubleValue();
	}

}
