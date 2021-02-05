package com.jihun.study.openDartApi.serviceImpl.stock;

import com.jihun.study.openDartApi.dto.stock.DartDto;
import com.jihun.study.openDartApi.dtoImpl.api.DartApiDetailDto;
import com.jihun.study.openDartApi.dtoImpl.api.DartApiResponseDto;
import com.jihun.study.openDartApi.dtoImpl.stock.DartCorpDto;
import com.jihun.study.openDartApi.entity.stock.CorpDetail;
import com.jihun.study.openDartApi.entity.stock.CorpUpdate;
import com.jihun.study.openDartApi.entity.stock.Corporation;
import com.jihun.study.openDartApi.repository.stock.CorpDetailRepository;
import com.jihun.study.openDartApi.repository.stock.CorpUpdateRepository;
import com.jihun.study.openDartApi.repository.stock.CorporationRepository;
import com.jihun.study.openDartApi.service.api.ApiService;
import com.jihun.study.openDartApi.service.keyCount.KeyService;
import com.jihun.study.openDartApi.service.stock.StockService;
import com.jihun.study.openDartApi.utils.evaluator.CorpEvaluator;
import com.jihun.study.openDartApi.utils.parser.DartXmlParser;
import com.jihun.study.openDartApi.utils.stream.ZipStream;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.LimitExceededException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class DartStockService implements StockService {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private CorporationRepository   corporationRepository;
    private CorpDetailRepository    corpDetailRepository;
    private CorpUpdateRepository    corpUpdateRepository;
    private KeyService              dartKeyCountService;

    private ApiService dartJsonService;
    private ApiService dartZipService;

//    private final String DART_KEY;
    private final String CORP_CODE_URI;
    private final String CORP_INFO_URI;
    private final String CORP_DETAIL_URI;

    private final Map<String, String> DETAIL_MAPPER = new HashMap<String, String>() {{
        put("자산총계" , "TotAssets");
        put("부채총계" , "TotLiability");
        put("자본총계" , "TotStockholdersEquity");
        put("자본금" , "StockholdersEquity");
        put("매출액" , "Revenue");
        put("영업이익" , "OperatingIncome");
        put("법인세차감전 순이익" , "IncomeBeforeTax");
        put("당기순이익" , "NetIncome");
    }};

    @Autowired
    public DartStockService(
            Environment environment
            , CorporationRepository         corporationRepository
            , CorpDetailRepository          corpDetailRepository
            , CorpUpdateRepository          corpUpdateRepository
            , KeyService                    dartKeyCountService
            , @Qualifier("DartJsonService") ApiService dartJsonService
            , @Qualifier("DartZipService")  ApiService dartZipService
    ) {
        this.corporationRepository  = corporationRepository;
        this.corpDetailRepository   = corpDetailRepository;
        this.corpUpdateRepository   = corpUpdateRepository;
        this.dartKeyCountService    = dartKeyCountService;

        this.dartJsonService    = dartJsonService;
        this.dartZipService     = dartZipService;

//        this.DART_KEY           = environment.getProperty("dart.key");
        this.CORP_CODE_URI      = environment.getProperty("dart.corpCode.uri");
        this.CORP_INFO_URI      = environment.getProperty("dart.corpInfo.uri");
        this.CORP_DETAIL_URI    = environment.getProperty("dart.corpDetail.uri");
    }

    @Override
    public ResponseEntity<List<DartDto>> getCorpInfos(
              @Nullable Boolean     isEvalDone
            , @Nullable Boolean     isIssued
            , @Nullable Character   corpCls
            , @Nullable String      corpName
    ) {
//        System.out.println("isEvalDone  = " + isEvalDone);
//        System.out.println("isIssued    = " + isIssued);
//        System.out.println("corpCls     = " + corpCls);

        if (isEvalDone == null || isIssued == null || corpCls == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        List<Corporation> results = null;

        if (corpName == null) {
            results = corporationRepository.findAllByIsEvalDoneAndIsIssuedAndCorpCls(isEvalDone, isIssued, corpCls);
        } else {
            results = corporationRepository.findAllByIsEvalDoneAndIsIssuedAndCorpClsAndCorpNameContaining(isEvalDone, isIssued, corpCls, corpName);
        }

        List<DartCorpDto> corporations = new ArrayList<>();

        for (Corporation result : results) {
            DartCorpDto corpDto = new DartCorpDto(result.getCorpCode(), result.getCorpName(), result.getCorpCls());

            corporations.add(corpDto);
        }

        return new ResponseEntity(corporations, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DartDto> getCorpDetail(String corpCode) {
        if (corpCode == null || "".equals(corpCode)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Corporation> corporation = corporationRepository.findById(corpCode);

        if (corporation.isPresent()) {
            return new ResponseEntity(corporation, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<DartDto> getUpdate() {
        Optional<CorpUpdate> corpUpdate = corpUpdateRepository.findTopByProgressNotOrderByIdDesc("failed");

        if (corpUpdate.isPresent()) {
            return new ResponseEntity(corpUpdate.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<DartDto> update() {
        Optional<CorpUpdate> beforeStartCheckCorpUpdate = corpUpdateRepository.findTopByOrderByIdDesc();

        if (beforeStartCheckCorpUpdate.isPresent()) {
            CorpUpdate corpUpdate = beforeStartCheckCorpUpdate.get();
            if("updating".equals(corpUpdate.getProgress())) {
                return new ResponseEntity(corpUpdate, HttpStatus.OK);
            }
        }
        CorpUpdate newCorpUpdate = new CorpUpdate("updating", new Date());
        corpUpdateRepository.save(newCorpUpdate);

        String progress = "success";
        try {
//            Thread.sleep(60000);
            progressUpdating();
        } catch (Exception e) {
            progress = "failed";
            e.printStackTrace();
        } finally {
            Optional<CorpUpdate> afterUpdateSaveCorpUpdate = corpUpdateRepository.findTopByOrderByIdDesc();

            if (afterUpdateSaveCorpUpdate.isPresent()) {
                CorpUpdate corpUpdate = afterUpdateSaveCorpUpdate.get();

                corpUpdate.setProgress(progress);
                corpUpdate.setUpdateDate(new Date());

                corpUpdateRepository.save(corpUpdate);
                return new ResponseEntity<>(corpUpdate, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void progressUpdating() throws LimitExceededException, InterruptedException, IOException, IllegalAccessException, InvocationTargetException, JDOMException, NoSuchMethodException {
        List<Corporation>               corpKeys    = null;
        Map<String, List<CorpDetail>>   corpDetails = null;

        List<Corporation>               corpInfos   = null;

//        /**
//         * Testing Code
//         *
//         * 삼성전자 - 00126380
//         * 셀트리온 - 00421045
//         * HMM - 00164645
//         * 박셀바이오 - 01335851
//         * 현대차증권 - 00137997
//         * LG전자 - 00401731
//         * 셀리버리 - 01182444
//         * 셀트리온헬스케어 - 00554024
//         * 기아자동차 - 00106641
//         * SK하이닉스 - 00164779
//         * NAVER - 00266961
//         * 삼성바이오로직스 - 00877059
//         * 삼성SDI - 00126362
//         * 카카오 - 00918444
//         * 현대모비스 - 00164788
//         * SK이노베이션 - 00631518
//         * 삼성물산 - 00149655
//         * LG생활건강 - 00356370
//         * 엔씨소프트 - 00261443
//         * SK텔레콤 - 00159023
//         * LG디스플레이 - 00105873
//         * KB금융 - 00688996
//         * 신한지주 - 00382199
//         * 삼성에스디에스 - 00126186
//         * 삼성전기 - 00126371
//         * 한국전력공사 - 00159193
//         * 아모레퍼시픽 - 00583424
//         * 넷마블 - 00441854
//         * 케이티앤지 - 00244455
//         * 하나금융지주 - 00547583
//         * 한온시스템 - 00161125
//         * 포스코케미칼 - 00155276
//         * 금호석유화학 - 00106368
//         */
//        List<Map<String, String>>   corpKeysForTest     = new ArrayList<>();
//        String[]                    corpKeysSample      = {
//                 "00126380"
//                ,"00421045"
//                ,"00164645"
//                ,"01335851"
//                ,"00137997"
//                ,"00401731"
//                ,"01182444"
//                ,"00554024"
//                ,"00106641"
//                ,"00164779"
//                ,"00266961"
//                ,"00877059"
//                ,"00126362"
//                ,"00918444"
//                ,"00164788"
//                ,"00631518"
//                ,"00149655"
//                ,"00356370"
//                ,"00261443"
//                ,"00159023"
//                ,"00105873"
//                ,"00688996"
//                ,"00382199"
//                ,"00126186"
//                ,"00126371"
//                ,"00159193"
//                ,"00583424"
//                ,"00441854"
//                ,"00244455"
//                ,"00547583"
//                ,"00161125"
//                ,"00155276"
//                ,"00106368"};
//
//        for (String corpKeySample : corpKeysSample) {
//            Map<String, String> corpKeysForTestMap = new HashMap<>();
//            corpKeysForTestMap.put("corp_code", corpKeySample);
//
//            corpKeysForTest.add(corpKeysForTestMap);
//        }

        try {
//            corpKeys    = corpKeysForTest;
            /**
             * 2021.02.03
             *
             * API 호출 횟수를 줄이기 위해
             *
             * corpKeys -> corpInfos    -> corpDetails 순서가
             * corpKeys -> corpDetails  -> corpInfos 순서로 변경되었습니다.
             */
            corpKeys    = getCorpKeys();
            corpDetails = getCorpDetails(corpKeys);
            corpInfos   = addCorpDetails(corpKeys, corpDetails);
            corpInfos   = getCorpOverviews(corpInfos);

            corpInfos   = evalCorporation(corpInfos, corpDetails);
            saveCorporation(corpInfos);

            logger.info("update successfully processed");
        } catch (Exception exception) {
            logger.error("update failed");
            throw exception;
        }
    }

    /**
     * saveCorporation
     *
     * 기존 데이터를 전부 제거한 후, 새로 모은 데이터를 저장합니다.
     *
     * @param corpInfos
     */
    private void saveCorporation(List<Corporation> corpInfos) {
        corporationRepository.deleteAll();

        for (Corporation corpInfo : corpInfos) {
            logger.debug("saveCorporation : " + corpInfo.toString());
            corporationRepository.save(corpInfo);
        }
    }

    /**
     * evalCorporation
     *
     * Open Dart API 에서 받아온 데이터를 기반으로 기업의 관리종목 및 상장폐지 요건을 평가합니다.
     *
     * 평가한 결과는 Corporation Entity 에 저장됩니다.
     * 만약 평가할 수 없다면, Corportation Entity 의 Stat_eval_done 변수가 false 로 변경됩니다.
     *
     * @param oldCorpInfos
     * @param corpDetails
     */
    private List<Corporation> evalCorporation(final List<Corporation> oldCorpInfos, final Map<String, List<CorpDetail>> corpDetails) {
        List<Corporation> corpInfos = new CopyOnWriteArrayList<>(oldCorpInfos);

        for (Corporation corpInfo : corpInfos) {
            List<CorpDetail> targetCorpDetails = corpDetails.getOrDefault(corpInfo.getCorpCode(), null);

            if (targetCorpDetails == null) {
                corpInfos.remove(corpInfo);
                continue;
            } else {
                corpInfo.addCorpDetails(targetCorpDetails);
            }

            String[] revenues           = new String[targetCorpDetails.size()];
            String[] tot_equities       = new String[targetCorpDetails.size()];
            String[] equities           = new String[targetCorpDetails.size()];
            String[] incomeBeforeTexes  = new String[targetCorpDetails.size()];
            String[] operatingIncomes   = new String[targetCorpDetails.size()];

            for (int index = 0; index < targetCorpDetails.size(); index++) {
                revenues[index]             = targetCorpDetails.get(index).getRevenue();
                tot_equities[index]         = targetCorpDetails.get(index).getTotStockholdersEquity();
                equities[index]             = targetCorpDetails.get(index).getStockholdersEquity();
                incomeBeforeTexes[index]    = targetCorpDetails.get(index).getIncomeBeforeTax();
                operatingIncomes[index]     = targetCorpDetails.get(index).getOperatingIncome();

                if (revenues[index]             == null
                    || tot_equities[index]      == null
                    || equities[index]          == null
                    || incomeBeforeTexes[index] == null
                    || operatingIncomes[index]  == null
                ) {
                    corpInfo.setEvalDone(false);
                    break;
                }
            }

            if (corpInfo.isEvalDone()) {
                try {
                    corpInfo.setRevenueLack(CorpEvaluator.isRevenueLack(corpInfo.getCorpCode(), corpInfo.getCorpCls(), revenues));
                    corpInfo.setEquityImpairment(CorpEvaluator.isEquityImpairment(corpInfo.getCorpCode(), corpInfo.getCorpCls(), tot_equities, equities));
                    corpInfo.setOperatingLoss(CorpEvaluator.isOperatingLoss(corpInfo.getCorpCode(), corpInfo.getCorpCls(), operatingIncomes));
                    corpInfo.setLossBeforeTax(CorpEvaluator.isLossBeforeTax(corpInfo.getCorpCode(), corpInfo.getCorpCls(), tot_equities, incomeBeforeTexes));
                } catch (NumberFormatException e) {
                    corpInfo.setEvalDone(false);
                }
            }
        }

        logger.debug("evalCorporation : corpInfos = " + corpInfos.toString());
        return corpInfos;
    }

    /**
     * 2021.02.03
     *
     * CorpInfos, CorpDetails 순서가 바뀌면서 Corporation 에 CorpDetail 을 미리 입력합니다.
     *
     * @param oldCorpInfos
     * @param corpDetails
     *
     * @return Corporation 리스트
     */
    private List<Corporation> addCorpDetails(final List<Corporation> oldCorpInfos, final Map<String, List<CorpDetail>> corpDetails) {
        List<Corporation> corpInfos = new CopyOnWriteArrayList<>(oldCorpInfos);

        for (Corporation corpInfo : corpInfos) {
            List<CorpDetail> corpDetail = corpDetails.getOrDefault(corpInfo.getCorpCode(), null);

            if (corpDetail == null) {
                corpInfos.remove(corpInfo);
            } else {
                corpInfo.addCorpDetails(corpDetail);
            }
        }

        return corpInfos;
    }

    /**
     * getCorpDetails
     *
     * 1. Open Dart API를 통해 기업의 4년치 재무정보를 획득합니다.
     *
     * API를 통해 받는 데이터를 아래와 같습니다.
     *
     * {
     * status	            에러 및 정보 코드
     * message	            에러 및 정보 메시지
     * rcept_no	            접수번호(14자리)
     * bsns_year	        사업연도(4자리)
     * corp_code            고유번호(8자리)
     * stock_code	        상장회사의 종목코드(6자리)
     * reprt_code	        보고서 코드	            1분기보고서 : 11013, 반기보고서 : 11012, 3분기보고서 : 11014, 사업보고서 : 11011
     * account_nm	        계정명	                ex) 자본총계
     * fs_div	            개별/연결구분	            CFS:연결재무제표, OFS:재무제표
     * fs_nm	            개별/연결명	            ex) 연결재무제표 또는 재무제표 출력
     * sj_div	            재무제표구분	            BS:재무상태표, IS:손익계산서
     * sj_nm	            재무제표명	            ex) 재무상태표 또는 손익계산서 출력
     * thstrm_nm	        당기명	                ex) 제 13 기 3분기말
     * thstrm_dt	        당기일자	                ex) 2018.09.30 현재
     * thstrm_amount	    당기금액	                9,999,999,999
     * thstrm_add_amount	당기누적금액	            9,999,999,999
     * frmtrm_nm	        전기명	                ex) 제 12 기말
     * frmtrm_dt	        전기일자	                ex) 2017.01.01 ~ 2017.12.31
     * frmtrm_amount	    전기금액	                9,999,999,999
     * frmtrm_add_amount	전기누적금액	            9,999,999,999
     * bfefrmtrm_nm	        전전기명	                ex) 제 11 기말(※ 사업보고서의 경우에만 출력)
     * bfefrmtrm_dt	        전전기일자	            ex) 2016.12.31 현재(※ 사업보고서의 경우에만 출력)
     * bfefrmtrm_amount	    전전기금액	            9,999,999,999(※ 사업보고서의 경우에만 출력)
     * ord	                계정과목 정렬순서
     * }
     *
     * 2. 받은 데이터를 통해 CorpDetail Entity 형태로 변환합니다.
     *
     * 변환 후의 데이터 형식은 아래와 같습니다.
     *
     * {
     *     corp_code : [corpDetail, corpDetail, ...],
     *     ...
     * }
     *
     * @param corpInfos
     * @return
     *
     * @throws LimitExceededException
     * @throws InterruptedException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private Map<String, List<CorpDetail>> getCorpDetails(final List<Corporation> corpInfos) throws LimitExceededException, InterruptedException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final int                       JOIN_LENGTH = 850;
        Map<String, List<CorpDetail>>   output      = new HashMap<>();

        int         targetYear  = LocalDate.now().getYear();
        String[]    reprtCodes  = {"11011", "11014", "11012", "11013"};
        int         storeCount  = 0;

        while(storeCount <= 4
            && (LocalDate.now().getYear() - targetYear) < 5
        ) {
            boolean         isStored    = false;
            for (String reprtCode : reprtCodes) {
                int         joinIdx     = 0;
                String      corpKeysStr = joinCorpKeys(corpInfos, joinIdx, joinIdx + JOIN_LENGTH);

                while(!"".equals(corpKeysStr)) {
                    ResponseEntity<DartApiResponseDto> response = dartJsonService.get(
                            CORP_DETAIL_URI + "?"
                                    + "crtfc_key="  + dartKeyCountService.getKey() + "&"
                                    + "corp_code="  + corpKeysStr   + "&"
                                    + "bsns_year="  + targetYear    + "&"
                                    + "reprt_code=" + reprtCode
                            , new HttpHeaders()
                            , DartApiResponseDto.class
                    );

                    logger.debug("response : status     = " + response.getBody().getStatus());

                    if ("000".equals(response.getBody().getStatus())
                        || "013".equals(response.getBody().getStatus())
                    ) {
                        if ("000".equals(response.getBody().getStatus())) {
                            parseDetailDto(output, response.getBody());
                            isStored = true;
                        }

                        joinIdx     += JOIN_LENGTH;
                        corpKeysStr = joinCorpKeys(corpInfos, joinIdx, joinIdx + JOIN_LENGTH);
                    } else {
                        throw new IllegalAccessException();
                    }
                }

                if (isStored) {
                    storeCount++;
                    break;
                }
            }
            targetYear--;
        }

        return output;
    }

    /**
     * getCorpKeysStr
     *
     * Open Dart API 재무정보 요청에 필요한 요청기업 고유번호를 ',' 을 Separator 로 해서 join 합니다.
     *
     * 2021.02.02
     * 인덱스 범위만큼만 corpKeys 를 join 하는 것으로 변경
     * 
     * @param corpInfos
     * @param fromIdx
     * @param toIdx
     * 
     * @return 고유번호 문자열
     */
    private String joinCorpKeys(final List<Corporation> corpInfos, int fromIdx, int toIdx) {
        if (fromIdx < 0             || fromIdx >= corpInfos.size()
            || fromIdx >= toIdx     || toIdx <= fromIdx
            || toIdx < 0
        ) {
            return "";
        } else if (toIdx >= corpInfos.size()) {
            toIdx = corpInfos.size() - 1;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int idx = fromIdx ; idx < toIdx; idx++) {
            Corporation corpInfo = corpInfos.get(idx);
            
            stringBuilder.append(corpInfo.getCorpCode());
            stringBuilder.append(',');
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    /**
     * parseDetailDto
     *
     * DartResponseDto 안에 있는 DartDetailDto 값을 추출하여 CorpDetail 형식으로 변환합니다.
     *
     * @param input
     * @param dartApiResponseDto
     *
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void parseDetailDto(Map<String, List<CorpDetail>> input, final DartApiResponseDto dartApiResponseDto) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<DartApiDetailDto> dartApiDetailDtos = dartApiResponseDto.getList();

        for (DartApiDetailDto dartApiDetailDto : dartApiDetailDtos) {
            /**
             * DartDetailDto 값 추출은
             * 재무제표의 자산총계, 부채총계, 자본총계, 자본금, 매출액, 영업이익, 법인세차감전 순이익, 당기순이익을 대상으로 합니다.
             */
            if (
//                "CFS".equals(dartDetailDto.getFs_div()) &&
                DETAIL_MAPPER.containsKey(dartApiDetailDto.getAccount_nm())
            ) {
                /**
                 * input 에서 corp_code, bsns_year, reprt_code 값이 동일한 CorpDetail 을 추출합니다.
                 * 없다면 새로 생성하여 추가합니다.
                 */
                List<CorpDetail>    corpDetails         = input.getOrDefault(dartApiDetailDto.getCorp_code(), null);
                CorpDetail          targetCorpDetail    = null;

                if (corpDetails == null) {
                    corpDetails = new ArrayList<>();
                    input.put(dartApiDetailDto.getCorp_code(), corpDetails);
                }

                for (CorpDetail corpDetail : corpDetails) {
                    if (dartApiDetailDto.getBsns_year() == corpDetail.getBsnsYear()
                        && dartApiDetailDto.getReprt_code().equals(corpDetail.getReprtCode())
                    ) {
                        targetCorpDetail = corpDetail;
                        break;
                    }
                }

                if (targetCorpDetail == null) {
                    targetCorpDetail = new CorpDetail(
                            dartApiDetailDto.getCorp_code()
                            , dartApiDetailDto.getBsns_year()
                            , dartApiDetailDto.getReprt_code()
                            , dartApiDetailDto.getThstrm_dt());

                    corpDetails.add(targetCorpDetail);
                }

                /**
                 * 해당 변수에 값을 넣어줍니다.
                 *
                 * 연결재무제표 값이 우선시 되며, 만약 연결재무제표가 없다면 재무제표 값이 들어갑니다.
                 */
                Class   targetClass     = targetCorpDetail.getClass();
                Method  targetGetMethod = null;
                Method  targetSetMethod = null;

                for (Method method : targetClass.getDeclaredMethods()) {
                    if (method.getName().equals("set" + DETAIL_MAPPER.get(dartApiDetailDto.getAccount_nm()))) {
                        targetGetMethod = targetClass.getMethod("get" + DETAIL_MAPPER.get(dartApiDetailDto.getAccount_nm()));
                        targetSetMethod = method;
                        break;
                    }
                }

                if (targetSetMethod == null || targetGetMethod == null) {
                    throw new NoSuchMethodException();
                }

                Object methodGetResult = targetGetMethod.invoke(targetCorpDetail);
                if (methodGetResult == null || "0".equals(methodGetResult.toString())) {
                    targetSetMethod.invoke(targetCorpDetail, dartApiDetailDto.getThstrm_amount());
                }
            }
        }
    }

    /**
     * getCorpOverviews
     *
     * Open Dart API에서 획득한 고유번호를 이용해 추가적인 기업정보를 획득하여 기업정보 Entity 에 저장합니다.
     *
     * Response 형식은 다음과 같습니다.
     *
     * {
     * status	        에러 및 정보 코드
     * message	        에러 및 정보 메시지
     * corp_name	    정식명칭
     * corp_name_eng	영문명칭
     * stock_name	    종목명(상장사) 또는 약식명칭(기타법인)
     * stock_code	    상장회사인 경우 주식의 종목코드
     * ceo_nm	        대표자명
     * corp_cls	        법인구분 : Y(유가), K(코스닥), N(코넥스), E(기타)
     * jurir_no	        법인등록번호
     * bizr_no	        사업자등록번호
     * adres	        주소
     * hm_url	        홈페이지
     * ir_url	        IR홈페이지
     * phn_no	        전화번호
     * fax_no	        팩스번호
     * induty_code	    업종코드
     * est_dt	        설립일(YYYYMMDD)
     * acc_mt	        결산월(MM)
     * }
     *
     * 2021.02.03
     * input 형식이 List<Map<String, String>> 에서 List<Corporation> 으로 변경되었습니다.
     *
     * @param corpInfos
     * @return 기업정보 리스트
     *
     * @throws LimitExceededException
     * @throws InterruptedException
     */
    private List<Corporation> getCorpOverviews(final List<Corporation> corpInfos) throws LimitExceededException, InterruptedException {
        List<Corporation> output = new ArrayList<>();

        int countIdx = 1;
        for (Corporation corpInfo : corpInfos) {
            String corpCode = corpInfo.getCorpCode();

            if ("".equals(corpCode)) {
                countIdx++;
                continue;
            }

            ResponseEntity<Corporation> response = dartJsonService.get(
                    CORP_INFO_URI + "?"
                    + "crtfc_key=" + dartKeyCountService.getKey() + "&"
                    + "corp_code=" + corpCode
                    , new HttpHeaders()
                    , Corporation.class
            );

            System.out.println("response suceessed = "
                    + countIdx
                    + " / "
                    + corpInfos.size());
//            logger.debug("corpInfo = " + response.getBody().toString());

            /**
             * status 000 - 정상
             *
             * corp_cls Y - 유가증권
             * corp_cls K - 코스닥
             */
            if ("000".equals(response.getBody().getStatus())
                && ('Y' == response.getBody().getCorpCls()
                || 'K' == response.getBody().getCorpCls())
            ) {
                output.add(response.getBody());
            }
            countIdx++;
        }

        if (output.size() <= 0) {
            logger.error("corpInfos cannot be empty");
            throw new NegativeArraySizeException();
        }

        return output;
    }

    /**
     * getCorpKeys
     *
     * 1. Open Dart API 에서 제공하는 Zip(Binary) 파일을 압축해제하여 Corp Code 들을 수집합니다.
     *
     * ZIP 파일에는 CORPCODE.xml 하나만 존재하며 XML 파일 형식은 아래와 같습니다.
     *
     * <pre>
     *     <result>
     *         <list>
     *             <corp_code></corp_code>
     *             <corp_name></corp_name>
     *             <stock_code></stock_code>
     *             <modify_date></modify_date>
     *         </list>
     *         ...
     *     </result>
     * </pre>
     *
     * 2. 해당 XML 파일을 parsing 하여 List 형식으로 변환합니다.
     *
     * List 형식은 아래와 같습니다.
     *
     * <pre>
     *     [{
     *          corp_code   : ,
     *          corp_name   :
     *     }, ...]
     * </pre>
     *
     * 2021.02.03
     * return 형식이 List<Map<String, String>> -> List<Corporation> 형식으로 변경되었습니다.
     *
     * @return 기업 고유번호 리스트
     *
     * @throws LimitExceededException
     * @throws InterruptedException
     * @throws IOException
     * @throws JDOMException
     */
    private List<Corporation> getCorpKeys() throws LimitExceededException, InterruptedException, IOException, JDOMException {
        List<Corporation> output = new ArrayList<>();

        ResponseEntity<byte[]> response = dartZipService.get(
                CORP_CODE_URI + "?"
                + "crtfc_key=" + dartKeyCountService.getKey()
                , new HttpHeaders()
        );

        List<String> corpKeysXml = ZipStream.streamZip(response.getBody(), "UTF-8");

        if (corpKeysXml.size() <= 0) {
            logger.error("corpKeysXml cannot be empty");
            throw new IOException();
        }

        String[]                    tags        = {"corp_code", "corp_name"};
        List<Map<String, String>>   corpKeys    = DartXmlParser.parse(corpKeysXml.get(0), tags);

        for (Map<String, String> corpKey : corpKeys) {
            output.add(new Corporation(corpKey.get("corp_code"), corpKey.get("corp_name")));
        }

        return output;
    }
}