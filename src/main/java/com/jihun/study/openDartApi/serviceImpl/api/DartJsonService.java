package com.jihun.study.openDartApi.serviceImpl.api;

import com.jihun.study.openDartApi.dto.DartApiDto;
import com.jihun.study.openDartApi.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service("DartJsonService")
public class DartJsonService implements ApiService<DartApiDto> {
    private RestTemplate restTemplate;

    @Autowired
    public DartJsonService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<DartApiDto> get(String url, HttpHeaders httpHeaders) {
        return callApiEndpoint(url, HttpMethod.GET, httpHeaders, null, DartApiDto.class);
    }

    public ResponseEntity<DartApiDto> get(String url, HttpHeaders httpHeaders, Class<DartApiDto> clazz) {
        return callApiEndpoint(url, HttpMethod.GET, httpHeaders, null, clazz);
    }

    public ResponseEntity<DartApiDto> post(String url, HttpHeaders httpHeaders, Object body) {
        return callApiEndpoint(url, HttpMethod.POST, httpHeaders, body,DartApiDto.class);
    }

    public ResponseEntity<DartApiDto> post(String url, HttpHeaders httpHeaders, Object body, Class<DartApiDto> clazz) {
        return callApiEndpoint(url, HttpMethod.POST, httpHeaders, body, clazz);
    }

    private ResponseEntity<DartApiDto> callApiEndpoint(String url, HttpMethod httpMethod, HttpHeaders httpHeaders, Object body, Class<DartApiDto> clazz) {
        ResponseEntity<DartApiDto> response = restTemplate.exchange(url, httpMethod, new HttpEntity<>(body, httpHeaders), clazz);

        if (response.getStatusCodeValue() == 200
            && ("000".equals(response.getBody().getStatus())
            || "013".equals(response.getBody().getStatus()))
        ) {
            return response;
        } else {
            System.out.println("response.getBody().getStatus() = " + response.getBody().getStatus());
            System.out.println("response.getBody().getMessage() = " + response.getBody().getMessage());

            throw new IllegalStateException();
        }
    }
}
