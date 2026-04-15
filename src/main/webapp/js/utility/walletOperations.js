
let isWalletOpened = false;
function getBalance() {

	getWalletView();


	fetch("/mywallet")
		.then(response => {

			if (!response.ok) {
				throw new Error("Bad request." + response.status);
			}
			return response.json();
		})
		.then(res => {

			if (res.status == 200 && res.data != null) {
				renderWallet(res.data);
			}
			else if (res.status == 404) {
				console.log(res.status);
				renderActivateWallet();
			}


		})
		.catch(error => {

		});

}
function getWalletView() {

	fetch("myWallet.html")
		.then(res => res.text())
		.then(html => {
			document.getElementById("container").innerHTML = html;
		});

};
async function handleActivate(event) {
	event.preventDefault();

	if (!ActivationValidation()) return;

	await activateWallet();
}


async function activateWallet() {
	const pass = document.getElementById("walletPassword").value;
	const data = { walletPassword: pass };

	const res = await fetchData("/activateWallet", "POST", data);

	console.log(res);
	if (res.status == 200) {
		const obj = res.body;
		if (obj != null && obj.status == 200) {
			const data = obj.data;
			if (data != null) {
				//document.getElementById("message").textContent = obj.message;
				getBalance();
				console.log(data);
			}

		}
		else {
			document.getElementById("message").textContent = obj.message;
		}

	}

}
function renderWallet(wallet) {
	activateView.hidden = true;
	walletView.hidden = false;
	walletActivation.hidden = true;

	document.getElementById("balance").textContent = wallet.walletAmount;
	document.getElementById("credits").textContent = wallet.creditPoints;

	console.log(balance);
	console.log(document.getElementById("balance"));
	//balancee.textContent = wallet.walletAmount;
	//credits.textContent = wallet.creditPoints;
}

function renderActivateWallet() {

	container.hidden = false;
	fetch("activateWallet.html")
		.then(res => res.text())
		.then(html => {
			document.getElementById("container").innerHTML = html;
		});


}

function RenderPassword() {
	walletActivation.hidden = false;
}
function renderID(id) {

	id.hidden = false;

}
function disableRender(id) {
	id.hidden = true;
}

function popPasswordContainer() {
	
	if (isWalletOpened == false) {
		popPassword();
	}
	else {

		getBalance();
	}

}

async function popPassword() {

	document.getElementById("message-account").textContent = "";
	//console.log(message-content);
	const res = await fetchGetData("/mywallet");
	//console.log(res);
	if (res.status == 200) {

		container.hidden = false;
		await fetch("walletPasswordModule.html")
			.then(res => res.text())
			.then(html => {
				document.getElementById("container").innerHTML = html;
			});

		renderID(passConfirmButton);
	}
	else if (res.status == 404) {

		console.log("hello");
		renderActivateWallet();
	}
}

function renderPayment(value) {
	console.log(value);
	if (value === "upimoney") {
		console.log("upi");
		upiInput.hidden = false;
		cardInput.hidden = true;
		netInput.hidden = true;
	}
	else if (value === "cardmoney") {
		upiInput.hidden = true;
		cardInput.hidden = false;
		netInput.hidden = true;
	}
	else if (value === "netmoney") {
		upiInput.hidden = true;
		cardInput.hidden = true;
		netInput.hidden = false;

	}

}
function ActivationValidation() {
	var pin = document.getElementById("walletPassword").value;
	const error = document.getElementById("Error");
	error.textContent = "";
	if (pin.length != 4) {
		error.textContent = "Invalid Pin, Enter 4 Digit Pin";
		return false;
	}
	const pattern = /^[0-9]+$/;
	if (!pattern.test(pin)) {
		error.textContent = "Invalid Pin, Enter Number Only.";
		return false;

	}
	error.textContent = "";

	return true;
}


async function postAmount(data, amount) {
	const jsonData = { data, amount: amount };
	//console.log(jsonData);
	const res = await fetchData("/addMoney", "POST", jsonData);
	if (res != null) {

		if (res.body.status == 200) {
			//document.getElementById("message-account").textContent = "Amount Successfully added to wallet.";
			getBalance();
		}
		else {

			document.getElementById("message-account").textContent = res.body.message;

		}
	}
	console.log(res);

}
function handleAddMoney(event) {
	event.preventDefault();
	document.getElementById("message-account").textContent = "";
	var ch = document.getElementById("payment").value;
	console.log(ch);


	if (ch === "upimoney") {

		const amount = document.getElementById("amountbox1").value;
		const upiId = getElementData("upibox");
		const paymentData = { method: "UPI", upiId: upiId };
		//console.log(paymentData);
		//console.log(amount);

		postAmount(paymentData, amount);

	}
	else if (ch === "netmoney") {
		const amount = document.getElementById("amountbox2").value;
		console.log(amount);

		const accNo = getElementData("accnobox");
		const ifsc = getElementData("ifscbox");
		const paymentData = { method: "NET", accountNumber: accNo, ifsc: ifsc };
		postAmount(paymentData, amount);

	}
	else if (ch === "cardmoney") {
		const amount = document.getElementById("amountbox3").value;
		console.log(amount);

		const cardNo = getElementData("carnobox");
		const cvv = getElementData("cvvbox");
		const paymentData = { method: "CARD", cardnumber: cardNo, cvv: cvv };
		postAmount(paymentData, amount);

	}

}

function getElementData(id) {
	return document.getElementById(id).value;

}
async function verifyPassword() {
	var password = getElementData("walletPassword");
	const data = { Password: password };
	//console.log(data);
	const res = await fetchData("/openWallet", "POST", data);
	console.log(res);
	if (res != null) {
		console.log(res.body.status);
		if (res.body.status == 200) {

			return true;

		}
		else {
			return false;
		}
	}
}


async function handlePassword(event, id) {

	console.log("handle");
	event.preventDefault();
	if (!ActivationValidation()) {
		return false;
	}

	document.getElementById("Error").textContent = "Verifying password...";
	var password = getElementData("walletPassword");
	const data = { Password: password };
	//console.log(data);
	const res = await fetchData("/openWallet", "POST", data);
	document.getElementById("Error").textContent = "";

	console.log(res);
	if (res != null) {
		console.log(res.body.status);
		if (res.body.status == 200) {
			isWalletOpened = true;
			//disableRender(container);
			disableRender(passConfirmButton);
			//renderID(id);
			getBalance();
		}
		else {
			document.getElementById("Error").textContent = res.body.message;

			disableRender(id);
			console.log('INVALID');
		}
	}

}

async function getAccount() {

	getElement("passConfirmButton").hidden=true;
	container.hidden = false;
	await fetch("myAccount.html")
		.then(res => res.text())
		.then(html => {
			document.getElementById("container").innerHTML = html;
		});

	getAccountBalance();
}
async function getAccountBalance() {
	const res = await fetchGetData("/myAccount");
	console.log(res.data);
	document.getElementById("accbalance").textContent = res.data.balance;
	document.getElementById("upiId").textContent = res.data.upiId;
	document.getElementById("accNum").textContent = res.data.accNo;
	document.getElementById("ifscId").textContent = res.data.ifscCode;
	document.getElementById("cardNo").textContent = res.data.debitCardNumber;
	document.getElementById("cvv").textContent = res.data.cvv;
}

async function handleAccountAddMoney() {

	const data = { amount: document.getElementById("accAmount").value };
	const res = await fetchData("/addAccountMoney", "POST", data);
	console.log(res);
	document.getElementById("acc-message").textContent = res.body.message;

}






