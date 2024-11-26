<%@ taglib uri="/struts-tags" prefix="s" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Borrower Home</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet"> 
    <link rel="stylesheet" href="/loanApplication/css/lenderStyle.css">

    <style>
        

    </style>
</head>

<body>
    <div class="navbar">
        <h1>Loan Application</h1>
        <div class="nav-links">
            <a onclick="showSection('overview')">Overview</a>
            <a onclick="showSection('borrow-requests')">Borrow Requests</a>
            <a onclick="showSection('report')">Report</a>
            <a onclick="showSection('transaction-history')">Transaction History</a>
        </div>
        <div class="buttons">
            <a href="<s:url action='userProfile' />"><i class="fas fa-user"></i></a>
            <a href="<s:url action='home' />"><i class="fas fa-home"></i></a>
            <a href="<s:url action='logout' />" class="logout">Logout</a>
        </div>
    </div>

    <%-- content --%>
    <div class="content">
        <!-- Overview Section -->
        <div class="section active" id="overview">
            <div class="overview-info">
                <div>Welcome, <strong><s:property value="username" /></strong></div>
                <div>Available Amount: <strong><i class="fa-solid fa-indian-rupee-sign"></i> <s:property value="availableFunds" /></strong></div>
            </div>

            <div class="section-title">Lending Status</div>
            <table class="lending-status-table">
                <thead>
                    <tr>
                        <th>Borrower</th>
                        <th>Date</th>
                        <th>Amount Lended</th>
                        <th>Interest Rate</th>
                        <th>Due Date</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>Jane Smith</td>
                        <td>01/01/2024</td>
                        <td>$5,000</td>
                        <td>10%</td>
                        <td>01/01/2025</td>
                    </tr>
                    <tr>
                        <td>John Doe</td>
                        <td>15/02/2024</td>
                        <td>$3,000</td>
                        <td>8%</td>
                        <td>15/02/2025</td>
                    </tr>
                </tbody>
            </table>
        </div>

        <!-- Borrow Requests Section -->
        <div class="section" id="borrow-requests">
            <div class="section-title">Borrow Requests</div>
            <s:if test="loanDetails.size()==0">
                <h3>No loan requests at the moment..</h3>
            </s:if>
            <s:else>
                <s:iterator value="loanDetails">
                    <div class="borrow-request">
                        <div>
                            <p><strong>Borrower : </strong> <s:property value="username"/></p>
                            <p><strong>Amount requested : </strong> <i class="fa-solid fa-indian-rupee-sign"></i> <s:property value="requestedAmount"/></p>
                            <s:if test="balanceAmount!=0">
                                <p><strong>Balance amount : </strong> <i class="fa-solid fa-indian-rupee-sign"></i> <s:property value="balanceAmount"/></p>
                            </s:if>
                            <p><strong>Loan purpose : </strong> <s:property value="loanPurpose"/></p>
                            <p><strong>Loan Term : </strong> <s:property value="loanMonth"/> months</p>
                        </div>
                        <div class="interest-rate">
                            <form action="lend_loan" method="post">
                                <input type="number" name="interestRate" placeholder="Interest Rate (%)" min="1" max="20">
                                <input type="number" name="grantLoanAmount" placeholder="Amount" >
                                <input type="hidden" name="borrowerName" value="<s:property value='username'/>">
                                <input type="hidden" name="requestedAmount" value="<s:property value='requestedAmount'/>">
                                <input type="hidden" name="loanMonth" value="<s:property value='loanMonth'/>">
                                <input type="hidden" name="loanId" value="<s:property value='loanId'/>">
                                <button type="submit">Lend</button>
                            </form>
                             <p class="status <s:property value='status.toLowerCase()' />">
                                Status : <s:property value="status" />
                            </p>
                        </div>
                    </div>
                </s:iterator>
            </s:else>
        </div>


        <!-- Report Section -->
        <div class="section" id="report">
            <div class="section-title">Report</div>
            <div class="report-section">
                <div class="transaction">
                    <p>Borrower: John Doe</p>
                    <p class="date">01/01/2024 12:00 PM</p>
                    <p class="amount">$10,000</p>
                </div>
                <div class="transaction">
                    <p>Borrower: Jane Smith</p>
                    <p class="date">02/01/2024 3:45 PM</p>
                    <p class="amount">$8,000</p>
                </div>
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
                                <th>Name</th>
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