package com.jihun.study.openDartApi.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import javax.naming.LimitExceededException;

public interface ApiService<T> {
    public ResponseEntity<T> get(String url, HttpHeaders httpHeaders) throws LimitExceededException, InterruptedException;
    public ResponseEntity<T> get(String url, HttpHeaders httpHeaders, Class<T> clazz) throws LimitExceededException, InterruptedException;
    public ResponseEntity<T> post(String url, HttpHeaders httpHeaders, Object body) throws LimitExceededException, InterruptedException;
    public ResponseEntity<T> post(String url, HttpHeaders httpHeaders, Object body, Class<T> clazz) throws LimitExceededException, InterruptedException;
}
