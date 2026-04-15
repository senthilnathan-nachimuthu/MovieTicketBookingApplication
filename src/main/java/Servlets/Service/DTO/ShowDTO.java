package Servlets.Service.DTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class ShowDTO {
	LocalDate fromDate;
	LocalDate ToDate;
	Map<Long, List<LocalTime>> screenList;
	long movieId;
	String Language;
	public ShowDTO(LocalDate fromDate, LocalDate toDate, Map<Long, List<LocalTime>> screenList, long movieId,
			String language) {
		super();
		this.fromDate = fromDate;
		ToDate = toDate;
		this.screenList = screenList;
		this.movieId = movieId;
		Language = language;
	}
	public LocalDate getFromDate() {
		return fromDate;
	}
	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}
	public LocalDate getToDate() {
		return ToDate;
	}
	public void setToDate(LocalDate toDate) {
		ToDate = toDate;
	}
	public Map<Long, List<LocalTime>> getScreenList() {
		return screenList;
	}
	public void setScreenList(Map<Long, List<LocalTime>> screenList) {
		this.screenList = screenList;
	}
	public long getMovieId() {
		return movieId;
	}
	public void setMovieId(long movieId) {
		this.movieId = movieId;
	}
	public String getLanguage() {
		return Language;
	}
	public void setLanguage(String language) {
		Language = language;
	}
}
