package com.loanapp.utils;

import java.lang.Math;

public class GeneratePassword {
    private StringBuilder password = new StringBuilder();   

    private int smallLetterStart = 97;
    private int smallLetterEnd = 122;
    private int smallLetterRange = getRange(smallLetterStart, smallLetterEnd);

    private int capsLetterStart = 65;
    private int capsLetterEnd = 90;
    private int capsLetterRange = getRange(capsLetterStart, capsLetterEnd);

    private char[] symbols = {'!', '@', '#', '$', '%', '^', '&', '*','-', '_', '=', '+','<', '>','?',};
    private int symbolsRange = getRange(0, symbols.length - 1);

    //method for getting the range 
    private int getRange(int start,int end){
        return (end - start + 1);
    }
    
    // method for genearting the password
    public String generatePassword(){

        for(int i=0;i<2;i++){
            // small letters
            for(int j=0;j<3;j++){
                int rand = (int)(Math.random() * smallLetterRange) + smallLetterStart;
                password.append((char)rand);
            }

            // capital letters
            for(int j=0;j<2;j++){
                int rand = (int)(Math.random() * capsLetterRange) + capsLetterStart;
                password.append((char)rand);
            }

            // digits
            int digitRandom = (int)(Math.random() * 10) + 1;
            password.append(digitRandom);

            // symbol
            char charRandom = symbols[(int)(Math.random() * symbolsRange)];
            password.append(charRandom);
        }
        return password.toString();
    }

}
