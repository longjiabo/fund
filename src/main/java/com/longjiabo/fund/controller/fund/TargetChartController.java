package com.longjiabo.fund.controller.fund;

import com.longjiabo.fund.Constant;
import com.longjiabo.fund.bean.chart.Chart;
import com.longjiabo.fund.bean.chart.Legend;
import com.longjiabo.fund.bean.chart.Series;
import com.longjiabo.fund.bean.chart.XAxis;
import com.longjiabo.fund.model.fund.History;
import com.longjiabo.fund.model.fund.Target;
import com.longjiabo.fund.repository.HistoryRepository;
import com.longjiabo.fund.repository.TargetRepository;
import com.longjiabo.fund.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基金报表
 */
@RestController
@RequestMapping("/cash")
public class TargetChartController {


    @Autowired
    HistoryRepository historyRepository;
    @Autowired
    TargetRepository targetRepository;


    /**
     * 计算基金从开始日期到当前时间一共收益
     *
     * @param start
     * @param end
     * @param type  货币基金/其他
     * @return
     */
    @GetMapping("/chart/summary")
    public Object chart(String start, String end, String type) {
        List<Target> targets = targetRepository.findAllByTypeByOrderByCodeAsc(type);
        List<Long> dates = getDates(DateTimeUtils.parseDate(start, "yyyy-MM-dd HH:mm:ss"), DateTimeUtils.parseDate(end, "yyyy-MM-dd HH:mm:ss"));
        if (Constant.Target_Other.equals(type)) {
            dates = dates.stream().filter(date -> {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(date);
                int day = cal.get(Calendar.DAY_OF_WEEK);
                if (day == Calendar.SUNDAY || day == Calendar.SATURDAY) return false;
                Date d = new Date(date);
                Integer count = historyRepository.isExistPriceDateForOtherTarget(d);
                return count > 0;
            }).collect(Collectors.toList());
        }

        Date startDay = new Date(dates.get(0));
        Date endDay = new Date();
        Chart chart = new Chart();
        List<String> xAxis = dates.stream().map(l -> DateTimeUtils.formatDate(new Date(l), "MM-dd")).collect(Collectors.toList());
        chart.setLegend(new Legend(targets.stream().map(t -> t.getName() + "(" + t.getCode() + ")").collect(Collectors.toList())));
        chart.setxAxis(new XAxis(xAxis, "category"));
        for (Target t : targets) {
            List<History> histories = historyRepository.findBetweenPriceDateAndCodeOrderbyPriceDateAsc(startDay, endDay, t.getCode());
            Series series = new Series();
            series.setName(t.getName() + "(" + t.getCode() + ")");
            series.setType("line");
            if (Constant.Target_CASH.equals(type)) {
                series.setData(parseSumCashData(histories, dates));
            } else {
                series.setData(parseSumOtherData(histories, dates));
            }

            chart.getSeries().add(series);
        }
        return chart;
    }

    private List<Double> parseSumOtherData(List<History> histories, List<Long> dates) {
        List<Double> dd = new ArrayList<>();
        Double now = 0.0;
        if (histories != null && !histories.isEmpty()) {
            History h = histories.get(histories.size() - 1);
            now = h.getPrice2() == null ? 0.0 : h.getPrice2();
        }

        for (Long l : dates) {
            Double current = getClosestPrice2ByPriceDate(histories, l);
            dd.add(now - current);

        }
        return dd;
    }


    /**
     * 每一个date 计算从date一直到now的和
     *
     * @param historys
     * @param dates
     * @return
     */
    private List<Double> parseSumCashData(List<History> historys, List<Long> dates) {
        List<Double> dd = new ArrayList<>();
        for (Long start : dates) {
            Double r = historys.stream().filter(history -> {
                Long d = history.getPriceDate().getTime();
                return d >= start;
            }).mapToDouble(History::getPrice1).sum();
            dd.add(r);
        }
        return dd;
    }

    /**
     * 计算货币基金n日平均收益
     *
     * @param days
     * @return
     */
    @GetMapping("/chart")
    public Object chart(int days, String start, String end, String type) {
        List<Target> targets = targetRepository.findAllByTypeByOrderByCodeAsc(type);
        List<Long> dates = getDates(DateTimeUtils.parseDate(start, "yyyy-MM-dd HH:mm:ss"), DateTimeUtils.parseDate(end, "yyyy-MM-dd HH:mm:ss"));
        if (Constant.Target_Other.equals(type)) {
            dates = dates.stream().filter(date -> {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(date);
                int day = cal.get(Calendar.DAY_OF_WEEK);
                if (day == Calendar.SUNDAY || day == Calendar.SATURDAY) return false;
                Integer count = historyRepository.isExistPriceDateForOtherTarget(new Date(date));
                return count > 0;
            }).collect(Collectors.toList());
        }
        long s = dates.get(0);
        s = s - Constant.oneDay * days;
        Date startDay = new Date(s);
        Date endDay = new Date(dates.get(dates.size() - 1) + Constant.oneDay);
        Chart chart = new Chart();
        List<String> xAxis = dates.stream().map(l -> DateTimeUtils.formatDate(new Date(l), "MM-dd")).collect(Collectors.toList());
        chart.setLegend(new Legend(targets.stream().map(t -> t.getName() + "(" + t.getCode() + ")").collect(Collectors.toList())));
        chart.setxAxis(new XAxis(xAxis, "category"));
        for (Target t : targets) {
            List<History> histories = historyRepository.findBetweenPriceDateAndCodeOrderbyPriceDateAsc(startDay, endDay, t.getCode());
            Series series = new Series();
            series.setName(t.getName() + "(" + t.getCode() + ")");
            series.setType("line");
            if (Constant.Target_CASH.equals(type)) {
                series.setData(parseAvgCashData(histories, dates, days));
            } else {
                series.setData(parseAvgOtherData(histories, dates, days));
            }

            chart.getSeries().add(series);
        }
        return chart;
    }

    /**
     * 获取小于给定时间的最近的一个price2（主要因为给定的时间不一定存在price2，所以取之前最近的）
     *
     * @param histories 按照pricedate 从小到大排好序的list
     * @param time      时间
     * @return
     */
    private double getClosestPrice2ByPriceDate(List<History> histories, long time) {
        return histories.stream().filter(history -> {
            Long d = history.getPriceDate().getTime();
            return d <= time;
        }).sorted((history1, history2) -> history2.getPriceDate().compareTo(history1.getPriceDate())).findFirst().filter(history -> history.getPrice2() != null).map(History::getPrice2).orElse(0.0);
    }

    private List<Double> parseAvgOtherData(List<History> historys, List<Long> dates, Integer days) {
        List<Double> dd = new ArrayList<>();
        for (Long start : dates) {
            double current = getClosestPrice2ByPriceDate(historys, start);
            double previous = getClosestPrice2ByPriceDate(historys, start - Constant.oneDay * days);
            dd.add(current - previous);
        }
        return dd;

    }

    private List<Double> parseAvgCashData(List<History> historys, List<Long> dates, Integer days) {
        List<Double> dd = new ArrayList<>();
        for (Long l : dates) {
            Double r = historys.stream().filter(history -> {
                Long start = l - Constant.oneDay * days;
                Long d = history.getPriceDate().getTime();
                return d > start && d <= l;
            }).mapToDouble(History::getPrice1).sum();
            dd.add(r);
        }
        return dd;
    }

    private Long getDayStart(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private List<Long> getDates(Date start, Date end) {
        long st = getDayStart(start);
        long en = getDayStart(end);
        List<Long> dates = new ArrayList<>();
        while (st <= en) {
            dates.add(st);
            st += Constant.oneDay;
        }
        return dates;
    }
}
