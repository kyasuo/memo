import java.util.ArrayList;
import java.util.List;

import jp.terasoluna.fw.batch.openapi.BLogic;
import jp.terasoluna.fw.batch.openapi.BLogicResult;
import jp.terasoluna.fw.batch.openapi.JobContext;
import jp.terasoluna.fw.batch.openapi.ReturnCode;
import jp.terasoluna.fw.dao.QueryDAO;
import jp.terasoluna.fw.dao.SqlHolder;
import jp.terasoluna.fw.dao.UpdateDAO;

public class DBTestBLogic implements BLogic<Object, JobContext> {

	private final QueryDAO queryDAO;
	private final UpdateDAO updateDAO;

	public DBTestBLogic(QueryDAO queryDAO, UpdateDAO updateDAO) {
		super();
		this.queryDAO = queryDAO;
		this.updateDAO = updateDAO;
	}

	static final String SCD = "113";
	static final String[] CD = { "703", "800", "903" };

	public BLogicResult execute(Object param, JobContext jobContext) {

		List<Shiten> select1 = queryDAO.executeForObjectList("selectShiten", null);

		System.out.println("selected:" + select1.size());

		Shiten bindParam = new Shiten();
		bindParam.setCd(SCD);
		bindParam.setName("Shiten_" + SCD);
		bindParam.setAddress("Japan Tokyo " + SCD);
		bindParam.setTel("0120-000-" + SCD);
		updateDAO.execute("insertShiten", bindParam);

		List<Shiten> select2 = queryDAO.executeForObjectList("selectShiten", null);

		System.out.println("selected:" + select2.size());

		List<SqlHolder> sqlHolders = new ArrayList<SqlHolder>();
		for (String cd : CD) {
			bindParam = new Shiten();
			bindParam.setCd(cd);
			bindParam.setName("Shiten_" + cd);
			bindParam.setAddress("Japan Tokyo " + cd);
			bindParam.setTel("0120-000-" + cd);
			sqlHolders.add(new SqlHolder("insertShiten", bindParam));
		}
		try {
			int updateCount = updateDAO.executeBatch(sqlHolders);
			System.out.println("updated:" + updateCount);

			List<Shiten> select3 = queryDAO.executeForObjectList("selectShiten", null);
			System.out.println("selected:" + select3.size());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new BLogicResult(ReturnCode.NORMAL_CONTINUE);
	}

}
