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

                <div class="form-group" id="lender-1-group">
                    <label for="lender-amount1">Lender 1 Loan Amount:</label>
                    <input type="number" id="lender-amount1" placeholder="Enter amount from Lender 1">
                </div>

                <!-- Dynamic Lender Fields (Hidden initially) -->
                <div id="additional-lenders"></div>

                <button class="submit-btn" type="submit">Submit Loan Request</button>
                <button class="add-lender-btn" onclick="addLender()">Add Another Lender</button>
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
                                <tr>
                                    <td><s:property value="#counter" /></td>
                                    <td><i class="fa-solid fa-indian-rupee-sign"></i> <s:property value="loanAmount" /></td>
                                    <td><s:property value="loanMonth" /></td>
                                    <td><s:property value="loanType" /></td>
                                    <td><s:property value="status" /></td>
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

        let lenderCount = 1;

        function addLender() {
            if (lenderCount < 3) {
                lenderCount++;
                const lenderField = document.createElement('div');
                lenderField.classList.add('lender-group');
                lenderField.id = `lender-${lenderCount}-group`;
                lenderField.innerHTML = `
                    <input type="number" id="lender-amount${lenderCount}" placeholder="Enter amount from Lender ${lenderCount}">
                    <i class="fas fa-minus remove-lender" onclick="removeLender(${lenderCount})"></i>
                `;
                document.getElementById('additional-lenders').appendChild(lenderField);
            } else {
                alert('You can only add up to 3 lenders.');
            }
        }

        function removeLender(lenderId) {
            const lenderField = document.getElementById(`lender-${lenderId}-group`);
            lenderField.remove();
            lenderCount--;
        }

        function submitLoanRequest() {
            const loanAmount = document.getElementById('loan-amount').value;
            const loanTerm = document.getElementById('loan-term').value;
            let lenderAmounts = [];

            for (let i = 1; i <= lenderCount; i++) {
                const lenderAmount = document.getElementById(`lender-amount${i}`).value;
                if (lenderAmount) {
                    lenderAmounts.push(`Lender ${i}: $${lenderAmount}`);
                }
            }

            if (!loanAmount || !loanTerm) {
                alert('Please fill in all required fields.');
                return;
            }

            alert(`Loan request submitted. Details: Total Loan: $${loanAmount}, Term: ${loanTerm} months, ${lenderAmounts.join(', ')}`);
        }

    </script>
</body>
</html>
