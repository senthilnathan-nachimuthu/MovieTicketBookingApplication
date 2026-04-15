package Utilities;

public class ShowCreationResult {
	
	public long screenId;
	public String showDate;
	public String showTime;
	public boolean isCreated;
	public String Reason;
	public ShowCreationResult(long screenId, String showDate, String showTime, boolean isCreated, String reason) {
		super();
		this.screenId = screenId;
		this.showDate = showDate;
		this.showTime = showTime;
		this.isCreated = isCreated;
		Reason = reason;
	}
	public long getScreenId() {
		return screenId;
	}
	public void setScreenId(long screenId) {
		this.screenId = screenId;
	}
	public String getShowDate() {
		return showDate;
	}
	public void setShowDate(String showDate) {
		this.showDate = showDate;
	}
	public String getShowTime() {
		return showTime;
	}
	public void setShowTime(String showTime) {
		this.showTime = showTime;
	}
	public boolean isCreated() {
		return isCreated;
	}
	public void setCreated(boolean isCreated) {
		this.isCreated = isCreated;
	}
	public String getReason() {
		return Reason;
	}
	public void setReason(String reason) {
		Reason = reason;
	}
}
