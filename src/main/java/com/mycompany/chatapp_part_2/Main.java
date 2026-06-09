package com.mycompany.chatapp_part_2;

import com.mycompany.chatapp_part_2.Message;
import java.util.Scanner;

/**
 * Main class for the ChatApp application.
 * Handles registration, login, and the main menu.
 *
 * Part 3 changes:
 *  - Calls Message.loadStoredMessages() right after successful login
 *  - Menu updated to show option 4
 *  - case 4 added to the switch statement
 *  - storedMessagesMenu() method added at the bottom
 *
 * Everything else is exactly as it was in Part 2.
 *
 * @author Your Name
 * @version 3.0
 */
public class Main {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        Login user = new Login();

        System.out.println("=================================");
        System.out.println("       WELCOME TO QUICKCHAT      ");
        System.out.println("=================================");

        String username;
        String password;
        String phoneNumber;

        // ---------------------------------------------------------------
        // Registration - NOT changed from Part 2
        // ---------------------------------------------------------------
        while (true) {
            System.out.print("Enter username: ");
            username = input.nextLine();
            if (user.checkUserName(username)) {
                System.out.println("Username successfully captured.");
                break;
            }
            System.out.println("Username is not correctly formatted; please ensure it contains an underscore and is no more than 5 characters.");
        }

        while (true) {
            System.out.print("Enter password: ");
            password = input.nextLine();
            if (user.checkPasswordComplexity(password)) {
                System.out.println("Password successfully captured.");
                break;
            }
            System.out.println("Password is not correctly formatted; must contain 8 characters, a capital letter, a number, and a special character.");
        }

        while (true) {
            System.out.print("Enter cell phone number: ");
            phoneNumber = input.nextLine();
            if (user.checkCellPhoneNumber(phoneNumber)) {
                System.out.println("Cell phone number successfully added.");
                break;
            }
            System.out.println("Cell phone number incorrectly formatted or does not contain international code.");
        }

        System.out.println(user.registerUser(username, password, phoneNumber));

        // ---------------------------------------------------------------
        // Login - NOT changed from Part 2
        // ---------------------------------------------------------------
        boolean loggedIn = false;
        
        System.out.println("==== LOGIN ====");
        while (!loggedIn) {
            System.out.print("\nUsername: ");
            String loginUsername = input.nextLine();

            System.out.print("Password: ");
            String loginPassword = input.nextLine();

            loggedIn = user.loginUser(loginUsername, loginPassword);
            System.out.println(user.returnLoginStatus(loggedIn));
        }

        // ---------------------------------------------------------------
        // Part 3 - Load stored messages from JSON file right after login
        // ---------------------------------------------------------------
        Message.loadStoredMessages();

        // ---------------------------------------------------------------
        // Main menu - option 4 added; everything else NOT changed
        // ---------------------------------------------------------------
        boolean running = true;

        while (running) {

            System.out.println("\n1. Send Messages");
            System.out.println("2. Show Recently Sent Messages");
            System.out.println("3. Quit");
            System.out.println("4. Stored Messages");   // Part 3 - new option
            System.out.print("Choose your option 1-4: ");
            int menuChoice = input.nextInt();
            input.nextLine();

            switch (menuChoice) {

                case 1:

                    System.out.print("How many messages would you like to send? ");
                    int numberOfMessages = input.nextInt();
                    input.nextLine();

                    for (int i = 1; i <= numberOfMessages; i++) {

                        Message message = new Message(i);

                        String recipient;

                        while (true) {
                            System.out.print("Recipient Number: ");
                            recipient = input.nextLine();
                            String result = message.checkRecipientCell(recipient);
                            System.out.println(result);
                            if (result.equals("Cell phone number successfully captured.")) {
                                message.setRecipient(recipient);
                                break;
                            }
                        }

                        System.out.print("Enter Message: ");
                        String text = input.nextLine();

                        String messageCheck = message.checkMessageLength(text);
                        System.out.println(messageCheck);

                        if (!messageCheck.equals("Message ready to send.")) {
                            continue;
                        }
                        
                        System.out.println("==== MESSAGE DETAILS ====");
                        message.setMessageText(text);
                        message.createMessageHash();

                        System.out.println("Message ID: "   + message.getMessageId());
                        System.out.println("Message Hash: " + message.getMessageHash());
                        
                        System.out.println(" What do you want to do with the message.");
                        System.out.println("\n1. Send");
                        System.out.println("2. Disregard");
                        System.out.println("3. Store");
                        
                        System.out.print("Choose your option 1-3: ");
                        int choice = input.nextInt();
                        input.nextLine();

                        System.out.println(message.sentMessage(choice));
                    }

                    System.out.println("\nTotal Messages: " + Message.getTotalMessages());
                    break;

                case 2:
                    // Part 2 had "Coming Soon" here - now shows the real report
                    System.out.println(Message.printMessages());
                    break;

                case 3:
                    running = false;
                    System.out.println("Goodbye.");
                    break;

                case 4:
                    // Part 3 - new stored messages sub-menu
                    storedMessagesMenu(input);
                    break;

                default:
                    System.out.println("Invalid option selected.");
            }
        }

        input.close();
    }

    // ---------------------------------------------------------------
    // Part 3 - storedMessagesMenu added below main()
    // Uses the same Scanner (input) that main() uses - passed in as parameter
    // ---------------------------------------------------------------

    /**
     * Displays the Stored Messages sub-menu and handles each option.
     * Each option calls a method in the Message class.
     * Option 7 returns to the main menu.
     *
     * @param scanner The shared Scanner reading from System.in
     */
    private static void storedMessagesMenu(Scanner input) {

        String subChoice = "";

        while (!subChoice.equals("g")) {

            System.out.println("\n--- Stored Messages Menu ---");
            System.out.println("a) Display all stored messages");
            System.out.println("b) Display longest message");
            System.out.println("c) Search by message ID");
            System.out.println("d) Search by recipient");
            System.out.println("e) Delete by message hash");
            System.out.println("f) Display full report");
            System.out.println("g) Return to main menu");
            System.out.print("Enter your choice: ");

            subChoice = input.nextLine().trim().toLowerCase();

            if (subChoice.equals("a")) {
                System.out.println(Message.displayStoredMessages());

            } else if (subChoice.equals("b")) {
                Message tempMessage = new Message(0);
                System.out.println("Longest message: " + tempMessage.displayLongestMessage());

            } else if (subChoice.equals("c")) {
                System.out.print("Enter message ID to search for: ");
                String searchID = input.nextLine();
                Message tempMessage = new Message(0);
                System.out.println(tempMessage.searchByMessageID(searchID));

            } else if (subChoice.equals("d")) {
                System.out.print("Enter recipient number to search for: ");
                String searchRecipient = input.nextLine();
                Message tempMessage = new Message(0);
                System.out.println(tempMessage.searchByRecipient(searchRecipient));

            } else if (subChoice.equals("e")) {
                System.out.print("Enter message hash to delete: ");
                String deleteHash = input.nextLine();
                System.out.println(Message.deleteByHash(deleteHash));

            } else if (subChoice.equals("f")) {
                System.out.println(Message.printMessages());

            } else if (subChoice.equals("g")) {
                System.out.println("Returning to main menu.");

            } else {
                System.out.println("Invalid option. Please enter a letter between a and g.");
            }
        }
    }
}
