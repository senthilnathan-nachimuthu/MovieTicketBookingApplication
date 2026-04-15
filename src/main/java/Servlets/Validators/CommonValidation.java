package Servlets.Validators;

import java.util.regex.Pattern;

public class CommonValidation {

	public static boolean generalValidation(String field) {
		if (field == null || field.isEmpty()) {
			return false;
		}
		return true;
	}

	public static boolean PatternValidation(String pattern, String field) {

		if (Pattern.matches(pattern, field)) {
			return true;
		}
		return false;
	}
}
