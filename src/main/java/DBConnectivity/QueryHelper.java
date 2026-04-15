package DBConnectivity;

public class QueryHelper {

	String query;
	Object[] params;

	public QueryHelper(String query, Object... params) {
		this.query = query;
		this.params = params;

	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

}
