package com.ml.controller;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ml.dto.JiraProduce;
import com.ml.dto.MLOrder;
import com.ml.service.DataListService;
import com.ml.util.RedisUtils;
import com.ml.util.UtilDate;

@RestController
public class OrderReportController {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private DataListService dataListService;

	/**
	 * 获取jira数据，从数据库单独获取员工产出
	 **/
	@RequestMapping(value = "/requestList")
	public JSONObject requestList(HttpServletRequest req) throws UnsupportedEncodingException {
		String listDto = req.getParameter("list");
		String month = req.getParameter("month");
		
		List<MLOrder> orderlist = JSONArray.parseArray(listDto, MLOrder.class);
		JSONObject re_back = new JSONObject();
		for (MLOrder mlOrder : orderlist) {
			if (!StringUtils.hasText(mlOrder.getJirabh())) {
                continue;
            }
			Map<String, String> revertParameter = revertParameter(mlOrder.getJirabh(),month);
			String sueids = revertParameter.get("sueids");
			JSONObject json = new JSONObject();
			if(sueids.length()!=0) {
				json = fullBackHaveDateResult(sueids,revertParameter.get("startDate"),revertParameter.get("endDate"));
				mlOrder.setFact(json.getDouble("fact"));
				mlOrder.setProduce(json.getDouble("produce"));
				mlOrder.setEffic(json.getString("effic"));//平均效率
			}
			
		}
		re_back.put("backlist", JSONArray.toJSONString(orderlist));
		return re_back;
	}
	
	/**
	 * 获取jira数据，从数据库单独获取员工产出
	 **/
	@RequestMapping(value = "/getOrderReportByMonth")
	public JSONObject index(HttpServletRequest req) throws UnsupportedEncodingException {
		String jirabh = req.getParameter("jirabh");
		String month = req.getParameter("month");
		
		Map<String, String> revertParameter = revertParameter(jirabh,month);
		
		String sueids = revertParameter.get("sueids");
		JSONObject json = new JSONObject();
		if(sueids.length()==0) {
			json.put("fact", 0);//实际工作时间
			json.put("produce", 0);//产出/天
			json.put("effic", "");//平均效率
		}else {
			json = fullBackHaveDateResult(sueids,revertParameter.get("startDate"),revertParameter.get("endDate"));
		}
		return json;
	}

	@RequestMapping(value = "/getOrderIssueid")
	public JSONObject getOrderIssueid(HttpServletRequest req) throws UnsupportedEncodingException {
		String jirabh = req.getParameter("jirabh");
		String month = req.getParameter("month");
		
		Map<String, String> revertParameter = revertParameter(jirabh,month);
		String startDate = revertParameter.get("startDate");
		String endDate = revertParameter.get("endDate");
		String sueids = revertParameter.get("sueids");
		String mcMap = revertParameter.get("mcMap");
		
		JSONObject parseObject = JSONObject.parseObject(mcMap);
		
		logger.info("使用log打印日志："+parseObject.toString());
		
		JSONObject json = new JSONObject();
		if(sueids.length()==0) {
			json.put("listStr", new ArrayList());
		}else {
 			json = fullOrderListByDetail(sueids,startDate,endDate,parseObject);
		}
		return json;
	}
	
	private Map<String,String> revertParameter(String jirabh,String month){
		Map<String,String> map = new HashMap<String, String>();
		String startDate = null, endDate = null;
		if (StringUtils.hasText(month)) {
			String m = month.substring(4, month.length());
			int maxDays = UtilDate.getMaxDayByYearMonth(Integer.valueOf(month.substring(0, 4)),
					Integer.valueOf(month.substring(4, month.length())));
			Calendar cal = Calendar.getInstance();
			System.out.println(cal.get(Calendar.YEAR));
			startDate = cal.get(Calendar.YEAR) + "-" + m + "-01";
			endDate = cal.get(Calendar.YEAR) + "-" + m + "-" + maxDays;
		}
		String bh = "";
		String jiras[] = jirabh.split(",");
//		for (int i = 0; i < jiras.length; i++) {
//			bh += "'" + jiras[i] + "',";
//		}
//		bh = bh.substring(0, bh.length() - 1);
		
//		List<Object[]> suidByJiraBh = dataListService.getSuidByJiraBh(bh);// 获取到issueid的集合信息
		String jirabhs = RedisUtils.getKey("jirabh");
		List<Object[]> suidByJiraBh = JSONArray.parseArray(jirabhs,Object[].class);
		JSONObject json = new JSONObject();
		String sueids = "";
		for (Object[] objects : suidByJiraBh) {
			for(String fwbh : jiras) {
				if(String.valueOf(objects[1]).equals(fwbh)) {
					sueids += objects[0] + ",";
					json.put(String.valueOf(objects[0]), String.valueOf(objects[2]));//将ssueid对应的epicname放入map
				}
			}
		}
		map.put("startDate", startDate);
		map.put("endDate", endDate);
//		map.put("bh", bh);
		map.put("sueids", sueids.length()>1?sueids.substring(0, sueids.length()-1):"");
		map.put("mcMap", json.toString());
		return map;
	}
	
	/**
	 * *
	 * @param sueids
	 * @param startDate
	 * @param endDate
	 * @param xzdatas
	 * @param xzMap
	 * 		当前传过来的jira编号对应的所有的issueid获取每个任务对应的时长
	 * @return
	 */
	public JSONObject fullBackHaveDateResult(String sueids, String startDate, String endDate) {
		JSONObject json = new JSONObject();
		double cc = 0.0;// 产出时间总和
		double sumsj = 0.0;// 实际工作时间总和

		for (String sueid : sueids.split(",")) {// 循环所有的任务
			// 一条任务的end预估和剩余
			Double fact = dataListService.getFactByOrderMonth(sueid, startDate, endDate);// Epic下所有任务的实际时长
			Object[] end = dataListService.getYSEndAndYSBegin(null, sueid, startDate, endDate);
			
			double ygend = 0.0;// 结束日期预估汇总
			double syend = 0.0;// 结束日期剩余汇总
			double ygstart = 0.0;// 开始日期预估汇总
			double systart = 0.0;// 开始日期剩余汇总
			
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

			double timeProduce = (ygend - syend) - (ygstart - systart);// 结束产出 - 开始产出 = 时间段产出

			Double produce= Double.valueOf(String.format("%.2f", timeProduce / 3600 / 8));//产出/天
			Double sjgzsj= Double.valueOf(String.format("%.2f", fact));//实际工作时间
			sumsj = sumsj+sjgzsj;
			cc = cc+produce;
		}
		String effic = "";
		if (sumsj == 0 || cc == 0) {
			effic = "";
		} else if (sumsj > 0 && cc > 0) {
			double a = cc / sumsj * 100;
			BigDecimal b = new BigDecimal(a);
			double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			if (f1 < 0) {
				effic="";
			} else {
				effic = (f1 + "%");//平均效率
			}
		}
		json.put("fact", sumsj);//实际工作时间
		json.put("produce", String.format("%.2f",cc));//产出/天
		json.put("effic", effic);//平均效率
		return json;
	}

	public JSONObject fullOrderListByDetail(String sueids, String startDate, String endDate,JSONObject map) {
		List<JiraProduce> list = new ArrayList<JiraProduce>();
		
		List<Object[]> jiraissue = dataListService.getJiraissueByJiraBh(sueids);
		for (Object[] objects : jiraissue) {
			JiraProduce model = new JiraProduce();
            model.setUsername(objects[0] == null?"":String.valueOf(objects[0]));
            model.setJirauser(objects[1] == null?"":String.valueOf(objects[1]));
            model.setPname(objects[2] == null?"":String.valueOf(objects[2]));
            model.setIssueSummary(objects[3] == null?"":String.valueOf(objects[3]));
            model.setpKey(objects[4] == null?"":String.valueOf(objects[4]));
            model.setJirabh(objects[5] == null?"":String.valueOf(objects[5]));
            model.setCreated(objects[6] == null?"":String.valueOf(objects[6]));//实际存放着任务ID借此字段一用
            list.add(model);
		}
		
		for (JiraProduce dto : list) {
			if(dto.getCreated() == null || dto.getCreated().equals("")) {
				continue;
			}
			JSONObject obj = fullBackHaveDateResult(dto.getCreated(),startDate,endDate);
			String fact = obj.get("fact")==null?"0":String.valueOf(obj.get("fact"));//实际工作时间
			String produce = obj.get("produce")==null?"0":String.valueOf(obj.get("produce"));//产出/天
			String effic = obj.getString("effic");//平均效率
			dto.setProduce(Double.valueOf(produce));
			dto.setEfficiencyPercent(effic);
			dto.setSjgzsj(Double.valueOf(fact));
			
			String epicname = map.get(dto.getCreated())==null?"":String.valueOf(map.get(dto.getCreated()));
			dto.setEpicname(epicname);
		}
		
		JiraProduce sumj = new JiraProduce();
        double sumsj = 0.0;//实际
        double sumproduce = 0.0;//产出
        double sumefficiency = 0.0;//效率 = 产出 / 实际工作时间
        double xzsum = 0.0;
        String sumefficiencyPrecent = "0";
        for (JiraProduce j : list) {
            double sj = changeDouble(j.getSjgzsj());//实际
            double produce = j.getProduce()==null?0:j.getProduce();//产出
            // 求和
            sumsj = sumsj + sj;//实际
            sumproduce = sumproduce+produce;
        }

        sumj.setSjgzsj(Double.valueOf(String.format("%.2f", sumsj)));
        sumj.setProduce(Double.valueOf(String.format("%.2f", sumproduce)));
        sumj.setEfficiency(sumefficiency);
        if (sumsj > 0) {
            sumefficiency = (sumj.getProduce() / sumj.getSjgzsj()) * 100;//效率 = 产出 / 实际工作时间
            sumefficiency = Double.valueOf(String.format("%.2f", sumefficiency));
            sumefficiencyPrecent = String.format("%.2f", sumefficiency);

//            xzsum = sumj.getProduce()/sumxz * 100;//含闲置效率 = 产出 / 实际工作时间
            xzsum = Double.valueOf(String.format("%.2f", xzsum));
        }
        if(sumefficiency<0){
            sumj.setEfficiencyPercent("");
        }else{
            sumj.setEfficiencyPercent(sumefficiencyPrecent + "%");//不含闲置效率
        }

        if(xzsum<0){
            sumj.setXzxl("");//含闲置效率
        }else{
            sumj.setXzxl(xzsum + "%");
        }

        sumj.setPname("合计：");
        list.add(sumj);
		
		JSONObject	json = new JSONObject();
		json.put("listStr", JSONArray.toJSON(list).toString());
		return json;
	}
	
	public static double addDouble(double m1, double m2) {
		BigDecimal p1 = new BigDecimal(Double.toString(m1));
		BigDecimal p2 = new BigDecimal(Double.toString(m2));
		return p1.add(p2).doubleValue();
	}

	public double changeDouble(Double num) {
        if (num == null) {
            num = 0.0;
        }
        return num;
    }
	
}
