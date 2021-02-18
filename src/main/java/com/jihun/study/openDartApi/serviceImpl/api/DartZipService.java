package com.jihun.study.openDartApi.serviceImpl.api;

import com.jihun.study.openDartApi.service.api.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service("DartZipService")
public class DartZipService implements ApiService<byte[]> {
    private RestTemplate restTemplate;

    @Autowired
    public DartZipService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public ResponseEntity<byte[]> get(String url, HttpHeaders httpHeaders) {
        return callApiEndpoint(url, HttpMethod.GET, httpHeaders, null, byte[].class);
    }

    public ResponseEntity<byte[]> get(String url, HttpHeaders httpHeaders, Class<byte[]> clazz) {
        return callApiEndpoint(url, HttpMethod.GET, httpHeaders, null, clazz);
    }

    public ResponseEntity<byte[]> post(String url, HttpHeaders httpHeaders, Object body) {
        return callApiEndpoint(url, HttpMethod.POST, httpHeaders, body, byte[].class);
    }

    public ResponseEntity<byte[]> post(String url, HttpHeaders httpHeaders, Object body, Class<byte[]> clazz) {
        return callApiEndpoint(url, HttpMethod.POST, httpHeaders, body, clazz);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private ResponseEntity<byte[]> callApiEndpoint(String url, HttpMethod httpMethod, HttpHeaders httpHeaders, Object body, Class<byte[]> clazz) {
        ResponseEntity<byte[]> response = restTemplate.exchange(url, httpMethod, new HttpEntity<>(body, httpHeaders), clazz);
        return response;
    }
}
