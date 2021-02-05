package com.jihun.study.openDartApi.entity.stock;

import com.jihun.study.openDartApi.dto.stock.DartDto;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class CorpDetailPK implements Serializable, DartDto {
    private String  corpCode;
    private int     bsnsYear;
    private String  reprtCode;

    public CorpDetailPK() {
    }

    public CorpDetailPK(String corpCode, int bsnsYear, String reprtCode) {
        this.corpCode = corpCode;
        this.bsnsYear = bsnsYear;
        this.reprtCode = reprtCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CorpDetailPK that = (CorpDetailPK) o;

        if (bsnsYear != that.bsnsYear) return false;
        if (!corpCode.equals(that.corpCode)) return false;
        return reprtCode.equals(that.reprtCode);
    }

    @Override
    public int hashCode() {
        int result = corpCode.hashCode();
        result = 31 * result + bsnsYear;
        result = 31 * result + reprtCode.hashCode();
        return result;
    }

    public String getCorpCode() {
        return corpCode;
    }

    public void setCorpCode(String corpCode) {
        this.corpCode = corpCode;
    }

    public int getBsnsYear() {
        return bsnsYear;
    }

    public void setBsnsYear(int bsnsYear) {
        this.bsnsYear = bsnsYear;
    }

    public String getReprtCode() {
        return reprtCode;
    }

    public void setReprtCode(String reprtCode) {
        this.reprtCode = reprtCode;
    }

    @Override
    public String toString() {
        return "CorpDetailPK{" +
                "corpCode='" + corpCode + '\'' +
                ", bsnsYear=" + bsnsYear +
                ", reprtCode='" + reprtCode + '\'' +
                '}';
    }
}
