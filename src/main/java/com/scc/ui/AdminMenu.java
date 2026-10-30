package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class AdminMenu {

    private static final Set<String> VALID_ROLES = Set.of("STUDENT", "TEACHER", "ADMIN");
    private static final Set<String> VALID_STATUSES = Set.of("APPROVED", "PENDING");

    private final Scanner scanner;
    private final User currentAdmin;
    private final UserDao userDao;

    public AdminMenu(Scanner scanner, User currentAdmin, UserDao userDao) {
        this.scanner = scanner;
        this.currentAdmin = currentAdmin;
        this.userDao = userDao;
    }

    public void show() {
        while (true) {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("Logged in as: " + currentAdmin.username());
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Update User Role and Status");
            System.out.println("0. Logout");

            int choice = readIntInput("Choose an option: ");

            switch (choice) {
                case 1 -> viewAllUsers();
                case 2 -> viewPendingUsers();
                case 3 -> approveUserAccount();
                case 4 -> updateUser();
                case 0 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid option. Please choose 0 to 4.");
            }
        }
    }

    // ---------- Formatted table helper ----------

    /** Prints any list of users as an aligned table with borders. */
    private void printUserTable(String title, List<User> users) {
        String border = "+-------+-----------------+--------------+------------+";

        System.out.println("\n=== " + title + " ===");
        System.out.println(border);
        System.out.printf("| %-5s | %-15s | %-12s | %-10s |%n", "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println(border);

        if (users.isEmpty()) {
            System.out.printf("| %-47s |%n", "No records found.");
        } else {
            for (User u : users) {
                System.out.printf("| %-5d | %-15s | %-12s | %-10s |%n",
                        u.id(), u.username(), u.role(), u.status());
            }
        }
        System.out.println(border);
    }

    // ---------- Input validation wrappers ----------

    /** Keeps asking until a whole number is typed. Letters/symbols never crash the app. */
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

    /** Returns a valid role, or null if the admin enters 0 to cancel. */
    private String readValidRole() {
        while (true) {
            System.out.print("Enter new role (STUDENT, TEACHER, ADMIN) or 0 to cancel: ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("0")) {
                return null;
            }
            if (VALID_ROLES.contains(input)) {
                return input;
            }
            System.out.println("Invalid role. Choose STUDENT, TEACHER, or ADMIN.");
        }
    }

    /** Returns a valid status, or null if the admin enters 0 to cancel. */
    private String readValidStatus() {
        while (true) {
            System.out.print("Enter new status (APPROVED, PENDING) or 0 to cancel: ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("0")) {
                return null;
            }
            if (VALID_STATUSES.contains(input)) {
                return input;
            }
            System.out.println("Invalid status. Choose APPROVED or PENDING.");
        }
    }

    // ---------- Features ----------

    private void viewAllUsers() {
        printUserTable("ALL SYSTEM USERS", userDao.getAllUsers());
    }

    private void viewPendingUsers() {
        printUserTable("PENDING REGISTRATIONS", userDao.getPendingUsers());
    }

    private void approveUserAccount() {
        List<User> pending = userDao.getPendingUsers();
        printUserTable("PENDING REGISTRATIONS", pending);

        if (pending.isEmpty()) {
            System.out.println("There are no accounts waiting for approval.");
            return;
        }

        while (true) {
            int targetUserId = readIntInput("\nEnter User ID to approve (0 to cancel): ");

            if (targetUserId == 0) {
                System.out.println("Approval cancelled.");
                return;
            }

            boolean inPendingList = pending.stream().anyMatch(u -> u.id() == targetUserId);
            if (!inPendingList) {
                System.out.println("ID " + targetUserId + " is not in the pending list. Try again.");
                continue;
            }

            if (userDao.approveUser(targetUserId)) {
                System.out.println("SUCCESS: User ID " + targetUserId + " has been approved.");
            } else {
                System.out.println("Approval failed. Please try again.");
            }
            return;
        }
    }

    private void updateUser() {
        List<User> users = userDao.getAllUsers();
        printUserTable("ALL SYSTEM USERS", users);

        int targetUserId = readIntInput("\nEnter User ID to update (0 to cancel): ");
        if (targetUserId == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        if (targetUserId == currentAdmin.id()) {
            System.out.println("WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        boolean exists = users.stream().anyMatch(u -> u.id() == targetUserId);
        if (!exists) {
            System.out.println("No user found with ID " + targetUserId + ".");
            return;
        }

        String newRole = readValidRole();
        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        String newStatus = readValidStatus();
        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        if (userDao.updateUserRoleAndStatus(targetUserId, newRole, newStatus)) {
            System.out.println("SUCCESS: User " + targetUserId + " updated to role "
                    + newRole + " and status " + newStatus + ".");
        } else {
            System.out.println("Update failed.");
        }
    }
}