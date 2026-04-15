package Servlets;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import Servlets.JsonUtility.JsonUtil;
import Servlets.SessionUtil.SessionValidation;
import Servlets.Web.ServerResponse;
import Utilities.userUtilities;
import WalletPackage.TestingAccount;
import WalletPackage.Wallet;
import WalletPackage.WalletManagement;

@WebServlet({ "/mywallet", "/activateWallet", "/openWallet", "/addMoney", "/myAccount", "/addAccountMoney" })
public class walletServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String path = request.getServletPath();
		if (path != null) {

			if (path.equals("/mywallet")) {
				handleWalletGetRequest(request, response);
			} else if (path.equals("/myAccount")) {
				handleAccountGetRequest(request, response);
			}

		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String path = request.getServletPath();
		if (path != null) {
			if (path.equals("/activateWallet")) {
				handleActivateWalletPostRequest(request, response);
			} else if (path.equals("/openWallet")) {
				handleVerifyPassword(request, response);
			} else if (path.equals("/addMoney")) {
				handleAddMoneyPostRequest(request, response);
			} else if (path.equals("/addAccountMoney")) {
				handleAddAccountMoneyPostRequest(request, response);
			}

		}
	}

	private void handleAddAccountMoneyPostRequest(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String username = SessionValidation.isLoggedIn(request, response);
		if (username != null) {
			JsonObject data = JsonUtil.getJsonObject(request);
			double amount = JsonUtil.getJsonDouble("amount", data);
			WalletManagement wmObj = new WalletManagement();

			if (amount > 0) {

				if (wmObj.addMoneyToAccount(username, amount)) {
					ServerResponse.sendResponse(response, 200, "Amount added successfully", null);
				} else {
					ServerResponse.sendResponse(response, 404, "operation failed.", null);
				}
			} else {
				ServerResponse.sendResponse(response, 404, "Invalid Amount.", null);
			}
		}
	}

	private void handleVerifyPassword(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String username = SessionValidation.isLoggedIn(request, response);
		if (username != null) {
			JsonObject data = JsonUtil.getJsonObject(request);
			String password = JsonUtil.getJsonString("Password", data);

			WalletManagement wmObj = new WalletManagement();
			if (wmObj.getOpenWallet(username, password)) {
				ServerResponse.sendResponse(response, 200, "Valid-Password", null);
			} else {
				ServerResponse.sendResponse(response, 404, "Invalid-Password", null);
			}
		}

	}

	private void handleAddMoneyPostRequest(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String username = SessionValidation.isLoggedIn(request, response);
		JsonObject data = JsonUtil.getJsonObject(request);
		JsonObject JsonPaymentData = data.get("data").getAsJsonObject();

		double amount = JsonUtil.getJsonDouble("amount", data);

		if (username != null) {
			if (amount > 0) {
				WalletManagement wmobj = new WalletManagement();
				String message = wmobj.addMoneytoWallet(JsonPaymentData, amount, username);
				if (message.equals("SUCCESSFUL")) {
					ServerResponse.sendResponse(response, 200, message, null);
				} else {
					ServerResponse.sendResponse(response, 404, message, null);
				}
			} else {
				ServerResponse.sendResponse(response, 404, "Invalid Amount", null);
			}
		}

	}

	private void handleWalletGetRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String username = SessionValidation.isLoggedIn(request, response);
		if (username != null) {
			WalletManagement wmObj = new WalletManagement();
			Wallet wallet = wmObj.getWalletObj(username);
			if (wallet != null) {
				ServerResponse.sendResponse(response, 200, "success", wallet);
			} else {
				ServerResponse.sendResponse(response, 404, "Activate-wallet", null);

			}
		}

	}

	private void handleAccountGetRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String username = SessionValidation.isLoggedIn(request, response);
		if (username != null) {

			userUtilities ut = new userUtilities();
			TestingAccount ta = ut.getAccount(username);
			if (ta != null) {
				ServerResponse.sendResponse(response, 200, "Success", ta);
			} else {
				ServerResponse.sendResponse(response, 200, "No Account Found", null);
			}
		}
	}

	private void handleActivateWalletPostRequest(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String username = SessionValidation.isLoggedIn(request, response);
		JsonObject data = JsonUtil.getJsonObject(request);
		String pin = JsonUtil.getJsonString("walletPassword", data);

		String pinvalidation = validatePin(pin);

		if (pinvalidation.equals("VALID")) {
			WalletManagement wmObj = new WalletManagement();
			if (wmObj.createWallet(username, pin) != null) {
				Wallet wallet = wmObj.getWalletObj(username);
				if (wallet != null) {
					ServerResponse.sendResponse(response, 200, "Wallet Created Successfully", wallet);
				}
			} else {
				ServerResponse.sendResponse(response, 404, "Wallet Creation Failed", null);
			}
		} else {
			ServerResponse.sendResponse(response, 404, pinvalidation, null);
		}

	}

	private String validatePin(String pin) {

		if (pin.length() != 4) {
			return "Pin length should be 4";
		}
		String pattern = "^[0-9]+$";
		if (!Pattern.matches(pattern, pin)) {
			return "Pin should contain only numbers";
		}

		return "VALID";
	}

}
