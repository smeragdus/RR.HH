package com.sistemahr;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class SpaController {
    @GetMapping("/{path:^(?!api$|assets$|error$).+[^.]*$}")
    String index() {
        return "forward:/index.html";
    }
}
