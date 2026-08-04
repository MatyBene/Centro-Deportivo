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
        basePackages = "com.utn.API_CentroDeportivo.model.repository.users",
        entityManagerFactoryRef = "usersEntityManagerFactory",
        transactionManagerRef  = "usersTransactionManager")
public class UsersDataSourceConfig {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.users")
    public DataSource usersDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean usersEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(usersDataSource());
        emf.setPackagesToScan("com.utn.API_CentroDeportivo.model.entity.users");
        emf.setPersistenceUnitName("usersPU");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setJpaPropertyMap(jpaProperties());
        return emf;
    }

    @Primary
    @Bean
    public PlatformTransactionManager usersTransactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(usersEntityManagerFactory().getObject());
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