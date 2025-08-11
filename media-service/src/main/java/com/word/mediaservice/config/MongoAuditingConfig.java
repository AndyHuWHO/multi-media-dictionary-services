package com.word.mediaservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@Configuration
@EnableReactiveMongoAuditing(modifyOnCreate = true)
public class MongoAuditingConfig {
}