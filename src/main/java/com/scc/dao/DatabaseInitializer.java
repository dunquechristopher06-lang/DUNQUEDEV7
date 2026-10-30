package com.scc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates the tables (with foreign keys) and seeds sample accounts.
 * Called once from Main before anything else runs.
 */
public class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    public static void initialize() {
        String usersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id       INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    roles    TEXT NOT NULL,
                    status   TEXT NOT NULL DEFAULT 'PENDING'
                )
                """;

        String equipmentTable = """
                CREATE TABLE IF NOT EXISTS equipment (
                    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
                    equipment_name      TEXT NOT NULL,
                    category            TEXT,
                    quantity            INTEGER NOT NULL DEFAULT 1,
                    availability_status TEXT NOT NULL DEFAULT 'AVAILABLE'
                )
                """;

        String transactionsTable = """
                CREATE TABLE IF NOT EXISTS borrowing_transactions (
                    id                   INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id              INTEGER NOT NULL,
                    equipment_id         INTEGER NOT NULL,
                    borrow_date          TEXT NOT NULL,
                    expected_return_date TEXT,
                    actual_return_date   TEXT,
                    quantity             INTEGER NOT NULL DEFAULT 1,
                    status               TEXT NOT NULL DEFAULT 'PENDING',
                    FOREIGN KEY (user_id)      REFERENCES users(id),
                    FOREIGN KEY (equipment_id) REFERENCES equipment(id)
                )
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {

            st.execute(usersTable);
            st.execute(equipmentTable);
            st.execute(transactionsTable);

            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    seedUsers(conn);
                }
            }
        } catch (SQLException e) {
            System.out.println("Database initialization error: " + e.getMessage());
        }
    }

    private static void seedUsers(Connection conn) throws SQLException {
        String sql = "INSERT INTO users (username, password, roles, status) VALUES (?, ?, ?, ?)";
        String[][] rows = {
                {"admin",    "admin123", "ADMIN",   "APPROVED"},
                {"teacher1", "teach123", "TEACHER", "APPROVED"},
                {"teacher2", "teach456", "TEACHER", "PENDING"},
                {"student1", "stud123",  "STUDENT", "PENDING"},
                {"student2", "stud456",  "STUDENT", "PENDING"},
                {"student3", "stud789",  "STUDENT", "APPROVED"}
        };
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String[] r : rows) {
                ps.setString(1, r[0]);
                ps.setString(2, r[1]);
                ps.setString(3, r[2]);
                ps.setString(4, r[3]);
                ps.executeUpdate();
            }
        }
    }
}