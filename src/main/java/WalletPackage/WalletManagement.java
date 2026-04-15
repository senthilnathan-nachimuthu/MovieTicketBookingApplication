package WalletPackage;

import java.util.Random;

import com.google.gson.JsonObject;

import DAO.userDAO;
import DAO.walletDAO;
import Exceptions.ApplicationException;
import Schemas.Users;

public class WalletManagement {

	public Wallet getWalletObj(String username) {

		Users userObj = getUserObj(username);
		if (userObj != null) {
			if (!userObj.getWalletId().equals("")) {

				double balance = showWalletBalance(userObj);
				double credits = showCredits(userObj);
				double pending = getPendingCredit(userObj);
				return new Wallet(userObj.userId, balance, credits, pending);
			}

		}

		return null;
	}

	public boolean getOpenWallet(String username, String pass) {

		Users userObj = getUserObj(username);
		if (userObj != null) {
			return openWallet(userObj, pass);
		}
		return false;
	}

	public boolean addMoneyToAccount(String username, double amount) {
		Users userObj = getUserObj(username);
		if (userObj != null) {
			userDAO user = new userDAO();
			return user.creditAmount(userObj.userId, amount);
		}
		return false;
	}

	public String addMoneytoWallet(JsonObject jsonPaymentData, double amount, String username) {

		Users userObj = getUserObj(username);

		String method = jsonPaymentData.get("method").getAsString();

		if (userObj != null) {
			TestingAccount ta = userObj.getAccount();

			if (ta.getBalance() < amount) {
				return "Insufficient Account Balance";
			}
			if (method.equals("UPI")) {
				String upiId = jsonPaymentData.get("upiId").getAsString();
				if (ta.getupiId().equals(upiId)) {
					if (withDrawAmount(ta, amount, userObj)) {
						return "SUCCESSFUL";
					}
				} else {
					return "Invalid UPI ID";
				}
			} else if (method.equals("CARD")) {
				String cardNo = jsonPaymentData.get("cardnumber").getAsString();
				String cvv = jsonPaymentData.get("cvv").getAsString();

				if (ta.getdebitcard().equals(cardNo) && ta.getCvv().equals(cvv)) {
					if (withDrawAmount(ta, amount, userObj)) {
						return "SUCCESSFUL";
					}
				} else {
					return "Invalid CardNo/Cvv";
				}
			} else if (method.equals("NET")) {
				String Accno = jsonPaymentData.get("accountNumber").getAsString();
				String ifsc = jsonPaymentData.get("ifsc").getAsString();

				if (ta.getAccNo().equals(Accno) && ta.getIfsc().equals(ifsc)) {
					if (withDrawAmount(ta, amount, userObj)) {
						return "SUCCESSFUL";
					}
				} else {
					return "Invalid Account Number/IFSC";
				}
			}
		}
		return "Operation failed,Money not added.";
	}

	public boolean withDrawAmount(TestingAccount ta, double amount, Users userObj) {

		if (ta.getBalance() > amount) {
			userDAO user = new userDAO();
			user.withDraw(userObj.userId, amount);
			walletDAO wallet = new walletDAO();
			Wallet walletObj=wallet.findWallet(userObj.userId);
			wallet.updateWalletAmount(walletObj.walletAmount+amount, userObj.getWalletId());
			return true;
		} else {
			return false;
		}
	}

	public Users getUserObj(String username) {
		userDAO userDaoObj = new userDAO();
		Users userObj = userDaoObj.findUser(username);
		return userObj;
	}

	public String createWallet(String username, String walletPassword) {

		userDAO userDaoObj = new userDAO();
		Users userObj = userDaoObj.findUser(username);
		long userId = userObj.userId;
		String walletId = generateWalletId();
		walletDAO walletDaoObj = new walletDAO();
		while (walletDaoObj.findWalletByWalletId(walletId) != null) {
			walletId = generateWalletId();
		}
		walletDaoObj.insertWallet(walletId, userId, walletPassword);
		return walletId;

	}

	private String generateWalletId() {

		String walletChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 5; i++) {
			char c = walletChar.charAt(random.nextInt(walletChar.length()));
			sb.append(c);
		}
		return sb.toString();
	}

	public boolean payThroughWallet(double amount, Users userObj) {

		if (authenticate(userObj)) {
			walletDAO wallet = new walletDAO();
			Wallet walletObj = wallet.findWallet(userObj.userId);
			if (walletObj.getWalletBalance() >= amount) {
				return wallet.updateWalletAmount(walletObj.getWalletBalance() - amount, userObj.getWalletId());
			} else {
				throw new ApplicationException("Insufficient Wallet Balance.Add Money to Wallet.");
			}

		} else {
			throw new ApplicationException("Wallet Not Found.");
		}
	}

	public boolean addCredit(Users userObj, double credits) {
		if (authenticate(userObj)) {
			walletDAO wallet = new walletDAO();
			Wallet walletObj = wallet.findWallet(userObj.userId);
			double NewCredits = walletObj.getCreditPoints()+credits;
			System.out.println("Credits added " );
			return wallet.updateCredit(NewCredits, walletObj.getWalletId());

		} else {
			System.out.println("Wallet Not found.!!");
		}
		return false;

	}

	public double showWalletBalance(Users userobj) {

		double balance = 0;
		if (authenticate(userobj)) {
			walletDAO wallet = new walletDAO();
			Wallet walletObj = wallet.findWallet(userobj.userId);
			balance = walletObj.getWalletBalance();
		} else {
			System.out.println("Wallet Not found.!!!!");
		}
		return balance;
	}

	public double showCredits(Users userobj) {

		double credits = 0;
		if (authenticate(userobj)) {
			walletDAO wallet = new walletDAO();
			Wallet walletObj = wallet.findWallet(userobj.userId);
			credits = walletObj.getCredits();
		} else {
			System.out.println("Wallet Not found.!!!!");
		}
		return credits;
	}

	public boolean addMoney(double amount, Users userobj, String walletPassword) {

		if (authenticate(userobj)) {
			if (openWallet(userobj, walletPassword)) {
				walletDAO wallet = new walletDAO();
				Wallet walletObj = wallet.findWallet(userobj.userId);
				return wallet.updateWalletAmount(walletObj.getWalletAmount() + amount, userobj.getWalletId());
			} else {
				throw new ApplicationException("Wrong Wallet Password.!!!!!");
			}

		}
		return false;

	}

	public boolean openWallet(Users userObj, String walletPassword) {

		walletDAO wallet = new walletDAO();
		Wallet walletObj = wallet.findWallet(userObj.userId);
		if (walletObj.verifyPassword(walletPassword)) {
			return true;

		}
		return false;

	}

	public boolean authenticate(Users userobj) {
		String userWalletId = userobj.getWalletId();
		walletDAO wallet = new walletDAO();
		Wallet obj = wallet.findWallet(userobj.userId);
		if (userWalletId.equals(obj.getWalletId())) {
			return true;
		}

		return false;
	}

	public boolean refundWalletBalance(Users userObj, double refundAmount) {

		if (authenticate(userObj)) {
			walletDAO wallet = new walletDAO();
			Wallet walletObj = wallet.findWallet(userObj.userId);
			wallet.updateWalletAmount(walletObj.getWalletAmount() + refundAmount, walletObj.getWalletId());
			return true;
		} else {
			throw new ApplicationException("Wallet Not Found, Refund Failed");
		}
	}

//	public boolean recoverCredit(Users userObj, double recoveredCredit) {
//
//		if (authenticate(userObj)) {
//			walletDAO wallet = new walletDAO();
//			Wallet walletObj = wallet.findWallet(userObj.userId);
//			if (walletObj.getCredits() < recoveredCredit) {
//				double pendingCredits = walletObj.getPendingcredits();
//				pendingCredits += recoveredCredit;
//				walletObj.setPendingCredit(pendingCredits);
//				//wallet.addPendingCredits(pendingCredits, walletObj.getWalletId());
//				System.out.println(
//						"Credit Points Already Redeemed.So you cannot get credit points for some future bookings.!!!!");
//				return true;
//			} else {
//				wallet.reduceCreditPoints(recoveredCredit, userObj.getWalletId());
//			}
//			return true;
//		} else {
//			System.out.println("Wallet Not found.!!!!");
//		}
//		return false;
//	}

	public boolean reedemMoney(Users userObj, String walletPassword, double redeemAmount) {

		if (authenticate(userObj)) {
			if (openWallet(userObj, walletPassword)) {
				walletDAO wallet = new walletDAO();
				Wallet walletObj = wallet.findWallet(userObj.userId);
				if (walletObj.getWalletBalance() >= redeemAmount) {
					return wallet.updateWalletAmount(walletObj.getWalletBalance() - redeemAmount,
							walletObj.getWalletId());
				} else {
					System.out.println("Insufficient Wallet Balance.Redemption Failed.!!!!");
					return false;
				}
			} else {
				System.out.println("Wrong Wallet Password!!!!");
			}
		} else {
			System.out.println("Wallet Not found.!!!!");
		}
		return false;
	}

	public double getPendingCredit(Users userObj) {

		if (authenticate(userObj)) {
			walletDAO wallet = new walletDAO();
			Wallet w = wallet.findWallet(userObj.userId);
			return w.getPendingcredits();

		} else {
			System.out.println("Wallet Not found.!!!!");
		}
		return 0;

	}

}
