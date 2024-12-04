// username validation
function validateUsername() {
    var username = document.getElementById("username").value.trim();
    var usernameError = document.getElementById("usernameError");

    usernameError.textContent = "";

    if (username === "") {
        usernameError.textContent = "Username cannot be empty.";
    } 
    else {
        var usernamePattern = /^[a-zA-Z0-9_]{3,15}$/; 
        if (!usernamePattern.test(username)) {
            usernameError.textContent = "Username must be alphanumeric and 3-15 characters long.";
        }
    }
}

// password validation
function validatePassword() {
    var password = document.getElementById("password").value.trim();
    var passwordError = document.getElementById("passwordError");

    passwordError.textContent = "";

    if (password === "") {
        passwordError.textContent = "Password cannot be empty.";
    } 
    else {
        if (password.length < 6) {
            passwordError.textContent = "Password must be at least 6 characters long.";
        }
    }
}

function clearError(errorElementId) {
    var errorElement = document.getElementById(errorElementId);
    errorElement.textContent = "";  
}

// Final Form Validation (submit check)
function validateForm(event) {
    event.preventDefault();
    validateUsername();
    validatePassword();

    var usernameError = document.getElementById("usernameError").textContent;
    var passwordError = document.getElementById("passwordError").textContent;

    if (usernameError || passwordError) {
        return false; 
    }

    document.getElementsByClassName("login-page")[0].style.display = "none";

    Swal.fire({
        icon: "success",
        title: "Login Successful",
        text: "You have logged in successfully.",
        confirmButtonText: 'OK',
        allowOutsideClick: false, 
        allowEscapeKey: false,    
        willClose: () => {
            document.getElementById("loginForm").submit()
            return true;
        }
    });
}