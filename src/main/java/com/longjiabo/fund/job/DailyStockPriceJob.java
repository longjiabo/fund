package com.longjiabo.fund.job;

import com.longjiabo.fund.service.DailyPriceService;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
@EnableScheduling
public class DailyStockPriceJob {
    private Logger log = LoggerFactory.getLogger(DailyStockPriceJob.class);

    @Autowired
    DailyPriceService dailyPriceService;


    //@Scheduled(cron = "0 */10 * * * ?")
    @Scheduled(cron = "0 */15 * * * ?")
    public void execute()
            throws JobExecutionException {
        log.info("start task...");
        dailyPriceService.dailyPrice();
        //sendMail();
    }

    private void sendMail() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 22);
        cal.set(Calendar.MINUTE, 30);
        Date t1 = cal.getTime();
        cal.add(Calendar.HOUR_OF_DAY, -1);
        Date t2 = cal.getTime();
        Date now = new Date();
        if (now.before(t1) && now.after(t2)) {
            log.info("send mail...");
            //BeanFactory.getBean(DailyMailService.class).sendMail(now);
        }
    }

}
