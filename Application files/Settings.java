package ans;

import db.MyJDBC;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Settings extends MainFrame
{
    // fields here serve as instance variables so Save button can read them
    private JTextField  usernameField;
    private JPasswordField passwordField;
    private JTextField  emailField;
    private JTextField  addressField;
    private JRadioButton standardBtn;
    private JRadioButton expressBtn;

    public Settings()
    {
        super("Blade Forge Shop — Settings");
        setLayout(new BorderLayout());
        addGuiComponents();
    }

    private void addGuiComponents()
    {
        // add sidebar
        add(new Sidebar(), BorderLayout.WEST);

        // main content
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(CommonConstants.PRIMARY_COLOR);

        // heading -----------
        JLabel heading = new JLabel("Settings & Personal Details");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(CommonConstants.TEXT_COLOR);
        heading.setBorder(BorderFactory.createEmptyBorder(24, 28, 8, 28));
        contentArea.add(heading, BorderLayout.NORTH);

        // Create the panel -----------
        JPanel formWrapper = new JPanel();
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.setBackground(CommonConstants.PRIMARY_COLOR);
        formWrapper.setBorder(BorderFactory.createEmptyBorder(8, 28, 28, 28));

        // load user data -----------
        // [0]=login_name [1]=password [2]=email [3]=address [4]=delivery_preference
        String[] details = MyJDBC.getUserDetails(CommonConstants.currentUser);
        String currentUsername = details != null ? details[0] : "";
        String currentPassword = details != null ? details[1] : "";
        String currentEmail = details != null ? details[2] : "";
        String currentAddress = details != null ? details[3] : "";
        String currentDelivery = details != null ? details[4] : "standard";

        // Credentials section -----------
        formWrapper.add(sectionLabel("Account Credentials"));
        formWrapper.add(Box.createRigidArea(new Dimension(0, 10)));

        usernameField = styledTextField(currentUsername);
        formWrapper.add(fieldRow("Username", usernameField));
        formWrapper.add(Box.createRigidArea(new Dimension(0, 12)));

        // Password chunk -----------
        passwordField = new JPasswordField(currentPassword);
        stylePasswordField(passwordField);
        JButton toggleBtn = new JButton("Show");
        styleToggleButton(toggleBtn);
        toggleBtn.addActionListener(e -> 
        {
            if (String.valueOf(toggleBtn.getText()).equals("Show")) 
            {
                passwordField.setEchoChar((char) 0); // revealed password
                toggleBtn.setText("Hide");
            } 
            else 
            {
                passwordField.setEchoChar('●'); // hidden password
                toggleBtn.setText("Show");
            }
        });
        JPanel passwordRow = labeledRowWithExtra("Password", passwordField, toggleBtn);
        formWrapper.add(passwordRow);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 24)));

        // Contact section -----------
        formWrapper.add(sectionLabel("Contact Information"));
        formWrapper.add(Box.createRigidArea(new Dimension(0, 10)));

        emailField = styledTextField(currentEmail);
        formWrapper.add(fieldRow("Email", emailField));
        formWrapper.add(Box.createRigidArea(new Dimension(0, 12)));

        addressField = styledTextField(currentAddress);
        formWrapper.add(fieldRow("Address", addressField));
        formWrapper.add(Box.createRigidArea(new Dimension(0, 24)));

        // Delivery preference chunk -----------
        formWrapper.add(sectionLabel("Delivery Preference"));
        formWrapper.add(Box.createRigidArea(new Dimension(0, 10)));

        standardBtn = styledRadio("Standard");
        expressBtn  = styledRadio("Express");
        ButtonGroup deliveryGroup = new ButtonGroup();
        deliveryGroup.add(standardBtn);
        deliveryGroup.add(expressBtn);

        if ("express".equalsIgnoreCase(currentDelivery)) 
        {
            expressBtn.setSelected(true);
        } 
        else 
        {
            standardBtn.setSelected(true);
        }

        JPanel deliveryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        deliveryPanel.setOpaque(false);
        deliveryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        deliveryPanel.add(standardBtn);
        deliveryPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        deliveryPanel.add(expressBtn);
        formWrapper.add(deliveryPanel);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 32)));

        // Save changes button -----------
        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setForeground(Color.BLACK);
        saveBtn.setBackground(CommonConstants.TEXT_COLOR);
        saveBtn.setFocusPainted(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveBtn.setPreferredSize(new Dimension(160, 40));
        saveBtn.setMaximumSize(new Dimension(160, 40));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        saveBtn.addActionListener(e -> handleSave());
        formWrapper.add(saveBtn);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 40)));

        // Divider -----------
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 60, 60));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        formWrapper.add(sep);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 24)));

        // Delete account button -----------
        JButton deleteBtn = new JButton("Delete Account");
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setBackground(new Color(180, 40, 40));
        deleteBtn.setFocusPainted(false);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteBtn.setPreferredSize(new Dimension(160, 40));
        deleteBtn.setMaximumSize(new Dimension(160, 40));
        deleteBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        deleteBtn.addActionListener(e -> handleDelete());
        formWrapper.add(deleteBtn);

        // Make panel scrollable -----------
        JScrollPane scrollPane = new JScrollPane(formWrapper);
        scrollPane.setBorder(null);
        scrollPane.setBackground(CommonConstants.PRIMARY_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        contentArea.add(scrollPane, BorderLayout.CENTER);
        add(contentArea, BorderLayout.CENTER);
    }

    // Action handlers -----------

    private void handleSave()
    {
        String newUsername  = usernameField.getText().trim();
        String newPassword  = new String(passwordField.getPassword()).trim();
        String newEmail     = emailField.getText().trim();
        String newAddress   = addressField.getText().trim();
        String newDelivery  = expressBtn.isSelected() ? "express" : "standard";

        if (newUsername.isEmpty() || newPassword.isEmpty())
        {
            JOptionPane.showMessageDialog(this,
                "Username and password cannot be empty.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = MyJDBC.updateUser
        (CommonConstants.currentUser, newUsername, newPassword, newEmail, newAddress, newDelivery);

        if (success) 
        {
            JOptionPane.showMessageDialog(this, "Details updated successfully!");
        } 
        else 
        {
            JOptionPane.showMessageDialog(this, "Failed to update. Please try again.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete()
    {
        int choice = JOptionPane.showConfirmDialog
        (
            this,
            "Are you sure you want to permanently delete your account?\nThis cannot be undone.",
            "Delete Account",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION)
        {
            boolean deleted = MyJDBC.deleteUser(CommonConstants.currentUser);
            if (deleted) 
            {
                JOptionPane.showMessageDialog(this, "Account deleted. Goodbye!");
                // dispose of this GUI
                this.dispose();
                
                // launch the new GUI
                new LoginFormGUI().setVisible(true);
            } 
            else 
            {
                JOptionPane.showMessageDialog(this, "Failed to delete account.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // UI design handlers

    private JLabel sectionLabel(String text)
    {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(CommonConstants.TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField styledTextField(String value)
    {
        JTextField field = new JTextField(value);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Color.WHITE);
        field.setBackground(new Color(30, 30, 30));
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder
        (
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        field.setPreferredSize(new Dimension(300, 36));
        field.setMaximumSize(new Dimension(300, 36));
        return field;
    }

    private void stylePasswordField(JPasswordField field)
    {
        field.setEchoChar('●');
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Color.WHITE);
        field.setBackground(new Color(30, 30, 30));
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder
        (
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        field.setPreferredSize(new Dimension(260, 36));
        field.setMaximumSize(new Dimension(260, 36));
    }

    private void styleToggleButton(JButton btn)
    {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(new Color(200, 160, 80));
        btn.setBackground(new Color(40, 40, 40));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(60, 36));
    }

    // row with a label on the left and one field on the right
    private JPanel fieldRow(String labelText, JComponent field)
    {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(180, 180, 180));
        label.setPreferredSize(new Dimension(100, 36));

        row.add(label);
        row.add(field);
        return row;
    }

    // row for password - a label, a field, and a radio buttons
    private JPanel labeledRowWithExtra(String labelText, JComponent field, JComponent extra)
    {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(180, 180, 180));
        label.setPreferredSize(new Dimension(100, 36));

        row.add(label);
        row.add(field);
        row.add(Box.createRigidArea(new Dimension(8, 0)));
        row.add(extra);
        return row;
    }

    private JRadioButton styledRadio(String text)
    {
        JRadioButton btn = new JRadioButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(new Color(200, 200, 200));
        btn.setBackground(CommonConstants.PRIMARY_COLOR);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}