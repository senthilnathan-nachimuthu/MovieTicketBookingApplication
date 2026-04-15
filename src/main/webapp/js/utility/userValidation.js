
function validate() {
	var username = document.getElementById("uname").value;
	var password = document.getElementById("pass").value;
	const Error = document.getElementById("error");
	const pattern = /^[a-zA-Z0-9]+$/;
	const pattern2 = /\s/;
	Error.textContent = "";
	if (username == "" || password == "") {

		Error.textContent = "Username/Password Cannot be Empty";
		return false;
	}
	else if (pattern.test(username) == false) {
		Error.textContent = "Username should not contains any special characters,spaces,numbers. Only contains Characters";
		return false;
	}
	else if (pattern2.test(password)) {
		Error.textContent = "Password should not contains any whitespaces.";
		return false;
	}
	return true;
}

function validateUserDetails() {

	var name = document.getElementById("name").value;
	var age = document.getElementById("age").value;
	var genderInput = document.querySelector('input[name="gender"]:checked');

	const Error = document.getElementById("error");
	const pattern = /^[a-zA-Z\s]+$/;

	if (name == null || name == "") {
		Error.textContent = "Name should not be empty.";
		return false;
	}
	else {

		if (!pattern.test(name)) {
			Error.textContent = "Name should not contain any special characters,numbers.Only contains charcaters. ";
			return false;
		}
	}

	if (age == null || age <= 0 || age > 150) { Error.textContent = "Age should less than 150 and greater than 0."; return false; }


	if (!genderInput) {
		Error.textContent = "Choose Gender";
		return false;
	}
	var gender = genderInput.value;


	if (!pattern.test(gender)) { Error.textContent = "Invalid Gender"; return false; }

	console.log(gender);

	return true;
}

function navigateToHome(res) {

	if (res != null && res.status == 200) {
		if (res.body.status == 200) {
			const jsonString = JSON.stringify(res.body);
			if (res.body.data.isadmin == false) {
				window.location.href = 'userHome.jsp';
			}
			else {
				window.location.href = 'adminHome.jsp';

			}

		}
		else if (res.body.status == 404) {
			error.textContent = "";
			error.textContent = res.body.message;

		}
	}
	else {
		error.textContent = "";
		error.textContent = res.body.message;
	}

}

