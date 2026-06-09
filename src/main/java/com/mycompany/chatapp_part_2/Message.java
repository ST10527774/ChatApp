package com.mycompany.chatapp_part_2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import org.json.JSONObject;

/**
 * Message class handles all message operations for the ChatApp.
 * Part 3 adds five arrays, JSON reading, search, delete, and report features.
 *
 * @author Your Name
 * @version 3.0
 */
public class Message {

    // ---------------------------------------------------------------
    // Existing Part 2 fields - NOT changed
    // ---------------------------------------------------------------
    private String messageId;
    private int messageNumber;
    private String recipient;
    private String messageText;
    private String messageHash;

    private static int totalMessages = 0;

    // ---------------------------------------------------------------
    // Part 3 - Five static arrays that stay alive for the whole session
    // ---------------------------------------------------------------
    private static ArrayList<String> sentMessages        = new ArrayList<>();
    private static ArrayList<String> disregardedMessages = new ArrayList<>();
    private static ArrayList<String> storedMessages      = new ArrayList<>();
    private static ArrayList<String> messageHashes       = new ArrayList<>();
    private static ArrayList<String> messageIDs          = new ArrayList<>();

    // Extra array to track the recipient for each sent message
    private static ArrayList<String> sentRecipients      = new ArrayList<>();

    // ---------------------------------------------------------------
    // Existing Part 2 constructor - NOT changed
    // ---------------------------------------------------------------

    /**
     * Creates a new Message with a generated ID and increments the total count.
     *
     * @param messageNumber The number of this message in the current session
     */
    public Message(int messageNumber) {
        this.messageNumber = messageNumber;
        this.messageId = generateMessageId();
        totalMessages++;
    }

    // ---------------------------------------------------------------
    // Existing Part 2 private helper - NOT changed
    // ---------------------------------------------------------------

    /**
     * Generates a random 10-digit message ID.
     *
     * @return A 10-digit ID as a String
     */
    private String generateMessageId() {
        Random random = new Random();
        long number = 1000000000L + (long) (random.nextDouble() * 9000000000L);
        return Long.toString(number);
    }

    // ---------------------------------------------------------------
    // Existing Part 2 validation methods - NOT changed
    // ---------------------------------------------------------------

    /**
     * Checks that the message ID is not longer than 10 characters.
     *
     * @return true if the ID is valid
     */
    public boolean checkMessageID() {
        return messageId != null && messageId.length() <= 10;
    }

    /**
     * Checks that the message text is not longer than 250 characters.
     *
     * @param text The message text to check
     * @return A result message
     */
    public String checkMessageLength(String text) {
        if (text == null) {
            text = "";
        }
        if (text.length() <= 250) {
            return "Message ready to send.";
        }
        int excessCharacters = text.length() - 250;
        return "Message exceeds 250 characters by " + excessCharacters + "; please reduce the size.";
    }

    /**
     * Checks that the cell number starts with +27 and is exactly 12 characters.
     *
     * @param cellNumber The cell number to check
     * @return A result message
     */
    public String checkRecipientCell(String cellNumber) {
        if (cellNumber != null && cellNumber.startsWith("+27") && cellNumber.length() == 12) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    /**
     * Generates and stores the message hash.
     * Format: first two digits of ID : message number : first word + last word (uppercase)
     *
     * @return The generated hash
     */
    public String createMessageHash() {
        if (messageText == null || messageText.trim().isEmpty()) {
            messageHash = "";
            return messageHash;
        }
        String[] words        = messageText.trim().split("\\s+");
        String firstWord      = removePunctuation(words[0]);
        String lastWord       = removePunctuation(words[words.length - 1]);
        String firstTwoDigits = messageId.substring(0, 2);
        messageHash = (firstTwoDigits + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
        return messageHash;
    }

    /**
     * Removes all non-letter characters from a word.
     *
     * @param word The word to clean
     * @return The cleaned word
     */
    private String removePunctuation(String word) {
        return word.replaceAll("[^a-zA-Z]", "");
    }

    // ---------------------------------------------------------------
    // sentMessage - Part 3 array population added inside each case.
    // The return values and structure are NOT changed.
    // ---------------------------------------------------------------

    /**
     * Processes the user's choice for this message.
     * Populates the correct Part 3 arrays based on the choice made.
     *
     * @param choice 1 = Send, 2 = Disregard, 3 = Store
     * @return A confirmation message
     */
    public String sentMessage(int choice) {

        switch (choice) {

            case 1:
                // Part 3 - add to sent arrays
                sentMessages.add(messageText);
                messageHashes.add(messageHash);
                messageIDs.add(messageId);
                sentRecipients.add(recipient);
                return "Message successfully sent.";

            case 2:
                // Part 3 - add to disregarded array
                disregardedMessages.add(messageText);
                return "Message disregarded.";

            case 3:
                // Part 3 - add hash and ID to arrays, then write to JSON
                messageHashes.add(messageHash);
                messageIDs.add(messageId);
                storeMessage();
                return "Message successfully stored.";

            default:
                return "Invalid option selected.";
        }
    }

    // ---------------------------------------------------------------
    // Existing Part 2 static getter - NOT changed
    // ---------------------------------------------------------------

    /**
     * Returns the total number of Message objects created this session.
     *
     * @return Total message count
     */
    public static int getTotalMessages() {
        return totalMessages;
    }

    // ---------------------------------------------------------------
    // Existing Part 2 getters and setters - NOT changed
    // ---------------------------------------------------------------

    /** Returns the message ID. */
    public String getMessageId() {
        return messageId;
    }

    /** Returns the message number. */
    public int getMessageNumber() {
        return messageNumber;
    }

    /** Returns the recipient cell number. */
    public String getRecipient() {
        return recipient;
    }

    /** Returns the message text. */
    public String getMessageText() {
        return messageText;
    }

    /** Returns the message hash. */
    public String getMessageHash() {
        return messageHash;
    }

    /** Sets the recipient cell number. */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /** Sets the message text. */
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    // ---------------------------------------------------------------
    // Existing Part 2 storeMessage - NOT changed
    // Attribution: org.json library - https://mvnrepository.com/artifact/org.json/json
    // ---------------------------------------------------------------

    /**
     * Writes this message to messages.json, one JSON object per line.
     */
    public void storeMessage() {
        try {
            // Attribution: org.json library - https://mvnrepository.com/artifact/org.json/json
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("messageID",   messageId);
            jsonMessage.put("recipient",   recipient);
            jsonMessage.put("messageText", messageText);
            jsonMessage.put("messageHash", messageHash);

            FileWriter file = new FileWriter("messages.json", true);
            file.write(jsonMessage.toString());
            file.write(System.lineSeparator());
            file.close();

        } catch (IOException e) {
            System.out.println("Error storing message.");
        }
    }

    // ---------------------------------------------------------------
    // Part 3 - Section 5: Read JSON file into storedMessages array
    // Attribution: org.json library - https://mvnrepository.com/artifact/org.json/json
    // ---------------------------------------------------------------

    /**
     * Reads messages.json and loads each message text into the storedMessages array.
     * Called once right after login. Exits gracefully if no file exists yet.
     */
    public static void loadStoredMessages() {
        try {
            // Attribution: org.json library - https://mvnrepository.com/artifact/org.json/json
            BufferedReader reader = new BufferedReader(new FileReader("messages.json"));
            String line = reader.readLine();

            while (line != null) {
                JSONObject jsonObject = new JSONObject(line);
                String text = jsonObject.getString("messageText");
                storedMessages.add(text);
                line = reader.readLine();
            }

            reader.close();
            System.out.println("Stored messages loaded successfully.");

        } catch (IOException e) {
            // File does not exist yet - this is fine on a first run
            System.out.println("No stored messages file found. Starting fresh.");
        }
    }

    // ---------------------------------------------------------------
    // Part 3 - Section 3: Display longest message
    // ---------------------------------------------------------------

    /**
     * Searches the storedMessages array and returns the longest message.
     *
     * @return The longest message string, or a notice if the array is empty
     */
    public String displayLongestMessage() {
        if (storedMessages.isEmpty()) {
            return "No stored messages found.";
        }

        String longest = storedMessages.get(0);

        for (int i = 1; i < storedMessages.size(); i++) {
            if (storedMessages.get(i).length() > longest.length()) {
                longest = storedMessages.get(i);
            }
        }

        return longest;
    }

    // ---------------------------------------------------------------
    // Part 3 - Section 4: Search by message ID
    // ---------------------------------------------------------------

    /**
     * Searches the messageIDs array for the given ID and returns the matching sent message.
     *
     * @param id The message ID to search for
     * @return The matching message text, or a not-found message
     */
    public String searchByMessageID(String id) {
        for (int i = 0; i < messageIDs.size(); i++) {
            if (messageIDs.get(i).equals(id)) {
                if (i < sentMessages.size()) {
                    return sentMessages.get(i);
                }
            }
        }
        return "Message ID not found.";
    }

    // ---------------------------------------------------------------
    // Part 3 - Section 4: Search by recipient
    // ---------------------------------------------------------------

    /**
     * Returns all sent messages for the given recipient number.
     *
     * @param recipient The cell number to search for
     * @return All matching messages, or a not-found message
     */
    public String searchByRecipient(String recipient) {
        String results = "";
        boolean found  = false;

        for (int i = 0; i < sentRecipients.size(); i++) {
            if (sentRecipients.get(i).equals(recipient)) {
                results = results + sentMessages.get(i) + "\n";
                found   = true;
            }
        }

        if (!found) {
            return "No messages found for recipient: " + recipient;
        }

        return results.trim();
    }

    // ---------------------------------------------------------------
    // Part 3 - Section 4: Delete by message hash
    // Static so Main.java can call Message.deleteByHash() directly
    // ---------------------------------------------------------------

    /**
     * Finds the message matching the given hash and removes it from all arrays.
     *
     * @param hash The hash of the message to delete
     * @return A success message with the deleted text, or a not-found message
     */
    public static String deleteByHash(String hash) {
        for (int i = 0; i < messageHashes.size(); i++) {
            if (messageHashes.get(i).equals(hash)) {
                String deletedText = "";

                if (i < sentMessages.size()) {
                    deletedText = sentMessages.get(i);
                    sentMessages.remove(i);
                    sentRecipients.remove(i);
                }

                messageHashes.remove(i);
                messageIDs.remove(i);

                return "Message: " + deletedText + " successfully deleted.";
            }
        }
        return "Hash not found.";
    }

    // ---------------------------------------------------------------
    // Part 3 - Section 6: Display full message report
    // ---------------------------------------------------------------

    /**
     * Builds and returns a formatted report of all sent messages.
     * Shows hash, recipient, and message text for each entry.
     *
     * @return A formatted report String
     */
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No sent messages to display.";
        }

        String report = "=== Message Report ===\n";

        for (int i = 0; i < sentMessages.size(); i++) {
            report = report + "------------------------------\n";
            report = report + "Hash:      " + messageHashes.get(i)  + "\n";
            report = report + "Recipient: " + sentRecipients.get(i) + "\n";
            report = report + "Message:   " + sentMessages.get(i)   + "\n";
        }

        report = report + "------------------------------\n";
        return report;
    }

    // ---------------------------------------------------------------
    // Part 3 - Helper: display all stored messages (sub-menu option 1)
    // ---------------------------------------------------------------

    /**
     * Returns all messages loaded from the JSON file as a formatted String.
     *
     * @return Formatted list of stored messages
     */
    public static String displayStoredMessages() {
        if (storedMessages.isEmpty()) {
            return "No stored messages found.";
        }

        String output = "=== Stored Messages ===\n";
        for (int i = 0; i < storedMessages.size(); i++) {
            output = output + (i + 1) + ". " + storedMessages.get(i) + "\n";
        }
        return output;
    }

    // ---------------------------------------------------------------
    // Part 3 - Getters and helpers used by unit tests
    // ---------------------------------------------------------------

    /** Returns the number of sent messages in this session. */
    public static int getSentCount() {
        return sentMessages.size();
    }

    /** Returns the number of disregarded messages in this session. */
    public static int getDisregardedCount() {
        return disregardedMessages.size();
    }

    /** Returns the sentMessages list. Used in unit tests. */
    public static ArrayList<String> getSentMessages() {
        return sentMessages;
    }

    /** Returns the storedMessages list. Used in unit tests. */
    public static ArrayList<String> getStoredMessages() {
        return storedMessages;
    }

    /** Clears all arrays. Used in unit tests to reset state between tests. */
    public static void clearAllArrays() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        sentRecipients.clear();
    }

    /**
     * Directly adds a message to storedMessages. Used in unit tests.
     *
     * @param text The message text to add
     */
    public static void addToStoredMessages(String text) {
        storedMessages.add(text);
    }
}