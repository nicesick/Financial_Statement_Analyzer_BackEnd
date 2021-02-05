package com.jihun.study.openDartApi.repository.stock;

import com.jihun.study.openDartApi.entity.stock.Corporation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.stream.Stream;

public interface CorporationRepository extends CrudRepository<Corporation, String> {
    Stream<Corporation> findAllByIsEvalDone(boolean isEvalDone);
    Stream<Corporation> findAllByIsIssued(boolean isIssued);
    Stream<Corporation> findAllByCorpCls(char corpCls);
    Stream<Corporation> findAllByCorpNameLike(String corpName);

    List<Corporation> findAllByIsEvalDoneAndIsIssuedAndCorpCls(boolean isEvalDone, boolean isIssued, char corpCls);
    List<Corporation> findAllByIsEvalDoneAndIsIssuedAndCorpClsAndCorpNameContaining(boolean isEvalDone, boolean isIssued, char corpCls, String corpName);
}
