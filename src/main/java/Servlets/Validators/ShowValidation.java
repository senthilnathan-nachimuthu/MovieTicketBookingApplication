package Servlets.Validators;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import Exceptions.ApplicationException;

public class ShowValidation {

	public static boolean validateShowDetails(LocalDate fromDate, LocalDate toDate, Map<Long, List<LocalTime>> screenList,
			String language) {

		if (fromDate == null) {
			throw new ApplicationException("Invalid FromDate");
		}
		if (toDate == null) {
			throw new ApplicationException("Invalid ToDate");
		}
		if (screenList.isEmpty()) {
			throw new ApplicationException("No screens Choosen");
		}
		if (!CommonValidation.PatternValidation("^[a-zA-Z]+$", language)) {
			throw new ApplicationException("Invalid Language");
		}
		return true;
	}
}
