package com.scc.ui;

import com.scc.model.User;

import java.util.Scanner;

public class TeacherMenu {

    private final Scanner scanner;
    private final User currentUser;

    public TeacherMenu(Scanner scanner, User currentUser) {
        this.scanner = scanner;
        this.currentUser = currentUser;
    }

    public void show() {
        while (true) {
            System.out.println("\n===== TEACHER MENU =====");
            System.out.println("Logged in as: " + currentUser.username());
            System.out.println("1. (Coming soon) Borrow equipment");
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