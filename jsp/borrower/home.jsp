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
        .nav-links a{
            cursor: pointer;
        }
        
        input[type="submit"] {
            padding: 8px 16px;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            font-weight: bold;
            transition: background-color 0.3s ease, transform 0.2s ease;
        }

        /* Hover Effect */
        button:hover, input[type="submit"]:hover {
            transform: scale(1.05); 
        }
        /* Styling Pay Button */
        input[type="submit"][value="Pay"] {
            background-color: #28a745;
        }

        input[type="submit"][value="Pay"]:hover {
            background-color: #218838; 
        }

        /* Styling View Button */
        input[type="submit"][value="View"] {
            background-color: #17a2b8; 
        }

        input[type="submit"][value="View"]:hover {
            background-color: #138496; 
        }
    </style>
</head>

<body>
    <div class="navbar">
        <h1>Loan Application</h1>
        <div class="nav-links">
            <a onclick="showSection('overview')">Overview</a>
            <a onclick="showSection('loan-request')">Loan Request</a>
            <a onclick="showSection('loan-status')">Loan Status</a>
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
            <s:if test="emiDetails.size()==0">
                <h3>No EMI's are available at the moment..</h3>
            </s:if>
            <s:else>
                <h3>Your Current EMI'S</h3>
                <div class="loan-table">
                    <table>
                        <thead>
                            <tr>
                                <th>S.no</th>
                                <th>Loan Amount</th>
                                <th>EMI Amount</th>
                                <th>EMI Paid</th>
                                <th>EMI Pending</th>
                                <th>Due Date</th>
                                <th>Option</th>
                            </tr>
                        </thead>
                        <tbody>
                            <s:set var="counter" value="0" />
                            <s:iterator value="emiDetails">
                                <s:set var="counter" value="#counter + 1" />
                                <tr>
                                    <td><s:property value="#counter" /> </td>
                                    <td><i class="fa-solid fa-indian-rupee-sign"></i> <s:property value="loanAmount" /></td>
                                    <td><i class="fa-solid fa-indian-rupee-sign"></i> <s:property value="emiAmount" /></td>
                                    <td><i class="fa-solid fa-indian-rupee-sign"></i> <s:property value="emiPaid" /></td>
                                    <td><i class="fa-solid fa-indian-rupee-sign"></i> <s:property value="emiPending" /></td>
                                    <td><s:property value="date" /></td>
                                    <td>
                                        <form action="payment_page" method="post">
                                            <input type="hidden" name="loanId" value="<s:property value='loanId' />" />
                                            <input type="hidden" name="emiPending" value="<s:property value='emiAmount' />" />
                                            <input type="hidden" name="emiAmount" value="<s:property value='emiPending' />" />
                                            <input type="submit" value="Pay" />
                                        </form>
                                    </td>
                                </tr>            
                            </s:iterator>
                        </tbody>
                    </table>
                </div>
            </s:else>
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
                    <input type="text" id="loan-purpose" name="loanPurpose" placeholder="Enter the purpose of the loan" required>
                </div>

                <div class="form-group">
                    <label for="loan-amount">Total Loan Amount:</label>
                    <input type="number" id="loan-amount" name="loanAmount" placeholder="Enter total loan amount" required>
                </div>
                
                <div class="form-group">
                    <label for="loan-term">Loan Tenure (Months):</label>
                    <input type="number" id="loan-term" name="loanMonth" placeholder="Enter loan term in months" required>
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
                                <th>Option</th>
                            </tr>
                        </thead>
                        <tbody>
                            <s:set var="counter" value="0" />
                            <s:iterator value="loans">
                                <s:set var="counter" value="#counter + 1" />
                                <tr>
                                    <td><s:property value="#counter" /></td>
                                    <td><i class="fa-solid fa-indian-rupee-sign"></i> <s:property value="loanAmount" /></td>
                                    <td><s:property value="loanMonth" /></td>
                                    <td><s:property value="loanType" /></td>
                                    <td><s:property value="status" /></td>
                                    <td>
                                        <form action="show_loan_detail" method="post">
                                            <input type="hidden" name="loanId" value="<s:property value='loanId' />" />
                                            <input type="submit" value="View" />
                                        </form>
                                    </td>
                                </tr>
                            </s:iterator>
                        </tbody>
                    </table>
                </s:else>
            </div>
        </div>

        <!-- Transaction History Section -->
        <div class="section" id="transaction-history">
            <div class="section-title">Transaction History</div>
            <div class="history-table">
                <s:if test="history.size()==0">
                    <h2>No transactions history at the moment</h2>
                </s:if>
                <s:else>
                    <table>
                        <thead>
                            <tr>
                                <th>S.No</th>
                                <th>Date</th>
                                <th>Lender Name</th>
                                <th>Amount</th>
                                <th>Transaction Type</th>
                            </tr>
                        </thead>
                        <tbody>
                            <s:set var="counter" value="0" />
                            <s:iterator value="history">
                                <s:set var="counter" value="#counter + 1" />
                                <tr>
                                    <td><s:property value="#counter" /></td>
                                    <td><s:property value="date" /></td>
                                    <td><s:property value="name" /></td>
                                    <td><i class="fa-solid fa-indian-rupee-sign"></i> <s:property value="amount" /></td>
                                    <td><s:property value="transactionType" /></td>
                                </tr>
                            </s:iterator>
                        </tbody>
                    </table>
                </s:else>
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

</body>
</html>
