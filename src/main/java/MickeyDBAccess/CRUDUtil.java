package MickeyDBAccess;

import java.util.List;

import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;

public class CRUDUtil {

	public static void setColumnValues(SelectQuery sq, Column[] columns) {

		if (columns == null || columns.length == 0) {
			return;
		}
		for (Column col : columns) {
			sq.addSelectColumn(col);
		}

	}

	public static Row getFirstRow(DataObject dob, String tableName) {
		if (dob != null) {
			try {
				return dob.getFirstRow(tableName);
			} catch (DataAccessException e) {
				throw new RuntimeException("Error While Fetching First Row", e);
			}
		}
		throw new RuntimeException("Data Object is Null");
	}

	public static Criteria createCriteria(String table, String column, Object Value, int condition) {
		Criteria criteria = new Criteria(Column.getColumn(table, column), condition);
		return criteria;
	}

	public static Criteria getNotDeletedCriteria(String table) {
		Criteria criteria = new Criteria(Column.getColumn(table, "is_deleted"), false, QueryConstants.EQUAL);
		Criteria criteria2 = new Criteria(Column.getColumn(table, "is_deleted"), null, QueryConstants.EQUAL);
		return criteria.or(criteria2);
	}

	public static void setJoins(SelectQuery sq, List<Join> joinList) {

		for (Join join : joinList) {
			sq.addJoin(join);
		}
	}

	public static boolean DOBValidator(DataObject dob) {
		if (dob == null || dob.isEmpty()) {
			return false;
		}
		return true;
	}

	public static long parseLongFromRow(Row row, String column) {
		if (row == null || column == null || column.isEmpty()) {
			throw new RuntimeException("Null Value Received While Parsing Long value in row.");
		}
		Object rowValue = row.get(column);
		if (rowValue == null) {
			throw new RuntimeException("Column is not found in the Row, During Parsing Long Value in row");
		}
		long value = ((Number) rowValue).longValue();
		return value;
	}

	public static double parseDoubleFromRow(Row row, String column) {
		if (row == null || column == null || column.isEmpty()) {
			throw new RuntimeException("Null Value Received While Parsing Double value in row.");
		}
		Object rowValue = row.get(column);
		if (rowValue == null) {
			throw new RuntimeException("Column is not found in the Row, During Parsing Double Value in row");
		}
		double value = ((Number) rowValue).doubleValue();
		return value;
	}

	public static String parseStringFromRow(Row row, String column) {
		if (row == null || column == null || column.isEmpty()) {
			throw new RuntimeException("Null Value Received While Parsing String value in row.");
		}
		Object rowValue = row.get(column);
		if (rowValue == null) {
			throw new RuntimeException("Column is not found in the Row, During Parsing String Value in row");
		}
		String value = (String) rowValue;
		return value;
	}

	public  static Join buildJoin(String table1, String table2, String[] param1, String[] param2, int joinType) {
		if (table1 == null || table2 == null) {
			throw new IllegalArgumentException("Table names cannot be null");
		}
		if (param1 == null || param2 == null) {
			throw new IllegalArgumentException("Join columns cannot be null");
		}
		if (param1.length != param2.length) {
			throw new IllegalArgumentException("Join columns size mismatch");
		}

		return new Join(table1, table2, param1, param2, joinType);
	}

}
