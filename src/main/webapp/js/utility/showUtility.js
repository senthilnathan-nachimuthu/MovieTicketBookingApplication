
async function handleCreateShows(movieObj) {
	const movie = JSON.parse(movieObj);
	const movieId = movie.movieId;
	console.log(movie, movieId);
	await renderTheatreDetail("createShows");
	sessionStorage.setItem("showMovie", movieId);
	getElement("movieTitle").textContent = movie.movieName;
	addSelectLanguages(movie.availableLanguages);
	const res = await fetchGetData("/getAllTheatres");
	if (res != null) {
		if (res.status == 200) {
			displayTheatreArrayResponse(res.data.TheatreDetails);
		}
		else if (res.status == 404) {
			document.getElementById("displayError").textContent = res.message;
		}
	}
}
function addSelectLanguages(languageList) {
	const languageSelect = getElement("languageSelect");
	languageList.forEach(language => {
		const option = document.createElement("option");
		option.textContent = language;
		option.id = language;
		languageSelect.appendChild(option);
	})
}
function displayTheatreArrayResponse(res) {
	const list = document.getElementById("showTheatreList");
	list.innerHTML = '';
	res.forEach(theatre => {
		const li = document.createElement("li");
		const theatreKey = theatre.theatreId;
		li.id = theatreKey + "theatreList";
		li.innerHTML = `<p><input type="checkbox" class="theatre-box"  data-theatre="${theatreKey}" onclick="handleToggleAllScreens(this,${theatreKey})" id="${theatre.theatreId}">TheatreId: <span style="color:green">${theatre.theatreId}
				</span> <br> Name: <span style="color:green">${theatre.theatreName.toUpperCase()}
		</span> ---- Location: <span style="color:green">${theatre.theatreLocation.toUpperCase()}</span></p>`;
		const iconId = theatreKey + "dropButton";
		const dropIcon = document.createElement("i");
		dropIcon.className = "fa fa-caret-down";
		dropIcon.id = iconId;
		dropIcon.textContent = " Screens";
		dropIcon.onclick = () => handleDropDown(li.id, theatreKey);
		li.append(dropIcon);
		//	console.log(handleDisplaySubList(li.id, theatreKey));
		if (handleDisplaySubList(li.id, theatreKey)) {
			list.appendChild(li);
		}
	});
}
function handleDropDown(ele, theatreId) {
	const parentList = document.getElementById(ele);
	const exSubList = parentList.querySelector("ul");
	const iconId = theatreId + "dropButton";
	const icon = document.getElementById(iconId);
	if (exSubList) {

		if (exSubList.hidden == false) {
			exSubList.hidden = true;
			icon.classList.remove("fa-caret-up");
			icon.classList.add("fa-caret-down");
			return;
		}
		else {
			exSubList.hidden = false;
			icon.classList.remove("fa-caret-down");
			icon.classList.add("fa-caret-up");
			return
		}
	}
}
async function handleDisplaySubList(ele, theatreId) {


	const data = { theatreId: theatreId };
	const url = "/getAllScreens";
	const parm = new URLSearchParams(data).toString();
	const finalurl = `${url}?${parm}`;

	const res = await fetchGetData(finalurl);

	if (res != null) {

		if (res.status == 200) {

			displayScreenAsSubList(ele, JSON.stringify(res.data.Screens), theatreId);
			return true;
		}
		else {
			const parentList = document.getElementById(ele);
			const sublist = document.createElement('ul');
			const error = document.createElement("p");
			error.style.color = "red";
			error.textContent = res.message;
			sublist.appendChild(error);
			parentList.appendChild(sublist);
			sublist.hidden = true;
			return false;
		}
	}
}
function displayScreenAsSubList(ele, screenObj, theatreId) {
	const screens = JSON.parse(screenObj);
	const parentList = document.getElementById(ele);
	const sublist = document.createElement('ul');
	const theatreMaster = document.querySelector(`.theatre-box[data-theatre='${theatreId}']`);

	screens.forEach(screen => {
		const li = document.createElement("li");
		const screenKey = screen.screenId;
		let html = `<p><input type="checkbox" class="screen-box" data-theatre='${screen.theatreId}' id="${screenKey}" data-name="${screen.screenName}" onchange="handleToggleSpecificScreen(this,'${theatreId}')">ScreenNo: <span style="color:red">${screen.screenNo}</span>--ScreenName: <span style="color:red">${screen.screenName}</span>-- SeatCapacity: <span style="color:red">${screen.seatCapacity}</span>
					-- ScreenType: <span style="color:red">${screen.screenType}</span> -- Default Show Times: `;
		for (let i = 0; i < screen.showTimes.length; i++) {
			html += `<input type="checkbox" class="show-time-box" data-screen='${screenKey}' id="${screen.showTimes[i]}"><span style="color:red">${screen.showTimes[i]}</span> `
		}
		li.innerHTML = html;
		sublist.appendChild(li);
	});
	parentList.appendChild(sublist);
	handleToggleAllScreens(theatreMaster, theatreId)
	sublist.hidden = true;
}
function syncTheatreCheckeBoxes(ele) {
	const parent = document.getElementById(ele);
	const theatreBox = document.querySelector(".theatre-box");
	const ScreenBox = document.getElementsByClassName("screen-box");
	console.log(ScreenBox);
	for (let i = 0; i < ScreenBox.length; i++) {
		ScreenBox[i].checked = theatreBox.checked;
	}


}
function handleToggleAllTheatres(master) {
	checkTheBoxes("theatre-box", master);
	checkTheBoxes("screen-box", master);
}
function checkTheBoxes(className, master) {
	const box = document.getElementsByClassName(className);
	for (let i = 0; i < box.length; i++) {
		box[i].checked = master.checked;
		toggleShowTimeCheckBox(box[i]);
	}
}
function handleToggleAllScreens(theatreMaster, theatreId) {
	const box = document.querySelectorAll(`.screen-box[data-theatre='${theatreId}']`);
	for (let i = 0; i < box.length; i++) {
		box[i].checked = theatreMaster.checked;
		toggleShowTimeCheckBox(box[i]);
	}
	const selectAll = document.getElementById("selectAllBox");
	const theatreBoxes = document.getElementsByClassName("theatre-box");
	updateToggle(theatreBoxes, selectAll);
}
function toggleShowTimeCheckBox(screenBox) {
	const screenBoxId = screenBox.id;
	const defaultTimeBoxes = document.querySelectorAll(`.show-time-box[data-screen='${screenBoxId}']`);
	for (let i = 0; i < defaultTimeBoxes.length; i++) {
		defaultTimeBoxes[i].checked = screenBox.checked;
	}
}
function handleToggleSpecificScreen(screenBox, theatreId) {
	toggleShowTimeCheckBox(screenBox);
	const allScreenBoxes = document.querySelectorAll(`.screen-box[data-theatre='${theatreId}']`)
	const theatreBox = document.querySelector(`.theatre-box[data-theatre='${theatreId}']`);
	updateToggle(allScreenBoxes, theatreBox);
}
function updateToggle(boxes, targetBox) {

	for (let i = 0; i < boxes.length; i++) {
		if (boxes[i].checked == false) {
			targetBox.checked = false;
			return;
		}
	}
	targetBox.checked = true;

}
function CollectAllChoosenTheatres() {
	const box = document.getElementsByClassName("screen-box");
	choosedScreens = [];
	for (let i = 0; i < box.length; i++) {
		if (box[i].checked == true) {
			const showTimes = [];
			const choosedScreenTimes = document.querySelectorAll(`.show-time-box[data-screen='${box[i].id}']`);
			for (let i = 0; i < choosedScreenTimes.length; i++) {
				if (choosedScreenTimes[i].checked == true) {
					showTimes.push(choosedScreenTimes[i].id);
				}
			}
			if (showTimes.length == 0) {
				getElement("Error").textContent = "Select Atleast one show time for a screen " + box[i].dataset.name;
				return false;
			}
			getElement("Error").textContent = "";
			choosedScreens.push({ screenId: box[i].id, showTimes: showTimes });
		}

	}
	if (choosedScreens.length == 0) {
		getElement("Error").textContent = "Choose Atleast one Screen";
		return false;
	}
	getElement("Error").textContent = "";
	getElement('theatreListDiv').hidden = true;
	getElement('dateSelection').hidden = false;

	console.log(choosedScreens);
}
async function handleShowCreation() {
	const fromDate = getElementData("fromDate");
	const toDate = getElementData("toDate");
	const language = getElementData("languageSelect");
	const error = getElement("Error");
	if (!generalvalidation(fromDate)) {
		error.textContent = "Invalid from Date";
		return false;
	}
	if (!generalvalidation(toDate)) {
		error.textContent = "Invalid To Date";
		return false;
	}

	if (!isValidAndGreaterEqualToday(fromDate, toDate)) {
		return false;
	}
	error.textContent = "";
	const movieId = sessionStorage.getItem("showMovie");
	const data = { fromDate: fromDate, toDate: toDate, choosedScreens: choosedScreens, movieId: movieId, movieLanguage: language };
	const res = await fetchData("/createShow", "POST", data);
	showMessageFromServer(res);
	handleDisplayShows();
	if (getResponseCode(res) == 201) {

		getElement('theatre-success-message').textContent = "Shows created But Some Shows are Skipped.[Due to Date and Time Conflict]";
		const button = document.createElement("button");
		button.textContent = "View Skipped Shows";
		getElement("multiple-error").appendChild(button);

		const errors = res.body.data;
		for (let i = 0; i < errors.length; i++) {
			console.log(errors[i]);
			const reason = errors[i].Reason;
			console.log(reason);
			skippedShowsErrors.push(errors[i]);

		}
		button.onclick = () => renderErrorReport(skippedShowsErrors);
	}
}
function renderErrorReport(errors) {

	const errorContainer = getElement('multiple-error');
	errorContainer.innerHTML = '';
	if (errors == null || errors.length == 0) {
		const errorElement = document.createElement("p");
		errorElement.textContent = "No Error Exists";
		errorContainer.appendChild(errorElement);
		return;
	}
	errorContainer.innerHTML = '';
	for (let i = 0; i < errors.length; i++) {
		const errorElement = document.createElement("p");
		errorElement.innerHTML=`ScreenId: <span style="color:green">${errors[i].screenId}</span>-- ShowDate: <span style="color:green">${errors[i].showDate}</span>--
		ShowTime: <span style="color:green">${errors[i].showTime}</span>-- Reason: <span style="color:green">${errors[i].Reason}</span> `;
		errorContainer.appendChild(errorElement);

	}
	const button = document.createElement("button");
	button.textContent = "Close";
	errorContainer.appendChild(button);
	button.addEventListener("click", () => {
		errorContainer.innerHTML = ''
	});

}
function isValidAndGreaterEqualToday(inputDateString, inputDateString2) {

	const inputDate = new Date(inputDateString);
	const toDate = new Date(inputDateString2);
	const error = getElement("Error");
	if (isNaN(inputDate.getTime())) {
		return false;
	}
	const today = new Date();
	today.setHours(0, 0, 0, 0);
	inputDate.setHours(0, 0, 0, 0);
	if (inputDate.getTime() < today.getTime() || toDate.getTime() < today.getTime()) {
		error.textContent = "Invalid Date , Date should be greater than or equal to Today's date";
		return false;
	}
	if (toDate.getTime() < inputDate.getTime()) {
		error.textContent = "Invalid ToDate , Date should be greater than or equal to fromDate date";
		return false;
	}
	return true;
}

async function handleDisplayShows() {
	const res = await fetchGetData("/getAllShows");
	console.log(res);
	if (res != null) {
		displayShows(res);
	}
}

async function displayShows(res) {
	await renderTheatreDetail('displayShows');

	if (res.status == 404) {
		document.getElementById("displayError").textContent = res.message;
		return false;
	}
	const data = res.data.Shows;
	const container = getElement('showList');
	const title = document.getElementById("showTitle");
	title.textContent = "Available Shows";
	const totalShows = getElement('totalShows');
	totalShows.textContent = `${data.length}`;
	const list = getElement('showList');
	list.innerHTML = '';

	data.forEach(show => {
		const li = document.createElement("li");
		li.innerHTML = `<p><input type="checkbox" class="show-box" id="${show.showId}"> ShowId: <span style="color:green">${show.showId}
					</span> AvailableCapacity: <span style="color:green">${show.availableCapcaity}
			</span> Date: <span style="color:green">${show.date}
								</span>
								Time: <span style="color:green">${show.time}
											</span>
			 Movie: <span style="color:green">${show.movieName}
								</span>
								Language: <span style="color:green">${show.movieLanguage}
											</span>
								Theatre: <span style="color:green">${show.theatre}
											</span>
											ScreenName: <span style="color:green">${show.screenName}</span>
											<button onclick="handleChangeShowPricing('${show.showId}')">Change Pricing</button>
											<button onclick="handleChangeShowMovie('${show.showId}','${show.movieName}','${show.movieLanguage}')">Change-Movie</button> 
											</p>`;
		list.appendChild(li);
	});
}

async function handleChangeShowMovie(showId, movieName, movieLanguage) {
	const res = await fetchGetData("/getAllMovies");
	console.log(res);
	if (res.status == 200) {
		openMovieSelectionPopup(res.data.Movies, showId, movieName, movieLanguage);
	}
}
let globalMovieList = [];

function openMovieSelectionPopup(movies, showId, currentMovie, currentLanguage) {

	globalMovieList = movies;
	getElement("movieChangeError").textContent = "";
	document.getElementById("popupShowId").value = showId;
	document.getElementById("currentMovie").value = currentMovie;
	document.getElementById("currentLanguage").value = currentLanguage;
	const movieDropdown = document.getElementById("movieDropdown");
	movieDropdown.innerHTML = "";

	movies.forEach(movie => {
		const option = document.createElement("option");
		option.value = movie.movieId;
		option.textContent = movie.movieName;
		if (movie.movieName === currentMovie) {
			option.selected = true;
		}
		movieDropdown.appendChild(option);
	});

	handleMovieSelection(currentLanguage);
	document.getElementById("moviePopup").style.display = "flex";
}
function handleMovieSelection(selectedLang = null) {

	const movieId = document.getElementById("movieDropdown").value;
	const languageDropdown = document.getElementById("languageDropdown");

	languageDropdown.innerHTML = "";

	const selectedMovie = globalMovieList.find(m => m.movieId == movieId);

	if (selectedMovie && selectedMovie.availableLanguages) {

		selectedMovie.availableLanguages.forEach(lang => {
			const option = document.createElement("option");
			option.value = lang;
			option.textContent = lang;

			if (lang === selectedLang) {
				option.selected = true;
			}

			languageDropdown.appendChild(option);
		});
	}
}
async function submitMovieChange() {
	const movieId = document.getElementById("movieDropdown").value;
	const language = document.getElementById("languageDropdown").value;
	const showId = getElementData('popupShowId');

	const selectedMovie = globalMovieList.find(m => m.movieId == movieId);
	console.log(getElementData("currentMovie"), selectedMovie.movieName);
	if (getElementData("currentMovie").toLowerCase() == (selectedMovie.movieName).toLowerCase() && getElementData("currentLanguage").toLowerCase() == language.toLowerCase()) {
		getElement("movieChangeError").textContent = "No Changes Made";
		return false;
	}
	getElement("movieChangeError").textContent = "";
	console.log(movieId, language, showId);
	const data = { showId: showId, movieId: movieId, movieLanguage: language }

	const res = await fetchData("/updateShowMovie", "POST", data);
	showMessageFromServer(res);
	if (getResponseCode(res) == 200) {
		handleDisplayShows();
	}
	console.log(res);
}
async function handleChangeShowPricing(showId) {

	const data = { showId: showId };
	const url = "/getSeatTypes";
	const param = new URLSearchParams(data).toString();
	const finalUrl = `${url}?${param}`;
	const res = await fetchGetData(finalUrl);
	console.log(res.data);
	if (res.status == 200) {
		sessionStorage.setItem("showId", showId);
		openChangePricingTab(res.data);
	}
}
let tempPricing = [];
function openChangePricingTab(seatTypes) {
	//console.log(data);
	tempPricing = seatTypes;
	getElement('popupError').textContent = "";
	getElement('popupTitle').textContent = "Show Pricing";
	document.getElementById("pricingPopup").style.display = "block";
	const container = document.getElementById("pricingContainer");
	container.innerHTML = "";


	seatTypes.forEach(seat => {

		const row = document.createElement("div");
		row.className = "price-row";

		row.innerHTML = `
	        <span class="seat-name" style="color:red">SeatType- ${seat.seatType}</span><br>
	        <span class="old-price">
	            Old Price: ₹${seat.Price}
	        </span>
			<br><br>
			<label>New Price: </label> 
	        <input 
	            type="number"
	            class="new-price"
	            data-seatTypeId="${seat.seatTypeId}"
	            value="${seat.Price}"
	            placeholder="New Price"
	        />
			<br><br>
	    `;
		container.appendChild(row);
	});
	console.log(container);

}
async function savePricing() {
	const newPrices = document.querySelectorAll(".new-price");
	let updatedPrice = [];
	newPrices.forEach(seat => {
		updatedPrice.push({ seatTypeId: parseInt(seat.dataset.seattypeid), Price: parseInt(seat.value) });
	});
	if (!isChanged(updatedPrice)) {
		getElement('popupError').textContent = "No changes made in price";
		return false;
	}
	getElement('popupError').textContent = "";

	const data = { showId: sessionStorage.getItem("showId"), seatTypes: updatedPrice };
	const res = await fetchData("/updateShowPrice", "POST", data);
	console.log(res);
	showMessageFromServer(res);
	if (getResponseCode(res) == 200) {
		closePricingPopup();
	}
}
function isChanged(seatTypes) {
	console.log(seatTypes);
	console.log(tempPricing);
	let found = false;
	seatTypes.forEach(seat => {

		tempPricing.forEach(pricing => {
			if (seat.seatTypeId == pricing.seatTypeId && seat.Price != pricing.Price) {
				found = true;
			}
		})
	})
	return found;

}

function closePricingPopup() {
	document.getElementById("pricingPopup").style.display = "none";

}
function closeMoviePopup() {
	document.getElementById("moviePopup").style.display = "none";

}
function toggleAllShows(selectAll) {
	const showBoxes = document.getElementsByClassName("show-box");

	for (let i = 0; i < showBoxes.length; i++) {
		showBoxes[i].checked = selectAll.checked;
	}

}
async function handleShowDeletion() {
	let showToDelete = [];
	const showBoxes = document.getElementsByClassName("show-box");

	for (let i = 0; i < showBoxes.length; i++) {
		if (showBoxes[i].checked == true) {
			showToDelete.push(showBoxes[i].id);
		}

	}
	if (showToDelete.length == 0) {
		getElement('displayError').textContent = "No Show Selected.";
		return false;
	}
	await renderTheatreDetail("popConfirmation");
	const confirmed = await confirmPopup("Do you want to Delete this Show? (Note: All Bookings in the show will be deleted)");
	if (confirmed) {
		const data = { showId: showToDelete };
		const res = await fetchData("/deleteShows", "POST", data);
		console.log(res);
		showMessageFromServer(res);
		handleDisplayShows();

	}
	else {
		handleDisplayShows();
	}
	console.log(confirmed);
}


