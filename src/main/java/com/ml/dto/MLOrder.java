package com.ml.dto;

import javax.persistence.*;

public class MLOrder {
    private Integer id ;

    /**order代码*/
    private String orderdm;
    /**order名称*/
    private String ordermc;
    /**所属项目*/
    private String ssxm;
    /**项目代码*/
    private String xmdm;

    /** order 人员 */
    private String orderry;

    /** order 颜色 */
    private String orderys;


    /** order 制作人 zzr */
    private String zzr;

    /** order 制作人 助理 zzrzl */
    private String zzrzl;

    /** order 项目经理  xmjl */
    private String xmjl;

    /** order 制作区域  zzqy  1 上海 2 天津 3 大连 */
    private String zzqy;


    /**项目名称 order名称*/
    private String xmmcordermc;

    /** formmodeid 21 */
    private Integer formmodeid;
    /** order 顺序号  */
    private String orderbbsxh;

    /**order 状态
     * 0 立项审批 1 正常  2 完成   3  终止
     * */
    private Integer orderzt;

    /** 客户 */
    private String kh;

    /** 和 jira 系统 的关联  */
    private String jirabh;

    /**是否是默认 order 0 是 1 否 */
    private Integer mrorder;


    /**
     * 临时变量
     * */
    private String temp;

    private Double ygcount;//预估时间合计
    private Double produce;//产出合计
    private Double fact;//实际工作合计
    private Double sy;//剩余时间合计
    private String progress;//进度（产出合计/预估时间合计）
    private String effic;//平均效率（产出合计/实际工作合计）

    private String xmmc;//项目名称
    private String orderzzrp;//order制作人
    private String orderfzz;//order负责人

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "orderdm")
    public String getOrderdm() {
        return orderdm;
    }

    public void setOrderdm(String orderdm) {
        this.orderdm = orderdm;
    }

    @Column(name = "ordermc")
    public String getOrdermc() {
        return ordermc;
    }

    public void setOrdermc(String ordermc) {
        this.ordermc = ordermc;
    }

    @Column(name = "ssxm")
    public String getSsxm() {
        return ssxm;
    }

    public void setSsxm(String ssxm) {
        this.ssxm = ssxm;
    }

    @Column(name = "orderry")
    public String getOrderry() {
        return orderry;
    }

    public void setOrderry(String orderry) {
        this.orderry = orderry;
    }

    @Column(name = "orderzt")
    public Integer getOrderzt() {
        return orderzt;
    }

    public void setOrderzt(Integer orderzt) {
        this.orderzt = orderzt;
    }

    @Column(name = "zzr")
    public String getZzr() {
        return zzr;
    }

    public void setZzr(String zzr) {
        this.zzr = zzr;
    }

    @Column(name = "zzrzl")
    public String getZzrzl() {
        return zzrzl;
    }

    public void setZzrzl(String zzrzl) {
        this.zzrzl = zzrzl;
    }

    @Column(name = "xmjl")
    public String getXmjl() {
        return xmjl;
    }

    public void setXmjl(String xmjl) {
        this.xmjl = xmjl;
    }

    @Column(name = "zzqy")
    public String getZzqy() {
        return zzqy;
    }

    public void setZzqy(String zzqy) {
        this.zzqy = zzqy;
    }

    @Column(name = "xmdm")
    public String getXmdm() {
        return xmdm;
    }

    public void setXmdm(String xmdm) {
        this.xmdm = xmdm;
    }

    @Column(name = "orderys")
    public String getOrderys() {
        return orderys;
    }

    public void setOrderys(String orderys) {
        this.orderys = orderys;
    }



    @Column(name = "xmmcordermc")
    public String getXmmcordermc() {
        return xmmcordermc;
    }

    public void setXmmcordermc(String xmmcordermc) {
        this.xmmcordermc = xmmcordermc;
    }


    public String getOrderbbsxh() {
        return orderbbsxh;
    }

    @Column(name = "orderbbsxh")
    public void setOrderbbsxh(String orderbbsxh) {
        this.orderbbsxh = orderbbsxh;
    }

    @Column(name = "mrorder")
    public Integer getMrorder() {
        return mrorder;
    }

    public void setMrorder(Integer mrorder) {
        this.mrorder = mrorder;
    }

    @Column(name = "jirabh")
    public String getJirabh() {
        return jirabh;
    }

    public void setJirabh(String jirabh) {
        this.jirabh = jirabh;
    }


    @Column(name = "kh")
    public String getKh() {
        return kh;
    }

    public void setKh(String kh) {
        this.kh = kh;
    }

    @Column(name = "formmodeid")
    public Integer getFormmodeid() {
        return formmodeid;
    }

    public void setFormmodeid(Integer formmodeid) {
        this.formmodeid = formmodeid;
    }

    @Transient
    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    @Transient
    public Double getYgcount() {
        return ygcount;
    }

    public void setYgcount(Double ygcount) {
        this.ygcount = ygcount;
    }
    @Transient
    public Double getProduce() {
        return produce;
    }

    public void setProduce(Double produce) {
        this.produce = produce;
    }
    @Transient
    public Double getFact() {
        return fact;
    }

    public void setFact(Double fact) {
        this.fact = fact;
    }
    @Transient
    public Double getSy() {
        return sy;
    }

    public void setSy(Double sy) {
        this.sy = sy;
    }
    @Transient
    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }
    @Transient
    public String getEffic() {
        return effic;
    }

    public void setEffic(String effic) {
        this.effic = effic;
    }
    @Transient
    public String getXmmc() {
        return xmmc;
    }

    public void setXmmc(String xmmc) {
        this.xmmc = xmmc;
    }
    @Transient
    public String getOrderzzrp() {
        return orderzzrp;
    }

    public void setOrderzzrp(String orderzzrp) {
        this.orderzzrp = orderzzrp;
    }
    @Transient
    public String getOrderfzz() {
        return orderfzz;
    }

    public void setOrderfzz(String orderfzz) {
        this.orderfzz = orderfzz;
    }
}
