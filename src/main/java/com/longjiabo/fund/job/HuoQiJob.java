package com.longjiabo.fund.job;

import com.longjiabo.fund.bean.LuProductParam;
import com.longjiabo.fund.model.lu.LuProduct;
import com.longjiabo.fund.repository.LuProductRepository;
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
//
//@EnableScheduling
//@Component
public class HuoQiJob {
    private String baseUrl = "https://list.lu.com/list/r030?minDays=&maxDays=&minRate=&maxRate=&mode=&subType=&instId=&haitongGrade=&fundGroupId=&searchWord=&trade=&isCx=&orderType=R030_INVEST_RATE&orderAsc=false&notHasBuyFeeRate=&rootProductCategoryEnum=";

    private static final Logger LOG = LoggerFactory.getLogger(HuoQiJob.class);

    private int[] money = new int[]{0, 10000, 50000, 100000, 100000000};

    @Autowired
    LuProductRepository luProductRepository;

    @Scheduled(cron = "0 */10 * * * ?")
    public void scanner() {
        for (int i = 1; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                LuProductParam lp = new LuProductParam();
                lp.setCurrentPage(i);
                lp.setMinMoney(money[j]);
                lp.setMaxMoney(money[j + 1]);
                List<LuProduct> list = getProducts(lp);
                for (LuProduct p : list) {
                    luProductRepository.save(p);
                }
            }
        }
    }

    private List<LuProduct> getProducts(LuProductParam param) {
        LOG.info("start " + param.getCurrentPage() + " " + param.getMinMoney() + " " + param.getMaxMoney());
        String url = baseUrl + "&currentPage=" + param.getCurrentPage() + "&minMoney=" + param.getMinMoney()
                + "&maxMoney=" + param.getMaxMoney();
        List<LuProduct> list = new ArrayList<LuProduct>();
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
                LuProduct p = new LuProduct();
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
                list.add(p);
            }
        } catch (Exception e) {
            LOG.error("", e);
        }
        return list;
    }

}
