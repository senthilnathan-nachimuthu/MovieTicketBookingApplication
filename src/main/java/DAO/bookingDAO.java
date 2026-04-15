package DAO;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.movieticketbooking.BOOKED_SEATS;
import com.adventnet.movieticketbooking.BOOKINGS;
import com.adventnet.movieticketbooking.MOVIE_LANGUAGE_MAPPING;
import com.adventnet.movieticketbooking.SHOWS;
import com.adventnet.movieticketbooking.THEATRE_MOVIE_DETAILS;
import com.adventnet.movieticketbooking.TICKETS;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;

import MickeyDBAccess.CRUDOperation;
import MickeyDBAccess.CRUDUtil;
import MickeyDBAccess.CriteriaBuilder;
import MickeyDBAccess.Operator;
import Schemas.Booking;

public class bookingDAO {

	public long insertBooking(long showId, double totalAmount, double credit, long userId, String bookingDesc,
			List<Long> seats) {

		long ticket_id = insertTicket(showId, totalAmount, credit, seats);
		if (ticket_id != -1) {

			Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
			Row row = new Row(BOOKINGS.TABLE);
			row.set(BOOKINGS.USER_ID, userId);
			row.set(BOOKINGS.DATE_TIME_BOOKING, currentTimestamp);
			row.set(BOOKINGS.BOOKING_STATUS, true);
			row.set(BOOKINGS.BOOKING_DESCRIPTION, bookingDesc);
			row.set(BOOKINGS.TICKET_ID, ticket_id);

			DataObject dob = CRUDOperation.insertOneRow(BOOKINGS.TABLE, row);
			if (CRUDUtil.DOBValidator(dob)) {
				Row result = CRUDUtil.getFirstRow(dob, BOOKINGS.TABLE);
				return CRUDUtil.parseLongFromRow(result, BOOKINGS.BOOKING_ID);
			}
		}
		return -1;
	}

	public long insertTicket(long showId, double totalAmount, double credit, List<Long> seats) {

		Row row = new Row(TICKETS.TABLE);
		row.set(TICKETS.SHOW_ID, showId);
		row.set(TICKETS.TOTAL_AMOUNT, totalAmount);
		row.set(TICKETS.CREDITS_EARNED, credit);

		DataObject dob = CRUDOperation.insertOneRow(TICKETS.TABLE, row);
		if (CRUDUtil.DOBValidator(dob)) {
			Row result = CRUDUtil.getFirstRow(dob, TICKETS.TABLE);
			long ticket_id = CRUDUtil.parseLongFromRow(result, TICKETS.TICKET_ID);
			if (ticket_id != -1) {
				insertBookedSeats(seats, ticket_id);
			}

			return ticket_id;
		}
		return -1;
	}

	private void insertBookedSeats(List<Long> seats, long ticket_id) {

		List<Row> rowList = new ArrayList<>();
		for (Long l : seats) {
			Row row = new Row(BOOKED_SEATS.TABLE);
			row.set(BOOKED_SEATS.SEAT_ID, l);
			row.set(BOOKED_SEATS.TICKET_ID, ticket_id);
			rowList.add(row);
		}
		DataObject dob = CRUDOperation.insertMultipleRows(BOOKED_SEATS.TABLE, rowList);
	}

	public Booking findBookingById(long booking_id) {

		Join j1 = bookingToTicketJoin();
		Criteria criteria = new CriteriaBuilder()
				.add(BOOKINGS.TABLE, BOOKINGS.BOOKING_ID, booking_id, QueryConstants.EQUAL, null).build();

		DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(BOOKINGS.TABLE, criteria, Arrays.asList(j1), null);
		return getBookingObject(dob);
	}

	public List<Booking> findBookingByUserId(long userId) {
		Join j1 = bookingToTicketJoin();
		Criteria criteria = new CriteriaBuilder()
				.add(BOOKINGS.TABLE, BOOKINGS.USER_ID, userId, QueryConstants.EQUAL, null).build();
		DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(BOOKINGS.TABLE, criteria, Arrays.asList(j1), null);
		if (CRUDUtil.DOBValidator(dob)) {
			return getBookingObjectList(dob);
		}
		return new ArrayList<>();
	}

	public boolean cancelBooking(List<Object[]> bookingList) {

		try {

			if (bookingList == null || bookingList.isEmpty()) {
				return false;
			}

			for (Object[] obj : bookingList) {

				String description = (String) obj[0];
				long bookingId = ((Number) obj[1]).longValue();
				Criteria criteria = new CriteriaBuilder()
						.add(BOOKINGS.TABLE, BOOKINGS.BOOKING_ID, bookingId, QueryConstants.EQUAL, Operator.AND)
						.build();
				CRUDOperation.updateMultipleColumns(BOOKINGS.TABLE,
						new String[] { BOOKINGS.BOOKING_STATUS, BOOKINGS.BOOKING_DESCRIPTION },
						new Object[] { false, description }, criteria);
			}
			return true;

		} catch (Exception e) {
			throw new RuntimeException("Error cancelling booking", e);
		}
	}

	public boolean updateBookingStatus(long bookingId, String description) {

		try {

			Criteria criteria = new CriteriaBuilder()
					.add(BOOKINGS.TABLE, BOOKINGS.BOOKING_ID, bookingId, QueryConstants.EQUAL, Operator.AND).build();
			CRUDOperation.updateMultipleColumns(BOOKINGS.TABLE,
					new String[] { BOOKINGS.BOOKING_STATUS, BOOKINGS.BOOKING_DESCRIPTION },
					new Object[] { false, description }, criteria);
			return true;

		} catch (Exception e) {
			throw new RuntimeException("Error updating booking status", e);
		}
	}

	public boolean updateAmount(long bookingId, double totalAmount) {

		try {
			Criteria bookingCriteria = new CriteriaBuilder()
					.add(BOOKINGS.TABLE, BOOKINGS.BOOKING_ID, bookingId, QueryConstants.EQUAL, Operator.AND).build();

			DataObject bookingDob = CRUDOperation.SelectAllColumns(BOOKINGS.TABLE, bookingCriteria, null);

			if (!CRUDUtil.DOBValidator(bookingDob)) {
				return false;
			}
			Row bookingRow = CRUDUtil.getFirstRow(bookingDob, BOOKINGS.TABLE);
			long ticketId = CRUDUtil.parseLongFromRow(bookingRow, BOOKINGS.TICKET_ID);
			Criteria ticketCriteria = new CriteriaBuilder()
					.add(TICKETS.TABLE, TICKETS.TICKET_ID, ticketId, QueryConstants.EQUAL, Operator.AND).build();

			CRUDOperation.updateSingleColumn(TICKETS.TABLE, ticketCriteria, TICKETS.TOTAL_AMOUNT, totalAmount);
			return true;

		} catch (Exception e) {
			throw new RuntimeException("Error updating amount", e);
		}
	}

	public boolean updateShow(long bookingId, long showId) {

		try {
			Criteria bookingCriteria = new CriteriaBuilder()
					.add(BOOKINGS.TABLE, BOOKINGS.BOOKING_ID, bookingId, QueryConstants.EQUAL, Operator.AND).build();

			DataObject bookingDob = CRUDOperation.SelectAllColumns(BOOKINGS.TABLE, bookingCriteria, null);

			if (!CRUDUtil.DOBValidator(bookingDob)) {
				return false;
			}

			Row bookingRow = CRUDUtil.getFirstRow(bookingDob, BOOKINGS.TABLE);
			long ticketId = CRUDUtil.parseLongFromRow(bookingRow, BOOKINGS.TICKET_ID);
			Criteria ticketCriteria = new CriteriaBuilder()
					.add(TICKETS.TABLE, TICKETS.TICKET_ID, ticketId, QueryConstants.EQUAL, Operator.AND).build();

			CRUDOperation.updateSingleColumn(TICKETS.TABLE, ticketCriteria, TICKETS.SHOW_ID, showId);
			return true;

		} catch (Exception e) {
			throw new RuntimeException("Error updating show", e);
		}
	}

	public List<Booking> findBookingByTheatre(long theatreId) {

		Criteria criteria = new Criteria(
				Column.getColumn(THEATRE_MOVIE_DETAILS.TABLE, THEATRE_MOVIE_DETAILS.THEATRE_ID), theatreId,
				QueryConstants.EQUAL);

		return fetchBookings(criteria);
	}

	public List<Booking> findBookingByMovie(long movieId) {

		List<Join> joins = buildBookingJoins();
		joins.add(CRUDUtil.buildJoin(THEATRE_MOVIE_DETAILS.TABLE, MOVIE_LANGUAGE_MAPPING.TABLE,
				new String[] { THEATRE_MOVIE_DETAILS.LANGUAGE_MAPPING_ID },
				new String[] { MOVIE_LANGUAGE_MAPPING.LANGUAGE_MAPPING_ID }, Join.INNER_JOIN));

		Criteria criteria = new Criteria(
				Column.getColumn(MOVIE_LANGUAGE_MAPPING.TABLE, MOVIE_LANGUAGE_MAPPING.MOVIE_ID), movieId,
				QueryConstants.EQUAL);

		return fetchBookings(criteria, joins); // ✅ FIXED
	}

	public List<Booking> findBookingByScreen(long screenId) {

		Criteria criteria = new Criteria(Column.getColumn(THEATRE_MOVIE_DETAILS.TABLE, THEATRE_MOVIE_DETAILS.SCREEN_ID),
				screenId, QueryConstants.EQUAL);
		return fetchBookings(criteria);
	}

	public List<Booking> findbookingByShows(long showId) {

		Criteria criteria = new Criteria(Column.getColumn(SHOWS.TABLE, SHOWS.SHOW_ID), showId, QueryConstants.EQUAL);
		return fetchBookings(criteria);
	}

	public List<Booking> findAllBookings() {
		return fetchBookings(null);
	}

	public List<Booking> getBookingObjectList(DataObject dob) {
		List<Booking> list = new ArrayList<>();
		try {

			Iterator<Row> rows = dob.getRows(BOOKINGS.TABLE);

			while (rows.hasNext()) {

				Row row = rows.next();

				long booking_id = CRUDUtil.parseLongFromRow(row, BOOKINGS.BOOKING_ID);
				Object dateObj = row.get(BOOKINGS.DATE_TIME_BOOKING);
				LocalDateTime ldt = ((java.sql.Timestamp) dateObj).toLocalDateTime();
				String description = CRUDUtil.parseStringFromRow(row, BOOKINGS.BOOKING_DESCRIPTION);
				boolean status = (boolean) row.get(BOOKINGS.BOOKING_STATUS);
				long ticketId = CRUDUtil.parseLongFromRow(row, BOOKINGS.TICKET_ID);
				Row Ticketrow = dob.getRow(TICKETS.TABLE, new CriteriaBuilder()
						.add(TICKETS.TABLE, TICKETS.TICKET_ID, ticketId, QueryConstants.EQUAL, null).build());
				if (Ticketrow == null) {
					throw new RuntimeException("Ticket not found for booking: " + booking_id);
				}
				long showId = CRUDUtil.parseLongFromRow(Ticketrow, TICKETS.SHOW_ID);
				double amount = CRUDUtil.parseDoubleFromRow(Ticketrow, TICKETS.TOTAL_AMOUNT);
				double credits = CRUDUtil.parseDoubleFromRow(Ticketrow, TICKETS.CREDITS_EARNED);
				long user_id = CRUDUtil.parseLongFromRow(row, BOOKINGS.USER_ID);
				Booking bookingObj = new Booking(booking_id, user_id, showId, ldt, description, status, amount,
						credits);
				list.add(bookingObj);
			}

		} catch (Exception e) {
			throw new RuntimeException("Error while creating booking Object", e);
		}
		return list;
	}

	public Booking getBookingObject(DataObject dob) {

		try {

			Iterator<Row> rows = dob.getRows(BOOKINGS.TABLE);

			if (rows.hasNext()) {

				Row row = rows.next();

				long booking_id = CRUDUtil.parseLongFromRow(row, BOOKINGS.BOOKING_ID);
				Object dateObj = row.get(BOOKINGS.DATE_TIME_BOOKING);
				LocalDateTime ldt = ((java.sql.Timestamp) dateObj).toLocalDateTime();
				String description = CRUDUtil.parseStringFromRow(row, BOOKINGS.BOOKING_DESCRIPTION);
				boolean status = (boolean) row.get(BOOKINGS.BOOKING_STATUS);
				long ticketId = CRUDUtil.parseLongFromRow(row, BOOKINGS.TICKET_ID);
				Row Ticketrow = dob.getRow(TICKETS.TABLE, new CriteriaBuilder()
						.add(TICKETS.TABLE, TICKETS.TICKET_ID, ticketId, QueryConstants.EQUAL, null).build());
				if (Ticketrow == null) {
					throw new RuntimeException("Ticket not found for booking: " + booking_id);
				}
				long showId = CRUDUtil.parseLongFromRow(Ticketrow, TICKETS.SHOW_ID);
				double amount = CRUDUtil.parseDoubleFromRow(Ticketrow, TICKETS.TOTAL_AMOUNT);
				double credits = CRUDUtil.parseDoubleFromRow(Ticketrow, TICKETS.CREDITS_EARNED);
				long user_id = CRUDUtil.parseLongFromRow(row, BOOKINGS.USER_ID);
				return new Booking(booking_id, user_id, showId, ldt, description, status, amount, credits);
			}

		} catch (Exception e) {
			throw new RuntimeException("Error while creating booking Object", e);
		}

		return null;
	}

	public List<Long> findBookedSeats(long bookingId) {

		List<Long> list = new ArrayList<>();

		List<Join> joins = Arrays.asList(CRUDUtil.buildJoin(BOOKED_SEATS.TABLE, BOOKINGS.TABLE,
				new String[] { BOOKED_SEATS.TICKET_ID }, new String[] { BOOKINGS.TICKET_ID }, Join.INNER_JOIN));

		Criteria criteria = new Criteria(Column.getColumn(BOOKINGS.TABLE, BOOKINGS.BOOKING_ID), bookingId,
				QueryConstants.EQUAL);

		DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(BOOKED_SEATS.TABLE, criteria, joins, null);

		if (!CRUDUtil.DOBValidator(dob))
			return list;

		Iterator<Row> rows;
		try {
			rows = dob.getRows(BOOKED_SEATS.TABLE);
		} catch (DataAccessException e) {
			throw new RuntimeException(e);
		}
		while (rows.hasNext()) {
			Row row = rows.next();
			list.add(CRUDUtil.parseLongFromRow(row, BOOKED_SEATS.SEAT_ID));
		}

		return list;
	}

	private Join bookingToTicketJoin() {
		Join j1 = CRUDUtil.buildJoin(BOOKINGS.TABLE, TICKETS.TABLE, new String[] { BOOKINGS.TICKET_ID },
				new String[] { TICKETS.TICKET_ID }, Join.INNER_JOIN);
		return j1;
	}

	private List<Join> buildBookingJoins() {

		List<Join> joins = new ArrayList<>();

		joins.add(CRUDUtil.buildJoin(BOOKINGS.TABLE, TICKETS.TABLE, new String[] { BOOKINGS.TICKET_ID },
				new String[] { TICKETS.TICKET_ID }, Join.INNER_JOIN));

		joins.add(CRUDUtil.buildJoin(TICKETS.TABLE, SHOWS.TABLE, new String[] { TICKETS.SHOW_ID },
				new String[] { SHOWS.SHOW_ID }, Join.INNER_JOIN));

		joins.add(CRUDUtil.buildJoin(SHOWS.TABLE, THEATRE_MOVIE_DETAILS.TABLE,
				new String[] { SHOWS.THEATRE_MOVIE_DETAIL_ID },
				new String[] { THEATRE_MOVIE_DETAILS.THEATRE_MOVIE_DETAIL_ID }, Join.INNER_JOIN));

		return joins;
	}

	private List<Booking> fetchBookings(Criteria criteria) {
		return fetchBookings(criteria, buildBookingJoins());
	}

	private List<Booking> fetchBookings(Criteria criteria, List<Join> joins) {

		try {

			Criteria finalCriteria = new Criteria(Column.getColumn(BOOKINGS.TABLE, BOOKINGS.BOOKING_STATUS), true,
					QueryConstants.EQUAL);

			if (criteria != null) {
				finalCriteria = finalCriteria.and(criteria);
			}

			SortColumn[] sort = new SortColumn[] { new SortColumn(BOOKINGS.TABLE, BOOKINGS.DATE_TIME_BOOKING, false) };

			DataObject dob = CRUDOperation.SelectAllColumnsWithJoin(BOOKINGS.TABLE, finalCriteria, joins, sort);

			if (!CRUDUtil.DOBValidator(dob)) {
				return new ArrayList<>();
			}

			return getBookingObjectList(dob);

		} catch (Exception e) {
			throw new RuntimeException("Error fetching bookings", e);
		}
	}

}
