package com.jihun.study.openDartApi.entity.stock;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jihun.study.openDartApi.dto.api.DartApiDto;
import com.jihun.study.openDartApi.dto.stock.DartDto;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Corporation implements Serializable, DartDto, DartApiDto {
    @Transient
    private String status;
    @Transient
    private String message;

    @Id
    @JsonProperty("corp_code")
    private String corpCode;
    @Column(nullable = false)
    @JsonProperty("corp_name")
    private String corpName;
    @JsonProperty("corp_name_eng")
    private String corpNameEng;

    @JsonProperty("stock_code")
    private String stockCode;
    @JsonProperty("stock_name")
    private String stockName;

    @Column(nullable = false)
    @JsonProperty("corp_cls")
    private char corpCls;

    @JsonProperty("ceo_nm")
    private String ceoNm;
    @JsonProperty("jurir_no")
    private String jurirNo;
    @JsonProperty("bizr_no")
    private String bizrNo;
    @JsonProperty("adres")
    private String adres;
    @JsonProperty("hm_url")
    private String hmUrl;
    @JsonProperty("ir_url")
    private String irUrl;
    @JsonProperty("phn_no")
    private String phnNo;
    @JsonProperty("fax_no")
    private String faxNo;
    @JsonProperty("induty_code")
    private String indutyCode;
    @JsonProperty("est_dt")
    private String estDt;
    @JsonProperty("acc_mt")
    private String accMt;

    private boolean isRevenueLack;
    private boolean isEquityImpairment;
    private boolean isOperatingLoss;
    private boolean isLossBeforeTax;

    @Formula("is_revenue_lack | is_equity_impairment | is_operating_loss | is_loss_before_tax")
    private boolean isIssued;
    private boolean isEvalDone = true;

    @OneToMany(
            fetch = FetchType.LAZY
            , cascade = CascadeType.ALL
            , mappedBy = "corpDetailPK.corpCode"
    )
    private List<CorpDetail> corpDetails = new ArrayList<>();

    public Corporation() {
    }

    public Corporation(String corpCode, String corpName) {
        this.corpCode = corpCode;
        this.corpName = corpName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Corporation that = (Corporation) o;

        return corpCode.equals(that.corpCode);
    }

    @Override
    public int hashCode() {
        return corpCode.hashCode();
    }

    public void addCorpDetail(CorpDetail corpDetail) {
        this.corpDetails.add(corpDetail);
    }

    public void addCorpDetails(List<CorpDetail> corpDetails) {
        for (CorpDetail corpDetail : corpDetails) {
            this.corpDetails.add(corpDetail);

//            if (corpDetail.getCorporation() != this) {
//                corpDetail.setCorporation(this);
//            }
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCorpCode() {
        return corpCode;
    }

    public void setCorpCode(String corpCode) {
        this.corpCode = corpCode;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public String getCorpNameEng() {
        return corpNameEng;
    }

    public void setCorpNameEng(String corpNameEng) {
        this.corpNameEng = corpNameEng;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getCeoNm() {
        return ceoNm;
    }

    public void setCeoNm(String ceoNm) {
        this.ceoNm = ceoNm;
    }

    public char getCorpCls() {
        return corpCls;
    }

    public void setCorpCls(char corpCls) {
        this.corpCls = corpCls;
    }

    public String getJurirNo() {
        return jurirNo;
    }

    public void setJurirNo(String jurirNo) {
        this.jurirNo = jurirNo;
    }

    public String getBizrNo() {
        return bizrNo;
    }

    public void setBizrNo(String bizrNo) {
        this.bizrNo = bizrNo;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public String getHmUrl() {
        return hmUrl;
    }

    public void setHmUrl(String hmUrl) {
        this.hmUrl = hmUrl;
    }

    public String getIrUrl() {
        return irUrl;
    }

    public void setIrUrl(String irUrl) {
        this.irUrl = irUrl;
    }

    public String getPhnNo() {
        return phnNo;
    }

    public void setPhnNo(String phnNo) {
        this.phnNo = phnNo;
    }

    public String getFaxNo() {
        return faxNo;
    }

    public void setFaxNo(String faxNo) {
        this.faxNo = faxNo;
    }

    public String getIndutyCode() {
        return indutyCode;
    }

    public void setIndutyCode(String indutyCode) {
        this.indutyCode = indutyCode;
    }

    public String getEstDt() {
        return estDt;
    }

    public void setEstDt(String estDt) {
        this.estDt = estDt;
    }

    public String getAccMt() {
        return accMt;
    }

    public void setAccMt(String accMt) {
        this.accMt = accMt;
    }

    public boolean isRevenueLack() {
        return isRevenueLack;
    }

    public void setRevenueLack(boolean revenueLack) {
        this.isRevenueLack = revenueLack;
    }

    public boolean isOperatingLoss() {
        return isOperatingLoss;
    }

    public void setOperatingLoss(boolean operatingLoss) {
        this.isOperatingLoss = operatingLoss;
    }

    public boolean isLossBeforeTax() {
        return isLossBeforeTax;
    }

    public void setLossBeforeTax(boolean lossBeforeTax) {
        this.isLossBeforeTax = lossBeforeTax;
    }

    public boolean isEquityImpairment() {
        return isEquityImpairment;
    }

    public void setEquityImpairment(boolean equityImpairment) {
        this.isEquityImpairment = equityImpairment;
    }

    public boolean isIssued() {
        return isIssued;
    }

    public void setIssued(boolean issued) {
        isIssued = issued;
    }

    public boolean isEvalDone() {
        return isEvalDone;
    }

    public void setEvalDone(boolean evalDone) {
        this.isEvalDone = evalDone;
    }

    public List<CorpDetail> getCorpDetails() {
        return corpDetails;
    }

    public void setCorpDetails(List<CorpDetail> corpDetails) {
        this.corpDetails = corpDetails;
    }

    @Override
    public String toString() {
        return "Corporation{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", corpCode='" + corpCode + '\'' +
                ", corpName='" + corpName + '\'' +
                ", corpNameEng='" + corpNameEng + '\'' +
                ", stockCode='" + stockCode + '\'' +
                ", stockName='" + stockName + '\'' +
                ", corpCls=" + corpCls +
                ", ceoNm='" + ceoNm + '\'' +
                ", jurirNo='" + jurirNo + '\'' +
                ", bizrNo='" + bizrNo + '\'' +
                ", adres='" + adres + '\'' +
                ", hmUrl='" + hmUrl + '\'' +
                ", irUrl='" + irUrl + '\'' +
                ", phnNo='" + phnNo + '\'' +
                ", faxNo='" + faxNo + '\'' +
                ", indutyCode='" + indutyCode + '\'' +
                ", estDt='" + estDt + '\'' +
                ", accMt='" + accMt + '\'' +
                ", isRevenueLack=" + isRevenueLack +
                ", isEquityImpairment=" + isEquityImpairment +
                ", isOperatingLoss=" + isOperatingLoss +
                ", isLossBeforeTax=" + isLossBeforeTax +
                ", isIssued=" + isIssued +
                ", isEvalDone=" + isEvalDone +
                ", corpDetails=" + corpDetails +
                '}';
    }
}
