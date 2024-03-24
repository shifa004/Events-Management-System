import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidation {
    // public static void main(String[] args) {
    //     try (Scanner inputReader = new Scanner(System.in)) {
    //         System.out.println("Enter email address: ");
    //         // Read user input
    //         String email = inputReader.nextLine();
    //         // Define the regular expression for valid email, it can only contain
    //         // letters, digits, underscores, hyphens, or dots in the username
    //         // followed by the @, followed by a domain name which can only contain
    //         // letters, digits, underscores, or hyphens, followed by the TLD
    //         // which can only contain letters, digits, underscores, or hyphens.
    //         // The TLD must have a length between 2 and 4 characters
    //         String regex = "^[\\w-\\.]+@([\\w-]+\\.)?[\\w-]{2,63}$";
            
    //         // Compile the regex into a pattern
    //         Pattern pattern = Pattern.compile(regex);

    //         // Match the given email against the pattern
    //         Matcher matcher = pattern.matcher(email);
    //         // validation
    //         if (matcher.matches()) {
    //             System.out.println("Email address is valid");
    //         }else{
    //             System.out.println("Invalid email");
    //         }
    //     }

        // checkUsername();
        // checkCreditCardNumber();
        // checkPhoneNumber();
        // checkDate();
        // checkInputString();
    // }

    public boolean checkUsername(String username){
        String regex = "[a-z-_]{2,10}";

        // Compile the regex into a pattern
        Pattern pattern = Pattern.compile(regex);

        // Match the given email against the pattern
        Matcher matcher = pattern.matcher(username);
        // validation
        if (matcher.matches()) {
            return true;
        }
        else {
            return false;
        }
    }

    public static void checkCreditCardNumber(){
        try (Scanner inputReader = new Scanner(System.in)) {
            System.out.println("Enter credit card number: ");
            // Read user input
            String num = inputReader.nextLine();
            String regex = "^(\\d{4}[ -]){3}\\d{4}$";

            // Compile the regex into a pattern
            Pattern pattern = Pattern.compile(regex);

            // Match the given email against the pattern
            Matcher matcher = pattern.matcher(num);
            // validation
            if (matcher.matches()) {
                System.out.println("Credit Card Number is valid");
            }else{
                System.out.println("Invalid Credit Card Number");
            }
        }
    }

    public static void checkPhoneNumber(){
        try (Scanner inputReader = new Scanner(System.in)) {
            System.out.println("Enter phone number: ");
            // Read user input
            String number = inputReader.nextLine();
            String regex = "^((\\+974)|(00974)|(\\(974\\)))\\d{8}$";

            // Compile the regex into a pattern
            Pattern pattern = Pattern.compile(regex);

            // Match the given email against the pattern
            Matcher matcher = pattern.matcher(number);
            // validation
            if (matcher.matches()) {
                System.out.println("Number is valid");
            }else{
                System.out.println("Invalid number");
            }
        }
    }
    public static void checkDate(){
        try (Scanner inputReader = new Scanner(System.in)) {
            System.out.println("Enter date: ");
            // Read user input
            String number = inputReader.nextLine();
            String regex = "^(\\d{2}(-|/)){2}\\d{2,4}$";

            // Compile the regex into a pattern
            Pattern pattern = Pattern.compile(regex);

            // Match the given email against the pattern
            Matcher matcher = pattern.matcher(number);
            // validation
            if (matcher.matches()) {
                System.out.println("date is valid");
            }else{
                System.out.println("Invalid date");
            }
        }
    }
    public static void checkInputString(){
        try (Scanner inputReader = new Scanner(System.in)) {
            System.out.println("Enter string: ");
            // Read user input
            String string = inputReader.nextLine();
            String regex = "(?).*(<\\/script>|<script>).*";

            // Compile the regex into a pattern
            Pattern pattern = Pattern.compile(regex);

            // Match the given email against the pattern
            Matcher matcher = pattern.matcher(string);
            // validation
            if (matcher.find()) {
                System.out.println("string is invalid");
            }else{
                System.out.println("Valid string");
            }
        }
    }
}
