package com.example.file_copy_2;

import java.io.File;
import java.sql.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubfolderScanner {
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

    public void insertSubfolder(Connection conn1, String subfolderName, String folderId, String information, int fileCount) {
        String sql1 = "SELECT COUNT(SubfolderName) FROM Subfolders WHERE SubfolderName = ? AND FolderID = ?";
        try (PreparedStatement pstmt1 = conn1.prepareStatement(sql1)) {
            pstmt1.setString(1, subfolderName);
            pstmt1.setString(2, folderId);
            ResultSet rd = pstmt1.executeQuery();

            if (rd.getInt(1) == 0){
                String sql = "INSERT INTO Subfolders(SubfolderName, FolderID, Information, FileCount) VALUES(?,?,?,?);";
                PreparedStatement pstmt = conn1.prepareStatement(sql);
                pstmt.setString(1, subfolderName);
                pstmt.setString(2, folderId);

                pstmt.setString(3, information);
                pstmt.setInt(4, fileCount);
                pstmt.executeUpdate();
            }else{
                String sql = "UPDATE Subfolders SET Information = ?, FileCount =? WHERE SubfolderName = ?;";
                PreparedStatement pstmt = conn1.prepareStatement(sql);

                pstmt.setString(1, information);
                pstmt.setInt(2, fileCount);

                pstmt.setString(3, subfolderName);

                pstmt.executeUpdate();

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void final_scan() throws SQLException {
        String query = "SELECT DirName FROM Directories WHERE DirID = 1"; // Adjust SQL according to your schema
        try (Connection conn = this.connect()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String a = rs.getString("DirName");
                SubfolderScanner temp = new SubfolderScanner();
                temp.scanDirectory(conn, a);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void scanDirectory(Connection conn, String directoryPath) {
        File directory = new File(directoryPath);
        File[] subfolders = directory.listFiles(File::isDirectory);
        String folderId = directory.getAbsolutePath();

        if (subfolders != null) {
            Arrays.sort(subfolders, (o1, o2) -> {
                Pattern pattern = Pattern.compile("\\d+");
                Matcher m1 = pattern.matcher(o1.getName());
                Matcher m2 = pattern.matcher(o2.getName());
                if (!m1.find() || !m2.find()) {
                    return o1.getName().compareTo(o2.getName());
                }
                return Integer.compare(Integer.parseInt(m1.group()), Integer.parseInt(m2.group()));
            });


            try(Statement stmt = conn.createStatement()){

                for (File subfolder : subfolders) {
                    String information = Objects.requireNonNull(subfolder.list()).length > 1 ? "Not Empty" : "Empty";
                    int fileCount = Objects.requireNonNull(subfolder.list()).length;

                    insertSubfolder(conn, subfolder.getName(), folderId, information, fileCount);
                }
                }catch (SQLException e) {
                System.out.println(e.getMessage());
            }

        }
    }

    public static void main(String[] args) throws SQLException {
        SubfolderScanner app = new SubfolderScanner();
        app.final_scan();
    }
}
