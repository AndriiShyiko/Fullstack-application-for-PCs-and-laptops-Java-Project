package ans;

import db.MyJDBC;
import javax.swing.*;
import java.awt.*;
import java.sql.*; 

public class CartPage extends MainFrame
{
    public CartPage()
    {
        super("Blade Forge Shop — Shopping Cart");
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
        JLabel heading = new JLabel("Shopping Cart");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(CommonConstants.TEXT_COLOR);
        heading.setBorder(BorderFactory.createEmptyBorder(24, 28, 12, 28));
        contentArea.add(heading, BorderLayout.NORTH);

        // Scrollable cart items panel
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(CommonConstants.PRIMARY_COLOR);
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 28));

        // Total label - updated when items are removed
        JLabel totalLabel = new JLabel();
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(Color.WHITE);

        loadCartItems(itemsPanel, totalLabel);

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(CommonConstants.PRIMARY_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentArea.add(scrollPane, BorderLayout.CENTER);

        // Bottom bar: total + buttons
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(new Color(18, 18, 18));
        bottomBar.setBorder(BorderFactory.createCompoundBorder
        (
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 160, 80, 60)),
            BorderFactory.createEmptyBorder(16, 28, 16, 28)
        ));

        bottomBar.add(totalLabel, BorderLayout.WEST);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnGroup.setOpaque(false);

        // Continue Shopping
        JButton continueBtn = new JButton("Continue Shopping");
        continueBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        continueBtn.setForeground(CommonConstants.TEXT_COLOR);
        continueBtn.setBackground(new Color(40, 40, 40));
        continueBtn.setBorderPainted(false);
        continueBtn.setFocusPainted(false);
        continueBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        continueBtn.addActionListener(e -> 
        {
            dispose();
            new ProductCatalog().setVisible(true);
        });

        // Pay Now
        JButton payBtn = new JButton("Pay Now");
        payBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        payBtn.setForeground(Color.BLACK);
        payBtn.setBackground(CommonConstants.BORDER_ORANGE);
        payBtn.setBorderPainted(false);
        payBtn.setFocusPainted(false);
        payBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        payBtn.addActionListener(e -> handlePayment(itemsPanel, totalLabel));

        btnGroup.add(continueBtn);
        btnGroup.add(payBtn);
        bottomBar.add(btnGroup, BorderLayout.EAST);

        contentArea.add(bottomBar, BorderLayout.SOUTH);
        add(contentArea, BorderLayout.CENTER);
    }

    private void loadCartItems(JPanel itemsPanel, JLabel totalLabel)
    {
        itemsPanel.removeAll();

        try
        {
            Connection con = DriverManager.getConnection
            (CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);

            PreparedStatement stmt = con.prepareStatement
            ("SELECT id, product_title, price, quantity FROM cart WHERE username = ?");
            stmt.setString(1, CommonConstants.currentUser);

            ResultSet rs = stmt.executeQuery();

            boolean hasItems = false;
            double total = 0;

            while (rs.next())
            {
                int cartId = rs.getInt("id");
                String title = rs.getString("product_title");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                double subtotal = price * quantity;

                itemsPanel.add(buildCartRow(cartId, title, quantity, subtotal, itemsPanel, totalLabel));
                itemsPanel.add(Box.createRigidArea(new Dimension(0, 8)));

                total += subtotal;
                hasItems = true;
            }

            if (!hasItems)
            {
                JLabel empty = new JLabel("Your cart is empty.");
                empty.setFont(new Font("Segoe UI", Font.ITALIC, 15));
                empty.setForeground(new Color(150, 150, 150));
                empty.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                itemsPanel.add(empty);
                totalLabel.setText("Total:  $0.00");
            }
            else
            {
                double deliveryPrice = 0;
                try
                {
                    PreparedStatement delivStmt = con.prepareStatement
                    ("SELECT d.price FROM delivery d JOIN " + CommonConstants.DB_USERS_TABLE
                        + " u ON u.delivery_preference = d.name WHERE u.login_name = ?");
                    delivStmt.setString(1, CommonConstants.currentUser);

                    ResultSet delivRs = delivStmt.executeQuery();
                    if (delivRs.next()) 
                    {deliveryPrice = delivRs.getDouble("price");}
                }
                catch (SQLException ex) { ex.printStackTrace(); }
 
                totalLabel.setText(String.format
                (
                    "<html>Subtotal: $%.2f &nbsp;&nbsp; Delivery: $%.2f &nbsp;&nbsp; <b>Total: $%.2f</b></html>",
                    total, deliveryPrice, total + deliveryPrice
                ));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();

            JLabel error = new JLabel("Failed to load cart. Check your database connection.");
            error.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            error.setForeground(new Color(220, 80, 80));
            itemsPanel.add(error);
        }

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    // ── Builds a single row: title × qty | subtotal | Remove button ───────
    private JPanel buildCartRow(int cartId, String title, int quantity, double subtotal, JPanel itemsPanel, JLabel totalLabel)
    {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setBackground(new Color(28, 28, 28));
        row.setBorder(BorderFactory.createCompoundBorder
        (
            BorderFactory.createLineBorder(new Color(200, 160, 80, 50), 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel(title + " * " + quantity);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setForeground(Color.WHITE);

        JLabel subtotalLabel = new JLabel(String.format("$%.2f", subtotal));
        subtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subtotalLabel.setForeground(new Color(200, 160, 80));
        subtotalLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton removeBtn = new JButton("Remove");
        removeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setBackground(new Color(160, 40, 40));
        removeBtn.setBorderPainted(false);
        removeBtn.setFocusPainted(false);
        removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeBtn.addActionListener(e -> 
        {
            MyJDBC.removeFromCart(cartId);
            loadCartItems(itemsPanel, totalLabel); // refresh list in place
        });

        row.add(nameLabel, BorderLayout.WEST);
        row.add(subtotalLabel, BorderLayout.CENTER);
        row.add(removeBtn, BorderLayout.EAST);

        return row;
    }

    private void handlePayment(JPanel itemsPanel, JLabel totalLabel)
    {
        // check cart is not empty by querying DB
        boolean cartEmpty = true;

        try
        {
            Connection con = DriverManager.getConnection
            (CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);

            PreparedStatement stmt = con.prepareStatement
            ("SELECT id FROM cart WHERE username = ? LIMIT 1");
            stmt.setString(1, CommonConstants.currentUser);

            ResultSet rs = stmt.executeQuery();
            cartEmpty = !rs.next();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        if (cartEmpty)
        {
            JOptionPane.showMessageDialog(this,
                "Your cart is empty!",
                "Nothing to pay",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog
        (
            this,
            "<html>" + totalLabel.getText() + "<br><br>Proceed to payment?</html>",
            "Confirm Payment",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION)
        {
            double grandTotal = MyJDBC.placeOrder(CommonConstants.currentUser);
 
            if (grandTotal < 0)
            {
                JOptionPane.showMessageDialog(this,
                    "Payment failed. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
 
            JOptionPane.showMessageDialog
            (
                this,
                String.format("Payment of $%.2f successful! Thank you for your order.", grandTotal),
                "Order Placed", JOptionPane.INFORMATION_MESSAGE
            );

            // dispose of this GUI
            dispose();

            // launch the new GUI
            new ProductCatalog().setVisible(true);
        }
    }
}