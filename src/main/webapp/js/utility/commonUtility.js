
function getResponseCode(res) {
	if (res != null) {
		if (res.body != null) {
			return res.body.status;
		}

	}
	return 500;
}
function getElementData(name) {
	const ObjectData = document.getElementById(name);
	if (ObjectData != null) {
		return ObjectData.value;
	}
	return "";
}
function getElement(name) {
	const ObjectData = document.getElementById(name);
	return ObjectData;
}
function generalvalidation(field) {
	if (field == null || field == "") {
		return false;
	}
	return true;

}

function popConfirmation(message) {

	const popup = document.getElementById("confirmPopup");
	popup.style.display = "block";

	document.getElementById("confirmMessage").textContent = message;

	return {
		yesButton: document.getElementById("yesButton"),
		noButton: document.getElementById("noButton")
	};
}
function patternValidation(pattern, field) {
	console.log(field);
	console.log(pattern.test(field));
	if (!pattern.test(field)) {
		return false;
	}
	return true;
}
function showMessageFromServer(res) {
	if (getResponseCode(res) == 200) {

		getElement('theatre-success-message').textContent = res.body.message;
		getElement('theatre-error').textContent = "";

	}
	else if (getResponseCode(res) == 404) {
		getElement('theatre-success-message').textContent = "";
		getElement('theatre-error').textContent = res.body.message;
	}
}
function clearMessages() {

	const successMessage = getElement('theatre-success-message');
	const theatreError = getElement('theatre-error');
	const multierror = getElement('multiple-error');
	if (successMessage != null) {
		successMessage.textContent = "";
	}
	if (theatreError != null) {
		theatreError.textContent = "";
	}
	if (multierror != null) {
		multierror.textContent = "";
	}
}
function confirmPopup(message) {

    return new Promise((resolve) => {

        const modal = document.getElementById("confirmModal");
        const msg = document.getElementById("confirmMessage");
        const yesBtn = document.getElementById("confirmYes");
        const noBtn = document.getElementById("confirmNo");

        msg.textContent = message;
        modal.style.display = "flex";

        yesBtn.onclick = null;
        noBtn.onclick = null;

        yesBtn.onclick = () => {
            modal.style.display = "none";
            resolve(true);
        };

        noBtn.onclick = () => {
            modal.style.display = "none";
            resolve(false);
        };
    });
}