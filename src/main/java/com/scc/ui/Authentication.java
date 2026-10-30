package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Optional;
import java.util.Scanner;

/**
 * Shows the start menu (Log in / Register / Exit), handles login for every role
 * and routes the user to the correct menu. Main only calls start().
 */
public class Authentication {

    private final Scanner scanner;
    private final UserDao userDao;

    public Authentication(Scanner scanner, UserDao userDao) {
        this.scanner = scanner;
        this.userDao = userDao;
    }

    public void start() {
        System.out.println("=== EQUIPMENT BORROWING & TRACKING SYSTEM ===");

        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Log in");
            System.out.println("2. Register");
            System.out.println("3. Exit");

            int choice = readIntInput("Choose an option: ");

            switch (choice) {
                case 1 -> login();
                case 2 -> register();
                case 3 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Please choose 1, 2 or 3.");
            }
        }
    }

    // ---------- Log in ----------

    private void login() {
        System.out.println("\n--- LOG IN ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        Optional<User> result = userDao.authenticate(username, password);

        if (result.isEmpty()) {
            System.out.println("Invalid username or password.");
            return;
        }

        User user = result.get();

        if (!"APPROVED".equalsIgnoreCase(user.status())) {
            System.out.println("Your account is " + user.status()
                    + ". Please wait for admin approval.");
            return;
        }

        System.out.println("Welcome, " + user.username() + " (" + user.role() + ")");
        routeToMenu(user);
    }

    private void routeToMenu(User user) {
        switch (user.role().toUpperCase()) {
            case "ADMIN" -> new AdminMenu(scanner, user, userDao).show();
            case "TEACHER" -> new TeacherMenu(scanner, user).show();
            case "STUDENT" -> new StudentMenu(scanner, user).show();
            default -> System.out.println("Unknown role: " + user.role());
        }
    }

    // ---------- Register ----------

    private void register() {
        System.out.println("\n--- REGISTER (enter 0 at any prompt to cancel) ---");

        String username;
        while (true) {
            System.out.print("Choose a username: ");
            username = scanner.nextLine().trim();

            if (username.equals("0")) {
                System.out.println("Registration cancelled.");
                return;
            }
            if (username.isEmpty() || username.contains(" ")) {
                System.out.println("Username cannot be empty or contain spaces.");
            } else if (userDao.usernameExists(username)) {
                System.out.println("That username is already taken. Try another one.");
            } else {
                break;
            }
        }

        String password;
        while (true) {
            System.out.print("Choose a password: ");
            password = scanner.nextLine().trim();

            if (password.equals("0")) {
                System.out.println("Registration cancelled.");
                return;
            }
            if (password.isEmpty()) {
                System.out.println("Password cannot be empty.");
            } else {
                break;
            }
        }

        String role = null;
        while (role == null) {
            System.out.println("Register as:");
            System.out.println("1. Student");
            System.out.println("2. Teacher");
            int choice = readIntInput("Choose a role (0 to cancel): ");

            switch (choice) {
                case 0 -> {
                    System.out.println("Registration cancelled.");
                    return;
                }
                case 1 -> role = "STUDENT";
                case 2 -> role = "TEACHER";
                default -> System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        }

        if (userDao.registerUser(username, password, role)) {
            System.out.println("SUCCESS: Account created as " + role
                    + ". Your account is PENDING until an admin approves it.");
        } else {
            System.out.println("Registration failed. Please try again.");
        }
    }

    // ---------- Input helper ----------

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}