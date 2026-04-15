package Servlets.Validators;

import java.util.regex.Pattern;

public class UserDataValidation {
	public  static String validateUsernamePassword(String username, String password) {

		if (username == null || username.equals("")) {

			return "Username cannot be Empty.";
		}
		if (password == null || password.equals("")) {

			return "Password cannot be Empty.";
		}

		String pattern = "^[a-zA-Z0-9]+$";
		if (!Pattern.matches(pattern, username)) {
			return "username does not contain any special characters";
		}
		if (password.contains(" ")) {
			return "Password does not contain spaces";
		}
		return "VALID";
	}

	public static String validateUserDetails(String name, int age, String gender) {

		if (name == null || name.equals("")) {
			return "Name cannot be empty";
		} else {
			String pattern = "^[a-zA_Z]+$";
			if (!Pattern.matches(pattern, name)) {
				return "Name does not contain any special characters,numbers";
			}
		}

		if (age <= 0 || age > 150) {
			return "Age should be greater than 0 and less than 150";
		}

		if (gender == null || gender.equals("")) {
			return "Gender cannot be empty";
		} else {
			String pattern = "^[a-zA_Z]+$";
			if (!Pattern.matches(pattern, gender)) {
				return "Gender does not contain any special characters";
			}
		}

		return "VALID";
	}

}
