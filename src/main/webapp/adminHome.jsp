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
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<meta charset="UTF-8">
<title>MTB-Admin</title>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/utility/theatreUtility.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/core/api.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/utility/screenUtility.js"></script>
<script src="<%=request.getContextPath()%>/js/utility/movieUtility.js"
	type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/js/utility/commonUtility.js"
	type="text/javascript"></script>
	<script src="<%=request.getContextPath()%>/js/utility/showUtility.js"
	type="text/javascript"></script>
<link rel="stylesheet" href="commonCss.css">
</head>
<body>

	<form action="adminHome" method="post">
		<h1>Hello Admin- ${username}</h1>
	</form>

	<form action="logout">
		<button type="submit" name="logoutAction" value="logout">Logout</button>
	</form>
	<br>
	<button type="button" onClick="renderTheatreDetail('addTheatre')">Add
		Theatre</button>
	<button type="button"
		onClick="handleDisplayTheatres('displayTheatres')">Display
		Theatres</button>
	<button type="button" onclick="addMovie('addMovie')">Add
		Movie</button>
		<button type="button" onclick="handleDisplayMovies()">Display Movies</button>
		<button type="button" onclick="handleDisplayShows()">Display Shows</button>
	<br>
	<br>
	<p style="color: green" id="theatre-success-message"></p>
	<p style="color: red" id="theatre-error"></p>
	<div style="color: red" id="multiple-error"></div>
	<div id="theatre-container"></div>


</body>
</html>