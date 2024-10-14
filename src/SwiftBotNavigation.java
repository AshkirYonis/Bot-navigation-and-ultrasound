import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.awt.image.BufferedImage;
import swiftbot.Button;
import swiftbot.SwiftBotAPI;
import java.util.ArrayList;
import java.util.List;

public class SwiftBotNavigation {
    // Create an instance of SwiftBotAPI
    private static SwiftBotAPI API = new SwiftBotAPI();
    // List to store the commands executed by the SwiftBot
    private static List<String> commandLog = new ArrayList<>();
    // Variable to track if the program has started
    private static boolean programStarted = false;

    public static void main(String[] args) {
        // Print welcome message and instructions
        System.out.println("Welcome to SwiftBot navigation system.");
        System.out.println("Press 'Y' on the SwiftBot to start the program.");

        // Enable Button.Y to start the program
        API.enableButton(Button.Y, () -> startProgram());
        // Enable Button.X to terminate the program
        API.enableButton(Button.X, () -> terminateProgram());

        // Wait for input from the console
        try {
            while (true) {
                char input = (char) System.in.read();
                if (input == 'X' || input == 'x') {
                    // Terminate the program if 'X' is pressed on the console
                    terminateProgram();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to start the program
    private static void startProgram() {
        if (!programStarted) {
            programStarted = true;
            System.out.println("Program started.");
            runProgram();
        }
    }

    // Method to run the main program loop
    private static void runProgram() {
        // Print instructions
        System.out.println("Press 'A' to scan QR code, 'B' to use ultrasound, 'X' to terminate.");
        // Enable Button.A to scan QR code
        API.enableButton(Button.A, () -> scanQRCode());
        // Enable Button.B to detect objects using ultrasound
        API.enableButton(Button.B, () -> detectObjects());
    }

    // Method to scan QR code
    private static void scanQRCode() {
        try {
            BufferedImage img = API.getQRImage();
            if (img != null) {
                String decodedText = API.decodeQRImage(img);
                processQRCode(decodedText);
                commandLog.add(decodedText);
            } else {
                // Print message if no QR code is detected
                System.out.println("No QR code detected.");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    // Method to detect objects using ultrasound
    private static void detectObjects() {
        try {
            double distanceToObject = measureDistance();
            // Print the distance to the object in front
            System.out.println("Distance to object in front: " + distanceToObject + " cm");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    // Method to measure distance using ultrasound
    private static double measureDistance() {
        // Take 6 ultrasound measurements
        double totalDistance = 0;
        for (int i = 0; i < 6; i++) {
            totalDistance += API.useUltrasound();
        }
        // Calculate the mean distance
        return totalDistance / 6.0;
    }

    // Method to process QR code commands
    private static void processQRCode(String decodedText) {
        String[] parts = decodedText.split(" ");
        String action = parts[0];
        switch(action) {
            case "F":
                int durationF = Integer.parseInt(parts[1]);
                int speedF = Integer.parseInt(parts[2]);
                moveForward(durationF, speedF);
                break;
            case "B":
                int durationB = Integer.parseInt(parts[1]);
                int speedB = Integer.parseInt(parts[2]);
                moveBackward(durationB, speedB);
                break;
            case "R":
                int durationR = Integer.parseInt(parts[1]);
                int speedR = Integer.parseInt(parts[2]);
                turnRight(durationR, speedR);
                break;
            case "L":
                int durationL = Integer.parseInt(parts[1]);
                int speedL = Integer.parseInt(parts[2]);
                turnLeft(durationL, speedL);
                break;
            case "T":
                int count = Integer.parseInt(parts[1]);
                retraceMovements(count);
                break;
            case "W":
                writeCommandLogToFile();
                break;
            default:
                // Print message for invalid command
                System.out.println("Invalid command: " + decodedText);
        }
    }

    // Method to move forward
    private static void moveForward(int duration, int speed) {
        try {
            API.move(speed, speed, duration * 1000);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    // Method to move backward
    private static void moveBackward(int duration, int speed) {
        try {
            API.move(-speed, -speed, duration * 1000);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    // Method to turn right
    private static void turnRight(int duration, int speed) {
        try {
            API.move(speed, -speed, duration * 1000);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    // Method to turn left
    private static void turnLeft(int duration, int speed) {
        try {
            API.move(-speed, speed, duration * 1000);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    // Method to retrace past movements
    private static void retraceMovements(int count) {
        // Ensure count does not exceed the number of recorded movements
        int numMovements = commandLog.size();
        if (count > numMovements) {
            System.out.println("Cannot retrace " + count + " movements, exceeds the recorded movements.");
            return;
        }

        // Determine the starting index for retracing
        int startIndex = numMovements - count;

        // Iterate over the last 'count' movements and execute them
        for (int i = startIndex; i < numMovements; i++) {
            String command = commandLog.get(i);
            processQRCode(command);
        }
    }

    // Method to write command log to a file
    private static void writeCommandLogToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("command_log.txt"))) {
            for (String command : commandLog) {
                writer.println(command);
            }
            // Print message after writing command log to file successfully
            System.out.println("Command log written to file successfully.");
        } catch (IOException e) {
            // Print error message if writing to file fails
            System.err.println("Error writing command log to file: " + e.getMessage());
        }
    }

    // Method to terminate the program
    private static void terminateProgram() {
        // Print termination message
        System.out.println("Program terminated.");
        // Exit the program
        System.exit(0);
    }
}

