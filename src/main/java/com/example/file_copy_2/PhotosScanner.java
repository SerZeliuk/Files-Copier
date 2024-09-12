package com.example.file_copy_2;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.stream.Stream;

public class PhotosScanner {

    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:database.db"; // Adjust for your database
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    public void final_scan() {
        String query = "SELECT DirName FROM Directories WHERE DirID = 3"; // Adjust SQL according to your schema
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String a = rs.getString("DirName");
                PhotosScanner temp = new PhotosScanner();
                temp.scanDirectoryForPhotos(conn, a);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void insertPhoto(Connection conn1, String photoName, String path, boolean copied, String photoDate, String photoTime) throws SQLException {
        String sql = "INSERT INTO Photos(PhotoName, DirName, Copied, PhotoDate, PhotoTime) VALUES(?,?,?,?,?);";
        String sql1 = "SELECT COUNT(*) FROM (SELECT PhotoName FROM Photos WHERE PhotoName = ? AND PhotoTime = ? AND PhotoDate = ? LIMIT 1000)";
        try (PreparedStatement pstmt1 = conn1.prepareStatement(sql1)) {
            pstmt1.setString(1, photoName);
            pstmt1.setString(2, photoTime);
            pstmt1.setString(3, photoDate);

            ResultSet rd = pstmt1.executeQuery();
            if(rd.getInt(1) == 0) {
                PreparedStatement pstmt = conn1.prepareStatement(sql);
                pstmt.setString(1, photoName);
                pstmt.setString(2, path);
                pstmt.setBoolean(3, copied);
                pstmt.setString(4, photoDate);
                pstmt.setString(5, photoTime);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Extensions filter
//    private static final String[] extensions = {".jpg", ".jpeg", };

    // Method to scan a directory and its subdirectories
    public void scanDirectoryForPhotos(Connection conn, String directoryPath) throws SQLException {
        File rootDirectory = new File(directoryPath);
        scanDirectory(conn, rootDirectory);
    }

    // Recursive method to scan directories for photos
    private void scanDirectory(Connection conn, File directory) throws SQLException {
        if (directory.isDirectory()) {
            // Define a filter to identify photo files based on their extensions
            FilenameFilter photoFilter = (dir, name) -> name.endsWith(".jpg");

            // Process all photo files in the current directory
            File[] files = directory.listFiles(photoFilter);
            if (files != null) {
                Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
                // Limit to the 15 most recently modified files
                File[] recentFiles = Arrays.copyOfRange(files, 0, Math.min(300, files.length));

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

                for (File file : recentFiles) {
                    String photoDate = dateFormat.format(file.lastModified());
                    String photoTime = timeFormat.format(file.lastModified());
                    insertPhoto(conn, file.getName(), directory.getPath(), false, photoDate, photoTime); // Assuming not copied initially
                }
            }
            File[] subdirectories = Stream.of(directory.listFiles(File::isDirectory))
                    .sorted((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()))
                    .limit(2).toArray(File[]::new);

            for (File subdir : subdirectories) {
                scanDirectory(conn, subdir);
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        PhotosScanner app = new PhotosScanner();
        app.final_scan();
    }
}
