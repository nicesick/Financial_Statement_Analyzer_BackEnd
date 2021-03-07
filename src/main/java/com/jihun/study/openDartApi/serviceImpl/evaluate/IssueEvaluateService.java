package com.jihun.study.openDartApi.serviceImpl.evaluate;

import com.jihun.study.openDartApi.dto.stock.DartDto;
import com.jihun.study.openDartApi.dtoImpl.evaluate.IssueEvaluation;
import com.jihun.study.openDartApi.entity.stock.CorpDetail;
import com.jihun.study.openDartApi.entity.stock.Corporation;
import com.jihun.study.openDartApi.service.evaluate.EvaluateService;
import com.jihun.study.openDartApi.utils.evaluate.EvaluateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("IssueEvaluateService")
public class IssueEvaluateService implements EvaluateService {
    private static final Logger logger = LoggerFactory.getLogger(IssueEvaluateService.class.getSimpleName());

    private static final String EVALUATE_SERVICE_NAME   = "issue";
    private static final String EVALUATE_SIMPLE_NAME    = "관리종목평가";

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
     * 2021.03.07
     * 연간보고서가 아닌 분기, 반기보고서의 경우 매출액, 영업이익, 법인세차감전순이익 값을 각각 연간 예상 값으로 조정합니다
     * 1분기보고서 : 11013, 반기보고서 : 11012, 3분기보고서 : 11014, 사업보고서 : 11011
     *
     * 11013 : 300% 증가
     * 11012 : 200% 증가
     * 11014 : 75% 증가
     *
     * @param corpInfo
     */
    @Override
    public void evaluate(DartDto corpInfo) {
        if (corpInfo instanceof Corporation) {
            List<CorpDetail>    corpDetails     = ((Corporation) corpInfo).getCorpDetails();
            IssueEvaluation     issueEvaluation = new IssueEvaluation(((Corporation) corpInfo).getCorpCode());

            try {
                if (corpDetails == null || corpDetails.size() == 0) {
                    issueEvaluation.setEvalDone(false);
                } else {
                    List<Long> revenues             = new ArrayList<>();
                    List<Long> tot_equities         = new ArrayList<>();
                    List<Long> equities             = new ArrayList<>();
                    List<Long> incomeBeforeTexes    = new ArrayList<>();
                    List<Long> operatingIncomes     = new ArrayList<>();

                    for (int index = 0; index < corpDetails.size(); index++) {
                        long revenue            = EvaluateUtils.parseLong(corpDetails.get(index).getRevenue());
                        long tot_equity         = EvaluateUtils.parseLong(corpDetails.get(index).getTotStockholdersEquity());
                        long equity             = EvaluateUtils.parseLong(corpDetails.get(index).getStockholdersEquity());
                        long incomeBeforeTex    = EvaluateUtils.parseLong(corpDetails.get(index).getIncomeBeforeTax());
                        long operatingIncome    = EvaluateUtils.parseLong(corpDetails.get(index).getOperatingIncome());

                        if ("11013".equals(corpDetails.get(index).getReprtCode())) {
                            revenue *= 3;
                            incomeBeforeTex *= 3;
                            operatingIncome *= 3;
                        } else if ("11012".equals(corpDetails.get(index).getReprtCode())) {
                            revenue *= 2;
                            incomeBeforeTex *= 2;
                            operatingIncome *= 2;
                        } else if ("11014".equals(corpDetails.get(index).getReprtCode())) {
                            revenue *= 1.25;
                            incomeBeforeTex *= 1.25;
                            operatingIncome *= 1.25;
                        }

                        revenues.add(revenue);
                        tot_equities.add(tot_equity);
                        equities.add(equity);
                        incomeBeforeTexes.add(incomeBeforeTex);
                        operatingIncomes.add(operatingIncome);
                    }

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
                }
            } catch(Exception e) {
                issueEvaluation.setEvalDone(false);
            } finally {
                ((Corporation) corpInfo).addCorpEval(EVALUATE_SERVICE_NAME, issueEvaluation);
            }

            /*
             * 2021-03-07
             *
             * @Deprecated
             *
             * EvaluateUtils 를 추가하여 String 값을 Long 값으로 parse 하는 기능을 추가하였습니다.
             * 아래의 코드는 더이상 사용되지 않습니다.
             *
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
                } else {
                    String reprtCode = corpDetails.get(index).getReprtCode();
                    if ("11013".equals(reprtCode)) {

                    } else if ("11012".equals(reprtCode)) {

                    } else if ("11014".equals(reprtCode)) {

                    }
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
            */
        }
    }

    @Override
    public String getServiceName() {
        return EVALUATE_SERVICE_NAME;
    }

    @Override
    public String getSimpleName() {
        return EVALUATE_SIMPLE_NAME;
    }

    /**
     *
     * isRevenueLack
     *
     * 기업의 매출액 관리종목 조건에 대해 평가합니다.
     *
     * 2021-03-07
     * String[] 형식으로 받지 않고, List<Long> 형식으로 받게 변경되었습니다.
     *
     * @param corpCls
     * @param revenues
     *
     * @return 매출액 관리종목 편입여부
     */
    public static boolean isRevenueLack(final String corpCode, char corpCls, List<Long> revenues) {
        logger.debug("isRevenueLack : corpCode          = " + corpCode + " (" + corpCls + ")");

        if (corpCls == KOSPI) {
            logger.debug("isRevenueLack : kospi standard    = " + KOSPI_REVENUE_STANDARD);
        } else if (corpCls == KOSDAQ) {
            logger.debug("isRevenueLack : kosdaq standard   = " + KOSDAQ_REVENUE_STANDARD);
        }

        boolean revenueLack = false;

        for (long revenue : revenues) {
            try {
                long longRevenue = revenue;
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
     * 2021-03-07
     * String[] 형식으로 받지 않고, List<Long> 형식으로 받게 변경되었습니다.
     *
     * @param corpCls
     * @param totEquities
     * @param equities
     *
     * @return 자본잠식 관리종목 편입 여부
     */
    public static boolean isEquityImpairment(final String corpCode, char corpCls, List<Long> totEquities, List<Long> equities) {
        logger.debug("isEquityImpairment : corpCode         = " + corpCode + " (" + corpCls + ")");

        if (corpCls == KOSPI) {
            logger.debug("isEquityImpairment : kospi standard   = " + KOSPI_EQUITY_IMPAIRMENT_STANDARD);
        } else if (corpCls == KOSDAQ) {
            logger.debug("isEquityImpairment : kosdaq standard  = " + KOSDAQ_EQUITY_IMPAIRMENT_STANDARD);
        }

        for (int index = 0; index < totEquities.size(); index++) {
            try {
                long    totEquity           = totEquities.get(index);
                long    equity              = equities.get(index);

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
     * 2021-03-07
     * String[] 형식으로 받지 않고, List<Long> 형식으로 받게 변경되었습니다.
     *
     * @param corpCls
     * @param operatingIncomes
     *
     * @return 영업손실 관리종목 편입 여부
     */
    public static boolean isOperatingLoss(final String corpCode, char corpCls, List<Long> operatingIncomes) {
        logger.debug("isOperatingLoss : corpCode         = " + corpCode + " (" + corpCls + ")");

        if (corpCls == KOSPI) {
            return false;
        } else if (corpCls == KOSDAQ) {
            logger.debug("isOperatingLoss : kosdaq standard  = " + KOSDAQ_OPERATING_LOSS_STANDARD);
        }

        int lossCount = 0;
        for (long operatingIncome : operatingIncomes) {
            try {
                long longOperatingIncome = operatingIncome;

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
     * 2021-03-07
     * String[] 형식으로 받지 않고, List<Long> 형식으로 받게 변경되었습니다.
     *
     * @param corpCls
     * @param totEquities
     * @param incomeBeforeTaxes
     * @return 법인세차감전 손순실 관리종목 편입 여부
     */
    public static boolean isLossBeforeTax(final String corpCode, char corpCls, List<Long> totEquities, List<Long> incomeBeforeTaxes) {
        logger.debug("isLossBeforeTax : corpCode            = " + corpCode + " (" + corpCls + ")");

        if (corpCls == KOSPI) {
            return false;
        } else if (corpCls == KOSDAQ) {
            logger.debug("isLossBeforeTax : kosdaq standard     = " + KOSDAQ_LOSS_BEFORE_TAX_STANDARD);
        }

        for (int index = 0; index < totEquities.size(); index++) {
            try {
                long totEquity          = totEquities.get(index);
                long incomeBeforeTax    = incomeBeforeTaxes.get(index);

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
