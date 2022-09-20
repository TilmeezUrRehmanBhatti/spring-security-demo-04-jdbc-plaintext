package com.tilmeez.springsecurity.demo.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.logging.Logger;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.tilmeez.springsecurity.demo")
@PropertySource("classpath:persistence-postgresql.properties")
public class DemoAppConfig {

    // set up variable to hold the properties
    @Autowired
    private Environment env;

    // set up a logger for diagnotics
    private Logger logger = Logger.getLogger(getClass().getName());

    // define a bean for viewResolver

    @Bean
    public ViewResolver viewResolver() {

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();

        viewResolver.setPrefix("/WEB-INF/view/");
        viewResolver.setSuffix(".jsp");

        return viewResolver;
    }

    // define a bean for our security datasource

    @Bean
    public DataSource securityDataSource() {

        // create a connection pool
        ComboPooledDataSource securityDataSource
                = new ComboPooledDataSource();

        // get the jdbc driver class
        try {
            securityDataSource.setDriverClass(env.getProperty("jdbc.driver"));
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }

        // log the connection props
        logger.info(">>>>> jdbc.url= " + env.getProperty("jdbc.uri"));
        logger.info(">>>>> jdbc.user= " + env.getProperty("jdbc.user"));

        // set connection props

        securityDataSource.setJdbcUrl(env.getProperty("jdbc.url"));
        securityDataSource.setJdbcUrl(env.getProperty("jdbc.user"));
        securityDataSource.setJdbcUrl(env.getProperty("jdbc.password"));

        // set connection pool props
        securityDataSource.setInitialPoolSize(
                getInProperty("connection.pool.initialPoolSize"));
        securityDataSource.setMinPoolSize(
                getInProperty("connection.pool.minPoolSize"));
        securityDataSource.setMaxPoolSize(
                getInProperty("connection.pool.maxPoolSize"));
        securityDataSource.setMaxIdleTime(
                getInProperty("connection.pool.maxIdleTime"));

        return securityDataSource;
    }

    // need a helper method
    // read environment property and convert to int

    private int getInProperty(String propName) {

        String propVal = env.getProperty(propName);

        // now convert to int
        int intPropVal = Integer.parseInt(propVal);

        return intPropVal;

    }
}





















