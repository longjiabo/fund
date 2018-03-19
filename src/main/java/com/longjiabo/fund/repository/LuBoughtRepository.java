package com.longjiabo.fund.repository;

import com.longjiabo.fund.model.lu.LuBought;
import org.springframework.data.repository.CrudRepository;

public interface LuBoughtRepository extends CrudRepository<LuBought, Integer> {
    Iterable<LuBought> findAllByOrderByCreatedOnDesc();
}
