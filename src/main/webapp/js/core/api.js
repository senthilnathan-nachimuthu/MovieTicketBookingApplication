function fetchData(url, type, data) {
    console.log(url);
	console.log(data);
	return fetch(url, {
		method: type,
		headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
		body: JSON.stringify(data)
	})
		.then(response => {
			if (response.status === 401) {
				window.location.href = "/login";
				return null;
			}

			return response.json().then(json => ({
				status: response.status,
				body: json
			}));
		})
		.catch(err => {
			console.log(err);
			return null;
		});
}


function fetchGetData(url) {
	return fetch(url, {
		method: 'GET',
		headers: {
			'X-Requested-With': 'XMLHttpRequest'
		}
	})
		.then(response => {

			if (response.status === 401) {
				window.location.href = "/login";
				return null;
			}
			if (!response.ok) {
				throw new Error("Bad request." + response.status);
			}
			return response.json();
		})
		.then(res => {

			//	console.log(res);
			return res;

		})
		.catch(error => {

		});

}
