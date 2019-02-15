package com.hibernatetutorial.demo.configuration;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.boot.autoconfigure.jdbc.JdbcOperationsDependsOnPostProcessor;
import org.springframework.boot.jdbc.SchemaManagement;
import org.springframework.boot.jdbc.SchemaManagementProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcOperations;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(prefix = "spring.flyway",name = "enabled",matchIfMissing = true)
public class FlywayConfiguration {

    @Bean
    @Primary
    public SchemaManagementProvider flywayDefaultDdlModeProvider(ObjectProvider<Flyway> flyways){
        return new SchemaManagementProvider() {
            @Override
            public SchemaManagement getSchemaManagement(DataSource dataSource) {
                return SchemaManagement.MANAGED;
            }
        };

    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource){
        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);
        flyway.setDataSource(dataSource);
        return flyway;
    }
    @Bean
    public FlywayMigrationInitializer flywayMigrationInitializer(Flyway flyway){
        return new FlywayMigrationInitializer(flyway,null);
    }


    @Configuration
    @ConditionalOnClass(JdbcOperations.class)
    @ConditionalOnBean(JdbcOperations.class)
    protected static class FlywayInitializerJdbcOperationsDependencyConfiguration extends JdbcOperationsDependsOnPostProcessor{

        public FlywayInitializerJdbcOperationsDependencyConfiguration(){
            super("flywayInitialzier");
        }
    }
}
