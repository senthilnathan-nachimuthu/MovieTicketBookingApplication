package Servlets.Web;

public class ApiResponse {
	
	int status;
	String message;
	Object data;
	
	public ApiResponse(int stCode,String msg,Object data)
	{
		this.status=stCode;
		this.message=msg;
		this.data=data;
	}
	public int getStatusCode() {
		return status;
	}
	public void setStatusCode(int statusCode) {
		this.status = statusCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}


}
