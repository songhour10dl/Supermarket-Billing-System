package AdminPage;

import UserPage.BillingPanel;
import UserPage.DBHelper;
import com.formdev.flatlaf.ui.FlatRoundBorder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static AdminPage.DatabaseHelper.connect;

public class ManageBill extends JPanel {
    private JLabel CustomerIDLabel, CustomerNameLabel, CustomerPhoneLabel, TotalAmountLabel;
    private JTextField CustomerIDTextField, CustomerNameTextField, CustomerPhoneTextField, TotalAmountTextField;
    private JButton deleteBillButton, UpdateBillButton, RefreshButton, LoadFromBillButton;
    private JPanel bottomPanel, topPanel, IDPanel, NamePanel, PhonePanel, TotalAmountpanel;
    private DefaultTableModel model = new DefaultTableModel();
    private DefaultTableModel productTableModel;
    private BillingPanel billingPanel;
    private JTable table;

    public ManageBill() {
        initializeComponents();
        loadBillData(); // Load data when component is created
    }

    // Constructor that accepts BillingPanel reference
    public ManageBill(BillingPanel billingPanel) {
        this.billingPanel = billingPanel;
        initializeComponents();
        loadBillData(); // Load data when component is created
    }

    // Setter method to set BillingPanel reference
    public void setBillingPanel(BillingPanel billingPanel) {
        this.billingPanel = billingPanel;
    }

    private void initializeComponents() {
        CustomerIDLabel = new JLabel("Customer ID:");
        CustomerNameLabel = new JLabel("Customer Name:");
        CustomerPhoneLabel = new JLabel("Customer Phone:");
        TotalAmountLabel = new JLabel("Customer Total Amount:");

        CustomerIDLabel.setFont(new Font("Sans Serif", Font.BOLD, 14));
        CustomerNameLabel.setFont(new Font("Sans Serif", Font.BOLD, 14));
        CustomerPhoneLabel.setFont(new Font("Sans Serif", Font.BOLD, 14));
        TotalAmountLabel.setFont(new Font("Sans Serif", Font.BOLD, 14));

        CustomerIDTextField = new JTextField(15);
        CustomerNameTextField = new JTextField(15);
        CustomerPhoneTextField = new JTextField(15);
        TotalAmountTextField = new JTextField(15);

        CustomerIDTextField.putClientProperty("JComponent.roundRect", true);
        CustomerNameTextField.putClientProperty("JComponent.roundRect", true);
        CustomerPhoneTextField.putClientProperty("JComponent.roundRect", true);
        TotalAmountTextField.putClientProperty("JComponent.roundRect", true);

        CustomerIDTextField.setBorder(new FlatRoundBorder());
        CustomerNameTextField.setBorder(new FlatRoundBorder());
        CustomerPhoneTextField.setBorder(new FlatRoundBorder());
        TotalAmountTextField.setBorder(new FlatRoundBorder());

        IDPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        NamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        PhonePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        TotalAmountpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setPreferredSize(new Dimension(300, 250));
        bottomPanel.setBorder(BorderFactory.createLineBorder(new Color(170, 170, 170)));

        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setPreferredSize(new Dimension(765, 590));
        topPanel.setBorder(BorderFactory.createLineBorder(new Color(187, 187, 187, 255)));

        IDPanel.add(CustomerIDLabel);
        IDPanel.add(CustomerIDTextField);

        NamePanel.add(CustomerNameLabel);
        NamePanel.add(CustomerNameTextField);

        PhonePanel.add(CustomerPhoneLabel);
        PhonePanel.add(CustomerPhoneTextField);

        TotalAmountpanel.add(TotalAmountLabel);
        TotalAmountpanel.add(TotalAmountTextField);

        // Buttons
        deleteBillButton = new JButton("Delete");
        UpdateBillButton = new JButton("Update");
        RefreshButton = new JButton("Refresh");
        LoadFromBillButton = new JButton("Load from Bill");

        deleteBillButton.setPreferredSize(new Dimension(100, 30));
        UpdateBillButton.setPreferredSize(new Dimension(100, 30));
        RefreshButton.setPreferredSize(new Dimension(100, 30));
        LoadFromBillButton.setPreferredSize(new Dimension(120, 30));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        buttonPanel.add(deleteBillButton);
        buttonPanel.add(UpdateBillButton);
        buttonPanel.add(RefreshButton);
        buttonPanel.add(LoadFromBillButton);

        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.setFocusable(false);
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonWrapper.add(buttonPanel);

        String[] columnNames = {"Customer ID", "Customer Name", "Customer Contact", "Customer Total Amount"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
//        table.setPreferredSize(new Dimension(765, 800));
        JScrollPane scrollPane = new JScrollPane(table);

        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                CustomerIDTextField.setText(model.getValueAt(selectedRow, 0).toString());
                CustomerNameTextField.setText(model.getValueAt(selectedRow, 1).toString());
                CustomerPhoneTextField.setText(model.getValueAt(selectedRow, 2).toString());
                TotalAmountTextField.setText(model.getValueAt(selectedRow, 3).toString());
            }
        });

        // Button functions
        deleteBillButton.addActionListener(e -> deleteBill());
        UpdateBillButton.addActionListener(e -> updateBill());
        RefreshButton.addActionListener(e -> refreshTable());
        LoadFromBillButton.addActionListener(e -> loadBillData());

        bottomPanel.add(IDPanel);
        bottomPanel.add(NamePanel);
        bottomPanel.add(PhonePanel);
        bottomPanel.add(TotalAmountpanel);
        bottomPanel.add(buttonWrapper);
        topPanel.add(scrollPane, BorderLayout.CENTER);
        setLayout(new BorderLayout());

        add(bottomPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
    }

    // Load bill data from database
    private void loadBillData() {
        model.setRowCount(0); // Clear existing data

        String query = "SELECT rowid, customer_name, contact, total_amount FROM purchases ORDER BY timestamp DESC";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                String customerId = "-ID" + rs.getInt("rowid"); // Generate customer ID from rowid
                String customerName = rs.getString("customer_name");
                String customerContact = rs.getString("contact");
                double totalAmount = rs.getDouble("total_amount");

                model.addRow(new Object[]{
                        customerId,
                        customerName,
                        customerContact,
                        String.format("%.2f", totalAmount)
                });
            }

            JOptionPane.showMessageDialog(this,
                    "Loaded " + rowCount + " bills from database",
                    "Data Loaded",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading bill data: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteBill() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String customerName = model.getValueAt(selectedRow, 1).toString();
            String customerContact = model.getValueAt(selectedRow, 2).toString();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete the bill for " + customerName + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String query = "DELETE FROM purchases WHERE customer_name = ? AND contact = ?";

                try (Connection conn = DBHelper.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(query)) {

                    pstmt.setString(1, customerName);
                    pstmt.setString(2, customerContact);

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        model.removeRow(selectedRow);
                        clearFields();
                        JOptionPane.showMessageDialog(this,
                                "Bill deleted successfully!",
                                "Delete Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                            "Error deleting bill: " + e.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select a bill to delete!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateBill() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String oldCustomerName = model.getValueAt(selectedRow, 1).toString();
            String oldCustomerContact = model.getValueAt(selectedRow, 2).toString();

            String newCustomerName = CustomerNameTextField.getText().trim();
            String newCustomerContact = CustomerPhoneTextField.getText().trim();
            String totalAmountStr = TotalAmountTextField.getText().trim();

            if (newCustomerName.isEmpty() || totalAmountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all required fields!",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double newTotalAmount = Double.parseDouble(totalAmountStr);

                String query = "UPDATE purchases SET customer_name = ?, contact = ?, total_amount = ? WHERE customer_name = ? AND contact = ?";

                try (Connection conn = DBHelper.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(query)) {

                    pstmt.setString(1, newCustomerName);
                    pstmt.setString(2, newCustomerContact);
                    pstmt.setDouble(3, newTotalAmount);
                    pstmt.setString(4, oldCustomerName);
                    pstmt.setString(5, oldCustomerContact);

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        model.setValueAt(newCustomerName, selectedRow, 1);
                        model.setValueAt(newCustomerContact, selectedRow, 2);
                        model.setValueAt(String.format("%.2f", newTotalAmount), selectedRow, 3);

                        JOptionPane.showMessageDialog(this,
                                "Bill updated successfully!",
                                "Update Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                            "Error updating bill: " + e.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid total amount!",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select a bill to update!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refreshTable() {
        loadBillData(); // Reload data from database
        clearFields();
        table.repaint();
        table.revalidate();
    }

    private void clearFields() {
        CustomerIDTextField.setText("");
        CustomerNameTextField.setText("");
        CustomerPhoneTextField.setText("");
        TotalAmountTextField.setText("");
    }
}