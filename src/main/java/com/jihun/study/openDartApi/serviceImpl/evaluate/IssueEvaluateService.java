package com.jihun.study.openDartApi.serviceImpl.evaluate;

import com.jihun.study.openDartApi.dto.stock.DartDto;
import com.jihun.study.openDartApi.dtoImpl.evaluate.IssueEvaluation;
import com.jihun.study.openDartApi.entity.stock.CorpDetail;
import com.jihun.study.openDartApi.entity.stock.Corporation;
import com.jihun.study.openDartApi.service.evaluate.EvaluateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("IssueEvaluateService")
public class IssueEvaluateService implements EvaluateService {
    private static final Logger logger = LoggerFactory.getLogger(IssueEvaluateService.class.getSimpleName());

    private static final String EVALUATE_SERVICE_NAME = "Issue";

    private static final char   KOSPI   = 'Y';
    private static final char   KOSDAQ  = 'K';

    private static final long   KOSPI_REVENUE_STANDARD              = 5000000000L;
    private static final int    KOSPI_EQUITY_IMPAIRMENT_STANDARD    = -50;

    private static final long   KOSDAQ_REVENUE_STANDARD             = 3000000000L;
    private static final int    KOSDAQ_EQUITY_IMPAIRMENT_STANDARD   = -50;
    private static final int    KOSDAQ_OPERATING_LOSS_STANDARD      = 4;
    private static final int    KOSDAQ_LOSS_BEFORE_TAX_STANDARD     = -50;

    /**
     * evaluate
     *
     * Open Dart API 에서 받아온 데이터를 기반으로 기업의 관리종목 및 상장폐지 요건을 평가합니다.
     *
     * 평가한 결과는 Corporation Entity 에 저장됩니다.
     * 만약 평가할 수 없다면, Corporation 의 is_eval_done 변수가 false 로 변경됩니다.
     *
     * @param corpInfo
     */
    @Override
    public void evaluate(DartDto corpInfo) {
        if (corpInfo instanceof Corporation) {
            List<CorpDetail>    corpDetails     = ((Corporation) corpInfo).getCorpDetails();
            IssueEvaluation     issueEvaluation = new IssueEvaluation(((Corporation) corpInfo).getCorpCode());

            if (corpDetails == null || corpDetails.size() == 0) {
                issueEvaluation.setEvalDone(false);
            } else {
                String[] revenues           = new String[corpDetails.size()];
                String[] tot_equities       = new String[corpDetails.size()];
                String[] equities           = new String[corpDetails.size()];
                String[] incomeBeforeTexes  = new String[corpDetails.size()];
                String[] operatingIncomes   = new String[corpDetails.size()];

                for (int index = 0; index < corpDetails.size(); index++) {
                    revenues[index]             = corpDetails.get(index).getRevenue();
                    tot_equities[index]         = corpDetails.get(index).getTotStockholdersEquity();
                    equities[index]             = corpDetails.get(index).getStockholdersEquity();
                    incomeBeforeTexes[index]    = corpDetails.get(index).getIncomeBeforeTax();
                    operatingIncomes[index]     = corpDetails.get(index).getOperatingIncome();

                    if (revenues[index]             == null
                        || tot_equities[index]      == null
                        || equities[index]          == null
                        || incomeBeforeTexes[index] == null
                        || operatingIncomes[index]  == null
                    ) {
                        issueEvaluation.setEvalDone(false);
                    }
                }

                if (issueEvaluation.isEvalDone()) {
                    try {
                        issueEvaluation.setRevenueLack(isRevenueLack(((Corporation) corpInfo).getCorpCode(), ((Corporation) corpInfo).getCorpCls(), revenues));
                        issueEvaluation.setEquityImpairment(isEquityImpairment(((Corporation) corpInfo).getCorpCode(), ((Corporation) corpInfo).getCorpCls(), tot_equities, equities));
                        issueEvaluation.setOperatingLoss(isOperatingLoss(((Corporation) corpInfo).getCorpCode(), ((Corporation) corpInfo).getCorpCls(), operatingIncomes));
                        issueEvaluation.setLossBeforeTax(isLossBeforeTax(((Corporation) corpInfo).getCorpCode(), ((Corporation) corpInfo).getCorpCls(), tot_equities, incomeBeforeTexes));

                        if (issueEvaluation.isRevenueLack()
                            || issueEvaluation.isEquityImpairment()
                            || issueEvaluation.isOperatingLoss()
                            || issueEvaluation.isLossBeforeTax()
                        ) {
                            issueEvaluation.setIssued(true);
                        } else {
                            issueEvaluation.setIssued(false);
                        }
                    } catch (NumberFormatException e) {
                        issueEvaluation.setEvalDone(false);
                    }
                }
            }
            ((Corporation) corpInfo).addCorpEval(EVALUATE_SERVICE_NAME, issueEvaluation);
        }
    }

    /**
     * isRevenueLack
     *
     * 기업의 매출액 관리종목 조건에 대해 평가합니다.
     *
     * @param corpCls
     * @param revenues
     * @return 매출액 관리종목 편입여부
     */
    public static boolean isRevenueLack(final String corpCode, char corpCls, String[] revenues) {
        logger.debug("isRevenueLack : corpCode          = " + corpCode + " (" + corpCls + ")");

        if (corpCls == KOSPI) {
            logger.debug("isRevenueLack : kospi standard    = " + KOSPI_REVENUE_STANDARD);
        } else if (corpCls == KOSDAQ) {
            logger.debug("isRevenueLack : kosdaq standard   = " + KOSDAQ_REVENUE_STANDARD);
        }

        boolean revenueLack = false;

        for (String revenue : revenues) {
            try {
                long longRevenue = Long.parseLong(revenue.replaceAll("\\,", ""));
                logger.debug("isRevenueLack : revenue           = " + longRevenue);

                if (corpCls == KOSPI && (KOSPI_REVENUE_STANDARD > longRevenue)) {
                    revenueLack = true;
                    break;
                }

                if (corpCls == KOSDAQ && (KOSDAQ_REVENUE_STANDARD > longRevenue)) {
                    revenueLack = true;
                    break;
                }
            } catch(NumberFormatException e) {
                throw e;
            }
        }

        return revenueLack;
    }

    /**
     * isEquityImpairment
     *
     * 기업의 자본잠식 관리종목 조건에 대해 평가합니다.
     *
     * @param corpCls
     * @param totEquities
     * @param equities
     * @return 자본잠식 관리종목 편입 여부
     */
    public static boolean isEquityImpairment(final String corpCode, char corpCls, String[] totEquities, String[] equities) {
        logger.debug("isEquityImpairment : corpCode         = " + corpCode + " (" + corpCls + ")");

        if (corpCls == KOSPI) {
            logger.debug("isEquityImpairment : kospi standard   = " + KOSPI_EQUITY_IMPAIRMENT_STANDARD);
        } else if (corpCls == KOSDAQ) {
            logger.debug("isEquityImpairment : kosdaq standard  = " + KOSDAQ_EQUITY_IMPAIRMENT_STANDARD);
        }

        for (int index = 0; index < totEquities.length; index++) {
            try {
                long    totEquity           = Long.parseLong(totEquities[index].replaceAll("\\,", ""));
                long    equity              = Long.parseLong(equities[index].replaceAll("\\,", ""));
                int     impairmentRatio     = (int) (((float)(totEquity - equity) / (totEquity)) * 100);

                logger.debug("isEquityImpairment : totEquity        = " + totEquity);
                logger.debug("isEquityImpairment : equity           = " + equity);
                logger.debug("isEquityImpairment : impairmentRatio  = " + impairmentRatio);

                if (corpCls == KOSPI && (KOSPI_EQUITY_IMPAIRMENT_STANDARD > impairmentRatio)) {
                    return true;
                }

                if (corpCls == KOSDAQ && (KOSDAQ_EQUITY_IMPAIRMENT_STANDARD > impairmentRatio)) {
                    return true;
                }
            } catch(NumberFormatException e) {
                throw e;
            }
        }

        return false;
    }

    /**
     * isOperatingLoss
     *
     * 기업의 영업손실 관리종목 조건에 대해 평가합니다.
     *
     * @param corpCls
     * @param operatingIncomes
     * @return 영업손실 관리종목 편입 여부
     */
    public static boolean isOperatingLoss(final String corpCode, char corpCls, String[] operatingIncomes) {
        logger.debug("isOperatingLoss : corpCode         = " + corpCode + " (" + corpCls + ")");

        if (corpCls == KOSPI) {
            return false;
        } else if (corpCls == KOSDAQ) {
            logger.debug("isOperatingLoss : kosdaq standard  = " + KOSDAQ_OPERATING_LOSS_STANDARD);
        }

        int lossCount = 0;
        for (String operatingIncome : operatingIncomes) {
            try {
                long longOperatingIncome = Long.parseLong(operatingIncome.replaceAll("\\,", ""));

                logger.debug("isOperatingLoss : operatingIncome  = " + longOperatingIncome);

                if (longOperatingIncome < 0) {
                    lossCount++;
                }
            } catch(NumberFormatException e) {
                throw e;
            }
        }

        if (lossCount >= KOSDAQ_OPERATING_LOSS_STANDARD) {
            return true;
        }

        return false;
    }

    /**
     * isLossBeforeTax
     *
     * 기업의 법인세차감전 손순실 관리종목 조건에 대해 평가합니다.
     *
     * @param corpCls
     * @param totEquities
     * @param incomeBeforeTaxes
     * @return 법인세차감전 손순실 관리종목 편입 여부
     */
    public static boolean isLossBeforeTax(final String corpCode, char corpCls, String[] totEquities, String[] incomeBeforeTaxes) {
        logger.debug("isLossBeforeTax : corpCode            = " + corpCode + " (" + corpCls + ")");

        if (corpCls == KOSPI) {
            return false;
        } else if (corpCls == KOSDAQ) {
            logger.debug("isLossBeforeTax : kosdaq standard     = " + KOSDAQ_LOSS_BEFORE_TAX_STANDARD);
        }

        for (int index = 0; index < totEquities.length; index++) {
            try {
                long totEquity          = Long.parseLong(totEquities[index].replaceAll("\\,", ""));
                long incomeBeforeTax    = Long.parseLong(incomeBeforeTaxes[index].replaceAll("\\,", ""));

                logger.debug("isLossBeforeTax : totEquity           = " + totEquity);
                logger.debug("isLossBeforeTax : incomeBeforeTax     = " + incomeBeforeTax);

                if (incomeBeforeTax < 0) {
                    int lossBeforeTaxRatio = (int) ((float) incomeBeforeTax / (float) totEquity * 100);
                    logger.debug("isLossBeforeTax : lossBeforeTaxRatio  = " + lossBeforeTaxRatio);

                    if (lossBeforeTaxRatio < KOSDAQ_LOSS_BEFORE_TAX_STANDARD) {
                        return true;
                    }
                }
            } catch(NumberFormatException e) {
                throw e;
            }
        }

        return false;
    }
}
