package com.jihun.study.openDartApi.serviceImpl.api;

import com.jihun.study.openDartApi.dto.api.DartApiDto;
import com.jihun.study.openDartApi.service.api.ApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service("DartJsonService")
public class DartJsonService implements ApiService<DartApiDto> {
    private static final Logger logger = LoggerFactory.getLogger(DartJsonService.class.getSimpleName());

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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private ResponseEntity<DartApiDto> callApiEndpoint(String url, HttpMethod httpMethod, HttpHeaders httpHeaders, Object body, Class<DartApiDto> clazz) {
        ResponseEntity<DartApiDto> response = restTemplate.exchange(url, httpMethod, new HttpEntity<>(body, httpHeaders), clazz);

        if (response.getStatusCodeValue() == 200
            && ("000".equals(response.getBody().getStatus())
            || "013".equals(response.getBody().getStatus()))
        ) {
            return response;
        } else {
            logger.debug("response.getBody().getStatus() = " + response.getBody().getStatus());
            logger.debug("response.getBody().getMessage() = " + response.getBody().getMessage());

            throw new IllegalStateException();
        }
    }
}
