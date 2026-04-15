<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Signup</title>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/utility/userValidation.js"></script>
<script type="text/javascript"
	src="<%= request.getContextPath() %>/js/core/api.js"></script>

</head>
<body>
	<h1>Signup</h1>

	<form action="signup" method="post" onsubmit="return  validateUserDetails() && validate() && getFetchResponse(event)">

		<input type=text id=name name="name" placeholder="Enter your name" required><br>
		
		<br> <input type=number id="age" name="age" placeholder="Enter your age"
			required><br> <br>

		<p>Gender:</p>
		<input type="radio" name="gender" value="male">
		<label for="maleButton">Male</label> 
		<input type="radio" name="gender"
			value="female"> <label for="maleButton">Female</label>
		<input type="radio" name="gender" value="others">
		<label for="maleButton">Others</label> <br> <br> 
		
		<input
			type=text  id="uname"  name="username" placeholder="Enter your username" required><br>
		<br> 
		
		<input  id="pass"  type=password name="password"
			placeholder="New Password" required><br> <br>
		<button>Submit</button>

	</form>
	<p style="color: red" id="error">${error}</p>
	
	
	<script>
	
	async function getFetchResponse(event)
	{
		event.preventDefault();
		const name=document.getElementById("name").value;
		const age=document.getElementById("age").value;
		const genderInput = document.querySelector('input[name="gender"]:checked');	
		const gender=genderInput.value;
		
		const username=document.getElementById("uname").value;
		const password=document.getElementById("pass").value;
		
		const data={name:name,age:age,gender:gender,username:username,password:password};
		
		console.log(data);
		
		try{
		 	const res= await fetchData("/signup","POST",data);
		    	console.log(res);
		    	navigateToHome(res);
		 	}
		catch (err) {
			  console.error("Network Error:", err);
			}

		
		
	}
	
	
	
	
	</script>

</body>
</html>