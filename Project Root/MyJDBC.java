package db;

import ans.CommonConstants;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// JDBC - Java Database Connectivity
// Serves as a gateway in accessing MySQL database
public class MyJDBC 
{
    // register new user to the database 
    // true - register success
    // false - register failure
    public static boolean register (String username, String password)
    {
        try
        {   // check if username exists in the DB (and if there were no records found do this)
            if (!checkUser(username))
                {
                    Connection con = DriverManager.getConnection
                    (CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD); 

                    // create insert query
                    PreparedStatement insertUser = con.prepareStatement("INSERT INTO " + CommonConstants.DB_USERS_TABLE + " (login_name, password) VALUES(?,?)");
                    insertUser.setString(1, username);
                    insertUser.setString(2, password);
                    
                    // update db with new user
                    insertUser.executeUpdate();
                    return true;
                }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return false;
    }

    // check if username exists in the DB
    // false - user doesn't exist
    // true - user exists in the DB
    public static boolean checkUser(String username)
    {
        try 
        {
            Connection con = DriverManager.getConnection
            (CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);

            PreparedStatement checkUserExistence = con.prepareStatement("SELECT * FROM " + CommonConstants.DB_USERS_TABLE + " WHERE login_name = ? ");
            checkUserExistence.setString(1, username);

            ResultSet rs = checkUserExistence.executeQuery();

            // if it's empty it means that there was no data that contains the username (user does not exist)
            if(!rs.isBeforeFirst()) 
            {
                return false;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return true;
    }
    public static boolean validateLogin(String username, String password)
    {
        try
        {
            Connection con = DriverManager.getConnection(CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);

            PreparedStatement validateUser = con.prepareStatement
            ("SELECT * FROM " + CommonConstants.DB_USERS_TABLE + " WHERE login_name = ? AND password = ? AND deleted = false");
            validateUser.setString(1, username);
            validateUser.setString(2, password);

            ResultSet rs = validateUser.executeQuery();

            if(!rs.isBeforeFirst())
                {
                    return false;
                }
            CommonConstants.currentUser = username;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return true;
    }

    // get user's details for the settings page
    // uses an array to return multiple string values
    // [0] = login_name, [1] = password, [2] = email, [3] = address, [4] = delivery_preference
    public static String[] getUserDetails(String username)
    {
        try
        {
            Connection con = DriverManager.getConnection
            (CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
 
            PreparedStatement stmt = con.prepareStatement
            ("SELECT login_name, password, email, address, delivery_preference FROM " + CommonConstants.DB_USERS_TABLE + " WHERE login_name = ?");
            
            stmt.setString(1, username);
 
            ResultSet rs = stmt.executeQuery();
 
            if (rs.next())
            {
                return new String[]
                {
                    rs.getString("login_name"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getString("delivery_preference")
                };
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    // update user details and return true on success
    public static boolean updateUser(String originalUsername, String newUsername, String newPassword, String newEmail, String newAddress, String deliveryPreference)
    {
        try
        {
            Connection con = DriverManager.getConnection(CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
 
            PreparedStatement stmt = con.prepareStatement
            ("UPDATE " + CommonConstants.DB_USERS_TABLE + " SET login_name = ?, password = ?, email = ?, address = ?, delivery_preference = ? WHERE login_name = ?");
            stmt.setString(1, newUsername);
            stmt.setString(2, newPassword);
            stmt.setString(3, newEmail);
            stmt.setString(4, newAddress);
            stmt.setString(5, deliveryPreference);
            stmt.setString(6, originalUsername);
 
            stmt.executeUpdate();

            if (!originalUsername.equals(newUsername))
                {
                    PreparedStatement cartUpdate = con.prepareStatement
                    ("UPDATE cart SET username = ? WHERE username = ?");

                    cartUpdate.setString(1, newUsername);
                    cartUpdate.setString(2, originalUsername);
                    
                    cartUpdate.executeUpdate();
                }
 
            // keep currentUser in sync if username was changed
            CommonConstants.currentUser = newUsername;
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    // delete user account and return true on success
    public static boolean deleteUser(String username)
    {
        try
        {
            Connection con = DriverManager.getConnection(CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
 
            PreparedStatement stmt = con.prepareStatement
            ("UPDATE " + CommonConstants.DB_USERS_TABLE + " SET deleted = 1 WHERE login_name = ?");

            stmt.setString(1, username);
 
            stmt.executeUpdate();
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }



    
    // Cart methods -------------------------------------------------

    // add a product to the user's cart
    // if the same product is already in the cart, increment quantity instead
    public static boolean addToCart(String username, int productId, String productTitle, double price)
    {
        try
        {
            Connection con = DriverManager.getConnection(CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
 
            // check if item already in cart for this user
            PreparedStatement checkStmt = con.prepareStatement("SELECT id, quantity FROM cart WHERE username = ? AND product_id = ?");
            checkStmt.setString(1, username);
            checkStmt.setInt(2, productId);
 
            ResultSet rs = checkStmt.executeQuery();
 
            if (rs.next())
            {
                // already in cart - increment quantity
                int newQty = rs.getInt("quantity") + 1;
                int cartId = rs.getInt("id");
 
                PreparedStatement updateStmt = con.prepareStatement("UPDATE cart SET quantity = ? WHERE id = ?");
                updateStmt.setInt(1, newQty);
                updateStmt.setInt(2, cartId);

                updateStmt.executeUpdate();

                // SS-02: decrements stock quantity when item is added to cart
                // if quantity hits 0, also flips in_stock to false
                PreparedStatement stockStmt = con.prepareStatement
                (
                    "UPDATE " + CommonConstants.DB_ITEMS_TABLE +
                    " SET stock_quantity = stock_quantity - 1," +
                    " in_stock = CASE WHEN stock_quantity <= 0 THEN FALSE ELSE in_stock END" +
                    " WHERE id = ? AND stock_quantity > 0"
                );
                stockStmt.setInt(1, productId);

                stockStmt.executeUpdate();
            }
            else
            {
                // new item — insert row
                PreparedStatement insertStmt = con.prepareStatement
                ("INSERT INTO cart (username, product_id, product_title, price, quantity) VALUES (?,?,?,?,1)");
                
                insertStmt.setString(1, username);
                insertStmt.setInt(2, productId);
                insertStmt.setString(3, productTitle);
                insertStmt.setDouble(4, price);

                insertStmt.executeUpdate();

                // SS-02: decrements stock quantity when item is added to cart
                // if quantity hits 0, also flips in_stock to false
                PreparedStatement stockStmt = con.prepareStatement
                (
                    "UPDATE " + CommonConstants.DB_ITEMS_TABLE +
                    " SET stock_quantity = stock_quantity - 1," +
                    " in_stock = CASE WHEN stock_quantity <= 0 THEN FALSE ELSE in_stock END" +
                    " WHERE id = ? AND stock_quantity > 0"
                );
                stockStmt.setInt(1, productId);

                stockStmt.executeUpdate();
            }
 
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
 
    // remove a single item from the cart by its cart row id
    public static boolean removeFromCart(int cartId)
    {
        try
        {
            Connection con = DriverManager.getConnection(CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);

            // restore stock when item is removed from cart
            PreparedStatement restoreStmt = con.prepareStatement
            (
                "UPDATE " + CommonConstants.DB_ITEMS_TABLE +
                " SET stock_quantity = stock_quantity + 1, in_stock = TRUE" +
                " WHERE id = (SELECT product_id FROM cart WHERE id = ?)"
            );
            restoreStmt.setInt(1, cartId);

            restoreStmt.executeUpdate();

            PreparedStatement stmt = con.prepareStatement("DELETE FROM cart WHERE id = ?");
            stmt.setInt(1, cartId);

            stmt.executeUpdate();
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
 
    // clear entire cart (called after successful payment)
    public static boolean clearCart(String username)
    {
        try
        {
            Connection con = DriverManager.getConnection(CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
 
            PreparedStatement stmt = con.prepareStatement("DELETE FROM cart WHERE username = ?");

            stmt.setString(1, username);

            stmt.executeUpdate();
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    // placeOrder() runs full payment transaction
    public static double placeOrder(String username)
    {
        try
        {
            Connection con = DriverManager.getConnection(CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
 
            // get idusers and delivery_preference for this user
            PreparedStatement userStmt = con.prepareStatement
            ("SELECT idusers, delivery_preference FROM " + CommonConstants.DB_USERS_TABLE + " WHERE login_name = ?");
            
            userStmt.setString(1, username);
 
            ResultSet userRs = userStmt.executeQuery();
 
            if (!userRs.next())
            {
                return -1.0; // user not found
            }
 
            int idusers = userRs.getInt("idusers");
            String deliveryPreference = userRs.getString("delivery_preference");
 
            // fetch delivery price by name from delivery table
            PreparedStatement deliveryStmt = con.prepareStatement("SELECT price FROM delivery WHERE name = ?");
            deliveryStmt.setString(1, deliveryPreference);
 
            ResultSet deliveryRs = deliveryStmt.executeQuery();
 
            if (!deliveryRs.next())
            {
                return -1.0; // delivery option not found
            }
 
            double deliveryPrice = deliveryRs.getDouble("price");
 
            // sum of the cart items
            PreparedStatement cartStmt = con.prepareStatement("SELECT product_title, price, quantity, booking_date FROM cart WHERE username = ?");
            cartStmt.setString(1, username);
 
            ResultSet cartRs = cartStmt.executeQuery();
 
            double subtotal = 0;
 
            // stores cart rows in parallel arrays
            // loops once for the sum and for order_line inserts
            String[] titles = new String[100];
            double[] prices = new double[100];
            int[] quantities = new int[100];
            String[] bookingDates  = new String[100];
            int itemCount = 0;
 
            while (cartRs.next())
            {
                titles[itemCount] = cartRs.getString("product_title");
                prices[itemCount] = cartRs.getDouble("price");
                quantities[itemCount] = cartRs.getInt("quantity");
                bookingDates[itemCount] = cartRs.getString("booking_date");

                subtotal += prices[itemCount] * quantities[itemCount];
                itemCount++;
            }
 
            if (itemCount == 0)
            {
                return -1.0; // empty cart
            }
 
            double grandTotal = subtotal + deliveryPrice;
 
            // insert into orders + get id
            PreparedStatement orderStmt = con.prepareStatement
            ("INSERT INTO orders (idusers, order_date, delivery_price, total) VALUES (?, NOW(), ?, ?)", Statement.RETURN_GENERATED_KEYS);
            
            orderStmt.setInt(1, idusers);
            orderStmt.setDouble(2, deliveryPrice);
            orderStmt.setDouble(3, grandTotal);
            
            orderStmt.executeUpdate();
 
            ResultSet generatedKeys = orderStmt.getGeneratedKeys();
 
            if (!generatedKeys.next())
            {
                return -1.0; // could not get order id
            }
 
            int orderId = generatedKeys.getInt(1);
 
            // insert one row per item into order_line
            PreparedStatement lineStmt = con.prepareStatement
            ("INSERT INTO order_line (order_id, product_title, price, quantity) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS); // take generated order_line id for booking FK
 
            PreparedStatement bookingStmt = con.prepareStatement
            ("INSERT INTO booking (idusers, order_line_id, product_title, booking_date) VALUES (?, ?, ?, ?)");

            for (int i = 0; i < itemCount; i++)
            {
                lineStmt.setInt(1, orderId);
                lineStmt.setString(2, titles[i]);
                lineStmt.setDouble(3, prices[i]);
                lineStmt.setInt(4, quantities[i]);

                lineStmt.executeUpdate();

                // if this cart item has a booking_date, create a booking row
                if (bookingDates[i] != null)
                {
                    ResultSet lineKeys = lineStmt.getGeneratedKeys();
                    if (lineKeys.next())
                    {
                        int orderLineId = lineKeys.getInt(1);
                        bookingStmt.setInt(1, idusers);
                        bookingStmt.setInt(2, orderLineId);
                        bookingStmt.setString(3, titles[i]);
                        bookingStmt.setString(4, bookingDates[i]);

                        bookingStmt.executeUpdate();
                    }
                }
            }
            // clear the cart
            clearCart(username);
 
            return grandTotal;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
 
        return -1.0;
    }

    // addBookableToCart() used same as addToCart but also stores booking_date in cart row
    // bookable items are always quantity of 1 so no increment logic needed
    public static boolean addBookableToCart(String username, int productId, String productTitle, double price, String bookingDate)
    {
        try
        {
            Connection con = DriverManager.getConnection(CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
 
            // bookable items are always 1 per booking - replace if already in cart
            PreparedStatement checkStmt = con.prepareStatement("SELECT id FROM cart WHERE username = ? AND product_id = ?");
            checkStmt.setString(1, username);
            checkStmt.setInt(2, productId);
 
            ResultSet rs = checkStmt.executeQuery();
 
            if (rs.next())
            {
                // already in cart - update booking date only
                int cartId = rs.getInt("id");
 
                PreparedStatement updateStmt = con.prepareStatement("UPDATE cart SET booking_date = ? WHERE id = ?");
                updateStmt.setString(1, bookingDate);
                updateStmt.setInt(2, cartId);

                updateStmt.executeUpdate();
            }
            else
            {
                // it is a new bookable item - insert with booking_date and quantity fixed to 1 
                PreparedStatement insertStmt = con.prepareStatement
                ("INSERT INTO cart (username, product_id, product_title, price, quantity, booking_date) VALUES (?,?,?,?,1,?)");

                insertStmt.setString(1, username);
                insertStmt.setInt(2, productId);
                insertStmt.setString(3, productTitle);
                insertStmt.setDouble(4, price);
                insertStmt.setString(5, bookingDate);

                insertStmt.executeUpdate();

                // SS-02: decrements stock quantity when item is added to cart
                // if quantity hits 0, also flip in_stock to false
                PreparedStatement stockStmt = con.prepareStatement
                (
                    "UPDATE " + CommonConstants.DB_ITEMS_TABLE +
                    " SET stock_quantity = stock_quantity - 1," +
                    " in_stock = CASE WHEN stock_quantity <= 0 THEN FALSE ELSE in_stock END" +
                    " WHERE id = ? AND stock_quantity > 0"
                );
                stockStmt.setInt(1, productId);

                stockStmt.executeUpdate();
            }
 
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    // updateBookingDate() called from MyBookingsPage when user changes lesson date
    public static boolean updateBookingDate(int bookingId, String newDate)
    {
        try
        {
            Connection con = DriverManager.getConnection(CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
 
            PreparedStatement stmt = con.prepareStatement("UPDATE booking SET booking_date = ? WHERE id = ?");
            stmt.setString(1, newDate);
            stmt.setInt(2, bookingId);

            stmt.executeUpdate();
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    // cancelBooking() deletes the booking row from DB
    public static boolean cancelBooking(int bookingId)
    {
        try
        {
            Connection con = DriverManager.getConnection(CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
 
            PreparedStatement stmt = con.prepareStatement("DELETE FROM booking WHERE id = ?");
            stmt.setInt(1, bookingId);

            stmt.executeUpdate();
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }


    // Reviews methods -------------------------------------------------

    // Get reviews for a specific product (max 100 reviews)
    public static String[] getReviews(int itemId)
    {
        String[] reviewsArray = new String[100];
        try
        {
            Connection con = DriverManager.getConnection(CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
 
            PreparedStatement stmt = con.prepareStatement("SELECT username, review FROM reviews WHERE item_id = ?");
            stmt.setInt(1, itemId);

            ResultSet rs = stmt.executeQuery();
 
            int count = 0;
            while (rs.next() && count < 100)
            {
                // Format - "Username: The review text"
                reviewsArray[count] = rs.getString("username") + ": " + rs.getString("review");
                count++;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return reviewsArray;
    }

    // Add a new review
    public static boolean addReview(String username, int itemId, String reviewText)
    {
        try
        {
            Connection con = DriverManager.getConnection(CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
 
            PreparedStatement stmt = con.prepareStatement("INSERT INTO reviews (item_id, review, username) VALUES (?, ?, ?)");
            
            stmt.setInt(1, itemId);
            stmt.setString(2, reviewText);
            stmt.setString(3, username);
            
            stmt.executeUpdate();
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
