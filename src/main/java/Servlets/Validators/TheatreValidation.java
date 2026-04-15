package Servlets.Validators;

import Exceptions.ApplicationException;

public class TheatreValidation {
	
	public static boolean validateTheatreDetails(String theatreName, String theatreLocation, double theatreCredit)
			throws ApplicationException {


		if (!CommonValidation.generalValidation(theatreName)) {
			throw new ApplicationException("Theatre Name cannot be Empty");
		} else if (!CommonValidation.PatternValidation("^[a-zA-Z]+$", theatreName)) {

			throw new ApplicationException("Theatre Name only contains characters");
		}
		if (!CommonValidation.generalValidation(theatreLocation)) {
			throw new ApplicationException("Theatre Location cannot be Empty");

		} else if (!CommonValidation.PatternValidation("^[a-zA-Z]+$", theatreLocation)) {
			throw new ApplicationException("Theatre Location only contains characters");

		}
		if (theatreCredit < 0) {
			throw new ApplicationException("Theatre Credit should be defined greater than or equal to 0");
		}
		return true;
	}

}
