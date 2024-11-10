<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Signup Page</title>
    <link rel="stylesheet" href="/loanApplication/css/signupStyle.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script> <%-- sweetalert cdn link --%>
    </head>
<body>
    <div class="signup-page">
        <div>
            <h1>Sign Up</h1>
        </div>
        <div class="signup-form">
            <form id="signupForm" onsubmit="return validateForm(event)">
                <div class="form-group">
                    <input type="text" id="username" name="username" placeholder=" " oninput="validateUsername()" onfocus="clearError('usernameError')" />
                    <label for="username">Username</label>
                </div>
                <div id="usernameError" class="error-message"></div>
                <br>

                <div class="form-group">
                    <input type="email" id="emailId" name="emailId" placeholder=" " oninput="validateEmail()" onfocus="clearError('emailError')" />
                    <label for="emailId">Email ID</label>
                </div>
                <div id="emailError" class="error-message"></div>
                <br>

                <div class="form-group">
                    <input type="text" id="mobileNo" name="mobileNo" placeholder=" " oninput="validateMobile()" onfocus="clearError('mobileError')" />
                    <label for="mobileNo">Mobile Number</label>
                </div>
                <div id="mobileError" class="error-message"></div>
                <br>

                <div class="gender-options">
                    <span>Gender :</span>
                    <label for="male">
                        <input type="radio" name="gender" value="male" id="male" onchange="validateGender()" />
                        Male
                    </label>
                    <label for="female">
                        <input type="radio" name="gender" value="female" id="female" onchange="validateGender()" />
                        Female
                    </label>
                </div>
                <div id="genderError" class="error-message"></div>

                <div id="select-options" class="form-group">
                    <select name="userType" id="userType" onchange="validateRole()">
                        <option value="">Select Role</option>
                        <option value="borrower">Borrower</option>
                        <option value="verifier">Verifier</option>
                        <option value="lender">Lender</option>
                    </select>
                </div>
                <div id="roleError" class="error-message"></div>
                <br>

                <input type="submit" value="Signup" />
            </form>
        </div>
        <div>
            <p>Existing user? <a href="login.jsp">Login</a></p>
        </div>
    </div>

    <script type="text/javascript" src="/loanApplication/js/signup_validation.js"></script>

</body>
</html>
