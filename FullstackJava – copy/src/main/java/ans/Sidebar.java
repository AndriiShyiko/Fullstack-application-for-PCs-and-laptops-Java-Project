package ans;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Sidebar extends JPanel 
{
    public Sidebar() 
    {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 0));
        setBackground(new Color(18, 18, 18)); // slightly lifted from pure black

        // set the shop title on top 
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(new Color(18, 18, 18));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(24, 16, 20, 16));

        JLabel logoLabel = new JLabel("Blade Forge Shop");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        logoLabel.setForeground(CommonConstants.BORDER_ORANGE);
         
        logoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoLabel.setToolTipText("Click for Catalog Page");
        logoLabel.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseClicked(MouseEvent e) 
            {
                // dispose of this GUI
                SwingUtilities.getWindowAncestor(logoLabel).dispose();

                // launch catalog GUI
                new ProductCatalog().setVisible(true);
            }
            @Override
            public void mouseEntered(MouseEvent e) 
            {
                logoLabel.setForeground(new Color(230, 195, 120)); // change on hover
            }
            @Override
            public void mouseExited(MouseEvent e) 
            {
                logoLabel.setForeground(CommonConstants.BORDER_ORANGE); // restore gold accent on exit
            }
        });
        logoPanel.add(logoLabel);

        add(logoPanel, BorderLayout.NORTH);

        // center navigation bar items
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(18, 18, 18));
        navPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        navPanel.add(createNavButton("Settings"));
        navPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        navPanel.add(createNavButton("My Bookings"));
        navPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        navPanel.add(createNavButton("i  Our Story"));
        navPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        navPanel.add(createNavButton("Logout"));

        add(navPanel, BorderLayout.CENTER);

        // make a thin gold border divider on the right
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, CommonConstants.BORDER_ORANGE));
    }

    private JButton createNavButton(String label) 
    {
        JButton btn = new JButton(label) 
        {
            @Override
            protected void paintComponent(Graphics g) 
            {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) 
                {
                    g2.setColor(new Color(200, 160, 80, 30)); // gold hover
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(new Color(200, 200, 200));
        btn.setBackground(null);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        btn.addMouseListener(new MouseAdapter() 
        {
            @Override public void mouseEntered(MouseEvent e) { btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.repaint(); }
        });

        // Wire up actions
        btn.addActionListener(e -> 
        {
            String text = label.trim();
            if (text.contains("Logout")) 
            {
                int choice = JOptionPane.showConfirmDialog
                (
                    this,
                    "Are you sure you want to log out?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
                );
                if (choice == JOptionPane.YES_OPTION) 
                {
                    // dispose of this GUI
                    SwingUtilities.getWindowAncestor(this).dispose();

                    // launch the new GUI
                    new LoginFormGUI().setVisible(true);
                }
            } 
            else if (text.contains("Settings")) 
            {
                // dispose of this GUI
                SwingUtilities.getWindowAncestor(this).dispose();

                // launch the new GUI
                new Settings().setVisible(true);
            }
            else if (text.contains("My Bookings")) 
            {
                // dispose of this GUI
                SwingUtilities.getWindowAncestor(this).dispose();

                // launch the new GUI
                new MyBookingsPage().setVisible(true);
            }
            else if (text.contains("i  Our Story")) // company info
            {
                // dispose of this GUI
                SwingUtilities.getWindowAncestor(this).dispose();

                // launch the new GUI
                new CompanyInfoPage().setVisible(true);
            }
        });

        return btn;
    }
}