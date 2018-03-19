package com.longjiabo.fund.controller.fund;

import com.fasterxml.jackson.databind.util.JSONWrappedObject;
import com.longjiabo.fund.bean.FundSum;
import com.longjiabo.fund.model.fund.History;
import com.longjiabo.fund.model.fund.Target;
import com.longjiabo.fund.repository.HistoryRepository;
import com.longjiabo.fund.repository.TargetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chart")
public class ChartController {
    @Autowired
    HistoryRepository historyRepository;
    @Autowired
    TargetRepository targetRepository;

    @GetMapping("/2")
    public String v2() {
        return "chart2";
    }








    @GetMapping("/data")
    public Object data(Integer step, Integer start) {
        Set<Long> errors = new HashSet<Long>();
        List<String> errorMsgs = new ArrayList<String>();
        if (step == null)
            step = 7;
        if (start == null)
            start = 30;
        List<History> historys = getHistory(step, start);
        Map<String, String> targetMap = getTargetMap();
        Long[] dates = getDates(historys, start);
        // series
        Map<String, Object> obj = new HashMap<String, Object>();
        List<Object> ary = getSeries(historys, targetMap, step, start, dates, errors, errorMsgs);
        obj.put("series", ary);

        // 检查series的data，是不是有spec标志的，如果有，该日期直接删除。
        List<Long> datesFix = new ArrayList<Long>();
        for (Long l : dates) {
            if (!errors.contains(l))
                datesFix.add(l);
        }

        // xAxis
        Map<String, Object> xAxis = new HashMap<>();

        xAxis.put("data", getX(datesFix));
        obj.put("xAxis", xAxis);

        // legend
        Map<String, Object> legend = new HashMap<>();
        legend.put("data", targetMap.values());
        obj.put("legend", legend);

        return obj;
    }

    private List<String> getX(List<Long> dates) {
        List<String> ds = new ArrayList<String>();
        for (Long l : dates) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(l);
            ds.add(cal.get(Calendar.MONTH) + 1 + "-" + cal.get(Calendar.DAY_OF_MONTH));
        }
        return ds;
    }

    private List<Object> getSeries(List<History> historys, Map<String, String> targetMap, Integer step, Integer start,
                                   Long[] dates, Set<Long> errors, List<String> errorMsgs) {
        List<Object> ary = new ArrayList<Object>();
        Map<String, List<History>> map = parseHistorys(historys, targetMap);
        for (Map.Entry<String, List<History>> entry : map.entrySet()) {
            Map<String, Object> o = new HashMap<>();
            o.put("name", entry.getKey());
            o.put("type", "line");
            o.put("data", getPrices(entry.getValue(), step, start, dates, errors, errorMsgs));
            ary.add(o);
        }
        return ary;
    }

    private Long[] getDates(List<History> historys, Integer start) {
        Set<Long> set = new TreeSet<Long>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -start);
        for (History h : historys) {
            long time = h.getPriceDate().getTime();
            if (time >= cal.getTimeInMillis())
                set.add(h.getPriceDate().getTime());
        }

        return set.toArray(new Long[set.size()]);
    }

    private Map<String, List<History>> parseHistorys(List<History> historys, Map<String, String> targetMap) {
        Map<String, List<History>> map = new HashMap<String, List<History>>();
        for (History h : historys) {
            String name = targetMap.get(h.getCode());
            List<History> list = map.get(name);
            if (list == null) {
                list = new ArrayList<History>();
                map.put(name, list);
            }
            list.add(h);
        }
        return map;
    }

    private List<History> getHistory(Integer step, Integer start) {
        return historyRepository.findAfterPriceDate(getDataStart(step, start));
    }

    private Map<String, String> getTargetMap() {
        List<Target> targets = targetRepository.findAllByType("1");
        Map<String, String> targetMap = new HashMap<String, String>();
        for (Target t : targets) {
            targetMap.put(t.getCode(), t.getName() + "(" + t.getCode() + ")");
        }
        return targetMap;
    }

    private Date getDataStart(Integer step, Integer start) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -start - step - 200);
        return calendar.getTime();
    }

    private List<Double> getPrices(List<History> list, Integer step, Integer start, Long[] dates, Set<Long> errors,
                                   List<String> errorMsgs) {
        ArrayList<Double> ds = new ArrayList<Double>();
        for (int i = 0; i < dates.length; i++) {
            try {
                History now = getHistoryByDate(list, dates[i]);
                History before = getHistoryByDate(list, dates[i] - step * 24 * 60 * 60 * 1000);
                double t = 100 * (now.getPrice2() - before.getPrice2()) / before.getPrice2();

                ds.add(t);
            } catch (Exception e) {
                System.out.println(i);
                // e.printStackTrace();
                errors.add(dates[i]);
                errorMsgs.add(list.get(0).getCode() + " miss " + dates[i]);
            }
        }
        return ds;
    }

    private History getHistoryByDate(List<History> list, Long long1) {
        for (History h : list) {
            if (h.getPriceDate().getTime() == long1.longValue())
                return h;
        }
        return null;
    }
}
