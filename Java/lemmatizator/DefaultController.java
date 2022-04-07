package ru.nedovizin;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {
    @RequestMapping("${spring.path}")
    public String index() {
        return "index";
    }

    @GetMapping("/api/startIndexing")
    public String startIndexing() {
        return "";
    }

    @GetMapping("/api/stopIndexing")
    public String stopIndexing() {
        return "";
    }

    @PostMapping("/api/indexPage")
    public String indexPage() {
        return "";
    }

    @GetMapping("/api/statistics")
    public String statistics() {
        return "";
    }
}

