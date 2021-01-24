package com.jihun.study.openDartApi;

import com.jihun.study.openDartApi.service.ApiService;
import com.jihun.study.openDartApi.utils.evaluator.CorpEvaluator;
import com.jihun.study.openDartApi.utils.stream.ZipStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.Map;

@SpringBootTest
class OpenDartApiApplicationTests {
	private Environment environment;
	private ApiService corpKeysService;
	private ApiService  dartService;

	@Autowired
	public OpenDartApiApplicationTests(
			Environment environment
			, @Qualifier("DartZipService")  ApiService corpKeysService
			, @Qualifier("DartTestService") ApiService dartService
	) {
		this.environment        = environment;
		this.corpKeysService    = corpKeysService;
		this.dartService        = dartService;
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
