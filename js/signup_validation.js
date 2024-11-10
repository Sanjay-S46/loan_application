// function to clear the error message
function clearError(errorId) {
    let errorElement = document.getElementById(errorId);
    errorElement.style.display = "none";
}

// Validate Username (Alphanumeric, 3 to 20 characters)
function validateUsername() {
    let username = document.getElementById("username").value.trim();
    let usernameError = document.getElementById("usernameError");
    let usernamePattern = /^[a-zA-Z0-9]{3,20}$/;

    if (username === "") {
        usernameError.textContent = "Username cannot be empty.";
        usernameError.style.display = "block";
    } else if (!usernamePattern.test(username)) {
        usernameError.textContent =
            "Username should be alphanumeric and between 3 to 20 characters.";
        usernameError.style.display = "block";
    } else {
        usernameError.style.display = "none";
    }
}

// Validate Email
function validateEmail() {
    let emailId = document.getElementById("emailId").value.trim();
    let emailError = document.getElementById("emailError");
    let emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

    if (emailId === "") {
        emailError.textContent = "Email cannot be empty.";
        emailError.style.display = "block";
    } else if (!emailPattern.test(emailId)) {
        emailError.textContent = "Please enter a valid email address.";
        emailError.style.display = "block";
    } else {
        emailError.style.display = "none";
    }
}

// Validate Mobile Number (starts with 6-9 and followed by 9 digits)
function validateMobile() {
    let mobileNo = document.getElementById("mobileNo").value.trim();
    let mobileError = document.getElementById("mobileError");
    let mobilePattern = /^[6-9][0-9]{9}$/;

    if (mobileNo === "") {
        mobileError.textContent = "Mobile number cannot be empty.";
        mobileError.style.display = "block";
    } else if (!mobilePattern.test(mobileNo)) {
        mobileError.textContent =
            "Please enter a valid 10-digit mobile number starting with 6-9.";
        mobileError.style.display = "block";
    } else {
        mobileError.style.display = "none";
    }
}

// Validate Gender
function validateGender() {
    let gender = document.querySelector('input[name="gender"]:checked');
    let genderError = document.getElementById("genderError");

    if (!gender) {
        genderError.textContent = "Please select your gender.";
        genderError.style.display = "block";
    } else {
        genderError.style.display = "none";
    }
}

// Validate User Role
function validateRole() {
    let userType = document.getElementById("userType").value.trim();
    let roleError = document.getElementById("roleError");

    if (userType === "") {
        roleError.textContent = "Please select a role.";
        roleError.style.display = "block";
    } else {
        roleError.style.display = "none";
    }
}

// Form validation before submission
function validateForm(event) {
    event.preventDefault();
    let valid = true;

    validateUsername();
    validateEmail();
    validateMobile();
    validateGender();
    validateRole();

    // Check if all validations passed
    if ( document.querySelectorAll(".error-message[style='display: block;']").length === 0) {
        Swal.fire({
            icon: "success",
            title: "Signup Successful",
            text: "You have successfully signed up.",
            confirmButtonText: "OK",
            allowOutsideClick: false,
            allowEscapeKey: false,
            willClose: () => {
                document.getElementById("signupForm").submit(); // Submit form after SweetAlert closes
            },
        });
    }

    return valid;
}