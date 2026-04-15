package Schemas;

public class Pricing {

	private long seatTypeId;
	private String seatType;
	private double Price;
	public Pricing(long seatId,String type,double price)
	{
		this.setSeatTypeId(seatId);
		this.setSeatType(type);
		this.setPrice(price);
	}
	public String getSeatType() {
		return seatType;
	}
	public void setSeatType(String seatType) {
		this.seatType = seatType;
	}
	public double getPrice() {
		return Price;
	}
	public void setPrice(double price) {
		this.Price = price;
	}
	public long getSeatTypeId() {
		return seatTypeId;
	}
	public void setSeatTypeId(long seatTypeId) {
		this.seatTypeId = seatTypeId;
	}
}
