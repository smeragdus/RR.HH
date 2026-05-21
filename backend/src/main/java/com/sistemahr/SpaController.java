package com.sistemahr;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class SpaController {
    @GetMapping(value = {"/", "/{path:^(?!api|assets|favicon\\.svg|icons\\.svg|logo-mendoza\\.svg).*$}"})
    String index() {
        return "forward:/index.html";
    }
}
