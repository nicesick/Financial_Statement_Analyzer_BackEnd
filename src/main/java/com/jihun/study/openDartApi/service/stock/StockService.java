package com.jihun.study.openDartApi.service.stock;

import com.jihun.study.openDartApi.dto.stock.DartDto;
import com.jihun.study.openDartApi.dto.stock.DartUpdate;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

import java.util.List;

public interface StockService {
    public ResponseEntity<List<DartDto>>    getCorpInfos(@Nullable Character corpCls, @Nullable String corpName);
    public ResponseEntity<DartDto>          getCorpDetail(String corpCode);
    public ResponseEntity<DartUpdate>       update();
    public ResponseEntity<DartUpdate>       getUpdate();
}
