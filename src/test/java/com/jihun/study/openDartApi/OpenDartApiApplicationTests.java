package com.jihun.study.openDartApi;

import com.jihun.study.openDartApi.dtoImpl.api.DartApiDetailDto;
import com.jihun.study.openDartApi.dtoImpl.api.DartApiResponseDto;
import com.jihun.study.openDartApi.entity.stock.CorpDetail;
import com.jihun.study.openDartApi.entity.stock.Corporation;
import com.jihun.study.openDartApi.service.api.ApiService;
import com.jihun.study.openDartApi.service.keyCount.KeyService;
import com.jihun.study.openDartApi.utils.evaluator.CorpEvaluator;
import com.jihun.study.openDartApi.utils.parser.DartXmlParser;
import com.jihun.study.openDartApi.utils.stream.ZipStream;
import org.jdom2.JDOMException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import javax.naming.LimitExceededException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;

@SpringBootTest
class OpenDartApiApplicationTests {
	private Environment environment;
	private ApiService 	dartZipService;
	private ApiService  dartService;
	private ApiService	dartJsonService;
	private KeyService 	dartKeyCountService;

	@Autowired
	public OpenDartApiApplicationTests(
			  Environment 	environment
			, KeyService 	dartKeyCountService
			, @Qualifier("DartZipService")  ApiService dartZipService
			, @Qualifier("DartTestService") ApiService dartService
			, @Qualifier("DartJsonService") ApiService dartJsonService
	) {
		this.environment        	= environment;
		this.dartKeyCountService	= dartKeyCountService;

		this.dartZipService 		= dartZipService;
		this.dartService        	= dartService;
		this.dartJsonService 		= dartJsonService;
	}

	@Test
	public void getMaxCorpDetailLengthTest() throws LimitExceededException, InterruptedException, IOException, JDOMException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		final String CORP_CODE_URI = environment.getProperty("dart.corpCode.uri");

		ResponseEntity<byte[]> response = dartZipService.get(
				CORP_CODE_URI + "?"
						+ "crtfc_key=" + dartKeyCountService.getKey()
				, new HttpHeaders()
		);

		List<String> corpKeysXml = ZipStream.streamZip(response.getBody(), "UTF-8");

		String[]                    tags        = {"corp_code", "corp_name"};
		List<Map<String, String>>   corpKeys    = DartXmlParser.parse(corpKeysXml.get(0), tags);

		System.out.println("corpKeys.size() = " + corpKeys.size());

		final String 					CORP_DETAIL_URI = environment.getProperty("dart.corpDetail.uri");
		final int                       JOIN_LENGTH 	= 850;
		Map<String, List<CorpDetail>>   output      	= new HashMap<>();

		int         targetYear  = LocalDate.now().getYear();
		String[]    reprtCodes  = {"11011", "11014", "11012", "11013"};
		int         storeCount  = 0;

		while(storeCount <= 4
			&& (LocalDate.now().getYear() - targetYear) < 5
		) {
			boolean         isStored    = false;
			for (String reprtCode : reprtCodes) {
				int         joinIdx     = 0;
				String      corpKeysStr = joinCorpKeys(corpKeys, joinIdx, joinIdx + JOIN_LENGTH);

				while(!"".equals(corpKeysStr)) {
					ResponseEntity<DartApiResponseDto> response2 = dartJsonService.get(
							CORP_DETAIL_URI + "?"
									+ "crtfc_key="  + dartKeyCountService.getKey() + "&"
									+ "corp_code="  + corpKeysStr   + "&"
									+ "bsns_year="  + targetYear    + "&"
									+ "reprt_code=" + reprtCode
							, new HttpHeaders()
							, DartApiResponseDto.class
					);

					System.out.println("response2.getStatusCodeValue() = " + response2.getStatusCodeValue());
					System.out.println("response2.getBody().getStatus() = " + response2.getBody().getStatus());
					System.out.println("response2.getBody().getMessage() = " + response2.getBody().getMessage());

					if ("000".equals(response2.getBody().getStatus())
							|| "013".equals(response2.getBody().getStatus())) {
						if ("000".equals(response2.getBody().getStatus())) {
							parseDetailDto(output, response2.getBody());

							System.out.println("output.size() = " + output.size());
							isStored = true;
						}

						joinIdx 	+= JOIN_LENGTH;
						corpKeysStr = joinCorpKeys(corpKeys, joinIdx, joinIdx + JOIN_LENGTH);
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

		Collection<List<CorpDetail>> oneOutput = output.values();

		for (List<CorpDetail> oneOut : oneOutput) {
			for (CorpDetail corpDetail : oneOut) {
				System.out.println("corpDetail = " + corpDetail.toString());
			}
		}
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
	private String joinCorpKeys(final List<Map<String, String>> corpInfos, int fromIdx, int toIdx) {
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
			String corpInfo = corpInfos.get(idx).get("corp_code");

			stringBuilder.append(corpInfo);
			stringBuilder.append(',');
		}

		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		return stringBuilder.toString();
	}

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
				Method targetGetMethod = null;
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

	@Test
	public void corpInfosTest() throws LimitExceededException, InterruptedException {
		/**
		 * 삼성전자 - 00126380
		 * 셀트리온 - 00421045
		 */
		ResponseEntity<Corporation> response = dartJsonService.get(
				"https://opendart.fss.or.kr/api/company.json" + "?"
						+ "crtfc_key=" + dartKeyCountService.getKey() + "&"
						+ "corp_code=" + "00126380"
				, new HttpHeaders()
				, Corporation.class
		);

		System.out.println("response = " + response.getStatusCode());
		System.out.println("response = " + response.getBody().toString());
	}

	@Test
    public void corpEvaluatorTest() {
	    String corpCode             = "00000000";
	    char corpCls                = 'K';
	    String[] totEquities        = {"50000"};
        String[] incomeBeforeTaxes  = {"-25000"};

        boolean result = CorpEvaluator.isLossBeforeTax(corpCode, corpCls, totEquities, incomeBeforeTaxes);
    }

//	/**
//	 * Open Dart API 공시정보 획득 테스트 -> ZIP 해제 Utils : utils 로 이동
//	 *
//	 * 공시정보 결과
//	 *
//	 * {CORPDART.xml : 공시정보.toString()}
//	 */
//	@Test
//	public Map<String, InputStream> getCorpKeysTest() {
//		String corpKeyUri   = environment.getProperty("dart.corpKey.uri");
//		String key          = environment.getProperty("dart.key");
//
//		ResponseEntity<byte[]> response    = corpKeysService.get(
//				corpKeyUri
//						+ "?crtfc_key=" + key
//				, new HttpHeaders());
//		Map<String, InputStream>     results     = ZipStream.getZipStream(response.getBody(), "UTF-8");
//
//        System.out.println("results = " + results);
//
//		return results;
//	}
//
//	/**
//	 * for testing
//	 *
//	 * 00434456 - 일산약품
//	 * 00430964 - 굿앤엘에스
//	 * 00432403 - 한라판지
//	 *
//	 * Open Dart API 기업개황 테스트
//	 *
//	 * {
//	 * status	        에러 및 정보 코드		(※메시지 설명 참조)
//	 * message	        에러 및 정보 메시지		(※메시지 설명 참조)
//	 * corp_name	    정식명칭		정식회사명칭
//	 * corp_name_eng	영문명칭		영문정식회사명칭
//	 * stock_name	    종목명(상장사) 또는 약식명칭(기타법인)		종목명(상장사) 또는 약식명칭(기타법인)
//	 * stock_code	    상장회사인 경우 주식의 종목코드		상장회사의 종목코드(6자리)
//	 * ceo_nm	        대표자명		대표자명
//	 * corp_cls	        법인구분		법인구분 : Y(유가), K(코스닥), N(코넥스), E(기타)
//	 * jurir_no	        법인등록번호		법인등록번호
//	 * bizr_no	        사업자등록번호		사업자등록번호
//	 * adres	        주소		주소
//	 * hm_url	        홈페이지		홈페이지
//	 * ir_url	        IR홈페이지		IR홈페이지
//	 * phn_no	        전화번호		전화번호
//	 * fax_no	        팩스번호		팩스번호
//	 * induty_code	    업종코드		업종코드
//	 * est_dt	        설립일(YYYYMMDD)		설립일(YYYYMMDD)
//	 * acc_mt	        결산월(MM)		결산월(MM)
//	 * }
//	 */
//	@Test
//	public String getCompInfo() {
//		String compInfoUri  = environment.getProperty("dart.compInfo.uri");
//		String key          = environment.getProperty("dart.key");
//		String compKey      = "00126380";
//
//		ResponseEntity<String> response = dartService.get(
//				compInfoUri + "?"
//						+ "crtfc_key=" + key + "&"
//						+ "corp_code=" + compKey
//				, new HttpHeaders()
//		);
//
//		System.out.println("response = " + response.getBody());
//
//		return response.getBody();
//	}
//
//	/**
//	 * Open Dart API 다중회사 보고서 테스트
//	 *
//	 */
//	@Test
//	public String getCompRpts() {
//		String compRptUri   = environment.getProperty("dart.compRpt.uri");
//		String key          = environment.getProperty("dart.key");
//		String corpCode     = "00126380";
//		String bsnsYear     = "2019";
//		String rptCode      = "11011";
//
//		ResponseEntity<String> response = dartService.get(
//				compRptUri + "?"
//						+ "crtfc_key="  + key       + "&"
//						+ "corp_code="  + corpCode  + "&"
//						+ "bsns_year="  + bsnsYear  + "&"
//						+ "reprt_code=" + rptCode
//				, new HttpHeaders()
//		);
//
//		System.out.println("response = " + response.getBody());
//
//		return null;
//	}
//
//	/**
//	 * Open Dart API 재무제표 원본파일 테스트
//	 *
//	 * 해석 불가능..
//	 */
//	@Test
//	public Map<String, InputStream> getXBRL() {
//		String xbrlUri      = environment.getProperty("dart.xbrl.uri");
//		String key          = environment.getProperty("dart.key");
//		String rceptNo      = "20190401004781";
//		String reprtCode    = "11011";
//
//		ResponseEntity<byte[]>  response    = corpKeysService.get(
//				xbrlUri
//						+ "?crtfc_key=" + key       + "&"
//						+ "rcept_no="   + rceptNo   + "&"
//						+ "reprt_code=" + reprtCode
//				, new HttpHeaders());
//		Map<String, InputStream>     results     = ZipStream.getZipStream(response.getBody(), "UTF-8");
//
//		System.out.println("results = " + results);
//
//		return results;
//	}
//
//
//	/**
//	 * 공시서류 다운로드 테스트
//	 *
//	 * 00118008 - 동원금속
//	 * 20201116001231 - 반기보고서(2020.09)
//	 *
//	 * 인코딩 문제?? -> UTF-8 임에도 한글이 인식 불가
//	 */
//	@Test
//	public Map<String, InputStream> getDocument() {
//		String documentUri  = environment.getProperty("dart.document.uri");
//		String key          = environment.getProperty("dart.key");
//
//		ResponseEntity<byte[]>  response    = corpKeysService.get(
//				documentUri + "?"
//						+ "crtfc_key=" + key + "&"
//						+ "rcept_no=" + "20201116001231"
//				, new HttpHeaders());
//		Map<String, InputStream>     results     = ZipStream.getZipStream(response.getBody(), "UTF-8");
//		return results;
//	}
}
