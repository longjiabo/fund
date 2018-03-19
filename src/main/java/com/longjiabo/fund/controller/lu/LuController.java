package com.longjiabo.fund.controller.lu;

import com.longjiabo.fund.bean.LuData;
import com.longjiabo.fund.job.HuoQiJob;
import com.longjiabo.fund.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/lu")
public class LuController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/data2")
    public Object data2(HttpServletRequest req) {
        String max = req.getParameter("maxMoney");
        String min = req.getParameter("minMoney");
        String condition = "left(date_format(created_on,'%y%m%d'),6) as date from lu_product where amount>? and amount<? group by date";
        String maxSql = "select max(rate) as rate," + condition;
        String minSql = "select min(rate) as rate," + condition;
        String avgSql = "select round(avg(rate),2) as rate," + condition;
        Object[] params = new Object[]{Double.valueOf(min), Double.valueOf(max)};
        List<Map<String, Object>> list = jdbcTemplate.queryForList(maxSql, params);
        Map<String, LuData> map = new HashMap<String, LuData>();
        for (Map<String, Object> m : list) {
            String date = m.get("date").toString();
            LuData ld = new LuData();
            map.put(date, ld);
            ld.setMax((Double) m.get("rate"));
            ld.setDate(date);
        }
        list = jdbcTemplate.queryForList(minSql, params);
        for (Map<String, Object> m : list) {
            String date = m.get("date").toString();
            LuData ld = map.get(date);
            ld.setMin((Double) m.get("rate"));
        }
        list = jdbcTemplate.queryForList(avgSql, params);
        for (Map<String, Object> m : list) {
            String date = m.get("date").toString();
            LuData ld = map.get(date);
            ld.setAvg((Double) m.get("rate"));
        }
        ArrayList<LuData> result = new ArrayList<LuData>();
        result.addAll(map.values());
        Collections.sort(result, Comparator.comparing(LuData::getDate)
        );
        return result;
    }

    @GetMapping("/data")
    public Object data(HttpServletRequest req) {
        String max = req.getParameter("maxMoney");
        String min = req.getParameter("minMoney");
        String start = req.getParameter("start");
        String end = req.getParameter("end");
        String type = req.getParameter("type");
        Date st = null, en = null;
        if (!StringUtils.isEmpty(start)) {
            st = DateTimeUtils.parseDate(start, "yyyy-MM-dd");
        }
        if (!StringUtils.isEmpty(end)) {
            en = DateTimeUtils.parseDate(end, "yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.setTime(en);
            c.add(Calendar.DAY_OF_MONTH, 1);
            en = c.getTime();
        }
        //1 e享  2 定期
        String table = "1".equals(type) ? "lu_product" : "lu_regular_product";
        String condition = "left(date_format(created_on,'%H:%i'),4) as date from " + table +
                " where amount>? and amount<? and (created_on>=? or ? is null) and (created_on<? or ? is null   )  group by date";
        String maxSql = "select max(rate) as rate," + condition;
        String minSql = "select min(rate) as rate," + condition;
        String avgSql = "select round(avg(rate),2) as rate," + condition;
        String medianSql = "SELECT sq.date, round(avg(sq.rate),2) as rate FROM ( "
                + "SELECT t1.row_number, t1.rate, t1.date FROM( "
                + "SELECT IF(@prev!=d.date, @rownum:=1, @rownum:=@rownum+1) as `row_number`, d.rate, @prev:=d.date AS date "
                + "FROM (select rate,left(date_format(created_on,'%H:%i'),4) as date from  " + table
                + " where amount>? and amount<? and (created_on>=? or ? is null) and (created_on<? or ? is null   ) "
                + ") d, (SELECT @rownum:=0, @prev:=NULL) r ORDER BY d.date, rate) as t1 INNER JOIN  ( "
                + "select count(*) as total_rows,left(date_format(created_on,'%H:%i'),4) as date from  " + table
                + " where amount>? and amount<? and (created_on>=? or ? is null) and (created_on<? or ? is null   ) "
                + "group by date ) as t2 ON t1.date = t2.date WHERE 1=1 "
                + "AND t1.row_number>=t2.total_rows/2 and t1.row_number<=t2.total_rows/2+1)sq " + "group by sq.date ";
        Object[] params = new Object[]{Double.valueOf(min), Double.valueOf(max), st, st, en, en};
        List<Map<String, Object>> list = jdbcTemplate.queryForList(maxSql, params);
        Map<String, LuData> map = new HashMap<String, LuData>();
        for (Map<String, Object> m : list) {
            String date = m.get("date").toString();
            LuData ld = new LuData();
            map.put(date, ld);
            ld.setMax((Double) m.get("rate"));
            ld.setDate(date);
        }
        list = jdbcTemplate.queryForList(minSql, params);
        for (Map<String, Object> m : list) {
            String date = m.get("date").toString();
            LuData ld = map.get(date);
            ld.setMin((Double) m.get("rate"));
        }
        list = jdbcTemplate.queryForList(avgSql, params);
        for (Map<String, Object> m : list) {
            String date = m.get("date").toString();
            LuData ld = map.get(date);
            ld.setAvg((Double) m.get("rate"));
        }

        list = jdbcTemplate.queryForList(medianSql, new Object[]{Double.valueOf(min), Double.valueOf(max), st, st, en, en,
                Double.valueOf(min), Double.valueOf(max), st, st, en, en});
        for (Map<String, Object> m : list) {
            String date = m.get("date").toString();
            LuData ld = map.get(date);
            ld.setMedian((Double) m.get("rate"));
        }
        ArrayList<LuData> result = new ArrayList<LuData>(map.values());
        result.sort(Comparator.comparing(LuData::getDate));
        return result;
    }
}
