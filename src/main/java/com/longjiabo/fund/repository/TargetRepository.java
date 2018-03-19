package com.longjiabo.fund.repository;

import com.longjiabo.fund.model.fund.Target;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TargetRepository extends CrudRepository<Target, Integer> {

    List<Target> findAllByOrderByCodeAsc();
    List<Target> findAllByType(String type);
    @Query(value = "select * from target where type=?1 order by code ", nativeQuery = true)
    List<Target> findAllByTypeByOrderByCodeAsc(String type);
    Target findByCode(String code);
}
