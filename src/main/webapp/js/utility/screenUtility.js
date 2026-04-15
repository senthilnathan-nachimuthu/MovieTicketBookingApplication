
function handleAddNewScreen() {
	layout = [];
	seatTypes = [];
	renderScreenDetail('addScreen');
}
async function renderScreenDetail(id) {
	const url = "TheatreFiles/" + id + ".html";
	await fetch(url).then(response => response.text()).then(html => {
		document.getElementById("screen-manage-container").innerHTML = html;
	});
}
function handleScreenDetails(id1, id2) {
	//event.preventDefault()
	console.log("kjhfd");
	const screenType = document.getElementById("screenType").value;
	const screenName = document.getElementById("screenName").value;
	if (generalvalidation(screenName) == false) {
		sendError("Screen Name should not be empty.");
		return false;
	}
	if (patternValidation(/^[a-zA-Z0-9\s]+$/, screenName) == false) {
		sendError("Screen Name should not contain special characters.");
		return false;
	}
	if (generalvalidation(screenType) == false) {
		sendError("Screen Type should not be empty.");
		return false;
	}
	if (patternValidation(/^[a-zA-Z0-9\s]+$/, screenType) == false) {
		sendError("Screen Type should not contain special characters.");
		return false;
	}
	closeError()
	renderNextContainer(id1, id2)
}
function renderNextContainer(id1, id2) {
	const container1 = document.getElementById(id1);
	const container2 = document.getElementById(id2);
	container1.hidden = true;
	container2.hidden = false;

}

function fillLayout(seatStructure) {
	layout = [];
	seatTypes = [];

	for (let i = 0; i < seatStructure.length; i++) {

		var seatType = seatStructure[i].seatType;
		if (!seatTypes.some(st => st.seatType === seatType)) {

			seatTypes.push({ seatType, Price: Number(seatStructure[i].seat_price) });
		}
	}

	for (let i = 0; i < seatStructure.length; i++) {

		const index = seatTypes.findIndex(st => st.seatType === seatStructure[i].seatType);
		const row = ({ seatTypeIndex: index, disabled: seatStructure[i].disabled, rowIndex: seatStructure[i].rowIndex, colIndex: seatStructure[i].colIndex });
		if (!layout[seatStructure[i].rowIndex]) {
			layout[seatStructure[i].rowIndex] = [];
		}
		layout[seatStructure[i].rowIndex][seatStructure[i].colIndex] = (row);
	}
}
function fillShowTimes(times) {

	for (let i = 0; i < times.length; i++) {
		showTimes.push(times[i]);
	}
}

function viewSeats(seatStructure) {

	fillLayout(seatStructure);
	showSavedLayout("screen-manage-container");
}
function addSeatType() {
	const seatType = document.getElementById("seatType").value;
	const seatTypePrice = document.getElementById("seatTypePrice").value;
	const row = document.getElementById("seatTypeRow").value;
	const col = document.getElementById("seatTypeCol").value;

	if (seatTypeValidation(seatType, seatTypePrice, row, col) == false) {
		return false;
	}
	if (!seatType || !seatTypePrice) {
		sendError("Invalid seat Type/Seat Price");
		return
	}
	if (seatTypes.some(st => st.seatType === seatType)) {
		sendError("Seat Type already exists.");
		return
	}
	seatTypes.push({ seatType, Price: Number(seatTypePrice), rows: row, columns: col });
	console.log(seatTypes);
	const index = seatTypes.findIndex(i => i.seatType === seatType);
	addInitialLayout(row, col, index);
	document.getElementById("seatType").value = "";
	document.getElementById("seatTypePrice").value = "";
	document.getElementById("seatTypeRow").value = "";
	document.getElementById("seatTypeCol").value = "";
	//console.log(seatTypes);
	renderSeatType();
}

function renderSeatType() {
	const container = document.getElementById("seatTypeContainer");
	container.innerHTML = `<h3>Added Seat Types`;
	seatTypes.forEach((t, i) => {
		const div = document.createElement("div");
		div.textContent = `${t.seatType}-${t.Price}`;
		const btn = document.createElement("i");
		btn.className = "fa fa-trash-o";

		btn.onclick = () => {
			seatTypes.splice(i, 1);

			layout.forEach((a, ii) => {

				let col = 0;
				while (col < a.length) {

					if (a[col].seatTypeIndex === i) {
						layout[ii].splice(col, 1);
					}
					else {
						col++;
					}
				}

			});
			renderSeatType();
		}

		const space = document.createElement("span");
		space.textContent = " ";
		div.appendChild(space);
		div.appendChild(btn);
		container.appendChild(div);
	});
}

function handleAddRow() {
	var row = [];
	row.push({ seatTypeIndex: 0, disabled: false, rowIndex: -1, colIndex: -1 });
	layout.push(row);
	//console.log(layout);
	renderLayout();
}
function addInitialLayout(row, col, index) {
	console.log(row + " " + col);
	const n = layout.length;
	console.log(n);
	const limit = parseInt(n) + parseInt(row);
	console.log(limit);
	for (let i = n; i < limit; i++) {
		var emptyRow = [];
		layout.push(emptyRow);
		console.log(i)
		for (let j = 1; j <= col; j++) {
			layout[i].push({ seatTypeIndex: index, disabled: false });
		}
	}
	console.log(layout);
}


function renderLayout() {

	const container = document.getElementById("layout-container");
	container.innerHTML = '';
	layout.forEach((row, rIndex) => {
		const rowDiv = document.createElement("div");
		rowDiv.className = "row";
		rowDiv.id = "seatRow";
		const Error = document.getElementById("seat-typeError");


		const addColBtn = document.createElement("button");
		addColBtn.className = "addCol";
		addColBtn.id = "addCol";
		addColBtn.textContent = "Add Column"
		addColBtn.classList.add('add-button-spacing');

		const removeColBtn = document.createElement("button");
		removeColBtn.className = "removeCol";
		removeColBtn.id = "removeCol";
		removeColBtn.classList.add('add-button-spacing');
		removeColBtn.textContent = "Remove Column"

		const removeRowBtn = document.createElement("button");
		removeRowBtn.id = "removeRow";
		removeRowBtn.className = "removeRow";
		removeRowBtn.textContent = "Remove Row"
		removeRowBtn.classList.add('add-button-spacing');

		addColBtn.onclick = () => addColumn(rIndex);
		removeColBtn.onclick = () => removeColumn(rIndex);
		removeRowBtn.onclick = () => removeRow(rIndex);
		row.forEach((seat, cIndex) => {


			console.log(seat.seatTypeIndex);
			const btn = document.createElement("button");
			btn.className = "seat";
			btn.classList.add('button-spacing');

			const type = seatTypes[seat.seatTypeIndex];
			btn.textContent = seat.disabled ? "X" : type?.seatType || "?";

			if (seat.disabled) btn.classList.add("disabled");

			btn.onclick = () => openSeatMenu(rIndex, cIndex);

			rowDiv.appendChild(btn);

		});
		if (row.length > 0) {
			rowDiv.appendChild(addColBtn);
			rowDiv.appendChild(removeColBtn);
			rowDiv.appendChild(removeRowBtn);

		}

		//container.appendChild(addColBtn);
		container.appendChild(rowDiv);
	});
}

function addColumn(rowIdx) {
	const col = prompt("Enter number of columns to add");
	const rowDiv = document.getElementById("rowDiv");
	for (let i = 1; i <= col; i++) {

		layout[rowIdx].push({ seatTypeIndex: -1, disabled: false });
	}
	renderLayout();
}
function removeColumn(rIndex) {

	layout[rIndex].pop();
	layout = layout.filter(e => e.length);

	const rowDiv = document.getElementById("seatRow");
	const addColBtn = document.getElementById("addCol");
	renderLayout();

}
function removeRow(rIndex) {
	layout[rIndex] = [];
	layout = layout.filter(e => e.length);
	renderLayout();
}
function handleLayoutSave() {
	document.getElementById("Error").textContent = ""
	if (layout.length == 0) {
		document.getElementById("Error").textContent = "Layout is undefined."
		return;
	}
	var flag = false;
	layout.forEach(a => {
		a.forEach(obj => {
			if (obj.seatTypeIndex === -1) {
				document.getElementById("Error").textContent = "Seat Type not assigned for Some seats.Assign to Continue";
				flag = true;
				return;
			}
		});
	});
	if (flag) {
		return false;
	}
	document.getElementById("ScreenContainer").hidden = true;
	document.getElementById("PropertyContainer").hidden = false;
	showSavedLayout("saved-layout-container");

}
function handlebackOperation(id1, id2) {
	document.getElementById(id1).hidden = false;
	document.getElementById(id2).hidden = true;
}
function handleAssignScreen() {
	if (seatTypes.length == 0) {
		document.getElementById("Error").textContent = "Add atleast one Seat type."
		return;
	}
	document.getElementById("Error").textContent = ""

	document.getElementById("ScreenContainer").hidden = false;
	document.getElementById("seatType-Container").hidden = true;
	renderLayout();
}
function showSavedLayout(containername) {
	const container = document.getElementById(containername);
	var size = 0;
	container.innerHTML = '';

	if (containername == "screen-manage-container") {
		container.innerHTML = `<br>	<button onclick="handleDisplayScreens()">Back</button> 		<h2>TotalCapacity:<span id="seatCapacity" style="color:red"></span>
				</h2>`
	}
	const html = document.createElement("div")
	html.innerHTML = `<p>-----------------------------------Screen This way-----------------------------------</p>`;
	container.appendChild(html);
	layout.forEach((row, rIndex) => {
		const rowDiv = document.createElement("div");
		rowDiv.className = "row";
		rowDiv.id = "seatRow";
		const Error = document.getElementById("seat-typeError");

		const rowValue = document.createElement("p");
		rowValue.id = "rowId";
		rowValue.textContent = intToExcelColumn(rIndex + 1);
		rowDiv.appendChild(rowValue);
		row.forEach((seat, cIndex) => {
			size++;

			layout[rIndex][cIndex].rowIndex = rIndex;
			layout[rIndex][cIndex].colIndex = cIndex;
			const btn = document.createElement("button");
			btn.className = "seat"; btn.classList.add('button-spacing');

			//console.log("seatType");
			//console.log(seat.seatTypeIndex);

			const type = seatTypes[seat.seatTypeIndex];
			//console.log(type);
			btn.textContent = seat.disabled ? "X" : type?.seatType || "?";

			if (seat.disabled) btn.classList.add("disabled");

			if (containername == "saved-layout-container") {

				btn.onclick = () => openSeatMenu(rIndex, cIndex);
			}

			rowDiv.appendChild(btn);
		});
		container.appendChild(rowDiv);
	});
	document.getElementById("seatCapacity").textContent = size;
}
function openSeatMenu(r, c) {

	document.getElementById("seatNum").textContent = (intToExcelColumn(r + 1) + (c + 1));
	document.getElementById("mymodal").style.display = "block";
	var closeBtn = document.getElementById("close");
	closeBtn.onclick = () => {
		document.getElementById("mymodal").style.display = "none";
	}

	const types = document.getElementById("chooseSeatType");
	sessionStorage.setItem("currRow", r);
	sessionStorage.setItem("currCol", c);

	if (layout[r][c].disabled == true) {
		document.getElementById("disableSeat").checked = true;
	}
	else {
		document.getElementById("disableSeat").checked = false;
	}
	seatTypes.forEach((type, i) => {
		const seatValue = type.seatType;
		let isExists = false;
		for (let i = 0; i < types.options.length; i++) {
			if (types.options[i].text === seatValue) {
				isExists = true;
				break;
			}
		}
		//console.log(isExists);
		if (isExists == false) {
			const option = document.createElement("option");
			option.id = (seatValue + "Option");
			option.value = i;
			option.textContent = seatValue;
			types.appendChild(option);
			//console.log(seatValue)

		}
	});

	for (let i = 0; i < types.options.length; i++) {
		let isexists = false;
		seatTypes.forEach((type) => {
			if (type.seatType == types.options[i].text) {
				isexists = true;
			}
		});

		if (isexists == false) {
			types.remove(i);
		}
	}
}
function intToExcelColumn(num) {
	let result = '';
	const A_CHAR_CODE = 'A'.charCodeAt(0);
	while (num > 0) {
		num--;
		const remainder = num % 26;
		result = String.fromCharCode(A_CHAR_CODE + remainder) + result;
		num = Math.floor(num / 26);
	}
	return result;
}

function closeModal() {

	console.log("closed");
	document.getElementById("mymodal").style.display = "none";


}
function handleSeatPropertySave() {
	const ch = document.getElementById("assignType").value;
	const seatTypeValue = document.getElementById("chooseSeatType").value;
	var isDisabled = document.getElementById("disableSeat");
	const i = sessionStorage.getItem("currRow");
	const j = sessionStorage.getItem("currCol");

	//console.log(isDisabled);
	if (ch == "byrow") {

		const row = layout[i];

		row.forEach((seat, colIndex) => {
			seat.seatTypeIndex = seatTypeValue;
			if (isDisabled.checked) {
				layout[i][colIndex].disabled = true;
			}
			else {
				layout[i][colIndex].disabled = false;
			}
		});

	}
	else if (ch == "bycol") {

	}
	else if (ch == "byseat") {

		layout[i][j].seatTypeIndex = seatTypeValue;
		if (isDisabled.checked) {
			//console.log("reached");
			layout[i][j].disabled = true;
		}
		else {
			layout[i][j].disabled = false;

		}
	}
	document.getElementById("mymodal").style.display = "none";
	renderLayout();

}
function addShowTime() {
	const time = document.getElementById("showTime").value;
	if (!time) {
		document.getElementById("Error").textContent = "Invalid Show time";
		return;
	}
	//console.log(time + " " + showTimes)
	if (showTimes.includes(time)) {
		document.getElementById("Error").textContent = "Show time already added";
		return;
	}
	document.getElementById("Error").textContent = "";

	showTimes.push(time);
	document.getElementById("showTime").value = "";
	renderShowTime();

}
function renderShowTime() {
	const container = document.getElementById("showTimeContainer");
	container.innerHTML = `<h3>Added Show Times</h3>`;

	showTimes.forEach((t, i) => {
		const div = document.createElement("div");
		div.textContent = `${t}`;
		const btn = document.createElement("i");
		btn.className = "fa fa-trash-o";
		btn.id = "showTimeDelete";

		btn.onclick = () => {
			showTimes.splice(i, 1);
			renderShowTime();
		}
		const space = document.createElement("span");
		space.textContent = " ";
		div.appendChild(space);
		div.appendChild(btn);
		container.appendChild(div);
	});
}
function handleScreenDetailsPage() {

	document.getElementById("PropertyContainer").hidden = true;
	document.getElementById("additionalDetails").hidden = false;
}
function handleFinishButton(event) {
	event.preventDefault();
	const Error = document.getElementById("Error");

	const screenType = document.getElementById("screenType").value;
	const screenName = document.getElementById("screenName").value;
	if (generalvalidation(screenType) == false) {
		Error.textContent = "Screen Type should not be empty.";
		return false;
	}
	if (patternValidation(/^[a-zA-Z0-9\s]+$/, screenType) == false) {
		Error.textContent = "Screen Type should not contain special characters.";
		return false;
	}
	if (showTimes.length == 0) {
		Error.textContent = "Add Atleast one show";
		return false;
	}
	Error.textContent = "";
	const thId = sessionStorage.getItem("theatreId");
	//console.log(thId);
	const data = { theatreId: thId, seatStructure: layout, seatTypes: seatTypes, screenType: screenType, showTimes: showTimes, screenName: screenName };
	//console.log(data);

	sendFinishResponse(data);

}
async function sendFinishResponse(data) {

	const res = await fetchData("/addScreen", "POST", data);
	console.log(data);
	if (res != null) {
		if (res.body.status == 200) {
			document.getElementById("theatre-success-message").textContent = res.body.message;
			handleDisplayTheatres('displayTheatres');
		}
		else {
			document.getElementById("theatre-error").textContent = res.body.message;
			handleDisplayTheatres('displayTheatres');
		}
	}
	////console.log(res);
}
async function handleScreenDeletion(screenId) {
	
	await renderTheatreDetail("popConfirmation");
	const confirmed = await confirmPopup("Do you want to Delete this Screen?(Note:All the Shows Created in this Screen are Removed)");
	if (confirmed) {
		const data = { ScreenId: screenId };
		deleteScreen(data);
	}
	else {
		document.getElementById("theatre-success-message").textContent = "Deletion Cancelled.";
		handleDisplayTheatres('displayTheatres');
	}


}
async function deleteScreen(data) {
	const res = await fetchData("/deleteScreen", "POST", data);
	console.log(res);
	if (getResponseCode(res) == 200) {
		document.getElementById("theatre-success-message").textContent = res.body.message;
		handleDisplayTheatres('displayTheatres');

	}
	else if (getResponseCode(res) == 500) {
		document.getElementById("theatre-error").textContent = "No response From Server";
		handleDisplayTheatres('displayTheatres');
	}
	else {
		document.getElementById("theatre-error").textContent = res.body.message;
		//handleDisplayTheatres('displayTheatres');
	}
}

async function handleScreenUpdation(data, screenType, showtime, screenId, screenName) {

	sessionStorage.setItem("screenId", screenId);
	fillLayout(data);
	fillShowTimes(showtime);
	await renderScreenDetail('addScreen')
	await renderSeatType();
	await renderShowTime();
	document.getElementById("screenType").value = screenType;
	document.getElementById("screenName").value = screenName;

	document.getElementById("finishButton").hidden = true;
	document.getElementById("updateButton").hidden = false;


}
async function handleScreenUpdate(event) {
	event.preventDefault();
	const screenId = sessionStorage.getItem("screenId");
	const Error = document.getElementById("Error");

	const screenType = document.getElementById("screenType").value;
	const screenName = document.getElementById("screenName").value;

	if (generalvalidation(screenType) == false) {
		Error.textContent = "Screen Type should not be empty.";
		return false;
	}
	if (patternValidation(/^[a-zA-Z0-9\s]+$/, screenType) == false) {
		Error.textContent = "Screen Type should not contain special characters.";
		return false;
	}
	if (showTimes.length == 0) {
		Error.textContent = "Add Atleast one show";
		return false;
	}
	Error.textContent = "";

	const data = { screenId: screenId, seatStructure: layout, seatTypes: seatTypes, screenType: screenType, showTimes: showTimes, screenName: screenName };

	console.log(showTimes);
	const res = await fetchData("/updateScreen", "POST", data);
	console.log(res);
	if (getResponseCode(res) == 200) {
		document.getElementById("theatre-success-message").textContent = res.body.message;
		document.getElementById("theatre-error").textContent = "";

		handleDisplayTheatres('displayTheatres');

	}
	else if (getResponseCode(res) == 500) {
		document.getElementById("theatre-error").textContent = "No response From Server";
		handleDisplayTheatres('displayTheatres');
	}
	else {
		document.getElementById("theatre-error").textContent = res.body.message;
		document.getElementById("theatre-success-message").textContent = "";
		handleDisplayTheatres('displayTheatres');
	}

}
function sendError(message) {
	const Error = document.getElementById("Error");
	Error.textContent = message
}
function closeError() {
	const Error = document.getElementById("Error");
	Error.textContent = ""
}
function seatTypeValidation(seatType, seatTypePrice, row, col) {

	if (generalvalidation(seatType) == false) {
		sendError("SeatType Should not be Empty");
		return false;
	}
	else if (patternValidation(/^[a-zA-Z\s]+$/, seatType) == false) {
		sendError("SeatType only contain characters.");
		return false;
	}
	if (generalvalidation(seatTypePrice) == false) {
		sendError("SeatTypePrice Should not be Empty and only contain numbers.");
		return false;
	}
	if (patternValidation(/^[0-9]+$/, seatTypePrice) == false) {
		sendError("SeatTypePrice only contain numbers.");
		return false;
	}
	else {
		if (seatTypePrice <= 0) {
			sendError("SeatTypePrice Should not be 0 or less");
			return false;
		}
	}
	if (generalvalidation(row) == false) {
		sendError("Row Should not be Empty and only contain numbers.");
		return false;
	}
	if (patternValidation(/^[0-9]+$/, row) == false) {
		sendError("row only contain numbers.");
		return false;
	}
	if (row <= 0) {
		sendError("Row Should be greater than Zero ");
		return false;
	}
	if (generalvalidation(col) == false) {
		sendError("Column Should not be Empty and only contain numbers.");
		return false;
	}
	if (patternValidation(/^[0-9]+$/, col) == false) {
		sendError("Column only contain numbers. ");
		return false;
	}
	if (col <= 0) {
		sendError("Column Should be greater than Zero");
		return false;
	}

	closeError();
	return true;

}

