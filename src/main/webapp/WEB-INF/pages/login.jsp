	
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>  
<html>
<!-- <script src="<c:url value='resources/jquery-3.5.1.min.js'/>"></script> -->
<style type="text/css">

<%@include file="style.css" %>
</style>
<head>
<title>Custom Login Page</title>
</head>
<body onload='document.loginForm.username.focus();'>
<h3>Custom Login Page</h3>

<div id="dark" class="dark"></div>
<form name='loginForm' id='loginForm' action="<c:url value='login' />" method='POST'>

<table>
<tr>
<td>User:</td>
<td><input type='text' name='username' id="username" value=''>
</td>
</tr>
<tr>
<td>Password:</td>
<td><input type='password' name='password' id="password" />
</td>
</tr>
<tr>
<td><input id="submitBtn" onclick="loadOtpPage();" name="submit" type="button" value="submit" />
</td>
<td><input name="reset" id="resetLoginForm" type="reset" />
</td>
</tr>

<tr>



</tr>
</table>
</form>
 <form style="display:none;" id='verifyOtpForm' name='verifyOtpForm' action="<c:url value='verifyOtp' />""
 method='POST'>
 <table>
 <tr>
 <td>Enter Otp:</td>
 <td>
 <input type='text' name='inputOtp' id="inputOtp" value='' >
 </td>

 </tr>
 <tr>
 <td><input name="OK" type="submit" value="Verify Otp" />
 </td>

 </tr>
 </table>
 </form>

<script>
      function loadOtpPage() {
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
          if (this.readyState == 4 && this.status == 200) {
            //alert(this.responseText);
            let errorMsg=this.getResponseHeader("error");
            //alert(errorMsg);
            if(errorMsg!=null){
                document.getElementById("dark").innerHTML = errorMsg;
            }


            if(this.responseText=="success"){
             document.getElementById('resetLoginForm').click();
             document.getElementById('verifyOtpForm').style.display = "block";
             document.getElementById('loginForm').style.display = "none";
            }else{
                document.getElementById('loginForm').style.display = "block";
                document.getElementById('verifyOtpForm').style.display = "none";
            }
          }
        };
        let loginData="username="+document.getElementById("username").value+"&password="+document.getElementById("password").value;
        xhttp.open("POST", "<c:url value='login' />", true);
        //xhttp.setRequestHeader("Cache-Control", "no-cache, no-store, max-age=0");

        xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xhttp.send(loginData);
      }



</script>
<script src="<c:url value='resources/jquery-3.5.1.min.js' />"></script>
<script>
    $( document ).ready(function() {
    var urlParams = new URLSearchParams(window.location.search);
    let errorMsg=urlParams.get('error')
   // alert("abc"+errorMsg);
        $("#dark").text(errorMsg);
        //$("#dark").val(${error});
    });
</script>
</body>
</html>
