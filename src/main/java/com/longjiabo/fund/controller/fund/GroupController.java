package com.longjiabo.fund.controller.fund;

import com.longjiabo.fund.model.fund.FundGroup;
import com.longjiabo.fund.repository.FundGroupRepository;
import com.longjiabo.fund.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
public class GroupController {
    @Autowired
    FundGroupRepository fundGroupRepository;
    @Autowired
    TransactionRepository transactionRepository;

    @GetMapping
    public Object index() {
        return fundGroupRepository.findAllByOrderByDisplayOrderAsc();
    }

    @GetMapping("/{id}")
    public Object edit(@PathVariable Integer id) {
        return fundGroupRepository.findById(id);
    }

    @PostMapping
    public void save(@RequestBody FundGroup group) {
        fundGroupRepository.save(group);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        transactionRepository.deleteByGroupId(id);
        fundGroupRepository.deleteById(id);
    }
}
