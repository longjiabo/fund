package com.longjiabo.fund.repository;

import com.longjiabo.fund.model.fund.History;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends CrudRepository<History, Integer> {
    List<History> findAllByCode(String code);

    List<History> findAllByCodeOrderByPriceDateDesc(String code);

    History findTop1ByOrderByPriceDateDesc();

    Optional<History> findByPriceDateAndCode(Date date,String code);

    @Query(value = "select * from history where price_date>=?1 and price_date<?2", nativeQuery = true)
    List<History> findBetweenPriceDate(Date start, Date end);

    @Query(value = "select * from history where price_date>=?1 and code not in (select code from target where type!=1) order by price_date desc", nativeQuery = true)
    List<History> findAfterPriceDate(Date d);

    List<History> findAllByOrderByPriceDateAsc();

    @Query(value = "select * from history where  price_date>=?1 and price_date<?2 and code=?3", nativeQuery = true)
    List<History> findBetweenPriceDateAndCodeOrderbyPriceDateAsc(Date start, Date end, String code);

    @Query(value = "select * from history where code=?1 order by price_date desc limit 1,1", nativeQuery = true)
    Optional<History> findYesterdayHistoryByCode(String code);

    @Query(value = "select * from history where code in (select code from transaction) and code in (select code from target where type =?1) order by price_date desc limit 0,1", nativeQuery = true)
    History findLatestHistoryWhichinTransactionByType(String type);

    @Query(value = "select count(1) from history where price_date=?1 and code  in (select code from target where type='1')", nativeQuery = true)
    Integer isExistPriceDateForOtherTarget(Date date);


}
