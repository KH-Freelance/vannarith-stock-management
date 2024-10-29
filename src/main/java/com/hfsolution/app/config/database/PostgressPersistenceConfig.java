package com.hfsolution.app.config.database;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.hfsolution.app.config.database.routing.PostgressDataSourceRouter;



@Configuration
@DependsOn("postgressDataSourceRouter")

@EnableJpaRepositories(basePackages = {
	"com.hfsolution.feature.user.repository",
	"com.hfsolution.feature.token.repository",
	"com.hfsolution.feature.stockmanagement.repository"
}, entityManagerFactoryRef = "postgressEntityManagerBean", transactionManagerRef = "postgressTransactionManager")
public class PostgressPersistenceConfig {


    @Autowired Environment environment;
    @Autowired PostgressDataSourceRouter postgressDataSourceRouter;





    @Bean("postgressEntityManagerBean")
	public LocalContainerEntityManagerFactoryBean postgressEntityManagerBean() {

		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(postgressDataSourceRouter);
		entityManagerFactoryBean.setPackagesToScan(new String[] { "com.hfsolution.feature.user.entity","com.hfsolution.feature.token.entity","com.hfsolution.feature.stockmanagement.entity" });
		Properties properties = new Properties();
		properties.setProperty("hibernate.jdbc.time_zone", environment.getProperty("spring.jpa.jdbc.time_zone"));
		properties.setProperty("hibernate.show_sql", environment.getProperty("spring.jpa.show-sql"));
		properties.setProperty("hibernate.jdbc.batch_size", environment.getProperty("spring.jpa.jdbc.batch_size"));
		properties.setProperty("hibernate.able_lazy_load_no_trans",environment.getProperty("postgress-connection.hibernate.enable_lazy_load_no_trans"));
		properties.setProperty("hibernate.hbm2ddl.auto", environment.getProperty("postgress-connection.hibernate.ddl-auto"));
		properties.setProperty("hibernate.event.merge.entity_copy_observer", "allow");
		properties.setProperty("hibernate.dialect", environment.getProperty("postgress-connection.hibernate.dialect"));
		entityManagerFactoryBean.setJpaProperties(properties);
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setDatabase(Database.POSTGRESQL);
		jpaVendorAdapter.setGenerateDdl(true);
		entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
		return entityManagerFactoryBean;

	}
	
	@Bean("postgressTransactionManager")
	public PlatformTransactionManager postgressTransactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(postgressEntityManagerBean().getObject());
		return transactionManager;
	}
}
