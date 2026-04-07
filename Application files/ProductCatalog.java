package ans;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import db.MyJDBC;

public class ProductCatalog extends MainFrame
{
    public ProductCatalog() 
    {
        super("Blade Forge Shop");

        // slot sidebar on the left and content on the right
        setLayout(new BorderLayout());

        addGuiComponents();
    }
    private void addGuiComponents()
    {
        // sidebar (to the left)
        Sidebar sidebar = new Sidebar();
        add(sidebar, BorderLayout.WEST);
 
        // main content area (in the center)
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(CommonConstants.PRIMARY_COLOR);
 
        // top bar: heading + cart button
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
 
        JLabel heading = new JLabel("Blacksmith's Products");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(CommonConstants.TEXT_COLOR);

        // cart button (top right)
        JButton cartBtn = new JButton("View Cart");
        cartBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cartBtn.setForeground(Color.BLACK);
        cartBtn.setBackground(CommonConstants.BORDER_ORANGE);
        cartBtn.setBorderPainted(false);
        cartBtn.setFocusPainted(false);
        cartBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cartBtn.addActionListener(e -> 
        {
            // dispose of this GUI
            dispose();

            // launch the new GUI
            new CartPage().setVisible(true);
        });
 
        topBar.add(heading, BorderLayout.WEST);
        topBar.add(cartBtn, BorderLayout.EAST);
        contentArea.add(topBar, BorderLayout.NORTH);
 
        // Grid panel — wraps cards into rows automatically
        JPanel gridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        gridPanel.setBackground(CommonConstants.PRIMARY_COLOR);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));
 
        // Load in-stock products from DB into the grid
        loadProducts(gridPanel);
 
        // Scroll pane — handles large numbers of products
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(CommonConstants.PRIMARY_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20); // smooth scroll
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
 
        contentArea.add(scrollPane, BorderLayout.CENTER);
        add(contentArea, BorderLayout.CENTER);
    }
 
    private void loadProducts(JPanel gridPanel) 
    {
        try
        {
            Connection con = DriverManager.getConnection
            (
                CommonConstants.DB_URL, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD
            );
 
            PreparedStatement fetchProducts = con.prepareStatement
            ("SELECT id, title, price, image_path, description FROM " + CommonConstants.DB_ITEMS_TABLE + " WHERE in_stock = TRUE");
 
            ResultSet rs = fetchProducts.executeQuery();
 
            // tracker of whether any rows came back
            boolean hasProducts = false;
 
            while (rs.next())
            {
                Product product = new Product
                (
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getDouble("price"),
                    rs.getString("image_path"),
                    rs.getString("description")
                );
 
                gridPanel.add(new ProductCard(product));
                hasProducts = true;
            }
 
            if (!hasProducts)
            {
                JLabel empty = new JLabel("No products currently in stock.");
                empty.setFont(new Font("Segoe UI", Font.ITALIC, 15));
                empty.setForeground(CommonConstants.TEXT_COLOR);
                gridPanel.add(empty);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
 
            JLabel error = new JLabel("Failed to load products. Check your database connection.");
            error.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            error.setForeground(CommonConstants.TEXT_COLOR);
            gridPanel.add(error);
        }
    }
}
