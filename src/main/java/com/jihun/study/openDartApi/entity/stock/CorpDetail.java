package com.jihun.study.openDartApi.entity.stock;

import com.jihun.study.openDartApi.dto.stock.DartDto;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class CorpDetail implements Serializable, DartDto {
    @EmbeddedId
    private CorpDetailPK    corpDetailPK;
    private String          thstrmDt;

    private String          totAssets;
    private String          totLiability;
    private String          totStockholdersEquity;

    private String          stockholdersEquity;

    private String          revenue;
    private String          operatingIncome;
    private String          incomeBeforeTax;
    private String          netIncome;

//    @ManyToOne
//    @JoinColumn(name = "corpCode", insertable = false, updatable = false)
//    private Corporation corporation;

    public CorpDetail() {
    }

    public CorpDetail(String corpCode, int bsnsYear, String reprtCode, String thstrmDt) {
        corpDetailPK    = new CorpDetailPK(corpCode, bsnsYear, reprtCode);
        this.thstrmDt   = thstrmDt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CorpDetail that = (CorpDetail) o;

        return corpDetailPK.equals(that.corpDetailPK);
    }

    @Override
    public int hashCode() {
        return corpDetailPK.hashCode();
    }

//    public Corporation getCorporation() {
//        return corporation;
//    }
//
//    public void setCorporation(Corporation corporation) {
//        this.corporation = corporation;
//
//        if (corporation.getCorpDetails().contains(this)) {
//            corporation.getCorpDetails().add(this);
//        }
//    }

    public String getCorpCode() {
        return this.corpDetailPK.getCorpCode();
    }

    public void setCorpCode(String corpCode) {
        this.corpDetailPK.setCorpCode(corpCode);
    }

    public int getBsnsYear() {
        return this.corpDetailPK.getBsnsYear();
    }

    public void setBsnsYear(int bsnsYear) {
        this.corpDetailPK.setBsnsYear(bsnsYear);
    }

    public String getReprtCode() {
        return this.corpDetailPK.getReprtCode();
    }

    public void setReprtCode(String reprtCode) {
        this.corpDetailPK.setReprtCode(reprtCode);
    }

    public String getThstrmDt() {
        return thstrmDt;
    }

    public void setThstrmDt(String thstrmDt) {
        this.thstrmDt = thstrmDt;
    }

    public String getTotAssets() {
        return totAssets;
    }

    public void setTotAssets(String totAssets) {
        this.totAssets = totAssets;
    }

    public String getTotStockholdersEquity() {
        return totStockholdersEquity;
    }

    public void setTotStockholdersEquity(String totStockholdersEquity) {
        this.totStockholdersEquity = totStockholdersEquity;
    }

    public String getTotLiability() {
        return totLiability;
    }

    public void setTotLiability(String totLiability) {
        this.totLiability = totLiability;
    }

    public String getStockholdersEquity() {
        return stockholdersEquity;
    }

    public void setStockholdersEquity(String stockholdersEquity) {
        this.stockholdersEquity = stockholdersEquity;
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public String getOperatingIncome() {
        return operatingIncome;
    }

    public void setOperatingIncome(String operatingIncome) {
        this.operatingIncome = operatingIncome;
    }

    public String getIncomeBeforeTax() {
        return incomeBeforeTax;
    }

    public void setIncomeBeforeTax(String incomeBeforeTax) {
        this.incomeBeforeTax = incomeBeforeTax;
    }

    public String getNetIncome() {
        return netIncome;
    }

    public void setNetIncome(String netIncome) {
        this.netIncome = netIncome;
    }
}
