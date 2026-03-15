package com.mobileshop.config;

import com.mobileshop.tenant.SchemaMultiTenantConnectionProvider;
import com.mobileshop.tenant.TenantIdentifierResolver;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class HibernateConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SchemaMultiTenantConnectionProvider connectionProvider;

    @Autowired
    private TenantIdentifierResolver tenantResolver;

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        HibernateJpaVendorAdapter vendorAdapter =
                new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);

        LocalContainerEntityManagerFactoryBean factory =
                new LocalContainerEntityManagerFactoryBean();

        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("com.mobileshop.entity");

        Properties props = new Properties();

        // ── Basic settings ──
        props.put(AvailableSettings.DIALECT,
                "org.hibernate.dialect.PostgreSQLDialect");
        props.put(AvailableSettings.SHOW_SQL, "false");
        props.put(AvailableSettings.FORMAT_SQL, "false");
        props.put(AvailableSettings.HBM2DDL_AUTO, "update");

        // ✅ THIS IS THE FIX — converts camelCase to snake_case automatically
        props.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY,
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        props.put(AvailableSettings.IMPLICIT_NAMING_STRATEGY,
                "org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl");

        // ── Multi-tenancy ──
        props.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER,
                connectionProvider);
        props.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER,
                tenantResolver);

        factory.setJpaProperties(props);
        return factory;
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(
                entityManagerFactory().getObject());
        return txManager;
    }
}