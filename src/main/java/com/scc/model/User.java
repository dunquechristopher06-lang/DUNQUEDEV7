package com.scc.model;

/**
 * Immutable user row from the "users" table.
 * The database column is named "roles"; it is exposed here as role().
 */
public record User(int id, String username, String password, String role, String status) {
}