<%@ taglib uri="/struts-tags" prefix="s" %> 

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Loan Application</title>
    <link rel="stylesheet" href="/loanApplication/css/verifierStyle.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>
    <div class="navbar">
        <h1>Loan Application</h1>
        <div class="nav-links">
            <a onclick="showSection('borrowers')">Borrowers</a>
            <a onclick="showSection('lenders')">Lenders</a>
            <a onclick="showSection('incoming-requests')">Incoming Requests</a>
            <a onclick="showSection('rejected-users')">Rejected Users</a>
            <a onclick="showSection('blocked-users')">Blocked Users</a>
        </div>
        <div class="buttons">
            <a href='<s:url action="home" />' id="home-icon"><i class="fa fa-house"></i></a>
            <a href='<s:url action="userProfile" />' id="profile-icon"><i class="fa fa-user"></i></a>
            <a href='<s:url action="logout" />' id="logout-button">Logout</a>
        </div>

    </div>

    <div class="content">

        <div id="incoming-requests" class="section hidden">
            <h2>Incoming Requests</h2>

            <s:if test="newUsers.size() == 0">
                <p>No incoming requests at this moment.</p>
            </s:if>
            <s:else>
                <table>
                    <thead>
                        <tr>
                            <th>User ID</th>
                            <th>Username</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th>Options</th>
                        </tr>
                    </thead>
                    <tbody>
                        <s:set var="verifierCount" value="0" />
                        <s:iterator value="newUsers">
                            <s:set var="verifierCount" value="#verifierCount + 1" />
                            <tr>
                                <td><s:property value="#verifierCount" /></td>
                                <td><s:property value="username" /></td>
                                <td><s:property value="emailId" /></td>
                                <td><s:property value="userType" /></td>
                                <td><s:property value="status" /></td>
                                <td>
                                    <form action="accept_user" method="post" style="display: inline;">
                                        <input type="hidden" name="username" value="<s:property value='username' />" />
                                        <button type="submit" class="accept-link">Accept</button>
                                    </form>
                                    <form action="reject_user" method="post" style="display: inline;">
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

        <div id="borrowers" class="section hidden">
            <h2>Borrowers</h2>

            <s:if test="borrowers.size() == 0">
                <p>No borrowers available at this moment.</p>
            </s:if>
            <s:else>
                <table>
                    <thead>
                        <tr>
                            <th>User ID</th>
                            <th>Username</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th>Options</th>
                        </tr>
                    </thead>
                    <tbody>
                        <s:set var="borrowerCount" value="0" />
                        <s:iterator value="borrowers">
                            <s:if test="userType=='borrower'">
                                <s:set var="borrowerCount" value="#borrowerCount + 1" />
                                <tr>
                                    <td><s:property value="#borrowerCount" /></td>
                                    <td><s:property value="username" /></td>
                                    <td><s:property value="emailId" /></td>
                                    <td><s:property value="userType" /></td>
                                    <td><s:property value="status" /></td>
                                    <td>
                                        <form action="block_user" method="post" style="display: inline;">
                                            <input type="hidden" name="username" value="<s:property value='username' />" />
                                            <button type="submit" class="reject-link">Block</button>
                                        </form>
                                    </td>
                                </tr>
                            </s:if>
                        </s:iterator>
                    </tbody>
                </table>
            </s:else>
        </div>

        <div id="lenders" class="section hidden">
            <h2>Lenders</h2>
            <s:if test="lenders.size() == 0">
                <p>No lenders available at this moment.</p>
            </s:if>
            <s:else>
                <table>
                    <thead>
                        <tr>
                            <th>User ID</th>
                            <th>Username</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th>Options</th>
                        </tr>
                    </thead>
                    <tbody>
                        <s:set var="lenderCount" value="0" />
                        <s:iterator value="lenders">
                            <s:if test="userType=='lender'">
                                <s:set var="lenderCount" value="#lenderCount + 1" />
                                <tr>
                                    <td><s:property value="#lenderCount" /></td>
                                    <td><s:property value="username" /></td>
                                    <td><s:property value="emailId" /></td>
                                    <td><s:property value="userType" /></td>
                                    <td><s:property value="status" /></td>
                                    <td>
                                        <form action="block_user" method="post" style="display: inline;">
                                            <input type="hidden" name="username" value="<s:property value='username' />" />
                                            <button type="submit" class="reject-link">Block</button>
                                        </form>
                                    </td>
                                </tr>
                            </s:if>
                        </s:iterator>
                    </tbody>
                </table>
            </s:else>
        </div>

        <div id="rejected-users" class="section hidden">
            <h2>Rejected Users</h2>
            <s:if test="rejectedUsers.size() == 0">
                <p>No rejected users available at this moment.</p>
            </s:if>
            <s:else>
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
                        <s:set var="counter" value="0" />
                        <s:iterator value="rejectedUsers">
                            <s:set var="counter" value="#counter + 1" />
                            <tr>
                                <td><s:property value="#counter" /></td>
                                <td><s:property value="username" /></td>
                                <td><s:property value="emailId" /></td>
                                <td><s:property value="userType" /></td>
                                <td>
                                    <form action="accept_user" method="post" style="display: inline;">
                                        <input type="hidden" name="username" value="<s:property value='username' />" />
                                        <button type="submit" class="accept-link">Accept</button>
                                    </form>
                                </td>
                            </tr>
                        </s:iterator>
                    </tbody>
                </table>
            </s:else>
        </div>

        <div id="blocked-users" class="section hidden">
            <h2>Blocked Users</h2>

            <s:if test="blockedUsers.size() == 0">
                <p>No Blocked Users at this moment.</p>
            </s:if>
            <s:else>
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
                        <s:set var="count" value="0" />
                        <s:iterator value="blockedUsers">
                            <s:set var="count" value="#count + 1" />
                            <tr>
                                <td><s:property value="#count" /></td>
                                <td><s:property value="username" /></td>
                                <td><s:property value="emailId" /></td>
                                <td><s:property value="userType" /></td>
                                <td>
                                    <form action="allow_user" method="post" style="display: inline;">
                                        <input type="hidden" name="username" value="<s:property value='username' />" />
                                        <button type="submit" class="accept-link">Allow</button>
                                    </form>                                
                                </td>
                            </tr>
                        </s:iterator>
                    </tbody>
                </table>
            </s:else>
        </div>

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

        showSection('borrowers');
    </script>
</body>
</html>

