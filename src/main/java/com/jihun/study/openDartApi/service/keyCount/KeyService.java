package com.jihun.study.openDartApi.service.keyCount;

import javax.naming.LimitExceededException;

public interface KeyService {
    String getKey() throws LimitExceededException, InterruptedException;
}
