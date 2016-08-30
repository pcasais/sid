package com.damosais.sid;

import java.util.Properties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

/**
 * This class initialises the web application
 * 
 * @author Pablo Casais Solano
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication
public class ServletInitializer extends SpringBootServletInitializer {
    static Properties getProperties() {
        final Properties props = new Properties();
        props.put("spring.config.location", "classpath:sidConfig/");
        return props;
    }
    
    /**
     * This is the main entry point of the application
     * 
     * @param args
     *            The arguments given to the application on start up
     */
    public static void main(String[] args) {
        final SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(ServletInitializer.class);
        springApplicationBuilder.sources(ServletInitializer.class).properties(getProperties()).run(args);
    }
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder springApplicationBuilder) {
        return springApplicationBuilder.sources(ServletInitializer.class).properties(getProperties());
    }
}