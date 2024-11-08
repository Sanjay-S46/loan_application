<%@ taglib uri="/struts-tags" prefix="s" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Loan Application</title>
    <style>
        body{
            background-color: #f0f2f5;
        }
        .navbar {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 1rem;
        }

        .navbar h1 {
            margin: 0;
            color: #333;
        }

        .buttons a {
            display: inline-block;
            text-decoration: none;
            background-color: rgb(104, 104, 220);
            color: white;
            font-size: 20px;
            padding: 10px 20px;
            border-radius: 10px;
            margin-left: 10px;
            transition: background-color 0.3s;
        }

        .buttons a:hover {
            background-color: rgb(84, 84, 180);
        }
    </style>
</head>
<body>
    <div class="navbar">
        <h1>Loan Application</h1>
        <div class="buttons">
            <a href='<s:url action="logout" />'>Logout</a>
        </div>
    </div>
</body>
</html>
