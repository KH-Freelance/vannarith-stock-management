package com.hfsolution.app.config.database.routing;


import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import com.hfsolution.app.config.database.context.PostgressDataSourceContextHolder;
import com.hfsolution.app.enums.EDataSourceType;


@Component("postgressDataSourceRouter")
public class PostgressDataSourceRouter extends AbstractRoutingDataSource{

	private PostgressDataSourceContextHolder postgressDataSourceContextHolder;
	
	public PostgressDataSourceRouter(			
			@Qualifier("postgressDataSourceContextHolder") PostgressDataSourceContextHolder postgressDataSourceContextHolder,
			@Qualifier("postgressDataSource") DataSource postgressPrimaryDataSource,
			@Qualifier("postgressDataSource") DataSource postgressSecondaryDataSource
			) {
		this.postgressDataSourceContextHolder = postgressDataSourceContextHolder;
		
		var dataSourceMap = new HashMap<Object, Object>();
		dataSourceMap.put(EDataSourceType.SECONDARY, postgressSecondaryDataSource);
		dataSourceMap.put(EDataSourceType.PRIMARY, postgressPrimaryDataSource);
		this.setTargetDataSources(dataSourceMap);
		if (postgressPrimaryDataSource != null)
			this.setDefaultTargetDataSource(postgressPrimaryDataSource);
	}
	
	@Override
	protected Object determineCurrentLookupKey() {
		return postgressDataSourceContextHolder.getBranchContext();
	}

}