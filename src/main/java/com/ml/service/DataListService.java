package com.ml.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ml.dao.SystemDao;
import com.ml.dto.JiraProduce;

@Service
@Transactional
public class DataListService {

	@Autowired
	private SystemDao systemDao;

	public List<Object[]> getXzData(String startDate, String endDate, String jirabh) {

		List<Object[]> objects = new ArrayList<Object[]>();
		String sql = "select w.timeworked/3600/8,a.id,a.summary,a.jirabh,w.author from worklog w left join (SELECT * FROM jiraissue j\n"
				+ "left join issue_bh b on j.ID = b.issueid\n" + "where jirabh in (" + jirabh
				+ ") and issuetype != 10000 \n" + "and resolution is null and assignee is null ) a\n"
				+ "on w.issueid = a.id\n" + "where a.id is not null  #and startdate like '%2020-09-30%';\n"
				+ "AND STR_TO_DATE(DATE_FORMAT(startDate, '%y-%m-%d'), '%Y-%m-%d') BETWEEN STR_TO_DATE('" + startDate
				+ "', '%Y-%m-%d') AND STR_TO_DATE('" + endDate + "', '%Y-%m-%d')\n" + "order by startDate";
		List<Object[]> bySql = systemDao.getBySql(sql);
		for (Object[] rs : bySql) {
			Object[] obj = new Object[5];
			obj[0] = rs[0] == null ? "" : rs[0];
			obj[1] = rs[1] == null ? "" : rs[1];
			obj[2] = rs[2] == null ? "" : rs[2];
			obj[3] = rs[3] == null ? "" : rs[3];
			obj[4] = rs[4] == null ? "" : rs[4];
			objects.add(obj);
		}

		return objects;
	}

	public Double getXHByUserAndData(String user, String startDate, String endDate, String ueid) {
		List list = new ArrayList();
		String sql_ = "";
		if (ueid != null && !ueid.equals("")) {
			sql_ = " and j.ID = " + ueid + " ";
		}
		String sql = "select if(sum(timeworked) is null,0,sum(timeworked)) from worklog where issueid in (\n"
				+ "SELECT t.issueid\n" + "FROM (\n" + "\tSELECT j.id AS issueid\n"
				+ "\tFROM cwd_user u, jiraissue j, project p\n" + "\tWHERE (j.assignee = u.user_name\n" + sql_
				+ "\t\tAND p.id = j.project\n" + "\t\tAND j.assignee IN (\n" + "\t\t\t'" + user + "'\n" + "\t\t))\n"
				+ ") t\n" + "\tLEFT JOIN issue_bh b ON t.issueid = b.issueid\n" + ")\n"
				+ "and STR_TO_DATE(DATE_FORMAT(startDate, '%y-%m-%d'),'%Y-%m-%d') BETWEEN STR_TO_DATE('" + startDate
				+ "','%Y-%m-%d') and STR_TO_DATE('" + endDate + "','%Y-%m-%d')";
		Double objects = 0d;
		Object singleResultBySql = systemDao.getSingleResultBySql(sql);
		if (singleResultBySql != null) {
			objects = Double.valueOf(String.valueOf(singleResultBySql));
		}
		return objects;
	}

	public Object[] getYSEndAndYSBegin(String user, String issueid, String startDate, String endDate) {
		List<JiraProduce> list = new ArrayList<JiraProduce>();
		Object[] objects = new Object[21];
		
		String sql = "select bghm.*,lned.* from \n" + "(\n"
				+ "select yg.ygfid as ygfids,yg.ygval as ygvals,yg.efid as efids,yg.eod as eods,yg.mainyg as mainygs,sy.ygfid1 as ygfid1s,sy.ygval1 as ygval1s,sy.efid1 as efid1s,sy.eod1 as eod1s,sy.mainsy as mainsy1,yg.id as id from \n"
				+ "(\n"
				+ "select main.id,endbefore.FIELD ygfid,endbefore.newvalue ygval,endafter.field efid,endafter.oldvalue eod,main.timeoriginalestimate mainyg from (\n"
				+ "select * from jiraissue where id = " + issueid + ") main\n" + "left join (\n"
				+ "SELECT t.*,c.AUTHOR,c.CREATED,c.issueid from changeitem  t left join changegroup c on t.groupid = c.id where #c.author='wangweidl' and\n"
				+ " t.FIELD = 'timeoriginalestimate' and c.issueid=" + issueid + " \n"
				+ "and  date_format(c.created, '%y-%m-%d')<= date_format('" + endDate
				+ "','%y-%m-%d')  order by c.CREATED desc limit 0,1\n" + ") endbefore\n"
				+ "on endbefore.issueid = main.id\n" + "left join (\n"
				+ "SELECT if(t.OLDVALUE is null,t.NEWVALUE,t.OLDVALUE) oldvalue,t.FIELD,c.AUTHOR,c.CREATED,c.issueid from changeitem  t left join changegroup c on t.groupid = c.id where #c.author='wangweidl' and\n"
				+ " t.FIELD = 'timeoriginalestimate' and c.issueid=" + issueid + " \n"
				+ " and  date_format(c.created, '%y-%m-%d')> date_format('" + endDate
				+ "','%y-%m-%d')  order by c.CREATED limit 0,1 \n" + ") endafter\n" + "on endafter.issueid = main.id\n"
				+ "where main.id = " + issueid + "\n" + ") yg join \n" + "(\n"
				+ "select main.id,endbefore.FIELD ygfid1,endbefore.newvalue ygval1,endafter.field efid1,endafter.oldvalue eod1,main.timeestimate mainsy from jiraissue main\n"
				+ "left join (\n"
				+ "SELECT t.*,c.AUTHOR,c.CREATED,c.issueid from changeitem  t left join changegroup c on t.groupid = c.id where #c.author='wangweidl' and\n"
				+ " t.FIELD = 'timeestimate' AND c.issueid=" + issueid + " \n"
				+ " and  date_format(c.created, '%y-%m-%d')<= date_format('" + endDate
				+ "','%y-%m-%d')  order by c.CREATED desc limit 0,1\n" + ") endbefore\n"
				+ "on endbefore.issueid = main.id\n" + "left join (\n"
				+ "SELECT if(t.OLDVALUE is null,t.NEWVALUE,t.OLDVALUE) oldvalue,t.FIELD,c.AUTHOR,c.CREATED,c.issueid from changeitem  t left join changegroup c on t.groupid = c.id where #c.author='wangweidl' and\n"
				+ " t.FIELD = 'timeestimate' AND c.issueid=" + issueid + "\n"
				+ " and  date_format(c.created, '%y-%m-%d')> date_format('" + endDate
				+ "','%y-%m-%d')  order by c.CREATED limit 0,1 \n" + ") endafter\n" + "on endafter.issueid = main.id\n"
				+ ") sy\n" + "where yg.id=sy.id\n" + ") bghm\n" + "join (\n"
				+ "select yg.ygfid,yg.ygval,yg.efid,yg.eod,yg.mainyg,sy.ygfid1,sy.ygval1,sy.efid1,sy.eod1,sy.mainsy,yg.id from \n"
				+ "(\n"
				+ "select main.id,endbefore.FIELD ygfid,endbefore.newvalue ygval,endafter.field efid,endafter.oldvalue eod,main.timeoriginalestimate mainyg from (\n"
				+ "select * from jiraissue where id = " + issueid + ") main\n" + "left join (\n"
				+ "SELECT t.*,c.AUTHOR,c.CREATED,c.issueid from changeitem  t left join changegroup c on t.groupid = c.id where #c.author='wangweidl' and\n"
				+ " t.FIELD = 'timeoriginalestimate' and c.issueid=" + issueid + " \n"
				+ "and  date_format(c.created, '%y-%m-%d')<= date_format('" + startDate
				+ "','%y-%m-%d')  order by c.CREATED desc limit 0,1\n" + ") endbefore\n"
				+ "on endbefore.issueid = main.id\n" + "left join (\n"
				+ "SELECT if(t.OLDVALUE is null,t.NEWVALUE,t.OLDVALUE) oldvalue,t.FIELD,c.AUTHOR,c.CREATED,c.issueid from changeitem  t left join changegroup c on t.groupid = c.id where #c.author='wangweidl' and\n"
				+ " t.FIELD = 'timeoriginalestimate' AND c.issueid=" + issueid + "\n"
				+ " and  date_format(c.created, '%y-%m-%d')> date_format('" + startDate
				+ "','%y-%m-%d')  order by c.CREATED limit 0,1 \n" + ") endafter\n" + "on endafter.issueid = main.id\n"
				+ ") yg join \n" + "(\n"
				+ "select main.id,endbefore.FIELD ygfid1,endbefore.newvalue ygval1,endafter.field efid1,endafter.oldvalue eod1,main.timeestimate mainsy from jiraissue main\n"
				+ "left join (\n"
				+ "SELECT t.*,c.AUTHOR,c.CREATED,c.issueid from changeitem  t left join changegroup c on t.groupid = c.id where #c.author='wangweidl' and\n"
				+ " t.FIELD = 'timeestimate' AND c.issueid=" + issueid + " \n"
				+ " and  date_format(c.created, '%y-%m-%d')<= date_format('" + startDate
				+ "','%y-%m-%d')  order by c.CREATED desc limit 0,1\n" + ") endbefore\n"
				+ "on endbefore.issueid = main.id\n" + "left join (\n"
				+ "SELECT if(t.OLDVALUE is null,t.NEWVALUE,t.OLDVALUE) oldvalue,t.FIELD,c.AUTHOR,c.CREATED,c.issueid from changeitem  t left join changegroup c on t.groupid = c.id where #c.author='wangweidl' and\n"
				+ " t.FIELD = 'timeestimate' AND c.issueid=" + issueid + "\n"
				+ " and  date_format(c.created, '%y-%m-%d')> date_format('" + startDate
				+ "','%y-%m-%d')  order by c.CREATED limit 0,1 \n" + ") endafter\n" + "on endafter.issueid = main.id\n"
				+ "where main.id=" + issueid + "\n" + ") sy\n" + "where yg.id=sy.id\n" + ") lned\n"
				+ "on bghm.id=lned.id";
		List<Object[]> lis = systemDao.getBySql(sql);
		
		for (Object[] rs : lis) {
			objects[0]=rs[0]==null?"":rs[0];
            objects[1]=rs[1]==null?"0":rs[1];
            objects[2]=rs[2]==null?"":rs[2];
            objects[3]=rs[3]==null?"0":rs[3];
            objects[4]=rs[4]==null?"0":rs[4];
            objects[5]=rs[5]==null?"":rs[5];
            objects[6]=rs[6]==null?"0":rs[6];
            objects[7]=rs[7]==null?"":rs[7];
            objects[8]=rs[8]==null?"0":rs[8];
            objects[9]=rs[9]==null?"0":rs[9];
            objects[10]=rs[11]==null?"":rs[11];
            objects[11]=rs[12]==null?"0":rs[12];
            objects[12]=rs[13]==null?"":rs[13];
            objects[13]=rs[14]==null?"0":rs[14];
            objects[14]=rs[15]==null?"0":rs[15];
            objects[15]=rs[16]==null?"":rs[16];
            objects[16]=rs[17]==null?"0":rs[17];
            objects[17]=rs[18]==null?"":rs[18];
            objects[18]=rs[19]==null?"0":rs[19];
            objects[19]=rs[20]==null?"0":rs[20];
            objects[20]=rs[10]==null?"0":rs[10];
		}
		return objects;
	}
	
	public List<Object[]> getSuidByJiraBh(String jirabh) {
//		String sql = "select id,issuetype,summary as a from jiraissue where id in (select issueid from issue_bh where jirabh in ("+jirabh+"))";
		String sql = "select id,mc.mc from jiraissue j left join issue_bhandmc mc on j.id = mc.issueid where id in (select issueid from issue_bh where jirabh in ("+jirabh+")) and issuetype != 10000";
		return systemDao.getBySql(sql);
	}
	
	/**
	 * 开局先放redis
	 * */
	public List<Object[]> getViewJiraBh() {
		String sql = "select v.* from issue_bhandmc v left join jiraissue je on je.id = v.issueid where je.issuetype != 10000";
		return systemDao.getBySql(sql);
	}
	
	public List<Object[]> getJiraissueByJiraBh(String issueid) {
		String sql = "SELECT t.display_name, t.assignee, t.pname, t.summary, t.pkey\r\n" + 
				"	, b.jirabh, t.issueid\r\n" + 
				"FROM (\r\n" + 
				"	SELECT u.display_name, j.assignee, p.pname, j.summary\r\n" + 
				"		, CONCAT(p.pkey, '-', j.issuenum) AS pkey\r\n" + 
				"		, j.id AS issueid\r\n" + 
				"	FROM cwd_user u, jiraissue j, project p\r\n" + 
				"	WHERE j.assignee = u.user_name\r\n" + 
				"		AND p.id = j.project\r\n" + 
				") t\r\n" + 
				"	LEFT JOIN issue_bh b ON t.issueid = b.issueid\r\n" + 
				"WHERE t.issueid in ("+issueid+")";
		return systemDao.getBySql(sql);
	}
	
	public Double getFactByOrderMonth(String sueids,String startDate,String endDate) {
		String sql = "SELECT if(sum(timeworked) is null,0,sum(timeworked))/3600/8 as num FROM worklog where issueid \r\n" + 
				"in ("+sueids+")\r\n" + 
				"and STR_TO_DATE(DATE_FORMAT(startDate, '%y-%m-%d'),'%Y-%m-%d') BETWEEN STR_TO_DATE('"+startDate+"','%Y-%m-%d') and STR_TO_DATE('"+endDate+"','%Y-%m-%d')";
		Object singleResultBySql = systemDao.getSingleResultBySql(sql);
		Double face = 0d;
		if(singleResultBySql != null) {
			face = Double.valueOf(String.valueOf(singleResultBySql));
		}
		return face;
	}
	
}
