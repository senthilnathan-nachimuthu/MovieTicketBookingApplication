package DAO;

import java.util.Iterator;

import com.adventnet.ds.query.Column;
import com.adventnet.movieticketbooking.USER_WALLET;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;

import Exceptions.ApplicationException;
import MickeyDBAccess.CRUDOperation;
import MickeyDBAccess.CRUDUtil;
import WalletPackage.Wallet;

public class walletDAO {

	public boolean insertWallet(String walletId, long userId, String walletPassword) {
		Wallet walletObj = findWallet(userId);
		if (walletObj != null) {
			throw new ApplicationException("User Already has wallet.");
		}
		Row row = constructWalletInsertRow(walletId, userId, walletPassword);
		DataObject dob = CRUDOperation.insertOneRow(USER_WALLET.TABLE, row);
		return CRUDUtil.DOBValidator(dob);
	}

	private Row constructWalletInsertRow(String walletId, long userId, String walletPassword) {
		Row row = new Row(USER_WALLET.TABLE);
		row.set(USER_WALLET.WALLET_ID, walletId);
		row.set(USER_WALLET.USER_ID, userId);
		row.set(USER_WALLET.WALLET_PASSWORD, walletPassword);
		return row;
	}

	public Wallet findWallet(long userId) {
		Criteria criteria = new Criteria(Column.getColumn(USER_WALLET.TABLE, USER_WALLET.USER_ID), userId,
				QueryConstants.EQUAL);
		DataObject dob = CRUDOperation.SelectAllColumns(USER_WALLET.TABLE, criteria, null);
		if (CRUDUtil.DOBValidator(dob)) {
			return getWalletObject(dob);
		}
		return null;
	}

	public Wallet findWalletByWalletId(String walletId) {

		Criteria criteria = getWalletCriteria(walletId);
		DataObject dob = CRUDOperation.SelectAllColumns(USER_WALLET.TABLE, criteria, null);
		if (CRUDUtil.DOBValidator(dob)) {
			return getWalletObject(dob);
		}
		return null;
	}

	public boolean updateWalletAmount(double amount, String walletId) {

		Criteria criteria = getWalletCriteria(walletId);
		DataObject dob = CRUDOperation.updateSingleColumn(USER_WALLET.TABLE, criteria, USER_WALLET.WALLET_AMOUNT,
				amount);
		if (CRUDUtil.DOBValidator(dob)) {
			return true;
		}
		return false;
	}

	public boolean updateCredit(double credits, String walletId) {
		Criteria criteria = getWalletCriteria(walletId);
		DataObject dob = CRUDOperation.updateSingleColumn(USER_WALLET.TABLE, criteria, USER_WALLET.CREDIT_POINTS,
				credits);
		return CRUDUtil.DOBValidator(dob);
	}

	public Criteria getWalletCriteria(String walletId) {
		Criteria criteria = new Criteria(Column.getColumn(USER_WALLET.TABLE, USER_WALLET.WALLET_ID), walletId,
				QueryConstants.EQUAL);
		return criteria;
	}

	public Wallet getWalletObject(DataObject dob) {
		try {
			Iterator<Row> rows = dob.getRows(USER_WALLET.TABLE);
			if (rows.hasNext()) {
				Row row = rows.next();

				String walletId = (String) row.get(USER_WALLET.WALLET_ID);
				long userId = ((Number) row.get(USER_WALLET.USER_ID)).longValue();
				String walletPassword = (String) row.get(USER_WALLET.WALLET_PASSWORD);

				double amount = ((Number) row.get(USER_WALLET.WALLET_AMOUNT)).doubleValue();
				double credits = ((Number) row.get(USER_WALLET.CREDIT_POINTS)).doubleValue();
				double pendingCredits = ((Number) row.get(USER_WALLET.PENDING_CREDITS)).doubleValue();

				Wallet wallet = new Wallet(walletId, userId, walletPassword);
				wallet.setWalletAmount(amount);
				wallet.setCreditPoints(credits);
				wallet.setPendingCredit(pendingCredits);

				return wallet;
			}

		} catch (Exception e) {
			throw new RuntimeException("Error while mapping wallet object", e);
		}

		return null;
	}
}
