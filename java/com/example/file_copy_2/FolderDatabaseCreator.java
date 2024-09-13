package com.example.file_copy_2;

import java.sql.*;

public class FolderDatabaseCreator {

    public static Connection connect(String filename) {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:" + filename;
            conn = DriverManager.getConnection(url);

            String createSubfoldersTable = """
                    CREATE TABLE IF NOT EXISTS Subfolders (
                        SubfolderID INTEGER PRIMARY KEY AUTOINCREMENT,
                        SubfolderName VARCHAR(255),
                        FolderID VARCHAR(255),
                        Information VARCHAR(255),
                        FileCount INT
                    );
                    """;
            String createVideosTable = """
                    CREATE TABLE IF NOT EXISTS Videos (
                        VideoID INTEGER PRIMARY KEY,
                        VideoName VARCHAR(255),
                        DirName VARCHAR(255),
                        Copied VARCHAR(3),
                        CopiedTo VARCHAR(255),
                        CopyTimestamp TIME,
                        VideoDate DATE,
                        VideoTime TIME
                    );
                    """;
            String createPhotosTable = """
                    CREATE TABLE IF NOT EXISTS Photos (
                        PhotoID INTEGER PRIMARY KEY,
                        PhotoName VARCHAR(255),
                        DirName VARCHAR(255),
                        Copied VARCHAR(3),
                        CopiedTo VARCHAR(255),
                        CopyTimestamp TIME,
                        PhotoDate DATE,
                        PhotoTime TIME
                    );
                    """;
            String createDirectoriesTable = """
                    CREATE TABLE IF NOT EXISTS Directories (
                        DirID INTEGER,
                        DirName VARCHAR(255)
                    );
                    """;
            String createLastProcessedTable = """
                    CREATE TABLE IF NOT EXISTS LastProcessed (
                        id INTEGER PRIMARY KEY,
                        type TEXT NOT NULL,
                        last_processed TIMESTAMP NOT NULL
                    );
                    """;

            Statement stmt = conn.createStatement();
            stmt.execute(createSubfoldersTable);
            stmt.execute(createVideosTable);
            stmt.execute(createPhotosTable);
            stmt.execute(createDirectoriesTable);
            stmt.execute(createLastProcessedTable);

//            String insertInitialValues = """
//                    INSERT INTO LastProcessed (type, last_processed) VALUES
//                    ('photo', '1970-01-01 00:00:00')
//                    ON CONFLICT(type) DO NOTHING;
//                    INSERT INTO LastProcessed (type, last_processed) VALUES
//                    ('video', '1970-01-01 00:00:00');
//                    """;
//            stmt.execute(insertInitialValues);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return conn;
    }

    private Connection connectToDatabase() {
        String url = "jdbc:sqlite:database.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public boolean exists(int id, String name) throws SQLException {
        String query = "SELECT COUNT(*) FROM Directories WHERE DirName = ? AND DirID = ?";
        try (Connection conn = connectToDatabase();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public void insertDirectory(int id, String name) throws SQLException {
        if (!exists(id, name)) {
            String insert = "INSERT INTO Directories (DirID, DirName) VALUES (?, ?)";
            try (Connection conn = connectToDatabase();
                 PreparedStatement pstmt = conn.prepareStatement(insert)) {
                pstmt.setInt(1, id);
                pstmt.setString(2, name);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Directory with this ID already exists.");
        }
    }

    public void set_to_dirs(int id, String name) throws SQLException {
        insertDirectory(id, name);
    }

    public static void main(String[] args) {
        connect("database.db");
    }
}
