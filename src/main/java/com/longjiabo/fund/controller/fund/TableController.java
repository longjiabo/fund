package com.longjiabo.fund.controller.fund;

import com.longjiabo.fund.bean.FundSum;
import com.longjiabo.fund.model.fund.History;
import com.longjiabo.fund.model.fund.Target;
import com.longjiabo.fund.repository.HistoryRepository;
import com.longjiabo.fund.repository.TargetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/table")
public class TableController {
    @Autowired
    HistoryRepository historyRepository;
    @Autowired
    TargetRepository targetRepository;

    @GetMapping
    public Object table() {
        List<Target> targets = targetRepository.findAllByType("1");
        List<History> histories = historyRepository.findAllByOrderByPriceDateAsc();
        List<Long> times = times();
        List<FundSum> list = new ArrayList<FundSum>();
        for (Target t : targets) {
            for (int i = 0; i < times.size(); i += 2) {
                FundSum fs = new FundSum();
                fs.setName(t.getName());
                fs.setCode(t.getCode());
                fs.setStart(times.get(i + 1));
                fs.setEnd(times.get(i));
                caculateFundSum(fs, histories);
                list.add(fs);
            }
        }
        AtomicInteger counter = new AtomicInteger();
        return list.stream().collect(Collectors.groupingBy(x -> counter.getAndIncrement() / 6)).values();
    }

    private List<History> getHistoyByFundSum(FundSum f, List<History> histories) {
        List<History> list = new ArrayList<History>();
        int start = 0;
        for (int i = 0; i < histories.size(); i++) {
            History h = histories.get(i);
            if (h.getCode().equals(f.getCode())) {
                long time = h.getPriceDate().getTime();
                if (time < f.getStart()) {
                    start = i;
                }
                if (time >= f.getStart() && time <= f.getEnd()) {
                    list.add(h);
                }
            }
        }
        list.add(0, histories.get(start));
        return list;
    }

    private List<Long> times() {
        List<Long> times = new ArrayList<Long>();
        Calendar cal = Calendar.getInstance();
        // 当前时间
        times.add(cal.getTimeInMillis());
        // 本周一
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        System.out.println(cal.getTime());
        times.add(cal.getTimeInMillis());
        // 上周五
        cal.add(Calendar.DAY_OF_MONTH, -2);
        times.add(cal.getTimeInMillis());
        // 上周一
        cal.add(Calendar.DAY_OF_MONTH, -5);
        times.add(cal.getTimeInMillis());
        // 上上周5
        cal.add(Calendar.DAY_OF_MONTH, -2);
        times.add(cal.getTimeInMillis());
        // 上上周1
        cal.add(Calendar.DAY_OF_MONTH, -5);
        times.add(cal.getTimeInMillis());
        // 上2周5
        cal.add(Calendar.DAY_OF_MONTH, -2);
        times.add(cal.getTimeInMillis());
        // 上2周1
        cal.add(Calendar.DAY_OF_MONTH, -5);
        times.add(cal.getTimeInMillis());

        cal = Calendar.getInstance();
        // 本月
        times.add(cal.getTimeInMillis());
        // 本月初
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        times.add(cal.getTimeInMillis());
        // 上月末
        cal.add(Calendar.SECOND, -10);
        times.add(cal.getTimeInMillis());
        // 上月初
        cal.add(Calendar.MONTH, -1);
        cal.add(Calendar.SECOND, 10);
        times.add(cal.getTimeInMillis());
        return times;
    }

    private void caculateFundSum(FundSum f, List<History> histories) {
        List<History> list = getHistoyByFundSum(f, histories);
        if (list.size() < 1) {
            f.setAdded(0);
            f.setBalanceDay(0);
            f.setDownDay(0);
            f.setUpDay(0);
        } else {
            double t = 100 * (list.get(list.size() - 1).getPrice2() - list.get(0).getPrice2())
                    / list.get(0).getPrice2();
            f.setAdded(t);
            Double pre = list.get(0).getPrice2();
            for (int i = 1; i < list.size(); i++) {
                History h = list.get(i);
                if (h.getPrice2() > pre) {
                    f.addUp(1);
                } else if (h.getPrice2().doubleValue() == pre) {
                    f.addBalance(1);
                } else {
                    f.addDown(1);
                }
                pre = h.getPrice2();
            }
        }

    }

}
