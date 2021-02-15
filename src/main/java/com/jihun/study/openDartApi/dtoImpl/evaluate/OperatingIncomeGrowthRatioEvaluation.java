package com.jihun.study.openDartApi.dtoImpl.evaluate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jihun.study.openDartApi.dto.evalute.Evaluation;

import java.io.Serializable;

@JsonAutoDetect(
          fieldVisibility       = JsonAutoDetect.Visibility.ANY
        , getterVisibility      = JsonAutoDetect.Visibility.NONE
        , setterVisibility      = JsonAutoDetect.Visibility.NONE
        , isGetterVisibility    = JsonAutoDetect.Visibility.NONE
        , creatorVisibility     = JsonAutoDetect.Visibility.NONE
)
public class OperatingIncomeGrowthRatioEvaluation implements Serializable, Evaluation {
    @JsonProperty("corp_code")
    private String corpCode;

    @JsonProperty("is_eval_done")
    private boolean isEvalDone = true;

    @JsonProperty("operating_income_growth_ratio")
    private float operatingIncomeGrowthRatio;

    @JsonProperty("is_keep_operating_income_positive")
    private boolean isKeepOperatingIncomePositive;

    @JsonProperty("is_keep_operating_income_growth_ratio_positive")
    private boolean isKeepOperatingIncomeGrowthRatioPositive;

    public OperatingIncomeGrowthRatioEvaluation() {
    }

    public OperatingIncomeGrowthRatioEvaluation(String corpCode) {
        this.corpCode = corpCode;
    }

    public String getCorpCode() {
        return corpCode;
    }

    public void setCorpCode(String corpCode) {
        this.corpCode = corpCode;
    }

    public boolean isEvalDone() {
        return isEvalDone;
    }

    public void setEvalDone(boolean evalDone) {
        isEvalDone = evalDone;
    }

    public float getOperatingIncomeGrowthRatio() {
        return operatingIncomeGrowthRatio;
    }

    public void setOperatingIncomeGrowthRatio(float operatingIncomeGrowthRatio) {
        this.operatingIncomeGrowthRatio = operatingIncomeGrowthRatio;
    }

    public boolean isKeepOperatingIncomePositive() {
        return isKeepOperatingIncomePositive;
    }

    public void setKeepOperatingIncomePositive(boolean keepOperatingIncomePositive) {
        isKeepOperatingIncomePositive = keepOperatingIncomePositive;
    }

    public boolean isKeepOperatingIncomeGrowthRatioPositive() {
        return isKeepOperatingIncomeGrowthRatioPositive;
    }

    public void setKeepOperatingIncomeGrowthRatioPositive(boolean keepOperatingIncomeGrowthRatioPositive) {
        isKeepOperatingIncomeGrowthRatioPositive = keepOperatingIncomeGrowthRatioPositive;
    }

    @Override
    public String toString() {
        return "OperatingIncomeGrowthRatioEvaluation{" +
                "corpCode='" + corpCode + '\'' +
                ", isEvalDone=" + isEvalDone +
                ", operatingIncomeGrowthRatio=" + operatingIncomeGrowthRatio +
                ", isKeepOperatingIncomePositive=" + isKeepOperatingIncomePositive +
                ", isKeepOperatingIncomeGrowthRatioPositive=" + isKeepOperatingIncomeGrowthRatioPositive +
                '}';
    }
}
