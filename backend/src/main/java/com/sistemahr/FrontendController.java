package com.sistemahr;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class FrontendController {
    @GetMapping({"/", "/index.html"})
    @ResponseBody
    Resource index() {
        return new ClassPathResource("static/index.html");
    }
}
