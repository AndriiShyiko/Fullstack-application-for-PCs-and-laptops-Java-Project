package ans;

import db.MyJDBC;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProductDetailPage extends MainFrame
{
    private final Product product;

    public ProductDetailPage(Product product)
    {
        super("Blade Forge Shop - " + product.getTitle());
        this.product = product;
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
        contentArea.setBorder(BorderFactory.createEmptyBorder(30, 36, 30, 36));

        // Top section: image on the left, info on the right 
        JPanel topSection = new JPanel(new BorderLayout(30, 0));
        topSection.setOpaque(false);

        // Product image
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(340, 300));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(28, 28, 28));
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 160, 80, 80), 1));

        ImageIcon icon = loadImage(product.getImagePath(), 340, 300);
        if (icon != null) 
        {
            imageLabel.setIcon(icon);
        } 
        else 
        {
            imageLabel.setText("No Image");
            imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            imageLabel.setForeground(new Color(120, 120, 120));
        }

        // Right side: title, price, description, buttons
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel(product.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Price
        JLabel priceLabel = new JLabel(String.format("$%.2f", product.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        priceLabel.setForeground(CommonConstants.TEXT_COLOR);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 16, 0));

        // Description
        JTextArea descArea = new JTextArea(product.getDescription());
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descArea.setForeground(new Color(200, 200, 200));
        descArea.setBackground(CommonConstants.PRIMARY_COLOR);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setEditable(false);
        descArea.setFocusable(false);
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        descArea.setMaximumSize(new Dimension(380, Integer.MAX_VALUE));
        descArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        // date picker shown only for bookable items
        // JSpinner with SpinnerDateModel - standard Swing date selector
        JSpinner datePicker = null;

        if (product.isBookable())
        {
            JLabel dateHeading = new JLabel("Select Booking Date");
            dateHeading.setFont(new Font("Segoe UI", Font.BOLD, 13));
            dateHeading.setForeground(CommonConstants.TEXT_COLOR);
            dateHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
 
            // start from tomorrow so users cannot book in the past
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_MONTH, 1);
 
            SpinnerDateModel dateModel = new SpinnerDateModel
            (
                tomorrow.getTime(), // initial value: tomorrow
                tomorrow.getTime(), // minimum: tomorrow
                null,           // no maximum date
                Calendar.DAY_OF_MONTH
            );
 
            datePicker = new JSpinner(dateModel);
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(datePicker, "dd / MM / yyyy");
            datePicker.setEditor(dateEditor);
            datePicker.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            datePicker.setBackground(new Color(30, 30, 30));
            datePicker.setForeground(Color.WHITE);
            datePicker.setMaximumSize(new Dimension(180, 36));
            datePicker.setAlignmentX(Component.LEFT_ALIGNMENT);
 
            infoPanel.add(dateHeading);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            infoPanel.add(datePicker);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        }
 
        // fixed reference of the datePicker to use inside the logic
        final JSpinner finalDatePicker = datePicker;

        // Add to Cart button 
        JButton addToCartBtn = new JButton("+  Add to Cart");
        addToCartBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addToCartBtn.setForeground(Color.BLACK);
        addToCartBtn.setBackground(CommonConstants.TEXT_COLOR);
        addToCartBtn.setBorderPainted(false);
        addToCartBtn.setFocusPainted(false);
        addToCartBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addToCartBtn.setPreferredSize(new Dimension(180, 42));
        addToCartBtn.setMaximumSize(new Dimension(180, 42));
        addToCartBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        addToCartBtn.addActionListener(e ->
        {
            boolean added;
 
            // bookable items go through addBookableToCart with the chosen date
            if (product.isBookable())
            {
                Date selectedDate = (Date) finalDatePicker.getValue();
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);
 
                added = MyJDBC.addBookableToCart
                (
                    CommonConstants.currentUser,
                    product.getId(),
                    product.getTitle(),
                    product.getPrice(),
                    formattedDate
                );
            }
            else
            {
                // regular product - original addToCart call unchanged
                added = MyJDBC.addToCart
                (
                    CommonConstants.currentUser,
                    product.getId(),
                    product.getTitle(),
                    product.getPrice()
                );
            }
 
            if (added) 
            {
                JOptionPane.showMessageDialog
                (
                    this,
                    product.getTitle() + " added to cart!",
                    "Added",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } 
            else 
            {
                JOptionPane.showMessageDialog
                (
                    this,
                    "Could not add to cart. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // View Cart button 
        JButton viewCartBtn = new JButton("View Cart");
        viewCartBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewCartBtn.setForeground(CommonConstants.TEXT_COLOR);
        viewCartBtn.setBackground(new Color(40, 40, 40));
        viewCartBtn.setBorderPainted(false);
        viewCartBtn.setFocusPainted(false);
        viewCartBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewCartBtn.setPreferredSize(new Dimension(180, 42));
        viewCartBtn.setMaximumSize(new Dimension(180, 42));
        viewCartBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        viewCartBtn.addActionListener(e -> 
        {
            // dispose of this GUI
            dispose();

            // launch the new GUI
            new CartPage().setVisible(true);
        });

        // Back to catalog link 
        JLabel backLabel = new JLabel("← Back to Catalog");
        backLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backLabel.setForeground(new Color(150, 150, 150));
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        backLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        backLabel.addMouseListener(new java.awt.event.MouseAdapter() 
        {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) 
            {
                // dispose of this GUI
                dispose();

                // launch the new GUI
                new ProductCatalog().setVisible(true);
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) 
            {
                backLabel.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) 
            {
                backLabel.setForeground(new Color(150, 150, 150));
            }
        });

        // Assemble info panel
        infoPanel.add(titleLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(descArea);
        infoPanel.add(addToCartBtn);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(viewCartBtn);
        infoPanel.add(backLabel);

        topSection.add(imageLabel, BorderLayout.WEST);
        topSection.add(infoPanel,  BorderLayout.CENTER);

        contentArea.add(topSection, BorderLayout.NORTH);

        // Review section
        JPanel reviewSection = new JPanel(new BorderLayout(0, 10));
        reviewSection.setOpaque(false);
        reviewSection.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Title
        JLabel reviewHeading = new JLabel("Customer Reviews");
        reviewHeading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        reviewHeading.setForeground(Color.WHITE);
        reviewSection.add(reviewHeading, BorderLayout.NORTH);

        // Existing reviews
        JTextArea reviewsDisplay = new JTextArea();
        String[] existingReviews = MyJDBC.getReviews(product.getId());
        boolean hasReviews = false;
        
        for (int i = 0; i < existingReviews.length; i++) 
        {
            if (existingReviews[i] != null) 
            {
                reviewsDisplay.append(existingReviews[i] + "\n\n");
                hasReviews = true;
            }
        }
        
        // If there are no reviews, show the default msg
        if (!hasReviews) 
        {
            reviewsDisplay.setText("Be the first to review this product!\n");
        }
        
        reviewsDisplay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reviewsDisplay.setForeground(new Color(200, 200, 200));
        reviewsDisplay.setBackground(new Color(30, 30, 30));
        reviewsDisplay.setEditable(false);
        reviewsDisplay.setLineWrap(true);
        reviewsDisplay.setWrapStyleWord(true);
        reviewsDisplay.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(reviewsDisplay);
        scrollPane.setPreferredSize(new Dimension(800, 150));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        
        scrollPane.getVerticalScrollBar().setBackground(new Color(40, 40, 40));
        reviewSection.add(scrollPane, BorderLayout.CENTER);

        // A new review
        JPanel addReviewPanel = new JPanel(new BorderLayout(10, 0));
        addReviewPanel.setOpaque(false);

        JTextField newReviewField = new JTextField();
        newReviewField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        newReviewField.setBackground(new Color(40, 40, 40));
        newReviewField.setForeground(Color.WHITE);
        newReviewField.setCaretColor(Color.WHITE);
        newReviewField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JButton submitReviewBtn = new JButton("Submit");
        submitReviewBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitReviewBtn.setForeground(Color.BLACK);
        submitReviewBtn.setBackground(CommonConstants.TEXT_COLOR);
        submitReviewBtn.setFocusPainted(false);
        submitReviewBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        submitReviewBtn.addActionListener(e -> 
        {
            String reviewText = newReviewField.getText().trim();
            
            if (reviewText.isEmpty()) 
            {
                return; // Nothing happens if the field is empty
            }
            
            // Check for the right character length
            if (reviewText.length() > 300) 
            {
                JOptionPane.showMessageDialog
                (
                    this,
                    "Your review is too long! Please keep it under 300 characters.",
                    "Review is too long",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            
            // Save review to the DB
            boolean success = MyJDBC.addReview(CommonConstants.currentUser, product.getId(), reviewText);
            
            if (success) 
            {
                // Temporarily update UI to show the new review instantly
                if (reviewsDisplay.getText().contains("Be the first")) 
                {
                    reviewsDisplay.setText(""); // Clear the placeholder
                }
                
                reviewsDisplay.append(CommonConstants.currentUser + ": " + reviewText + "\n\n");
                newReviewField.setText(""); // Clear input field
                
                JOptionPane.showMessageDialog
                (
                    this,
                    "Review submitted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } 
            else 
            {
                JOptionPane.showMessageDialog
                (
                    this,
                    "Failed to submit review. Please try again.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        addReviewPanel.add(newReviewField, BorderLayout.CENTER);
        addReviewPanel.add(submitReviewBtn, BorderLayout.EAST);

        reviewSection.add(addReviewPanel, BorderLayout.SOUTH);

        // Add the new section to the layout
        contentArea.add(reviewSection, BorderLayout.CENTER);

        add(contentArea, BorderLayout.CENTER);
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