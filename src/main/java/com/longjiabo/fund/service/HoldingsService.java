package com.longjiabo.fund.service;

import com.longjiabo.fund.model.fund.Transaction;
import com.longjiabo.fund.util.BaseUtils;
import com.longjiabo.fund.util.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HoldingsService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

//    public void sendMail() {
//        Map<String, Object> model = new HashMap<String, Object>();
//        model.put("funds", perpareFunds());
//        String str = BaseUtils.getMailContent(model, "mail.ftl");
//        MailUtil.sendMail(str);
//    }

    public List<Map<String, Object>> perpareFunds() {
        List<Map<String, Object>> funds = new ArrayList<Map<String, Object>>();
        List<Transaction> list = jdbcTemplate.queryForList(
                "select * from transaction", null, Transaction.class);
        Map<String, List<Transaction>> trans = new HashMap<String, List<Transaction>>();
        for (Transaction t : list) {
            List<Transaction> ts = trans.get(t.getCode());
            if (ts == null) {
                ts = new ArrayList<Transaction>();
                trans.put(t.getCode(), ts);
            }
            ts.add(t);
        }
        for (Map.Entry<String, List<Transaction>> entry : trans.entrySet()) {
            double amount = 0;
            double volume = 0;
            for (Transaction t : entry.getValue()) {
                if (t.getVolume() != null)
                    volume += t.getVolume();
                if (t.getAmount() != null)
                    amount += t.getAmount();
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("amount", amount);
            map.put("volume", volume);
            map.put("code", entry.getKey());
            funds.add(map);
        }
        return funds;
    }

}
