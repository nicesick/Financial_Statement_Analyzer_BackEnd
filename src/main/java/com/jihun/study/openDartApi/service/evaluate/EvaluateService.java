package com.jihun.study.openDartApi.service.evaluate;

import com.jihun.study.openDartApi.dto.stock.DartDto;

import java.util.List;

public interface EvaluateService {
    public void     evaluate(DartDto corpInfos);
    public String   getServiceName();
    public boolean  isSortable();
}
