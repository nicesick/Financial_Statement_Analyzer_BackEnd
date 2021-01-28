package com.jihun.study.openDartApi.service;

import com.jihun.study.openDartApi.dto.DartDto;
import org.jdom2.JDOMException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

import javax.naming.LimitExceededException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface StockService {
    public ResponseEntity<List<DartDto>>    getCorpInfos(@Nullable Boolean isEvalDone, @Nullable Boolean isIssued, @Nullable Character corpCls, @Nullable String corpName);
    public ResponseEntity<DartDto>          getCorpDetail(String corpCode);
    public ResponseEntity<DartDto>          update();
    public ResponseEntity<DartDto>          getUpdate();
}
