package com.jihun.study.openDartApi.dtoImpl.stock;

import com.jihun.study.openDartApi.dto.evalute.Evaluation;
import com.jihun.study.openDartApi.dto.stock.DartDto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DartCorpDto implements Serializable, DartDto {
    private String  corpCode;
    private String  corpName;
    private char    corpCls;

    private Map<String, Evaluation> corpEvals = new HashMap<>();

    public DartCorpDto() {
    }

    public DartCorpDto(String corpCode, String corpName, char corpCls) {
        this.corpCode = corpCode;
        this.corpName = corpName;
        this.corpCls = corpCls;
    }

    public void addCorpEval(String evalKey, Evaluation corpEval) {
        this.corpEvals.put(evalKey, corpEval);
    }

    public void addCorpEvals(Map<String, Evaluation> corpEvals) {
        for (String evalKey : corpEvals.keySet()) {
            this.corpEvals.put(evalKey, corpEvals.get(evalKey));

//            if (corpDetail.getCorporation() != this) {
//                corpDetail.setCorporation(this);
//            }
        }
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
