package UserPage;

import AdminPage.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class BillingManager {
    private DefaultTableModel billTableModel;
    private DefaultTableModel productTableModel;
    private JTable billTable;
    private JTable productTable;
    private JTextField customerNameField;
    private JTextField customerPhoneField;
    private JLabel totalAmountLabel;
    private JTextArea billPreviewArea;
    private double totalAmount = 0.0;
    private int billNumber = 1;
    private BillingPanel billingPanel;

    private ManageProduct manageProduct; // store reference

    // Constructor now takes BillingPanel as parameter to access its components
    public BillingManager(BillingPanel panel,ManageProduct manageProduct) {
        this.billingPanel = panel;
        this.manageProduct = manageProduct;
        // Initialize references to components from BillingPanel
        initializeReferences();
        loadProductsFromManageProduct();

    }

    // Initialize references to components from BillingPanel
    private void initializeReferences() {
        this.billTableModel = billingPanel.getBillTableModel();
        this.productTableModel = billingPanel.getProductTableModel();
        this.billTable = billingPanel.getBillTable();
        this.productTable = billingPanel.getProductTable();
        this.customerNameField = billingPanel.getCustomerNameField();
        this.customerPhoneField = billingPanel.getCustomerPhoneField();
        this.totalAmountLabel = billingPanel.getTotalAmountLabel();
    }


    public void loadProductsFromManageProduct() {
        productTableModel.setRowCount(0);

        for (Product p : manageProduct.getAllProducts()) {
            productTableModel.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getCategory(),
                    String.valueOf(p.getPrice()),
                    String.valueOf(p.getQuantity())
            });
        }
    }

    void searchProduct(String searchTerm) {
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(billingPanel, "Please enter a search term!", "Search Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Simple search implementation
        for (int i = 0; i < productTableModel.getRowCount(); i++) {
            String code = (String) productTableModel.getValueAt(i, 0);
            String name = (String) productTableModel.getValueAt(i, 1);

            if (code.toLowerCase().contains(searchTerm.toLowerCase()) ||
                    name.toLowerCase().contains(searchTerm.toLowerCase())) {
                productTable.setRowSelectionInterval(i, i);
                productTable.scrollRectToVisible(productTable.getCellRect(i, 0, true));
                return;
            }
        }

        JOptionPane.showMessageDialog(billingPanel, "Product not found!", "Search Result",
                JOptionPane.INFORMATION_MESSAGE);
    }

    void printBill() {
        // Create bill preview dialog
        JDialog printDialog = new JDialog(billingPanel, "Bill Preview", true);
        printDialog.setSize(400, 600);
        printDialog.setLocationRelativeTo(billingPanel);

        billPreviewArea = new JTextArea();
        billPreviewArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        billPreviewArea.setEditable(false);

        StringBuilder bill = new StringBuilder();
        bill.append("=====================================\n");
        bill.append("        SUPERMARKET BILLING         \n");
        bill.append("=====================================\n");
        bill.append("Bill No: ").append(billNumber).append("\n");
        bill.append("Date: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())).append("\n");
        bill.append("Customer: ").append(customerNameField.getText()).append("\n");
        bill.append("Phone: ").append(customerPhoneField.getText()).append("\n");
        bill.append("=====================================\n");
        bill.append("Item                Qty   Price  Total\n");
        bill.append("=====================================\n");

        for (int i = 0; i < billTableModel.getRowCount(); i++) {
            String item = (String) billTableModel.getValueAt(i, 0);
            String qty = (String) billTableModel.getValueAt(i, 1);
            String price = (String) billTableModel.getValueAt(i, 2);
            String total = (String) billTableModel.getValueAt(i, 3);

            bill.append(String.format("%-15s %5s %8s %8s\n",
                    item.length() > 15 ? item.substring(0, 15) : item, qty, price, total));
        }

        bill.append("=====================================\n");
        bill.append("Total Amount: $").append(totalAmountLabel.getText()).append("\n");
        bill.append("=====================================\n");
        bill.append("     Thank you for shopping!        \n");
        bill.append("=====================================\n");

        billPreviewArea.setText(bill.toString());

        JScrollPane scrollPane = new JScrollPane(billPreviewArea);
        printDialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton actualPrintButton = new JButton("Print");
        JButton closeButton = new JButton("Close");

        actualPrintButton.setBackground(new Color(46, 125, 50));
        actualPrintButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(158, 158, 158));
        closeButton.setForeground(Color.WHITE);

        buttonPanel.add(actualPrintButton);
        buttonPanel.add(closeButton);

        printDialog.add(buttonPanel, BorderLayout.SOUTH);

        actualPrintButton.addActionListener(e -> {
            try {
                billPreviewArea.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(printDialog, "Error printing: " + ex.getMessage());
            }
        });

        closeButton.addActionListener(e -> printDialog.dispose());

        printDialog.setVisible(true);
    }

    void saveBill() {
        if (billTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(billingPanel, "No items in the bill to save!", "Empty Bill",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String customerName = customerNameField.getText().trim();
        String customerPhone = customerPhoneField.getText().trim();

        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(billingPanel, "Please enter customer name!", "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Generate customer ID (could be UUID or just billNumber)
            String customerId = "-" + billNumber;

            // Save only one record for the bill
            DBHelper.savePurchase(
                    customerName,
                    customerPhone,
                    totalAmount
            );

            JOptionPane.showMessageDialog(billingPanel,
                    "Bill #" + billNumber + " saved successfully!\nTotal Amount: $" + totalAmountLabel.getText(),
                    "Bill Saved",
                    JOptionPane.INFORMATION_MESSAGE);

            billNumber++;
            billingPanel.updateBillNumber(billNumber);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(billingPanel, "Error saving bill: " + e.getMessage(),
                    "Data Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    void clearBill() {
        billTableModel.setRowCount(0);
        totalAmount = 0.0;
        totalAmountLabel.setText("0.00");
        customerNameField.setText("");
        customerPhoneField.setText("");
        JOptionPane.showMessageDialog(billingPanel, "Bill cleared successfully!", "Bill Cleared",
                JOptionPane.INFORMATION_MESSAGE);
    }

    void newBill() {
        clearBill();
        billNumber++;
        billingPanel.updateBillNumber(billNumber);
        JOptionPane.showMessageDialog(billingPanel, "New bill created. Bill No: " + billNumber, "New Bill",
                JOptionPane.INFORMATION_MESSAGE);
    }

    void addToBill() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            String code = (String) productTableModel.getValueAt(selectedRow, 0);
            String name = (String) productTableModel.getValueAt(selectedRow, 1);
            String price = (String) productTableModel.getValueAt(selectedRow, 3);
            String availableStock = (String) productTableModel.getValueAt(selectedRow, 4);

            // Check if item already exists in bill
            for (int i = 0; i < billTableModel.getRowCount(); i++) {
                if (name.equals(billTableModel.getValueAt(i, 0))) {
                    JOptionPane.showMessageDialog(billingPanel,
                            "Item already added to bill! Please modify quantity if needed.",
                            "Duplicate Item", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            String qty = JOptionPane.showInputDialog(billingPanel,
                    "Enter quantity for " + name + ":\n(Available Stock: " + availableStock + ")",
                    "Quantity Input",
                    JOptionPane.QUESTION_MESSAGE);

            if (qty != null && !qty.trim().isEmpty()) {
                try {
                    int quantity = Integer.parseInt(qty);
                    int stock = Integer.parseInt(availableStock);

                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(billingPanel, "Quantity must be greater than 0!",
                                "Invalid Quantity",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (quantity > stock) {
                        JOptionPane.showMessageDialog(billingPanel, "Insufficient stock! Available: " + stock,
                                "Stock Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double unitPrice = Double.parseDouble(price);
                    double total = quantity * unitPrice;

                    billTableModel.addRow(new Object[] { name, qty, price, String.format("%.2f", total) });
                    totalAmount += total;
                    totalAmountLabel.setText(String.format("%.2f", totalAmount));

                    // Update stock in product table
                    int newStock = stock - quantity;
                    productTableModel.setValueAt(String.valueOf(newStock), selectedRow, 4);

                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(billingPanel, "Please enter a valid number!", "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(billingPanel, "Please select a product to add!", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // Remove selected product from bill table and restore stock
    void removeProduct() {
        int selectedRow = billTable.getSelectedRow();
        if (selectedRow >= 0) {
            String itemName = (String) billTableModel.getValueAt(selectedRow, 0);
            String qtyStr = (String) billTableModel.getValueAt(selectedRow, 1);
            String totalStr = (String) billTableModel.getValueAt(selectedRow, 3);

            int confirm = JOptionPane.showConfirmDialog(billingPanel,
                    "Remove " + itemName + " from bill?",
                    "Confirm Remove",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                double itemTotal = Double.parseDouble(totalStr);
                int quantity = Integer.parseInt(qtyStr);

                // Update total amount
                totalAmount -= itemTotal;
                totalAmountLabel.setText(String.format("%.2f", totalAmount));

                // Restore stock in product table
                for (int i = 0; i < productTableModel.getRowCount(); i++) {
                    String productName = (String) productTableModel.getValueAt(i, 1);
                    if (productName.equals(itemName)) {
                        int currentStock = Integer.parseInt((String) productTableModel.getValueAt(i, 4));
                        productTableModel.setValueAt(String.valueOf(currentStock + quantity), i, 4);
                        break;
                    }
                }

                // Remove from bill
                billTableModel.removeRow(selectedRow);
            }
        } else {
            JOptionPane.showMessageDialog(billingPanel, "Please select a bill item to remove!", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    public void processPurchase(String customerName, String contact, double totalCost) {
        DBHelper.savePurchase(customerName, contact, totalCost);

        // Optionally update UI
        Object[] rowData = { customerName, contact, totalCost };
        billTableModel.addRow(rowData);
    }


}