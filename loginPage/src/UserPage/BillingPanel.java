package UserPage;

import AdminPage.ManageProduct;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BillingPanel extends JFrame {
    private DefaultTableModel billTableModel;
    private DefaultTableModel productTableModel;
    private JTable billTable;
    private JTable productTable;
    private JTextField customerNameField;
    private JTextField customerPhoneField;
    private JTextField productSearchField;
    private JLabel totalAmountLabel;
    private JLabel billNoLabel;
    private int billNumber = 1;
    private BillingManager billingManager;

    public BillingPanel() {
        initializeGUI();
        billingManager = new BillingManager(this,new ManageProduct());
    }

    // Getter methods for BillingManager to access components
    public DefaultTableModel getBillTableModel() {
        return billTableModel;
    }

    public DefaultTableModel getProductTableModel() {
        return productTableModel;
    }

    public JTable getBillTable() {
        return billTable;
    }

    public JTable getProductTable() {
        return productTable;
    }

    public JTextField getCustomerNameField() {
        return customerNameField;
    }

    public JTextField getCustomerPhoneField() {
        return customerPhoneField;
    }

    public JLabel getTotalAmountLabel() {
        return totalAmountLabel;
    }

    public void updateBillNumber(int newBillNumber) {
        this.billNumber = newBillNumber;
        billNoLabel.setText(String.valueOf(billNumber));
    }

    public void initializeGUI() {
        setTitle("Supermarket Billing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Create main billing panel
        add(createBillingPanel());

        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createBillingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top panel - Customer info and date
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        topPanel.setBorder(BorderFactory.createTitledBorder("Bill Information"));

        gbc.insets = new Insets(5, 5, 5, 5);

        // Bill number
        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("Bill No:"), gbc);
        gbc.gridx = 1;
        billNoLabel = new JLabel(String.valueOf(billNumber));
        billNoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(billNoLabel, gbc);

        // Date
        gbc.gridx = 2;
        gbc.gridy = 0;
        topPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 3;
        JLabel dateLabel = new JLabel(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        topPanel.add(dateLabel, gbc);

        // Customer name
        gbc.gridx = 0;
        gbc.gridy = 1;
        topPanel.add(new JLabel("Customer Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        customerNameField = new JTextField(20);
        topPanel.add(customerNameField, gbc);

        // Customer phone
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        topPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        customerPhoneField = new JTextField(20);
        topPanel.add(customerPhoneField, gbc);

        panel.add(topPanel, BorderLayout.NORTH);

        // Center panel - Product selection and bill items
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        // Left side - Product selection
        JPanel productPanel = new JPanel(new BorderLayout());
        productPanel.setBorder(BorderFactory.createTitledBorder("Product Selection"));

        // Product search
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(new JLabel("Search Product:"));
        productSearchField = new JTextField(15);
        searchPanel.add(productSearchField);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(25, 118, 210));
        searchButton.setForeground(Color.WHITE);
        searchPanel.add(searchButton);

        JButton clearSearchButton = new JButton("Clear");
        searchPanel.add(clearSearchButton);

        productPanel.add(searchPanel, BorderLayout.NORTH);

        // Product table
        String[] productColumns = { "Code", "Name", "Category", "Price", "Stock" };
        productTableModel = new DefaultTableModel(productColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        productTable = new JTable(productTableModel);
        productTable.setRowHeight(30);

        JScrollPane productScrollPane = new JScrollPane(productTable);
        productPanel.add(productScrollPane, BorderLayout.CENTER);

        centerPanel.add(productPanel);

        // Right side - Bill items
        JPanel billPanel = new JPanel(new BorderLayout());
        billPanel.setBorder(BorderFactory.createTitledBorder("Bill Items"));

        String[] billColumns = { "Item", "Qty", "Price", "Total" };
        billTableModel = new DefaultTableModel(billColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        billTable = new JTable(billTableModel);
        billTable.setRowHeight(30);

        JScrollPane billScrollPane = new JScrollPane(billTable);
        billPanel.add(billScrollPane, BorderLayout.CENTER);

        centerPanel.add(billPanel);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel - Total and actions
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Total amount panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(new JLabel("Total Amount: $"));
        totalAmountLabel = new JLabel("0.00");
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalAmountLabel.setForeground(Color.RED);
        totalPanel.add(totalAmountLabel);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton printButton = new JButton("Print Bill");
        JButton saveButton = new JButton("Save Bill");
        JButton clearButton = new JButton("Clear All");
        JButton newBillButton = new JButton("New Bill");
        JButton addToBillButton = new JButton("Add to Bill");
        JButton removeBillButton = new JButton("Remove");

        printButton.setBackground(new Color(33, 150, 243));
        printButton.setForeground(Color.WHITE);

        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.WHITE);

        clearButton.setBackground(new Color(244, 67, 54));
        clearButton.setForeground(Color.WHITE);

        newBillButton.setBackground(new Color(255, 193, 7));
        newBillButton.setForeground(Color.BLACK);

        addToBillButton.setBackground(new Color(0, 172, 105));
        addToBillButton.setForeground(Color.WHITE);

        removeBillButton.setBackground(new Color(229, 57, 53));
        removeBillButton.setForeground(Color.WHITE);

        buttonPanel.add(addToBillButton);
        buttonPanel.add(removeBillButton);
        buttonPanel.add(printButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(newBillButton);

        bottomPanel.add(totalPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Add action listeners (will be set after billingManager is initialized)
        addToBillButton.addActionListener(e -> {
            if (billingManager != null)
                billingManager.addToBill();
        });
        removeBillButton.addActionListener(e -> {
            if (billingManager != null)
                billingManager.removeProduct();
        });
        printButton.addActionListener(e -> {
            if (billingManager != null)
                billingManager.printBill();
        });
        saveButton.addActionListener(e -> {
            if (billingManager != null)
                billingManager.saveBill();
        });
        clearButton.addActionListener(e -> {
            if (billingManager != null)
                billingManager.clearBill();
        });
        newBillButton.addActionListener(e -> {
            if (billingManager != null)
                billingManager.newBill();
        });

        // Search functionality
        searchButton.addActionListener(e -> {
            if (billingManager != null)
                billingManager.searchProduct(productSearchField.getText().trim());
        });
        clearSearchButton.addActionListener(e -> {
            productSearchField.setText("");
            if (billingManager != null)
                billingManager.loadProductsFromManageProduct(); // Reload all products
        });

        return panel;
    }

}