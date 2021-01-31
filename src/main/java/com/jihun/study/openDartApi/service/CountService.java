package com.jihun.study.openDartApi.service;

import java.time.LocalDate;
import java.time.LocalTime;

public interface CountService {
    int  isCountOver(LocalDate localDate, LocalTime localDateTime);
    int  addCount(LocalDate localDate, LocalTime localDateTime) throws InterruptedException;
    void saveCount(int count, LocalDate localDate, LocalTime localDateTime);
    int  getCount(LocalDate localDate, LocalTime localDateTime);
}
