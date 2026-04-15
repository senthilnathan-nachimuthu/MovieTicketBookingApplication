<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
if (session.getAttribute("username") == null) {
	response.sendRedirect("Login.jsp");

}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>MTB</title>
<link rel="stylesheet" href="commonCss.css">

<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/core/api.js"></script>

</head>
<body>

	<form action="userHome" method="post">
		<h1>Welcome! ${username}</h1>
		<p id="details"></p>
	</form>
	<form action="logout">
		<button type="submit" name="logoutAction" value="logout">Logout</button>
	</form>
	<br>
	<button type="button" onclick="popPasswordContainer()">My
		Wallet</button>

	<button type="button" onclick="getAccount()">My Account</button>

	<button type="button" onclick="renderBookingOption()">Book-Movie</button>
	<button type="button" onclick="renderMyBookings()">My Bookings</button>

	<div id="booking-operation-container"></div>


	<p style="color: green" id="success-message"></p>
	<p style="color: red" id="error-message"></p>

	<br />

	<div id="container"></div>
	<p style="color: blue" id="message-account"></p>


	<button type="button" id="passConfirmButton" hidden=true
		onclick="handlePassword(event,'addMoney')">Confirm</button>


	<script type="text/javascript"
		src="<%=request.getContextPath()%>/js/utility/walletOperations.js">
		
	</script>
	<script type="text/javascript"
		src="<%=request.getContextPath()%>/js/utility/bookingUtility.js">
		
	</script>
	<script type="text/javascript"
		src="<%=request.getContextPath()%>/js/utility/commonUtility.js">
		
	</script>
	<script type="text/javascript"
		src="<%=request.getContextPath()%>/js/utility/theatreUtility.js">
		
	</script>
	<script type="text/javascript"
		src="<%=request.getContextPath()%>/js/utility/screenUtility.js">
		
	</script>



</body>
</html>