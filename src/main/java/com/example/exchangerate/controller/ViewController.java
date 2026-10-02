package com.example.exchangerate.controller;

import com.example.exchangerate.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final ExchangeRateService exchangeRateService;

    /**
     * 메인 페이지
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("currencies", exchangeRateService.getMajorCurrencies());
        return "index";
    }
}
