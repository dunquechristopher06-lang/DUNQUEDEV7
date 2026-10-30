package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.dao.UserDao;
import com.scc.ui.Authentication;

import java.util.Scanner;

/** Single entry point of the application. */
public class Main {

    public static void main(String[] args) {
        // 1. Make sure the database, tables and foreign keys are ready.
        DatabaseInitializer.initialize();

        // 2. Hand control to Authentication, which routes to the right menu.
        try (Scanner scanner = new Scanner(System.in)) {
            new Authentication(scanner, new UserDao()).start();
        }
    }
}