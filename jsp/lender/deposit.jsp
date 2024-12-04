<%@ taglib uri="/struts-tags" prefix="s" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Deposit Page</title>
    <!-- Include Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <style>
        /* Reset default styling */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        /* Page background */
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f4f4f9;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            padding: 20px;
        }

        /* Form container */
        .payment-container {
            background-color: white;
            border-radius: 10px;
            box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);
            width: 480px; /* Increased width */
            padding: 30px;
            text-align: left;
            border: 1px solid #e5e5e5;
        }

        /* Heading styles */
        h2 {
            font-size: 28px;
            margin-bottom: 20px;
            color: #333;
            font-weight: bold;
            text-align: center;
        }

        /* Icon styling */
        .input-icon {
            position: absolute;
            left: 15px;
            top: 50%;
            transform: translateY(-50%);
            color: #999;
        }

        /* Input field container */
        .input-container {
            position: relative;
            margin: 20px 0;
        }

        /* Input field styles */
        .input-field {
            width: 100%;
            padding: 14px 15px 14px 45px;
            margin: 8px 0;
            border: 1px solid #ddd;
            border-radius: 8px;
            font-size: 16px;
            outline: none;
            background-color: #f9f9f9;
            transition: border 0.3s ease;
        }

        .input-field:focus {
            border-color: #4CAF50;
        }

        /* Card type selector */
        .card-type-selector {
            width: 100%;
            padding: 14px 15px;
            margin: 20px 0;
            border: 1px solid #ddd;
            border-radius: 8px;
            font-size: 16px;
            background-color: white;
            cursor: pointer;
        }

        /* Button styles */
        .btn-submit {
            background-color: #4CAF50;
            color: white;
            padding: 16px;
            border: none;
            border-radius: 8px;
            font-size: 18px;
            width: 100%;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .btn-submit:hover {
            background-color: #45a049;
        }

        .btn-submit:active {
            background-color: #388e3c;
        }

        /* Footer text */
        .footer-text {
            margin-top: 20px;
            font-size: 14px;
            color: #777;
            text-align: center;
        }

        /* Payment icons */
        .card-icons {
            margin-top: 25px;
            text-align: center;
        }

        .card-icons img {
            width: 45px;
            margin-right: 15px;
        }

        .card-icons span {
            font-size: 14px;
            color: #777;
        }

        /* Mobile responsiveness */
        @media (max-width: 600px) {
            .payment-container {
                width: 100%;
                padding: 20px;
            }

            .input-field {
                padding: 12px 15px 12px 40px;
            }

            .btn-submit {
                padding: 14px;
            }

            h2 {
                font-size: 24px;
            }
        }
    </style>
</head>
<body>
    <div class="payment-container">
        <h2>Deposit Amount</h2>
        <form action="deposit_amount" method="post">

            <div class="input-container"> 
                <input type="text" class="input-field" placeholder="Amount" name="depositAmount" required>
                <i class="fa-solid fa-indian-rupee-sign input-icon"></i>
            </div>

            <div class="input-container">
                <input type="text" class="input-field" placeholder="Cardholder Name" required>
                <i class="fas fa-user input-icon"></i>
            </div>

            <div class="input-container">
                <input type="text" class="input-field" placeholder="Card Number" required>
                <i class="fas fa-credit-card input-icon"></i>
            </div>

            <div class="input-container">
                <input type="text" class="input-field" placeholder="MM/YY" required>
                <i class="fas fa-calendar-alt input-icon"></i>
            </div>

            <div class="input-container">
                <input type="text" class="input-field" placeholder="CVV" required>
                <i class="fas fa-lock input-icon"></i>
            </div>

            <select class="card-type-selector">
                <option value="visa">Visa</option>
                <option value="mastercard">Mastercard</option>
                <option value="amex">American Express</option>
            </select>

            <button type="submit" class="btn-submit">Pay Now</button>
        </form>

        <p class="footer-text">Your payment is securely processed.</p>
    </div>
</body>
</html>
