package com.longjiabo.fund.controller.fund;

import com.longjiabo.fund.repository.HistoryRepository;
import com.longjiabo.fund.util.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/historys")
public class DailyController {
    private static final Logger log = LoggerFactory.getLogger(DailyController.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    HistoryRepository historyRepository;

    @GetMapping
    public Object get(String code) {
        return historyRepository.findAllByCodeOrderByPriceDateDesc(code);
    }


    @GetMapping("/daily/{s}/{en}")
    public Object daily(@PathVariable String s, @PathVariable String en) {
        Date start = DateTimeUtils.parseDate(s, "yyyy-MM-dd");
        Date end = DateTimeUtils.parseDate(en, "yyyy-MM-dd");
        if (start == null)
            start = new Date();
        if (end == null)
            end = new Date();
        Calendar st = Calendar.getInstance();
        st.setTime(start);
        st.set(Calendar.HOUR_OF_DAY, 0);
        st.set(Calendar.MINUTE, 0);
        st.set(Calendar.SECOND, 0);
        start = st.getTime();
        st.setTime(end);
        st.set(Calendar.HOUR_OF_DAY, 0);
        st.set(Calendar.MINUTE, 0);
        st.set(Calendar.SECOND, 0);
        st.add(Calendar.DAY_OF_MONTH, 1);
        end = st.getTime();
        List<Map<String, Object>> all = new ArrayList<Map<String, Object>>();
        while (start.before(end)) {
            Calendar next = Calendar.getInstance();
            next.setTime(start);
            next.add(Calendar.DAY_OF_MONTH, 1);
            if (next.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY
                    && next.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                String sql = "select concat(a.name,'(',a.code,')') as code,b.price1,b.price2,b.price_date as priceDate,b.created_on as createdOn from target as a left join (select * from history where price_date>=? and price_date<?)"
                        + " as b on a.code=b.code where a.type=1";
                List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, new Object[]{start, next.getTime()});
                String priceDate = DateTimeUtils.formatDate(start, "MM-dd");
                for (Map<String, Object> map : list) {
                    if (map.get("priceDate") == null) {
                        map.put("priceDate", priceDate);
                    } else {
                        map.put("priceDate", DateTimeUtils.formatDate((Date) map.get("priceDate"), "MM-dd"));
                    }
                    if (map.get("createdOn") != null) {
                        map.put("createdOn", DateTimeUtils.formatDate((Date) map.get("createdOn"), "MM-dd HH:mm"));
                    }
                }
                all.addAll(list);
            }
            start = next.getTime();
        }
        return all;
    }
}
