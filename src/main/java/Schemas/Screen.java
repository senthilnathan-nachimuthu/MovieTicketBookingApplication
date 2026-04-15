package Schemas;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import userInteraction.seat_Structure;

public class Screen {
	 public long screenId;
	 int seatCapacity;
	 int screenNo;
	 long theatreId;
	 String screentype;
	 List<LocalTime> showTime = new ArrayList<>();
	 List<seat_Structure> seats=new ArrayList<>();
	 String ScreenName;
	public Screen(long screenId,long thId, String screentype,int seatCapacity,List<seat_Structure> seats, List<LocalTime> l,int scNo, String screen_name) {
		this.screenId = screenId;
		this.screentype = screentype;
		this.showTime = l;
		this.seatCapacity=seatCapacity;
		this.seats=seats;
		this.screenNo=scNo;
		this.ScreenName=screen_name;
		this.theatreId=thId;
	}
	
	public long getTheatreId() {
		return theatreId;
	}

	public void setTheatreId(long theatreId) {
		this.theatreId = theatreId;
	}

	public String getScreenName() {
		return ScreenName;
	}

	public void setScreenName(String screenName) {
		ScreenName = screenName;
	}

	public long getScreenId() {
		return screenId;
	}

	public void setScreenId(long screenId) {
		this.screenId = screenId;
	}

	public int getScreenNo() {
		return screenNo;
	}

	public void setScreenNo(int screenNo) {
		this.screenNo = screenNo;
	}

	public String getScreentype() {
		return screentype;
	}

	public void setScreentype(String screentype) {
		this.screentype = screentype;
	}

	public void setShowTime(List<LocalTime> showTime) {
		this.showTime = showTime;
	}

	public void setSeats(List<seat_Structure> seats) {
		this.seats = seats;
	}

	public String getScreenType()
	{
		return this.screentype;
	}
	public int getSeatCapacity()
	{
		return this.seatCapacity;
	}
	public List<LocalTime> getShowTime()
	{
		return this.showTime;
	}
	public void setSeatCapacity(int newSeatCapacity)
	{
		this.seatCapacity=newSeatCapacity;
	}
	public void setScreenType(String newScreenType)
	{
		this.screentype=newScreenType;
	}
	public List<seat_Structure> getSeats() {
		return seats;
	}
	
	
}
