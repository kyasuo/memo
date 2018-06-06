package org.terasoluna.batch.async.db;

import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.stereotype.Component;
import org.terasoluna.batch.async.db.repository.BatchJobRequestRepository;

@Intercepts({
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class }),
		@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
@Component
public class BatchRepositoryInterceptor implements Interceptor {

	private static final String TARGET_QUERY = BatchJobRequestRepository.class.getName() + ".find";
	private static final String TARGET_UPDATE = BatchJobRequestRepository.class.getName() + ".updateStatus";

	private final JobExplorer jobExplorer;

	public BatchRepositoryInterceptor(JobExplorer jobExplorer) {
		super();
		this.jobExplorer = jobExplorer;
	}

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		transformParameters(((MappedStatement) invocation.getArgs()[0]).getId(),
				(Map<String, Object>) invocation.getArgs()[1]);
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
	}

	// TODO generalize transformation(id,transformation)
	protected void transformParameters(String id, Map<String, Object> paramMap) {
		if (TARGET_QUERY.equals(id)) {
			// TODO add parameters, transform, etc...
		} else if (TARGET_UPDATE.equals(id)) {
			// TODO add parameters, transform, etc...
		}
	}

}
