
let dataArray = [];
let theatreArray = [];
let movieArray = [];

let bookingLayout = [];
let bookingSeatTypes = [];

let userChoice = {};

async function renderBookingContainer(id) {
	const file = "UserBookingHtmlFiles/" + id + ".html";
	await fetch(file).then(response => response.text()).then(html => {
		document.getElementById("container").innerHTML = html;
	});
}
async function renderBookingOption() {
	getElement("passConfirmButton").hidden = true;
	await renderBookingContainer('booking');
	getElement("optionContainer").hidden = false;
}
let isTheatreFlow = false;
let isMovieFlow = false;
async function loadBookingOptionData(value) {
	dataArray = [];
	theatreArray = [];
	movieArray = [];
	userChoice = {};
	getElement("ticketContainer").hidden = true;
	getElement("bookingError").textContent = "";
	if (value == "theatre") {
		isTheatreFlow = true;
		isMovieFlow = false;
		const res = await fetchGetData("/getAllTheatres");
		if (res.status == 200) {
			getElement("bookingError").textContent = "";
			displayTheatreList(res.data.TheatreDetails);
		}
		else {
			getElement("bookingError").textContent = "Currently No Theatres Available for Booking.";

		}
	}
	else if (value == "movie") {
		isMovieFlow = true;
		isTheatreFlow = false;
		const res = await fetchGetData("/getAllMovies");
		if (res.status == 200) {
			getElement("bookingError").textContent = "";
			displayMovieList(res.data.Movies);
		}
		else {
			getElement("bookingError").textContent = "Currently No Movies Available for Booking.";

		}


	}
}
function displayTheatreList(theatres) {
	const container = getElement("bookingContainer");
	container.innerHTML = '';
	const title = document.createElement("h3");
	title.textContent = "Available Theatres";
	title.style.color = "red";

	container.appendChild(title);
	theatres.forEach((theatre, i) => {
		const div = document.createElement("span");
		div.innerHTML = `<button style="color:green"  onclick="handleBookingTheatreClick('${theatre.theatreId}','${theatre.theatreName.toUpperCase()}','${theatre.theatreLocation.toUpperCase()}')"><p>${theatre.theatreName.toUpperCase()}--${theatre.theatreLocation.toUpperCase()}</p></button> `
		container.append(div);
	})
}

function displayMovieList(movies) {
	const container = getElement("bookingContainer");
	container.innerHTML = '';

	const title = document.createElement("h3");
	title.textContent = "Available Movies";
	title.style.color = "red";
	container.appendChild(title);

	movies.forEach((movie, i) => {
		const div = document.createElement("span");
		let duration = movie.movieDuration;
		if (duration == null) {
			duration = movie.duration;
		}
		const lang = movie.availableLanguages;
		let html = `<button style="color:green" onclick="openLanguagePopup('${movie.movieId}','${movie.movieName}',${JSON.stringify(lang).split('"').join('&quot;')} )"><p>${movie.movieName.toUpperCase()}  <span style="color:red">${duration} Minutes</span></p>
		<p>Languages: `
		for (let i = 0; i < lang.length; i++) {
			html += `<span style="color:blue">${lang[i].toUpperCase()}</span> `
		}
		html += `</p></button>   `;

		div.innerHTML = html;

		container.append(div);
	})
}
async function handleBookingTheatreClick(theatreId, theatreName, theatreLocation) {

	getElement("bookingError").textContent = "";
	if (isTheatreFlow) {

		const data = { theatreId: theatreId };
		const url = "/getShowsByTheatre";
		const params = new URLSearchParams(data).toString();
		const finalUrl = `${url}?${params}`;

		const res = await fetchGetData(finalUrl);
		if (res.status == 200) {
			renderShowTimePage(res.data.Shows);
		}
		else if (res.status == 404) {
			getElement("bookingError").textContent = res.message;
		}
	}
}
async function handleBookingMovieClick() {
	getElement("bookingError").textContent = "";
	const languageSelect = getElement("languageDropdown").value;
	const movieId = getElement("movieId").value;
	closeMoviePopup();
	if (isMovieFlow) {
		const data = { movieId: movieId, Language: languageSelect };
		const url = "/getShowsByMovie";
		const params = new URLSearchParams(data).toString();
		const finalUrl = `${url}?${params}`;

		const res = await fetchGetData(finalUrl);

		if (res.status == 200) {
			renderShowTimePage(res.data.Shows);
		}
		else if (res.status == 404) {
			getElement("bookingError").textContent = res.message;
		}
	}
}
function openLanguagePopup(movieId, name, lang) {


	getElement("movieId").value = movieId;
	getElement("language-popup").style.display = "block";
	const languageSelect = getElement("languageDropdown");
	getElement("movieTitle").textContent = name;
	languageSelect.innerHTML = '';
	for (let i = 0; i < lang.length; i++) {
		const op = document.createElement("option");
		op.textContent = lang[i];
		languageSelect.appendChild(op);
	}

}
function closeMoviePopup() {
	getElement("language-popup").style.display = "none";
}
function fillDates(data, selectElement) {
	data.forEach(show => {
		const date = show.date;
		if (!dataArray.includes(date)) {
			dataArray.push(date);
		}
	})

	dataArray.forEach(date => {
		const op = document.createElement("option");
		op.textContent = date;
		op.value = date;
		selectElement.appendChild(op);
	})
}
function fillTheatres(data) {
	data.forEach(show => {
		const theatre = show.theatre;
		if (!theatreArray.includes(theatre)) {
			theatreArray.push(theatre);

		}
	})
}
function fillMovies(data) {
	data.forEach(show => {
		const movie = show.movieName;
		if (!movieArray.includes(movie)) {
			movieArray.push(movie);

		}
	})
}

async function renderShowTimePage(data) {

	const container = getElement("bookingContainer");
	container.innerHTML = '';
	const dataSelect = document.createElement("select");
	dataSelect.innerHTML = '';
	const op = document.createElement("option");
	op.textContent = "All Dates";
	dataSelect.appendChild(op);
	await fillDates(data, dataSelect);
	container.appendChild(dataSelect);

	const ParentDiv = document.createElement("div");
	ParentDiv.id = "parent";
	dataSelect.onchange = () => handleLoadShowTimeContent(dataSelect.value, data, container, ParentDiv);

	if (isMovieFlow) {
		await fillTheatres(data);
		handleLoadShowTimeContent("All Dates", data, container, ParentDiv);
	}
	else if (isTheatreFlow) {
		await fillMovies(data);
		handleLoadShowTimeContent("All Dates", data, container, ParentDiv);
	}
}
function handleLoadShowTimeContent(value, data, container, ParentDiv) {


	if (value == "All Dates") {

		ParentDiv.innerHTML = '';

		dataArray.forEach(date => {

			if (isMovieFlow == true) {
				const div = document.createElement("div");
				let html = `<h3 style="color:red">${date}</h3>`
				theatreArray.forEach(theatre => {
					let isShowFound = false;
					let row = ``;
					data.forEach(show => {

						const ScreenObj = show.Screen.Screens[0];
						if (show.theatre == theatre && show.date == date) {
							const button = `<button id="showTimeButton" onclick="handleShowBooking('${show.showId}',${JSON.stringify(show).split('"').join('&quot;')},${JSON.stringify(ScreenObj.seatStructure).split('"').join('&quot;')})"><p>${show.time} - <span>${ScreenObj.screenType}</span><br><span style="color:green">Screen:${ScreenObj.screenName}</span></p></button> `
							row += button;
							isShowFound = true;


						}
					})
					if (isShowFound == true) {
						html += `<p style="color:blue">${theatre.toUpperCase()}</p>`
					}
					html += row;
				})
				div.innerHTML = html;
				ParentDiv.appendChild(div);
			}
			else {
				const div = document.createElement("div");
				let html = `<h3 style="color:red">${date}</h3>`
				movieArray.forEach(movie => {
					let isShowFound = false;
					let row = ``;
					let language = "";
					data.forEach(show => {
						const ScreenObj = show.Screen.Screens[0];

						if (show.movieName == movie && show.date == date) {
							const button = `<button onclick="handleShowBooking('${show.showId}',${JSON.stringify(show).split('"').join('&quot;')},${JSON.stringify(ScreenObj.seatStructure).split('"').join('&quot;')})"><p>${show.time} - <span>${ScreenObj.screenType}</span><br><span style="color:green">Screen:${ScreenObj.screenName}</span></p></button> `
							row += button;
							isShowFound = true;
							language = show.movieLanguage;
						}
					})
					if (isShowFound == true) {
						html += `<p style="color:blue">${movie.toUpperCase()} - ${language}</p>`
					}
					html += row;
				})
				div.innerHTML = html;
				ParentDiv.appendChild(div);
			}

		})
		container.appendChild(ParentDiv);
	}
	else {
		const date = value;
		ParentDiv.innerHTML = '';
		container.removeChild(getElement("parent"));
		if (isMovieFlow == true) {
			const div = document.createElement("div");
			let html = `<h3 style="color:red">${date}</h3>`
			theatreArray.forEach(theatre => {

				let isShowFound = false;
				let row = ``;
				data.forEach(show => {

					const ScreenObj = show.Screen.Screens[0];
					if (show.theatre == theatre && show.date == date) {
						const button = `<button onclick="handleShowBooking('${show.showId}',${JSON.stringify(show).split('"').join('&quot;')},${JSON.stringify(ScreenObj.seatStructure).split('"').join('&quot;')})"><p>${show.time} - <span>${ScreenObj.screenType}</span><br><span style="color:green">Screen:${ScreenObj.screenName}</span></p></button> `
						row += button;
						isShowFound = true;
					}
				})
				if (isShowFound == true) {
					html += `<p style="color:blue">${theatre.toUpperCase()}</p>`
				}
				html += row;
			})
			div.innerHTML = html;
			ParentDiv.appendChild(div);
			container.appendChild(ParentDiv);
		}
		else {
			const div = document.createElement("div");
			let html = `<h3 style="color:red">${date}</h3>`
			movieArray.forEach(movie => {
				let isShowFound = false;
				let row = ``;
				let language = "";
				data.forEach(show => {
					const ScreenObj = show.Screen.Screens[0];

					if (show.movieName == movie && show.date == date) {
						const button = `<button onclick="handleShowBooking('${show.showId}',${JSON.stringify(show).split('"').join('&quot;')},${JSON.stringify(ScreenObj.seatStructure).split('"').join('&quot;')})"><p>${show.time} - <span>${ScreenObj.screenType}</span><br><span style="color:green">Screen:${ScreenObj.screenName}</span></p></button> `
						row += button;
						isShowFound = true;
						language = show.movieLanguage;
					}
				})
				if (isShowFound == true) {
					html += `<p style="color:blue">${movie.toUpperCase()} - ${language}</p>`
				}
				html += row;
			})
			div.innerHTML = html;
			ParentDiv.appendChild(div);
			container.appendChild(ParentDiv);

		}

	}
}


function fillUserChoice(showObj) {
	userChoice = { showId: showObj.showId, date: showObj.date, time: showObj.time, movie: showObj.movieName || showObj.movie, language: showObj.movieLanguage || showObj.language, theatre: showObj.theatre, screen: showObj.screenName, Pricing: showObj.Pricing };
}


async function handleShowBooking(showId, ShowObj, seatStructure) {

	await fillUserChoice(ShowObj);
	console.log(ShowObj);
	await fillBookingLayout(seatStructure);
	console.log(bookingLayout);
	console.log(bookingSeatTypes);
	await openBookingLayout(showId);
	//updateBookingStatus(showId);

}
let isFetched = false;
let bookingTimerId;
function updateBookingStatus(showId) {
	bookingTimerId = setInterval(async () => {

		if (isFetched == true) {
			return;
		}
		isFetched = true;
		await openBookingLayout(showId);
		isFetched = false;
	}, 10000);


}

function fillBookingLayout(seatStructure) {
	bookingLayout = [];
	bookingSeatTypes = [];
	const ShowPricing = userChoice.Pricing;
	console.log(ShowPricing);

	/*for (let i = 0; i < seatStructure.length; i++) {

		var seatType = seatStructure[i].seatType;
		if (!bookingSeatTypes.some(st => st.seatType === seatType)) {

			bookingSeatTypes.push({ seatType, Price: Number(seatStructure[i].seat_price) });
		}
	}*/

	for (let i = 0; i < ShowPricing.length; i++) {
		var seatType = ShowPricing[i].seatType;
		bookingSeatTypes.push({ seatType, Price: Number(ShowPricing[i].Price) });
	}

	for (let i = 0; i < seatStructure.length; i++) {

		const index = bookingSeatTypes.findIndex(st => st.seatType === seatStructure[i].seatType);
		const row = ({ seatId: seatStructure[i].seatId, seatTypeIndex: index, disabled: seatStructure[i].disabled, rowIndex: seatStructure[i].rowIndex, colIndex: seatStructure[i].colIndex });
		if (!bookingLayout[seatStructure[i].rowIndex]) {
			bookingLayout[seatStructure[i].rowIndex] = [];
		}
		bookingLayout[seatStructure[i].rowIndex][seatStructure[i].colIndex] = (row);
	}
}


async function getSeatBookingStatus(showId) {
	const data = { showId: showId };
	const url = "/getBookingStatus";
	const params = new URLSearchParams(data).toString();
	const finalUrl = `${url}?${params}`;
	const res = await fetchGetData(finalUrl);
	if (res.status == 200) {
		return res.data;
	}
}


async function openBookingLayout(showId) {

	console.log(showId);
	console.log(bookingSeatTypes);
	let bookingStatus = await getSeatBookingStatus(showId);
	let status = JSON.parse(bookingStatus.bookingStatus);
	//console.log(status[5001]);
	let selectedSeats = [];

	const container = document.getElementById("bookingContainer");
	var size = 0;
	container.innerHTML = '';

	const nextButton = document.createElement("button");
	nextButton.textContent = "Next";
	container.appendChild(nextButton);

	nextButton.onclick = () => handleSelectedSeats(selectedSeats);

	const html = document.createElement("div")
	html.innerHTML = `<p>-----------------------------------Screen This way-----------------------------------</p>`;
	container.appendChild(html);
	bookingLayout.forEach((row, rIndex) => {
		const rowDiv = document.createElement("div");
		rowDiv.className = "row";
		rowDiv.id = "seatRow";

		const rowValue = document.createElement("p");
		rowValue.id = "rowId";
		rowValue.textContent = intToExcelColumn(rIndex + 1);
		rowDiv.appendChild(rowValue);
		row.forEach((seat, cIndex) => {
			size++;

			bookingLayout[rIndex][cIndex].rowIndex = rIndex;
			bookingLayout[rIndex][cIndex].colIndex = cIndex;
			const btn = document.createElement("button");
			btn.className = "seat";
			btn.classList.add('button-spacing');
			btn.id = seat.seatId;
			const type = bookingSeatTypes[seat.seatTypeIndex];
			const label = intToExcelColumn(rIndex + 1) + (cIndex + 1);
			console.log(status[seat.seatId]);
			if (status[seat.seatId] != -1) {
				btn.innerHTML = `<p>X</p>`
			}
			else {
				btn.innerHTML = `<p><span style="color:red">${label}</span><br>${type.seatType} - Rs.${type.Price}</p><input type="checkbox" class="seat-box" data-seat="${seat.seatId}">`
				btn.onclick = () => {
					const chkBox = document.querySelectorAll(`.seat-box[data-seat='${seat.seatId}']`);
					chkBox[0].checked = !chkBox[0].checked;
					if (chkBox[0].checked == true) {
						if (!selectedSeats.some(st => st.seatId === seat.seatId)) {
							selectedSeats.push({ seatId: seat.seatId, seatLabel: label, seat });
						}
					}
					else {
						const index = selectedSeats.findIndex(st => st.seatId === seat.seatId);
						selectedSeats.splice(index, 1);
					}
				}
			}
			if (seat.disabled) btn.classList.add("disabled");

			rowDiv.appendChild(btn);
		});
		container.appendChild(rowDiv);
	});
}

function handleSelectedSeats(selectedSeats) {


	if (selectedSeats.length == 0) {
		getElement("bookingError").textContent = "No seats Selected";
		return;
	}
	else {
		getElement("bookingError").textContent = "";
		if (bookingTimerId) {
			clearInterval(bookingTimerId);
		}
		handleWalletPaymentRequest(selectedSeats);
		//	displayTicketConfirmation(selectedSeats);
	}

}
async function handleWalletPaymentRequest(selectedSeats) {

	userChoice.SelectedSeats = selectedSeats;


	const PrevContainer = getElement("bookingContainer");
	PrevContainer.innerHTML = '';
	const walletContainer = getElement("walletContainer");
	walletContainer.hidden = false;

	const res = await fetchGetData("/mywallet");
	if (res.status == 200) {
		const wallet = res.data;
		getElement("balance").textContent = wallet.walletAmount;

		const seatsContainer = getElement("ticketSeats");
		let totalAmount = 0;
		selectedSeats.forEach(seat => {
			const row = document.createElement("span");
			row.textContent = seat.seatLabel + "-" + bookingSeatTypes[seat.seat.seatTypeIndex].seatType + " ";
			seatsContainer.appendChild(row);

			let seatAmount = bookingSeatTypes[seat.seat.seatTypeIndex].Price;
			totalAmount += seatAmount;

		});
		userChoice.totalAmount = totalAmount;
		getElement("ticketAmount").textContent = totalAmount;

		if (wallet.walletAmount < totalAmount) {

			getElement("walletPaymentBtn").hidden = true;
			getElement("walletErrorMessage").textContent = "Insufficient Wallet Balance."

		}
	}
	else if (res.status == 404) {
		renderActivateWallet();
	}


}
async function popPasswordHelper() {
	getElement("walletPassword").value = "";
	getElement("password-popup").style.display = "block";
}
function closePasswordPopup() {
	getElement("walletPassword").value = "";
	getElement("password-popup").style.display = "none";

}
async function verifyPassword() {
	var password = getElementData("walletPassword");
	const data = { Password: password };
	const res = await fetchData("/openWallet", "POST", data);
	console.log(res);
	if (res != null) {
		console.log(res);
		if (res.body.status == 200) {
			handleBookingPayment();
			getElement("walletPassword").value = "";
		}
		else {
			getElement("passwordError").textContent = "Invalid Password";
		}
	}
}

function displayTicketConfirmation(message) {

	console.log(userChoice);
	const selectedSeats = userChoice.SelectedSeats;
	console.log(selectedSeats);
	/*const PrevContainer = getElement("bookingContainer");
	PrevContainer.innerHTML = '';*/
	const walletContainer = getElement("walletContainer");
	walletContainer.hidden = true;
	const ticketContainer = getElement("ticketContainer");
	ticketContainer.hidden = false;

	getElement("ticketSuccessMessage").textContent = message;
	getElement("dateTime").textContent = (userChoice.date + " / " + userChoice.time);
	getElement("ticketMovie").textContent = (userChoice.movie + " - " + userChoice.language);
	getElement("ticketTheatre").textContent = (userChoice.theatre);
	getElement("ticketScreen").textContent = userChoice.screen;

	const seatsContainer = getElement("ConfirmedTicketSeats");

	let totalAmount = 0;
	selectedSeats.forEach(seat => {
		const row = document.createElement("span");
		row.textContent = seat.seatLabel + "-" + bookingSeatTypes[seat.seat.seatTypeIndex].seatType + " ";
		seatsContainer.appendChild(row);

	});
	getElement("ConfirmedTicketAmount").textContent = userChoice.totalAmount;

}
async function handleBookingPayment() {

	closePasswordPopup();
	console.log(userChoice);
	const seatIds = [];
	userChoice.SelectedSeats.forEach(seat => {
		seatIds.push(seat.seatId);
	});
	const data = { showId: userChoice.showId, seatId: seatIds, isPasswordVerified: true, Amount: userChoice.totalAmount };
	const res = await fetchData("/bookNow", "POST", data);
	if (getResponseCode(res) == 200) {
		const message = res.body.message + " " + " Congratulations You have Earned " + res.body.data + " Credits for this booking ";
		displayTicketConfirmation(message);
	}
}

async function renderMyBookings() {

	const res = await fetchGetData("/myBookings");

	if (res != null && res.status == 200) {
		const booking = JSON.parse(res.data);
		displayMyBookings(booking);
		console.log(booking);
	}
	else {
		await renderBookingContainer("displayBookings");
		getElement("myBookingError").textContent = "No Booking Exists";
	}

}
async function displayMyBookings(booking) {
	await renderBookingContainer("displayBookings");
	getElement("myBookingError").textContent = "";
	const container = getElement("myBookingList");
	container.innerHTML = '';
	booking.forEach(row => {
		const li = document.createElement("li");
		let html = '';
		let status;
		html += `<p>BookingId: <span style="color:red">${row.bookingId}</span>`;
		if (row.bookingStatus == true) { html += ` Booking Status: <span style="color:red">Confirmed</span>`; status = "Confirmed" }
		else { html += ` Booking Status: <span style="color:red">Cancelled</span>`; status = "Cancelled" }
		html += ` Credits Earned: <span style="color:red">${row.creditEarned}</span> 
		<button onclick="handleViewTicket(${JSON.stringify(row.showObj).split('"').join('&quot;')},
		${JSON.stringify(row.seats).split('"').join('&quot;')},
		${JSON.stringify(row.totalAmount).split('"').join('&quot;')},'${status}')">
		View Ticket</button>`;
		if (row.bookingStatus == true) { html += ` <button onclick="handleBookingCancellation(${row.bookingId})">Cancel</button>` }
		li.innerHTML = html;
		container.appendChild(li);
	})
}
function handleViewTicket(showObj, seats, amount, status) {
	fillUserChoice(showObj);
	userChoice.SelectedSeats = seats;
	userChoice.totalAmount = amount;
	displayViewTicket(status);
}
function displayViewTicket(message) {

	console.log(userChoice);
	const selectedSeats = userChoice.SelectedSeats;
	console.log(selectedSeats);
	document.getElementById("booking-display-popup").style.display = "block";
	const ticketContainer = getElement("ticketContainer");
	ticketContainer.hidden = false;

	getElement("ticketStatus").textContent = message;
	getElement("dateTime").textContent = (userChoice.date + " / " + userChoice.time);
	getElement("ticketMovie").textContent = (userChoice.movie + " - " + userChoice.language);
	getElement("ticketTheatre").textContent = (userChoice.theatre);
	getElement("ticketScreen").textContent = userChoice.screen;

	const seatsContainer = getElement("ConfirmedTicketSeats");
	seatsContainer.innerHTML = '';
	let totalAmount = 0;
	const row = document.createElement("span");
	let html = ``;
	selectedSeats.forEach(seat => {
		html += ` ${seat} `
	});
	row.innerHTML = (html);
	seatsContainer.appendChild(row);
	getElement("ConfirmedTicketAmount").textContent = userChoice.totalAmount;

}
function closeTicketDisplayPopup() {
	document.getElementById("booking-display-popup").style.display = "none";
}
async function handleBookingCancellation(bookingId) {
	await renderBookingContainer("popConfirmation");
	const confirmed = await confirmPopup("Do you want to Cancel this Booking?");
	if (confirmed == true) {
		const data = { bookingId: bookingId };
		const res = await fetchData("/cancelBooking", "POST", data);
		if (res != null && res.body.status == 200) {
			renderMyBookings();
			getElement("success-message").textContent = res.body.message;
			getElement("error-message").textContent = "";
		}
		else {
			renderMyBookings();
			getElement("error-message").textContent = res.body.message;
			getElement("success-message").textContent = "";
		}
		console.log(res);
	}
	else {
		renderMyBookings();
	}
}
