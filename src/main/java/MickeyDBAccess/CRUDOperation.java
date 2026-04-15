package MickeyDBAccess;

import java.util.Iterator;
import java.util.List;

import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.Row;

import Exceptions.ApplicationException;

public class CRUDOperation {

	public static Persistence getPersistance() throws Exception {
		return (Persistence) BeanUtil.lookup("Persistence");
	}

	public static DataObject insertOneRow(String table, Row row) {

		CRUDValidator.validateTable(table, "INSERT");
		try {
			DataObject dob = DataAccess.constructDataObject();
			if (row != null) {
				dob.addRow(row);
			}
			Persistence p = getPersistance();
			if (p == null) {
				throw new RuntimeException("Persistence bean not initialized");
			}
			p.add(dob);
			return dob;
		} catch (Exception e) {
			throw new RuntimeException("Error inserting rows into table: " + table, e);
		}
	}

	public static DataObject insertMultipleRows(String table, List<Row> rows) {

		CRUDValidator.validateTable(table, "INSERT");
		try {
			DataObject dob = DataAccess.constructDataObject();
			if (rows != null) {
				for (Row row : rows) {
					dob.addRow(row);
				}
			}
			Persistence p = getPersistance();
			if (p == null) {
				throw new RuntimeException("Persistence bean not initialized");
			}
			p.add(dob);
			return dob;
		} catch (Exception e) {
			throw new RuntimeException("Error inserting rows into table: " + table, e);
		}
	}

	public static DataObject insertDataObject(DataObject insertDO) {

		if (insertDO == null || insertDO.isEmpty()) {
			throw new ApplicationException("DataObject is empty, nothing to insert");
		}

		try {
			Persistence p = getPersistance();
			if (p == null) {
				throw new RuntimeException("Persistence bean not initialized");
			}

			return p.add(insertDO);

		} catch (Exception e) {
			throw new RuntimeException("Error inserting DataObject", e);
		}
	}

	public static DataObject SelectAllColumns(String tableName, Criteria criteria, SortColumn[] sortColumns) {

		CRUDValidator.validateTable(tableName, "Select");

		SelectQuery sq = new SelectQueryImpl(Table.getTable(tableName));
		sq.addSelectColumn(Column.getColumn(tableName, "*"));

		if (sortColumns != null && sortColumns.length > 0) {
			for (SortColumn sc : sortColumns) {
				sq.addSortColumn(sc);
			}
		}

		if (criteria != null) {
			sq.setCriteria(criteria);
		}
		DataObject dob;

		try {
			Persistence p = getPersistance();
			if (p == null) {
				throw new RuntimeException("Persistence bean not initialized");
			}
			dob = p.get(sq);
			return dob;
		} catch (Exception e) {
			throw new RuntimeException("Error fetching rows from table: " + tableName, e);
		}
	}

	public static DataObject SelectSpecificColumns(String tableName, Criteria criteria, Column columns[]) {
		CRUDValidator.validateTable(tableName, "Select");

		SelectQuery sq = new SelectQueryImpl(Table.getTable(tableName));
		if (columns == null || columns.length == 0) {
			throw new RuntimeException("Columns Should not be Empty in this SelectSpecificColumns Function.");
		}
		CRUDUtil.setColumnValues(sq, columns);
		if (criteria != null) {
			sq.setCriteria(criteria);
		}
		DataObject dob;

		try {
			Persistence p = getPersistance();
			if (p == null) {
				throw new RuntimeException("Persistence bean not initialized");
			}
			dob = p.get(sq);
			return dob;
		} catch (Exception e) {
			throw new RuntimeException("Error fetching rows from table: " + tableName, e);
		}
	}

	public static DataObject SelectAllColumnsWithJoin(String tableName, Criteria criteria, List<Join> joinList,
			SortColumn[] sortColumns) {
		CRUDValidator.validateTable(tableName, "Select");

		SelectQuery sq = new SelectQueryImpl(Table.getTable(tableName));
		CRUDUtil.setJoins(sq, joinList);
		sq.addSelectColumn(Column.getColumn(tableName, "*"));

		if (joinList != null) {
			for (Join join : joinList) {
				String joinedTable = join.getReferencedTableName();
				sq.addSelectColumn(Column.getColumn(joinedTable, "*"));
			}
		}
		if (criteria != null) {
			sq.setCriteria(criteria);
		}
		if (sortColumns != null) {
			for (SortColumn sc : sortColumns) {
				sq.addSortColumn(sc);
			}
		}
		DataObject dob;

		try {
			Persistence p = getPersistance();
			if (p == null) {
				throw new RuntimeException("Persistence bean not initialized");
			}
			dob = p.get(sq);
			return dob;
		} catch (Exception e) {
			throw new RuntimeException("Error fetching rows from table: " + tableName, e);
		}
	}

	public static DataObject updateSingleColumn(String tableName, Criteria criteria, String columnToUpdate,
			Object updatedValue) {
		CRUDValidator.validateTable(tableName, "Update");
		try {

			Persistence p = getPersistance();
			if (p == null) {
				throw new RuntimeException("Persistence bean not initialized");
			}
			UpdateQuery update = new UpdateQueryImpl(tableName);
			if (criteria == null) {
				throw new IllegalArgumentException("Criteria cannot be null for update operation");
			}
			update.setCriteria(criteria);
			update.setUpdateColumn(columnToUpdate, updatedValue);
			p.update(update);
			return p.get(tableName, criteria);

		} catch (Exception e) {
			throw new RuntimeException("Error Updating table single Column in a row: " + tableName, e);
		}
	}

	public static DataObject updateMultipleColumns(String tableName, String[] columns, Object[] values,
			Criteria criteria) {
		CRUDValidator.validateTable(tableName, "Update");
		if (columns.length != values.length) {
			throw new ApplicationException("Columns and its value mismatched");
		}
		if (criteria == null) {
			throw new ApplicationException("Criteria cannot be null for update");
		}
		try {
			Persistence p = getPersistance();
			if (p == null) {
				throw new RuntimeException("Persistence bean not initialized");
			}
			UpdateQuery update = new UpdateQueryImpl(tableName);
			update.setCriteria(criteria);
			for (int i = 0; i < columns.length; i++) {
				update.setUpdateColumn(columns[i], values[i]);
			}
			p.update(update);

			return p.get(tableName, criteria);

		} catch (Exception e) {
			throw new RuntimeException("Error Updating table single Column in a row: " + tableName, e);
		}
	}

	public static DataObject updateColumnsWithJoin(String tableName, String[] columns, Object[] values,
			Criteria criteria, List<Join> joins) {

		CRUDValidator.validateTable(tableName, "Update");
		if (columns.length != values.length) {
			throw new ApplicationException("Columns and its value mismatched");
		}
		if (joins == null || joins.isEmpty()) {
			throw new ApplicationException("Join cannot be null or Empty for this Function-UpdateColumnsWithJoin()");

		}
		if (criteria == null) {
			throw new ApplicationException("Criteria cannot be null for update");
		}
		try {
			Persistence p = getPersistance();
			if (p == null) {
				throw new RuntimeException("Persistence bean not initialized");
			}

			UpdateQuery update = new UpdateQueryImpl(tableName);
			update.setCriteria(criteria);
			for (Join join : joins) {
				update.addJoin(join);
			}
			for (int i = 0; i < columns.length; i++) {
				update.setUpdateColumn(columns[i], values[i]);
			}

			p.update(update);

			return p.get(tableName, criteria);

		} catch (Exception e) {
			throw new RuntimeException("Error Updating table single Column in a row: " + tableName, e);
		}
	}

	public static DataObject updateDataObject(DataObject dob) {

		if (dob == null || dob.isEmpty()) {
			throw new ApplicationException("DataObject is empty, nothing to update");
		}

		try {
			Persistence p = getPersistance();
			if (p == null) {
				throw new RuntimeException("Persistence bean not initialized");
			}

			return p.update(dob);

		} catch (Exception e) {
			throw new RuntimeException("Error updating DataObject", e);
		}
	}

	public static void deleteRows(String tableName, Criteria criteria) {

		if (criteria == null) {
			throw new RuntimeException("Deletion should not be done without criteria");
		}

		try {
			Persistence p = getPersistance();

			DeleteQuery dq = new DeleteQueryImpl(tableName);
			dq.setCriteria(criteria);

			p.delete(dq);

		} catch (Exception e) {
			throw new RuntimeException("Error deleting rows from table: " + tableName, e);
		}
	}

}
