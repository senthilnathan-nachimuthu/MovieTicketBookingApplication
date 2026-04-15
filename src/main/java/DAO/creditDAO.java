package DAO;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.movieticketbooking.*;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;

import MickeyDBAccess.CRUDOperation;
import MickeyDBAccess.CRUDUtil;
import MickeyDBAccess.CriteriaBuilder;
import MickeyDBAccess.Operator;

public class creditDAO {

	String tempError = "";

	public String getError() {
		return tempError;
	}

	public boolean insertTheatreCredits(double credit, long theatreId) {

		Row row = new Row(CREDIT_POINTS.TABLE);
		row.set(CREDIT_POINTS.THEATRE_ID, theatreId);
		row.set(CREDIT_POINTS.CREDITS, credit);
		DataObject dob = CRUDOperation.insertOneRow(CREDIT_POINTS.TABLE, row);
		if (dob != null && !dob.isEmpty()) {
			return true;
		}
		return false;
	}

	public double findCreditByTheatre(long theatreId) {

		Criteria criteria = new CriteriaBuilder()
				.add(CREDIT_POINTS.TABLE, CREDIT_POINTS.THEATRE_ID, theatreId, QueryConstants.EQUAL, Operator.AND)
				.build();
		DataObject dob = CRUDOperation.SelectAllColumns(CREDIT_POINTS.TABLE, criteria, null);
		if (CRUDUtil.DOBValidator(dob)) {
			Row row = CRUDUtil.getFirstRow(dob, CREDIT_POINTS.TABLE);
			if (row != null) {
				return CRUDUtil.parseDoubleFromRow(row, CREDIT_POINTS.CREDITS);
			}
		}
		return -1;
	}

	public boolean insertThreshold(double threshold, double cc) {

		Row row = new Row(CREDIT_MANAGEMENT.TABLE);
		row.set(CREDIT_MANAGEMENT.THRESHOLD, threshold);
		row.set(CREDIT_MANAGEMENT.CONVERSION_PERCENTAGE, cc);
		DataObject dob = CRUDOperation.insertOneRow(CREDIT_MANAGEMENT.TABLE, row);
		return CRUDUtil.DOBValidator(dob);
	}

	public double findThreshold() {

		DataObject dob = CRUDOperation.SelectAllColumns(CREDIT_MANAGEMENT.TABLE, null,
				new SortColumn[] { new SortColumn(CREDIT_MANAGEMENT.TABLE, CREDIT_MANAGEMENT.CREDIT_ID, false) });
		if (CRUDUtil.DOBValidator(dob)) {
			Row row = CRUDUtil.getFirstRow(dob, CREDIT_MANAGEMENT.TABLE);
			if (row != null) {
				return CRUDUtil.parseDoubleFromRow(row, CREDIT_MANAGEMENT.THRESHOLD);
			}
		}
		return -1;
	}

	public double findConversionPercentage() {

		DataObject dob = CRUDOperation.SelectAllColumns(CREDIT_MANAGEMENT.TABLE, null,
				new SortColumn[] { new SortColumn(CREDIT_MANAGEMENT.TABLE, CREDIT_MANAGEMENT.CREDIT_ID, false) });
		if (CRUDUtil.DOBValidator(dob)) {
			Row row = CRUDUtil.getFirstRow(dob, CREDIT_MANAGEMENT.TABLE);
			if (row != null) {
				return CRUDUtil.parseDoubleFromRow(row, CREDIT_MANAGEMENT.CONVERSION_PERCENTAGE);
			}
		}
		return -1;
	}

	public String updateCredits(long theatreId, double credit) {

		Criteria criteria = new CriteriaBuilder()
				.add(CREDIT_POINTS.TABLE, CREDIT_POINTS.THEATRE_ID, theatreId, QueryConstants.EQUAL, Operator.AND)
				.build();
		DataObject dob = CRUDOperation.updateSingleColumn(CREDIT_POINTS.TABLE, criteria, CREDIT_POINTS.CREDITS,
				credit);
		if (CRUDUtil.DOBValidator(dob)) {
			return "Updated";
		}
		return "Credits not Updated for this theatre";

	}
}
