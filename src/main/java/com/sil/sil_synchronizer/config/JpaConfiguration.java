package com.sil.sil_synchronizer.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.Objects;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class JpaConfiguration {

    private Environment env = null;

    @Autowired
    public void setEnvironment(Environment env) {
        this.env = env;
    }

    @Bean(destroyMethod = "close")
    public ComboPooledDataSource dataSource() throws PropertyVetoException {

        /*
         * Create a datasource pool connection
         * and set the properties.
         * */
        ComboPooledDataSource dataSource = new ComboPooledDataSource();

        // Set properties
        dataSource.setInitialPoolSize(Integer.parseInt(Objects.requireNonNull(env.getProperty("hibernate.c3p0.min_size"))));
        dataSource.setMinPoolSize(Integer.parseInt(Objects.requireNonNull(env.getProperty("hibernate.c3p0.min_size"))));
        dataSource.setMaxPoolSize(Integer.parseInt(Objects.requireNonNull(env.getProperty("hibernate.c3p0.max_size"))));
        dataSource.setMaxIdleTime(Integer.parseInt(Objects.requireNonNull(env.getProperty("hibernate.c3p0.idle_max_idle_time"))));
        dataSource.setIdleConnectionTestPeriod(Integer.parseInt(Objects.requireNonNull(env.getProperty("hibernate.c3p0.idle_test_period"))));

        dataSource.setJdbcUrl(env.getProperty("db.url"));
        dataSource.setPassword(env.getProperty("db.password"));
        dataSource.setUser(env.getProperty("db.username"));
        dataSource.setDriverClass(env.getProperty("db.driver"));

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {

        /*
         *  Create a new entity manager and
         *  set the properties.
         * */
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);

        // Classpath scanning of @Component, @Service, etc annotated class
        entityManagerFactory.setPackagesToScan("com.sil.sil_synchronizer");

        // Vendor adapter
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);

        // Hibernate properties
        Properties additionalProperties = new Properties();
        additionalProperties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        additionalProperties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        additionalProperties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        additionalProperties.put("hibernate.naming.physical-strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
        additionalProperties.put("hibernate.event.merge.entity_copy_observer", "allow");

        entityManagerFactory.setJpaProperties(additionalProperties);

        // Return the entity manager.
        return entityManagerFactory;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}

