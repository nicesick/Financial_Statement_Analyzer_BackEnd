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
    private static final Logger logger = LoggerFactory.getLogger(IssueEvaluateService.class.getSimpleName());

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

            return (int)
                    (((OperatingIncomeGrowthRatioEvaluation) comp2Evaluation).getOperatingIncomeGrowthRatio()
                    - ((OperatingIncomeGrowthRatioEvaluation) comp1Evaluation).getOperatingIncomeGrowthRatio());
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

    private float evaluateOperatingIncomeRatio(List<String> operatingIncomes) {
        if (operatingIncomes.size() < 2) {
            throw new IllegalArgumentException();
        }

        float output = 0;

        try {
            long curOperatingIncome = Long.parseLong(operatingIncomes.get(0).replaceAll("\\,", ""));

            for (int idx = 1; idx < operatingIncomes.size(); idx++) {
                long prevOperatingIncome = Long.parseLong(operatingIncomes.get(idx).replaceAll("\\,", ""));

                output              += curOperatingIncome - prevOperatingIncome;
                curOperatingIncome  = prevOperatingIncome;
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
