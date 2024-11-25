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
        .section {
            display: block;
        }
        .loan-details-from-lender-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        .loan-details-from-lender-table th, .loan-details-from-lender-table td {
            padding: 12px;
            text-align: center;
            border-bottom: 1px solid #ddd;
        }

        .loan-details-from-lender-table th {
            background-color: #f4f4f4;
        }

        .loan-details-from-lender-table tr:hover {
            background-color: #f9f9f9;
        }

        .loan-details-from-lender-table .options {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 10px;
        }

        .loan-details-from-lender-table .options button {
            padding: 8px 16px;
            font-size: 14px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .loan-details-from-lender-table .options .accept {
            background-color: #4CAF50;
            color: white;
        }

        .loan-details-from-lender-table .options .accept:hover {
            background-color: #45a049;
        }

        .loan-details-from-lender-table .options .reject {
            background-color: #f44336;
            color: white;
        }

        .loan-details-from-lender-table .options .reject:hover {
            background-color: #d32f2f;
        }

    </style>
</head>

<body>
    <div class="navbar">
        <h1>Loan Application</h1>
        <div class="buttons">
            <a href="<s:url action='userProfile' />"><i class="fas fa-user"></i></a>
            <a href="<s:url action='home' />"><i class="fas fa-home"></i></a>
            <a href="<s:url action='logout' />" class="logout">Logout</a>
        </div>
    </div>

    <div class="content">

        <!-- Loan Details Section -->
        <div class="section" id="loan-details-from-lender">
            <div class="section-title">Loan Details</div>
            
            <s:if test="loanDetails.size()==0">
                <h2>No loans granted at the moment..</h2>
            </s:if>
            <s:else>
                <!-- Loan Details Table -->
                <table class="loan-details-from-lender-table">
                    <thead>
                        <tr>
                            <th>S.No.</th>
                            <th>Lender Name</th>
                            <th>Loan Amount</th>
                            <th>Interest Rate</th>
                            <th>EMI</th>
                            <th>Options</th>
                        </tr>
                    </thead>
                    <tbody>
                        <s:set var="counter" value="0" />
                        <s:iterator value="loanDetails">
                        <s:set var="counter" value="#counter + 1" />
                            <tr>
                                <td><s:property value="#counter" /></td>
                                <td><s:property value="lender"/></td>
                                <td><i class="fa-solid fa-indian-rupee-sign"></i> <s:property value="loanGrantedAmount"/></td>
                                <td><s:property value="interestRate"/> %</td>
                                <td><i class="fa-solid fa-indian-rupee-sign"></i> <s:property value="emi" /></td>
                                <td class="options">
                                    <form action="accept_loan" method="post">
                                        <input type="hidden" name="emi" value="<s:property value="emi" />" >
                                        <input type="hidden" name="distributionId" value="<s:property value='distributionId' />">
                                        <button class="accept" type="submit">Accept</button>
                                    </form>
                                    <form action="reject_loan" method="post">
                                        <input type="hidden" name="distributionId" value="<s:property value='distributionId' />">
                                        <button class="reject" type="submit">Reject</button>
                                    </form>
                                </td>
                            </tr>
                        </s:iterator>
                    </tbody>
                </table>
            </s:else>
        </div>
    </div>

</body>
</html>
