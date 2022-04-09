package edu.phystech.servicemesh.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collection;
import java.util.Collections;

import static java.lang.String.format;

@Configuration
@PropertySource({"classpath:mongodb.properties"})
@EnableMongoRepositories(
        basePackages = "edu.phystech.servicemesh.repositories"
)
public class MongoConfig extends AbstractMongoClientConfiguration {
    private final Environment environment;

    public MongoConfig(Environment environment) {
        this.environment = environment;
    }

    @Override
    protected String getDatabaseName() {
        return environment.getProperty("mongodb.services.db");
    }

    @Override
    public MongoClient mongoClient() {
        String host = environment.getProperty("mongodb.services.host");
        String port = environment.getProperty("mongodb.services.port");
        ConnectionString connectionString = new ConnectionString(format("mongodb://%s:%s/%s", host, port, getDatabaseName()));
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Override
    public Collection getMappingBasePackages() {
        return Collections.singleton("edu.phystech.servicemash.repositories");
    }
}