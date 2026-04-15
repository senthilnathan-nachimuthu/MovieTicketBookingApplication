package Schemas;

import DAO.userDAO;
import DAO.walletDAO;
import WalletPackage.TestingAccount;
import WalletPackage.Wallet;
import WalletPackage.WalletManagement;

public class Users {
	public long userId;
	private String name;
	private String gender;
	private int age;
	private String username;
	private String password;
	public boolean isadmin;
	private String walletId;
	private TestingAccount accountObj;

//	public Users(int userId,String name, String gender, int age, String u, String p) {
//		this.userId=userId;
//		this.name = name;
//		this.gender = gender;
//		this.age = age;
//		this.username = u;
//		this.password = p;
//		
//	}

	public Users(long userId2, String name2, String uname, String password2, boolean isAdmin2) {

		this.userId = userId2;
		this.name = name2;
		this.username = uname;
		this.password = password2;
		this.isadmin = isAdmin2;
	}

	public boolean activateWallet(String walletPassword) {
		WalletManagement wm = new WalletManagement();
		if (isadmin == false && walletId == null) {
			this.walletId = wm.createWallet(username, walletPassword);
			if (walletId.equals("")) {
				return false;
			}
			return true;
		}
		return false;
	}

	public String getWalletId() {
		walletDAO wallet = new walletDAO();
		Wallet walletObj = wallet.findWallet(userId);
		String walletId2 = "";
		if (walletObj != null) {
			walletId2 = walletObj.getWalletId();
		}
		return walletId2;

	}

	public void setAdmin() {
		isadmin = true;
	}

	public String getusername() {
		return this.username;
	}

	public String getuserPassword() {
		return this.password;
	}

	public String getName() {
		return this.name;
	}

	public int getAge() {
		return this.age;
	}

	public String getGender() {
		return this.gender;
	}

	public boolean isActivatedWallet() {

		if (walletId != null) {
			return true;
		}

		return false;
	}

	public void setAccount(TestingAccount taObj) {
		this.accountObj = taObj;

	}

	public TestingAccount getAccount() {

		userDAO userDaoObj = new userDAO();

		return userDaoObj.findBankAccount(userId);

	}

	public void setWalletId(String walletId2) {

		this.walletId = walletId2;

	}

}
