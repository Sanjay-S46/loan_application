<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Waiting Page</title>
    <style>
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            background-color: #f7f9fc;
            font-family: 'Arial', sans-serif;
        }

        .circle {
            width: 400px; 
            height: 400px; 
            border-radius: 50%;
            background: linear-gradient(135deg, #6a11cb, #2575fc);
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            position: relative;
            color: white;
            text-align: center;
            box-shadow: 0 4px 30px rgba(0, 0, 0, 0.2);
            padding: 30px; 
        }

        .circle::before {
            content: '';
            position: absolute;
            top: 10px;
            left: 10px;
            width: 100%;
            height: 100%;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.1);
            z-index: 0;
        }

        .circle h1 {
            position: relative;
            z-index: 1;
            font-size: 28px; 
            margin: 0 0 15px; 
        }

        .circle p {
            position: relative;
            z-index: 1;
            font-size: 18px; 
            margin: 0; 
            line-height: 1.5; 
        }

        .decorative {
            position: absolute;
            width: 100%;
            height: 100%;
            top: 0;
            left: 0;
            pointer-events: none;
        }

        .decorative div {
            position: absolute;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 50%;
            animation: pulse 3s infinite;
        }

        @keyframes pulse {
            0% {
                transform: scale(0.9);
                opacity: 0.5;
            }
            50% {
                transform: scale(1);
                opacity: 0.2;
            }
            100% {
                transform: scale(0.9);
                opacity: 0.5;
            }
        }

        .decorative div:nth-child(1) {
            width: 80px;
            height: 80px;
            top: 15%;
            left: 15%;
            animation-delay: 0s;
        }

        .decorative div:nth-child(2) {
            width: 120px;
            height: 120px;
            top: 55%;
            left: 55%;
            animation-delay: 0.5s;
        }

        .decorative div:nth-child(3) {
            width: 160px;
            height: 160px;
            top: 5%;
            left: 75%;
            animation-delay: 1s;
        }

        .decorative div:nth-child(4) {
            width: 70px;
            height: 70px;
            top: 80%;
            left: 25%;
            animation-delay: 1.5s;
        }
    </style>
</head>
<body>
    <div class="circle">
        <h1>Verification in progress</h1>
        <p>Your account is currently being verified. The password will be sent to your registered email and phone number shortly. Thank you for your patience!</p>
        <div class="decorative">
            <div></div>
            <div></div>
            <div></div>
            <div></div>
        </div>
    </div>
</body>
</html>
