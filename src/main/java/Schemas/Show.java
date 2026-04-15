package Schemas;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Show {

	public long showId;
	public long screenId;
	public long movieId;
	public long theatreId;
	public String movie;
	public LocalDate date;
	public LocalTime time;
	int AvailableCapacity;
	String theatre;
	String screenName;
	String language;
	Screen screenObj;
	public Screen getScreenObj() {
		return screenObj;
	}

	public void setScreenObj(Screen screenObj) {
		this.screenObj = screenObj;
	}

	public long getScreenId() {
		return screenId;
	}

	public void setScreenId(long screenId) {
		this.screenId = screenId;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	List<Pricing> pricingList;
	Map<Long, Long> bookingStatus = new HashMap<>();

	public String getTheatre() {
		return theatre;
	}

	public void setTheatre(String theatre) {
		this.theatre = theatre;
	}

	public Show(LocalDate date, LocalTime showTime) {
		this.time = showTime;
		this.date = date;
	}

	public Show(long show_id, long scId, String mov, String th, LocalDate showDate, LocalTime showTime, int avCap,
			List<Pricing> p, Map<Long, Long> m, String language,String scName,long theatreId,long movieId) {
		this.showId = show_id;
		this.screenId = scId;
		this.movie = mov;
		this.date = showDate;
		this.time = showTime;
		this.AvailableCapacity = avCap;
		this.pricingList = p;
		this.bookingStatus = m;
		this.theatre = th;
		this.language = language;
		this.screenName=scName;
		this.movieId=movieId;
		this.theatreId=theatreId;
	}


	public long getMovieId() {
		return movieId;
	}

	public void setMovieId(long movieId) {
		this.movieId = movieId;
	}

	public long getTheatreId() {
		return theatreId;
	}

	public void setTheatreId(long theatreId) {
		this.theatreId = theatreId;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public long getShowId() {
		return showId;
	}

	public void setShowId(long showId) {
		this.showId = showId;
	}

	public String getMovie() {
		return movie;
	}

	public void setMovie(String movie) {
		this.movie = movie;
	}
	private String FetchScreenName(long scId) {
		// TODO Auto-generated method stub
		return null;
	}


	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public int getAvailableCapacity() {
		return AvailableCapacity;
	}

	public void setAvailableCapacity(int availableCapacity) {
		AvailableCapacity = availableCapacity;
	}

	public List<Pricing> getPricingList() {
		return pricingList;
	}

	public void setPricingList(List<Pricing> pricingList) {
		this.pricingList = pricingList;
	}

	public Map<Long, Long> getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(Map<Long, Long> bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

	public double getShowPricing(String seatType) {
		List<Pricing> p = getPricingList();
		for (Pricing i : p) {
			if (i.getSeatType().equals(seatType)) {
				return i.getPrice();
			}
		}
		return 0;
	}

}
