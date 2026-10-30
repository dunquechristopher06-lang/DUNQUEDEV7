package com.scc.ui;

import com.scc.model.User;

import java.util.Scanner;

public class StudentMenu {

    private final Scanner scanner;
    private final User currentUser;

    public StudentMenu(Scanner scanner, User currentUser) {
        this.scanner = scanner;
        this.currentUser = currentUser;
    }

    public void show() {
        while (true) {
            System.out.println("\n===== STUDENT MENU =====");
            System.out.println("Logged in as: " + currentUser.username());
            System.out.println("1. (Coming soon) Browse equipment");
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();
            if (input.equals("0")) {
                System.out.println("Logging out...");
                return;
            }
            System.out.println("This feature is not available yet.");
        }
    }
}