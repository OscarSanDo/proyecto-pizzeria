package com.pizzaiolo.authorization.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceWebConfig implements WebMvcConfigurer {
    final Environment environment;

    public ResourceWebConfig(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        // For windows user;
         registry.addResourceHandler("/api/v1/uploads/**").addResourceLocations("file:///C://Eclipse//test//spring-user-management/uploads");
//        String location = environment.getProperty("app.file.storage.mapping");

        //registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }
}
