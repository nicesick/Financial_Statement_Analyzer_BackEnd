package com.jihun.study.openDartApi.serviceImpl.keyCount;

import com.jihun.study.openDartApi.entity.count.RequestCount;
import com.jihun.study.openDartApi.entity.count.RequestCountPK;
import com.jihun.study.openDartApi.repository.RequestCountRepository;
import com.jihun.study.openDartApi.service.CountService;
import com.jihun.study.openDartApi.service.KeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.naming.LimitExceededException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DartKeyCountService implements CountService, KeyService {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private static final int MAX_REQ_PER_DAY = 10000;
    private static final int MAX_REQ_PER_MIN = 900;

    public static final int REQ_OVER_DAY    = 1;
    public static final int REQ_OVER_MIN    = 2;
    public static final int REQ_OK          = 3;

    private RequestCountRepository  requestCountRepository;
    private AtomicInteger           atomicInteger;
    private List<String>            dartKeys;
    private int                     totMaxReqPerDay;

    @Autowired
    public DartKeyCountService(
            Environment                 environment
            , RequestCountRepository    requestCountRepository
    ) {
        this.requestCountRepository = requestCountRepository;
        atomicInteger               = new AtomicInteger(0);

        dartKeys                    = new ArrayList<>();
        for (String dartKey : Arrays.asList(environment.getProperty("dart.key").split(","))) {
            dartKeys.add(dartKey.trim());
        }
        totMaxReqPerDay             = MAX_REQ_PER_DAY * dartKeys.size();
    }

    @Override
    public int isCountOver(LocalDate localDate, LocalTime localTime) {
//        logger.debug("isCountOver : countInDay = " + countInDay);
//        logger.debug("isCountOver : countInMin = " + countInMin);
        int countInDay = getCount(localDate, localTime);
        System.out.println("isCountOver : countInDay = " + countInDay);

        return countInDay >= totMaxReqPerDay ? REQ_OVER_DAY : atomicInteger.get() >= MAX_REQ_PER_MIN ? REQ_OVER_MIN : REQ_OK;
    }

    @Override
    public int addCount(LocalDate localDate, LocalTime localTime) throws InterruptedException {
        int output = 0;

/*        Optional<RequestCount> requestCount = requestCountRepository.findById(new RequestCountPK(localDate, localTime));

        if (requestCount.isPresent()) {
            RequestCount presentRequestCount = requestCount.get();
            presentRequestCount.setCount(presentRequestCount.getCount() + 1);

            output = presentRequestCount.getCount();
            requestCountRepository.save(presentRequestCount);
        } else {
            RequestCount newRequestCount = new RequestCount(new RequestCountPK(localDate, localTime));

            output = 1;
            requestCountRepository.save(newRequestCount);
        }*/
        output = atomicInteger.incrementAndGet();
        if (output >= MAX_REQ_PER_MIN) {
            saveCount(output, localDate, localTime);

            Thread.sleep(60000);
            output = atomicInteger.getAndSet(0);
        }

        return output;
    }

    @Override
    public void saveCount(int count, LocalDate localDate, LocalTime localTime) {
        Optional<RequestCount> requestCount = requestCountRepository.findById(new RequestCountPK(localDate, localTime));

        if (requestCount.isPresent()) {
            RequestCount presentRequestCount = requestCount.get();
            presentRequestCount.setCount(presentRequestCount.getCount() + count);

            requestCountRepository.save(presentRequestCount);
        } else {
            RequestCount newRequestCount = new RequestCount(new RequestCountPK(localDate, localTime));
            newRequestCount.setCount(count);
            requestCountRepository.save(newRequestCount);
        }
    }

    @Override
    public int getCount(LocalDate localDate, LocalTime localTime) {
        int countInDay = 0;
//        int countInMin = 0;

        List<RequestCount> requestCountList = requestCountRepository.findAllByRequestCountPKLocalDate(localDate);

//        if (requestCountList.size() == 0) {
//            return REQ_OK;
//        }

        for (RequestCount requestCount : requestCountList) {
            countInDay += requestCount.getCount();
        }

        return countInDay + atomicInteger.get();
    }

    @Override
    public String getKey() throws LimitExceededException, InterruptedException {
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now().withSecond(0);

        if (this.isCountOver(localDate, localTime) == this.REQ_OVER_DAY) {
            throw new LimitExceededException();
        }
//        else if (countOverFlag == DartCountService.REQ_OVER_MIN) {
//            Thread.sleep(60000);
//        }

        this.addCount(localDate, localTime);
        String key = dartKeys.get(this.getCount(localDate, localTime) / MAX_REQ_PER_DAY);
        System.out.println("key = " + key);

        return key;
    }

    @Override
    protected void finalize() {
        if (atomicInteger != null && atomicInteger.get() != 0) {
            saveCount(atomicInteger.getAndSet(0), LocalDate.now(), LocalTime.now().withSecond(0));
        }
    }
}
