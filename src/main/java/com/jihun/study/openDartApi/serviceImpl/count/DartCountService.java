package com.jihun.study.openDartApi.serviceImpl.count;

import com.jihun.study.openDartApi.entity.count.RequestCount;
import com.jihun.study.openDartApi.entity.count.RequestCountPK;
import com.jihun.study.openDartApi.repository.RequestCountRepository;
import com.jihun.study.openDartApi.service.CountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class DartCountService implements CountService {
    private Logger logger = LoggerFactory.getLogger("DartCountService");

    private static final int MAX_REQ_PER_DAY = 10000;
    private static final int MAX_REQ_PER_MIN = 500;

    public static final int REQ_OVER_DAY    = 1;
    public static final int REQ_OVER_MIN    = 2;
    public static final int REQ_OK          = 3;

    private RequestCountRepository requestCountRepository;

    @Autowired
    public DartCountService(RequestCountRepository requestCountRepository) {
        this.requestCountRepository = requestCountRepository;
    }

    @Override
    public synchronized int isCountOver(LocalDate localDate, LocalTime localTime) {
        int countInDay = 0;
        int countInMin = 0;

        List<RequestCount> requestCountList = requestCountRepository.findAllByRequestCountPKLocalDate(localDate);

        if (requestCountList.size() == 0) {
            return REQ_OK;
        }

        for (RequestCount requestCount : requestCountList) {
            countInDay += requestCount.getCount();
        }

        Optional<RequestCount> requestCount = requestCountRepository.findById(new RequestCountPK(localDate, localTime));

        if (requestCount.isPresent()) {
            countInMin = requestCount.get().getCount();
        }

//        logger.debug("isCountOver : countInDay = " + countInDay);
//        logger.debug("isCountOver : countInMin = " + countInMin);

        System.out.println("isCountOver : countInDay = " + countInDay);
        System.out.println("isCountOver : countInMin = " + countInMin);

        return countInDay + countInMin >= MAX_REQ_PER_DAY ? REQ_OVER_DAY : countInMin >= MAX_REQ_PER_MIN ? REQ_OVER_MIN : REQ_OK;
    }

    @Override
    public synchronized int addCount(LocalDate localDate, LocalTime localTime) {
        int output = 0;

        Optional<RequestCount> requestCount = requestCountRepository.findById(new RequestCountPK(localDate, localTime));

        if (requestCount.isPresent()) {
            RequestCount presentRequestCount = requestCount.get();
            presentRequestCount.setCount(presentRequestCount.getCount() + 1);

            output = presentRequestCount.getCount();
            requestCountRepository.save(presentRequestCount);
        } else {
            RequestCount newRequestCount = new RequestCount(new RequestCountPK(localDate, localTime));

            output = 1;
            requestCountRepository.save(newRequestCount);
        }

        return output;
    }
}
