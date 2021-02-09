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
public class IssueEvaluation implements Serializable, Evaluation {
    @JsonProperty("corp_code")
    private String corpCode;

    @JsonProperty("is_eval_done")
    private boolean isEvalDone = true;
    @JsonProperty("is_issued")
    private boolean isIssued;

    @JsonProperty("is_revenue_lack")
    private boolean isRevenueLack;
    @JsonProperty("is_equity_impairment")
    private boolean isEquityImpairment;
    @JsonProperty("is_operating_loss")
    private boolean isOperatingLoss;
    @JsonProperty("is_loss_before_tax")
    private boolean isLossBeforeTax;

    public IssueEvaluation() {
    }

    public IssueEvaluation(String corpCode) {
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

    public boolean isRevenueLack() {
        return isRevenueLack;
    }

    public void setRevenueLack(boolean revenueLack) {
        isRevenueLack = revenueLack;
    }

    public boolean isEquityImpairment() {
        return isEquityImpairment;
    }

    public void setEquityImpairment(boolean equityImpairment) {
        isEquityImpairment = equityImpairment;
    }

    public boolean isOperatingLoss() {
        return isOperatingLoss;
    }

    public void setOperatingLoss(boolean operatingLoss) {
        isOperatingLoss = operatingLoss;
    }

    public boolean isLossBeforeTax() {
        return isLossBeforeTax;
    }

    public void setLossBeforeTax(boolean lossBeforeTax) {
        isLossBeforeTax = lossBeforeTax;
    }

    public boolean isIssued() {
        return isIssued;
    }

    public void setIssued(boolean issued) {
        isIssued = issued;
    }
}
