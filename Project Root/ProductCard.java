package ans;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ProductCard extends JPanel 
{
    private static final int CARD_WIDTH = 200;
    private static final int CARD_HEIGHT = 280;
    private static final int IMAGE_HEIGHT = 160;

    public ProductCard(Product product) 
    {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setMaximumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setBackground(new Color(28, 28, 28));
        setBorder(BorderFactory.createCompoundBorder
        (
            BorderFactory.createLineBorder(new Color(200, 160, 80, 80), 1), // gold border color for the product box
            BorderFactory.createEmptyBorder(10, 10, 12, 10)
        ));

        // Product image
        JButton imageBtn = new JButton();
        imageBtn.setPreferredSize(new Dimension(CARD_WIDTH - 20, IMAGE_HEIGHT));
        imageBtn.setBackground(new Color(40, 40, 40));
        imageBtn.setBorderPainted(false);
        imageBtn.setFocusPainted(false);
        imageBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imageBtn.setToolTipText("View " + product.getTitle());

        ImageIcon icon = loadImage(product.getImagePath(), CARD_WIDTH - 20, IMAGE_HEIGHT);
        if (icon != null) 
        {
            imageBtn.setIcon(icon);
        } 
        else 
        {
            imageBtn.setText("No Image");
            imageBtn.setForeground(new Color(120, 120, 120));
            imageBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }
        // clicking the image opens the product detail page
        imageBtn.addActionListener(e -> 
        {
            // dispose of this GUI
            SwingUtilities.getWindowAncestor(this).dispose();

            // launch the new GUI
            new ProductDetailPage(product).setVisible(true);
        });
            
        // Title
        JLabel titleLabel = new JLabel("<html><body style='width:170px'>" + product.getTitle() + "</body></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 4, 0));

        // Price
        JLabel priceLabel = new JLabel(String.format("$%.2f", product.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        priceLabel.setForeground(CommonConstants.TEXT_COLOR); // gold

        // Stock quantity
        JLabel stockLabel; 

        if (product.getStockQuantity() <= 0)
        {
            stockLabel = new JLabel("Out of Stock");
            stockLabel.setForeground(new Color(200, 60, 60));
        }
        else if (product.getStockQuantity() <= 3) // shows "Low stock" in red when <= 3, otherwise shows normally
        {
            stockLabel = new JLabel("Only " + product.getStockQuantity() + " left!");
            stockLabel.setForeground(new Color(220, 120, 40)); // amber
        }
        else
        {
            stockLabel = new JLabel("In Stock: " + product.getStockQuantity());
            stockLabel.setForeground(new Color(100, 180, 100)); // green
        }

        stockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        stockLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

        // Bottom panel (title + price)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.add(titleLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(stockLabel);

        add(imageBtn, BorderLayout.NORTH);
        add(infoPanel,  BorderLayout.CENTER);
    }

    private ImageIcon loadImage(String imagePath, int width, int height) 
    {
        try 
        {
            File file = new File("src/main/java/ans/images/" + imagePath);
            if (!file.exists()) return null;

            ImageIcon raw = new ImageIcon(file.getAbsolutePath());
            Image scaled = raw.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } 
        catch (Exception e) 
        {
            return null;
        }
    }
}