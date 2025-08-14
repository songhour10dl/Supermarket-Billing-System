package AdminPage;

import com.formdev.flatlaf.ui.FlatBorder;
import com.formdev.flatlaf.ui.FlatRoundBorder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ManageProduct extends  JPanel {
    private static final String DB_URL = "jdbc:sqlite:products.db";
    private JLabel productID,productName,productPrice,productQuantity,productCategory;
    private JTextField fieldID,fieldName,fieldPrice,fieldQuantity, fieldCategory;
    private JPanel leftPanel,rightPanel, itemId,itemName,itemPrice,itemQuantity, itemCategory;
    private JButton addProduct, updateButton, deleteButton, clearButton, refreshButton;
    private JTable table;
    private DefaultTableModel model = new DefaultTableModel();
    private String id , name, priceText, qtyText, categoryText;

    private double price;
    private int quantity ; ;
    private List<Product> products = new ArrayList<>();

    public ManageProduct() {
        super();
        DatabaseHelper.createTableIfNotExists();
        loadProductsFromDatabase();
        productID = new JLabel("Product ID:       ");//10
        productName = new JLabel("Product Name:");
        productPrice = new JLabel("Price:                ");
        productQuantity = new JLabel("Quantity:          ");
        productCategory = new JLabel("Product Category:");

        productID.setFont(new Font("Sans Serif", Font.BOLD, 14)); //test
        productName.setFont(new Font("Sans Serif", Font.BOLD, 14)); //test
        productPrice.setFont(new Font("Sans Serif", Font.BOLD, 14)); //test
        productQuantity.setFont(new Font("Sans Serif", Font.BOLD, 14)); //test

        fieldID = new JTextField(15);
        fieldName = new JTextField(15);
        fieldPrice = new JTextField(15);
        fieldQuantity = new JTextField(15);
        fieldCategory = new JTextField(15);

        fieldID.putClientProperty("JComponent.roundRect", true); // optional, makes sure FlatLaf respects it
        fieldName.putClientProperty("JComponent.roundRect", true); // optional, makes sure FlatLaf respects it
        fieldPrice.putClientProperty("JComponent.roundRect", true); // optional, makes sure FlatLaf respects it
        fieldQuantity.putClientProperty("JComponent.roundRect", true); // optional, makes sure FlatLaf respects it
        fieldCategory.putClientProperty("JComponent.roundRect", true); // optional, makes sure FlatLaf respects it

        fieldID.setBorder(new FlatBorder());
        fieldID.setBorder(new FlatRoundBorder());
        fieldName.setBorder(new FlatRoundBorder());
        fieldPrice.setBorder(new FlatRoundBorder());
        fieldQuantity.setBorder(new FlatRoundBorder());
        fieldCategory.setBorder(new FlatRoundBorder());

        itemId = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemName = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemPrice = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemQuantity = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemCategory = new JPanel(new FlowLayout(FlowLayout.LEFT));

        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(300, 250));
        leftPanel.setBorder(BorderFactory.createLineBorder(new Color(170, 170, 170)));

        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(765, 250));
        rightPanel.setBorder(BorderFactory.createLineBorder(new Color(187, 187, 187, 255)));

        itemId.add(productID);
        itemId.add(fieldID);

        itemName.add(productName);
        itemName.add(fieldName);

        itemPrice.add(productPrice);
        itemPrice.add(fieldPrice);

        itemQuantity.add(productQuantity);
        itemQuantity.add(fieldQuantity);

        itemCategory.add(productCategory);
        itemCategory.add(fieldCategory);

        addProduct = new JButton("Add Product");
        addProduct.setBackground(new Color(255, 0, 0));
        updateButton = new JButton("Update Product");
        deleteButton = new JButton("Delete Product");
        clearButton = new JButton("Clear Fields");
        refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1 , 0, 25));
        buttonPanel.add(addProduct);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setFocusable(false);
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonWrapper.add(buttonPanel);

        String[] columnNames = {"Product ID", "Name", "Price", "Quantity", "Category"};


        model = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
            return false; // Makes all cells not editable
            }
        };
        table = new JTable(model);
        table.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
        JScrollPane scrollPane = new JScrollPane(table);

        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                fieldID.setText(model.getValueAt(selectedRow, 0).toString());
                fieldName.setText(model.getValueAt(selectedRow, 1).toString());
                fieldPrice.setText(model.getValueAt(selectedRow, 2).toString());
                fieldQuantity.setText(model.getValueAt(selectedRow, 3).toString());
                fieldCategory.setText(model.getValueAt(selectedRow, 4).toString());
            }
        });

        //BUTTs
        addProduct.addActionListener((ActionEvent e) -> {addProduct();refreshTable();});
        updateButton.addActionListener((ActionEvent e) -> {updateProduct();});
        deleteButton.addActionListener((ActionEvent e) -> {
            deleteProduct();
            loadDataFromDatabase();
            refreshButton.doClick();
        });

        clearButton.addActionListener((ActionEvent e) -> {clearFields();});
        refreshButton.addActionListener((ActionEvent e) -> {refreshTable() ;System.out.println(getAllProducts());});

        refreshButton.doClick(); // Simulates a click

        leftPanel.add(itemId);
        leftPanel.add(itemName);
        leftPanel.add(itemPrice);
        leftPanel.add(itemQuantity);
        leftPanel.add(itemCategory);
        leftPanel.add(buttonWrapper);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    public void addProduct() {
        id = fieldID.getText().trim();
        name = fieldName.getText().trim();
        priceText = fieldPrice.getText().trim();
        qtyText = fieldQuantity.getText().trim();
        categoryText = fieldCategory.getText().trim();


        if (id.isEmpty() || name.isEmpty() || priceText.isEmpty() || qtyText.isEmpty() || categoryText.isEmpty())  {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }

        try {
            price = Double.parseDouble(priceText);
            quantity = Integer.parseInt(qtyText);

            String sql = "INSERT INTO products(id, name, price, quantity, category) VALUES (?, ?, ?, ?,?)";
            try (Connection conn = DatabaseHelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, id);
                pstmt.setString(2, name);
                pstmt.setDouble(3, price);
                pstmt.setInt(4, quantity);
                pstmt.setString(5, categoryText);
                pstmt.executeUpdate();
            }

            Product product = new Product(id, name, price, quantity,categoryText);
            products.add(product);
            model.addRow(new Object[]{id, name, price, quantity,categoryText});

            clearFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a number, Quantity must be an integer");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error while adding product: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    public void updateProduct() {
        id = fieldID.getText().trim();
        name = fieldName.getText().trim();
        priceText = fieldPrice.getText().trim();
        qtyText = fieldQuantity.getText().trim();
        categoryText = fieldCategory.getText().trim();

        // Validate that all fields are filled
        if (id.isEmpty() || name.isEmpty() || priceText.isEmpty() || qtyText.isEmpty()|| categoryText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }

        try {
            price = Double.parseDouble(priceText);
            quantity = Integer.parseInt(qtyText);

            // Check if product exists in database first
            String checkSql = "SELECT COUNT(*) FROM products WHERE id = ?";
            try (Connection conn = DatabaseHelper.connect();
                 PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

                checkStmt.setString(1, id);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next() && rs.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(this, "Product with ID '" + id + "' not found!");
                    return;
                }
            }

            // Update in database
            String updateSql = "UPDATE products SET name = ?, price = ?, quantity = ? ,category=? WHERE id = ?";
            try (Connection conn = DatabaseHelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(updateSql)) {

                pstmt.setString(1, name);
                pstmt.setDouble(2, price);
                pstmt.setInt(3, quantity);
                pstmt.setString(4, categoryText);
                pstmt.setString(5, id);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    // Update in-memory products list
                    for (int i = 0; i < products.size(); i++) {
                        Product p = products.get(i);
                        if (p.getId().equals(id)) {
                            // Create new product with updated values
                            Product updatedProduct = new Product(id, name, price, quantity,categoryText);
                            products.set(i, updatedProduct);
                            break;
                        }
                    }

                    // Refresh the table
                    refreshTable();

                    // Clear fields after successful update
                    clearFields();

                    JOptionPane.showMessageDialog(this, "Product updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update product in database.");
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a number, Quantity must be an integer");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error while updating product: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void deleteProduct() {
        String id = fieldID.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter ID.");
            return;
        }

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM products WHERE id = ?")) {

            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Remove from the in-memory products list
                products.removeIf(product -> product.getId().equals(id));

                // Refresh the table
                refreshTable();

                JOptionPane.showMessageDialog(this, "Product deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Product not found in database.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
        public void clearFields(){
        fieldID.setText("");
        fieldName.setText("");
        fieldPrice.setText("");
        fieldQuantity.setText("");
        fieldCategory.setText("");
    }


    public void loadProductsFromDatabase() {
//        products.clear();
        model.setRowCount(0); // Clear tabletable

        String sql = "SELECT * FROM products";
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                String category = rs.getString("category");

                Product p = new Product(id, name, price, quantity,category);
                products.add(p);
                model.addRow(new Object[]{id, name, price, quantity,category});
            }
            System.out.println("Loaded " + products.size() + " products from database.");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void loadDataFromDatabase() {
        model.setRowCount(0);
        products.clear();

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                String category = rs.getString("category");

                Product p = new Product(id, name, price, quantity,category);
                products.add(p);
            }

            // Refresh table after loading
            refreshTable();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load data: " + e.getMessage());
        }
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Product product : getAllProducts()) {
            Object[] row = {
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getQuantity(),
                    product.getCategory()
            };
            model.addRow(row);
        }
    }
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }
}


