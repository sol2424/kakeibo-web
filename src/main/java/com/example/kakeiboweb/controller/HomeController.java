package com.example.kakeiboweb.controller;

import java.time.LocalDate;
import java.time.Year;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.kakeiboweb.entity.Transaction;
import com.example.kakeiboweb.service.TransactionService;

@Controller
public class HomeController {

    private final TransactionService service;

    public HomeController(TransactionService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home(
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month,
        @RequestParam(required = false) Boolean showIncome,
        @RequestParam(required = false) Boolean showExpense,
        @RequestParam(required = false) String activeTab,
        @RequestParam(required = false) String sort,
        @RequestParam(required = false) String direction,
        @RequestParam(required = false) String selectedType,
        Model model
    ) {

    	// デフォルト
    	LocalDate now = LocalDate.now();

    	if (year == null) year = now.getYear();
    	if (month == null) month = now.getMonthValue();

    	// チェックボックス初期値
    	if (showIncome == null && showExpense == null) {
    	    showIncome = true;
    	    showExpense = true;
    	} else {
    	    if (showIncome == null) showIncome = false;
    	    if (showExpense == null) showExpense = false;
    	}

    	if (activeTab == null) activeTab = "input";
    	if (selectedType == null) selectedType = "income";
    	
    	if (sort == null) sort = "date";
    	if (direction == null) direction = "desc";

        // 全件（入力タブ用）
        List<Transaction> allList = service.findAll();
        
        //ソート処理
        switch (sort) {

        case "amount":
            allList.sort(
                Comparator.comparing(Transaction::getAmount)
            );
            break;

        case "category":
            allList.sort(
                Comparator.comparing(Transaction::getCategory)
            );
            break;

        case "type":
            allList.sort(
                Comparator.comparing(Transaction::getType)
            );
            break;

        default:
            allList.sort(
                Comparator.comparing(Transaction::getDate)
            );
    }
        if ("desc".equals(direction)) {
            Collections.reverse(allList);
        }

     // フィルタ済み
        List<Transaction> filteredList = service.filter(year, month, showIncome, showExpense);
        
     // 月別グラフ専用（年だけで取得）
        List<Transaction> yearlyList =
                service.filter(year, null, showIncome, showExpense);

        int income = 0;
        int expense = 0;

        for (Transaction t : filteredList) {
            if ("income".equals(t.getType())) {
                income += t.getAmount();
            } else if ("expense".equals(t.getType())) {
                expense += t.getAmount();
            }
        }      
        
        Map<Integer, Integer> incomeData = new HashMap<>();
        Map<Integer, Integer> expenseData = new HashMap<>();

        for (int i = 1; i <= 12; i++) {
            incomeData.put(i, 0);
            expenseData.put(i, 0);
        }

        for (Transaction t : yearlyList) {

            int m = t.getDate().getMonthValue();

            if ("income".equals(t.getType())) {
                incomeData.put(
                    m,
                    incomeData.get(m) + t.getAmount()
                );

            } else if ("expense".equals(t.getType())) {
                expenseData.put(
                    m,
                    expenseData.get(m) + t.getAmount()
                );
            }
        }
        
        Map<String, Integer> categoryData = new HashMap<>();
        for (Transaction t : filteredList) {
            String category = t.getCategory();
            categoryData.put(category,
                categoryData.getOrDefault(category, 0) + t.getAmount());
        }
        
        int currentYear = Year.now().getValue();

        List<Integer> years = IntStream
                .rangeClosed(currentYear - 5, currentYear + 1)
                .boxed()
                .toList();

        model.addAttribute("years", years);
        
        model.addAttribute("income", income);
        model.addAttribute("expense", expense);


        model.addAttribute("incomeData", incomeData);
        model.addAttribute("expenseData", expenseData);
        model.addAttribute("categoryData", categoryData);
        model.addAttribute("transactions", allList);
        model.addAttribute("filteredTransactions", filteredList);

        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("showIncome", showIncome);
        model.addAttribute("showExpense", showExpense);
        
        model.addAttribute("activeTab", activeTab);
        model.addAttribute("selectedType", selectedType);

        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        
        Transaction transaction = new Transaction();
        transaction.setType(selectedType);

        model.addAttribute("transaction", transaction);
        
        return "index";
    }

    @PostMapping("/add")
    public String add(
            @Valid @ModelAttribute("transaction") Transaction transaction,
            BindingResult result,
            Model model
    ) {

        // 入力エラー
        if (result.hasErrors()) {

            // home画面表示用データを再セット
            List<Transaction> allList = service.findAll();

            model.addAttribute("transactions", allList);
            model.addAttribute("filteredTransactions", allList);

            model.addAttribute("income", 0);
            model.addAttribute("expense", 0);

            model.addAttribute("incomeData", new HashMap<>());
            model.addAttribute("expenseData", new HashMap<>());
            model.addAttribute("categoryData", new HashMap<>());

            int currentYear = Year.now().getValue();

            List<Integer> years = IntStream
                    .rangeClosed(currentYear - 5, currentYear + 1)
                    .boxed()
                    .toList();

            model.addAttribute("years", years);

            model.addAttribute("year", LocalDate.now().getYear());
            model.addAttribute("month", LocalDate.now().getMonthValue());

            model.addAttribute("showIncome", true);
            model.addAttribute("showExpense", true);

            model.addAttribute("sort", "date");
            model.addAttribute("direction", "desc");

            // 入力タブ維持
            model.addAttribute("activeTab", "input");

            // ラジオボタン維持
            model.addAttribute(
                    "selectedType",
                    transaction.getType()
            );

            return "index";
        }

        service.save(transaction);

        return "redirect:/?selectedType="
                + transaction.getType();
    }
    
    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {

        service.delete(id);

        return "redirect:/";
    }
    
    @GetMapping("/edit")
    public String edit(@RequestParam Long id, Model model) {

        Transaction t = service.findById(id);

        model.addAttribute("transaction", t);

        return "edit";
    }
    
    @PostMapping("/update")
    public String update(
            @Valid @ModelAttribute("transaction")
            Transaction transaction,
            BindingResult result
    ) {

        // 入力エラー時
        if (result.hasErrors()) {
            return "edit";
        }

        service.save(transaction);

        return "redirect:/";
    }
}