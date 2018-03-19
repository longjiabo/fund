package com.longjiabo.fund.job;

import com.longjiabo.fund.bean.LuProductParam;
import com.longjiabo.fund.model.lu.LuProduct;
import com.longjiabo.fund.model.lu.LuRegularProduct;
import com.longjiabo.fund.repository.LuProductRepository;
import com.longjiabo.fund.repository.LuRegularProductRepository;
import com.longjiabo.fund.util.HttpClientUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

//@EnableScheduling
//@Component
public class DingQiJob {
    private String baseUrl = "https://list.lu.com/list/transfer-dingqi?minMoney=%s&maxMoney=%s&minDays=%s&maxDays=%s&minRate=&maxRate=&mode=&tradingMode=&isOverdueTransfer=&isCx=&currentPage=%s&orderCondition=&isShared=&canRealized=&productCategoryEnum=&notHasBuyFeeRate=&riskLevel=";

    private static final Logger LOG = LoggerFactory.getLogger(HuoQiJob.class);

    private int[] money = new int[]{300000, -1};
    private int[] days = new int[]{30, 90, 180, 360};
    private int page = 4;
    @Autowired
    LuRegularProductRepository luRegularProductRepository;

    @Scheduled(cron = "0 */10 * * * ?")
    public void scanner() {
        for (int i = 1; i <= page; i++) {
            for (int j = 0; j < money.length - 1; j++) {
                for (int k = 0; k < days.length - 1; k++) {
                    LuProductParam lp = new LuProductParam();
                    lp.setCurrentPage(i);
                    lp.setMinMoney(money[j]);
                    lp.setMaxMoney(money[j + 1]);
                    lp.setMinDays(days[k]);
                    lp.setMaxDays(days[k + 1]);
                    List<LuRegularProduct> list = getProducts(lp);
                    for (LuRegularProduct p : list) {
                        luRegularProductRepository.save(p);
                    }
                }

            }
        }
    }

    private String formatValue(Integer v) {
        if (v == -1) return "";
        return v.toString();
    }

    private List<LuRegularProduct> getProducts(LuProductParam param) {
        String url = String.format(baseUrl, formatValue(param.getMinMoney()), formatValue(param.getMaxMoney()), formatValue(param.getMinDays()), formatValue(param.getMaxDays()), formatValue(param.getCurrentPage()));
        LOG.info(url);
        List<LuRegularProduct> list = new ArrayList<LuRegularProduct>();
        InputStream stream = HttpClientUtils.get(url);
        try {
            if (stream == null) {
                stream = HttpClientUtils.get(url);
            }
            if (stream == null) {
                stream = HttpClientUtils.get(url);
            }
            if (stream == null)
                return list;
            Document dom = Jsoup.parse(stream, "utf-8", "");
            Elements lis = dom.select(".main-list>li");
            Iterator<Element> it = lis.iterator();
            Date now = new Date();
            while (it.hasNext()) {
                Element li = it.next();
                LuRegularProduct p = new LuRegularProduct();
                String amount = li.select(".product-amount .num-style").text();
                amount = amount.replace(",", "");
                if (StringUtils.isEmpty(amount)) {
                    p.setAmount(0.0);
                } else {
                    p.setAmount(Double.valueOf(amount));
                }
                p.setCreatedOn(now);
                String rate = li.select(".interest-rate .num-style").text();
                rate = rate.replace("%", "");
                if (StringUtils.isEmpty(rate)) {
                    p.setRate(0.0);
                } else {
                    p.setRate(Double.valueOf(rate));
                }

                String days = li.select(".invest-period p").text();
                days = days.split("天")[0];
                if (StringUtils.isEmpty(days)) {
                    p.setLeftDays(0);
                } else {
                    p.setLeftDays(Integer.valueOf(days));
                }

                Element a = li.selectFirst(".product-name a");
                p.setName(a.text());
                p.setUrl(a.attr("href"));
                list.add(p);
            }
        } catch (Exception e) {
            LOG.error("", e);
        }
        return list;
    }


}
