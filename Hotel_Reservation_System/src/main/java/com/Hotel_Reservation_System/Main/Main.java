package com.Hotel_Reservation_System.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Statement;

public class Main {

	private static final String url="jdbc:mysql://localhost:3306/hotelreservation";
	private static final String user="root";
	private static final String password="Aksh1234";
	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Drivers loaded successfully");
		}catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		
		try {
			Connection con=DriverManager.getConnection(url,user,password);
			System.out.println();
			Statement s=con.createStatement();
						
			while(true) {
				System.out.println("Hotel Management System");
				Scanner sc=new Scanner(System.in);
				System.out.println("1. Reserve a room");
				System.out.println("2. View Reservation");
				System.out.println("3. Get Room Number");
				System.out.println("4. Update reservation");
				System.out.println("5. Delete Reservation");
				System.out.println("6. Exit");
				System.out.print("Choose an Option:");
				int key=sc.nextInt();
				
				switch (key) {
				case 1:reserveRoom(con, sc, s);				
					break;
				case 2:viewReservation(con,s);
					break;
				case 3:getRoomno(con, sc,s);
					break;
				case 4:updateReservation(con, sc,s);
					break;
				case 5:deleteReservation(con, sc,s);
					break;
				case 6:Exit();
					sc.close();
					
					return;
					
				default:System.out.println("Invalid choice. Try again.");
					
				}
			}
		}catch (SQLException e) {
			System.out.println(e.getMessage());
		}catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

	}
	
	private static void reserveRoom(Connection con, Scanner sc, Statement s) throws SQLException{
		
		System.out.print("Enter guest name: ");
		String guest_name=sc.next();
		
		System.out.print("Enter room number: " );
		int room_number=sc.nextInt();
		
		System.out.print("Enter contact number: " );
		String contact_number=sc.next();
		
		String sql="insert into reservation (guest_name,room_number,contact_number)"+
				"values('"+guest_name+"','"+room_number+"','"+contact_number+"')";
		try {
			int affectedRows =s.executeUpdate(sql);
			
			if(affectedRows>0)System.out.println("Reservation successful");
			else System.out.println("Reservation failed. Try again...");
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void viewReservation(Connection con,Statement s)throws SQLException {
		String sql="Select r_id,guest_name,room_number,contact_number,r_date from reservation";
		try(ResultSet rs=s.executeQuery(sql)) {
			System.out.println("----------------------------------------------------------------------------------------------");
			System.out.println("Reservation ID    Guest    Room_Number	  Contact_Number	  Reservation_Date");
			System.out.println("----------------------------------------------------------------------------------------------");
			
			while(rs.next()) {
				int rId=rs.getInt("r_id");
				String guestName=rs.getString("guest_name");
				int roomNumber=rs.getInt("room_number");
				String contactNumber=rs.getString("contact_number");
				String rDate=rs.getString("r_date");
				
				System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        rId, guestName, roomNumber, contactNumber, rDate);
				
			}
			System.out.println("----------------------------------------------------------------------------------------------");	

		}catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private static void getRoomno(Connection con,Scanner sc,Statement s) throws SQLException {
		System.out.print("Enter Reservation_ID: ");
		int r_id=sc.nextInt();
		System.out.print("Enter Guest Nname: ");
		String guest_name=sc.next();
		
		String sql="SELECT room_number FROM reservation WHERE r_id = " + r_id + " AND guest_name = '" + guest_name + "'";
		
		try{
			ResultSet rs=s.executeQuery(sql);
			if(rs.next()) {
				int roomNO=rs.getInt("room_number");
				System.out.println("Room Number for Reservation ID: "+r_id+" and Guest: "+guest_name+" is "+roomNO);
			}
			else {
                System.out.println("Reservation not found for the given ID and guest name.");
            }
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void updateReservation(Connection con, Scanner sc,Statement s) {
		System.out.print("Enter Reservation ID ");
		int r_id=sc.nextInt();
		sc.nextLine();
		
		if(!reservationExist(con,r_id, s)) {
			System.out.println("Reservation not found for this Id.");
			return;
		}
		
		System.out.print("Enter new Guest Name: ");
		String newguest_name=sc.next();
		System.out.print("Enter new Contact Number: ");
		String newcontact_number=sc.next();
		System.out.println("Reservation Updated Successfully");
		
		String sql="Update reservation SET guest_name= '"+newguest_name+"', contact_number='"+newcontact_number+"' where r_id="+r_id ;
		
		try{
			int affectedRows=s.executeUpdate(sql);
			
			if(affectedRows>0)System.out.println("Updated Successfully");
			else System.out.println("Updation failed");
			}catch (SQLException e) {
				// TODO: handle exception
			}
	}
	
	private static void deleteReservation(Connection con, Scanner sc,Statement s) {
		System.out.print("Enter Reservation Id to delete: ");
		int r_id=sc.nextInt();
		if(!reservationExist(con, r_id, s)) {
			System.out.println("Reservation Id not found.");
			return;
		}
		System.out.println();
		String sql="Delete from reservation where r_id="+r_id;
		
		try {
			int affectedrows=s.executeUpdate(sql);
			
			if(affectedrows>0)System.out.println("Reservation deleted successfully.");
			else System.out.println("Deletion failed.");
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	private static boolean reservationExist(Connection con, int r_id,Statement s) {
		try {
			String sql="Select r_id from reservation where r_id="+r_id;
			
			try (ResultSet rs=s.executeQuery(sql)){
				return rs.next();
				
			}
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	private static void Exit() throws InterruptedException {
		System.out.print("Application closing");
		int i=5;
		while(i!=0) {
			System.out.print(".");
			Thread.sleep(1000);
			i--;
		}
		System.out.println();
		System.out.println("Application closed");
		
	}
}
