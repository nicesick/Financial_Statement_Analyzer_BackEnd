package com.jihun.study.openDartApi.service;

import javax.naming.LimitExceededException;

public interface KeyService {
    String getKey() throws LimitExceededException, InterruptedException;
}
