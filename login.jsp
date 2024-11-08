<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login Page</title>
    <link rel="stylesheet" href="/loanApplication/css/loginStyle.css">
</head>
<body>
    <div class="login-page">
        <div>
            <h1>Login</h1>
        </div>
        <div class="login-form">
            <form action="login" method="post">
                <div class="form-group">
                    <input type="text" id="username" name="username" placeholder=" "  />
                    <label for="username">Username</label>
                </div>
                <div class="form-group">
                    <input type="password" id="password" name="password" placeholder=" " />
                    <label for="password">Password</label>
                </div>
                <input type="submit" value="Login" />
            </form>
        </div>
        <div>
            <p>New user? <a href="/loanApplication/signup.jsp">Signup</a></p>
        </div>
    </div>
</body>
</html>
