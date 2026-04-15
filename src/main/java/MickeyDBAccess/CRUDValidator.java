package MickeyDBAccess;

public class CRUDValidator {

	public static void validateTable(String tableName, String operation) {
		if (tableName == null || tableName.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"Error at " + operation + " CRUD: Table name should not be null or empty.");
		}
	}

	public static void validateColumnsAndParams(String[] columns, Object[] params) {
		if (columns == null || params == null) {
			throw new IllegalArgumentException("Columns and params must not be null.");
		}

		if (columns.length != params.length) {
			throw new IllegalArgumentException("Columns and params must have the same length.");
		}
	}

	public static void validateDataObject(Object dob) {
		if (dob == null) {
			throw new RuntimeException("DataObject is null");
		}
	}

}
