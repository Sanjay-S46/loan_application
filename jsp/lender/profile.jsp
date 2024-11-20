<%@ taglib uri="/struts-tags" prefix="s" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Borrower Profile Page</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet"> 
    <link rel="stylesheet" href="/loanApplication/css/lenderStyle.css">

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
  
        .action-buttons {
            display: flex;
            justify-content: center;
            gap: 20px; 
            margin-top: 20px;
        }

        .btn-action {
            background-color: #007bff; 
            color: white; 
            border: none;
            border-radius: 5px;
            padding: 10px 20px;
            cursor: pointer;
            font-size: 14px;
            transition: background-color 0.3s, transform 0.2s;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        .btn-action:hover {
            background-color: #0056b3;
            transform: scale(1.05); 
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
                    <th>Available funds</th>
                    <td id="funds"><i class="fa-solid fa-indian-rupee-sign"></i> <s:property value="user.availableFunds" /></td>
                </tr>
            </table>
        </div>
        <div class="action-buttons">
            <button class="btn-action" onclick="openDepositModal()">Deposit</button>
            <button class="btn-action" onclick="openModal()">Change Password</button>
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

    <!-- Modal for Deposit -->
    <div id="depositModal" class="modal">
        <div class="modal-content">
            <h3>Deposit Funds</h3>
            <form id="depositForm" action="deposit_amount" method="post">
                <label for="depositAmount">Enter Deposit Amount</label>
                <input type="number" id="depositAmount" name="depositAmount" placeholder="Enter amount" required>

                <div class="modal-buttons">
                    <input type="submit" class="btn-submit" value="Submit" />
                    <input type="submit" class="btn-cancel" value="Cancel" onclick="closeDepositModal()" />
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

        function openDepositModal() {
            document.getElementById("depositModal").classList.add("show");
        }

        function closeDepositModal() {
            document.getElementById("depositModal").classList.remove("show");
        }
    </script>

</body>
</html>