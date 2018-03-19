package com.longjiabo.fund.controller.fund;

import com.longjiabo.fund.model.fund.Target;
import com.longjiabo.fund.repository.TargetRepository;
import com.longjiabo.fund.service.DailyPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/targets")
public class TargetController {
    @Autowired
    TargetRepository targetRepository;

    @Autowired
    DailyPriceService dailyPriceService;

    @GetMapping
    public Object target() {
        Iterable<Target> targets = targetRepository.findAllByOrderByCodeAsc();
        return targets;
    }

    @GetMapping("/{id}")
    public Object edit(@PathVariable Integer id) {
        return targetRepository.findById(id);
    }

    @PostMapping
    public void save(@RequestBody Target target) {
        if (target.getId() == null) {
            dailyPriceService.scannerFund(
                    target, 1000);
        }
        target.setCreatedOn(new Date());
        target.setUpdatedOn(new Date());
        targetRepository.save(target);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        targetRepository.deleteById(id);
    }
}
