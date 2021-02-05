package com.jihun.study.openDartApi.repository.stock;

import com.jihun.study.openDartApi.entity.stock.CorpDetail;
import com.jihun.study.openDartApi.entity.stock.CorpDetailPK;
import org.springframework.data.repository.CrudRepository;

public interface CorpDetailRepository extends CrudRepository<CorpDetail, CorpDetailPK> {
}
