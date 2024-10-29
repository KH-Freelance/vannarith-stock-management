package com.hfsolution.app.config.database;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hfsolution.app.properties.PostgressConnectionPropeties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


@Configuration
public class DataSourceConfig {

	@Bean("postgressDataSource")
	public DataSource postgressDataSource(PostgressConnectionPropeties postgressConnectionProperties) {

		final var hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName(postgressConnectionProperties.getDriverClassName());
		hikariConfig.setJdbcUrl(postgressConnectionProperties.getJdbcUrl());
		hikariConfig.setUsername(postgressConnectionProperties.getUsername());
		hikariConfig.setPassword(postgressConnectionProperties.getPassword());
		hikariConfig.setPoolName("POSTGRESS-CONNECTION-POLL");
		hikariConfig.setMaximumPoolSize(Integer.valueOf(postgressConnectionProperties.getMaximumPoolSize()));
		hikariConfig.setConnectionTimeout(Integer.valueOf(postgressConnectionProperties.getConnectionTimeout()));
		hikariConfig.setIdleTimeout(Integer.valueOf(postgressConnectionProperties.getIdleTimeout()));
		hikariConfig.setMinimumIdle(Integer.valueOf(postgressConnectionProperties.getMinimumIdle()));
		hikariConfig.setMaxLifetime(Integer.valueOf(postgressConnectionProperties.getMaxLifetime()));
		hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", "256");
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		hikariConfig.setKeepaliveTime(Integer.parseInt(postgressConnectionProperties.getKeepAlive()));
		final var dataSource = new HikariDataSource(hikariConfig);
		return dataSource;

	}
}

