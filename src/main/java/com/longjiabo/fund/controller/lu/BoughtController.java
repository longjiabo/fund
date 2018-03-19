package com.longjiabo.fund.controller.lu;

import com.longjiabo.fund.model.lu.LuBought;
import com.longjiabo.fund.repository.LuBoughtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lu/bought")
public class BoughtController {
    @Autowired
    LuBoughtRepository luBoughtRepository;

    @GetMapping("/list")
    public Object getProjects() {
        Iterable<LuBought> ps = luBoughtRepository.findAllByOrderByCreatedOnDesc();
        return ps;
    }

    @GetMapping("/code")
    public Object code() {
        return "    if 0 <= txn.user.amount - product.amount < 600 and product.rate >= 10.1:\n" +
                "        return True\n" +
                "    return False";
    }
}
