package com.longjiabo.fund.repository;

import com.longjiabo.fund.model.fund.FundGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FundGroupRepository extends CrudRepository<FundGroup, Integer> {
    Iterable<FundGroup> findAllByOrderByDisplayOrderAsc();

    @Query(value = "select * from fund_group where id in (select distinct group_id from transaction where code in (select code from target where type=?1) )order by display_order ", nativeQuery = true)
    List<FundGroup> findAllByTargetTypeWhichInTransaction(String type);


}
