package com.jihun.study.openDartApi.serviceImpl.evaluate;

import com.jihun.study.openDartApi.dto.evalute.Evaluation;
import com.jihun.study.openDartApi.dto.stock.DartDto;
import com.jihun.study.openDartApi.dtoImpl.evaluate.OperatingIncomeGrowthRatioEvaluation;
import com.jihun.study.openDartApi.entity.stock.CorpDetail;
import com.jihun.study.openDartApi.entity.stock.Corporation;
import com.jihun.study.openDartApi.service.evaluate.EvaluateService;
import com.jihun.study.openDartApi.service.evaluate.SortableService;
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
            if (!((OperatingIncomeGrowthRatioEvaluation) comp1Evaluation).isEvalDone()
                && !((OperatingIncomeGrowthRatioEvaluation) comp2Evaluation).isEvalDone()
            ) {
                return 0;
            } else if (!((OperatingIncomeGrowthRatioEvaluation) comp1Evaluation).isEvalDone()) {
                return 1;
            } else if (!((OperatingIncomeGrowthRatioEvaluation) comp2Evaluation).isEvalDone()) {
                return -1;
            }

            /*
             * 평가결과 확인
             *
             * 1. 영업이익증가율 양수유지여부
             * 2. 영업이익      양수유지여부
             * 3. 영업이익증가율 대소구분
             */
            if (((OperatingIncomeGrowthRatioEvaluation) comp1Evaluation).isKeepOperatingIncomeGrowthRatioPositive()
                && ((OperatingIncomeGrowthRatioEvaluation) comp2Evaluation).isKeepOperatingIncomeGrowthRatioPositive()
            ) {
                if (((OperatingIncomeGrowthRatioEvaluation) comp1Evaluation).isKeepOperatingIncomePositive()
                    && ((OperatingIncomeGrowthRatioEvaluation) comp2Evaluation).isKeepOperatingIncomePositive()
                ) {
                    return (int)
                            (((OperatingIncomeGrowthRatioEvaluation) comp2Evaluation).getOperatingIncomeGrowthRatio()
                            - ((OperatingIncomeGrowthRatioEvaluation) comp1Evaluation).getOperatingIncomeGrowthRatio());
                } else if (((OperatingIncomeGrowthRatioEvaluation) comp2Evaluation).isKeepOperatingIncomePositive()) {
                    return 1;
                } else {
                    return -1;
                }
            } else if (((OperatingIncomeGrowthRatioEvaluation) comp2Evaluation).isKeepOperatingIncomeGrowthRatioPositive()) {
                return 1;
            } else {
                return -1;
            }
        }
    };

    @Override
    public void evaluate(DartDto corpInfo) {
        if (corpInfo instanceof Corporation) {
            OperatingIncomeGrowthRatioEvaluation operatingIncomeGrowthRatioEvaluation
                    = new OperatingIncomeGrowthRatioEvaluation(((Corporation) corpInfo).getCorpCode());

            List<String> operatingIncomes = new ArrayList<>();
            for (CorpDetail corpDetail : ((Corporation) corpInfo).getCorpDetails()) {
                String operatingIncome = corpDetail.getOperatingIncome();

                if (operatingIncome == null) {
                    operatingIncomeGrowthRatioEvaluation.setEvalDone(false);
                    break;
                } else {
                    operatingIncomes.add(operatingIncome);
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
        }
    }

    private boolean evaluateKeepOperatingIncomeGrowthRatioPositive(List<String> operatingIncomes) {
        if (operatingIncomes.size() < 2) {
            throw new IllegalArgumentException();
        }

        boolean isKeepOperatingIncomeGrowthRatioPositive = true;

        try {
            long parsedCurOperatingIncome = Long.parseLong(operatingIncomes.get(0).replaceAll("\\,", ""));

            for (int idx = 1; idx < operatingIncomes.size(); idx++) {
                long parsedNextOperatingIncome = Long.parseLong(operatingIncomes.get(idx).replaceAll("\\,", ""));

                if (parsedNextOperatingIncome < parsedCurOperatingIncome) {
                    isKeepOperatingIncomeGrowthRatioPositive = false;
                    break;
                }
            }

            return isKeepOperatingIncomeGrowthRatioPositive;
        } catch(Exception e) {
            throw new NumberFormatException();
        }
    }

    private boolean evaluateKeepOperatingIncomePositive(List<String> operatingIncomes) {
        if (operatingIncomes.size() < 2) {
            throw new IllegalArgumentException();
        }

        boolean isKeepOperatingIncomePositive = true;

        try {
            for (String operatingIncome : operatingIncomes) {
                long parsedOperatingIncome = Long.parseLong(operatingIncome.replaceAll("\\,", ""));

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

    private float evaluateOperatingIncomeRatio(List<String> operatingIncomes) {
        if (operatingIncomes.size() < 2) {
            throw new IllegalArgumentException();
        }

        float output = 0;

        try {
            long curOperatingIncome = Long.parseLong(operatingIncomes.get(0).replaceAll("\\,", ""));

            for (int idx = 1; idx < operatingIncomes.size(); idx++) {
                long nextOperatingIncome = Long.parseLong(operatingIncomes.get(idx).replaceAll("\\,", ""));

                output              += ((float)(nextOperatingIncome - curOperatingIncome)) / nextOperatingIncome * 100;
                logger.debug("curOperatingIncome  = " + curOperatingIncome);
                logger.debug("nextOperatingIncome = " + nextOperatingIncome);
                logger.debug("output              = " + output);

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
    public Comparator getComparator() {
        return EVALUATE_COMPARATOR;
    }
}
