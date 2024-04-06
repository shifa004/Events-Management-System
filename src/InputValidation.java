import java.text.Normalizer;
import java.text.Normalizer.Form;
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

    
    public static boolean checkFirstName(String name){
        //Allow any string that contains only letters and zero or more occurences of space between the letters.
        String regex = "^[a-zA-Z]+$";

        // Compile the regex into a pattern
        Pattern pattern = Pattern.compile(regex);

        // Match the given email against the pattern
        Matcher matcher = pattern.matcher(name);
        // validation
        if (matcher.matches()) {
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean checkLastName(String name){
        //Allow any string that contains at least one alphabetical character (either uppercase or lowercase).
        String regex = "^[a-zA-Z]+$";

        // Compile the regex into a pattern
        Pattern pattern = Pattern.compile(regex);

        // Match the given email against the pattern
        Matcher matcher = pattern.matcher(name);
        // validation
        if (matcher.matches()) {
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean checkPassword(String password){
        //At least 8 characters, includes lowercase, uppercase, numbers, and special characters
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&()-_=+\\[\\]{}|;:'\",.<>?]{8,}$";

        // Compile the regex into a pattern
        Pattern pattern = Pattern.compile(regex);

        // Match the given email against the pattern
        Matcher matcher = pattern.matcher(password);
        // validation
        if (matcher.matches()) {
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean checkUsername(String username){
        //Allows lower case letters, numbers from 0-9, hyphen and underscore and the length can be from 5 to 20
        String regex = "[a-z0-9-_]{5,20}";

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
    // public void checkUsername(String username){
    //     String regex = "[a-z-_]{2,10}";
    //     Pattern pattern = Pattern.compile(regex);
    //     Matcher matcher = pattern.matcher(username);
    //     if (matcher.matches()) {
    //         System.out.println("Username is valid");
    //     }else{
    //         System.out.println("Invalid username");
    //     }
    // }
    public static Boolean checkSearchField(String searchInput){
        // Normalize the search input
        String normalizedInput = Normalizer.normalize(searchInput, Form.NFKC);
        //Check if it contains any angular brackets
        String regex = ".*[<>].*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(normalizedInput);
        if (matcher.matches()){
            return false;
        }
        else{
            return true;
        }
    }

    public static Boolean checkEventName(String name){
        //Allows any strings that contain only alphanumeric characters, along with the characters '!', ',', '-', '(', ')', '&', and whitespace characters.
        String regex = "^[a-zA-Z0-9!,&.()*\\s]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        if (matcher.matches()) {
            return true;
        }else{
            return false;
        }
    }

    public static Boolean checkEventDescription(String description){
        //Any strings that have a length between 1 and 1000 characters, inclusive.
        String regex = "^.{1,1000}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(description);
        if (matcher.matches()) {
            return true;
        }else{
            return false;
        }
    }
    
    public static Boolean checkEventDate(String date){
        //\d{1,2}: Matches one or two digits, representing the day.
        // \s: Matches whitespace (space character).
        // (?:January|February|March|April|May|June|July|August|September|October|November|December): Non-capturing group that matches any of the month names.
        // \d{4}: Matches exactly four digits, representing the year.
        // -: Matches the hyphen character, separating the start and end dates.
        // The pattern repeats to match the end date in the same format as the start date.
        String regex = "^\\d{1,2}\\s(?:January|February|March|April|May|June|July|August|September|October|November|December)\\s\\d{4}\\s-\\s\\d{1,2}\\s(?:January|February|March|April|May|June|July|August|September|October|November|December)\\s\\d{4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(date);
        if (matcher.matches()) {
            return true;
        }else{
            return false;
        }
    }

    public static Boolean checkEventTime(String time){
        //(0[1-9]|1[0-2]): Matches the hour in 12-hour format (01 to 12).
        // :[0-5][0-9]: Matches the minutes (00 to 59).
        // \s: Matches whitespace (space character).
        // (?:am|pm): Non-capturing group that matches either "am" or "pm".
        String regex = "^(0[1-9]|1[0-2]):[0-5][0-9]\\s(?:am|pm)\\s-\\s(0[1-9]|1[0-2]):[0-5][0-9]\\s(?:am|pm)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(time);
        if (matcher.matches()) {
            return true;
        }else{
            return false;
        }
    }

    public static Boolean checkEventLocation(String location){
        //Any strings that contain only alphanumeric characters, spaces, commas, periods, and hyphens 
        String regex = "^[a-zA-Z0-9\s.,-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(location);
        if (matcher.matches()) {
            return true;
        }else{
            return false;
        }
    }

    public static Boolean checkEventImage(String image){
        String regex = "^https?:\\/\\/(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(?:\\/[^\\s]*)?(?:\\.png|\\.jpg|\\.jpeg|\\.gif|\\.bmp)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(image);
        if (matcher.matches()) {
            return true;
        }else{
            return false;
        }
    }

    public static Boolean checkEventCategory(String category){
        //Any lower and upper case letters that are between 1 and 50 characters in length, inclusive.
        String regex = "^[a-zA-Z]{1,50}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(category);
        if (matcher.matches()) {
            return true;
        }else{
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
