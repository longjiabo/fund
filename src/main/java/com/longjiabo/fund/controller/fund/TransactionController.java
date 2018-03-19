package com.longjiabo.fund.controller.fund;

import com.longjiabo.fund.model.fund.Transaction;
import com.longjiabo.fund.repository.TransactionRepository;
import com.longjiabo.fund.service.TransactionService;
import com.longjiabo.fund.util.BaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    TransactionService transactionService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping
    public Object target() {
        List<Map<String, Object>> transactions = jdbcTemplate.queryForList("select txn.*,t.name,f.name as groupName from transaction txn left join target t on txn.code=t.code left join fund_group f on txn.group_id=f.id order by txn.txn_date desc");
        return transactions;
    }

    @GetMapping("/{id}")
    public Optional<Transaction> edit(@PathVariable Integer id) {
        return transactionRepository.findById(id);
    }

    @PostMapping
    public void save(@RequestBody Transaction transaction) {
        if (transaction.getCode() == null)
            return;
        transaction.setCreatedOn(new Date());
        transaction.setUpdatedOn(new Date());
        if (transaction.getId() != null) {
            Transaction old = transactionRepository.findById(transaction.getId()).get();
            transaction.setCreatedOn(old.getCreatedOn());
            transaction.setUpdatedOn(new Date());
            transactionRepository.deleteById(transaction.getId());
            transaction.setId(null);
        }
        transactionService.newOperation(
                transaction);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        transactionRepository.deleteById(id);
    }
}
