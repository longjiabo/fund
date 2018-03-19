package com.longjiabo.fund.repository;

import com.longjiabo.fund.model.ihome.IpInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface IpInfoRepository extends CrudRepository<IpInfo,Integer>{
    @Query(value = "select * from ip_info order by created_on desc limit 0,1",nativeQuery = true)
    IpInfo findCurrentIp();
}
