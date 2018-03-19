package com.longjiabo.fund.service;

import com.longjiabo.fund.model.fund.History;
import com.longjiabo.fund.model.fund.Target;
import com.longjiabo.fund.util.DateTimeUtils;
import com.longjiabo.fund.util.HttpClientUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class V2Scanner implements Scanner {
    private final Logger log = LoggerFactory.getLogger(V1Scanner.class);
    private final String baseURL = "http://fund.eastmoney.com/";


    @Override
    public List<History> scanner(Target target, Integer count) {
        String url = baseURL + "f10/F10DataApi.aspx?type=lsjz&code=" + target.getCode() + "&per="
                + (count == null ? 10 : count.intValue());
        List<History> list = new ArrayList<History>();
        InputStream stream = HttpClientUtils.get(url);
        try {
            Document dom = Jsoup.parse(stream, "gbk", "");
            Element table = dom.select("tbody").get(0);
            Elements trs = table.select("tr");
            Iterator<Element> it = trs.iterator();
            while (it.hasNext()) {
                Element tr = it.next();
                String date = tr.child(0).text();
                String price1 = tr.child(1).text();
                String price2 = tr.child(2).text();
                price2 = price2.replace("%", "");
                History history = new History();
                history.setCreatedOn(new Date());
                history.setCode(target.getCode());
                if (date.endsWith("*")) {
                    date = date.replace("*", "");
                    history.setMultiDay(1);
                }
                history.setPriceDate(DateTimeUtils.parseDate(date, "yyyy-MM-dd"));
                history.setPrice1(Double.valueOf(price1));
                history.setPrice2(Double.valueOf(price2));
                list.add(history);
            }

        } catch (Exception e) {
            log.error("", e);
        }
        return list;
    }

}
