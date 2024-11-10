<%@ taglib uri="/struts-tags" prefix="s" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/loanApplication/css/verifierStyle.css">
    <title>Verifier Profile Page</title>
    <style>
        /* Profile Page Styling */
        .profile-container {
            background-color: #fff;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            max-width: 800px;
            margin: 20px auto;
        }

        h2 {
            color: #333;
            text-align: center;
            margin-bottom: 20px;
        }

        .profile-info table {
            width: 100%;
            border-collapse: collapse;
        }

        .profile-info th, .profile-info td {
            padding: 10px;
            border: 1px solid #ddd;
            text-align: left;
        }

        .profile-info th {
            background-color: #f4f4f4;
        }

        .change-password {
            margin-top: 20px;
            text-align: center;
        }

        .btn-change-password {
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            font-size: 16px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }

        .btn-change-password:hover {
            background-color: #0056b3;
        }

        /* Modal Styles */
        .modal {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-color: rgba(0, 0, 0, 0.5);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 1000;
            opacity: 0;
            visibility: hidden;
            transition: opacity 0.3s, visibility 0.3s;
        }

        .modal.show {
            opacity: 1;
            visibility: visible;
        }

        .modal-content {
            background-color: #fff;
            padding: 30px;
            border-radius: 10px;
            max-width: 500px; /* Increased width */
            width: 100%;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
            animation: slideUp 0.3s ease-out;
        }

        @keyframes slideUp {
            0% {
                transform: translateY(50px);
                opacity: 0;
            }
            100% {
                transform: translateY(0);
                opacity: 1;
            }
        }

        .modal h3 {
            margin-bottom: 20px;
            text-align: center;
            color: #333;
        }

        .modal label {
            display: block;
            margin: 10px 0 5px;
            font-weight: bold;
            font-size: 16px;
        }

        .modal input {
            width: 95%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            margin-bottom: 15px;
            font-size: 16px;
        }

        .modal .modal-buttons {
            display: flex;
            justify-content: space-between;
        }

        .modal .btn-submit,
        .modal .btn-cancel {
            padding: 12px 20px;
            font-size: 16px;
            border-radius: 5px;
            cursor: pointer;
            width: 48%;
        }

        .modal .btn-submit {
            background-color: #28a745;
            color: white;
            border: none;
            margin-right: 4%;
        }

        .modal .btn-submit:hover {
            background-color: #218838;
        }

        .modal .btn-cancel {
            background-color: #dc3545;
            color: white;
            border: none;
        }

        .modal .btn-cancel:hover {
            background-color: #c82333;
        }

    </style>
</head>
<body>
    <div class="navbar">
        <h1>Loan Application</h1>
        <div class="buttons">
            <a href="<s:url action='logout'/>" id="logout-button">Logout</a>
        </div>
    </div>


    <!-- Profile Page Content -->
    <div class="profile-container">
        <h2>User Profile</h2>
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
                    <td id="usertype"><s:property value="user.gender" /></td>
                </tr>
            </table>
        </div>
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
                    <button type="button" class="btn-cancel" onclick="closeModal()">Cancel</button>
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