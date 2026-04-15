let choosedScreens = [];
let skippedShowsErrors = [];
function handleAddMovieLanguage() {
	const movieLanguage = getElementData('movieLanguage').toLowerCase();
	const Error = getElement('Error');
	if (!movieLanguage || generalvalidation(movieLanguage) == false || patternValidation(/^[a-zA-Z\s]+$/, movieLanguage) == false) {
		Error.textContent = "Invalid Movie Language.";
		return false;
	}
	if (movieLanguages.includes(movieLanguage)) {
		Error.textContent = "Movie Language Already added.";
		return false;
	}
	Error.textContent = "";
	movieLanguages.push(movieLanguage.toLowerCase());
	renderMovieLanguage();
}
function renderMovieLanguage() {

	const languageContainer = getElement('movieLanguageContainer');
	languageContainer.innerHTML = '';
	languageContainer.innerHTML = ('<p style="color:green">Added Movie Languages</p>')
	movieLanguages.forEach((language, i) => {

		const div = document.createElement("div");
		div.textContent = language.toUpperCase();
		languageContainer.appendChild(div);

		const btn = document.createElement("i");
		btn.className = "fa fa-trash-o";

		btn.onclick = () => {
			movieLanguages.splice(i, 1);
			renderMovieLanguage();
		}
		div.appendChild(btn);
	})
	getElement('movieLanguage').value = "";
}
async function handleAddMovieRequest(event) {
	event.preventDefault();
	const movieName = getElementData('movieName');
	const movieDuration = getElementData('movieDuration');
	if (movieDetailValidation(movieName, movieDuration) == false) {
		return false;
	}

	getElement('Error').textContent = "";
	const data = { movieName: movieName, movieDuration: movieDuration, movieLanguages: movieLanguages };
	const res = await fetchData("/addMovie", "POST", data);
	showMessageFromServer(res);
	if (getResponseCode(res) == 200) {
		handleDisplayMovies();
	}

	console.log(res);
}

function movieDetailValidation(movieName, movieDuration) {
	console.log(movieName, movieDuration);
	if (movieLanguages.length == 0) {
		getElement('Error').textContent = "Add atleast one movie Language";
		return false;
	}
	if (!movieName || generalvalidation(movieName) == false || patternValidation(/^[a-zA-z0-9\s]+$/, movieName) == false) {
		getElement('Error').textContent = "Invalid movie name";
		return false;
	}
	if (!movieDuration || movieDuration <= 0 || patternValidation(/^[0-9]+$/, movieDuration) == false) {
		getElement('Error').textContent = "Invalid movie Duration";
		return false;
	}
	return true;
}
async function handleDisplayMovies() {
	clearMessages();
	const res = await fetchGetData("/getAllMovies");
	displayMovies(res);
	console.log(res);
}
async function displayMovies(res) {
	await renderTheatreDetail('displayTheatres');

	if (res.status == 404) {
		document.getElementById("displayError").textContent = res.message;
		return false;
	}
	const movies = res.data.Movies;
	const container = getElement('displayList');
	const title = document.getElementById("displayTitle");
	title.textContent = "Available Movies";

	movies.forEach(movieObj => {
		const list = document.createElement("li");
		let html = `<p>Movie-Name: <span style="color:red">${movieObj.movieName}</span> 
		 Movie-Duration: <span style="color:red">${movieObj.movieDuration}</span> 	 <br>Available-Languages: `;
		console.log(movieObj);
		let lang = movieObj.availableLanguages;
		for (let i = 0; i < lang.length; i++) {
			html += `<span style="color:green">${lang[i].toUpperCase()}</span> `
		}
		html += `<br><button onclick="handleMovieDeletion(${movieObj.movieId})">delete</button> 
		<button onclick="handleMovieEdit('${movieObj.movieId}','${movieObj.movieName}','${movieObj.movieDuration}',${JSON.stringify(movieObj.availableLanguages).split('"').join('&quot;')})">Edit</button>
		<button onclick="handleCreateShows('${JSON.stringify(movieObj).split('"').join('&quot;')}')">Create Shows</button> 
		</p>`;
		list.innerHTML = html;
		container.appendChild(list);
	})
}
async function handleMovieDeletion(movieId) {

	await renderTheatreDetail("popConfirmation");
	const confirmed = await confirmPopup("Do you want to Delete this Movie? (Note:All the Shows Created Using this Movie will be Removed)");
	//await popConfirmation("Do you want to Delete this Movie? (Note:All the Shows Created Using this Movie will be Removed)");
	if (confirmed) {
		const data = { movieId: movieId };
		deleteMovie(data);
	}
	else {
		document.getElementById("theatre-success-message").textContent = "Deletion Cancelled.";
		handleDisplayMovies();
	}

}
async function deleteMovie(data) {
	const res = await fetchData("/deleteMovie", "POST", data);
	console.log(res);
	showMessageFromServer(res);
	if (getResponseCode(res) == 200) {
		handleDisplayMovies();
	}
}
async function handleMovieEdit(movieId, movieName, movieDuration, movieLanguageArray) {
	console.log(movieLanguageArray);
	await addMovie('/addMovie');
	getElement('movieName').value = movieName;
	getElement('movieDuration').value = movieDuration;
	getElement('submitMovieDetail').hidden = true;
	getElement('updateMovieDetail').hidden = false;

	sessionStorage.setItem("movieId", movieId);

	for (let i = 0; i < movieLanguageArray.length; i++) {
		movieLanguages.push(movieLanguageArray[i]);
	}
	renderMovieLanguage();
}

async function handleUpdateMovieRequest(event) {
	event.preventDefault();
	const movieName = getElementData('movieName');
	const movieDuration = getElementData('movieDuration');
	if (movieDetailValidation(movieName, movieDuration) == false) {
		return false;
	}
	getElement('Error').textContent = "";
	const data = { movieId: sessionStorage.getItem("movieId"), movieName: movieName, movieDuration: movieDuration, movieLanguages: movieLanguages };
	const res = await fetchData("/updateMovie", "POST", data);
	showMessageFromServer(res);
	if (getResponseCode(res) == 200) {
		sessionStorage.removeItem("movieId");
		handleDisplayMovies();
	}
	console.log(res);
}
/*async function handleCreateShows(movieObj) {
	const movie = JSON.parse(movieObj);
	const movieId = movie.movieId;
	console.log(movie, movieId);
	await renderTheatreDetail("createShows");
	sessionStorage.setItem("showMovie", movieId);
	getElement("movieTitle").textContent = movie.movieName;
	addSelectLanguages(movie.movieLanguages);
	const res = await fetchGetData("/movieTicketBooking/getAllTheatres");
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
	console.log(languageList);
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
		list.appendChild(li);
		handleDisplaySubList(li.id, theatreKey);
	});
}*/