package com.jihun.study.openDartApi.dtoImpl.stock;

import com.jihun.study.openDartApi.dto.stock.DartDto;

import java.io.Serializable;

public class DartCorpDto implements Serializable, DartDto {
    private String  corpCode;
    private String  corpName;
    private char    corpCls;

    public DartCorpDto() {
    }

    public DartCorpDto(String corpCode, String corpName, char corpCls) {
        this.corpCode = corpCode;
        this.corpName = corpName;
        this.corpCls = corpCls;
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

    public char getCorpCls() {
        return corpCls;
    }

    public void setCorpCls(char corpCls) {
        this.corpCls = corpCls;
    }
}
