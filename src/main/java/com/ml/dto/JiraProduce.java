package com.ml.dto;

public class JiraProduce {

	/**  issueid */
    private Integer issueid;
    /**  jira 编号  */
    private String jirabh;
    /** 预估时间 */
    private Double ygsj;
    /** 剩余时间 */
    private Double sysj;
    /** 实际工作时间  【 消耗 = 实际工作时间 】 */
    private Double sjgzsj;
    /** jira用户名字  */
    private String  jirauser;
    /** 用户名字  */
    private String  username;

    /** 产出 = 预估时间 - 剩余 */
    private Double produce;

    /** 效率 = 产出 / 实际工作时间  ? 不应该是 预估 / 实际吗   */
    private Double efficiency;

    private String  efficiencyPercent;

    /** 这些字段 用来产看详情  start */
    private String pname;
    private String issueSummary;
    private String pKey;
    private String changeContent;
    private String created;
    /** 这些字段 用来产看详情 end */

    /** 闲置时长 */
    private Double xz;
    /** 闲置时长 */
    private String xzxl;
    
    private String epicname;
	public Integer getIssueid() {
		return issueid;
	}
	public void setIssueid(Integer issueid) {
		this.issueid = issueid;
	}
	public String getJirabh() {
		return jirabh;
	}
	public void setJirabh(String jirabh) {
		this.jirabh = jirabh;
	}
	public Double getYgsj() {
		return ygsj;
	}
	public void setYgsj(Double ygsj) {
		this.ygsj = ygsj;
	}
	public Double getSysj() {
		return sysj;
	}
	public void setSysj(Double sysj) {
		this.sysj = sysj;
	}
	public Double getSjgzsj() {
		return sjgzsj;
	}
	public void setSjgzsj(Double sjgzsj) {
		this.sjgzsj = sjgzsj;
	}
	public String getJirauser() {
		return jirauser;
	}
	public void setJirauser(String jirauser) {
		this.jirauser = jirauser;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Double getProduce() {
		return produce;
	}
	public void setProduce(Double produce) {
		this.produce = produce;
	}
	public Double getEfficiency() {
		return efficiency;
	}
	public void setEfficiency(Double efficiency) {
		this.efficiency = efficiency;
	}
	public String getEfficiencyPercent() {
		return efficiencyPercent;
	}
	public void setEfficiencyPercent(String efficiencyPercent) {
		this.efficiencyPercent = efficiencyPercent;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public String getIssueSummary() {
		return issueSummary;
	}
	public void setIssueSummary(String issueSummary) {
		this.issueSummary = issueSummary;
	}
	public String getpKey() {
		return pKey;
	}
	public void setpKey(String pKey) {
		this.pKey = pKey;
	}
	public String getChangeContent() {
		return changeContent;
	}
	public void setChangeContent(String changeContent) {
		this.changeContent = changeContent;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public Double getXz() {
		return xz;
	}
	public void setXz(Double xz) {
		this.xz = xz;
	}
	public String getEpicname() {
		return epicname;
	}
	public void setEpicname(String epicname) {
		this.epicname = epicname;
	}
	public String getXzxl() {
		return xzxl;
	}
	public void setXzxl(String xzxl) {
		this.xzxl = xzxl;
	}
	
}
