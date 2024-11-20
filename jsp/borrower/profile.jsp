<%@ taglib uri="/struts-tags" prefix="s" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Borrower Profile Page</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet"> 
    <link rel="stylesheet" href="/loanApplication/css/borrowerStyle.css">

    <style>

        input[type="number"], input[type="text"] {
            width: 95%; 
            font-size: 16px; 
            border: none; 
            outline: none;
        }

        input[type=number]{
            -moz-appearance: textfield;
        }

        .edit-icon {
            color: #888;
            cursor: pointer;
            transition: color 0.3s ease;
        }

        .edit-icon:hover {
            color: #007bff; 
        }

    </style>
</head>
<body>
    <div class="navbar">
        <h1>Loan Application</h1>
        <div class="buttons">
            <a href="<s:url action='home' />"><i class="fas fa-home"></i></a>
            <a href="<s:url action='logout' />" class="logout">Logout</a>
        </div>
    </div>


    <!-- Profile Page Content -->
    <div class="profile-container">
        <h2 class="profile-heading">User Profile</h2>
        <form id="profileForm" action="update_profile" method="post">
            <div class="profile-info">
                <table>
                    <tr>
                        <th>Username</th>
                        <td id="username"><s:property value="user.username" /></td>
                    </tr>
                    <tr>
                        <th>Email</th>
                        <td id="email"><s:property value="user.emailId" /></td>
                    </tr>
                    <tr>
                        <th>Mobile No</th>
                        <td id="mobile"><s:property value="user.mobileNo" /></td>
                    </tr>
                    <tr>
                        <th>User Type</th>
                        <td id="usertype"><s:property value="user.userType" /></td>
                    </tr>
                    <tr>
                        <th>Gender</th>
                        <td id="gender"><s:property value="user.gender" /></td>
                    </tr>
                    <tr>
                        <th>Annual Income</th>
                        <td>
                            <input type="number" name="annualIncome" id="annualIncome" value="<s:property value='user.annualIncome'/>" placeholder="Enter your annual income" required>
                            <i class="fa fa-pencil edit-icon" aria-hidden="true"></i>
                        </td>
                    </tr>
                    <tr>
                        <th>Assets</th>
                        <td>
                            <input type="text" name="assets" id="assets" value="<s:property value='user.assets'/>" placeholder="Enter your assets" required>
                            <i class="fa fa-pencil edit-icon" aria-hidden="true"></i>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="save-button">
                <button type="submit" class="btn-save">Save</button>
            </div>
        </form>
        <div class="change-password">
            <button class="btn-change-password" onclick="openModal()">Change Password</button>
        </div>
    </div>

    <!-- Modal for Changing Password -->
    <div id="changePasswordModal" class="modal">
        <div class="modal-content">
            <h3>Change Password</h3>
            <form id="changePasswordForm" action="change_password" method="post">
                <label for="currentPassword">Current Password</label>
                <input type="password" id="currentPassword" name="currentPassword" required>

                <label for="newPassword">New Password</label>
                <input type="password" id="newPassword" name="newPassword" required>

                <label for="confirmPassword">Confirm Password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>

                <div class="modal-buttons">
                    <input type="submit" class="btn-submit" value="Submit" />
                    <input type="submit" class="btn-cancel" value="Cancel" onclick="closeModal()" />
                </div>
            </form>
        </div>
    </div>

    <!-- JavaScript -->
    <script>

        function openModal() {
            document.getElementById("changePasswordModal").classList.add("show");
        }

        function closeModal() {
            document.getElementById("changePasswordModal").classList.remove("show");
        }

    </script>

</body>
</html>