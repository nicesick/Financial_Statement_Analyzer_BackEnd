package com.jihun.study.openDartApi.controller;

import com.jihun.study.openDartApi.dto.stock.DartDto;
import com.jihun.study.openDartApi.dto.stock.DartUpdate;
import com.jihun.study.openDartApi.dtoImpl.stock.DartCorpDto;
import com.jihun.study.openDartApi.entity.stock.Corporation;
import com.jihun.study.openDartApi.service.evaluate.EvaluateService;
import com.jihun.study.openDartApi.service.evaluate.SortableService;
import com.jihun.study.openDartApi.service.stock.StockService;
import com.jihun.study.openDartApi.serviceImpl.evaluate.IssueEvaluateService;
import com.jihun.study.openDartApi.serviceImpl.stock.DartStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class StockController {
    private StockService            stockService;
    private List<EvaluateService>   evaluateServices;

    @Autowired
    public StockController(
                StockService stockService
                , @Qualifier("IssueEvaluateService") EvaluateService issueEvaluateService
                , @Qualifier("OperatingIncomeGrowthRatioEvaluationService") EvaluateService operatingIncomeGrowthRatioEvaluationService
    ) {
        this.stockService       = stockService;
        this.evaluateServices   = new ArrayList<>();

        this.evaluateServices.add(issueEvaluateService);
        this.evaluateServices.add(operatingIncomeGrowthRatioEvaluationService);
    }

    @GetMapping("/search")
    public ResponseEntity<List<DartDto>> getCorpInfo(
              @Nullable Character   corpCls
            , @Nullable String      corpName
            , @Nullable String      sortByService
    ) {
        ResponseEntity<List<DartDto>> output = stockService.getCorpInfos(corpCls, corpName);

        if (output.getStatusCodeValue() == 200
            && output.getBody().size() > 0
        ) {
            for (EvaluateService evaluateService : evaluateServices) {
                for (DartDto corpInfo : output.getBody()) {
                    evaluateService.evaluate(corpInfo);
                }

                if (evaluateService.getServiceName().equals(sortByService)
                    && evaluateService instanceof SortableService
                ) {
                    Collections.sort(output.getBody(), ((SortableService) evaluateService).getComparator());
                }
            }
        }
        return output;
    }

    @GetMapping("/search/{corpCode}")
    public ResponseEntity<DartDto> getCorpDetail(@PathVariable String corpCode) {
        ResponseEntity<DartDto> output = stockService.getCorpDetail(corpCode);

        if (output.getStatusCodeValue() == 200) {
            for (EvaluateService evaluateService : evaluateServices) {
                evaluateService.evaluate(output.getBody());
            }
        }
        return output;
    }

    @GetMapping("/search/reportUri")
    public ResponseEntity<String> getReportUri() {
        ResponseEntity<String> output = null;

        if (stockService instanceof DartStockService) {
            output = ((DartStockService) stockService).getReportUri();
        }
        return output;
    }

    @GetMapping("/search/corpCls")
    public ResponseEntity<List<String>> getCorpClses() {
        ResponseEntity<List<String>> output = null;

        if (stockService instanceof DartStockService) {
            output = ((DartStockService) stockService).getCorpClses();
        }
        return output;
    }

    @GetMapping("/search/evaluator")
    public ResponseEntity<List<Map<String, String>>> getEvaluators() {
        List<Map<String, String>> evaluators = new ArrayList<>();

        for (EvaluateService evaluateService : evaluateServices) {
            if (evaluateService instanceof SortableService) {
                evaluators.add(new HashMap<String, String>() {
                    {
                        put("name"  , evaluateService.getSimpleName());
                        put("value" , evaluateService.getServiceName());
                    }
                });
            }
        }

        return new ResponseEntity<>(evaluators, HttpStatus.OK);
    }

    @GetMapping("/update")
    public ResponseEntity<DartUpdate> getUpdate() {
        ResponseEntity<DartUpdate> output = stockService.getUpdate();
        return output;
    }

    @PostMapping("/update")
    public ResponseEntity<DartUpdate> postUpdate() {
        ResponseEntity<DartUpdate> output = stockService.update();
        return output;
    }
}
