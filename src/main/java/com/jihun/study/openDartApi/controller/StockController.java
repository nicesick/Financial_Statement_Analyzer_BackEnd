package com.jihun.study.openDartApi.controller;

import com.jihun.study.openDartApi.dto.stock.DartDto;
import com.jihun.study.openDartApi.service.keyCount.CountService;
import com.jihun.study.openDartApi.service.stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class StockController {
    private StockService stockService;
    private CountService countService;

    @Autowired
    public StockController(StockService stockService, CountService countService) {
        this.stockService = stockService;
        this.countService = countService;
    }

//    @RequestMapping("/dto")
//    public CorpDetail dtoTest() {
//        CorpDetail corpDetail = new CorpDetail();
//
//        System.out.println("corpDetail = " + corpDetail.toString());
//        return corpDetail;
//    }

//    @RequestMapping("/count")
//    public synchronized int count() {
//        int result  = countService.isCountOver(LocalDate.now(), LocalTime.now().withSecond(0));
//        int count   = countService.addCount(LocalDate.now(), LocalTime.now().withSecond(0));
//
//        return count;
//    }

    @GetMapping("/search")
    public ResponseEntity<List<DartDto>> getCorpInfo(
              @Nullable Boolean     isEvalDone
            , @Nullable Boolean     isIssued
            , @Nullable Character   corpCls
            , @Nullable String      corpName
    ) {
        ResponseEntity<List<DartDto>> output = stockService.getCorpInfos(isEvalDone, isIssued, corpCls, corpName);
        return output;
    }

    @GetMapping("/search/{corpCode}")
    public ResponseEntity<DartDto> getCorpDetail(@PathVariable String corpCode) {
        ResponseEntity<DartDto> output = stockService.getCorpDetail(corpCode);
        return output;
    }

    @GetMapping("/update")
    public ResponseEntity<DartDto> getUpdate() {
        ResponseEntity<DartDto> output = stockService.getUpdate();
        return output;
    }

    @PostMapping("/update")
    public ResponseEntity<DartDto> postUpdate() {
        ResponseEntity<DartDto> output = stockService.update();
        return output;
    }

    @RequestMapping("/test/update")
    public ResponseEntity<DartDto> testUpdate() {
        ResponseEntity<DartDto> output = stockService.update();
        return output;
    }
}
