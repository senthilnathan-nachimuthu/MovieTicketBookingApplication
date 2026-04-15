package DAO;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.movieticketbooking.*;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;

import Exceptions.ApplicationException;
import MickeyDBAccess.CRUDOperation;
import MickeyDBAccess.CRUDUtil;
import MickeyDBAccess.CriteriaBuilder;
import MickeyDBAccess.Operator;
import Schemas.Pricing;
import Schemas.Screen;
import userInteraction.seat_Structure;

public class screenDAO {

	public long insertScreen(long theatreId, String screenType, List<seat_Structure> seatList, List<Pricing> list,
			int seatCapacity, List<LocalTime> l, String screenName) {
		long screenId = 0;
		long typeId = insertScreenType(screenType);
		if (typeId != -1) {
			Row row = constructScreenRow(theatreId, typeId, seatCapacity, screenName);
			DataObject dob = CRUDOperation.insertOneRow(SCREEN.TABLE, row);
			if (CRUDUtil.DOBValidator(dob)) {
				Row screenResult = CRUDUtil.getFirstRow(dob, SCREEN.TABLE);
				if (screenResult != null) {
					screenId = CRUDUtil.parseLongFromRow(row, SCREEN.SCREEN_ID);
					Map<String, Long> m = insertSeatTypes(screenId, list);
					insertSeatStructure(screenId, seatList, m);
					insertTiming(screenId, l);
				}
			}
		}
		return screenId;
	}

	private Row constructScreenRow(long theatreId, long typeId, int seatCapacity, String screenName) {
		Row row = new Row(SCREEN.TABLE);
		row.set(SCREEN.THEATRE_ID, theatreId);
		row.set(SCREEN.SCREEN_TYPE_ID, typeId);
		row.set(SCREEN.SEAT_CAPACITY, seatCapacity);
		row.set(SCREEN.SCREEN_NAME, screenName);
		return row;
	}

	private Map<String, Long> insertSeatTypes(long screenId, List<Pricing> list) {

		Map<String, Long> seatTypeMap = new HashMap<>();
		try {
			Set<String> seatTypes = list.stream().map(Pricing::getSeatType).collect(Collectors.toSet());
			Criteria criteria = new Criteria(Column.getColumn(SEAT_TYPE.TABLE, SEAT_TYPE.SEAT_TYPE),
					seatTypes.toArray(), QueryConstants.IN);

			DataObject seatTypeDO = CRUDOperation.SelectAllColumns(SEAT_TYPE.TABLE, criteria, null);

			if (CRUDUtil.DOBValidator(seatTypeDO)) {
				Iterator<Row> rows = seatTypeDO.getRows(SEAT_TYPE.TABLE);
				while (rows.hasNext()) {
					Row row = rows.next();
					String type = (String) row.get(SEAT_TYPE.SEAT_TYPE);
					long id = ((Number) row.get(SEAT_TYPE.SEAT_TYPE_ID)).longValue();
					seatTypeMap.put(type, id);
				}
			}
			DataObject insertDO = new WritableDataObject();

			for (String type : seatTypes) {
				if (!seatTypeMap.containsKey(type)) {
					Row row = new Row(SEAT_TYPE.TABLE);
					row.set(SEAT_TYPE.SEAT_TYPE, type);
					insertDO.addRow(row);
				}
			}
			if (!insertDO.isEmpty()) {
				DataObject resultDO = CRUDOperation.insertDataObject(insertDO);

				Iterator<Row> rows = resultDO.getRows(SEAT_TYPE.TABLE);
				while (rows.hasNext()) {
					Row row = rows.next();
					String type = (String) row.get(SEAT_TYPE.SEAT_TYPE);
					long id = ((Number) row.get(SEAT_TYPE.SEAT_TYPE_ID)).longValue();
					seatTypeMap.put(type, id);
				}
			}
			Criteria pricingCriteria = new Criteria(Column.getColumn(SCREEN_PRICING.TABLE, SCREEN_PRICING.SCREEN_ID),
					screenId, QueryConstants.EQUAL);

			DataObject pricingDO = CRUDOperation.SelectAllColumns(SCREEN_PRICING.TABLE, pricingCriteria, null);

			Set<Long> existingSeatTypeIds = new HashSet<>();
			if (CRUDUtil.DOBValidator(pricingDO)) {
				Iterator<Row> rows = pricingDO.getRows(SCREEN_PRICING.TABLE);
				while (rows.hasNext()) {
					Row row = rows.next();
					long seatTypeId = ((Number) row.get(SCREEN_PRICING.SEAT_TYPE_ID)).longValue();
					existingSeatTypeIds.add(seatTypeId);
				}
			}

			DataObject pricingInsertDO = new WritableDataObject();

			for (Pricing p : list) {
				long seatTypeId = seatTypeMap.get(p.getSeatType());
				p.setSeatTypeId(seatTypeId);

				if (!existingSeatTypeIds.contains(seatTypeId)) {
					Row row = new Row(SCREEN_PRICING.TABLE);
					row.set(SCREEN_PRICING.SCREEN_ID, screenId);
					row.set(SCREEN_PRICING.SEAT_TYPE_ID, seatTypeId);
					row.set(SCREEN_PRICING.SEAT_TYPE_PRICE, p.getPrice());
					pricingInsertDO.addRow(row);
				}
			}

			if (!pricingInsertDO.isEmpty()) {
				CRUDOperation.insertDataObject(pricingInsertDO);
			}

		} catch (Exception e) {
			throw new RuntimeException("Error inserting seat types", e);
		}

		return seatTypeMap;
	}

	private void insertTiming(long screenId, List<LocalTime> l) {

		DataObject dob = CRUDOperation.SelectAllColumns(SHOW_TIME.TABLE, null, null);
		if (dob != null) {
			Map<LocalTime, Long> timeList = getShowTimeMapping(dob);
			List<Row> rowsToInsert = new ArrayList<>();
			for (LocalTime time : l) {
				if (timeList.get(time) == null) {
					Row row = new Row(SHOW_TIME.TABLE);
					row.set(SHOW_TIME.SHOW_TIME, java.sql.Time.valueOf(time));
					rowsToInsert.add(row);
				}
			}
			DataObject insertedResult = null;
			if (!rowsToInsert.isEmpty()) {
				insertedResult = CRUDOperation.insertMultipleRows(SHOW_TIME.TABLE, rowsToInsert);
			}
			Map<LocalTime, Long> insertedTimeList = new HashMap<>();
			if (insertedResult != null && !insertedResult.isEmpty()) {
				insertedTimeList = getShowTimeMapping(insertedResult);
			}
			rowsToInsert = new ArrayList<>();
			for (LocalTime time : l) {

				long time_id = -1;
				if (timeList.get(time) != null) {
					time_id = timeList.get(time);
				} else if (insertedTimeList.get(time) != null) {
					time_id = insertedTimeList.get(time);
				}
				if (time_id != -1) {
					Row row = new Row(SHOW_TIME_MAPPING.TABLE);
					row.set(SHOW_TIME_MAPPING.SCREEN_ID, screenId);
					row.set(SHOW_TIME_MAPPING.TIME_ID, time_id);
					rowsToInsert.add(row);
				}
			}
			if (!rowsToInsert.isEmpty()) {
				insertedResult = CRUDOperation.insertMultipleRows(SHOW_TIME_MAPPING.TABLE, rowsToInsert);
			}
		}
	}

	private void insertSeatStructure(long screenId, List<seat_Structure> seatList, Map<String, Long> seatType) {

		Map<String, int[]> positionList = new HashMap<>();
		Map<String, Long> positionIdList = new HashMap<>();
		for (seat_Structure seats : seatList) {
			String key = seats.getRowIndex() + "," + seats.getColIndex();
			positionList.put(key, new int[] { seats.getRowIndex(), seats.getColIndex() });
		}

		DataObject dob = CRUDOperation.SelectAllColumns(SEAT_POSITION.TABLE, null, null);
		if (CRUDUtil.DOBValidator(dob)) {
			positionIdList = getPositionMapping(dob);
		}

		List<Row> seatPositionInsertList = new ArrayList<>();
		for (Map.Entry<String, int[]> m : positionList.entrySet()) {

			if (positionIdList.get(m.getKey()) == null) {
				int arr[] = m.getValue();
				Row row = new Row(SEAT_POSITION.TABLE);
				row.set(SEAT_POSITION.ROW_NO, arr[0]);
				row.set(SEAT_POSITION.COLUMN_NO, arr[1]);
				seatPositionInsertList.add(row);
			}
		}
		DataObject insertedDob = CRUDOperation.insertMultipleRows(SEAT_POSITION.TABLE, seatPositionInsertList);
		Map<String, Long> insertedPositions = getPositionMapping(insertedDob);
		List<Row> seatPositionMappingInsertList = new ArrayList<>();
		for (seat_Structure seats : seatList) {
			Row row = new Row(SEAT_POSITION_MAPPING.TABLE);
			long position_id = -1;
			String key = seats.getRowIndex() + "," + seats.getColIndex();
			if (positionIdList.get(key) != null) {
				position_id = positionIdList.get(key);
			} else if (insertedPositions.get(key) != null) {
				position_id = insertedPositions.get(key);
			}
			row.set(SEAT_POSITION_MAPPING.SCREEN_ID, screenId);
			row.set(SEAT_POSITION_MAPPING.POSITION_ID, position_id);
			row.set(SEAT_POSITION_MAPPING.SEAT_TYPE_ID, seatType.get(seats.getSeatType()));
			row.set(SEAT_POSITION_MAPPING.IS_DISABLED, seats.isDisabled());
			seatPositionMappingInsertList.add(row);
		}
		if (!seatPositionMappingInsertList.isEmpty()) {
			DataObject result = CRUDOperation.insertMultipleRows(SEAT_POSITION_MAPPING.TABLE,
					seatPositionMappingInsertList);
		}
	}

	private Map<String, Long> getPositionMapping(DataObject dob) {
		Map<String, Long> tempMap = new HashMap<>();
		Iterator<Row> rows;
		try {
			rows = dob.getRows(SEAT_POSITION.TABLE);
			while (rows.hasNext()) {
				Row row = rows.next();
				long rowNo = CRUDUtil.parseLongFromRow(row, SEAT_POSITION.ROW_NO);
				long colNo = CRUDUtil.parseLongFromRow(row, SEAT_POSITION.COLUMN_NO);
				long positionId = CRUDUtil.parseLongFromRow(row, SEAT_POSITION.POSITION_ID);
				String key = rowNo + "," + colNo;
				tempMap.put(key, positionId);
			}
		} catch (DataAccessException e) {
			throw new RuntimeException(e);
		}
		return tempMap;
	}

	private long insertScreenType(String screenType) {

		Criteria criteria = new CriteriaBuilder()
				.add(SCREEN_TYPE.TABLE, SCREEN_TYPE.SCREEN_TYPE, screenType, QueryConstants.EQUAL, Operator.AND)
				.build();
		DataObject dob = CRUDOperation.SelectAllColumns(SCREEN_TYPE.TABLE, criteria, null);
		if (CRUDUtil.DOBValidator(dob)) {
			Row row = CRUDUtil.getFirstRow(dob, SCREEN_TYPE.TABLE);
			if (row != null) {
				return CRUDUtil.parseLongFromRow(row, SCREEN_TYPE.SCREEN_TYPE_ID);
			}
		} else {
			Row row = new Row(SCREEN_TYPE.TABLE);
			row.set(SCREEN_TYPE.SCREEN_TYPE, screenType);
			dob = CRUDOperation.insertOneRow(SCREEN_TYPE.TABLE, row);
			if (CRUDUtil.DOBValidator(dob)) {
				row = CRUDUtil.getFirstRow(dob, SCREEN_TYPE.TABLE);
				if (row != null) {
					return CRUDUtil.parseLongFromRow(row, SCREEN_TYPE.SCREEN_TYPE_ID);
				}
			}
		}
		return -1;

	}

	public int findScreenNumber(long theatreId, long screenId) {

		Criteria criteria = new CriteriaBuilder()
				.add(SCREEN.TABLE, SCREEN.THEATRE_ID, theatreId, QueryConstants.EQUAL, Operator.AND)
				.add(CRUDUtil.getNotDeletedCriteria(SCREEN.TABLE), Operator.AND).build();

		DataObject dob = CRUDOperation.SelectAllColumns(SCREEN.TABLE, criteria, null);
		if (CRUDUtil.DOBValidator(dob)) {
			int scNo = 1;
			try {
				Iterator<Row> rows = dob.getRows(SCREEN.TABLE);
				while (rows.hasNext()) {
					Row row = rows.next();
					long existingScreenId = CRUDUtil.parseLongFromRow(row, SCREEN.SCREEN_ID);
					if (existingScreenId == screenId) {
						return scNo;
					}
					scNo++;
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}
		return -1;
	}

	public Screen findDeletedScreenByShow(long show_id) {

		List<Join> joinList = new ArrayList<>();
		joinList.add(ShowToTMDJoin());
		joinList.add(TMDToScreen());

		Criteria criteria = new CriteriaBuilder().add(SHOWS.TABLE, SHOWS.SHOW_ID, show_id, QueryConstants.EQUAL, null)
				.build();
		DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(SHOWS.TABLE, criteria, joinList, null);
		if (CRUDUtil.DOBValidator(dob)) {
			Screen screenObj = getScreenObj(dob);
			return screenObj;
		}
		return null;
	}

	public Screen findScreenByShow(long show_id) {

		List<Join> joinList = new ArrayList<>();
		joinList.add(ShowToTMDJoin());
		joinList.add(TMDToScreen());

		Criteria criteria = new CriteriaBuilder()
				.add(SHOWS.TABLE, SHOWS.SHOW_ID, show_id, QueryConstants.EQUAL, Operator.AND)
				.add(CRUDUtil.getNotDeletedCriteria(SCREEN.TABLE), Operator.AND).build();

		DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(SHOWS.TABLE, criteria, joinList, null);

		if (CRUDUtil.DOBValidator(dob)) {
			return getScreenObj(dob);
		}
		return null;
	}

	public Screen findScreen(long screenId) {
		Criteria criteria = new CriteriaBuilder()
				.add(SCREEN.TABLE, SCREEN.SCREEN_ID, screenId, QueryConstants.EQUAL, Operator.AND)
				.add(CRUDUtil.getNotDeletedCriteria(SCREEN.TABLE), Operator.AND).build();

		DataObject dob = CRUDOperation.SelectAllColumns(SCREEN.TABLE, criteria, null);
		if (CRUDUtil.DOBValidator(dob)) {
			return getScreenObj(dob);
		}

		return null;
	}

	public List<Pricing> findAllPricing(long screen_id) {

		List<Join> joinList = new ArrayList<>();
		joinList.add(ScreenPricingToSeatType());

		Criteria criteria = new CriteriaBuilder()
				.add(SCREEN_PRICING.TABLE, SCREEN_PRICING.SCREEN_ID, screen_id, QueryConstants.EQUAL, Operator.AND)
				.build();

		DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(SCREEN_PRICING.TABLE, criteria, joinList, null);
		List<Pricing> list = new ArrayList<>();

		if (CRUDUtil.DOBValidator(dob)) {

			Iterator<Row> rows;
			try {
				rows = dob.getRows(SCREEN_PRICING.TABLE);

				while (rows.hasNext()) {
					Row pricingRow = rows.next();
					long seat_type_id = CRUDUtil.parseLongFromRow(pricingRow, SCREEN_PRICING.SEAT_TYPE_ID);
					double seat_price = (double) CRUDUtil.parseLongFromRow(pricingRow, SCREEN_PRICING.SEAT_TYPE_PRICE);
					Row seatTypeRow = dob.getRow(SEAT_TYPE.TABLE,
							new Criteria(Column.getColumn(SEAT_TYPE.TABLE, SEAT_TYPE.SEAT_TYPE_ID), seat_type_id,
									QueryConstants.EQUAL));
					String seat_type = seatTypeRow != null ? (String) seatTypeRow.get(SEAT_TYPE.SEAT_TYPE) : null;
					list.add(new Pricing(seat_type_id, seat_type, seat_price));
				}
			} catch (DataAccessException e) {
				throw new RuntimeException(e);
			}
		}

		return list;

	}

	public List<Screen> findAllScreens(long theatreKey) {

		Criteria criteria = new CriteriaBuilder()
				.add(SCREEN.TABLE, SCREEN.THEATRE_ID, theatreKey, QueryConstants.EQUAL, Operator.AND)
				.add(CRUDUtil.getNotDeletedCriteria(SCREEN.TABLE), Operator.AND).build();

		DataObject dob = CRUDOperation.SelectAllColumns(SCREEN.TABLE, criteria, null);
		if (CRUDUtil.DOBValidator(dob)) {
			return getScreenListObj(dob);
		}
		return new ArrayList<>();
	}

	private List<Screen> getScreenListObj(DataObject dob) {
		List<Screen> screenList = new ArrayList<>();
		try {
			Iterator<Row> rows = dob.getRows(SCREEN.TABLE);
			while (rows.hasNext()) {
				Row row = rows.next();
				long screen_id = CRUDUtil.parseLongFromRow(row, SCREEN.SCREEN_ID);
				long theatre_id = CRUDUtil.parseLongFromRow(row, SCREEN.THEATRE_ID);
				String screen_type = findScreenType(CRUDUtil.parseLongFromRow(row, SCREEN.SCREEN_TYPE_ID));
				int seat_capacity = (int) CRUDUtil.parseLongFromRow(row, SCREEN.SEAT_CAPACITY);
				String screen_name = CRUDUtil.parseStringFromRow(row, SCREEN.SCREEN_NAME);

				if (screen_type != null) {
					List<seat_Structure> seatList = findAllSeats(screen_id);
					List<LocalTime> timeList = findAllTimes(screen_id);

					int screenNum = findScreenNumber(theatre_id, screen_id);
					if (seatList != null && timeList != null && screenNum != -1) {
						Screen screenObj = new Screen(screen_id, theatre_id, screen_type, seat_capacity, seatList,
								timeList, screenNum, screen_name);
						screenList.add(screenObj);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return screenList;
	}

	private List<LocalTime> findAllTimes(long screen_id) {

		Join mappingToTime = new Join(SHOW_TIME_MAPPING.TABLE, SHOW_TIME.TABLE,
				new String[] { SHOW_TIME_MAPPING.TIME_ID }, new String[] { SHOW_TIME.TIME_ID }, Join.INNER_JOIN);

		Criteria criteria = new CriteriaBuilder().add(SHOW_TIME_MAPPING.TABLE, SHOW_TIME_MAPPING.SCREEN_ID, screen_id,
				QueryConstants.EQUAL, Operator.AND).build();

		DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(SHOW_TIME_MAPPING.TABLE, criteria,
				Arrays.asList(mappingToTime), null);

		return getTimeList(dob);
	}

	private List<seat_Structure> findAllSeats(long screen_id) {

		List<Join> joinList = new ArrayList<>();

		joinList.add(CRUDUtil.buildJoin(SEAT_POSITION_MAPPING.TABLE, SEAT_POSITION.TABLE,
				new String[] { SEAT_POSITION_MAPPING.POSITION_ID }, new String[] { SEAT_POSITION.POSITION_ID },
				Join.INNER_JOIN));

		joinList.add(CRUDUtil.buildJoin(SEAT_POSITION_MAPPING.TABLE, SCREEN_PRICING.TABLE,
				new String[] { SEAT_POSITION_MAPPING.SEAT_TYPE_ID, SEAT_POSITION_MAPPING.SCREEN_ID },
				new String[] { SCREEN_PRICING.SEAT_TYPE_ID, SCREEN_PRICING.SCREEN_ID }, Join.INNER_JOIN));

		joinList.add(CRUDUtil.buildJoin(SEAT_POSITION_MAPPING.TABLE, SEAT_TYPE.TABLE,
				new String[] { SEAT_POSITION_MAPPING.SEAT_TYPE_ID }, new String[] { SEAT_TYPE.SEAT_TYPE_ID },
				Join.INNER_JOIN));

		Criteria criteria = new CriteriaBuilder().add(SEAT_POSITION_MAPPING.TABLE, SEAT_POSITION_MAPPING.SCREEN_ID,
				screen_id, QueryConstants.EQUAL, Operator.AND).build();

		DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(SEAT_POSITION_MAPPING.TABLE, criteria, joinList, null);

		List<seat_Structure> list = new ArrayList<>();

		if (!CRUDUtil.DOBValidator(dob)) {
			return list;
		}

		try {
			Iterator<Row> rows = dob.getRows(SEAT_POSITION_MAPPING.TABLE);

			while (rows.hasNext()) {

				Row spm = rows.next();
				long seat_id = CRUDUtil.parseLongFromRow(spm, SEAT_POSITION_MAPPING.SEAT_ID);
				long positionId = CRUDUtil.parseLongFromRow(spm, SEAT_POSITION_MAPPING.POSITION_ID);
				long seatTypeId = CRUDUtil.parseLongFromRow(spm, SEAT_POSITION_MAPPING.SEAT_TYPE_ID);
				boolean disabled = (Boolean) spm.get(SEAT_POSITION_MAPPING.IS_DISABLED);

				Row posRow = dob.getRow(SEAT_POSITION.TABLE,
						new Criteria(Column.getColumn(SEAT_POSITION.TABLE, SEAT_POSITION.POSITION_ID), positionId,
								QueryConstants.EQUAL));

				int row_no = (int) CRUDUtil.parseLongFromRow(posRow, SEAT_POSITION.ROW_NO);
				int col_no = (int) CRUDUtil.parseLongFromRow(posRow, SEAT_POSITION.COLUMN_NO);

				Row seatTypeRow = dob.getRow(SEAT_TYPE.TABLE, new Criteria(
						Column.getColumn(SEAT_TYPE.TABLE, SEAT_TYPE.SEAT_TYPE_ID), seatTypeId, QueryConstants.EQUAL));

				String seat_type = CRUDUtil.parseStringFromRow(seatTypeRow, SEAT_TYPE.SEAT_TYPE);

				Criteria pricingCriteria = new Criteria(
						Column.getColumn(SCREEN_PRICING.TABLE, SCREEN_PRICING.SEAT_TYPE_ID), seatTypeId,
						QueryConstants.EQUAL)
						.and(new Criteria(Column.getColumn(SCREEN_PRICING.TABLE, SCREEN_PRICING.SCREEN_ID), screen_id,
								QueryConstants.EQUAL));

				Row pricingRow = dob.getRow(SCREEN_PRICING.TABLE, pricingCriteria);

				double price = CRUDUtil.parseDoubleFromRow(pricingRow, SCREEN_PRICING.SEAT_TYPE_PRICE);

//				System.out.println("seatTypeId: " + seatTypeId);
//				System.out.println("seatTypeRow: " + seatTypeRow);
				seat_Structure seatObj = new seat_Structure(seat_id, row_no, col_no);

				seatObj.setSeatType(seat_type);
				seatObj.setSeat_price(price);
				seatObj.setDisabled(disabled);

				list.add(seatObj);
			}

		} catch (Exception e) {
			throw new RuntimeException("Error mapping seats", e);
		}

		return list;
	}

	public String findScreenType(long screen_type_id) {

		Criteria criteria = new CriteriaBuilder()
				.add(SCREEN_TYPE.TABLE, SCREEN_TYPE.SCREEN_TYPE_ID, screen_type_id, QueryConstants.EQUAL, Operator.AND)
				.build();

		DataObject dob = CRUDOperation.SelectAllColumns(SCREEN_TYPE.TABLE, criteria, null);

		if (CRUDUtil.DOBValidator(dob)) {
			Row row = CRUDUtil.getFirstRow(dob, SCREEN_TYPE.TABLE);
			return CRUDUtil.parseStringFromRow(row, SCREEN_TYPE.SCREEN_TYPE);
		}

		return null;
	}

	public boolean deleteScreen(long screenId) {

		Criteria criteria = new CriteriaBuilder()
				.add(SCREEN.TABLE, SCREEN.SCREEN_ID, screenId, QueryConstants.EQUAL, Operator.AND).build();

		DataObject dob = CRUDOperation.updateSingleColumn(SCREEN.TABLE, criteria, SCREEN.IS_DELETED, true);

		if (CRUDUtil.DOBValidator(dob)) {
			return true;
		}

		throw new ApplicationException("Screen not deleted");
	}

	public boolean updateScreenType(long theatreId, long screenId, String screenType) {

		long screen_type_id = insertScreenType(screenType);

		Criteria criteria = new CriteriaBuilder()
				.add(SCREEN.TABLE, SCREEN.SCREEN_ID, screenId, QueryConstants.EQUAL, Operator.AND).build();

		DataObject dob = CRUDOperation.updateSingleColumn(SCREEN.TABLE, criteria, SCREEN.SCREEN_TYPE_ID,
				screen_type_id);

		return CRUDUtil.DOBValidator(dob);
	}

	public boolean deleteAllScreensByTheatre(long theatreId) {

		Criteria criteria = new CriteriaBuilder()
				.add(SCREEN.TABLE, SCREEN.THEATRE_ID, theatreId, QueryConstants.EQUAL, Operator.AND).build();
		DataObject dob = CRUDOperation.updateSingleColumn(SCREEN.TABLE, criteria, SCREEN.IS_DELETED, true);
		return CRUDUtil.DOBValidator(dob);
	}

	public void updateScreenData(long screenId, List<seat_Structure> seatList, String screenType,
			List<LocalTime> showTime, List<Pricing> pricingList, String screenName) {

		try {
			if (findScreen(screenId) == null) {
				throw new ApplicationException("Screen Not found");
			}

			Criteria screenCriteria = new CriteriaBuilder()
					.add(SCREEN.TABLE, SCREEN.SCREEN_ID, screenId, QueryConstants.EQUAL, Operator.AND).build();

			DataObject dob = CRUDOperation.SelectAllColumns(SCREEN.TABLE, screenCriteria, null);

			if (!CRUDUtil.DOBValidator(dob)) {
				throw new ApplicationException("Screen not found");
			}

			Row screenRow = dob.getFirstRow(SCREEN.TABLE);

			long typeId = insertScreenType(screenType);

			screenRow.set(SCREEN.SCREEN_TYPE_ID, typeId);
			screenRow.set(SCREEN.SEAT_CAPACITY, seatList.size());
			screenRow.set(SCREEN.SCREEN_NAME, screenName);

			dob.updateRow(screenRow);
			CRUDOperation.updateDataObject(dob);
			deleteMappings(screenId);

			Map<String, Long> seatTypeMap = insertSeatTypes(screenId, pricingList);
			insertSeatStructure(screenId, seatList, seatTypeMap);
			insertTiming(screenId, showTime);

		} catch (Exception e) {
			throw new RuntimeException("Error updating screen data", e);
		}
	}

	private void deleteMappings(long screenId) {

		try {

			Criteria spmCriteria = new Criteria(
					Column.getColumn(SEAT_POSITION_MAPPING.TABLE, SEAT_POSITION_MAPPING.SCREEN_ID), screenId,
					QueryConstants.EQUAL);

			Criteria pricingCriteria = new Criteria(Column.getColumn(SCREEN_PRICING.TABLE, SCREEN_PRICING.SCREEN_ID),
					screenId, QueryConstants.EQUAL);

			Criteria timeCriteria = new Criteria(Column.getColumn(SHOW_TIME_MAPPING.TABLE, SHOW_TIME_MAPPING.SCREEN_ID),
					screenId, QueryConstants.EQUAL);
			CRUDOperation.deleteRows(SEAT_POSITION_MAPPING.TABLE, spmCriteria);
			CRUDOperation.deleteRows(SCREEN_PRICING.TABLE, pricingCriteria);
			CRUDOperation.deleteRows(SHOW_TIME_MAPPING.TABLE, timeCriteria);

		} catch (Exception e) {
			throw new RuntimeException("Error deleting mappings", e);
		}
	}

	private Map<LocalTime, Long> getShowTimeMapping(DataObject dob) {
		Map<LocalTime, Long> timeList = new HashMap<>();
		if (dob == null || dob.isEmpty()) {
			return timeList;
		}
		try {
			Iterator<Row> rows = dob.getRows(SHOW_TIME.TABLE);
			while (rows.hasNext()) {
				Row row = rows.next();
				long time_id = CRUDUtil.parseLongFromRow(row, SHOW_TIME.TIME_ID);
				Time time = (Time) row.get(SHOW_TIME.SHOW_TIME);
				LocalTime localTime = time.toLocalTime();
				timeList.put(localTime, time_id);
			}
		} catch (DataAccessException e) {
			throw new RuntimeException(e);
		}
		return timeList;
	}

	private Join ShowToTMDJoin() {
		return CRUDUtil.buildJoin(SHOWS.TABLE, THEATRE_MOVIE_DETAILS.TABLE,
				new String[] { SHOWS.THEATRE_MOVIE_DETAIL_ID },
				new String[] { THEATRE_MOVIE_DETAILS.THEATRE_MOVIE_DETAIL_ID }, Join.INNER_JOIN);
	}

	private Join TMDToScreen() {
		return CRUDUtil.buildJoin(THEATRE_MOVIE_DETAILS.TABLE, SCREEN.TABLE,
				new String[] { THEATRE_MOVIE_DETAILS.SCREEN_ID }, new String[] { SCREEN.SCREEN_ID }, Join.INNER_JOIN);
	}

	private Join ScreenPricingToSeatType() {
		return CRUDUtil.buildJoin(SCREEN_PRICING.TABLE, SEAT_TYPE.TABLE, new String[] { SCREEN_PRICING.SEAT_TYPE_ID },
				new String[] { SEAT_TYPE.SEAT_TYPE_ID }, Join.INNER_JOIN);
	}

	private Screen getScreenObj(DataObject dob) {

		try {
			Iterator<Row> rows = dob.getRows(SCREEN.TABLE);
			while (rows.hasNext()) {
				Row row = rows.next();
				long screen_id = CRUDUtil.parseLongFromRow(row, SCREEN.SCREEN_ID);
				long theatre_id = CRUDUtil.parseLongFromRow(row, SCREEN.THEATRE_ID);
				String screen_type = findScreenType(CRUDUtil.parseLongFromRow(row, SCREEN.SCREEN_TYPE_ID));
				int seat_capacity = (int) CRUDUtil.parseLongFromRow(row, SCREEN.SEAT_CAPACITY);
				String screen_name = CRUDUtil.parseStringFromRow(row, SCREEN.SCREEN_NAME);

				if (screen_type != null) {
					List<seat_Structure> seatList = findAllSeats(screen_id);
					List<LocalTime> timeList = findAllTimes(screen_id);
					List<Pricing> priceList = findAllPricing(screen_id);
					int screenNum = findScreenNumber(theatre_id, screen_id);
					if (seatList != null && timeList != null && screenNum != -1) {
						Screen screenObj = new Screen(screen_id, theatre_id, screen_type, seat_capacity, seatList,
								timeList, screenNum, screen_name);
						return screenObj;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private List<LocalTime> getTimeList(DataObject dob) {

		List<LocalTime> list = new ArrayList<>();

		if (CRUDUtil.DOBValidator(dob)) {
			Iterator<Row> rows;
			try {
				rows = dob.getRows(SHOW_TIME_MAPPING.TABLE);
				while (rows.hasNext()) {

					Row mappingRow = rows.next();
					long timeId = ((Number) mappingRow.get(SHOW_TIME_MAPPING.TIME_ID)).longValue();
					Row timeRow = dob.getRow(SHOW_TIME.TABLE, new Criteria(
							Column.getColumn(SHOW_TIME.TABLE, SHOW_TIME.TIME_ID), timeId, QueryConstants.EQUAL));
					if (timeRow != null) {
						java.sql.Time sqlTime = (java.sql.Time) timeRow.get(SHOW_TIME.SHOW_TIME);
						list.add(sqlTime.toLocalTime());
					}
				}
			} catch (DataAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return list;
	}

}
