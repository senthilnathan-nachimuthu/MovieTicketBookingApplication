package Servlets.Service.DTO;

import java.time.LocalTime;
import java.util.List;

import Schemas.Pricing;
import userInteraction.seat_Structure;

public class ScreenDTO {

	private Long theatreId;
	private Long screenId;
	private String screenType;
	private String screenName;
	private List<seat_Structure> seats;
	private List<Pricing> seatTypes;
	private List<LocalTime> showTimes;


	public Long getTheatreId() {
		return theatreId;
	}

	public void setTheatreId(Long theatreId) {
		this.theatreId = theatreId;
	}

	public Long getScreenId() {
		return screenId;
	}

	public void setScreenId(Long screenId) {
		this.screenId = screenId;
	}

	public String getScreenType() {
		return screenType;
	}

	public void setScreenType(String screenType) {
		this.screenType = screenType;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public List<seat_Structure> getSeats() {
		return seats;
	}

	public void setSeats(List<seat_Structure> seats) {
		this.seats = seats;
	}

	public List<Pricing> getSeatTypes() {
		return seatTypes;
	}

	public void setSeatTypes(List<Pricing> seatTypes) {
		this.seatTypes = seatTypes;
	}

	public List<LocalTime> getShowTimes() {
		return showTimes;
	}

	public void setShowTimes(List<LocalTime> showTimes) {
		this.showTimes = showTimes;
	}

}
