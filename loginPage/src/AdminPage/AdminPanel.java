package AdminPage;

import UserPage.BillingPanel;
import UserPage.MainBilling;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends ManageProduct{
      ManageProduct manageProduct = new ManageProduct();
      public AdminPanel() {
        JFrame frame = new JFrame("Admin Panel");
        frame.setSize(1200,990);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
//        frame.setResizable(false);
//        frame.setLayout(new GridLayout(0,2));

        try {
          UIManager.setLookAndFeel(new FlatIntelliJLaf());
          UIManager.put( "Button.arc", 999 );
          UIManager.put( "Component.arc", 999 );
          UIManager.put( "ProgressBar.arc", 999 );
          UIManager.put( "TextComponent.arc", 999 );

          UIManager.put( "Button.arc", 999 );
          UIManager.put( "Component.arc", 999 );
          UIManager.put( "ProgressBar.arc", 999 );
          UIManager.put( "TextComponent.arc", 999 );
          UIManager.put("Table.selectionBackground", new Color(0xCB23C8F1, true)); // selected row bg
          UIManager.put("Table.selectionForeground", Color.BLACK);       // selected row fg
          UIManager.put("Table.gridColor", new Color(0x5EA7FF));         // grid line color

          UIManager.put("TabbedPane.tabArc", 999);
          UIManager.put("TabbedPane.contentAreaArc", 15);
          UIManager.put("TabbedPane.tabInsets", new Insets(10, 20, 10, 20));
          UIManager.put("TabbedPane.selectedBackground", new Color(0xE0E0E0));
          UIManager.put("TabbedPane.background", new Color(0xF5F5F5));
          UIManager.put("TabbedPane.foreground", Color.DARK_GRAY);
          UIManager.put("TabbedPane.selectedForeground", Color.BLACK);
          UIManager.put("TabbedPane.tabSeparatorColor", new Color(0xCCCCCC));
          UIManager.put("TabbedPane.contentBorderColor", new Color(0xDDDDDD));
          UIManager.put("TabbedPane.minimumTabWidth", 100);

        } catch (Exception ex) {
          System.err.println("Failed to initialize LaF");
        }

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Manage Product",  new ManageProduct());
        tabbedPane.addTab("Manage Billing ", new ManageBill());
       tabbedPane.setTabPlacement(JTabbedPane.LEFT);

        frame.add(tabbedPane);
        frame.setVisible(true);







    }
}
