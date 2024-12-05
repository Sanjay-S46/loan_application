<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Loan Application</title>
    <style>
        body {
            background-color: #f0f2f5;
            font-family: Arial, sans-serif;
        }

        .navbar {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 1rem 2rem;
        }

        .navbar h1 {
            margin: 0;
            color: #333;
        }

        .buttons a {
            display: inline-block;
            text-decoration: none;
            background-color: #007BFF; /* Blue color */
            color: white;
            font-size: 20px;
            padding: 10px 20px;
            border-radius: 10px;
            margin-left: 10px;
            transition: background-color 0.3s ease, transform 0.2s ease;
        }

        .buttons a:hover {
            background-color: #0056b3; 
            transform: translateY(-3px); 
        }
    </style>
</head>
<body>  
    <div class="navbar">
        <h1>Loan Application</h1>
        <div class="buttons">
            <a href="signup.jsp" class="signup-link">Signup</a>
            <a href="login.jsp" class="login-link">Login</a>
        </div>
    </div>
</body>
</html>
