package com.jihun.study.openDartApi.serviceImpl.evaluate;

import com.jihun.study.openDartApi.dto.evalute.Evaluation;
import com.jihun.study.openDartApi.dto.stock.DartDto;
import com.jihun.study.openDartApi.dtoImpl.evaluate.IssueEvaluation;
import com.jihun.study.openDartApi.dtoImpl.evaluate.OperatingIncomeGrowthRatioEvaluation;
import com.jihun.study.openDartApi.entity.stock.CorpDetail;
import com.jihun.study.openDartApi.entity.stock.Corporation;
import com.jihun.study.openDartApi.service.evaluate.EvaluateService;
import com.jihun.study.openDartApi.service.evaluate.SortableService;
import com.jihun.study.openDartApi.utils.evaluate.EvaluateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service("OperatingIncomeGrowthRatioEvaluationService")
public class OperatingIncomeGrowthRatioEvaluationService implements EvaluateService, SortableService {
    private static final Logger logger = LoggerFactory.getLogger(OperatingIncomeGrowthRatioEvaluationService.class.getSimpleName());

    private static final String                     EVALUATE_SERVICE_NAME   = "operatingIncomeGrowthRatio";
    private static final String                     EVALUATE_SIMPLE_NAME    = "영업이익성장률평가";
    private static final Comparator<Corporation>    EVALUATE_COMPARATOR     = new Comparator<Corporation>() {
        @Override
        public int compare(Corporation comp1, Corporation comp2) {
            Evaluation comp1Evaluation = comp1.getCorpEvals().getOrDefault(EVALUATE_SERVICE_NAME, null);
            Evaluation comp2Evaluation = comp2.getCorpEvals().getOrDefault(EVALUATE_SERVICE_NAME, null);

            /*
             * Evaluation 확인
             */
            if (comp1Evaluation == null && comp2Evaluation == null) {
                return 0;
            } else if (comp1Evaluation == null) {
                return 1;
            } else if (comp2Evaluation == null) {
                return -1;
            }

            /*
             * 평가여부 확인
             */
            boolean comp1EvalDone = ((OperatingIncomeGrowthRatioEvaluation) comp1Evaluation).isEvalDone();
            boolean comp2EvalDone = ((OperatingIncomeGrowthRatioEvaluation) comp2Evaluation).isEvalDone();

            if (!comp1EvalDone && !comp2EvalDone) {
                return 0;
            } else if (!comp1EvalDone) {
                return 1;
            } else if (!comp2EvalDone) {
                return -1;
            }

            /*
             * 평가결과 확인
             *
             * 1. 영업이익증가율 양수유지여부
             * 2. 영업이익      양수유지여부
             * 3. 영업이익증가율 대소구분
             */
            boolean comp1IncomeGrowthRatioPositive  = ((OperatingIncomeGrowthRatioEvaluation) comp1Evaluation).isKeepOperatingIncomeGrowthRatioPositive();
            boolean comp1IncomePositive             = ((OperatingIncomeGrowthRatioEvaluation) comp1Evaluation).isKeepOperatingIncomePositive();
            boolean comp2IncomeGrowthRatioPositive  = ((OperatingIncomeGrowthRatioEvaluation) comp2Evaluation).isKeepOperatingIncomeGrowthRatioPositive();
            boolean comp2IncomePositive             = ((OperatingIncomeGrowthRatioEvaluation) comp2Evaluation).isKeepOperatingIncomePositive();

            float   comp1IncomeGrowthRatio = ((OperatingIncomeGrowthRatioEvaluation) comp1Evaluation).getOperatingIncomeGrowthRatio();
            float   comp2IncomeGrowthRatio = ((OperatingIncomeGrowthRatioEvaluation) comp2Evaluation).getOperatingIncomeGrowthRatio();

            /**
             * TODO
             * 좀 더 나은 조건식으로 변경
             */
            if (comp1IncomeGrowthRatioPositive && comp1IncomePositive
                && comp2IncomeGrowthRatioPositive && comp2IncomePositive
            ) {
                return (int)(comp2IncomeGrowthRatio - comp1IncomeGrowthRatio);
            } else if (!comp1IncomeGrowthRatioPositive && comp1IncomePositive
                    && comp2IncomeGrowthRatioPositive && comp2IncomePositive
            ) {
                return 1;
            } else if (comp1IncomeGrowthRatioPositive && !comp1IncomePositive
                    && comp2IncomeGrowthRatioPositive && comp2IncomePositive
            ) {
                return 1;
            } else if (comp1IncomeGrowthRatioPositive && comp1IncomePositive
                    && !comp2IncomeGrowthRatioPositive && comp2IncomePositive
            ) {
                return -1;
            } else if (comp1IncomeGrowthRatioPositive && comp1IncomePositive
                    && comp2IncomeGrowthRatioPositive && !comp2IncomePositive
            ) {
                return -1;
            } else if (!comp1IncomeGrowthRatioPositive && !comp1IncomePositive
                    && comp2IncomeGrowthRatioPositive && comp2IncomePositive
            ) {
                return 1;
            } else if (comp1IncomeGrowthRatioPositive && comp1IncomePositive
                    && !comp2IncomeGrowthRatioPositive && !comp2IncomePositive
            ) {
                return -1;
            } else if (!comp1IncomeGrowthRatioPositive && comp1IncomePositive
                    && !comp2IncomeGrowthRatioPositive && comp2IncomePositive
            ) {
                return (int)(comp2IncomeGrowthRatio - comp1IncomeGrowthRatio);
            } else if (comp1IncomeGrowthRatioPositive && !comp1IncomePositive
                    && comp2IncomeGrowthRatioPositive && !comp2IncomePositive
            ) {
                return (int)(comp2IncomeGrowthRatio - comp1IncomeGrowthRatio);
            } else if (!comp1IncomeGrowthRatioPositive && comp1IncomePositive
                    && comp2IncomeGrowthRatioPositive && !comp2IncomePositive
            ) {
                return (int)(comp2IncomeGrowthRatio - comp1IncomeGrowthRatio);
            } else if (comp1IncomeGrowthRatioPositive && !comp1IncomePositive
                    && !comp2IncomeGrowthRatioPositive && comp2IncomePositive
            ) {
                return (int)(comp2IncomeGrowthRatio - comp1IncomeGrowthRatio);
            } else if (!comp1IncomeGrowthRatioPositive && !comp1IncomePositive
                    && !comp2IncomeGrowthRatioPositive && comp2IncomePositive
            ) {
                return 1;
            } else if (!comp1IncomeGrowthRatioPositive && !comp1IncomePositive
                    && comp2IncomeGrowthRatioPositive && !comp2IncomePositive
            ) {
                return 1;
            } else if (comp1IncomeGrowthRatioPositive && !comp1IncomePositive
                    && !comp2IncomeGrowthRatioPositive && !comp2IncomePositive
            ) {
                return -1;
            } else if (!comp1IncomeGrowthRatioPositive && comp1IncomePositive
                    && !comp2IncomeGrowthRatioPositive && !comp2IncomePositive
            ) {
                return -1;
            } else {
                return (int)(comp2IncomeGrowthRatio - comp1IncomeGrowthRatio);
            }
        }
    };

    /**
     * evaluate
     *
     * Open Dart API 에서 받아온 데이터를 기반으로 기업의 영업이익을 분석합니다.
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
            OperatingIncomeGrowthRatioEvaluation operatingIncomeGrowthRatioEvaluation
                    = new OperatingIncomeGrowthRatioEvaluation(((Corporation) corpInfo).getCorpCode());

            try {
                if (corpDetails == null || corpDetails.size() == 0) {
                    operatingIncomeGrowthRatioEvaluation.setEvalDone(false);
                } else {
                    List<Long> operatingIncomes = new ArrayList<>();

                    for (int index = 0; index < corpDetails.size(); index++) {
                        long operatingIncome = EvaluateUtils.parseLong(corpDetails.get(index).getOperatingIncome());

                        if ("11013".equals(corpDetails.get(index).getReprtCode())) {
                            operatingIncome *= 3;
                        } else if ("11012".equals(corpDetails.get(index).getReprtCode())) {
                            operatingIncome *= 2;
                        } else if ("11014".equals(corpDetails.get(index).getReprtCode())) {
                            operatingIncome *= 1.25;
                        }
                        operatingIncomes.add(operatingIncome);
                    }

                    operatingIncomeGrowthRatioEvaluation.setOperatingIncomeGrowthRatio(evaluateOperatingIncomeRatio(operatingIncomes));
                    operatingIncomeGrowthRatioEvaluation.setKeepOperatingIncomeGrowthRatioPositive(evaluateKeepOperatingIncomeGrowthRatioPositive(operatingIncomes));
                    operatingIncomeGrowthRatioEvaluation.setKeepOperatingIncomePositive(evaluateKeepOperatingIncomePositive(operatingIncomes));
                }
            } catch (Exception e) {
                operatingIncomeGrowthRatioEvaluation.setEvalDone(false);
            } finally {
                ((Corporation) corpInfo).addCorpEval(EVALUATE_SERVICE_NAME, operatingIncomeGrowthRatioEvaluation);
            }

            /*
             * 2021-03-07
             *
             * @Deprecated
             *
             * EvaluateUtils 를 추가하여 String 값을 Long 값으로 parse 하는 기능을 추가하였습니다.
             * 아래의 코드는 더이상 사용되지 않습니다.
             *
            List<String> operatingIncomes = new ArrayList<>();
            for (CorpDetail corpDetail : ((Corporation) corpInfo).getCorpDetails()) {
                if ("11011".equals(corpDetail.getReprtCode())) {
                    String operatingIncome = corpDetail.getOperatingIncome();

                    if (operatingIncome == null) {
                        operatingIncomeGrowthRatioEvaluation.setEvalDone(false);
                        break;
                    } else {
                        operatingIncomes.add(operatingIncome);
                    }
                }
            }

            if (operatingIncomeGrowthRatioEvaluation.isEvalDone()) {
                try {
                    operatingIncomeGrowthRatioEvaluation.setOperatingIncomeGrowthRatio(evaluateOperatingIncomeRatio(operatingIncomes));
                    operatingIncomeGrowthRatioEvaluation.setKeepOperatingIncomeGrowthRatioPositive(evaluateKeepOperatingIncomeGrowthRatioPositive(operatingIncomes));
                    operatingIncomeGrowthRatioEvaluation.setKeepOperatingIncomePositive(evaluateKeepOperatingIncomePositive(operatingIncomes));
                } catch (NumberFormatException e) {
                    operatingIncomeGrowthRatioEvaluation.setEvalDone(false);
                } catch (IllegalArgumentException e) {
                    operatingIncomeGrowthRatioEvaluation.setEvalDone(false);
                } finally {
                    ((Corporation) corpInfo).addCorpEval(EVALUATE_SERVICE_NAME, operatingIncomeGrowthRatioEvaluation);
                }
            } else {
                ((Corporation) corpInfo).addCorpEval(EVALUATE_SERVICE_NAME, operatingIncomeGrowthRatioEvaluation);
            }
             */
        }
    }

    /**
     * evaluateKeepOperatingIncomeGrowthRatioPositive
     *
     * 영업이익 성장률이 계속 상승하는지 평가합니다.
     *
     * 2021-03-07
     * String[] 형식으로 받지 않고, List<Long> 형식으로 받게 변경되었습니다.
     *
     * @param operatingIncomes
     * @return
     */
    private boolean evaluateKeepOperatingIncomeGrowthRatioPositive(List<Long> operatingIncomes) {
        if (operatingIncomes.size() < 2) {
            throw new IllegalArgumentException();
        }

        boolean isKeepOperatingIncomeGrowthRatioPositive = true;

        try {
            long parsedCurOperatingIncome = operatingIncomes.get(0);

            for (int idx = 1; idx < operatingIncomes.size(); idx++) {
                long parsedNextOperatingIncome = operatingIncomes.get(idx);

                if (parsedNextOperatingIncome < parsedCurOperatingIncome) {
                    isKeepOperatingIncomeGrowthRatioPositive = false;
                    break;
                }

                parsedCurOperatingIncome = parsedNextOperatingIncome;
            }

            return isKeepOperatingIncomeGrowthRatioPositive;
        } catch(Exception e) {
            throw new NumberFormatException();
        }
    }

    /**
     * evaluateKeepOperatingIncomePositive
     *
     * 영업이익이 계속 상승하는지 평가합니다.
     *
     * 2021-03-07
     * String[] 형식으로 받지 않고, List<Long> 형식으로 받게 변경되었습니다.
     *
     * @param operatingIncomes
     * @return
     */
    private boolean evaluateKeepOperatingIncomePositive(List<Long> operatingIncomes) {
        if (operatingIncomes.size() < 2) {
            throw new IllegalArgumentException();
        }

        boolean isKeepOperatingIncomePositive = true;

        try {
            for (long operatingIncome : operatingIncomes) {
                long parsedOperatingIncome = operatingIncome;

                if (parsedOperatingIncome < 0) {
                    isKeepOperatingIncomePositive = false;
                    break;
                }
            }

            return isKeepOperatingIncomePositive;
        } catch(Exception e) {
            throw new NumberFormatException();
        }
    }

    /**
     * evaluateOperatingIncomeRatio
     *
     * 영업이익 성장률을 평가합니다.
     *
     * 2021-03-07
     * String[] 형식으로 받지 않고, List<Long> 형식으로 받게 변경되었습니다.
     *
     * @param operatingIncomes
     * @return
     */
    private float evaluateOperatingIncomeRatio(List<Long> operatingIncomes) {
        if (operatingIncomes.size() < 2) {
            throw new IllegalArgumentException();
        }

        float output = 0;

        try {
            long curOperatingIncome = operatingIncomes.get(0);

            for (int idx = 1; idx < operatingIncomes.size(); idx++) {
                long nextOperatingIncome = operatingIncomes.get(idx);

                logger.debug("curOperatingIncome  = " + curOperatingIncome);
                logger.debug("nextOperatingIncome = " + nextOperatingIncome);

                output              += ((float)(nextOperatingIncome - curOperatingIncome)) / nextOperatingIncome * 100;
                curOperatingIncome  = nextOperatingIncome;
            }

            output /= operatingIncomes.size() - 1;
            return output;
        } catch (Exception e) {
            throw new NumberFormatException();
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

    @Override
    public Comparator getComparator() {
        return EVALUATE_COMPARATOR;
    }
}
