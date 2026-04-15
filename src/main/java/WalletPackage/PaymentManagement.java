package WalletPackage;

import DAO.userDAO;
import Schemas.Users;
import userInteraction.InputHandler;

public class PaymentManagement {
	
	TestingAccount taObj;
	InputHandler ipObj;
	public PaymentManagement(InputHandler inputhandlerObj,TestingAccount ta){
		this.ipObj=inputhandlerObj;
		this.taObj=ta;
	}
	public double getMoney(Users userObj)
	{
		int paymentChoice=PaymentOptions();
		double amount=0;
		if(paymentChoice==1)
		{
			amount=transactionThroughUPI(userObj, amount);
		}
		else if(paymentChoice==2)
		{
			amount=transactionThroughCARD(userObj, amount);
		}
		else if(paymentChoice==3)
		{
			amount=transactionThroughNET(userObj, amount);
		}
		return amount;
	}
	public boolean creditThroughNET(double amount,Users userObj)
	{
		if(amount<0)
		{
			System.out.println("Invalid amount");
			return false;
		}
		//System.out.println("NET-BANKING:");
		
		//String accountNumber=ipObj.mreadStringInput("Enter Account Number:");
		String accountNumber=taObj.getAccNo();
		if(accountNumber.matches("[0-9]+") && accountNumber.length()==16 && taObj.getAccNo().equals(accountNumber))
		{
			//String ifscCode=ipObj.mreadStringInput("Enter 11 Digit Bank IFSC Code");
			String ifscCode=taObj.getIfsc();
			if(ifscCode.length()==11 && taObj.getIfsc().equals(ifscCode))
			{
				userDAO user=new userDAO();
				if(user.creditAmount(userObj.userId,  amount))
				{
					System.out.println("Transaction successfull");
				}
					return true;
			}
			else
			{
				System.out.println("WroncreditThroughNETg IFSC Code");
			}
			
		}
		else
		{
			System.out.println("Invalid account Number");
			return false;
		}
		return true;
	}
	public double transactionThroughNET(Users userObj,double amount) {
		
		System.out.println("NET-BANKING:");
		//double amount=0;
	//	String accountNumber=ipObj.mreadStringInput("Enter Account Number:");
		String accountNumber=taObj.getAccNo();
		if(accountNumber.matches("[0-9]+") && accountNumber.length()==16 && taObj.getAccNo().equals(accountNumber))
		{
			//String ifscCode=ipObj.mreadStringInput("Enter 11 Digit Bank IFSC Code");
			String ifscCode=taObj.getIfsc();
			if(ifscCode.length()==11 && taObj.getIfsc().equals(ifscCode))
			{
//				System.out.println("Enter Amount");
//				amount=ipObj.s.nextDouble();
				//ipObj.s.nextLine();
				if(amount<taObj.getBalance())
				{
					taObj.withDraw(amount);
					if(amount>0)
					{
						return amount;
					}
					else
					{
						System.out.println("Invalid amount.");
					}
					
				}
				else
				{
					System.out.println("Insufficient Account Balance.Transaction Failed");
					amount=0;
				}
			}
			else
			{
				System.out.println("Wrong IFSC Code");
			}
			
		}
		else
		{
			System.out.println("Invalid account Number");
		}
		return amount;
	}
	private double transactionThroughCARD(Users userObj,double amount) {
	
//		String cardNumber=ipObj.mreadStringInput("Enter Card Number");
//		String cvv=ipObj.mreadStringInput("Enter Card CVV");
		String cardNumber=taObj.getdebitcard();
		String cvv=taObj.getCvv();
		//double amount=0;
		if(taObj.getdebitcard().equals(cardNumber) && taObj.getCvv().equals(cvv))
		{
			//amount=ipObj.mreadDoubleInput("Enter Amount.");
			if(amount<taObj.getBalance())
			{
				userDAO user=new userDAO();
				if(user.withDraw(userObj.userId,  amount))
				{
					System.out.println("Transaction successfull");
				}
			}
			else
			{
				System.out.println("Insufficient Account Balance.Transaction Failed");
				amount= 0;
			}
		}
		return amount;
	}
	private double transactionThroughUPI(Users userObj,double amount) {
		
//		String upId=ipObj.mreadStringInput("Enter UPI ID");
		String upId=taObj.getupiId();
		//double amount=0;
		if(taObj.getupiId().equals(upId))
		{
			//System.out.println("Enter amount");
		//	amount=ipObj.s.nextDouble();
			if(taObj.getBalance()>=amount)
			{
				userDAO user=new userDAO();

				if(user.withDraw(userObj.userId,  amount))
				{
					//System.out.println("Transaction successfull");
				}
			}
			else
			{
				System.out.println("Insufficient Account Balance.Transaction Failed");
				amount= 0;
			}
		}
		else
		{
			System.out.println("UPI ID not found.");
		}
		return amount;
		
	}
	private int PaymentOptions() {
	
		System.out.println("Enter\n1.UPI\n2.CARD\n3.NET BANKING");
		int paymentChoice=ipObj.readIntInput();
		ipObj.s.nextLine();
		while(paymentChoice>3 ||paymentChoice<=0)
		{
			System.out.println("Wrong Choice. Re-Enter");
			System.out.println("Enter\n1.UPI\n2.CARD\n3.NET BANKING");
			paymentChoice=ipObj.readIntInput();
			ipObj.s.nextLine();
		}
		return paymentChoice;
	}
	public void getAccountBalance()
	{
		System.out.println("\nAccount Balance: "+taObj.getBalance());
	}
	public void refund(Users user,double amount) {
	
		userDAO userObj=new userDAO();
		userObj.creditAmount(user.userId, amount);
		
	}

}
