<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login Page</title>
    <link rel="stylesheet" href="/loanApplication/css/loginStyle.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script> <%-- sweetalert cdn link --%>
</head>
<body>
    <div class="login-page">
        <div>
            <h1>Login</h1>
        </div>
        <div class="login-form">
            <form id="loginForm" action="login" method="POST" onsubmit="return validateForm(event)">
                <div class="form-group">
                    <input type="text" id="username" name="username" placeholder=" " oninput="validateUsername()" onfocus="clearError('usernameError')" />
                    <label for="username">Username</label>
                    <div class="error" id="usernameError"></div>
                </div>
                <div class="form-group">
                    <input type="password" id="password" name="password" placeholder=" " oninput="validatePassword()" onfocus="clearError('passwordError')" />
                    <label for="password">Password</label>
                    <div class="error" id="passwordError"></div>
                </div>
                <input type="submit" value="Login" />
            </form>
        </div>
        <div>
            <p>New user? <a href="/loanApplication/signup.jsp">Signup</a></p>
        </div>
    </div>

    <%-- <script type="text/javascript" src="/loanApplication/js/login_validation.js"></script> --%>

</body>
</html>