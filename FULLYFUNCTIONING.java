package ans;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class FULLYFUNCTIONING 
{
    public static void main( String[] args ) throws SQLException
    {
        String url = "jdbc:mysql://localhost:3306/mysql";
        String dbuser = "root";
        String password = "";

        Scanner get = new Scanner(System.in);
        int option = -1;
        int i = 0;
        int id;
        String firstname;
        String Surname;
        String address;
        String string;

        System.out.println("--MENU--");
        System.out.println("--1. Create--");
        System.out.println("--2. Retrive all--");
        System.out.println("--3. Update --");
        System.out.println("--4. Delete--");
        System.out.println("--5. Exit--");

        while(option != 5)
        {   
            Connection con = DriverManager.getConnection(url, dbuser, password);
            PreparedStatement pstat = null;
            Statement stmt = con.createStatement();

            option = get.nextInt(); 

            if(option == 1) //create a record in the database
                {
                    try
                        {
                            i=0;
                            get.nextLine(); // repair
                            System.out.println("Name?");
                            firstname = get.nextLine();

                            System.out.println("Surname?");
                            Surname = get.nextLine();

                            System.out.println("Address?");
                            address = get.nextLine();

                            pstat = con.prepareStatement("INSERT INTO mydb (fname, surname, address) VALUES(?,?,?)"); //INSERT INTO mydb (fname, surname, address) VALUES (?,?,?)"
                            pstat.setString(1, firstname);
                            pstat.setString(2, Surname);
                            pstat.setString(3, address);
                            
                            i = pstat.executeUpdate();
                            System.out.println( i + " record successfully added to the table .");
                        }
                    catch(SQLException sqlException)
                        {
                            sqlException.printStackTrace();
                        }
                    finally 
                        {
                            try 
                                {
                                    pstat.close();
                                    con.close();
                                }
                            catch (Exception exception)
                                {
                                    exception.printStackTrace();
                                }
                        }
                }
            else if(option == 2) // retrive data from the database
                {
                    String query = "SELECT * FROM mydb";
                    ResultSet rs = stmt.executeQuery(query);

                    while(rs.next())
                        {
                            id = rs.getInt("id");
                            firstname = rs.getString("fname");
                            Surname = rs.getString("surname");
                            address = rs.getString("address");

                            System.out.println("ID: " + id + "\nName: " + firstname + "\nSurname: " + Surname + "\nAddress: " + address);
                            System.out.println();
                        }
                }
            else if(option == 3) //update the table data
                {
                    i=0;
                    get.nextLine(); // repair
                    string = "UPDATE mydb SET surname=? WHERE fname=?";

                    System.out.println("Enter First-name where surname modification takes place :");
                    firstname = get.nextLine();

                    System.out.println("Surname that will be inserted :");
                    Surname = get.nextLine();

                    try {
                            pstat = con.prepareStatement(string);
                            pstat.setString(1, Surname);
                            pstat.setString(2, firstname);

                            i = pstat.executeUpdate();
                            System.out.println("Update was successful!");
                        }
                    catch(SQLException sqlException) //default lines of code that close connections
                        {
                            sqlException.printStackTrace();
                        }
                    finally
                        {
                            try 
                                {
                                    pstat.close () ;
                                    con.close () ;
                                }
                                catch (Exception exception)
                                {
                                    exception . printStackTrace () ;
                                }
                        }
                }
            else if(option == 4) //delete record
                {
                    get.nextLine(); // repair
                    string = "DELETE FROM mydb WHERE fname=?";
                    System.out.println("Enter first name to delete a corresponding row");
                    firstname = get.nextLine();

                    try
                        {
                            pstat = con.prepareStatement(string);
                            pstat.setString(1, firstname);

                            i = pstat.executeUpdate();
                            System.out.println("The record was deleted successfully");
                        }
                    catch(SQLException sqlException)
                        {
                            sqlException.printStackTrace();
                        }
                    finally
                        {
                            try 
                                {
                                    pstat.close () ;
                                    con.close () ;
                                }
                                catch (Exception exception)
                                {
                                    exception . printStackTrace () ;
                                }
                        }
                }
        }//end while loop
    }//end main
}
