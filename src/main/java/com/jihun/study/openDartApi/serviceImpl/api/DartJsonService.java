package com.jihun.study.openDartApi.serviceImpl.api;

import com.jihun.study.openDartApi.dto.DartApiDto;
import com.jihun.study.openDartApi.service.ApiService;
import com.jihun.study.openDartApi.service.CountService;
import com.jihun.study.openDartApi.serviceImpl.count.DartCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.naming.LimitExceededException;
import java.time.LocalDate;
import java.time.LocalTime;

@Service("DartJsonService")
public class DartJsonService implements ApiService<DartApiDto> {
    private RestTemplate restTemplate;
    private CountService countService;

    @Autowired
    public DartJsonService(RestTemplate restTemplate, CountService countService) {
        this.restTemplate = restTemplate;
        this.countService = countService;
    }

    public ResponseEntity<DartApiDto> get(String url, HttpHeaders httpHeaders) throws LimitExceededException, InterruptedException {
        return callApiEndpoint(url, HttpMethod.GET, httpHeaders, null, DartApiDto.class);
    }

    public ResponseEntity<DartApiDto> get(String url, HttpHeaders httpHeaders, Class<DartApiDto> clazz) throws LimitExceededException, InterruptedException {
        return callApiEndpoint(url, HttpMethod.GET, httpHeaders, null, clazz);
    }

    public ResponseEntity<DartApiDto> post(String url, HttpHeaders httpHeaders, Object body) throws LimitExceededException, InterruptedException {
        return callApiEndpoint(url, HttpMethod.POST, httpHeaders, body,DartApiDto.class);
    }

    public ResponseEntity<DartApiDto> post(String url, HttpHeaders httpHeaders, Object body, Class<DartApiDto> clazz) throws LimitExceededException, InterruptedException {
        return callApiEndpoint(url, HttpMethod.POST, httpHeaders, body, clazz);
    }

    @Transactional
    private ResponseEntity<DartApiDto> callApiEndpoint(String url, HttpMethod httpMethod, HttpHeaders httpHeaders, Object body, Class<DartApiDto> clazz) throws LimitExceededException, InterruptedException {
        int countOverFlag = countService.isCountOver(LocalDate.now(), LocalTime.now().withSecond(0));

        if (countOverFlag == DartCountService.REQ_OVER_DAY) {
            throw new LimitExceededException();
        } else if (countOverFlag == DartCountService.REQ_OVER_MIN) {
            Thread.sleep(60000);
        }

        countService.addCount(LocalDate.now(), LocalTime.now().withSecond(0));
        return restTemplate.exchange(url, httpMethod, new HttpEntity<>(body, httpHeaders), clazz);
    }
}
