package com.longjiabo.fund.service;

import com.longjiabo.fund.bean.Statistic;
import com.longjiabo.fund.model.fund.History;
import com.longjiabo.fund.model.fund.Target;
import com.longjiabo.fund.repository.TargetRepository;
import com.longjiabo.fund.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatisticService {
    private static final int TBDINT = 999;
    private static final Double TBD = -999.99;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    TargetRepository targetRepository;

    public void run(Date start, Date end) {
        List<History> historys = jdbcTemplate.queryForList(
                "select * from history where priceDate>=? and priceDate<? order by priceDate",
                new Object[]{start, end}, History.class);
        Iterable<Target> targets = targetRepository.findAll();
        List<Long> dates = caculateDates(historys);
        List<Statistic> list = new ArrayList<Statistic>();
        for (Target t : targets) {
            Statistic st = new Statistic();
            List<History> hiss = getHistorys(historys, t, dates);
            st.setName(t.getName());
            st.setCode(t.getCode());
            st.setHistorys(hiss);
            st.setDates(dates);
            st.setAmount(new double[dates.size()]);
            st.setRank(new int[dates.size()]);
            list.add(st);
        }
        setAmount(list);
        setRank(list, dates);
        setAvg(list);
        print(list);
    }

    private void print(List<Statistic> list) {
        StringBuffer sb = new StringBuffer();
        sb.append("名称\t总天数\t上涨天数\t持平天数\t下跌天数\t最大涨幅\t最大跌幅\t最好排名\t最坏排名\t平均排名\t标准差\t总涨幅\t");
        for (int i = 0; i < list.get(0).getDates().size(); i++) {
            Date d = new Date(list.get(0).getDates().get(i));
            String str = DateTimeUtils.formatDate(d, "yyyy-MM-dd");
            sb.append(str).append("\t").append(str).append("\t");
        }
        sb.append("\n");
        for (Statistic st : list) {
            sb.append(st.getName() + "\t" + st.getDays() + "\t"
                    + st.getUpDays() + "\t" + st.getHoldDays() + "\t"
                    + st.getDownDays() + "\t"
                    + doubleFormat(st.getMaxUpAmount()) + "\t"
                    + doubleFormat(st.getMaxDownAmount()) + "\t" + st.getBest()
                    + "\t" + st.getWorst() + "\t"
                    + doubleFormat(st.getAverage()) + "\t"
                    + doubleFormat(st.getStandand()) + "\t"
                    + st.getTotalAmount() + "\t");
            for (int i = 0; i < st.getAmount().length; i++) {
                sb.append(doubleFormat(st.getAmount()[i])).append("\t")
                        .append(st.getRank()[i]).append("\t");
            }
            sb.append("\n");
        }
        System.out.println(sb);

    }

    private String doubleFormat(double d) {
        return String.format("%.6f", d);
    }

    private void setAmount(List<Statistic> list) {
        for (Statistic st : list) {
            caculateSimpleTargetValues(st);
        }
    }

    private void setAvg(List<Statistic> list) {
        for (Statistic st : list) {
            int[] rank = st.getRank();
            int best = 999;
            int worst = 0;
            int sum = 0;
            int size = 0;

            for (int r : rank) {
                if (r == TBDINT)
                    continue;
                best = best < r ? best : r;
                worst = worst > r ? worst : r;
                sum += r;
                size++;
            }
            st.setBest(best);
            st.setWorst(worst);
            st.setAverage(sum / size);
            double avg = st.getAverage();
            double s = 0;
            for (int r : rank) {
                if (r == TBDINT)
                    continue;
                s += (r - avg) * (r - avg);
            }
            s = s / size;
            st.setStandand(Math.sqrt(s));
        }
    }

    private void setRank(List<Statistic> list, List<Long> dates) {
        for (Statistic st : list) {
            st.getRank()[0] = TBDINT;
        }
        for (int i = 1; i < dates.size(); i++) {
            final int index = i;
            list.sort((o1, o2) -> {
                double t1 = o1.getAmount()[index];
                double t2 = o2.getAmount()[index];
                if (t1 == TBD) {
                    o1.getRank()[index] = TBDINT;
                    return 1;
                }
                if (t2 == TBD) {
                    o2.getRank()[index] = TBDINT;
                    return -1;
                }
                return t2 > t1 ? 1 : -1;
            });
            double lastAmount = TBD * 2;
            int lastPos = -1;
            for (int j = 0; j < list.size(); j++) {
                int[] rank = list.get(j).getRank();
                double currentAmount = list.get(j).getAmount()[index];
                if (rank[index] != TBDINT) {
                    if (Math.abs(currentAmount - lastAmount) < 0.000001) {
                        list.get(j).getRank()[index] = lastPos;
                    } else {
                        list.get(j).getRank()[index] = j + 1;
                    }
                    lastAmount = currentAmount;
                    lastPos = list.get(j).getRank()[index];
                }
            }
        }
    }

    private List<Long> caculateDates(List<History> historys) {
        Set<Long> dates = new HashSet<Long>();
        for (History h : historys) {
            Date date = h.getPriceDate();
            dates.add(dayStart(date).getTime());
        }
        List<Long> times = new ArrayList<Long>(dates);
        times.sort((o1, o2) -> o1.longValue() > o2.longValue() ? 1 : -1);
        return times;
    }

    private Date dayStart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private History getFirstHistory(List<History> hiss) {
        for (History h : hiss) {
            if (h.getPrice2() == TBD)
                continue;
            return h;
        }
        return null;
    }

    private History getLastHistory(List<History> hiss) {
        for (int i = 0; i < hiss.size(); i++) {
            if (hiss.get(hiss.size() - i - 1).getPrice2() == TBD)
                continue;
            return hiss.get(hiss.size() - i - 1);
        }
        return null;
    }

    private void caculateSimpleTargetValues(Statistic st) {
        int upDays = 0;
        int downDays = 0;
        int holdDays = 0;
        int days = 1;
        History start = getFirstHistory(st.getHistorys());
        History first = getFirstHistory(st.getHistorys());
        History last = getLastHistory(st.getHistorys());
        double maxUp = 0;
        double maxDown = 0;
        for (int i = 0; i < st.getHistorys().size(); i++) {
            History h = st.getHistorys().get(i);
            if (h.getPrice2().intValue() == TBD) {
                st.getAmount()[i] = TBD;
            } else {
                days++;
                double price = (h.getPrice2() - start.getPrice2())
                        / start.getPrice2();
                st.getAmount()[i] = price;
                if (price > 0) {
                    upDays++;
                    maxUp = price > maxUp ? price : maxUp;
                } else if (price == 0) {
                    holdDays++;
                } else {
                    downDays++;
                    price = -price;
                    maxDown = price > maxDown ? price : maxDown;
                }
            }
            start = h;
        }
        st.setTotalAmount((last.getPrice2() - first.getPrice2())
                / first.getPrice2());
        st.setDays(days);
        st.setUpDays(upDays);
        st.setDownDays(downDays);
        st.setHoldDays(holdDays);
        st.setMaxDownAmount(maxDown);
        st.setMaxUpAmount(maxUp);
    }

    private List<History> getHistorys(List<History> historys, Target t,
                                      List<Long> dates) {
        List<History> list = new ArrayList<History>();
        List<History> hiss = getHistorys(historys, t);
        for (long d : dates) {
            History his = null;
            for (History h : hiss) {
                long time = dayStart(h.getPriceDate()).getTime();
                if (time == d)
                    his = h;
            }
            if (his == null) {
                his = new History();
                his.setPriceDate(new Date(d));
                his.setPrice2(TBD);
            }
            list.add(his);
        }
        list.sort(Comparator.comparing(History::getPriceDate));
        return list;
    }

    private List<History> getHistorys(List<History> historys, Target t) {
        List<History> list = new ArrayList<History>();
        for (History h : historys) {
            if (h.getCode().equals(t.getCode()))
                list.add(h);
        }
        return list;
    }
}
