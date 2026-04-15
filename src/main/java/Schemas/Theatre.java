package Schemas;

import DAO.creditDAO;

public class Theatre {
	public long getTheatreId() {
		return theatreId;
	}
	public void setTheatreId(long theatreId) {
		this.theatreId = theatreId;
	}
	public String getTheatreLocation() {
		return theatreLocation;
	}
	public void setTheatreLocation(String theatreLocation) {
		this.theatreLocation = theatreLocation;
	}
	public double getTheatreCredit() {
		return theatreCredit;
	}
	public void setTheatreCredit(double theatreCredit) {
		this.theatreCredit = theatreCredit;
	}
	public void setTheatreName(String theatreName) {
		this.theatreName = theatreName;
	}

	public long theatreId;
	 String theatreName;
	// int totalScreens;
	 String theatreLocation;
	 double theatreCredit;
//	 Map<Integer, Screen> screenList = new HashMap<>();
//	 Map<Integer,Screen> screenBin=new HashMap<>();	
	//int nextScreenId = 1;

	public Theatre(long theatreid, String theatrename, String theatre_location) {
		this.theatreId = theatreid;
		this.theatreName = theatrename;
		this.theatreLocation=theatre_location;
		this.theatreCredit=getTheatreCredit(theatreid);
	}
	public Theatre(String theatrename, String theatre_location,double theatreCredit)
	{
	
		this.theatreName = theatrename;
		this.theatreLocation=theatre_location;
		this.theatreCredit=theatreCredit;
	}
private double getTheatreCredit(long theatreid2) {
	
	creditDAO credit =new creditDAO();
	double cr= credit.findCreditByTheatre(theatreid2);
	System.out.println(cr);
	return cr;
	
}
public String getLocation()
{
	return this.theatreLocation;
}
//	public int generateScreenId() {
//		return nextScreenId++;
//	}

	public String getTheatreName() {
	return this.theatreName;
	}
//	public int getTotalScreens() {
//		return this.totalScreens;
//	}
//	public Map<Integer,Screen> getScreenList()
//	{
//		return this.screenList;
//	}
//	public void  setTheatreName(String newName)
//	{
//		this.theatreName=newName;
//		return;
//	}
//
//	public void reduceTotalScreens() {
//	
//		this.totalScreens--;
//	}
//
//	public void setScreenBin(Map<Integer, Screen> screenList2) {
//		this.screenBin=screenList2;
//		
//	}
}
