package Schemas;

public class Location {
	private long location_id;
	String location;
	public Location(long location_id,String location)
	{
		this.setLocation_id(location_id);
		this.location=location;
	}
	public long getLocation_id() {
		return location_id;
	}
	public void setLocation_id(long location_id) {
		this.location_id = location_id;
	}
}
