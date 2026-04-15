package DAO;

import java.util.Iterator;

import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.movieticketbooking.USERS;
import com.adventnet.movieticketbooking.USER_DETAILS;
import com.adventnet.movieticketbooking.USER_BANK_ACCOUNT;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;

import Exceptions.ApplicationException;
import MickeyDBAccess.CRUDOperation;
import MickeyDBAccess.CRUDUtil;
import Schemas.Users;
import WalletPackage.TestingAccount;

public class userDAO {
	public Users insertUser(String name, String username, String password, String gender, int age, boolean isAdmin) {

		Row row = constructUserInsertRow(name, username, password, isAdmin);
		DataObject dob = CRUDOperation.insertOneRow(USERS.TABLE, row);
		Row result = CRUDUtil.getFirstRow(dob, USERS.TABLE);
		Long userId = ((Number) result.get(USERS.USER_ID)).longValue();
		if (userId > 0) {
			row = constructUserDetailInsertRow(userId, age, gender);
			CRUDOperation.insertOneRow(USER_DETAILS.TABLE, row);
		}
		if (CRUDUtil.DOBValidator(dob)) {
			return findUser(userId);
		}
		return null;
	}

	private Row constructUserDetailInsertRow(Long userId, int age, String gender) {
		Row row = new Row(USER_DETAILS.TABLE);
		row.set(USER_DETAILS.USER_ID, userId);
		row.set(USER_DETAILS.AGE, age);
		row.set(USER_DETAILS.GENDER, gender);
		return row;
	}

	private Row constructUserInsertRow(String name, String username, String password, boolean isAdmin) {
		Row row = new Row(USERS.TABLE);
		row.set(USERS.NAME, name);
		row.set(USERS.USERNAME, username);
		row.set(USERS.PASSWORD, password);
		row.set(USERS.IS_ADMIN, isAdmin);
		return row;
	}

	public Users findUser(String username) {

		Criteria criteria = new Criteria(Column.getColumn(USERS.TABLE, USERS.USERNAME), username, QueryConstants.EQUAL);
		DataObject dob = CRUDOperation.SelectAllColumns(USERS.TABLE, criteria,null);
		if (!CRUDUtil.DOBValidator(dob)) {
			return null;
		}
		return getUserObj2(dob);
	}

	public Users findUser(long userId) {

		Criteria criteria = new Criteria(Column.getColumn(USERS.TABLE, USERS.USER_ID), userId, QueryConstants.EQUAL);
		DataObject dob = CRUDOperation.SelectAllColumns(USERS.TABLE, criteria,null);
		if (!CRUDUtil.DOBValidator(dob)) {
			return null;
		}
		return getUserObj2(dob);
	}

	public boolean insertBankAccount(long user_id, double balance, String upiId, String accNo, String ifsc,
			String cardNumber, String cvv) {

		Row row = constructUserBankAccount(user_id, balance, upiId, accNo, ifsc, cardNumber, cvv);
		DataObject dob = CRUDOperation.insertOneRow(USER_BANK_ACCOUNT.TABLE, row);
		return CRUDUtil.DOBValidator(dob);
	}

	private Row constructUserBankAccount(long user_id, double balance, String upiId, String accNo, String ifsc,
			String cardNumber, String cvv) {
		Row row = new Row(USER_BANK_ACCOUNT.TABLE);
		row.set(USER_BANK_ACCOUNT.USER_ID, user_id);
		row.set(USER_BANK_ACCOUNT.BALANCE, balance);
		row.set(USER_BANK_ACCOUNT.UPI_ID, upiId);
		row.set(USER_BANK_ACCOUNT.ACCOUNT_NUMBER, accNo);
		row.set(USER_BANK_ACCOUNT.IFSC_CODE, ifsc);
		row.set(USER_BANK_ACCOUNT.CARD_NUMBER, cardNumber);
		row.set(USER_BANK_ACCOUNT.CVV, cvv);
		return row;
	}

	public TestingAccount findBankAccount(long userId) {

		Criteria criteria = new Criteria(Column.getColumn(USER_BANK_ACCOUNT.TABLE, USER_BANK_ACCOUNT.USER_ID), userId,
				QueryConstants.EQUAL);
		DataObject dob = CRUDOperation.SelectAllColumns(USER_BANK_ACCOUNT.TABLE, criteria,null);
		if (!CRUDUtil.DOBValidator(dob)) {
			return null;
		}
		return getUserBankAccountObj(dob);
	}

	public boolean withDraw(long userId, double amount) {

		TestingAccount account = findBankAccount(userId);
		if (account != null) {
			double currentBalance = account.getBalance();
			if (currentBalance >= amount) {
				double updatedValue = currentBalance - amount;
				Criteria criteria = new Criteria(Column.getColumn(USER_BANK_ACCOUNT.TABLE, USER_BANK_ACCOUNT.USER_ID),
						userId, QueryConstants.EQUAL);
				DataObject dob = CRUDOperation.updateSingleColumn(USER_BANK_ACCOUNT.TABLE, criteria,
						USER_BANK_ACCOUNT.BALANCE, updatedValue);
				return CRUDUtil.DOBValidator(dob);
			}
			return false;
		}
		throw new ApplicationException("Account Not Found");

	}
	public boolean creditAmount(long userId, double amount) {

		TestingAccount account = findBankAccount(userId);
		if (account != null) {
			double currentBalance = account.getBalance();
			double updatedValue = currentBalance + amount;
			Criteria criteria = new Criteria(Column.getColumn(USER_BANK_ACCOUNT.TABLE, USER_BANK_ACCOUNT.USER_ID),
					userId, QueryConstants.EQUAL);
			DataObject dob = CRUDOperation.updateSingleColumn(USER_BANK_ACCOUNT.TABLE, criteria,
					USER_BANK_ACCOUNT.BALANCE, updatedValue);
			return CRUDUtil.DOBValidator(dob);
		}
		throw new ApplicationException("Account Not Found");

	}

	private Users getUserObj2(DataObject dob) {

		try {
			Iterator<Row> itr = dob.getRows(USERS.TABLE);

			if (itr.hasNext()) {
				Row row = itr.next();

				long userId = ((Number) row.get(USERS.USER_ID)).longValue();
				String name = (String) row.get(USERS.NAME);
				String username = (String) row.get(USERS.USERNAME);
				String password = (String) row.get(USERS.PASSWORD);
				boolean isAdmin = (Boolean) row.get(USERS.IS_ADMIN);

				return new Users(userId, name, username, password, isAdmin);
			}

		} catch (DataAccessException e) {
			throw new RuntimeException("Error while mapping user object", e);
		}

		return null;
	}

	private TestingAccount getUserBankAccountObj(DataObject dob) {

		try {
			Iterator<Row> itr = dob.getRows(USER_BANK_ACCOUNT.TABLE);
			if (itr.hasNext()) {
				Row row = itr.next();

				double balance = ((Number) row.get(USER_BANK_ACCOUNT.BALANCE)).doubleValue();
				String accNo = (String) row.get(USER_BANK_ACCOUNT.ACCOUNT_NUMBER);
				String ifsc = (String) row.get(USER_BANK_ACCOUNT.IFSC_CODE);
				String upi = (String) row.get(USER_BANK_ACCOUNT.UPI_ID);
				String card = (String) row.get(USER_BANK_ACCOUNT.CARD_NUMBER);
				String cvv = (String) row.get(USER_BANK_ACCOUNT.CVV);

				return new TestingAccount(balance, accNo, ifsc, upi, card, cvv);
			}

		} catch (DataAccessException e) {
			throw new RuntimeException("Error while mapping bank account object", e);
		}

		return null;
	}

}
