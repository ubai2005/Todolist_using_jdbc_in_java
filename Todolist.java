package jdbc.drive;
import java.sql.*;
import java.util.Scanner;

public class Todolist {

    public static void main(String[] args) {
        Connection con = null;
        Scanner sc = new Scanner(System.in);

        try {
            
            Class.forName("com.mysql.cj.jdbc.Driver");  

            
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/todolist1", "root", "");

            int choice;

            do {
                System.out.println("\n--- To-Do Menu ---");
                System.out.println("1. Add Task");
                System.out.println("2. View Tasks");
                System.out.println("3. Mark Task Done");
                System.out.println("4. Delete Task");
                System.out.println("5. Exit");
                System.out.print("Choice: ");
                choice = sc.nextInt();
                sc.nextLine();  

                switch (choice) {
                    case 1:
                        System.out.print("Enter task: ");
                        String desc = sc.nextLine();
                        try (PreparedStatement pst = con.prepareStatement(
                                "INSERT INTO todolist1(description, status) VALUES (?, 'pending')")) {
                            pst.setString(1, desc);
                            pst.executeUpdate();
                            System.out.println("Task Added!");
                        }
                        break;

                    case 2:
                        try (Statement st = con.createStatement();
                             ResultSet rs = st.executeQuery("SELECT * FROM todolist1")) {
                            while (rs.next()) {
                                System.out.println(rs.getInt("id") + ". [" +
                                        rs.getString("status") + "] " +
                                        rs.getString("description"));
                            }
                        }
                        break;

                    case 3:
                        System.out.print("Enter task ID: ");
                        int doneId = sc.nextInt();
                        try (PreparedStatement pst = con.prepareStatement(
                                "UPDATE todolist1 SET status='done' WHERE id=?")) {
                            pst.setInt(1, doneId);
                            int rows = pst.executeUpdate();
                            if (rows > 0) System.out.println("Marked Done!");
                            else System.out.println("ID Not Found.");
                        }
                        break;

                    case 4:
                        System.out.print("Enter task ID: ");
                        int delId = sc.nextInt();
                        try (PreparedStatement pst = con.prepareStatement(
                                "DELETE FROM todolist1 WHERE id=?")) {
                            pst.setInt(1, delId);
                            int rows = pst.executeUpdate();
                            if (rows > 0) System.out.println("Task Deleted!");
                            else System.out.println("ID Not Found.");
                        }
                        break;

                    case 5:
                        System.out.println("Exit...");
                        break;

                    default:
                        System.out.println("Invalid Choice!");
                }
            } while (choice != 5);

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found — check driver class name.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                // ignore
            }
            sc.close();
        }
    }
}
