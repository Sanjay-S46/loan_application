<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Signup Page</title>
    <link rel="stylesheet" href="/loanApplication/css/signupStyle.css">
    </head>
<body>
    <div class="signup-page">
        <div>
            <h1>Sign Up</h1>
        </div>
        <div class="signup-form">
            <form onsubmit="validateForm(event)">
                <div class="form-group">
                    <input type="text" id="username" name="username" placeholder=" " />
                    <label for="username">Username</label>
                </div>

                <div class="form-group">
                    <input type="email" id="emailId" name="emailId" placeholder=" " />
                    <label for="emailId">Email ID</label>
                </div>

                <div class="form-group">
                    <input type="text" id="mobileNo" name="mobileNo" placeholder=" " />
                    <label for="mobileNo">Mobile Number</label>
                </div>

                <div class="gender-options">
                    <span>Gender :</span>
                    <label for="male">
                        <input type="radio" name="gender" value="male" id="male" />
                        Male
                    </label>
                    <label for="female">
                        <input type="radio" name="gender" value="female" id="female" />
                        Female
                    </label>
                </div>                

                <div class="form-group">
                    <select name="userType">
                        <option value="">Select Role</option>
                        <option value="verifier">Verifier</option>
                        <option value="lender">Lender</option>
                        <option value="borrower">Borrower</option>
                    </select>
                </div>

                <input type="submit" value="Signup" />
            </form>
        </div>
        <div>
            <p>Existing user? <a href="login.jsp">Login</a></p>
        </div>
    </div>
</body>
</html>
