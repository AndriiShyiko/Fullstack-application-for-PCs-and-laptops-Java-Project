package ans;

import javax.swing.*;
import java.awt.*;

public class CompanyInfoPage extends MainFrame
{
    public CompanyInfoPage()
    {
        super("Blade Forge Shop — Our Story");
        setLayout(new BorderLayout());
        addGuiComponents();
    }

    private void addGuiComponents()
    {
        // Sidebar
        add(new Sidebar(), BorderLayout.WEST);

        // Scrollable main content
        JPanel contentArea = new JPanel();
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));
        contentArea.setBackground(CommonConstants.PRIMARY_COLOR);
        contentArea.setBorder(BorderFactory.createEmptyBorder(40, 60, 60, 60));

        // Heading: forge name + founding year
        JLabel forgeTitle = new JLabel("鍛冶屋 — Blade Forge");
        forgeTitle.setFont(new Font("Serif", Font.BOLD, 38));
        forgeTitle.setForeground(CommonConstants.TEXT_COLOR);
        forgeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel foundedLabel = new JLabel("Est. 1578  ·  Mino Province, Japan");
        foundedLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        foundedLabel.setForeground(new Color(180, 150, 80));
        foundedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        foundedLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        contentArea.add(forgeTitle);
        contentArea.add(foundedLabel);
        contentArea.add(Box.createRigidArea(new Dimension(0, 24)));
        contentArea.add(goldDivider());
        contentArea.add(Box.createRigidArea(new Dimension(0, 32)));

        // Master Blacksmith section
        contentArea.add(sectionHeading("  The Master"));
        contentArea.add(Box.createRigidArea(new Dimension(0, 14)));

        contentArea.add(bodyText
        (
            "Master Blacksmith:   Takenobu Moriuji"
        ));
        contentArea.add(Box.createRigidArea(new Dimension(0, 8)));

        contentArea.add(bodyText
        (
            "Contact Number:      +81-3-0000-1578   (Tokyo Studio)"
        ));
        contentArea.add(Box.createRigidArea(new Dimension(0, 8)));

        contentArea.add(bodyText
        (
            "Studio Address:      2-14 Kajiya-cho, Seki City, Gifu Prefecture, Japan"
        ));
        contentArea.add(Box.createRigidArea(new Dimension(0, 32)));
        contentArea.add(goldDivider());
        contentArea.add(Box.createRigidArea(new Dimension(0, 32)));

        // History section
        contentArea.add(sectionHeading("  Our History"));
        contentArea.add(Box.createRigidArea(new Dimension(0, 14)));

        contentArea.add(bodyTextWrapped
        (
            "In the third year of the Tenshō era - 1578 - a young swordsmith named " +
            "Moriuji Takenobu set up his first forge in the mountains of Mino Province, " +
            "a region already renowned across feudal Japan for the exceptional quality of " +
            "its tamahagane steel. Trained under the legendary Seki school, Takenobu " +
            "developed a folding technique that reduced impurities to a degree that made " +
            "his blades both harder and more resilient than any produced in the region at " +
            "the time. His work quickly drew the attention of local daimyo, and within a " +
            "decade his forge had become one of the most respected in Mino."
        ));
        contentArea.add(Box.createRigidArea(new Dimension(0, 16)));

        contentArea.add(bodyTextWrapped
        (
            "For over four centuries, the Moriuji lineage has passed the craft from " +
            "father to son - never to outsiders, never from a book. Each generation has " +
            "refined the original technique while preserving the core philosophy: a blade " +
            "is not merely a weapon, it is the visible form of the maker's discipline. " +
            "The current master, the fourteenth in the line, continues to work the same " +
            "river clay and charcoal methods used in 1578, in a studio built on the " +
            "foundations of the original forge."
        ));
        contentArea.add(Box.createRigidArea(new Dimension(0, 32)));
        contentArea.add(goldDivider());
        contentArea.add(Box.createRigidArea(new Dimension(0, 32)));

        // About the Shop section
        contentArea.add(sectionHeading("  About the Shop"));
        contentArea.add(Box.createRigidArea(new Dimension(0, 14)));

        contentArea.add(bodyTextWrapped
        (
            "Blade Forge Shop is the official online presence of the Moriuji forge, " +
            "bringing authentic hand-crafted blades to collectors and enthusiasts " +
            "worldwide. Every item in the catalogue is produced by hand at the Seki " +
            "City studio using traditional techniques - no machinery, no shortcuts. " +
            "Each piece is unique and ships with a certificate of authenticity signed " +
            "by Master Takenobu himself."
        ));
        contentArea.add(Box.createRigidArea(new Dimension(0, 16)));

        contentArea.add(bodyTextWrapped
        (
            "For those who wish to go further, we offer a limited number of " +
            "Blacksmith Lesson bookings each season. Under the direct guidance of the " +
            "master, guests spend a full day at the forge learning the ancient techniques " +
            "of tamahagane smelting, clay coating, and blade shaping - and leave with a " +
            "small knife they have forged themselves. Places are strictly limited and " +
            "must be purchased through the catalogue."
        ));
        contentArea.add(Box.createRigidArea(new Dimension(0, 32)));
        contentArea.add(goldDivider());
        contentArea.add(Box.createRigidArea(new Dimension(0, 28)));

        // Footer note
        JLabel footerNote = new JLabel
        (
            "All products are made for adults only. Valid ID may be required upon delivery."
        );
        footerNote.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footerNote.setForeground(new Color(100, 100, 100));
        footerNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentArea.add(footerNote);

        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(null);
        scrollPane.setBackground(CommonConstants.PRIMARY_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    // UI helpers

    // bold gold section heading with glyph included in label
    private JLabel sectionHeading(String text)
    {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 17));
        label.setForeground(CommonConstants.TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // single line body text used for name, phone, address rows
    private JLabel bodyText(String text)
    {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Courier New", Font.PLAIN, 14));
        label.setForeground(new Color(210, 210, 210));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // multi line wrapped paragraph JTextArea styled as a label
    private JTextArea bodyTextWrapped(String text)
    {
        JTextArea area = new JTextArea(text);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setForeground(new Color(200, 200, 200));
        area.setBackground(CommonConstants.PRIMARY_COLOR);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setFocusable(false);
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        area.setMaximumSize(new Dimension(820, Integer.MAX_VALUE));
        return area;
    }

    // decorative gold divider with ⚒ glyph centred
    private JPanel goldDivider()
    {
        JPanel divider = new JPanel(new BorderLayout(10, 0));
        divider.setOpaque(false);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator leftLine = new JSeparator();
        leftLine.setForeground(new Color(200, 160, 80, 80));

        JLabel glyph = new JLabel("⚒");
        glyph.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        glyph.setForeground(new Color(200, 160, 80));

        JSeparator rightLine = new JSeparator();
        rightLine.setForeground(new Color(200, 160, 80, 80));

        divider.add(leftLine, BorderLayout.WEST);
        divider.add(glyph, BorderLayout.CENTER);
        divider.add(rightLine, BorderLayout.EAST);

        return divider;
    }
}
