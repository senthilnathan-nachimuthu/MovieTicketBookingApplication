package userInteraction;

import Schemas.Pricing;

public class seat_Structure {
	private long seatId;
	private int rowIndex;
	private int colIndex;
	private String seatType;
	private double seat_price;
	boolean disabled;

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.disabled = isDisabled;
	}

	int seatTypeIndex;
	Pricing seatTypeObj;

	public seat_Structure(long SeatId, int row, int col) {
		this.seatId = SeatId;
		this.setRowIndex(row);
		this.setColIndex(col);
	}

	public int getSeatTypeIndex() {
		return seatTypeIndex;
	}

	public void setSeatTypeIndex(int seatTypeIndex) {
		this.seatTypeIndex = seatTypeIndex;
	}

	public Pricing getSeatTypeObj() {
		return seatTypeObj;
	}

	public void setSeatTypeObj(Pricing seatTypeObj) {
		this.seatTypeObj = seatTypeObj;
	}

	public void setSeatType(String seat) {
		this.seatType = seat;
		if (this.seatTypeObj != null) {
			this.seatTypeObj.setSeatType(seat);
		}
		// this.seatTypeObj.setSeatType(seat);
	}

	public String getSeatType() {
		return seatTypeObj.getSeatType();
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public int getColIndex() {
		return colIndex;
	}

	public void setColIndex(int colIndex) {
		this.colIndex = colIndex;
	}

	public double getSeat_price() {
		return seatTypeObj.getPrice();
	}

	public void setSeat_price(double seat_price) {
		this.seat_price = seat_price;
		// this.seatTypeObj.setPrice(seat_price);
		if (this.seatTypeObj != null) {
			this.seatTypeObj.setPrice(seat_price);
		}
	}

	public long getSeatId() {
		return seatId;
	}

	public void setSeatId(long seatId) {
		this.seatId = seatId;
	}

}
