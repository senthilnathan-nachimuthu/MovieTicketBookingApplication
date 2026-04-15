package DAO;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.movieticketbooking.*;

import DBConnectivity.connectDB;
import Exceptions.ApplicationException;
import MickeyDBAccess.CRUDOperation;
import MickeyDBAccess.CRUDUtil;
import MickeyDBAccess.CriteriaBuilder;
import MickeyDBAccess.Operator;
import Schemas.Pricing;
import Schemas.Show;
import userInteraction.seat_Structure;

public class showDAO {

	connectDB connect = new connectDB();

	public void insertShows(List<Object[]> objectParams, List<seat_Structure> ls, List<Pricing> pricingList) {

		List<Row> insertRowList = constructShowRows(objectParams);

		DataObject dob = CRUDOperation.insertMultipleRows(SHOWS.TABLE, insertRowList);

		List<Row> seatObjectList = new ArrayList<>();
		List<Row> PricingParamList = new ArrayList<>();
		Iterator<Row> rows;
		try {
			rows = dob.getRows(SHOWS.TABLE);
			while (rows.hasNext()) {
				Row row = rows.next();
				long showId = CRUDUtil.parseLongFromRow(row, SHOWS.SHOW_ID);

				for (seat_Structure s : ls) {
					Row seatRow = new Row(SEAT_BOOKING_STATUS.TABLE);
					seatRow.set(SEAT_BOOKING_STATUS.SHOW_ID, showId);
					seatRow.set(SEAT_BOOKING_STATUS.SEAT_ID, s.getSeatId());
					seatObjectList.add(seatRow);
				}
				for (Pricing p : pricingList) {
					Row pricingRow = new Row(SHOW_PRICING.TABLE);
					pricingRow.set(SHOW_PRICING.SHOW_ID, showId);
					pricingRow.set(SHOW_PRICING.SEAT_TYPE_ID, p.getSeatTypeId());
					pricingRow.set(SHOW_PRICING.SEAT_TYPE_PRICE, p.getPrice());
					PricingParamList.add(pricingRow);
				}
			}
			CRUDOperation.insertMultipleRows(SEAT_BOOKING_STATUS.TABLE, seatObjectList);
			CRUDOperation.insertMultipleRows(SHOW_PRICING.TABLE, PricingParamList);
		} catch (DataAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private List<Row> constructShowRows(List<Object[]> objectParams) {

		List<Row> rowList = new ArrayList<>();
		for (Object obj[] : objectParams) {
			Row row = new Row(SHOWS.TABLE);
			row.set(SHOWS.THEATRE_MOVIE_DETAIL_ID, obj[0]);
			row.set(SHOWS.DATE_ID, obj[1]);
			row.set(SHOWS.SHOW_TIME_ID, obj[2]);
			rowList.add(row);
		}

		return rowList;
	}

	public List<Show> getAllShows() {

		List<Join> joinList = buildShowJoins();
		Criteria criteria = CRUDUtil.getNotDeletedCriteria(SHOWS.TABLE);
		SortColumn[] sortColumns = new SortColumn[] { new SortColumn(SHOW_DATE.TABLE, SHOW_DATE.DATE, true) };
		DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(SHOWS.TABLE, criteria, joinList, sortColumns);
		if (CRUDUtil.DOBValidator(dob)) {
			return getShowObjectList(dob);
		}
		return new ArrayList<>();
	}

	public Map<LocalTime, Long> findTimeId(long screenId) {

		List<Join> joinList = Arrays.asList(CRUDUtil.buildJoin(SHOW_TIME_MAPPING.TABLE, SHOW_TIME.TABLE,
				new String[] { SHOW_TIME_MAPPING.TIME_ID }, new String[] { SHOW_TIME.TIME_ID }, Join.INNER_JOIN));
		Criteria criteria = new CriteriaBuilder()
				.add(SHOW_TIME_MAPPING.TABLE, SHOW_TIME_MAPPING.SCREEN_ID, screenId, QueryConstants.EQUAL, Operator.AND)
				.build();
		DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(SHOW_TIME_MAPPING.TABLE, criteria, joinList, null);
		Map<LocalTime, Long> m = new HashMap<>();
		if (CRUDUtil.DOBValidator(dob)) {
			try {
				Iterator<Row> rows = dob.getRows(SHOW_TIME_MAPPING.TABLE);

				while (rows.hasNext()) {

					Row stmRow = rows.next();

					long show_time_id = CRUDUtil.parseLongFromRow(stmRow, SHOW_TIME_MAPPING.SHOW_TIME_ID);
					long time_id = CRUDUtil.parseLongFromRow(stmRow, SHOW_TIME_MAPPING.TIME_ID);
					Row timeRow = dob.getRow(SHOW_TIME.TABLE, new Criteria(
							Column.getColumn(SHOW_TIME.TABLE, SHOW_TIME.TIME_ID), time_id, QueryConstants.EQUAL));

					Time sqlTime = (Time) timeRow.get(SHOW_TIME.SHOW_TIME);
					LocalTime lt = sqlTime.toLocalTime();
					m.put(lt, show_time_id);
				}

			} catch (Exception e) {
				throw new RuntimeException("Error mapping show time", e);
			}
		}
		return m;
	}

	public long insertTheatreMovieDetails(long theatreId, long screenId, long language_mapping_id) {
		try {

			Criteria criteria = new CriteriaBuilder()
					.add(THEATRE_MOVIE_DETAILS.TABLE, THEATRE_MOVIE_DETAILS.THEATRE_ID, theatreId, QueryConstants.EQUAL,
							Operator.AND)
					.add(THEATRE_MOVIE_DETAILS.TABLE, THEATRE_MOVIE_DETAILS.SCREEN_ID, screenId, QueryConstants.EQUAL,
							Operator.AND)
					.add(THEATRE_MOVIE_DETAILS.TABLE, THEATRE_MOVIE_DETAILS.LANGUAGE_MAPPING_ID, language_mapping_id,
							QueryConstants.EQUAL, Operator.AND)
					.build();

			DataObject dob = CRUDOperation.SelectAllColumns(THEATRE_MOVIE_DETAILS.TABLE, criteria, null);

			if (CRUDUtil.DOBValidator(dob)) {
				Row row = CRUDUtil.getFirstRow(dob, THEATRE_MOVIE_DETAILS.TABLE);
				return CRUDUtil.parseLongFromRow(row, THEATRE_MOVIE_DETAILS.THEATRE_MOVIE_DETAIL_ID);
			}

			Row row = new Row(THEATRE_MOVIE_DETAILS.TABLE);
			row.set(THEATRE_MOVIE_DETAILS.THEATRE_ID, theatreId);
			row.set(THEATRE_MOVIE_DETAILS.SCREEN_ID, screenId);
			row.set(THEATRE_MOVIE_DETAILS.LANGUAGE_MAPPING_ID, language_mapping_id);

			DataObject insertDob = CRUDOperation.insertOneRow(THEATRE_MOVIE_DETAILS.TABLE, row);

			if (CRUDUtil.DOBValidator(insertDob)) {
				Row insertedRow = CRUDUtil.getFirstRow(insertDob, THEATRE_MOVIE_DETAILS.TABLE);
				return CRUDUtil.parseLongFromRow(insertedRow, THEATRE_MOVIE_DETAILS.THEATRE_MOVIE_DETAIL_ID);
			}

		} catch (Exception e) {
			throw new RuntimeException("Error in getOrCreateTMD", e);
		}

		return -1;
	}

	public Map<LocalDate, Long> findDates() {

		Map<LocalDate, Long> m = new HashMap<>();
		DataObject dob = CRUDOperation.SelectAllColumns(SHOW_DATE.TABLE, null, null);
		if (!CRUDUtil.DOBValidator(dob)) {
			return m;
		}
		try {
			Iterator<Row> rows = dob.getRows(SHOW_DATE.TABLE);
			while (rows.hasNext()) {
				Row row = rows.next();
				long date_id = CRUDUtil.parseLongFromRow(row, SHOW_DATE.DATE_ID);
				Date sqlDate = (Date) row.get(SHOW_DATE.DATE);
				LocalDate date = sqlDate.toLocalDate();
				m.put(date, date_id);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error fetching show dates", e);
		}
		return m;
	}

	public Map<LocalDate, Long> insertDate(LocalDate fromDate, LocalDate toDate) {

		try {
			Map<LocalDate, Long> existingDates = findDates();
			List<Row> rowsToInsert = new ArrayList<>();
			for (LocalDate i = fromDate; !i.isAfter(toDate); i = i.plusDays(1)) {

				if (!existingDates.containsKey(i)) {
					Row row = new Row(SHOW_DATE.TABLE);
					row.set(SHOW_DATE.DATE, Date.valueOf(i));
					rowsToInsert.add(row);
				}
			}
			if (!rowsToInsert.isEmpty()) {
				CRUDOperation.insertMultipleRows(SHOW_DATE.TABLE, rowsToInsert);
			}
			return findDates();

		} catch (Exception e) {
			throw new RuntimeException("Error inserting dates", e);
		}
	}

	public boolean insertShowPricing(List<Pricing> pricingList, long show_id) {

		try {
			Criteria criteria = new CriteriaBuilder()
					.add(SHOW_PRICING.TABLE, SHOW_PRICING.SHOW_ID, show_id, QueryConstants.EQUAL, Operator.AND).build();

			DataObject dob = CRUDOperation.SelectAllColumns(SHOW_PRICING.TABLE, criteria, null);

			Map<Long, Row> existingMap = new HashMap<>();

			if (CRUDUtil.DOBValidator(dob)) {
				Iterator<Row> rows = dob.getRows(SHOW_PRICING.TABLE);
				while (rows.hasNext()) {
					Row row = rows.next();
					long seatTypeId = CRUDUtil.parseLongFromRow(row, SHOW_PRICING.SEAT_TYPE_ID);
					existingMap.put(seatTypeId, row);
				}
			}
			List<Row> insertList = new ArrayList<>();

			for (Pricing p : pricingList) {

				long seatTypeId = p.getSeatTypeId();
				double price = p.getPrice();

				if (existingMap.containsKey(seatTypeId)) {

					Row row = existingMap.get(seatTypeId);
					row.set(SHOW_PRICING.SEAT_TYPE_PRICE, price);
					dob.updateRow(row);

				} else {
					Row row = new Row(SHOW_PRICING.TABLE);
					row.set(SHOW_PRICING.SHOW_ID, show_id);
					row.set(SHOW_PRICING.SEAT_TYPE_ID, seatTypeId);
					row.set(SHOW_PRICING.SEAT_TYPE_PRICE, price);
					insertList.add(row);
				}
			}

			if (CRUDUtil.DOBValidator(dob)) {
				CRUDOperation.updateDataObject(dob);
			}
			if (!insertList.isEmpty()) {
				CRUDOperation.insertMultipleRows(SHOW_PRICING.TABLE, insertList);
			}
			return true;

		} catch (Exception e) {
			throw new RuntimeException("Error inserting show pricing", e);
		}
	}

	public Show findShowById(long show_id) {

		List<Join> joinList = buildShowJoins();
		Criteria criteria = new CriteriaBuilder()
				.add(SHOWS.TABLE, SHOWS.SHOW_ID, show_id, QueryConstants.EQUAL, Operator.AND)
				.add(CRUDUtil.getNotDeletedCriteria(SHOWS.TABLE), Operator.AND).build();
		DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(SHOWS.TABLE, criteria, joinList, null);
		if (!CRUDUtil.DOBValidator(dob)) {
			return null;
		}
		List<Show> list = getShowObjectList(dob);
		return list.isEmpty() ? null : list.get(0);
	}

	public List<Show> findShowsByTheatreandScreen(long theatreId, long screenId) {

		List<Join> joinList = buildShowJoins();
		Criteria criteria = new CriteriaBuilder().add(CRUDUtil.getNotDeletedCriteria(SHOWS.TABLE), Operator.AND)
				.add(THEATRE_MOVIE_DETAILS.TABLE, THEATRE_MOVIE_DETAILS.THEATRE_ID, theatreId, QueryConstants.EQUAL,
						Operator.AND)
				.add(THEATRE_MOVIE_DETAILS.TABLE, THEATRE_MOVIE_DETAILS.SCREEN_ID, screenId, QueryConstants.EQUAL,
						Operator.AND)
				.build();
		DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(SHOWS.TABLE, criteria, joinList, null);

		if (!CRUDUtil.DOBValidator(dob)) {
			return new ArrayList<>();
		}
		return getShowObjectList(dob);
	}

	public List<Show> findShowsByTheatre(long theatreId) {
		List<Join> joinList = buildShowJoins();
		Criteria criteria = new CriteriaBuilder().add(CRUDUtil.getNotDeletedCriteria(SHOWS.TABLE), Operator.AND)
				.add(THEATRE_MOVIE_DETAILS.TABLE, THEATRE_MOVIE_DETAILS.THEATRE_ID, theatreId, QueryConstants.EQUAL,
						Operator.AND)
				.build();
		SortColumn[] sortColumns = new SortColumn[] { new SortColumn(SHOW_DATE.TABLE, SHOW_DATE.DATE, true) };
		DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(SHOWS.TABLE, criteria, joinList, sortColumns);
		if (!CRUDUtil.DOBValidator(dob)) {
			return new ArrayList<>();
		}
		return getShowObjectList(dob);
	}

	public int getAvailableSeats(long show_id) {

		try {
			Criteria criteria = new CriteriaBuilder()
					.add(SEAT_BOOKING_STATUS.TABLE, SEAT_BOOKING_STATUS.SHOW_ID, show_id, QueryConstants.EQUAL,
							Operator.AND)
					.add(SEAT_BOOKING_STATUS.TABLE, SEAT_BOOKING_STATUS.BOOKING_ID, -1, QueryConstants.EQUAL,
							Operator.AND)
					.build();

			DataObject dob = CRUDOperation.SelectAllColumns(SEAT_BOOKING_STATUS.TABLE, criteria, null);

			if (!CRUDUtil.DOBValidator(dob)) {
				return 0;
			}
			int count = 0;
			Iterator<Row> rows = dob.getRows(SEAT_BOOKING_STATUS.TABLE);
			while (rows.hasNext()) {
				rows.next();
				count++;
			}
			return count;
		} catch (Exception e) {
			throw new RuntimeException("Error fetching available seats", e);
		}
	}

	public Map<Long, Long> findSeatBookingStatus(long show_id) {

		Map<Long, Long> m = new HashMap<>();

		try {
			Criteria criteria = new CriteriaBuilder().add(SEAT_BOOKING_STATUS.TABLE, SEAT_BOOKING_STATUS.SHOW_ID,
					show_id, QueryConstants.EQUAL, Operator.AND).build();

			DataObject dob = CRUDOperation.SelectAllColumns(SEAT_BOOKING_STATUS.TABLE, criteria, null);

			if (!CRUDUtil.DOBValidator(dob)) {
				return m;
			}
			Iterator<Row> rows = dob.getRows(SEAT_BOOKING_STATUS.TABLE);
			while (rows.hasNext()) {
				Row row = rows.next();
				long seat_id = CRUDUtil.parseLongFromRow(row, SEAT_BOOKING_STATUS.SEAT_ID);
				long bookingId = CRUDUtil.parseLongFromRow(row, SEAT_BOOKING_STATUS.BOOKING_ID);
				m.put(seat_id, bookingId);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error fetching seat booking status", e);
		}

		return m;
	}

	public List<Pricing> findShowPricing(long show_id) {

		List<Pricing> list = new ArrayList<>();

		try {
			Join join = CRUDUtil.buildJoin(SHOW_PRICING.TABLE, SEAT_TYPE.TABLE,
					new String[] { SHOW_PRICING.SEAT_TYPE_ID }, new String[] { SEAT_TYPE.SEAT_TYPE_ID },
					Join.INNER_JOIN);

			Criteria criteria = new CriteriaBuilder()
					.add(SHOW_PRICING.TABLE, SHOW_PRICING.SHOW_ID, show_id, QueryConstants.EQUAL, Operator.AND).build();

			DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(SHOW_PRICING.TABLE, criteria, Arrays.asList(join),
					null);

			if (!CRUDUtil.DOBValidator(dob)) {
				return list;
			}

			Iterator<Row> rows = dob.getRows(SHOW_PRICING.TABLE);

			while (rows.hasNext()) {

				Row pricingRow = rows.next();
				long seatTypeId = CRUDUtil.parseLongFromRow(pricingRow, SHOW_PRICING.SEAT_TYPE_ID);
				double price = CRUDUtil.parseDoubleFromRow(pricingRow, SHOW_PRICING.SEAT_TYPE_PRICE);

				Row seatTypeRow = dob.getRow(SEAT_TYPE.TABLE, new Criteria(
						Column.getColumn(SEAT_TYPE.TABLE, SEAT_TYPE.SEAT_TYPE_ID), seatTypeId, QueryConstants.EQUAL));

				String seatType = seatTypeRow != null ? CRUDUtil.parseStringFromRow(seatTypeRow, SEAT_TYPE.SEAT_TYPE)
						: null;

				list.add(new Pricing(seatTypeId, seatType, price));
			}

		} catch (Exception e) {
			throw new RuntimeException("Error fetching show pricing", e);
		}

		return list;
	}

	public boolean deleteShows(List<Object[]> showToRemove) {

		try {

			if (showToRemove == null || showToRemove.isEmpty()) {
				return false;
			}
			List<Long> showIds = new ArrayList<>();

			for (Object[] obj : showToRemove) {
				if (obj != null && obj.length > 0) {
					showIds.add(((Number) obj[0]).longValue());
				}
			}
			if (showIds.isEmpty()) {
				return false;
			}
			Criteria criteria = new Criteria(Column.getColumn(SHOWS.TABLE, SHOWS.SHOW_ID), showIds.toArray(),
					QueryConstants.IN);

			DataObject dob = CRUDOperation.SelectAllColumns(SHOWS.TABLE, criteria, null);

			if (!CRUDUtil.DOBValidator(dob)) {
				return false;
			}
			Iterator<Row> rows = dob.getRows(SHOWS.TABLE);

			while (rows.hasNext()) {
				Row row = rows.next();
				row.set(SHOWS.IS_DELETED, true);
				dob.updateRow(row);
			}
			CRUDOperation.updateDataObject(dob);
			return true;

		} catch (Exception e) {
			throw new ApplicationException("Show deletion Failed.");
		}
	}

	public boolean updateShowMovie(long showId, long language_mapping_id) {

		try {

			Join join = CRUDUtil.buildJoin(SHOWS.TABLE, THEATRE_MOVIE_DETAILS.TABLE,
					new String[] { SHOWS.THEATRE_MOVIE_DETAIL_ID },
					new String[] { THEATRE_MOVIE_DETAILS.THEATRE_MOVIE_DETAIL_ID }, Join.INNER_JOIN);
			Criteria criteria = new CriteriaBuilder()
					.add(SHOWS.TABLE, SHOWS.SHOW_ID, showId, QueryConstants.EQUAL, Operator.AND).build();
			DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(SHOWS.TABLE, criteria, Arrays.asList(join), null);

			if (!CRUDUtil.DOBValidator(dob)) {
				return false;
			}
			Row showRow = dob.getFirstRow(SHOWS.TABLE);

			long tmdId = CRUDUtil.parseLongFromRow(showRow, SHOWS.THEATRE_MOVIE_DETAIL_ID);

			Row tmdRow = dob.getRow(THEATRE_MOVIE_DETAILS.TABLE, new Criteria(
					Column.getColumn(THEATRE_MOVIE_DETAILS.TABLE, THEATRE_MOVIE_DETAILS.THEATRE_MOVIE_DETAIL_ID), tmdId,
					QueryConstants.EQUAL));

			if (tmdRow == null) {
				throw new ApplicationException("Theatre movie details not found");
			}

			long theatreId = CRUDUtil.parseLongFromRow(tmdRow, THEATRE_MOVIE_DETAILS.THEATRE_ID);
			long screenId = CRUDUtil.parseLongFromRow(tmdRow, THEATRE_MOVIE_DETAILS.SCREEN_ID);
			long newTmdId = insertTheatreMovieDetails(theatreId, screenId, language_mapping_id);

			showRow.set(SHOWS.THEATRE_MOVIE_DETAIL_ID, newTmdId);
			dob.updateRow(showRow);

			CRUDOperation.updateDataObject(dob);

			return true;

		} catch (Exception e) {
			throw new RuntimeException("Error updating show movie", e);
		}
	}

	public boolean updateShowPrice(long show_id, List<Pricing> pricingList) {

		try {
			Criteria criteria = new CriteriaBuilder()
					.add(SHOW_PRICING.TABLE, SHOW_PRICING.SHOW_ID, show_id, QueryConstants.EQUAL, Operator.AND).build();
			DataObject dob = CRUDOperation.SelectAllColumns(SHOW_PRICING.TABLE, criteria, null);
			if (!CRUDUtil.DOBValidator(dob)) {
				return false;
			}
			Map<Long, Row> pricingMap = new HashMap<>();

			Iterator<Row> rows = dob.getRows(SHOW_PRICING.TABLE);
			while (rows.hasNext()) {
				Row row = rows.next();
				long seatTypeId = CRUDUtil.parseLongFromRow(row, SHOW_PRICING.SEAT_TYPE_ID);
				pricingMap.put(seatTypeId, row);
			}
			for (Pricing p : pricingList) {

				Row row = pricingMap.get(p.getSeatTypeId());

				if (row != null) {
					row.set(SHOW_PRICING.SEAT_TYPE_PRICE, p.getPrice());
					dob.updateRow(row);
				}
			}
			CRUDOperation.updateDataObject(dob);
			return true;
		} catch (Exception e) {
			throw new RuntimeException("Error updating show pricing", e);
		}
	}

	public List<Show> getShowsByMovie(long movieId) {

		List<Join> joinList = buildShowJoins();
		Criteria criteria = new CriteriaBuilder().add(CRUDUtil.getNotDeletedCriteria(SHOWS.TABLE), Operator.AND)
				.add(MOVIE_LANGUAGE_MAPPING.TABLE, MOVIE_LANGUAGE_MAPPING.MOVIE_ID, movieId, QueryConstants.EQUAL,
						Operator.AND)
				.build();
		DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(SHOWS.TABLE, criteria, joinList, null);

		if (!CRUDUtil.DOBValidator(dob)) {
			return new ArrayList<>();
		}

		return getShowObjectList(dob);
	}

	public List<Show> getShowsByMovieLanguage(long movieId, long language_mapping_id) {

		List<Join> joinList = buildShowJoins();
		Criteria criteria = new CriteriaBuilder()
				.add(THEATRE_MOVIE_DETAILS.TABLE, THEATRE_MOVIE_DETAILS.LANGUAGE_MAPPING_ID, language_mapping_id,
						QueryConstants.EQUAL, Operator.AND)
				.add(MOVIE_LANGUAGE_MAPPING.TABLE, MOVIE_LANGUAGE_MAPPING.MOVIE_ID, movieId, QueryConstants.EQUAL,
						Operator.AND)
				.add(CRUDUtil.getNotDeletedCriteria(SHOWS.TABLE), Operator.AND).build();

		SortColumn[] sort = new SortColumn[] { new SortColumn(SHOW_DATE.TABLE, SHOW_DATE.DATE, true) };

		DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(SHOWS.TABLE, criteria, joinList, sort);

		if (!CRUDUtil.DOBValidator(dob)) {
			return new ArrayList<>();
		}

		return getShowObjectList(dob);
	}

	public boolean bookSeats(long showId, long booking_id, List<Long> seats) {

		try {

			if (seats == null || seats.isEmpty()) {
				return false;
			}
			Criteria criteria = new Criteria(Column.getColumn(SEAT_BOOKING_STATUS.TABLE, SEAT_BOOKING_STATUS.SEAT_ID),
					seats.toArray(), QueryConstants.IN)
					.and(new Criteria(Column.getColumn(SEAT_BOOKING_STATUS.TABLE, SEAT_BOOKING_STATUS.SHOW_ID), showId,
							QueryConstants.EQUAL));
			DataObject dob = CRUDOperation.SelectAllColumns(SEAT_BOOKING_STATUS.TABLE, criteria, null);

			if (!CRUDUtil.DOBValidator(dob)) {
				return false;
			}
			Iterator<Row> rows = dob.getRows(SEAT_BOOKING_STATUS.TABLE);

			while (rows.hasNext()) {
				Row row = rows.next();
				row.set(SEAT_BOOKING_STATUS.BOOKING_ID, booking_id);
				dob.updateRow(row);
			}
			CRUDOperation.updateDataObject(dob);
			return true;

		} catch (Exception e) {
			throw new RuntimeException("Error booking seats", e);
		}
	}

	public List<Long> findSeatsByBookingId(long booking_id, long show_id) {

		List<Long> list = new ArrayList<>();

		System.out.println("bokingId " + booking_id + " show_id " + show_id);
		try {
			Criteria criteria = new CriteriaBuilder()
					.add(SEAT_BOOKING_STATUS.TABLE, SEAT_BOOKING_STATUS.BOOKING_ID, booking_id, QueryConstants.EQUAL,
							Operator.AND)
					.add(SEAT_BOOKING_STATUS.TABLE, SEAT_BOOKING_STATUS.SHOW_ID, show_id, QueryConstants.EQUAL,
							Operator.AND)
					.build();
			DataObject dob = CRUDOperation.SelectAllColumns(SEAT_BOOKING_STATUS.TABLE, criteria, null);
			if (!CRUDUtil.DOBValidator(dob)) {
				return list;
			}
			System.out.println("DOB Fetched for booking Id" + dob);
			Iterator<Row> rows = dob.getRows(SEAT_BOOKING_STATUS.TABLE);

			while (rows.hasNext()) {
				Row row = rows.next();
				long seatId = CRUDUtil.parseLongFromRow(row, SEAT_BOOKING_STATUS.SEAT_ID);
				list.add(seatId);
			}

		} catch (Exception e) {
			throw new RuntimeException("Error fetching seats by booking", e);
		}

		return list;
	}

	public boolean cancelBookingsByShows(List<Object[]> showList) {

		try {

			if (showList == null || showList.isEmpty()) {
				return false;
			}
			List<Criteria> criteriaList = new ArrayList<>();

			for (Object[] obj : showList) {
				long showId = ((Number) obj[0]).longValue();
				Criteria c = new Criteria(Column.getColumn(SEAT_BOOKING_STATUS.TABLE, SEAT_BOOKING_STATUS.SHOW_ID),
						showId, QueryConstants.EQUAL);

				criteriaList.add(c);
			}
			Criteria finalCriteria = null;
			for (Criteria c : criteriaList) {
				finalCriteria = (finalCriteria == null) ? c : finalCriteria.or(c);
			}
			if (finalCriteria == null) {
				return false;
			}

			DataObject dob = CRUDOperation.SelectAllColumns(SEAT_BOOKING_STATUS.TABLE, finalCriteria, null);
			if (!CRUDUtil.DOBValidator(dob)) {
				return false;
			}

			Iterator<Row> rows = dob.getRows(SEAT_BOOKING_STATUS.TABLE);

			while (rows.hasNext()) {
				Row row = rows.next();
				row.set(SEAT_BOOKING_STATUS.BOOKING_ID, -1);
				dob.updateRow(row);
			}
			CRUDOperation.updateDataObject(dob);
			return true;

		} catch (Exception e) {
			throw new RuntimeException("Error cancelling bookings by shows", e);
		}
	}

	public boolean updateBookedSeats(List<Object[]> seats) {

		try {

			if (seats == null || seats.isEmpty()) {
				return false;
			}
			List<Criteria> criteriaList = new ArrayList<>();

			for (Object[] obj : seats) {
				long seatId = ((Number) obj[0]).longValue();
				long showId = ((Number) obj[1]).longValue();

				Criteria c = new Criteria(Column.getColumn(SEAT_BOOKING_STATUS.TABLE, SEAT_BOOKING_STATUS.SEAT_ID),
						seatId, QueryConstants.EQUAL)
						.and(new Criteria(Column.getColumn(SEAT_BOOKING_STATUS.TABLE, SEAT_BOOKING_STATUS.SHOW_ID),
								showId, QueryConstants.EQUAL));
				criteriaList.add(c);
			}
			Criteria finalCriteria = null;
			for (Criteria c : criteriaList) {
				finalCriteria = (finalCriteria == null) ? c : finalCriteria.or(c);
			}
			DataObject dob = CRUDOperation.SelectAllColumns(SEAT_BOOKING_STATUS.TABLE, finalCriteria, null);

			System.out.println("DOB fetched for Cancellation" + dob);
			Iterator<Row> rows = dob.getRows(SEAT_BOOKING_STATUS.TABLE);

			while (rows.hasNext()) {
				Row row = rows.next();
				row.set(SEAT_BOOKING_STATUS.BOOKING_ID, -1);
				dob.updateRow(row);
			}

			CRUDOperation.updateDataObject(dob);
			return true;
		} catch (Exception e) {
			throw new RuntimeException("Error updating booked seats", e);
		}
	}

	public List<Show> findSimilarShows(long showId) {

		try {
			Criteria showCriteria = new CriteriaBuilder()
					.add(SHOWS.TABLE, SHOWS.SHOW_ID, showId, QueryConstants.EQUAL, Operator.AND).build();

			DataObject showDob = CRUDOperation.SelectAllColumns(SHOWS.TABLE, showCriteria, null);

			if (!CRUDUtil.DOBValidator(showDob)) {
				return new ArrayList<>();
			}

			Row showRow = CRUDUtil.getFirstRow(showDob, SHOWS.TABLE);
			long tmdId = CRUDUtil.parseLongFromRow(showRow, SHOWS.THEATRE_MOVIE_DETAIL_ID);
			List<Join> joinList = buildShowJoins();
			Criteria criteria = new CriteriaBuilder()
					.add(SHOWS.TABLE, SHOWS.THEATRE_MOVIE_DETAIL_ID, tmdId, QueryConstants.EQUAL, Operator.AND)
					.add(SHOWS.TABLE, SHOWS.SHOW_ID, showId, QueryConstants.NOT_EQUAL, Operator.AND)
					.add(CRUDUtil.getNotDeletedCriteria(SHOWS.TABLE), Operator.AND).build();

			SortColumn[] sort = new SortColumn[] { new SortColumn(SHOW_DATE.TABLE, SHOW_DATE.DATE, true) };
			DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(SHOWS.TABLE, criteria, joinList, sort);
			if (!CRUDUtil.DOBValidator(dob)) {
				return new ArrayList<>();
			}
			return getShowObjectList(dob);

		} catch (Exception e) {
			throw new RuntimeException("Error finding similar shows", e);
		}
	}

	public boolean deleteAllShowsByTheatre(long theatreId) {

		try {

			Criteria tmdCriteria = new CriteriaBuilder().add(THEATRE_MOVIE_DETAILS.TABLE,
					THEATRE_MOVIE_DETAILS.THEATRE_ID, theatreId, QueryConstants.EQUAL, Operator.AND).build();

			DataObject tmdDob = CRUDOperation.SelectAllColumns(THEATRE_MOVIE_DETAILS.TABLE, tmdCriteria, null);

			if (!CRUDUtil.DOBValidator(tmdDob)) {
				return false;
			}

			List<Long> tmdIds = new ArrayList<>();

			Iterator<Row> tmdRows = tmdDob.getRows(THEATRE_MOVIE_DETAILS.TABLE);
			while (tmdRows.hasNext()) {
				Row row = tmdRows.next();
				tmdIds.add(CRUDUtil.parseLongFromRow(row, THEATRE_MOVIE_DETAILS.THEATRE_MOVIE_DETAIL_ID));
			}

			return softDeleteShowsByTmdIds(tmdIds);

		} catch (Exception e) {
			throw new RuntimeException("Error deleting shows by theatre", e);
		}
	}

	public boolean deleteAllShowsByScreen(long screenId) {

		try {

			Criteria tmdCriteria = new CriteriaBuilder().add(THEATRE_MOVIE_DETAILS.TABLE,
					THEATRE_MOVIE_DETAILS.SCREEN_ID, screenId, QueryConstants.EQUAL, Operator.AND).build();

			DataObject tmdDob = CRUDOperation.SelectAllColumns(THEATRE_MOVIE_DETAILS.TABLE, tmdCriteria, null);
			if (!CRUDUtil.DOBValidator(tmdDob)) {
				return false;
			}

			List<Long> tmdIds = new ArrayList<>();

			Iterator<Row> rows = tmdDob.getRows(THEATRE_MOVIE_DETAILS.TABLE);
			while (rows.hasNext()) {
				Row row = rows.next();
				tmdIds.add(CRUDUtil.parseLongFromRow(row, THEATRE_MOVIE_DETAILS.THEATRE_MOVIE_DETAIL_ID));
			}
			return softDeleteShowsByTmdIds(tmdIds);

		} catch (Exception e) {
			throw new RuntimeException("Error deleting shows by screen", e);
		}
	}

	public boolean deleteAllShowsByMovie(long movieId) {

		try {
			Join join = CRUDUtil.buildJoin(THEATRE_MOVIE_DETAILS.TABLE, MOVIE_LANGUAGE_MAPPING.TABLE,
					new String[] { THEATRE_MOVIE_DETAILS.LANGUAGE_MAPPING_ID },
					new String[] { MOVIE_LANGUAGE_MAPPING.LANGUAGE_MAPPING_ID }, Join.INNER_JOIN);

			Criteria criteria = new CriteriaBuilder().add(MOVIE_LANGUAGE_MAPPING.TABLE, MOVIE_LANGUAGE_MAPPING.MOVIE_ID,
					movieId, QueryConstants.EQUAL, Operator.AND).build();

			DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(THEATRE_MOVIE_DETAILS.TABLE, criteria,
					Arrays.asList(join), null);

			if (!CRUDUtil.DOBValidator(dob)) {
				return false;
			}

			List<Long> tmdIds = new ArrayList<>();

			Iterator<Row> rows = dob.getRows(THEATRE_MOVIE_DETAILS.TABLE);
			while (rows.hasNext()) {
				Row row = rows.next();
				tmdIds.add(CRUDUtil.parseLongFromRow(row, THEATRE_MOVIE_DETAILS.THEATRE_MOVIE_DETAIL_ID));
			}

			return softDeleteShowsByTmdIds(tmdIds);

		} catch (Exception e) {
			throw new RuntimeException("Error deleting shows by movie", e);
		}
	}

	private boolean softDeleteShowsByTmdIds(List<Long> tmdIds) {

		try {

			if (tmdIds == null || tmdIds.isEmpty()) {
				return false;
			}

			Criteria criteria = new Criteria(Column.getColumn(SHOWS.TABLE, SHOWS.THEATRE_MOVIE_DETAIL_ID),
					tmdIds.toArray(), QueryConstants.IN);

			DataObject dob = CRUDOperation.SelectAllColumns(SHOWS.TABLE, criteria, null);

			if (!CRUDUtil.DOBValidator(dob)) {
				return false;
			}

			Iterator<Row> rows = dob.getRows(SHOWS.TABLE);

			while (rows.hasNext()) {
				Row row = rows.next();
				row.set(SHOWS.IS_DELETED, true);
				dob.updateRow(row);
			}

			CRUDOperation.updateDataObject(dob);

			return true;

		} catch (Exception e) {
			throw new RuntimeException("Error soft deleting shows", e);
		}
	}

	public Show findDeletedShow(long show_id) {

		try {
			List<Join> joinList = buildShowJoins();
			Criteria criteria = new CriteriaBuilder()
					.add(SHOWS.TABLE, SHOWS.SHOW_ID, show_id, QueryConstants.EQUAL, Operator.AND).build();
			DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(SHOWS.TABLE, criteria, joinList, null);

			if (!CRUDUtil.DOBValidator(dob)) {
				return null;
			}
			List<Show> showList = getShowObjectList(dob);
			if (!showList.isEmpty()) {
				return showList.get(0);
			}

		} catch (Exception e) {
			throw new RuntimeException("Error fetching deleted show", e);
		}
		return null;
	}

	public List<Show> getShowObjectList(DataObject dob) {

		List<Show> list = new ArrayList<>();
		screenDAO screen = new screenDAO();

		try {
			Iterator<Row> rows = dob.getRows(SHOWS.TABLE);

			while (rows.hasNext()) {

				Row showRow = rows.next();

				long show_id = CRUDUtil.parseLongFromRow(showRow, SHOWS.SHOW_ID);

				Row dateRow = dob.getRow(SHOW_DATE.TABLE,
						new Criteria(Column.getColumn(SHOW_DATE.TABLE, SHOW_DATE.DATE_ID), showRow.get(SHOWS.DATE_ID),
								QueryConstants.EQUAL));

				LocalDate date = ((Date) dateRow.get(SHOW_DATE.DATE)).toLocalDate();

				Row stmRow = dob.getRow(SHOW_TIME_MAPPING.TABLE,
						new Criteria(Column.getColumn(SHOW_TIME_MAPPING.TABLE, SHOW_TIME_MAPPING.SHOW_TIME_ID),
								showRow.get(SHOWS.SHOW_TIME_ID), QueryConstants.EQUAL));

				Row timeRow = dob.getRow(SHOW_TIME.TABLE,
						new Criteria(Column.getColumn(SHOW_TIME.TABLE, SHOW_TIME.TIME_ID),
								stmRow.get(SHOW_TIME_MAPPING.TIME_ID), QueryConstants.EQUAL));

				LocalTime time = ((Time) timeRow.get(SHOW_TIME.SHOW_TIME)).toLocalTime();

				Row tmdRow = dob.getRow(THEATRE_MOVIE_DETAILS.TABLE,
						new Criteria(
								Column.getColumn(THEATRE_MOVIE_DETAILS.TABLE,
										THEATRE_MOVIE_DETAILS.THEATRE_MOVIE_DETAIL_ID),
								showRow.get(SHOWS.THEATRE_MOVIE_DETAIL_ID), QueryConstants.EQUAL));

				long screen_id = CRUDUtil.parseLongFromRow(tmdRow, THEATRE_MOVIE_DETAILS.SCREEN_ID);
				long theatreId = CRUDUtil.parseLongFromRow(tmdRow, THEATRE_MOVIE_DETAILS.THEATRE_ID);

				Row screenRow = dob.getRow(SCREEN.TABLE, new Criteria(Column.getColumn(SCREEN.TABLE, SCREEN.SCREEN_ID),
						screen_id, QueryConstants.EQUAL));

				String screenName = CRUDUtil.parseStringFromRow(screenRow, SCREEN.SCREEN_NAME);

				Row theatreRow = dob.getRow(THEATRES.TABLE, new Criteria(
						Column.getColumn(THEATRES.TABLE, THEATRES.THEATRE_ID), theatreId, QueryConstants.EQUAL));

				String theatreName = CRUDUtil.parseStringFromRow(theatreRow, THEATRES.THEATRE_NAME);

				Row mlmRow = dob.getRow(MOVIE_LANGUAGE_MAPPING.TABLE,
						new Criteria(
								Column.getColumn(MOVIE_LANGUAGE_MAPPING.TABLE,
										MOVIE_LANGUAGE_MAPPING.LANGUAGE_MAPPING_ID),
								tmdRow.get(THEATRE_MOVIE_DETAILS.LANGUAGE_MAPPING_ID), QueryConstants.EQUAL));

				long movieId = CRUDUtil.parseLongFromRow(mlmRow, MOVIE_LANGUAGE_MAPPING.MOVIE_ID);

				Row movieRow = dob.getRow(MOVIE.TABLE,
						new Criteria(Column.getColumn(MOVIE.TABLE, MOVIE.MOVIE_ID), movieId, QueryConstants.EQUAL));

				String movieName = CRUDUtil.parseStringFromRow(movieRow, MOVIE.MOVIE_NAME);

				Row langRow = dob.getRow(MOVIE_LANGUAGE.TABLE,
						new Criteria(Column.getColumn(MOVIE_LANGUAGE.TABLE, MOVIE_LANGUAGE.LANGUAGE_ID),
								mlmRow.get(MOVIE_LANGUAGE_MAPPING.LANGUAGE_ID), QueryConstants.EQUAL));

				String language = CRUDUtil.parseStringFromRow(langRow, MOVIE_LANGUAGE.LANGUAGE);

				int avCap = getAvailableSeats(show_id);
				List<Pricing> pList = findShowPricing(show_id);
				Map<Long, Long> sList = findSeatBookingStatus(show_id);

				Show showObj = new Show(show_id, screen_id, movieName, theatreName, date, time, avCap, pList, sList,
						language, screenName, theatreId, movieId);

				showObj.setScreenObj(screen.findScreen(screen_id));

				list.add(showObj);
			}

		} catch (Exception e) {
			throw new RuntimeException("Error mapping shows", e);
		}

		return list;
	}

	private List<Join> buildShowJoins() {
		List<Join> joins = new ArrayList<>();

		joins.add(CRUDUtil.buildJoin(SHOWS.TABLE, THEATRE_MOVIE_DETAILS.TABLE,
				new String[] { SHOWS.THEATRE_MOVIE_DETAIL_ID },
				new String[] { THEATRE_MOVIE_DETAILS.THEATRE_MOVIE_DETAIL_ID }, Join.INNER_JOIN));

		joins.add(CRUDUtil.buildJoin(THEATRE_MOVIE_DETAILS.TABLE, SCREEN.TABLE,
				new String[] { THEATRE_MOVIE_DETAILS.SCREEN_ID }, new String[] { SCREEN.SCREEN_ID }, Join.INNER_JOIN));

		joins.add(CRUDUtil.buildJoin(THEATRE_MOVIE_DETAILS.TABLE, THEATRES.TABLE,
				new String[] { THEATRE_MOVIE_DETAILS.THEATRE_ID }, new String[] { THEATRES.THEATRE_ID },
				Join.INNER_JOIN));

		joins.add(CRUDUtil.buildJoin(SHOWS.TABLE, SHOW_DATE.TABLE, new String[] { SHOWS.DATE_ID },
				new String[] { SHOW_DATE.DATE_ID }, Join.INNER_JOIN));

		joins.add(CRUDUtil.buildJoin(SHOWS.TABLE, SHOW_TIME_MAPPING.TABLE, new String[] { SHOWS.SHOW_TIME_ID },
				new String[] { SHOW_TIME_MAPPING.SHOW_TIME_ID }, Join.INNER_JOIN));

		joins.add(CRUDUtil.buildJoin(SHOW_TIME_MAPPING.TABLE, SHOW_TIME.TABLE,
				new String[] { SHOW_TIME_MAPPING.TIME_ID }, new String[] { SHOW_TIME.TIME_ID }, Join.INNER_JOIN));

		joins.add(CRUDUtil.buildJoin(THEATRE_MOVIE_DETAILS.TABLE, MOVIE_LANGUAGE_MAPPING.TABLE,
				new String[] { THEATRE_MOVIE_DETAILS.LANGUAGE_MAPPING_ID },
				new String[] { MOVIE_LANGUAGE_MAPPING.LANGUAGE_MAPPING_ID }, Join.INNER_JOIN));

		joins.add(CRUDUtil.buildJoin(MOVIE_LANGUAGE_MAPPING.TABLE, MOVIE.TABLE,
				new String[] { MOVIE_LANGUAGE_MAPPING.MOVIE_ID }, new String[] { MOVIE.MOVIE_ID }, Join.INNER_JOIN));

		joins.add(CRUDUtil.buildJoin(MOVIE_LANGUAGE_MAPPING.TABLE, MOVIE_LANGUAGE.TABLE,
				new String[] { MOVIE_LANGUAGE_MAPPING.LANGUAGE_ID }, new String[] { MOVIE_LANGUAGE.LANGUAGE_ID },
				Join.INNER_JOIN));

		return joins;
	}

}
