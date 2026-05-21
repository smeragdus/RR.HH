package com.sistemahr;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/{path:^(?!api|assets|error|favicon\\.svg|icons\\.svg|logo-mendoza\\.svg).*$}")
                .setViewName("forward:/index.html");
        registry.addViewController("/{path:^(?!api|assets|error|favicon\\.svg|icons\\.svg|logo-mendoza\\.svg).*$}/**")
                .setViewName("forward:/index.html");
    }
}
