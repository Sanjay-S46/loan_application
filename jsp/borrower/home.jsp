<%@ taglib uri="/struts-tags" prefix="s" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Borrower Home</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet"> 
    <link rel="stylesheet" href="/loanApplication/css/borrowerStyle.css">

    <style>
    </style>
</head>

<body>
    <div class="navbar">
        <h1>Loan Application</h1>
        <div class="nav-links">
            <a onclick="showSection('overview')">Overview</a>
            <a onclick="showSection('loan-request')">Loan Request</a>
            <a onclick="showSection('loan-status')">Loan Status</a>
            <%-- <a onclick="showSection('loan-details-from-lender')">Loan Details from Lender</a> --%>
            <a onclick="showSection('transaction-history')">Transaction History</a>
        </div>
        <div class="buttons">
            <a href="<s:url action='userProfile' />"><i class="fas fa-user"></i></a>
            <a href="<s:url action='home' />"><i class="fas fa-home"></i></a>
            <a href="<s:url action='logout' />" class="logout">Logout</a>
        </div>
    </div>

    <div class="content">
        <!-- Overview Section (default visible) -->
        <div class="section active" id="overview">
            <div class="overview-header">
                <h1 class="welcome-message">Welcome, <s:property value="username" /></h1>
                <h2 class="max-loan">Max Loan Amount: <i class="fa-solid fa-indian-rupee-sign"></i> <s:property value="maxLoanAmount" default="0" /></h2>
            </div>
            <h2 class="current-balance">Current Balance: <i class="fa-solid fa-indian-rupee-sign"></i> <s:property value="currentBalance" /></h2>
            <h3>Your Current Loans</h3>
            <div class="loan-table">
                <table>
                    <thead>
                        <tr>
                            <th>Loan Amount</th>
                            <th>EMI Paid</th>
                            <th>EMI Pending</th>
                            <th>Due Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td><i class="fa-solid fa-indian-rupee-sign"></i> 50,000</td>
                            <td><i class="fa-solid fa-indian-rupee-sign"></i> 5,000</td>
                            <td><i class="fa-solid fa-indian-rupee-sign"></i> 45,000</td>
                            <td>12/15/2024</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>


        <!-- Loan Request Section -->
        <div class="section" id="loan-request">
            <form action="applyLoan" method="post">
                <div class="section-title">Request a Loan</div>
            
                <div class="form-group">
                    <label for="loan-type">Loan Type:</label>
                    <select id="loan-type" name="loanType">
                        <option value="personal">Personal</option>
                        <option value="mortgage">Mortgage</option>
                        <option value="business">Business</option>
                        <option value="other">Other</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="loan-purpose">Purpose of Loan:</label>
                    <input type="text" id="loan-purpose" name="loanPurpose" placeholder="Enter the purpose of the loan">
                </div>

                <div class="form-group">
                    <label for="loan-amount">Total Loan Amount:</label>
                    <input type="number" id="loan-amount" name="loanAmount" placeholder="Enter total loan amount">
                </div>
                
                <div class="form-group">
                    <label for="loan-term">Loan Tenure (Months):</label>
                    <input type="number" id="loan-term" name="loanMonth" placeholder="Enter loan term in months">
                </div>

                <button class="submit-btn" type="submit">Submit Loan Request</button>
            </form>
        </div>

        <%-- loan status section --%>

        <div class="section" id="loan-status">
            <div class="loan-status-title">Loan Status</div>
            <div class="loan-status-table">
                <s:if test="loans.size()==0">
                    <p>No loans taken by <s:property value="username" /></p>
                </s:if>
                <s:else>
                    <table>
                        <thead>
                            <tr>
                                <th>S.No</th>
                                <th>Requested Amount</th>
                                <th>Months</th>
                                <th>Loan Type</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <s:set var="counter" value="0" />
                            <s:iterator value="loans">
                                <s:set var="counter" value="#counter + 1" />
                                <tr onclick="submitLoanForm('<s:property value='loanId' />')">
                                    <td><s:property value="#counter" /></td>
                                    <td><i class="fa-solid fa-indian-rupee-sign"></i> <s:property value="loanAmount" /></td>
                                    <td><s:property value="loanMonth" /></td>
                                    <td><s:property value="loanType" /></td>
                                    <td><s:property value="status" /></td>
                                </tr>
                            </s:iterator>
                        </tbody>
                    </table>
                    <form action="show_loan_detail" method="post" id="loanForm">
                        <input type="hidden" name="loanId" id="hiddenLoanId" value="" />
                    </form>
                </s:else>
            </div>
        </div>

        <!-- Transaction History Section -->
        <div class="section" id="transaction-history">
            <div class="section-title">Transaction History</div>
            <div class="history-table">
                <table>
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Lender</th>
                            <th>Amount</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>01/01/2024</td>
                            <td>Lender 1</td>
                            <td>$30,000</td>
                            <td>Paid</td>
                        </tr>
                        <tr>
                            <td>02/01/2024</td>
                            <td>Lender 2</td>
                            <td>$20,000</td>
                            <td>Pending</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <script>
        // Function to show the selected section and hide the others
        function showSection(sectionId) {
            const sections = document.querySelectorAll('.section');
            sections.forEach(section => {
                section.classList.remove('active');
            });
            document.getElementById(sectionId).classList.add('active');
        }
    </script>
    <script>
        function submitLoanForm(loanId) {
            // Set the loanId in the hidden input field
            document.getElementById('hiddenLoanId').value = loanId;

            // Submit the form
            document.getElementById('loanForm').submit();
        }
    </script>
</body>
</html>
