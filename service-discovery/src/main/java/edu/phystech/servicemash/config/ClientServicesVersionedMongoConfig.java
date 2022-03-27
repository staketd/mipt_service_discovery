package edu.phystech.servicemash.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import edu.phystech.servicemash.util.CombinedPropertyLoader;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import static java.lang.String.format;

//@Configuration
@PropertySource({"classpath:mongodb.properties"})
@EnableMongoRepositories(basePackages = "edu.phystech.servicemash.repositories.versioned")
public class ClientServicesVersionedMongoConfig/* extends AbstractMongoClientConfiguration */ {
    private final Environment environment;

    public ClientServicesVersionedMongoConfig(Environment environment) {
        this.environment = environment;
    }

    @Primary
    @Bean
    @ConfigurationProperties(prefix="spring.mongodb.versioned-services")
    public DataSource versionedServicesDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean versionedServicesEntityManager() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(versionedServicesDataSource());
        em.setPackagesToScan("edu.phystech.servicemash");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        final HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect", environment.getProperty("hibernate.dialect"));
        em.setJpaPropertyMap(properties);

        return em;
    }
//    private final CombinedPropertyLoader propertyLoader;
//
//    public ClientServicesVersionedMongoConfig(CombinedPropertyLoader propertyLoader) {
//        this.propertyLoader = propertyLoader;
//    }
//
//    @Override
//    protected String getDatabaseName() {
//        return propertyLoader.getProperty("mongodb.versioned_services.db");
//    }
//
//    @Override
//    public MongoClient mongoClient() {
//        String host = propertyLoader.getProperty("mongodb.versioned_services.host");
//        String port = propertyLoader.getProperty("mongodb.versioned_services.port");
//        ConnectionString connectionString = new ConnectionString(format("mongodb://%s:%s/%s", host, port, getDatabaseName()));
//        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
//                .applyConnectionString(connectionString)
//                .build();
//
//        return MongoClients.create(mongoClientSettings);
//    }
//
//    @Override
//    public Collection getMappingBasePackages() {
//        return Collections.singleton("edu.phystech.servicemash.repositories.versioned");
//    }
}
