package userInteraction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import Schemas.Pricing;
import Schemas.Screen;
import Schemas.Show;

public class InputHandler {

	public Scanner s = new Scanner(System.in);

	public int readIntInput() {
		try {
			int a = s.nextInt();
			return a;
		} catch (InputMismatchException e) {
			System.out.println("Wront input.Enter Integer value");
			return -1;
		}

	}

	public String readStringInput() {
		try {
			String a = s.nextLine();
			return a;
		} catch (InputMismatchException e) {
			System.out.println("Wront input.Enter String value");
			return "";
		}

	}

	public String mreadStringInput(String message) {
		System.out.println(message);
		try {
			String a = s.nextLine();
			return a;
		} catch (InputMismatchException e) {
			System.out.println("Wront input.Enter String value");
			return "";
		}
	}

	public double mreadDoubleInput(String message) {
		System.out.println(message);
		try {
			double a = s.nextDouble();
			while (a <= 0) {
				System.out.println("Value should greater than 0.Enter again");
				a = s.nextDouble();
			}
			s.nextLine();
			return a;
		} catch (InputMismatchException e) {
			System.out.println("Wront input.Enter double value");
			return 0;
		}
	}

	public LocalDate getFromDate() {
		System.out.println("Enter From date.");
		String date = readStringInput();
		LocalDate temp = getLocalDate(date);
		while (temp == null) {
			System.out.println("Re-Enter From date");
			date = readStringInput();
			temp = getLocalDate(date);
		}
		return temp;
	}

	public LocalDate getToDate() {
		System.out.println("Enter To date.");
		String date = readStringInput();
		LocalDate temp = getLocalDate(date);
		while (temp == null) {
			System.out.println("Re-Enter To date");
			date = readStringInput();
			temp = getLocalDate(date);
		}
		return temp;
	}

	public List<LocalTime> getTimeList() {
		System.out.println("Enter Number of Show to Add.");
		int count = readIntInput();
		s.nextLine();
		List<LocalTime> timeList = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			System.out.println("Enter Time:" + i);
			String time = readStringInput();
			LocalTime localtime = getLocalTime(time);
			while (localtime == null) {
				System.out.println("Re-Enter Time" + i);
				time = readStringInput();
				localtime = getLocalTime(time);
			}
			timeList.add(localtime);
		}
		return timeList;
	}

	public String getMovieName() {
		System.out.println("Enter the movie name");
		String movName = readStringInput();
		return movName.toLowerCase();
	}

	public String getMovieLanguage() {

		System.out.println("Enter the movie Language");
		String lang = readStringInput();
		return lang.toLowerCase();
	}

	public double getMovieDuration() {
		System.out.println("Enter the movie duration in minutes");

		double duration = s.nextFloat();
		s.nextLine();
		while (duration < 0 || duration > 1440) {
			System.err.println("Duration Must be greater than 0 && Less Than 1440 \nRe-Enter Duration");
			duration = s.nextFloat();
			s.nextLine();

		}
		return duration;
	}

	public int getTheatreCount() {
		System.out.println("Enter number of Theatres to add.");
		int numberOfTheatres = readIntInput();
		s.nextLine();
		return numberOfTheatres;
	}

	public String getTheatreName() {

		System.out.println("Enter the theatre name:");
		return s.nextLine().toLowerCase();
	}

	public int getScreenCount() {
		System.out.println("Enter total screens to add.");
		int totalscreens = readIntInput();
		s.nextLine();
		return totalscreens;
	}

//	public int getSeatCapacity() {
//		System.out.println("Enter the Seat Capacity");
//		int seatcap = readIntInput();
//		s.nextLine();
//
//		return seatcap;
//	}

	public int chooseScreen() {

		System.out.println("Choose Screen.");

		int screenChoice = readIntInput();
		s.nextLine();
		return screenChoice;
	}

	public int chooseMovie() {
		System.out.println("Enter Movie No");

		int movieChoice = readIntInput();
		s.nextLine();
		return movieChoice;
	}

	public int continueChoice() {
		System.out.println("Enter 1.Continue 2. Exit");

		int continueChoice = readIntInput();
		s.nextLine();
		return continueChoice;
	}

	public int theatreChoice() {
		System.out.println("Choose Theatre");

		int theatreChoice = readIntInput();
		s.nextLine();
		return theatreChoice;
	}

	public int deleteChoice() {
		System.out.println("Do you want to delete \n1.YES\n2.NO");

		int deleteChoice = readIntInput();
		s.nextLine();
		return deleteChoice;
	}

	public String getScreenType() {
		String screenType = "";
		while (screenType.equals("")) {
			System.out.println("Enter Screen Type.");
			screenType = readStringInput();

		}
		return screenType.toLowerCase();
	}

	public LocalTime getLocalTime(String time) {
		DateTimeFormatter lt = DateTimeFormatter.ofPattern("ha", Locale.ENGLISH);
		LocalTime localtime = null;

		try {
			localtime = LocalTime.parse(time, lt);

		} catch (java.time.format.DateTimeParseException e) {
			System.out.println("Invalid Format");
			return null;
		}
		return localtime;
	}

	public LocalDate getLocalDate(String date) {
		DateTimeFormatter dt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate localdate = null;
		try {
			localdate = LocalDate.parse(date, dt);
		} catch (java.time.format.DateTimeParseException e) {
			System.out.println("Invalid Format");
			return null;

		}
		return localdate;
	}

	public int chooseUpdateOption() {
		System.out.println(
				"Theatre-Update-options\n1.Remove-Theatre\n2.Change-TheatreName\n3.Update Screens\n4.Update-Shows");

		int updateChoice = readIntInput();
		s.nextLine();
		return updateChoice;
	}

	public int screenAddRemoveChoice() {
		System.out.println("1.ADD\n2.REMOVE\n3.Change Screen-Type");
		int screenChoice = readIntInput();
		s.nextLine();

		return screenChoice;
	}

	public int movieAddRemoveChoice() {
		System.out.println("1.REMOVE-MOVIE\n2.CHANGE-NAME\n3.CHANGE-DURATION\n");

		int movieChoice = readIntInput();
		s.nextLine();

		return movieChoice;
	}

	public int showUpdateChoice() {

		System.out.println("1.INCREASE-SHOW\n2.REDUCE-SHOW\n3.CHANGE-SHOW-MOVIE 4.CHANGE-SHOW-TICKET-PRICE");
		int showChoice = readIntInput();
		s.nextLine();
		return showChoice;
	}

	public List<Integer> getShowsToRemove() {

		System.out.println("Enter Number of shows to Remove");
		int count = readIntInput();
		System.out.println("Enter Show Number:");
		List<Integer> temp = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			System.out.println("Show ID-" + i);
			int showId = readIntInput();
			temp.add(showId);
		}
		return temp;
	}

	public int chooseShow() {
		// TODO Auto-generated method stub
		System.out.println("Enter the Show Number:");
		int showId = readIntInput();
		s.nextLine();
		return showId;
	}

	public int bookingSeats() {
		// TODO Auto-generated method stub
		System.out.println("Enter number of seats to book");
		int seats = readIntInput();
		s.nextLine();

		while (seats <= 0) {
			System.out.println("Invalid ReEnter");
			seats = readIntInput();
			s.nextLine();
		}
		return seats;
	}

	public LocalDate chooseDate(Set<LocalDate> showDates) {

		System.out.println("Choose the Date.");
		int dateChoice = s.nextInt();
		if (dateChoice < 0) {
			System.err.println("Invalid choice.");
		}
		int index = 1;
		for (LocalDate s : showDates) {
			if (index == dateChoice) {
				return s;
			}
			index++;
		}
		return null;
	}

	public String bookingTheatreChoice(Set<String> tempTheatreList) {
		System.out.println("Choose the Theatre to View More details.");
		int theatreChoice = s.nextInt();
		if (theatreChoice < 0) {
			System.err.println("Invalid choice.");
		}
		int index = 1;
		for (String i : tempTheatreList) {
			if (index == theatreChoice) {
				return i;
			}
			index++;
		}
		return "";
	}

	public List<seat_Structure> getScreenSeatStructure() {
		System.out.println("Enter the Number of Seat-Rows in a screen.");
		int rowLength = readIntInput();
		System.out.println("Enter the Number of Seat-Columns in a screen.");
		int colLength = readIntInput();
		boolean mat[][] = new boolean[rowLength][colLength];
		for (boolean[] b : mat) {
			Arrays.fill(b, true);
		}
		int total = rowLength * colLength;
		System.out.println("Seat-Structure");
		while (true) {
			displaySeats(mat);
			System.out.println("1.Disable-Seat\n2.Enable-Seat\n3.Save-Structure");
			int choice = readIntInput();
			s.nextLine();
			if (choice == 1) {
				System.out.println("Enter Seat number to disable");
				int seatNum = readIntInput();
				while (seatNum > total) {
					System.out.println("Enter Valid Seat number to disable");
					seatNum = readIntInput();
				}
				if (disableSeat(mat, seatNum)) {
					System.out.println("Seat Disabled.");
				}
			} else if (choice == 2) {
				System.out.println("Enter Seat number to enable");
				int seatNum = readIntInput();
				while (seatNum > total) {
					System.out.println("Enter Valid Seat number to enable");
					seatNum = readIntInput();
				}
				if (enableSeat(mat, seatNum)) {
					System.out.println("Seat Enabled.");
				}
			} else if (choice == 3) {
				break;
			}

		}
		List<seat_Structure> l = new ArrayList<>();
		int count = 1;
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat[0].length; j++) {
				if (mat[i][j] == true) {
					seat_Structure pairObj = new seat_Structure(count, i, j);
					count++;
					l.add(pairObj);

				}
			}
		}
		getSeatType(l, mat);

		return l;

	}

	private void displaySeats(boolean[][] mat) {
		int curr = 1;
		String format = "%5s";
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat[0].length; j++) {
				if (mat[i][j] == true) {
					System.out.printf(format, curr);
				} else {
					System.out.printf(format, "__");
				}
				curr++;
			}
			System.out.println("\n");
		}

	}

	private boolean enableSeat(boolean mat[][], int seatNum) {

		int curr = 1;
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat[0].length; j++) {
				if (seatNum == curr) {
					mat[i][j] = true;
					return true;
				}
				curr++;
			}
		}
		return false;
	}

	private boolean disableSeat(boolean mat[][], int seatNum) {
		int curr = 1;
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat[0].length; j++) {
				if (seatNum == curr) {
					mat[i][j] = false;
					return true;
				}
				curr++;
			}
		}
		return false;
	}

	public List<Long> chooseSeats(Show showObj, int numberOfSeats, Screen screenObj) {

		List<seat_Structure> seat_StructureList = screenObj.getSeats();
		Map<Long, Long> bookingStatus = showObj.getBookingStatus();

		int maxRow = 0;
		int maxCol = 0;
		for (seat_Structure s : seat_StructureList) {
			int currRow = s.getRowIndex();
			if (currRow > maxRow) {
				maxRow = currRow;
			}
			int currCol = s.getColIndex();
			if (currCol > maxCol) {
				maxCol = currCol;
			}
		}
		long mat[][] = new long[maxRow + 1][maxCol + 1];
		for (seat_Structure s : seat_StructureList) {
			int i = s.getRowIndex();
			int j = s.getColIndex();
			mat[i][j] = s.getSeatId();
		}

		List<Long> seatsToBook = new ArrayList<>();
		int seat = 1;
		while (seat <= numberOfSeats) {
			String choice = mreadStringInput("Enter seat number " + (seat) + ":").toUpperCase();
			boolean flag = false;
			char c = 'A';
			for (int i = 0; i < mat.length; i++) {
				for (int j = 0; j < mat[0].length; j++) {
					long seat_id = mat[i][j];
					String s = c + "" + (j + 1);
					if (s.equals(choice)) {
						if (bookingStatus != null && bookingStatus.get(seat_id) == -1) {
							seatsToBook.add(seat_id);
							bookingStatus.put(seat_id, (long) 1);
							flag = true;
							break;
						} else {
							System.out.println("Seat is longer Available");
							break;
						}
					}

				}

				c++;

			}
			if (flag) {
				seat++;
			} else {
				System.out.println("Invalid Seat");
			}

		}
		return seatsToBook;
	}

	public int mreadIntInput(String message) {
		System.out.println(message);
		try {
			int a = s.nextInt();
			return a;
		} catch (InputMismatchException e) {
			System.out.println("Wront input.Enter Integer value");
			return -1;
		}
	}

	public double getPrice(String message) {
		System.out.println(message);
		double price = s.nextDouble();
		while (price <= 0 || price > 10000) {
			System.err.println("Invalid Price.Re Enter");
			price = s.nextDouble();

		}
		return price;
	}

	public void getSeatType(List<seat_Structure> seatList, boolean mat[][]) {

		Map<String, Double> typePrice = new HashMap<>();

		while (true) {
			System.out.println("DEFINE SEAT TYPE\n1.Add SeatType\n2.Exit");
			int choice = readIntInput();
			s.nextLine();

			if (choice == 2) {
				break;
			} else if (choice == 1) {
				String seatType = mreadStringInput("Enter the seat type");
				if (typePrice.containsKey(seatType)) {
					System.out.println("Seat Type already exists.Do you want to update seat price \n1.yes\n2.No");
					int choice2 = readIntInput();
					s.nextLine();
					if (choice2 == 1) {
						double price = mreadDoubleInput("Enter " + seatType + " Price");
						typePrice.put(seatType, price);
						System.out.println("Price Updated.");
					}
				} else {
					double price = mreadDoubleInput("Enter " + seatType + " Price");
					typePrice.put(seatType, price);
					System.out.println("Seat Type Added.");
				}
			}
		}
		boolean flag = true;
		while (flag) {
			displaySeatsWithType(mat, seatList);
			System.out.println("Seat-Type-Assignment\n1.Assign-by-row\n2.Assign-by-seat\n3.Save");
			int choice = readIntInput();
			s.nextLine();
			if (choice == 1) {
				System.out.println("Enter row Number");
				int row = readIntInput();
				s.nextLine();
				String type = getSeatType(typePrice);
				double price = typePrice.get(type);
				if (row <= mat.length) {
					for (int j = 0; j < mat[0].length; j++) {
						int i = row - 1;
						for (seat_Structure p : seatList) {
							if (p.getRowIndex() == i && p.getColIndex() == j && mat[i][j] == true) {
								p.setSeatType(type);
								p.setSeat_price(price);
								// System.out.println("Seat type Assigned for row");
							}
						}
					}

				} else {
					System.out.println("Invalid row");

				}

			} else if (choice == 2) {
				System.out.println("Enter Seat Number");
				int seatNum = readIntInput();
				if (seatNum < 0 || seatNum > (mat.length * mat[0].length)) {
					System.out.println("Invalid seat number");
					continue;
				}
				s.nextLine();
				int curr = 1;
				for (int i = 0; i < mat.length; i++) {
					for (int j = 0; j < mat[0].length; j++) {
						if (mat[i][j] == true && curr == seatNum) {
							String type = getSeatType(typePrice);
							double price = typePrice.get(type);

							for (seat_Structure p : seatList) {
								if (p.getRowIndex() == i && p.getColIndex() == j && mat[i][j] == true) {
									p.setSeatType(type);
									p.setSeat_price(price);

									System.out.println("Seat type Assigned");
									break;
								}

							}
							break;
						}
						curr++;
					}
				}
			} else {

				for (seat_Structure p : seatList) {
					if (p.getSeatType() == null) {
						System.out.println("Some Seats are Un-Assigned. Assign and save ");
						flag = true;
						break;
					} else {
						flag = false;
					}

				}

			}

		}

	}

	private String getSeatType(Map<String, Double> typePrice) {
		System.out.println("Choose Seat Type:");
		int i = 1;
		for (Map.Entry<String, Double> m : typePrice.entrySet()) {
			System.out.print(i + ". " + m.getKey() + " ");
			i++;
		}
		int choice3 = readIntInput();
		while (choice3 > i) {
			System.out.println("Invalid Re-Enter");
			choice3 = readIntInput();
			s.nextLine();
		}
		if (choice3 <= i) {
			i = 1;
			for (Map.Entry<String, Double> m : typePrice.entrySet()) {
				if (i == choice3) {
					return m.getKey();
				}
				i++;
			}
		}
		return "";

	}

	private void displaySeatsWithType(boolean[][] mat, List<seat_Structure> seatList) {
		// TODO Auto-generated method stub
		String format = "%5s-%5s ";
		String format2 = "%5s ";
		int curr = 1;
		for (int i = 0; i < mat.length; i++) {

			for (int j = 0; j < mat[0].length; j++) {
				for (seat_Structure p : seatList) {
					if (p.getRowIndex() == i && p.getColIndex() == j && mat[i][j] == true) {
						System.out.printf(format, curr, p.getSeatType());

					}
				}
				if (mat[i][j] == false) {
					System.out.printf(format2, "__");
				}

				curr++;
			}
			System.out.println("\n");
		}

	}

	public String getLocation() {

		System.out.println("Enter theatre Location");
		String location = readStringInput();
		return location;
	}

	public List<Pricing> getShowPricing(List<Pricing> pricing) {

		if (pricing.isEmpty()) {
			return null;
		}
		List<Pricing> temp = new ArrayList<>();
		System.out.println("SHOW_PRICING");
		for (Pricing p : pricing) {
			System.out.println("SEAT-TYPE: " + p.getSeatType() + "  DEFAULT-PRICE: " + p.getPrice());
			System.out.println("Enter 1.CHANGE-PRICE\n2.SKIP ");
			int choice = readIntInput();
			s.nextLine();
			if (choice == 1) {
				double price = mreadDoubleInput("Enter new " + p.getSeatType() + " Price.");
				if (price > 0) {
					long seat_id = p.getSeatTypeId();
					String seat_type = p.getSeatType();

					Pricing newP = new Pricing(seat_id, seat_type, price);

					temp.add(newP);
					System.out.println("Price Changed.");
				}
			}
		}
		return temp;

	}

	public int chooseMovieLanguage(List<String> langList) {

		int choice = mreadIntInput("Choose the Movie Language");
		s.nextLine();
		while (choice > langList.size() && choice > 0) {
			System.out.println("Invalid");
			choice = mreadIntInput("Choose the Movie Language");
			s.nextLine();
		}
		return choice;
	}

	public List<String> getSeatLabel(List<Long> l2, long booking_id, Screen screenObj) {
		List<seat_Structure> seat_StructureList = screenObj.getSeats();
		int maxRow = 0;
		int maxCol = 0;
		for (seat_Structure s : seat_StructureList) {
			int currRow = s.getRowIndex();
			if (currRow > maxRow) {
				maxRow = currRow;
			}
			int currCol = s.getColIndex();
			if (currCol > maxCol) {
				maxCol = currCol;
			}
		}
		long mat[][] = new long[maxRow + 1][maxCol + 1];
		for (seat_Structure s : seat_StructureList) {
			int i = s.getRowIndex();
			int j = s.getColIndex();
			mat[i][j] = s.getSeatId();
		}

		List<String> seatlabel = new ArrayList<>();
		for (Long l : l2) {

			long key = l;

			char c = 'A';
			for (int i = 0; i < mat.length; i++) {
				for (int j = 0; j < mat[0].length; j++) {
					long seat_id = mat[i][j];
					String s = c + "" + (j + 1);
					if (seat_id == key) {
						seatlabel.add(s);
					}

				}

				c++;

			}

		}
		return seatlabel;

	}
}
