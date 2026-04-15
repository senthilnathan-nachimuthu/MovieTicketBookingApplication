package WalletPackage;

public class TestingAccount {
	
	private double balance=10000;
	private String upiId;
	private String accNo;
	private String ifscCode;
	private String debitCardNumber;
	private String cvv;
	public TestingAccount(double balance,String accNo, String ifscCode,String upiId,String debitCardNumber,String cvv)
	{
		this.balance=balance;
		this.upiId=upiId;
		this.accNo=accNo;
		this.ifscCode=ifscCode;
		this.debitCardNumber=debitCardNumber;
		this.cvv=cvv;
	}
	
	public double getBalance()
	{
		return balance;
	}
	public void withDraw(double amount)
	{
		if(amount<balance)
		{
			balance-=amount;
		}
		else
		{
			System.err.println("Insufficient balance");
			
		}
	}
	public void credit(double amount)
	{
			balance+=amount;
		
	}
	public void setBalance(double balance2)
	{
		this.balance=balance2;
	}
	public String getupiId()
	{
		return upiId;
	}
	public String getAccNo()
	{
		return accNo;
	}
	public String getIfsc()
	{
		return ifscCode;
	}
	
	public String getdebitcard()
	{
		return debitCardNumber;
	}
	public String getCvv()
	{
		return cvv;
	}
	
	
	
	

}
