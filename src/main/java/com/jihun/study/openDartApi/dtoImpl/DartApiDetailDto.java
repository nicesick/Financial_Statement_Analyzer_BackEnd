package com.jihun.study.openDartApi.dtoImpl;

import java.io.Serializable;

public class DartApiDetailDto implements Serializable {
    private String  rcept_no;
    private int     bsns_year;
    private String  corp_code;
    private String  stock_code;
    private String  reprt_code;
    private String  account_nm;
    private String  fs_div;
    private String  fs_nm;
    private String  sj_div;
    private String  sj_nm;
    private String  thstrm_nm;
    private String  thstrm_dt;
    private String  thstrm_amount;
    private String  thstrm_add_amount;
    private String  frmtrm_nm;
    private String  frmtrm_dt;
    private String  frmtrm_amount;
    private String  frmtrm_add_amount;
    private String  bfefrmtrm_nm;
    private String  bfefrmtrm_dt;
    private String  bfefrmtrm_amount;
    private String  ord;

    public DartApiDetailDto() {
    }

    public String getRcept_no() {
        return rcept_no;
    }

    public void setRcept_no(String rcept_no) {
        this.rcept_no = rcept_no;
    }

    public int getBsns_year() {
        return bsns_year;
    }

    public void setBsns_year(int bsns_year) {
        this.bsns_year = bsns_year;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public String getStock_code() {
        return stock_code;
    }

    public void setStock_code(String stock_code) {
        this.stock_code = stock_code;
    }

    public String getReprt_code() {
        return reprt_code;
    }

    public void setReprt_code(String reprt_code) {
        this.reprt_code = reprt_code;
    }

    public String getAccount_nm() {
        return account_nm;
    }

    public void setAccount_nm(String account_nm) {
        this.account_nm = account_nm;
    }

    public String getFs_div() {
        return fs_div;
    }

    public void setFs_div(String fs_div) {
        this.fs_div = fs_div;
    }

    public String getFs_nm() {
        return fs_nm;
    }

    public void setFs_nm(String fs_nm) {
        this.fs_nm = fs_nm;
    }

    public String getSj_div() {
        return sj_div;
    }

    public void setSj_div(String sj_div) {
        this.sj_div = sj_div;
    }

    public String getSj_nm() {
        return sj_nm;
    }

    public void setSj_nm(String sj_nm) {
        this.sj_nm = sj_nm;
    }

    public String getThstrm_nm() {
        return thstrm_nm;
    }

    public void setThstrm_nm(String thstrm_nm) {
        this.thstrm_nm = thstrm_nm;
    }

    public String getThstrm_dt() {
        return thstrm_dt;
    }

    public void setThstrm_dt(String thstrm_dt) {
        this.thstrm_dt = thstrm_dt;
    }

    public String getThstrm_amount() {
        return thstrm_amount;
    }

    public void setThstrm_amount(String thstrm_amount) {
        this.thstrm_amount = thstrm_amount;
    }

    public String getThstrm_add_amount() {
        return thstrm_add_amount;
    }

    public void setThstrm_add_amount(String thstrm_add_amount) {
        this.thstrm_add_amount = thstrm_add_amount;
    }

    public String getFrmtrm_nm() {
        return frmtrm_nm;
    }

    public void setFrmtrm_nm(String frmtrm_nm) {
        this.frmtrm_nm = frmtrm_nm;
    }

    public String getFrmtrm_dt() {
        return frmtrm_dt;
    }

    public void setFrmtrm_dt(String frmtrm_dt) {
        this.frmtrm_dt = frmtrm_dt;
    }

    public String getFrmtrm_amount() {
        return frmtrm_amount;
    }

    public void setFrmtrm_amount(String frmtrm_amount) {
        this.frmtrm_amount = frmtrm_amount;
    }

    public String getFrmtrm_add_amount() {
        return frmtrm_add_amount;
    }

    public void setFrmtrm_add_amount(String frmtrm_add_amount) {
        this.frmtrm_add_amount = frmtrm_add_amount;
    }

    public String getBfefrmtrm_nm() {
        return bfefrmtrm_nm;
    }

    public void setBfefrmtrm_nm(String bfefrmtrm_nm) {
        this.bfefrmtrm_nm = bfefrmtrm_nm;
    }

    public String getBfefrmtrm_dt() {
        return bfefrmtrm_dt;
    }

    public void setBfefrmtrm_dt(String bfefrmtrm_dt) {
        this.bfefrmtrm_dt = bfefrmtrm_dt;
    }

    public String getBfefrmtrm_amount() {
        return bfefrmtrm_amount;
    }

    public void setBfefrmtrm_amount(String bfefrmtrm_amount) {
        this.bfefrmtrm_amount = bfefrmtrm_amount;
    }

    public String getOrd() {
        return ord;
    }

    public void setOrd(String ord) {
        this.ord = ord;
    }

    @Override
    public String toString() {
        return "DartDetailDto{" +
                "rcept_no='" + rcept_no + '\'' +
                ", bsns_year=" + bsns_year +
                ", corp_code='" + corp_code + '\'' +
                ", stock_code='" + stock_code + '\'' +
                ", reprt_code='" + reprt_code + '\'' +
                ", account_nm='" + account_nm + '\'' +
                ", fs_div='" + fs_div + '\'' +
                ", fs_nm='" + fs_nm + '\'' +
                ", sj_div='" + sj_div + '\'' +
                ", sj_nm='" + sj_nm + '\'' +
                ", thstrm_nm='" + thstrm_nm + '\'' +
                ", thstrm_dt='" + thstrm_dt + '\'' +
                ", thstrm_amount='" + thstrm_amount + '\'' +
                ", thstrm_add_amount='" + thstrm_add_amount + '\'' +
                ", frmtrm_nm='" + frmtrm_nm + '\'' +
                ", frmtrm_dt='" + frmtrm_dt + '\'' +
                ", frmtrm_amount='" + frmtrm_amount + '\'' +
                ", frmtrm_add_amount='" + frmtrm_add_amount + '\'' +
                ", bfefrmtrm_nm='" + bfefrmtrm_nm + '\'' +
                ", bfefrmtrm_dt='" + bfefrmtrm_dt + '\'' +
                ", bfefrmtrm_amount='" + bfefrmtrm_amount + '\'' +
                ", ord='" + ord + '\'' +
                '}';
    }
}
