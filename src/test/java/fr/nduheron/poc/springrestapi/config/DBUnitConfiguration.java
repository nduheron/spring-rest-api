package fr.nduheron.poc.springrestapi.config;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DBUnitConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean
    public DatabaseConfigBean dbUnitDatabaseConfig() {
        DatabaseConfigBean dbConfig = new com.github.springtestdbunit.bean.DatabaseConfigBean();
        dbConfig.setDatatypeFactory(new PostgresqlDataTypeFactory());
        return dbConfig;
    }

    @Bean
    public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() {
        DatabaseDataSourceConnectionFactoryBean dbConnection = new DatabaseDataSourceConnectionFactoryBean(dataSource);
        dbConnection.setDatabaseConfig(dbUnitDatabaseConfig());
        return dbConnection;
    }

    @Bean
    public IDatabaseTester dbTester() throws Exception {
        DefaultDatabaseTester defaultDatabaseTester = new DefaultDatabaseTester(dbUnitDatabaseConnection().getObject());
        defaultDatabaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        return defaultDatabaseTester;
    }

}
