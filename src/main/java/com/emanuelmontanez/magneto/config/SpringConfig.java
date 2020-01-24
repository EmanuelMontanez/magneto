package com.emanuelmontanez.magneto.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ComponentScan (basePackages = {"com.emanuelmontanez.magneto"})
@EnableMongoRepositories (basePackages = {"com.emanuelmontanez.magneto"})
public class SpringConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoClientUriStr;

    @Bean
    public MongoDbFactory mongoDbFactory() {
        if (mongoClientUriStr.trim().equalsIgnoreCase("localhost")) {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            return new SimpleMongoDbFactory(mongoClient, "magneto");
        }
        MongoClientURI mongoClientUri = new MongoClientURI(mongoClientUriStr);
        return new SimpleMongoDbFactory(mongoClientUri);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        return mongoTemplate;
    }
}
