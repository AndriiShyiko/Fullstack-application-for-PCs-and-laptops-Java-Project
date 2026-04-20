package ans;

import db.MyJDBC;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyBookingsPage extends MainFrame
{
    public MyBookingsPage()
    {
        super("Blade Forge Shop - My Bookings");
        setLayout(new BorderLayout());
        addGuiComponents();
    }

    private void addGuiComponents()
    {
        // Sidebar
        add(new Sidebar(), BorderLayout.WEST);

        // Main content
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(CommonConstants.PRIMARY_COLOR);

        // Heading
        JLabel heading = new JLabel("My Bookings");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(CommonConstants.TEXT_COLOR);
        heading.setBorder(BorderFactory.createEmptyBorder(24, 28, 12, 28));
        contentArea.add(heading, BorderLayout.NORTH);

        // Scrollable bookings panel
        JPanel bookingsPanel = new JPanel();
        bookingsPanel.setLayout(new BoxLayout(bookingsPanel, BoxLayout.Y_AXIS));
        bookingsPanel.setBackground(CommonConstants.PRIMARY_COLOR);
        bookingsPanel.setBorder(BorderFactory.createEmptyBorder(8, 28, 28, 28));

        loadBookings(bookingsPanel);

        JScrollPane scrollPane = new JScrollPane(bookingsPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(CommonConstants.PRIMARY_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        contentArea.add(scrollPane, BorderLayout.CENTER);
        add(contentArea, BorderLayout.CENTER);
    }

    // Queries booking table directly and builds a card per row
    private void loadBookings(JPanel bookingsPanel)
    {
        bookingsPanel.removeAll();

        try
        {
            Connection con = DriverManager.getConnection
            (CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);

            // join booking -> users to filter by current logged-in user
            PreparedStatement stmt = con.prepareStatement
            (
                "SELECT b.id, b.product_title, b.booking_date " +
                "FROM booking b " +
                "JOIN users u ON u.idusers = b.idusers " +
                "WHERE u.login_name = ? " +
                "ORDER BY b.booking_date ASC"
            );
            stmt.setString(1, CommonConstants.currentUser);

            ResultSet rs = stmt.executeQuery();

            boolean hasBookings = false;

            while (rs.next())
            {
                int bookingId = rs.getInt("id");
                String productTitle = rs.getString("product_title");
                String bookingDate = rs.getString("booking_date");

                bookingsPanel.add(buildBookingCard(bookingId, productTitle, bookingDate, bookingsPanel));
                bookingsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
                hasBookings = true;
            }

            if (!hasBookings)
            {
                JLabel empty = new JLabel("No bookings made.");
                empty.setFont(new Font("Segoe UI", Font.ITALIC, 15));
                empty.setForeground(new Color(150, 150, 150));
                empty.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                bookingsPanel.add(empty);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();

            JLabel error = new JLabel("Failed to load bookings. Check your database connection.");
            error.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            error.setForeground(new Color(220, 80, 80));
            bookingsPanel.add(error);
        }

        bookingsPanel.revalidate();
        bookingsPanel.repaint();
    }

    // Builds one booking card with date spinner + change + cancel
    private JPanel buildBookingCard(int bookingId, String productTitle, String bookingDate, JPanel bookingsPanel)
    {
        JPanel card = new JPanel(new BorderLayout(16, 0));
        card.setBackground(new Color(28, 28, 28));
        card.setBorder(BorderFactory.createCompoundBorder
        (
            BorderFactory.createLineBorder(new Color(200, 160, 80, 60), 1),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Left: product title + current date
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(productTitle);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(Color.WHITE);

        JLabel dateLabel = new JLabel("Booked for: " + formatDisplayDate(bookingDate));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(new Color(180, 180, 180));

        leftPanel.add(titleLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        leftPanel.add(dateLabel);

        // Center: date spinner for changing the date
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_MONTH, 1);

        // initial value is always >= minimum
        Date parsedDate = parseDate(bookingDate);
        Date minDateVal = minDate.getTime();
        // if stored booking date is before tomorrow, fall back to tomorrow as the displayed value
        Date initialValue = parsedDate.before(minDateVal) ? minDateVal : parsedDate;

        SpinnerDateModel dateModel = new SpinnerDateModel
        (
            initialValue, // show current booking date
            minDateVal,      // can't pick past dates
            null,
            Calendar.DAY_OF_MONTH
        );

        JSpinner datePicker = new JSpinner(dateModel);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(datePicker, "dd / MM / yyyy");
        datePicker.setEditor(editor);
        datePicker.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        datePicker.setMaximumSize(new Dimension(160, 32));
        datePicker.setPreferredSize(new Dimension(160, 32));

        // Right: Change Date + Cancel buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton changeBtn = new JButton("Change Date");
        changeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        changeBtn.setForeground(Color.BLACK);
        changeBtn.setBackground(CommonConstants.TEXT_COLOR);
        changeBtn.setBorderPainted(false);
        changeBtn.setFocusPainted(false);
        changeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        changeBtn.addActionListener(e ->
        {
            Date selected = (Date) datePicker.getValue();
            String newDate = new SimpleDateFormat("yyyy-MM-dd").format(selected);

            boolean updated = MyJDBC.updateBookingDate(bookingId, newDate);

            if (updated) 
            {
                JOptionPane.showMessageDialog(
                    this,
                    "Booking updated to " + formatDisplayDate(newDate),
                    "Updated", JOptionPane.INFORMATION_MESSAGE);
                loadBookings(bookingsPanel); // refresh to show new date
            } 
            else 
            {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to update booking.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelBtn = new JButton("Cancel Booking");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setBackground(new Color(160, 40, 40));
        cancelBtn.setBorderPainted(false);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        cancelBtn.addActionListener(e ->
        {
            int confirm = JOptionPane.showConfirmDialog
            (
                this,
                "Cancel your booking for " + productTitle + "?",
                "Cancel Booking",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION)
            {
                boolean deleted = MyJDBC.cancelBooking(bookingId);

                if (deleted) 
                {
                    loadBookings(bookingsPanel); // refresh list
                } 
                else 
                {
                    JOptionPane.showMessageDialog(
                        this,
                        "Failed to cancel booking.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnPanel.add(datePicker);
        btnPanel.add(changeBtn);
        btnPanel.add(cancelBtn);

        card.add(leftPanel, BorderLayout.WEST);
        card.add(btnPanel,  BorderLayout.EAST);

        return card;
    }

    // Helpers 

    private Date parseDate(String dateStr)
    {
        try 
        {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } 
        catch (Exception e) 
        {
            return new Date();
        }
    }

    // converting "yyyy-MM-dd" into a readable "dd MMM yyyy" for display labels
    private String formatDisplayDate(String dateStr)
    {
        try 
        {
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            return new SimpleDateFormat("dd MMM yyyy").format(d);
        } 
        catch (Exception e) 
        {
            return dateStr;
        }
    }
}
