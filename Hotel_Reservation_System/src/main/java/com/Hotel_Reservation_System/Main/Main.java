package com.Hotel_Reservation_System.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.PreparedStatement;

public class Main {

    private static final String url = "jdbc:mysql://localhost:3306/hotelreservation";
    private static final String user = "root";
    private static final String password = "Aksh1234";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Drivers loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading JDBC driver: " + e.getMessage());
            return;
        }

        try (Connection con = DriverManager.getConnection(url, user, password);
             Scanner sc = new Scanner(System.in)) {

            while (true) {
                System.out.println("Hotel Management System");
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("6. Exit");
                System.out.print("Choose an Option: ");
                
                int key = getValidIntegerInput(sc);

                switch (key) {
                    case 1:
                        reserveRoom(con, sc);
                        break;
                    case 2:
                        viewReservation(con);
                        break;
                    case 3:
                        getRoomno(con, sc);
                        break;
                    case 4:
                        updateReservation(con, sc);
                        break;
                    case 5:
                        deleteReservation(con, sc);
                        break;
                    case 6:
                        Exit();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Application interrupted: " + e.getMessage());
        }
    }

    private static void reserveRoom(Connection con, Scanner sc) {
        System.out.print("Enter guest name: ");
        String guest_name = sc.next();

        System.out.print("Enter room number: ");
        int room_number = getValidIntegerInput(sc);

        System.out.print("Enter contact number: ");
        String contact_number = sc.next();

        String sql = "INSERT INTO reservation (guest_name, room_number, contact_number) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, guest_name);
            pstmt.setInt(2, room_number);
            pstmt.setString(3, contact_number);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Reservation successful");
            } else {
                System.out.println("Reservation failed. Try again...");
            }
        } catch (SQLException e) {
            System.err.println("Error reserving room: " + e.getMessage());
        }
    }

    private static void viewReservation(Connection con) {
        String sql = "SELECT r_id, guest_name, room_number, contact_number, r_date FROM reservation";
        try (PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("----------------------------------------------------------------------------------------------");
            System.out.println("Reservation ID    Guest    Room_Number	  Contact_Number	  Reservation_Date");
            System.out.println("----------------------------------------------------------------------------------------------");

            while (rs.next()) {
                int rId = rs.getInt("r_id");
                String guestName = rs.getString("guest_name");
                int roomNumber = rs.getInt("room_number");
                String contactNumber = rs.getString("contact_number");
                String rDate = rs.getString("r_date");

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n", rId, guestName, roomNumber, contactNumber, rDate);
            }
            System.out.println("----------------------------------------------------------------------------------------------");

        } catch (SQLException e) {
            System.err.println("Error viewing reservations: " + e.getMessage());
        }
    }

    private static void getRoomno(Connection con, Scanner sc) {
        System.out.print("Enter Reservation_ID: ");
        int r_id = getValidIntegerInput(sc);

        System.out.print("Enter Guest Nname: ");
        String guest_name = sc.next();

        String sql = "SELECT room_number FROM reservation WHERE r_id = ? AND guest_name = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, r_id);
            pstmt.setString(2, guest_name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int roomNO = rs.getInt("room_number");
                    System.out.println("Room Number for Reservation ID: " + r_id + " and Guest: " + guest_name + " is " + roomNO);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting room number: " + e.getMessage());
        }
    }

    private static void updateReservation(Connection con, Scanner sc) {
        System.out.print("Enter Reservation ID: ");
        int r_id = getValidIntegerInput(sc);

        if (!reservationExist(con, r_id)) {
            System.out.println("Reservation not found for this Id.");
            return;
        }

        System.out.print("Enter new Guest Name: ");
        String newguest_name = sc.next();

        System.out.print("Enter new Contact Number: ");
        String newcontact_number = sc.next();

        System.out.println("Reservation Updated Successfully");

        String sql = "UPDATE reservation SET guest_name = ?, contact_number = ? WHERE r_id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, newguest_name);
            pstmt.setString(2, newcontact_number);
            pstmt.setInt(3, r_id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Updated Successfully");
            } else {
                System.out.println("Updation failed");
            }
        } catch (SQLException e) {
            System.err.println("Error updating reservation: " + e.getMessage());
        }
    }

    private static void deleteReservation(Connection con, Scanner sc) {
        System.out.print("Enter Reservation Id to delete: ");
        int r_id = getValidIntegerInput(sc);

        if (!reservationExist(con, r_id)) {
            System.out.println("Reservation Id not found.");
            return;
        }

        String sql = "DELETE FROM reservation WHERE r_id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, r_id);

            int affectedrows = pstmt.executeUpdate();
            if (affectedrows > 0) {
                System.out.println("Reservation deleted successfully.");
            } else {
                System.out.println("Deletion failed.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
        }
    }

    private static boolean reservationExist(Connection con, int r_id) {
        String sql = "SELECT r_id FROM reservation WHERE r_id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, r_id);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking reservation existence: " + e.getMessage());
            return false;
        }
    }

    private static int getValidIntegerInput(Scanner sc) {
        while (true) {
            try {
                return sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                sc.next(); // Clear invalid input
            }
        }
    }

    private static void Exit() throws InterruptedException {
        System.out.print("Application closing");
        int i = 5;
        while (i != 0) {
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("Application closed");
    }
}