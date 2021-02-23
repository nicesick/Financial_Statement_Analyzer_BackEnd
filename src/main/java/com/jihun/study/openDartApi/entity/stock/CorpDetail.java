package com.jihun.study.openDartApi.entity.stock;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.jihun.study.openDartApi.entity.stock.pk.CorpDetailPK;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;

@JsonAutoDetect(
          fieldVisibility   = JsonAutoDetect.Visibility.ANY
        , getterVisibility  = JsonAutoDetect.Visibility.NONE
        , setterVisibility  = JsonAutoDetect.Visibility.NONE
        , creatorVisibility = JsonAutoDetect.Visibility.NONE
)
@Entity
public class CorpDetail implements Serializable {
    @EmbeddedId
    @JsonUnwrapped
    private CorpDetailPK    corpDetailPK;
    @JsonProperty("thstrm_dt")
    private String          thstrmDt;
    @JsonProperty("rcept_no")
    private String          rceptNo;

    @JsonProperty("tot_assets")
    private String          totAssets;
    @JsonProperty("tot_liability")
    private String          totLiability;
    @JsonProperty("tot_stockholders_equity")
    private String          totStockholdersEquity;
    @JsonProperty("stockholders_equity")
    private String          stockholdersEquity;

    @JsonProperty("revenue")
    private String          revenue;
    @JsonProperty("operating_income")
    private String          operatingIncome;
    @JsonProperty("income_before_tax")
    private String          incomeBeforeTax;
    @JsonProperty("net_income")
    private String          netIncome;

//    @ManyToOne
//    @JoinColumn(name = "corpCode", insertable = false, updatable = false)
//    private Corporation corporation;

    public CorpDetail() {
    }

    public CorpDetail(String corpCode, int bsnsYear, String reprtCode, String thstrmDt, String rceptNo) {
        corpDetailPK    = new CorpDetailPK(corpCode, bsnsYear, reprtCode);
        this.thstrmDt   = thstrmDt;
        this.rceptNo    = rceptNo;
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

    public String getRceptNo() {
        return rceptNo;
    }

    public void setRceptNo(String rceptNo) {
        this.rceptNo = rceptNo;
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

    @Override
    public String toString() {
        return "CorpDetail{" +
                "corpDetailPK=" + corpDetailPK +
                ", thstrmDt='" + thstrmDt + '\'' +
                ", rceptNo='" + rceptNo + '\'' +
                ", totAssets='" + totAssets + '\'' +
                ", totLiability='" + totLiability + '\'' +
                ", totStockholdersEquity='" + totStockholdersEquity + '\'' +
                ", stockholdersEquity='" + stockholdersEquity + '\'' +
                ", revenue='" + revenue + '\'' +
                ", operatingIncome='" + operatingIncome + '\'' +
                ", incomeBeforeTax='" + incomeBeforeTax + '\'' +
                ", netIncome='" + netIncome + '\'' +
                '}';
    }
}
