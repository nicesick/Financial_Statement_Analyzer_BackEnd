package com.jihun.study.openDartApi.service.evaluate;

import com.jihun.study.openDartApi.dto.stock.DartDto;

public interface EvaluateService {
    public void     evaluate(DartDto corpInfo);
    public String   getServiceName();
    public String   getSimpleName();
}
