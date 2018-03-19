package com.longjiabo.fund.repository;

import com.longjiabo.fund.model.fund.Transaction;
import org.hibernate.annotations.SQLUpdate;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
    List<Transaction> findAllByOrderByTxnDateDesc();

    @Query(value = "select * from transaction where code=?1 and txn_date=?2 and (type=?3 or type=?4) and price is null", nativeQuery = true)
    List<Transaction> findNotCacul(String code, Date date, Integer type1, Integer type2);

    List<Transaction> findAllByGroupId(Integer groupId);

    @Query(value = "select * from transaction where group_id=?1 and code in (select code from target where type=?2) order by txn_date desc", nativeQuery = true)
    List<Transaction> findAllByGroupIdAndType(Integer groupId, String type);

    @Modifying
    @Transactional
    @Query(value = "delete from transaction where group_id=?1", nativeQuery = true)
    void deleteByGroupId(Integer id);
}
