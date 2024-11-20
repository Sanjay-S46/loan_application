<%@ taglib uri="/struts-tags" prefix="s" %> 

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Loan Application</title>
    <link rel="stylesheet" href="/loanApplication/css/adminStyle.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>
    <div class="navbar">
        <h1>Loan Application</h1>
        <div class="nav-links">
            <a onclick="showSection('all-users')">All Users</a>
            <a onclick="showSection('verifiers')">Verifiers</a>
            <a onclick="showSection('rejected-users')">Rejected verifiers</a>
            <%-- <a onclick="showSection('borrowers')">Borrowers</a>
            <a onclick="showSection('lenders')">Lenders</a> --%>
            <a onclick="showSection('incoming-requests')">Incoming Requests</a>
        </div>
        <div class="buttons">
            <a class="logout-button" href='<s:url action="logout" />'>Logout</a>
        </div>
    </div>

    <div class="content">
        <div id="all-users" class="section">
            <h2>All Users</h2>
            <table>
                <thead>
                    <tr>
                        <th>User ID</th>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <s:iterator value="users" status="status">
                        <tr>
                            <td><s:property value="#status.index + 1" /></td>
                            <td><s:property value="username" /></td>
                            <td><s:property value="emailId" /></td>
                            <td><s:property value="userType" /></td>
                            <td><s:property value="status" /></td>
                        </tr>
                    </s:iterator>
                </tbody>
            </table>
        </div>

        <div id="incoming-requests" class="section hidden">
            <h2>Incoming Requests</h2>

            <s:if test="newVerifiers.size() == 0">
                <p>No incoming requests at this moment.</p>
            </s:if>
            <s:else>
                <table>
                    <thead>
                        <tr>
                            <th>S.No</th>
                            <th>Username</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th>Options</th>
                        </tr>
                    </thead>
                    <tbody>
                        <s:set var="verifierCount" value="0" />
                        <s:iterator value="newVerifiers">
                            <s:set var="verifierCount" value="#verifierCount + 1" />
                            <tr>
                                <td><s:property value="#verifierCount" /></td>
                                <td><s:property value="username" /></td>
                                <td><s:property value="emailId" /></td>
                                <td><s:property value="userType" /></td>
                                <td><s:property value="status" /></td>
                                <td>
                                    <form action="accept_verifier" method="post" style="display: inline;">
                                        <input type="hidden" name="username" value="<s:property value='username' />" />
                                        <button type="submit" class="accept-link">Accept</button>
                                    </form>
                                    <form action="reject_verifier" method="post" style="display: inline;">
                                        <input type="hidden" name="username" value="<s:property value='username' />" />
                                        <button type="submit" class="reject-link">Reject</button>
                                    </form>
                                </td>
                            </tr>
                        </s:iterator>
                    </tbody>
                </table>
            </s:else>
        </div>


        <div id="verifiers" class="section hidden">
            <h2>Verifiers</h2>
            <%-- <p>No verifiers available at this moment.</p> --%>

            <table>
                <thead>
                    <tr>
                        <th>User ID</th>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <s:set var="verifierCount" value="0" />
                    <s:iterator value="users">
                        <s:if test="userType=='verifier'">
                            <s:set var="verifierCount" value="#verifierCount + 1" />
                            <tr>
                                <td><s:property value="#verifierCount" /></td>
                                <td><s:property value="username" /></td>
                                <td><s:property value="emailId" /></td>
                                <td><s:property value="userType" /></td>
                                <td><s:property value="status" /></td>
                            </tr>
                        </s:if>
                    </s:iterator>
                </tbody>
            </table>
        </div>


        <div id="rejected-users" class="section hidden">
            <h2>Rejected verifiers</h2>
            <table>
                <thead>
                    <tr>
                        <th>User ID</th>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Options</th>
                    </tr>
                </thead>
                <tbody>
                    <s:set var="verifierCount" value="0" />
                    <s:iterator value="users">
                        <s:if test="userType=='verifier' && status=='Rejected' ">
                            <s:set var="verifierCount" value="#verifierCount + 1" />
                            <tr>
                                <td><s:property value="#verifierCount" /></td>
                                <td><s:property value="username" /></td>
                                <td><s:property value="emailId" /></td>
                                <td><s:property value="userType" /></td>
                                <td>
                                    <form action="accept_verifier" method="post" style="display: inline;">
                                        <input type="hidden" name="username" value="<s:property value='username' />" />
                                        <button type="submit" class="accept-link">Accept</button>
                                    </form>
                                </td>
                            </tr>
                        </s:if>
                    </s:iterator>
                </tbody>
            </table>
        </div>

        <%-- <div id="borrowers" class="section hidden">
            <h2>Borrowers</h2>
            <p>No borrowers available at this moment.</p>

            <table>
                <thead>
                    <tr>
                        <th>User ID</th>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <s:set var="borrowerCount" value="0" />
                    <s:iterator value="users">
                        <s:if test="userType=='borrower' ">
                            <s:set var="borrowerCount" value="#borrowerCount + 1" />
                            <tr>
                                <td><s:property value="#borrowerCount" /></td>
                                <td><s:property value="username" /></td>
                                <td><s:property value="emailId" /></td>
                                <td><s:property value="userType" /></td>
                                <td><s:property value="status" /></td>
                            </tr>
                        </s:if>
                    </s:iterator>
                </tbody>
            </table>
        </div> --%>

        <%-- <div id="lenders" class="section hidden">
            <h2>Lenders</h2>
            <p>No lenders available at this moment.</p>

            <table>
                <thead>
                    <tr>
                        <th>User ID</th>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <s:set var="lenderCount" value="0" />
                    <s:iterator value="users">
                        <s:if test="userType=='lender'">
                            <s:set var="lenderCount" value="#lenderCount + 1" />
                            <tr>
                                <td><s:property value="#lenderCount" /></td>
                                <td><s:property value="username" /></td>
                                <td><s:property value="emailId" /></td>
                                <td><s:property value="userType" /></td>
                                <td><s:property value="status" /></td>
                            </tr>
                        </s:if>
                    </s:iterator>
                </tbody>
            </table>
        </div> --%>

    </div>

    <script>
        function showSection(sectionId) {
            // Hide all sections
            const sections = document.querySelectorAll('.section');
            sections.forEach(section => section.classList.add('hidden'));

            // Show the selected section
            const activeSection = document.getElementById(sectionId);
            if (activeSection) {
                activeSection.classList.remove('hidden');
            }
        }

        // Show the "All Users" section by default
        showSection('all-users');
    </script>
</body>
</html>

