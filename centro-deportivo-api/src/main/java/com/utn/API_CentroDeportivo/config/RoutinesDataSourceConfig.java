package com.utn.API_CentroDeportivo.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.utn.API_CentroDeportivo.model.repository.routine",
        entityManagerFactoryRef = "routinesEntityManagerFactory",
        transactionManagerRef  = "routinesTransactionManager")
public class RoutinesDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.routines")
    public DataSource routinesDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean routinesEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(routinesDataSource());
        emf.setPackagesToScan("com.utn.API_CentroDeportivo.model.entity.routine");
        emf.setPersistenceUnitName("routinesPU");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setJpaPropertyMap(jpaProperties());
        return emf;
    }

    @Bean
    public PlatformTransactionManager routinesTransactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(routinesEntityManagerFactory().getObject());
        return tm;
    }

    private Map<String, Object> jpaProperties() {
        Map<String, Object> p = new HashMap<>();
        p.put("hibernate.hbm2ddl.auto", "update");
        p.put("hibernate.show_sql", "true");
        p.put("hibernate.format_sql", "true");
        p.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        p.put("hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        return p;
    }
}