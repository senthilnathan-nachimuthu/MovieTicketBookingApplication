package Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import DAO.userDAO;
import DAO.walletDAO;
import Exceptions.ApplicationException;
import Schemas.Users;
import WalletPackage.TestingAccount;
import WalletPackage.Wallet;

public class userUtilities {

	static private List<TestingAccount> accountList = new ArrayList<>();
	String tempError = "";
	public Users doLogin(String uname, String upass) {
		return isValid(uname, upass);
	}
	public Users doSignUp(String name, String gender, int age, String username, String password)  {
		
		boolean user = isExists(username);
		if (user == true) {
			throw new ApplicationException("Username Already Exists.Try Other.");
		}
		if (age > 100) {
			throw new ApplicationException("Age must be less than 100.");
		}
		double balance = generateBalance();
		String upiId = generateUpi();
		String accNo = generateAccountNumber();
		String cardNumber = generatecardNumber();
		String cvv = generateCvv();
		TestingAccount taObj = new TestingAccount(balance, upiId, accNo, "CNRB0160411", cardNumber, cvv);

		userDAO userDaoObj = new userDAO();
		Users newUserObj = userDaoObj.insertUser(name, username, password,gender,age, false);
		//userDaoObj.insertUserDetails(newUserObj.userId, gender, age);

		userDaoObj.insertBankAccount(newUserObj.userId, balance, upiId, accNo, "CNRB0160411", cardNumber, cvv);
		newUserObj.setAccount(taObj);
		return newUserObj;
	}
	private String generateCvv() {

		StringBuilder cvv = new StringBuilder();
		String temp = "1234567890";
		Random random = new Random();
		for (int i = 0; i < 3; i++) {
			cvv.append(temp.charAt(random.nextInt(temp.length())));
		}
		while (isExits(cvv.toString(), "cvv")) {
			for (int i = 0; i < 3; i++) {
				cvv.append(temp.charAt(random.nextInt(temp.length())));
			}
		}
		return cvv.toString();
	}

	private String generatecardNumber() {

		StringBuilder card = new StringBuilder();
		String temp = "1234567890";
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			card.append(temp.charAt(random.nextInt(temp.length())));
		}
		while (isExits(card.toString(), "card")) {
			for (int i = 0; i < 10; i++) {
				card.append(temp.charAt(random.nextInt(temp.length())));
			}

		}

		return card.toString();
	}

	private String generateAccountNumber() {
		StringBuilder acc = new StringBuilder();
		String temp = "1234567890";
		Random random = new Random();
		for (int i = 0; i < 16; i++) {
			acc.append(temp.charAt(random.nextInt(temp.length())));
		}
		while (isExits(acc.toString(), "account")) {
			for (int i = 0; i < 16; i++) {
				acc.append(temp.charAt(random.nextInt(temp.length())));
			}
		}
		return acc.toString();
	}

	private String generateUpi() {

		StringBuilder upi = new StringBuilder();
		String temp = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			upi.append(temp.charAt(random.nextInt(temp.length())));
		}
		while (isExits(upi.toString(), "account")) {
			for (int i = 0; i < 6; i++) {
				upi.append(temp.charAt(random.nextInt(temp.length())));
			}
		}
		return upi.toString();
	}

	private double generateBalance() {
		StringBuilder balance = new StringBuilder();
		String temp = "1234567890";
		Random random = new Random();
		for (int i = 0; i < 5; i++) {
			balance.append(temp.charAt(random.nextInt(temp.length())));
		}

		return Double.parseDouble(balance.toString());

	}

	private boolean isExits(String temp, String key) {

		if (key.equals("cvv")) {
			for (TestingAccount ta : accountList) {
				if (ta.getCvv().equals(temp)) {
					return true;
				}
				return false;
			}
		} else if (key.equals("upi")) {
			for (TestingAccount ta : accountList) {
				if (ta.getupiId().equals(temp)) {
					return true;
				}
				return false;
			}
		} else if (key.equals("account")) {
			for (TestingAccount ta : accountList) {
				if (ta.getAccNo().equals(temp)) {
					return true;
				}
				return false;
			}
		} else if (key.equals("card")) {
			for (TestingAccount ta : accountList) {
				if (ta.getdebitcard().equals(temp)) {
					return true;
				}
				return false;
			}
		}
		return false;

	}
//	public static void logout() {
//		Landing.isLoggedIn=false;
//		Landing.main(null);
//	}

	public Users isValid(String uname, String upass) {

		userDAO userDaoObj = new userDAO();
		Users u = userDaoObj.findUser(uname);
		// System.out.println("Found"+u.isadmin);
		if (u != null && u.getusername().equals(uname) && u.getuserPassword().equals(upass)) {
			walletDAO wallet = new walletDAO();
			Wallet w = wallet.findWallet(u.userId);
			if (w != null) {
				u.setWalletId(w.getWalletId());
			}
			return u;
		} else if (u != null && u.getusername().equals(uname)) {
			this.tempError = "Incorrect Password";
			System.out.println("Incorrect Password");
			return null;
		}

		this.tempError = "User Account not found";
		System.out.println("User Account not found");

		return null;
	}

	public boolean isExists(String uname) {

		userDAO userDaoObj = new userDAO();
		Users u = userDaoObj.findUser(uname);

		if (u != null && u.getusername().equals(uname)) {
			return true;
		}

		return false;
	}

	public static List<Users> getUserList() {
		return null;
	}

	public static Users getUser(long userId) {

		userDAO userDaoObj = new userDAO();
		Users u = userDaoObj.findUser(userId);
		return u;

	}

	public String getError() {
		return this.tempError;
	}

	public TestingAccount getAccount(String username) {

		userDAO user = new userDAO();
		Users userObj = user.findUser(username);
		if (userObj != null) {
			return userObj.getAccount();
		}
		return null;
	}
}
