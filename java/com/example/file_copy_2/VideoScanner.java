package com.example.file_copy_2;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.stream.Stream;

public class VideoScanner {
    public void final_scan() {
        String query = "SELECT DirName FROM Directories WHERE DirID = 4"; // Adjust SQL according to your schema
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String a = rs.getString("DirName");
                VideoScanner temp = new VideoScanner();
                temp.scanDirectoryForVideo(conn, a);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private Connection connect() {
        String url = "jdbc:sqlite:database.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void insertVideo(Connection conn1, String videoName, String path, boolean copied, String videoDate, String videoTime) throws SQLException {
        String sql = "INSERT INTO Videos(VideoName, DirName, Copied, VideoDate, VideoTime) VALUES(?,?,?,?,?);";
        String sql1 = "SELECT COUNT(VideoName) FROM (SELECT VideoName FROM videos WHERE VideoName = ? AND VideoTime = ? LIMIT 100)";

        try (PreparedStatement pstmt1 = conn1.prepareStatement(sql1)) {
            pstmt1.setString(1, videoName);
            pstmt1.setString(2, videoTime);
            ResultSet rd = pstmt1.executeQuery();
            if(rd.getInt(1) == 0) {
                PreparedStatement pstmt = conn1.prepareStatement(sql);
                pstmt.setString(1, videoName);
                pstmt.setString(2, path);

                pstmt.setBoolean(3, copied);
                pstmt.setString(4, videoDate);
                pstmt.setString(5, videoTime);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void scanDirectoryForVideo(Connection conn, String directoryPath) throws SQLException {
        File rootDirectory = new File(directoryPath);
        scanDirectory(conn, rootDirectory);
    }

    // Recursive method to scan directories for video files
    private void scanDirectory(Connection conn, File directory) throws SQLException {
        if (directory.isDirectory()) {
            // Define a filter to identify video files based on their extension
            FilenameFilter videoFilter = (dir, name) -> name.toLowerCase().endsWith(".mp4");

            // Process all video files in the current directory
            File[] files = directory.listFiles(videoFilter);
            if (files != null) {

                Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

                // Limit to the 15 most recently modified files
                File[] recentFiles = Arrays.copyOfRange(files, 0, Math.min(15, files.length));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

                for (File file : recentFiles) {
                    String videoDate = dateFormat.format(file.lastModified());
                    String videoTime = timeFormat.format(file.lastModified());
                    insertVideo(conn, file.getName(), directory.getPath(), false, videoDate, videoTime); // Assuming not copied initially
                }
            }

            // Recursively process all subdirectories
            File[] subdirectories = Stream.of(directory.listFiles(File::isDirectory))
                    .sorted((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()))
                    .limit(2).toArray(File[]::new);
            for (File subdir : subdirectories) {
                scanDirectory(conn, subdir);
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        VideoScanner app = new VideoScanner();
        app.final_scan();
    }
}
