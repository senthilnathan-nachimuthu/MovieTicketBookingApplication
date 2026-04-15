package WalletPackage;

public class Wallet {
	
	Wallet(long userid,double amount,double credits,double pending)
	{
		this.walletAmount=amount;
		this.creditPoints=credits;
		this.pendingCredits=pending;
		this.userId=userid;
	}
	private String walletId;
	private long userId;
	private String walletPassword;
	public double walletAmount;
	private double creditPoints;
	private double pendingCredits=0;
	public double getWalletAmount() {
		return walletAmount;
	}
	public void setWalletAmount(double walletAmount) {
		this.walletAmount = walletAmount;
	}
	public double getCreditPoints() {
		return creditPoints;
	}
	public void setCreditPoints(double creditPoints) {
		this.creditPoints = creditPoints;
	}
	public double getPendingCredits() {
		return pendingCredits;
	}
	public void setPendingCredits(double pendingCredits) {
		this.pendingCredits = pendingCredits;
	}
	public Wallet(String walletid2,long UserId,String password)
	{
		this.walletId=walletid2;
		this.userId=UserId;
		this.walletPassword=password;
	}
	public String getWalletId() {
		
		return this.walletId;
	}
	public double getWalletBalance() {
		return walletAmount;
	}
	public double getCredits()
	{
		return creditPoints;
	}
	public boolean verifyPassword(String walletPassword2) {
		
		if(walletPassword.equals(walletPassword2))
		{
			return true;
		}
		return false;
		
	}
//	public void addBalance(double amount) {
//
//		if(amount>0)
//		{
//			walletAmount+=amount;			
//		}
//		
//	}
//	public void withDrawMoney(double amount) {
//	
//		if(walletAmount>=amount && amount>0)
//		{
//			walletAmount-=amount;
//		}
//	}
//	public void addCredit(double credits) {
//		
//		if(credits>0)
//		{
//			creditPoints+=credits;
//		}
//		
//	}
//	public void reduceCreditPoints(double recoveredCredit) {
//		
//		if(recoveredCredit>0 && creditPoints>=recoveredCredit)
//		{
//			creditPoints-=recoveredCredit;
//		}
//	}
	public double getPendingcredits()
	{
		return pendingCredits;
	}
	public void setPendingCredit(double value)
	{
		pendingCredits=value;
	}
}
