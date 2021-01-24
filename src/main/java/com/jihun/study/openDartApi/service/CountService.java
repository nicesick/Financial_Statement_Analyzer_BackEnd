package com.jihun.study.openDartApi.service;

import java.time.LocalDate;
import java.time.LocalTime;

public interface CountService {
    public int isCountOver(LocalDate localDate, LocalTime localDateTime);
    public int addCount(LocalDate localDate, LocalTime localDateTime);
}
