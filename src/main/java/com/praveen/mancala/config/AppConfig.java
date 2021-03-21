package com.praveen.mancala.config;

import com.praveen.mancala.game.GameStore;
import com.praveen.mancala.model.CustomPitSerializer;
import com.praveen.mancala.model.Pit;
import com.praveen.mancala.web.GameFilter;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Collections;

@Configuration
public class AppConfig {

    @Bean
    public GameFilter gameFilter() {
        return new GameFilter();
    }

    @Bean
    public FilterRegistrationBean<GameFilter> tenantFilterRegistration() {
        FilterRegistrationBean<GameFilter> result = new FilterRegistrationBean<>();
        result.setFilter(this.gameFilter());
        result.setUrlPatterns(Collections.singletonList("/*"));
        result.setName("Game Store Filter");
        result.setOrder(1);
        return result;
    }

    @Bean
    public CustomPitSerializer customPitSerializer() {
        return new CustomPitSerializer();
    }

    @Bean(destroyMethod = "destroy")
    public ThreadLocalTargetSource threadLocalGameStore() {
        ThreadLocalTargetSource result = new ThreadLocalTargetSource();
        result.setTargetBeanName("gameStore");
        return result;
    }

    @Primary
    @Bean(name = "proxiedThreadLocalTargetSource")
    public ProxyFactoryBean proxiedThreadLocalTargetSource(ThreadLocalTargetSource threadLocalTargetSource) {
        ProxyFactoryBean result = new ProxyFactoryBean();
        result.setTargetSource(threadLocalTargetSource);
        return result;
    }

    @Bean(name = "gameStore")
    @Scope(scopeName = "prototype")
    public GameStore gameStore() {
        return new GameStore();
    }


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
            }
        };
    }
}
