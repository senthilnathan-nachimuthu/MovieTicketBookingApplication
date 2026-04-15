package DAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.movieticketbooking.*;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;

import Exceptions.ApplicationException;
import MickeyDBAccess.CRUDOperation;
import MickeyDBAccess.CRUDUtil;
import MickeyDBAccess.CriteriaBuilder;
import MickeyDBAccess.Operator;
import Schemas.Location;
import Schemas.Theatre;

public class theatreDAO {

	public Theatre findByTheatrename(String theatreName) {
		Criteria finalCriteria = new CriteriaBuilder()
				.add(THEATRES.TABLE, THEATRES.THEATRE_NAME, theatreName, QueryConstants.EQUAL, Operator.AND)
				.add(CRUDUtil.getNotDeletedCriteria(THEATRES.TABLE), Operator.AND).build();

		DataObject dob = CRUDOperation.SelectAllColumns(THEATRES.TABLE, finalCriteria, null);
		if (CRUDUtil.DOBValidator(dob)) {
			return getTheatreObj(dob);
		}
		return null;
	}

	public String findLocation(long location_id) {

		Criteria criteria = new CriteriaBuilder()
				.add(LOCATION.TABLE, LOCATION.LOCATION_ID, location_id, QueryConstants.EQUAL, Operator.AND).build();
		DataObject dob = CRUDOperation.SelectAllColumns(LOCATION.TABLE, criteria, null);
		if (CRUDUtil.DOBValidator(dob)) {
			Row row = CRUDUtil.getFirstRow(dob, LOCATION.TABLE);
			if (row != null) {
				return (String) row.get(LOCATION.LOCATION);
			}
		}
		return null;
	}

	private Location findLocationByName(String theatreLocation) {
		Criteria criteria = new CriteriaBuilder()
				.add(LOCATION.TABLE, LOCATION.LOCATION, theatreLocation, QueryConstants.EQUAL, Operator.AND).build();
		DataObject dob = CRUDOperation.SelectAllColumns(LOCATION.TABLE, criteria, null);
		if (CRUDUtil.DOBValidator(dob)) {
			return getLocationObject(dob);
		}
		return null;
	}

	public Theatre addTheatre(String theatreName, String theatreLocation) throws ApplicationException {

		Location locationObj = addLocation(theatreLocation);
		if (locationObj != null) {

			Row row = new Row(THEATRES.TABLE);
			row.set(THEATRES.THEATRE_NAME, theatreName);
			row.set(THEATRES.LOCATION_ID, locationObj.getLocation_id());

			DataObject dob = CRUDOperation.insertOneRow(THEATRES.TABLE, row);
			if (CRUDUtil.DOBValidator(dob)) {
				return findByTheatrename(theatreName);
			}
		}
		return null;

	}

	public Map<Long, Theatre> findAllTheatresByMapping() {

		List<Join> joinList = new ArrayList<>();
		joinList.add(constructTheatreAndLocationJoin());
		Criteria criteria = CRUDUtil.getNotDeletedCriteria(THEATRES.TABLE);
		DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(THEATRES.TABLE, criteria, joinList, null);
		if (CRUDUtil.DOBValidator(dob)) {
			return getTheatreObjectListByMapping(dob);
		}
		return null;
	}

	public Theatre isTheatreExists(String theatreName, String theatreLocation) {

		Location locationObj = findLocationByName(theatreLocation);
		if (locationObj != null) {
			Criteria criteria = new CriteriaBuilder()
					.add(THEATRES.TABLE, THEATRES.THEATRE_NAME, theatreName, QueryConstants.EQUAL, Operator.AND)
					.add(THEATRES.TABLE, THEATRES.LOCATION_ID, locationObj.getLocation_id(), QueryConstants.EQUAL,
							Operator.AND)
					.add(CRUDUtil.getNotDeletedCriteria(THEATRES.TABLE), Operator.AND).build();

			DataObject dob = CRUDOperation.SelectAllColumns(THEATRES.TABLE, criteria, null);
			System.out.println("Fetched Data-->" + dob);
			if (CRUDUtil.DOBValidator(dob)) {
				return getTheatreObj(dob);
			}
		}
		return null;

	}

	public boolean isTheatreDeleted(long theatreId) {

		Criteria criteria = new CriteriaBuilder()
				.add(THEATRES.TABLE, THEATRES.THEATRE_ID, theatreId, QueryConstants.EQUAL, Operator.AND).build();
		Column[] columns = new Column[] { Column.getColumn(THEATRES.TABLE, THEATRES.IS_DELETED), };
		DataObject dob = CRUDOperation.SelectSpecificColumns(THEATRES.TABLE, criteria, columns);
		if (CRUDUtil.DOBValidator(dob)) {
			try {
				Row row = dob.getFirstRow(THEATRES.TABLE);
				if (row != null) {
					return (boolean) row.get(THEATRES.IS_DELETED);
				}
			} catch (Exception e) {
				throw new RuntimeException("Error fetching theatre delete status", e);
			}

		}
		throw new RuntimeException("Theatre not found for id: " + theatreId);
	}

	private Location addLocation(String theatreLocation) {

		Criteria criteria = new CriteriaBuilder()
				.add(LOCATION.TABLE, LOCATION.LOCATION, theatreLocation, QueryConstants.EQUAL, Operator.AND).build();
		DataObject dob = CRUDOperation.SelectAllColumns(LOCATION.TABLE, criteria, null);
		if (dob != null && !dob.isEmpty()) {
			return getLocationObject(dob);
		} else {
			Row row = new Row(LOCATION.TABLE);
			row.set(LOCATION.LOCATION, theatreLocation);
			dob = CRUDOperation.insertOneRow(LOCATION.TABLE, row);
			if (CRUDUtil.DOBValidator(dob)) {
				return getLocationObject(dob);
			}
		}
		return null;

	}

	public Theatre getTheatreObj(DataObject dob) {
		try {
			Iterator<Row> row = dob.getRows(THEATRES.TABLE);
			if (row.hasNext()) {
				Row r = row.next();
				long theatreId = ((Number) r.get(THEATRES.THEATRE_ID)).longValue();
				String theatrename = (String) r.get(THEATRES.THEATRE_NAME);
				long locationId = ((Number) r.get(THEATRES.LOCATION_ID)).longValue();
				Theatre theatreObj = new Theatre(theatreId, theatrename, findLocation(locationId));
				return theatreObj;
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	public List<Theatre> findAllTheatres() {

		List<Join> joinList = new ArrayList<>();
		joinList.add(constructTheatreAndLocationJoin());
		joinList.add(constructTheatreAndCreditsJoin());
		Criteria criteria = CRUDUtil.getNotDeletedCriteria(THEATRES.TABLE);
		DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(THEATRES.TABLE, criteria, joinList, null);
		if (CRUDUtil.DOBValidator(dob)) {
			return getTheatreObjectList(dob);
		}
		return new ArrayList<>();
	}

	private List<Theatre> getTheatreObjectList(DataObject dob) {
		System.out.println("Theatres Details--> " + dob);
		List<Theatre> list = new ArrayList<>();
		try {
			Iterator<Row> rows = dob.getRows(THEATRES.TABLE);
			while (rows.hasNext()) {
				Row row = rows.next();
				long theatre_id = ((Number) row.get(THEATRES.THEATRE_ID)).longValue();
				String theatre_name = (String) row.get(THEATRES.THEATRE_NAME);
				long location_id = ((Number) row.get(THEATRES.LOCATION_ID)).longValue();
				Row locationRow = dob.getRow(LOCATION.TABLE, new Criteria(
						Column.getColumn(LOCATION.TABLE, LOCATION.LOCATION_ID), location_id, QueryConstants.EQUAL));

				String location = locationRow != null ? (String) locationRow.get(LOCATION.LOCATION) : null;

				Row creditRow = dob.getRow(CREDIT_POINTS.TABLE,
						new Criteria(Column.getColumn(CREDIT_POINTS.TABLE, CREDIT_POINTS.THEATRE_ID), theatre_id,
								QueryConstants.EQUAL));
				double credits = creditRow != null ? ((Number) creditRow.get(CREDIT_POINTS.CREDITS)).doubleValue() : 0;

				Theatre theatreObj = new Theatre(theatre_id, theatre_name, location);
				theatreObj.setTheatreCredit(credits);
				list.add(theatreObj);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error while mapping theatre list", e);
		}
		return list;

	}

	private Map<Long, Theatre> getTheatreObjectListByMapping(DataObject dob) {

		Map<Long, Theatre> list = new HashMap<>();
		try {
			Iterator<Row> rows = dob.getRows(THEATRES.TABLE);
			while (rows.hasNext()) {
				Row row = rows.next();
				long theatre_id = ((Number) row.get(THEATRES.THEATRE_ID)).longValue();
				String theatre_name = (String) row.get(THEATRES.THEATRE_NAME);
				Row locationRow = dob.getRow(LOCATION.TABLE, row);
				String location = null;
				if (locationRow != null) {
					location = (String) locationRow.get(LOCATION.LOCATION);
				}
				Theatre theatreObj = new Theatre(theatre_id, theatre_name, location);
				list.put(theatre_id, theatreObj);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error while mapping theatre list", e);
		}

		return list;

	}

	public boolean deleteByTheatreId(long theatreId) {

		Criteria criteria = new CriteriaBuilder()
				.add(THEATRES.TABLE, THEATRES.THEATRE_ID, theatreId, QueryConstants.EQUAL, Operator.AND).build();
		DataObject dob = CRUDOperation.updateSingleColumn(THEATRES.TABLE, criteria, THEATRES.IS_DELETED, true);
		if (CRUDUtil.DOBValidator(dob)) {
			return true;
		}
		return false;
	}

	public Theatre findTheatreById(long theatreId) {

		Criteria finalCriteria = new CriteriaBuilder()
				.add(THEATRES.TABLE, THEATRES.THEATRE_ID, theatreId, QueryConstants.EQUAL, Operator.AND)
				.add(CRUDUtil.getNotDeletedCriteria(THEATRES.TABLE), Operator.AND).build();

		DataObject dob = CRUDOperation.SelectAllColumns(THEATRES.TABLE, finalCriteria, null);
		if (CRUDUtil.DOBValidator(dob)) {
			return getTheatreObj(dob);
		}
		return null;

	}

	public boolean updateTheatreDetails(Theatre theatreObj) {

		try {
			Location locationObj = addLocation(theatreObj.getTheatreLocation());

			List<Join> joins = new ArrayList<>();
			joins.add(new Join(THEATRES.TABLE, CREDIT_POINTS.TABLE, new String[] { THEATRES.THEATRE_ID },
					new String[] { CREDIT_POINTS.THEATRE_ID }, Join.INNER_JOIN));

			Criteria criteria = new CriteriaBuilder().add(THEATRES.TABLE, THEATRES.THEATRE_ID,
					theatreObj.getTheatreId(), QueryConstants.EQUAL, Operator.AND).build();

			DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(THEATRES.TABLE, criteria, joins, null);

			if (dob == null || dob.isEmpty()) {
				return false;
			}

			Row theatreRow = dob.getFirstRow(THEATRES.TABLE);
			theatreRow.set(THEATRES.THEATRE_NAME, theatreObj.getTheatreName());
			theatreRow.set(THEATRES.LOCATION_ID, locationObj.getLocation_id());
			dob.updateRow(theatreRow);

			Row creditRow = dob.getRow(CREDIT_POINTS.TABLE, theatreRow);

			if (creditRow != null) {
				creditRow.set(CREDIT_POINTS.CREDITS, theatreObj.getTheatreCredit());
				dob.updateRow(creditRow);
			}

			CRUDOperation.updateDataObject(dob);
			return true;

		} catch (Exception e) {
			throw new RuntimeException("Error updating theatre details", e);
		}
	}

	public Location getLocationObject(DataObject dob) {
		try {
			Row row = dob.getFirstRow(LOCATION.TABLE);
			if (row != null) {
				long locationId = ((Number) row.get(LOCATION.LOCATION_ID)).longValue();
				String location = (String) row.get(LOCATION.LOCATION);
				return new Location(locationId, location);
			}
		} catch (DataAccessException e) {
			throw new RuntimeException("Error While Fetching Existing Location.");
		}
		return null;
	}

	public Join constructTheatreAndLocationJoin() {

		Join j1 = new Join(THEATRES.TABLE, LOCATION.TABLE, new String[] { THEATRES.LOCATION_ID },
				new String[] { LOCATION.LOCATION_ID }, Join.INNER_JOIN);
		return j1;
	}

	public Join constructTheatreAndCreditsJoin() {
		Join j1 = new Join(THEATRES.TABLE, CREDIT_POINTS.TABLE, new String[] { THEATRES.THEATRE_ID },
				new String[] { CREDIT_POINTS.THEATRE_ID }, Join.INNER_JOIN);
		return j1;
	}

}
