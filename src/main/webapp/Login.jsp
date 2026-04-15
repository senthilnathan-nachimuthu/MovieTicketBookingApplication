<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login</title>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/utility/userValidation.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/core/api.js"></script>
<body>
	<h1>Login page</h1>

	<form method='post'
		action='<%=response.encodeURL("j_security_check")%>'>

		<input name="j_username" id="uname" type="text" required
			placeholder="Enter Username"> <br> <br> <input
			name="j_password" id="pass" type="password" required
			placeholder="Enter Password"><br> <br>
		<button name="LoginSubmit" type="submit">Submit</button>
		<p id="error" style="color: red">${error}</p>
	</form>
	<form action="signup">
		<button type="submit" formnovalidate>New User?</button>
	</form>
	

	<script>
	 var contextPath = "<%=request.getContextPath()%>";
	
	async function getFetchResponse(event) {
	   event.preventDefault(); 
	  
	 	const username=document.getElementById("uname").value;
	 	const password=document.getElementById("pass").value;
	 	const error=document.getElementById("error");
	 	const data={j_username:username,j_password:password};	 	
	 	try{
	 		const res= await fetchData("/j_security_check","POST",data);
	    	console.log(res);
	   	if(res!=null)
		   {
	    	navigateToHome(res);
		   }
	 	}
	 	catch{
	 		(err => console.error("Network Error:", err));
	 	}
	}
	
	</script>
</body>
</html>