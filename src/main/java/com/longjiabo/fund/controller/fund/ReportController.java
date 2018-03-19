package com.longjiabo.fund.controller.fund;

import com.longjiabo.fund.Constant;
import com.longjiabo.fund.model.fund.History;
import com.longjiabo.fund.repository.HistoryRepository;
import com.longjiabo.fund.service.DailyMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ReportController {

    @Autowired
    HistoryRepository historyRepository;
    @Autowired
    DailyMailService dailyMailService;

    @GetMapping("/report")
    public Object report() {
        History history = historyRepository.findLatestHistoryWhichinTransactionByType(Constant.Target_Other);
        if (history == null) return null;
        return dailyMailService.getGroups(
                history.getPriceDate());
    }

    @GetMapping("/report/cash")
    public Object cashReport() {
        History history = historyRepository.findLatestHistoryWhichinTransactionByType(Constant.Target_CASH);
        if (history == null) return null;
        return dailyMailService.getCashGroups(
                history.getPriceDate());
    }
}
