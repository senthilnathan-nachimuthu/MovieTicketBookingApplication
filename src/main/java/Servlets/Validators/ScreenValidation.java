package Servlets.Validators;

import java.time.LocalTime;
import java.util.List;

import Exceptions.ApplicationException;
import Schemas.Pricing;
import userInteraction.seat_Structure;

public class ScreenValidation {


	public static boolean validateScreenDetails(List<seat_Structure> seatList, List<Pricing> list, String screenType,
			List<LocalTime> showTime, String screenName) {
		if (!CommonValidation.PatternValidation("^[a-zA-Z0-9]+$", screenType)) {
			throw new ApplicationException(
					"Invalid ScreenType detail.ScreenType should not contain special Characters.");
		}
		if (!CommonValidation.PatternValidation("^[a-zA-Z0-9]+$", screenName)) {
			throw new ApplicationException("Invalid ScreenName.ScreenName should not contain special Characters.");
		}
		if (seatList == null || seatList.isEmpty() || screenType.equals("") || screenType == null || showTime == null
				|| showTime.isEmpty() || list == null || list.isEmpty()) {
			throw new ApplicationException("Invalid updation details found,updation failed");
		}

		return true;
	}
}
