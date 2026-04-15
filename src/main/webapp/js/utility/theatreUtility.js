
let seatTypes = [];
let layout = [];
let showTimes = [];
let movieLanguages = [];
let suggestedMovieLanguages = [];

window.addEventListener("load", () => {
	const saved = sessionStorage.getItem("message");
	console.log(saved);
	if (saved) {
		document.getElementById("theatre-success-message").textContent = saved;
		sessionStorage.removeItem("message");
	}
});

async function renderTheatreDetail(id) {
	await fetch("TheatreOperationsPage.html").then(response => response.text()).then(html => {
		document.getElementById("theatre-container").innerHTML = html;
	});
	await renderTheatreOperation(id);
}
async function renderTheatreOperation(id) {
	const url = "TheatreFiles/" + id + ".html";
	await fetch(url).then(response => response.text()).then(html => {
		document.getElementById("operation-container").innerHTML = html;
	});

}
function handleAddTheatre(event) {
	event.preventDefault();
	const theatreName = document.getElementById("theatreName").value.toLowerCase();
	const theatreLocation = document.getElementById("theatreLocation").value.toLowerCase();
	const credit = document.getElementById("theatreCreditPoint").value;

	if (!validateTheatreDetails(theatreName, theatreLocation, credit)) {
		return false;
	}

	const data = { theatreName: theatreName, theatreLocation: theatreLocation, theatreCredit: credit };
	addTheatre(data);
}

async function addTheatre(data) {
	const res = await fetchData("/addTheatre", "POST", data);
	console.log(res);
	if (res != null) {
		const statusCode = res.body.status;

		if (statusCode == 200) {
			document.getElementById("theatre-error").textContent = "";
			document.getElementById("theatre-success-message").textContent = "Theatre Added Sucessfully";
			handleDisplayTheatres('displayTheatres');
		}
		else {
			document.getElementById("theatre-success-message").textContent = "";
			document.getElementById("theatre-error").textContent = res.body.message;
		}
	}
}
async function handleUpdateTheatre(event) {
	event.preventDefault();
	const theatreId = sessionStorage.getItem("editingTheatre");
	if (theatreId != null) {

		const theatreName = document.getElementById("theatreName").value.toLowerCase();
		const theatreLocation = document.getElementById("theatreLocation").value.toLowerCase()
		const credit = document.getElementById("theatreCreditPoint").value;

		if (!validateTheatreDetails(theatreName, theatreLocation, credit)) {
			return false;
		}
		const data = { theatreId: theatreId, theatreName: theatreName, theatreLocation: theatreLocation, theatreCredit: credit };
		updateTheatre(data);
	}
}
async function updateTheatre(data) {
	const res = await fetchData("/updateTheatre", "POST", data);

	console.log(res);
	if (res != null) {
		if (res.body.status == 200) {
			document.getElementById("theatre-error").textContent = "";
			sessionStorage.removeItem("editingTheatre");
			document.getElementById("theatre-success-message").textContent = "Theatre Updated Successfully.";

			handleDisplayTheatres('displayTheatres');
		}
		else if (res.body.status == 201) {

			document.getElementById("theatre-error").textContent = "";
			sessionStorage.removeItem("editingTheatre");
			document.getElementById("theatre-success-message").textContent = "No changes";
			handleDisplayTheatres('displayTheatres');
		}
		else {
			sessionStorage.setItem("message", "");
			document.getElementById("theatre-success-message").textContent = "";
			document.getElementById("theatre-error").textContent = res.body.message;
		}
	}
}
async function handleDeletion(theatreId) {

	await renderTheatreDetail("popConfirmation");
	const confirmed = await confirmPopup("Do you want to Delete this Theatre? (Note:All the Shows Created in this Theatre are Removed)");
	if (confirmed) {
		const data = { theatreId: theatreId };
		deleteTheatre(data);
	}
	else {
		document.getElementById("theatre-success-message").textContent = "Deletion Cancelled.";
		document.getElementById("theatre-error").textContent = "";

		handleDisplayTheatres('displayTheatres');
	}

}
async function deleteTheatre(data) {
	const res = await fetchData("/deleteTheatre", "POST", data);

	if (res != null) {

		document.getElementById("theatre-success-message").textContent = res.body.message;
		handleDisplayTheatres('displayTheatres');
	}
}

async function popConfirmation(message) {
	await renderTheatreDetail('confirmAction');
	document.getElementById("confirm-message").textContent = message;
}

async function handleDisplayTheatres(id) {
	await renderTheatreDetail(id);
	clearMessages();

	const res = await fetchGetData("/getAllTheatres");
	if (res != null) {
		if (res.status == 200) {
			displayArrayResponse(res.data.TheatreDetails);
		}
		else if (res.status == 404) {
			document.getElementById("displayError").textContent = res.message;
		}
	}
}
async function handleDisplayScreens() {

	const theatreId = sessionStorage.getItem("theatreId");
	//console.log("session theatre-->"+theatreId);
	const data = { theatreId: theatreId };
	const url = "/getAllScreens";
	const parm = new URLSearchParams(data).toString();
	const finalurl = `${url}?${parm}`;
	console.log(finalurl);

	const res = await fetchGetData(finalurl);

	if (res != null) {
		const container = document.getElementById("screen-manage-container");
		container.innerHTML = `<h3 id="ScreenTitle"></h3>
				<ul id="screenList"></ul>
				<p id="screenError" style="color: red"></p>`

		if (res.status == 200) {
			displayScreenResponse(res.data.Screens);
		}
		else {
			document.getElementById("screenError").textContent = res.message;
		}

	}
}
function displayScreenResponse(screens) {

	const container = document.getElementById("screen-manage-container");
	const list = document.getElementById("screenList");
	list.innerHTML = '';

	document.getElementById('ScreenTitle').textContent = "Available Screens:"
	screens.forEach(screen => {
		const li = document.createElement("li");
		let html = `<p>ScreenNo: <span style="color:red">${screen.screenNo}</span>--ScreenName: <span style="color:red">${screen.screenName}</span>-- SeatCapacity: <span style="color:red">${screen.seatCapacity}</span>
				-- ScreenType: <span style="color:red">${screen.screenType}</span> -- Default Show Times: `;
		for (let i = 0; i < screen.showTimes.length; i++) {
			html += `<span style="color:red">${screen.showTimes[i]}</span> `
		}
		html += ` <button onclick='viewSeats(${JSON.stringify(screen.seatStructure).split('"').join('&quot;')})'>View Seats</button> 	<button onclick="handleScreenDeletion('${screen.screenId}')">Remove Screen</button>
		<button onclick='handleScreenUpdation(${JSON.stringify(screen.seatStructure).split('"').join('&quot;')},${JSON.stringify(screen.screenType).split('"').join('&quot;')},${JSON.stringify(screen.showTimes).split('"').join('&quot;')},${JSON.stringify(screen.screenId).split('"').join('&quot;')},${JSON.stringify(screen.screenName).split('"').join('&quot;')})'>Update Screen</button> </p>`;
		li.innerHTML = html;
		list.appendChild(li);
	});
}
//console.log(res.data);
//console.log(res);

function displayArrayResponse(res) {

	const list = document.getElementById("displayList");
	list.innerHTML = '';
	document.getElementById('displayTitle').textContent = "Available Theatres"
	res.forEach(theatre => {
		const li = document.createElement("li");
		li.innerHTML = `<p >TheatreId: <span style="color:green">${theatre.theatreId}
				</span> <br> Name: <span style="color:green">${theatre.theatreName.toUpperCase()}
		</span> ---- Location: <span style="color:green">${theatre.theatreLocation.toUpperCase()}</span> 
	<br>	<button onclick="renderEditOption('${theatre.theatreId}','${theatre.theatreName}','${theatre.theatreLocation}','${theatre.theatreCredit}')">EDIT</button> 
	<button onclick="handleDeletion('${theatre.theatreId}')">DELETE</button> <button id="manageScreenButton" onclick="handleManageScreens('${theatre.theatreId}','${theatre.theatreName}','${theatre.theatreLocation}','${theatre.theatreCredit}')">MANAGE SCREEN</button>
	</p>`;
		list.appendChild(li);
	});
}

async function handleManageScreens(theatreId, theatreName, theatreLocation, credit) {

	seatTypes = [];
	layout = [];
	showTimes = [];
	sessionStorage.removeItem("theatreId");
	sessionStorage.setItem("theatreId", theatreId);
	await renderTheatreDetail('manageScreen');
	await handleDisplayScreens();
	document.getElementById("displayTheatreName").textContent = theatreName.toUpperCase();
	document.getElementById("displayTheatreLocation").textContent = theatreLocation.toUpperCase();

}

async function renderEditOption(theatreId, theatreName, theatreLocation, credit) {
	await renderTheatreDetail('addTheatre');
	clearMessages();
	fillEditDetails(theatreId, theatreName, theatreLocation, credit);

}
function fillEditDetails(theatreId, theatreName, theatreLocation, credit) {

	document.getElementById("theatreId").textContent = ("Theatre Id-" + theatreId);
	sessionStorage.setItem("editingTheatre", theatreId);
	document.getElementById("theatreName").value = theatreName;
	document.getElementById("theatreLocation").value = theatreLocation;
	document.getElementById("theatreCreditPoint").value = credit;
	document.getElementById("updateButton").hidden = false;
	document.getElementById("addButton").hidden = true;
}

function validateTheatreDetails(theatreName, theatreLocation, credit) {

	const Error = document.getElementById("theatre-error");
	if (generalvalidation(theatreName) == false) {
		Error.textContent = "Theatrename Should not be Empty";
		return false;
	}
	else if (patternValidation(/^[a-zA-Z\s]+$/, theatreName) == false) {
		Error.textContent = "Theatre Name only contain characters.";
		return false;
	}
	if (generalvalidation(theatreLocation) == false) {
		Error.textContent = "Theatre Location Should not be Empty";
		return false;
	}
	else if (patternValidation(/^[a-zA-Z\s]+$/, theatreLocation) == false) {
		Error.textContent = "Theatre Location only contain characters.";
		return false;
	}
	if (credit < 0) {
		Error.textContent = "Credit Should be Greater than or equal to 0";
		return false;
	}
	return true;
}

async function addMovie(fileName) {
	movieLanguages = [];
	clearMessages();
	await renderTheatreDetail(fileName);
}
