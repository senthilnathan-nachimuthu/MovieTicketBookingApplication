//package userInteraction;
//
//
//import java.sql.SQLException;
//import java.util.Collection;
//import java.util.List;
//import java.util.Scanner;
//
//import ActionClasses.adminAction;
//import Schemas.Show;
//import Schemas.Theatre;
//import Schemas.Users;
//import Utilities.BookingUtility;
//import Utilities.MovieUtility;
//import Utilities.ShowUtility;
//import Utilities.TheatreUtility;
//import Utilities.userUtilities;
//import WalletPackage.AdminCreditManagement;
//import WalletPackage.PaymentManagement;
//import WalletPackage.WalletManagement;
//import ActionClasses.userAction;
//import DAO.showDAO;
//import DAO.userDAO;
//
//public class Home {
//
//	Users userobj;
//	InputHandler inputhandlerObj;
//	ShowUtility showUtilityObj ;
//	TheatreUtility theatreUtility ;
//	MovieUtility movieUtility;
//	BookingUtility bookingutilityObj;
//	AdminCreditManagement creditManageObj;
//	Scanner s = new Scanner(System.in);
//	Home(Users user,InputHandler ipObj,TheatreUtility theatreObj,MovieUtility movieUtilityObj,ShowUtility showUtilityObj,BookingUtility bookingUtilityObj,AdminCreditManagement creditManageObj) {
//
//		this.userobj=user;
//		this.inputhandlerObj=ipObj;
//		this.theatreUtility=theatreObj;
//		this.showUtilityObj=showUtilityObj;
//		this.movieUtility=movieUtilityObj;
//		this.bookingutilityObj=bookingUtilityObj;
//		this.creditManageObj=creditManageObj;
//		System.out.println("Hello " + user.getName());
//		if (user.isadmin) {
//			showAdminMenu();
//		} else {
//			showUserMenu();
//		}
//	}
//
//	public void showAdminMenu() {
//		adminAction adminaction = new adminAction(movieUtility, theatreUtility, showUtilityObj, inputhandlerObj,bookingutilityObj);
//
//		while (true) {
//
//			System.out.println("""
//					Enter
//					1.Add-Theatre
//					2.Add-Movie
//					3.Create-Shows
//					4.Update-Theatre
//					5.Update-Movie
//					6.Display-Theatres
//					7.Display-Movies
//					8.Search-by-Theatre
//					9.Search-by-Movie
//					10.Booking-Details.
//					11.Define-Credits
//					12.Logout.
//					13.EXIT
//					""");
//
//			int choice = s.nextInt();
//			s.nextLine();
//
//			switch (choice) {
//
//			case 1: // ADD THEATRE
//
//				int theatreCount = inputhandlerObj.getTheatreCount();
//				for (int i = 1; i <= theatreCount; i++) {
//					System.out.println("Theatre " + i + " Details:");
//					String theatrename = inputhandlerObj.getTheatreName().toLowerCase();
//					String theatreLocation=inputhandlerObj.getLocation().toLowerCase();
//					if (adminaction.addTheatre(theatrename,theatreLocation)) {
//						System.out.println("Theatre Added Successfully.");
//					} else {
//						System.out.println("Theatre already exists");
//					}
//				}
//
//				break;
//
//			case 2:// ADD MOVIE
//				System.out.println("Enter number of movies to add.");
//				int numberOfMovies = inputhandlerObj.readIntInput();
//				inputhandlerObj.s.nextLine();
//				for (int i = 1; i <= numberOfMovies; i++) {
//					System.out.println("ENTER MOVIE " + i + " DETAILS");
//					if (adminaction.addMovie(inputhandlerObj.getMovieName(), inputhandlerObj.getMovieLanguage(),
//							inputhandlerObj.getMovieDuration())) {
//						System.out.println("Movie Added successfully.");
//					} else {
//						System.out.println("Movie already exists.");
//					}
//				}
//				break;
//
//			case 3:// CREATE SHOW
//
//				adminaction.displayTheatres();
//				int theatreChoice = inputhandlerObj.theatreChoice();
//				long theatreId=theatreUtility.getTheatreId(theatreChoice);
//				if(theatreId!=-1)
//				{
//					adminaction.createShow(theatreId);
//					
//				}
//				else
//				{
//					System.out.println("Invalid Theatre.");
//				}
//				break;
//
//			case 4:// UPDATE THEATRES
//
////				
//				System.out.println("Update Theatre:");
//				int updateChoice = inputhandlerObj.chooseUpdateOption();
//				switch (updateChoice) {
//
//				case 1:
//						if(!adminaction.displayTheatres())
//						{
//							System.err.println("No theatre Exists.");
//							break;
//						}
//						
//					int theatreToDelete = inputhandlerObj.theatreChoice();
//					long theatreId1=theatreUtility.getTheatreId(theatreToDelete);
//					if(theatreId1!=-1)
//					{
//						adminaction.deleteTheatre(theatreId1);
//
//					}
//					else
//					{
//						System.err.println("Invalid Theatre");
//					}
//					break;
//				case 2:
//					if(!adminaction.displayTheatres())
//					{
//						System.err.println("No theatre Exists.");
//						break;
//					}
//					int theatreToUpdate = inputhandlerObj.theatreChoice();
//					long theatreId2=theatreUtility.getTheatreId(theatreToUpdate);
//					if(theatreId2!=-1)
//					{
//						System.out.println("New Theatre Name");
//						String theatreName = inputhandlerObj.getTheatreName();
//						adminaction.updateTheatreName(theatreId2, theatreName);
//						
//					}
//					else
//					{
//						System.out.println("Invalid Theatre choosen.");
//					}
//					break;
//				case 3:
//
//					if(!adminaction.displayTheatres())
//					{
//						System.err.println("No theatre Exists.");
//						break;
//					}
//					theatreId = theatreUtility.getTheatreId(inputhandlerObj.theatreChoice());
//					if(theatreId!=-1)
//					{
//						int addRemoveChoice = inputhandlerObj.screenAddRemoveChoice();
//						if (addRemoveChoice == 1) {
//							if (adminaction.createScreen(theatreId)) {
//								System.out.println("Screen added");
//							} else {
//								System.out.println("Screen not added");
//							}
//						} else if (addRemoveChoice == 2) {
//							if (adminaction.removeScreen(theatreId)) {
//								System.out.println("Screen Deleted.");
//							} else {
//								System.out.println("Screen not deleted.");
//								
//							}
//						} else if (addRemoveChoice == 3) {
//							if (adminaction.changeScreenType(theatreId)) {
//								System.out.println("Screen Type Updated.");
//							}
//						} else {
//							System.err.println("Invalid Choice.");
//						}
//						
//					}
//					else
//					{
//						System.out.println("Invalid Theatre.");
//					}
//					break;
//				case 4:
//
//					int showUpdateChoice = inputhandlerObj.showUpdateChoice();
//					switch (showUpdateChoice) {
//					case 1:
//						int showTheatreId;
//						if(adminaction.displayTheatres()==true) {
//							showTheatreId = inputhandlerObj.theatreChoice();
//							theatreId=theatreUtility.getTheatreId(showTheatreId);
//							if(theatreId!=-1)
//							{
//								adminaction.createShow(theatreId);								
//							}
//							else
//							{
//								System.out.println("Invalid");
//							}
//						}
//						else
//						{
//							System.err.println("No Theatre Exists.");
//						}
//						break;
//					case 2:
//						if(adminaction.displayTheatres()==true) {
//							showTheatreId = inputhandlerObj.theatreChoice();
//							theatreId=theatreUtility.getTheatreId(showTheatreId);
//							if(theatreId!=-1)
//							{
//								adminaction.reduceShow(theatreId);								
//							}
//							else
//							{
//								System.out.println("Invalid");
//							}
//						}
//						else
//						{
//							System.err.println("No Theatre Exists.");
//						}
//						break;
//					case 3:
//						adminaction.displayTheatres();
//						showTheatreId = inputhandlerObj.theatreChoice();
//						theatreId=theatreUtility.getTheatreId(showTheatreId);
//						if(theatreId!=-1)
//						{
//							adminaction.changeShowMovie(theatreId);
//						}
//						else
//						{
//							System.out.println("Invalid");
//						}
//						break;
//					case 4:
//						adminaction.displayTheatres();
//						showTheatreId = inputhandlerObj.theatreChoice();
//						theatreId=theatreUtility.getTheatreId(showTheatreId);
//						adminaction.changeTicketPrice(theatreId);
//						break;
//					default:
//						System.err.println("Invalid Choice");
//
//					}
//					break;
//				default:
//					System.out.println("Invalid update choice");
//				}
//				break;
//
//			case 5: //UPDATE MOVIE
//
//			if(adminaction.displayMovies())
//			{
//				long movieId = movieUtility.getMovieId(inputhandlerObj.chooseMovie());
//				if(adminaction.updateMovie(movieId))
//				{
//					System.out.println("Movie Operation successful.");
//				}
//				
//			}
//			break;
//			case 6: // DISPLAY THEATRES
//				adminaction.displayTheatres();
//				break;
//
//			case 7: // DISPLAY MOVIES
//				adminaction.displayMovies();
//				break;
//
//			case 8:
//				adminaction.searchByTheatre(inputhandlerObj.getTheatreName());
//				break;
//
//			case 9:
//				adminaction.searchByMovie(inputhandlerObj.getMovieName());
//				break;
//
//			case 10:
//				int viewChoice=inputhandlerObj.mreadIntInput("Enter \n1.View-Booking-History\n2.View-Booked-seats\n3. Cancel Booking");
//				if(viewChoice==1)
//				{
//					adminaction.viewBookingHistory();					
//				}
//				else if(viewChoice==2)
//				{
//					
//					adminaction.viewBookedSeats();
//				}
//			
//				else if(viewChoice==3)
//				{
//					System.out.println("Cancel by 1.Theatre\n2.Screen\n3.Movie\n4.Show");
//					int cancelChoice=inputhandlerObj.readIntInput();
//					inputhandlerObj.s.nextLine();
//					switch(cancelChoice)
//					{
//						case 1:
//							adminaction.displayTheatres();
//							int theatreChoice1=inputhandlerObj.theatreChoice();
//							long theatreId1=theatreUtility.getTheatreId(theatreChoice1);
//							if(adminaction.cancelByTheatre(theatreId1))
//							{
//								System.out.println("Completed");
//							}
//							else
//							{
//								System.out.println("Booking Cancellation Failed.");
//							}
//							break;
//						case 2:
//							adminaction.displayTheatres();
//							
//							int theatreChoice11=inputhandlerObj.theatreChoice();
//							long theatreId11=theatreUtility.getTheatreId(theatreChoice11);
//							inputhandlerObj.s.nextLine();
//
//							adminaction.displayScreens(theatreId11);
//							int screenCho=inputhandlerObj.readIntInput();
//							inputhandlerObj.s.nextLine();
//							long screenId=theatreUtility.getScreenId(theatreId11, screenCho);
//		
//
//							if(adminaction.cancelByScreen(screenId))
//							{
//								System.out.println("Completed");
//							}
//							else
//							{
//								System.out.println("Booking Cancellation Failed.");
//							}
//							break;
//						case 3:
//							adminaction.displayMovies();
//							int movieCho=inputhandlerObj.chooseMovie();
//							inputhandlerObj.s.nextLine();
//							long movieId=movieUtility.getMovieId(movieCho);
//							
//							if(adminaction.cancelByMovie(movieId))
//							{
//								System.out.println("Completed");
//							}
//							else
//							{
//								System.out.println("Booking Cancellation Failed.");
//							}
//							break;
//						case 4:
//							int theatreChoiceForShow=inputhandlerObj.theatreChoice();
//							long theatreIdForShow=theatreUtility.getTheatreId(theatreChoiceForShow);
//							inputhandlerObj.s.nextLine();
//
//						
//
//							adminaction.displayScreens(theatreIdForShow);
//							int screenChoForShow=inputhandlerObj.readIntInput();
//							inputhandlerObj.s.nextLine();
//							long screenIdForShow=theatreUtility.getScreenId(theatreIdForShow, screenChoForShow);
//		
//
//							adminaction.displayShows(theatreIdForShow, screenIdForShow);
//							List<Integer> showsToRemove = inputhandlerObj.getShowsToRemove();
//							List<Show> showList=showUtilityObj.getShowsByTheatre(theatreIdForShow,screenIdForShow);
//							showDAO showDaoObj=new showDAO();
//							for (Integer s : showsToRemove) {
//								
//								if (s<=showList.size()) {
//									long showId=showList.get(s-1).showId;
//									
//									if(adminaction.cancelByShow(showId))
//									{
//										showDaoObj.deleteShows(theatreIdForShow,screenIdForShow,showId);							
//									}}
//							}
//						break;
//						default:
//							System.out.println("Invalid Choice.");
//					}
//					
//				}
//				else
//				{
//					System.err.println("Invalid Choice");
//				}
//				break;
//				//defining credit
//			case 11:
//				int defineChoice=inputhandlerObj.mreadIntInput("Credit Management\nEnter\n1.Define-Threshold and Credit-Conversion Percentage");
//				inputhandlerObj.s.nextLine();
//				switch(defineChoice)
//				{
//				case 1:
//					double threshold=inputhandlerObj.mreadDoubleInput("Enter Threshold value");
//					double creditConverison=inputhandlerObj.mreadDoubleInput("Enter Credit-Converison percentage in decimals");
//					if(adminaction.defineCreditThreshold(threshold,creditConverison))
//					{
//						System.out.println("Conversion percentage defined. Threshold Defined.");						
//					}
//					break;
//				default:
//					System.out.println("Invalid");
//					break;
//				}
//				break;
//			case 12:
//				userUtilities.logout();
//				break;
//			case 13:
//				System.out.println("Exiting...");
//				return;
//
//			default:
//				System.out.println("Invalid Choice");
//			}
//		}
//
//	}
//
//	public void showUserMenu() {
//
//		WalletManagement wm=new WalletManagement();
//		userDAO userDaoObj=new userDAO();
//		PaymentManagement pmObj=null;
//		try {
//			pmObj = new PaymentManagement(inputhandlerObj,userDaoObj.findBankAccount(userobj.userId));
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		userAction useraction = new userAction(theatreUtility, movieUtility, showUtilityObj,inputhandlerObj,userobj,bookingutilityObj,wm,pmObj,creditManageObj);
//		while (true) {
//
//			System.out.println("\nEnter \n 1.Book-Movie 2.Search-Movie 3.Search-Theatre 4.My-Booking 5.My-Wallet 6.Logout 7.View Account Details 8.Exit");
//			//pmObj.getAccountBalance();
//			int ch = inputhandlerObj.readIntInput();
//			inputhandlerObj.s.nextLine();
//			if (ch == 8) {
//				break;
//			}
//			switch (ch) {
//			case 1: //booking
//				if(!useraction.displayMovies())
//				{
//					System.err.println("No Movies Currently Running.");
//					break;
//				}
//				int movieChoice=inputhandlerObj.chooseMovie();
//				long movieId=movieUtility.getMovieId(movieChoice);
//				if(movieId==-1)
//				{
//					System.out.println("Invalid choice");
//					continue;
//				}
//				useraction.displayMovieLanguages(movieId);
//				List<String> langList=movieUtility.getLanguages(movieId);
//				String language=langList.get(inputhandlerObj.chooseMovieLanguage(langList)-1);
//				long language_id=movieUtility.getLanguageId(language);
//				
//				long language_mapping_id=movieUtility.getLanguageMappingId(movieId,language_id);
//				
//				useraction.bookMovie(movieId,language_mapping_id);
//				break;
//			case 2: //searchbymovie
//				useraction.bookByMovieSearch(inputhandlerObj.getMovieName());
//				break;
//			case 3: //search by theatre
//				useraction.searchByTheatre(inputhandlerObj.getTheatreName());
//				break;
//			case 4: // my booking
//				System.out.println("Enter\n1.View-Booking-Details\n2.Cancel-Booking\n3.Change-Booking");
//				int bookingChoice=inputhandlerObj.readIntInput();
//				switch(bookingChoice)
//				{
//					case 1:
//						useraction.myBooking();
//						break;
//					case 2:
//						useraction.cancelBooking();
//						break;
//					case 3:
//						useraction.changeBooking();
//						break;	
//				}
//				break;
//			case 5: //my wallet
//				
//				
//				while(useraction.myWallet())
//				{
//					int walletOperation=inputhandlerObj.mreadIntInput("Enter\n1.Add-Money\n2.Redeem-Money\n3.Exit");
//					if(walletOperation==3)
//					{
//						break;
//					}
//					inputhandlerObj.s.nextLine();
//					switch(walletOperation)
//					{
//						case 1:
//							System.out.println("Enter Wallet Password.");
//							String walletPassword=inputhandlerObj.readStringInput();
//							double amount=pmObj.getMoney(userobj);
//							
//							if(amount>0 && wm.openWallet(userobj,walletPassword))
//							{
//								if(wm.addMoney(amount, userobj, walletPassword))
//								{
//									System.out.println("\n\nWallet Balance Updated.!!");									
//								}
//								else
//								{
//									System.out.println("\n\nWallet Balance not updated!!!");
//								}
//							}
//							else
//							{
//								pmObj.refund(userobj, amount);									
//								System.out.println("Transaction Failed.");
//							}
//							break;
//						case 2:
//							System.out.println("\n*Enter Wallet Password.");
//							walletPassword=inputhandlerObj.readStringInput();
//						//	System.out.println("\n*Enter Account Details to Reedem.");
//							double redeemAmount=inputhandlerObj.mreadDoubleInput("\n*Enter the redeem Amount");
//							if(wm.reedemMoney(userobj,walletPassword,redeemAmount) )
//							{
//								if(pmObj.creditThroughNET(redeemAmount,userobj))
//								{
//									System.out.println("\n\nAmount Reemed Successfully.!!!!");
//									System.out.println("\nRefund Completed.!");
//								}
//								else
//								{
//									System.out.println("\nReemption Failed!!!");
//									wm.addMoney(redeemAmount, userobj, walletPassword);
//									
//								}
//							}
//							else
//							{
//								System.out.println("\nRedemption Failed.!!!!");
//							}
//							break;
//					}
//				}
//				break;
//		
//			case 6:
//				userUtilities.logout();
//				break;
//			case 7:
//				useraction.displayAccountDetails();
//				break;
//			default:
//				System.err.println("Invalid Choice");
//				break;
//			}
//		}
//
//	}
//}
