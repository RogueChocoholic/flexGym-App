/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.FrameStorage;
import model.ModifyTables;
import model.MySQL;
import model.StockTableObject;
import model.Validation;
import raven.toast.Notifications;

public class AddNewStock extends javax.swing.JFrame {

    HashMap<String, String> supplierMap = new HashMap<>();
    HashMap<String, String> productMap = new HashMap<>();
    HashMap<String, String> sizeMap = new HashMap<>();

    Vector<StockTableObject> productVector = new Vector<>();

    protected JTextField getSupplierField() {
        return jTextField4;
    }

    protected JLabel getBrandLabel() {
        return jLabel15;
    }

    protected JLabel getCategoryLabel() {
        return jLabel14;
    }

    protected JTextField getProductField() {
        return jTextField1;
    }

    public AddNewStock() {
        initComponents();
        Notifications.getInstance().setJFrame(this);
        init();
    }

    private void init() {
        setExtendedState(AddNewStock.MAXIMIZED_BOTH);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/logo.png"))); // sets the icon
        jButton3.putClientProperty(FlatClientProperties.STYLE, "arc:50");
        jButton3.setBorderPainted(false);
        jButton2.putClientProperty(FlatClientProperties.STYLE, "arc:50");
        jButton2.setBorderPainted(false);
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());

        ModifyTables modifyTables = new ModifyTables(); // loads the object with the table modifier method
        modifyTables.modifyTables(jPanel4, jTable1, jScrollPane2, false);
        loadSizes();
    }

    private void addSizes() {
        String newSize = jTextField5.getText();
        boolean addStatus = true;

        if (newSize.isBlank()) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 3000l, "Please Include a size before inserting");

        } else if (productMap.get("pid") == null) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 3000l, "Please select a product before inserting");

        } else {

            try {
                ResultSet sizeSet = MySQL.executeSearch("SELECT * FROM `productsizes` WHERE `product_pid` = '" + productMap.get("pid") + "' ");
                while (sizeSet.next()) {
                    if (sizeSet.getString("size").equals(newSize)) {
                        addStatus = false;
                        Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 3000l, "Size Already Exists in the system!");
                    }
                }

                if (addStatus) {
                    MySQL.executeIUD("INSERT INTO `productsizes` (`size`,`product_pid`)"
                            + " VALUES ('" + newSize + "','" + productMap.get("pid") + "') ");
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, 3000l, "Size included in the system!");
                    loadSizes();
                    jTextField5.setText("");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void loadSizes() {
        Vector<String> vector = new Vector<>();
        sizeMap.clear();
        try {
            if (productMap.get("pid") == null) {
                DefaultComboBoxModel model = new DefaultComboBoxModel(vector);
                jComboBox1.setModel(model);
                jComboBox1.setEnabled(false);
            } else {
                ResultSet sizeSet = MySQL.executeSearch("SELECT * FROM `productsizes` WHERE `product_pid` = '" + productMap.get("pid") + "' ");
                jComboBox1.setEnabled(true);

                while (sizeSet.next()) {
                    vector.add(sizeSet.getString("size"));
                    sizeMap.put(sizeSet.getString("size"), sizeSet.getString("sizeID"));
                }
                jComboBox1.setModel(new DefaultComboBoxModel<>(vector));

            }
        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.WARNING, "Unable to load sizes", e);
        }
    }

    private String generateBarcode() {
        Date date = new Date();
        Random random = new Random();
        int random3Digit = 100 + random.nextInt(900);

        String empSuffix = "STK";

        String barCode = empSuffix + formatDate("yy", date) + formatDate("MM", date) + formatDate("dd", date) + formatDate("mm", date) + formatDate("HH", date) + formatDate("ss", date) + String.valueOf(random3Digit);

        try {

            ResultSet checkMemID = MySQL.executeSearch("SELECT * FROM `stock` WHERE `barcode` = '" + barCode + "' ");

            if (checkMemID.next()) {
                generateBarcode();
            }
        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.WARNING, "Unable to create barcode", e);
        }

        return barCode;
    }

    private String formatDate(String format, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jButton4 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jFormattedTextField2 = new javax.swing.JFormattedTextField();
        jFormattedTextField3 = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        datePicker1 = new com.github.lgooddatepicker.components.DatePicker();
        datePicker2 = new com.github.lgooddatepicker.components.DatePicker();
        jLabel10 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jTextField5 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jFormattedTextField4 = new javax.swing.JFormattedTextField();
        jButton12 = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jFormattedTextField5 = new javax.swing.JFormattedTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();

        jPopupMenu1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jMenuItem5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-edit-20.png"))); // NOI18N
        jMenuItem5.setText("Edit");
        jMenuItem5.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem5);

        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-delete-20.png"))); // NOI18N
        jMenuItem4.setText("Delete");
        jMenuItem4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem4);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add New Stock | FlexGym");
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setText("Add New Stock");
        jLabel1.setFont(new java.awt.Font("Poppins", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(46, 59, 78));

        jLabel11.setText("Supplier");
        jLabel11.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jTextField4.setEditable(false);
        jTextField4.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jButton6.setText("Select Supplier");
        jButton6.setBackground(new java.awt.Color(255, 160, 64));
        jButton6.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButton6.setForeground(new java.awt.Color(249, 249, 249));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addGap(346, 346, 346)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setText("Product");
        jLabel2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jTextField1.setEditable(false);
        jTextField1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jButton1.setText("Select Product");
        jButton1.setBackground(new java.awt.Color(255, 160, 64));
        jButton1.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(249, 249, 249));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Details");
        jLabel3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jTextField2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel4.setText("Size");
        jLabel4.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "kovi", "kovi21", "hi" }));
        jComboBox1.setEnabled(false);
        jComboBox1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel5.setText("Quantity");
        jLabel5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-minus-24.png"))); // NOI18N
        jButton2.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-plus-24.png"))); // NOI18N
        jButton3.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        jFormattedTextField1.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jFormattedTextField1.setText("0");
        jFormattedTextField1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jFormattedTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jFormattedTextField1KeyReleased(evt);
            }
        });

        jButton4.setText("Add Size");
        jButton4.setBackground(new java.awt.Color(255, 160, 64));
        jButton4.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButton4.setForeground(new java.awt.Color(249, 249, 249));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel6.setText("Buying Price");
        jLabel6.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel7.setText("Selling Price");
        jLabel7.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jFormattedTextField2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField2.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jFormattedTextField2.setText("0.00");
        jFormattedTextField2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jFormattedTextField3.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField3.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jFormattedTextField3.setText("0.00");
        jFormattedTextField3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel8.setText("MFD");
        jLabel8.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel9.setText("EXP");
        jLabel9.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        datePicker1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        datePicker2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel10.setText("Barcode");
        jLabel10.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N

        jTextField3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField3KeyReleased(evt);
            }
        });

        jButton5.setText("Generate Barcode");
        jButton5.setBackground(new java.awt.Color(255, 160, 64));
        jButton5.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButton5.setForeground(new java.awt.Color(249, 249, 249));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton7.setText("Add Stock");
        jButton7.setBackground(new java.awt.Color(255, 111, 0));
        jButton7.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton7.setForeground(new java.awt.Color(249, 249, 249));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(250, 250, 250));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Product", "Stock ID", "Brand", "Details", "Size", "Quantity", "Buying Price", "Selling Price", "MFD", "EXP", "Barcode"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setBackground(new java.awt.Color(250, 250, 250));
        jTable1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane2)
                .addGap(20, 20, 20))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );

        jButton9.setText("Clear All");
        jButton9.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setText("Scan BarCode");
        jButton10.setBackground(new java.awt.Color(255, 111, 0));
        jButton10.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButton10.setForeground(new java.awt.Color(249, 249, 249));
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jTextField5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel12.setText("Brand:");
        jLabel12.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N

        jLabel16.setText("Category:");
        jLabel16.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N

        jLabel15.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jButton11.setText("Edit Stock");
        jButton11.setBackground(new java.awt.Color(255, 160, 64));
        jButton11.setEnabled(false);
        jButton11.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton11.setForeground(new java.awt.Color(249, 249, 249));
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(17, 17, 17)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                                    .addComponent(jTextField1)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel16))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15)
                                    .addComponent(jLabel14))))
                        .addGap(39, 39, 39)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField3))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(21, 21, 21)
                                .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(35, 35, 35)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(45, 45, 45)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7))
                                .addGap(21, 21, 21)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jFormattedTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jFormattedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(datePicker2, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(datePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(48, 48, 48))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jFormattedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(datePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jLabel5))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(jFormattedTextField1, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(jButton3)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(jFormattedTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)
                        .addComponent(datePicker2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel10)
                                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel15))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton10)
                                    .addComponent(jButton5)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel14))))
                        .addGap(29, 29, 29))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton8.setText("Add to Stock");
        jButton8.setBackground(new java.awt.Color(255, 111, 0));
        jButton8.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton8.setForeground(new java.awt.Color(249, 249, 249));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel17.setText("Total : ");
        jLabel17.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N

        jFormattedTextField4.setEditable(false);
        jFormattedTextField4.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField4.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jFormattedTextField4.setText("0.00");
        jFormattedTextField4.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jButton12.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton12.setForeground(new java.awt.Color(255, 111, 0));
        jButton12.setText("Cancel");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel18.setText("Payment");

        jFormattedTextField5.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField5.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jFormattedTextField5.setText("0.00");
        jFormattedTextField5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jFormattedTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jFormattedTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17)
                        .addComponent(jFormattedTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(jFormattedTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(30, 30, 30))
        );

        jScrollPane1.setViewportView(jPanel1);

        jMenuBar1.setBackground(new java.awt.Color(255, 255, 255));

        jMenu1.setText("Window");

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-refresh-20.png"))); // NOI18N
        jMenuItem3.setText("Refresh");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-exit-20.png"))); // NOI18N
        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, 0));
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-fullscreen-20.png"))); // NOI18N
        jMenuItem2.setText("Exit Fullscreen");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1716, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 886, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jFormattedTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField1KeyReleased
        int quantity = Integer.parseInt(jFormattedTextField1.getText());
        try {
            if (quantity < 0) {
                jFormattedTextField1.setText("0");
            }

        } catch (Exception e) {
            e.printStackTrace();
            SplashScreen.exceptionRecords.log(Level.SEVERE, "cannot edit quantity in add new stock", e);
        }
    }//GEN-LAST:event_jFormattedTextField1KeyReleased

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        int quantity = Integer.parseInt(jFormattedTextField1.getText());
//        change this after database is connected
// check with the quantity in the stock and stuff
        quantity += 1;
        jFormattedTextField1.setText(String.valueOf(quantity));


    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int quantity = Integer.parseInt(jFormattedTextField1.getText());
        try {
            if (quantity > 0) {
                quantity -= 1;
                jFormattedTextField1.setText(String.valueOf(quantity));
            }

        } catch (Exception e) {
            e.printStackTrace();
            SplashScreen.exceptionRecords.log(Level.SEVERE, "cannot edit quantity in add new stock", e);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        refresh();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        this.dispose();
        FrameStorage.addMemberFrame = null;
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        if (jMenuItem2.getText().equals("Exit Fullscreen")) {
            jMenuItem2.setText("Fullscreen");
            this.dispose();
            this.setUndecorated(false);
            this.setVisible(true);

        } else {
            jMenuItem2.setText("Exit Fullscreen");
            //  this.dispose();
            this.dispose();
            this.setUndecorated(true);
            this.setVisible(true);
            setExtendedState(AddNewStock.MAXIMIZED_BOTH);

        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        FrameStorage.addNewStockFrame = null;
    }//GEN-LAST:event_formWindowClosed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        jTextField3.setText(generateBarcode());
        jTextField3.setEditable(false);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        jTextField3.setText("");
        jTextField3.setEditable(true);
        jTextField3.grabFocus();

    }//GEN-LAST:event_jButton10ActionPerformed

    private void jTextField3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextField3.setEditable(false);
            jTextField2.grabFocus();
        }
    }//GEN-LAST:event_jTextField3KeyReleased

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        SelectSupplier sSupplier = new SelectSupplier(this, true, this);
        sSupplier.setVisible(true);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        SelectProduct sProduct = new SelectProduct(this, true, this);
        sProduct.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        addSizes();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        refresh();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        String details = jTextField2.getText();
        int qty = Integer.parseInt(jFormattedTextField1.getText());
        String size = String.valueOf(jComboBox1.getSelectedItem());
        String barcode = jTextField3.getText();
        double buyingPrice = Double.parseDouble(jFormattedTextField2.getText());
        double sellingPrice = Double.parseDouble(jFormattedTextField3.getText());

        LocalDate mfd = datePicker1.getDate();
        LocalDate exp = datePicker2.getDate();

        String stockID = "";

        boolean mfdValidation = validateDate(String.valueOf(mfd));
        boolean expValidation = validateDate(String.valueOf(exp));

        int statusCount = 0;                // this adds one to every validation and proceeds with the process only if the count == the number equal to all the validations. Just testing if it works as i want. GO BACK TO OLD METHOD IF CONFUSED!!!!
        if (productMap.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Please select a product.");
        } else {
            statusCount++;
        }
        if (details.isBlank()) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Please enter stock details.");
        } else {
            statusCount++;
        }
        if (qty == 0) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Quantity should be higher to add to stock.");
        } else {
            statusCount++;
        }
        if (barcode.isBlank()) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Please scan or generate a barcode for the product.");
        } else {
            statusCount++;
        }
        if (size.equals("null")) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Please select a size for the product stock.");
        } else {
            statusCount++;
        }
        if (String.valueOf(mfd).equals("null") || String.valueOf(exp).equals("null")) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Date of manufacture and expiry should not be kept empty.");
        } else if (mfd.isAfter(exp)) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Manufactured date should be a date before the date of expiry.");
        } else {
            statusCount++;
        }
        if (statusCount >= 6) {

            if (buyingPrice == 0.00 && sellingPrice == 0.00) {
                int freePriceOption = JOptionPane.showConfirmDialog(this, "Buying and Selling prices are set to 0 making them free items."
                        + " Continue regardless?", "0 price items detected!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (freePriceOption == JOptionPane.YES_OPTION) {
                    statusCount++;
                }

            } else if (buyingPrice == 0.00 && sellingPrice != 0.00) {
                int charityPriceOption = JOptionPane.showConfirmDialog(this, "Buying price is set to 0."
                        + " Continue regardless?", "Dontations detected!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (charityPriceOption == JOptionPane.YES_OPTION) {
                    statusCount++;
                }

            } else if (sellingPrice == 0.00 && buyingPrice != 0.00) {
                int charityPriceOption = JOptionPane.showConfirmDialog(this, "Selling price is set to 0."
                        + " Continue regardless?", "Charity goods detected!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (charityPriceOption == JOptionPane.YES_OPTION) {
                    statusCount++;
                }

            } else if (buyingPrice >= sellingPrice) {
                int buyingPriceOption = JOptionPane.showConfirmDialog(this, "Buying price is set to equal or higher than the selling"
                        + " price. This would result in a loss of profit. Continue Regardless?", "Buying price is equal or higher than the selling price!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (buyingPriceOption == JOptionPane.YES_OPTION) {
                    statusCount++;
                }
            } else {
                statusCount++;
            }
        }
        if (statusCount == 7) {
            boolean duplicate = false;
            int duplicateRow = 0;
            for (StockTableObject stockTableObject : productVector) {
                if (stockTableObject.getPid().equals(productMap.get("pid"))
                        && stockTableObject.getDetails().equals(details)
                        && Double.parseDouble(stockTableObject.getBuyingPrice()) == buyingPrice
                        && Double.parseDouble(stockTableObject.getSellingPrice()) == sellingPrice
                        && mfd.isEqual(LocalDate.parse(stockTableObject.getMfd()))
                        && exp.isEqual(LocalDate.parse(stockTableObject.getExp()))
                        && size.equals(stockTableObject.getSize())) {
                    duplicate = true;
                    break;
                }
                duplicateRow++;
            }

            if (duplicate) {
                System.out.println(productVector.get(duplicateRow).getName());
                int newQty = qty + Integer.parseInt(productVector.get(duplicateRow).getQty());
                productVector.get(duplicateRow).setQty(String.valueOf(newQty));
                loadTable();
                refresh();
            } else {

                try {
                    ResultSet stockRS = MySQL.executeSearch("SELECT * FROM `stock` INNER JOIN `productsizes`"
                            + " ON `productsizes`.`sizeID` = `stock`.`stock_id` INNER JOIN `product` ON `productsizes`.`product_pid` "
                            + "= `product`.`pid` WHERE `stock`.`details` = '" + details + "' AND `productsizes`.`sizeID` = '" + sizeMap.get(size) + "' "
                            + "AND `stock`.`mfd` = '" + String.valueOf(mfd) + "' AND `stock`.`exp` = '" + String.valueOf(exp) + "' "
                            + " AND `price` = '" + String.valueOf(sellingPrice) + "' ");
                    if (stockRS.next()) {

                        stockID = stockRS.getString("stock_id");
                    } else {
                        stockID = generateBarcode();
                    }

                    StockTableObject stockElement = new StockTableObject();

                    stockElement.setPid(productMap.get("pid"));
                    stockElement.setName(productMap.get("name"));
                    stockElement.setStockID(stockID);
                    stockElement.setBrand(productMap.get("brand"));
                    stockElement.setDetails(details);
                    stockElement.setSize(size);
                    stockElement.setQty(String.valueOf(qty));
                    stockElement.setBuyingPrice(String.valueOf(buyingPrice));
                    stockElement.setSellingPrice(String.valueOf(sellingPrice));
                    stockElement.setMfd(String.valueOf(mfd));
                    stockElement.setExp(String.valueOf(exp));
                    stockElement.setBarcode(barcode);
                    stockElement.setCategory(productMap.get("category"));

                    productVector.add(stockElement);
                    loadTable();
                    refresh();
                } catch (Exception e) {
                    SplashScreen.exceptionRecords.log(Level.SEVERE, "Couldn't connect to db at add New stock frame. Database error.", e);
                    e.printStackTrace();
                    Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Connection failure. Please check your network connection and try again.");

                }
            }
        }

        System.out.println(statusCount);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (jTable1.getSelectedRowCount() == 1) {
            if (evt.getButton() == MouseEvent.BUTTON3) {
                jPopupMenu1.show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        int row = jTable1.getSelectedRow();
        productVector.remove(row);
        loadTable();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // updating the rows in the table in frame (NOT THE DATABASE) 
        if (jTable1.getSelectedRowCount() != 1) {
            Notifications.getInstance().show(Notifications.Type.INFO, Notifications.Location.TOP_CENTER, 3000l, "Invalid number of rows Selected. Please select one row at a time to edit.");
        } else {
            String details = jTextField2.getText();
            int qty = Integer.parseInt(jFormattedTextField1.getText());
            String size = String.valueOf(jComboBox1.getSelectedItem());
            String barcode = jTextField3.getText();
            double buyingPrice = Double.parseDouble(jFormattedTextField2.getText());
            double sellingPrice = Double.parseDouble(jFormattedTextField3.getText());

            LocalDate mfd = datePicker1.getDate();
            LocalDate exp = datePicker2.getDate();

            int row = jTable1.getSelectedRow();
            StockTableObject stockObject = productVector.get(row);

            String stockID = stockObject.getStockID();

            boolean mfdValidation = validateDate(String.valueOf(mfd));
            boolean expValidation = validateDate(String.valueOf(exp));

            int statusCount = 0;                // this adds one to every validation and proceeds with the process only if the count == the number equal to all the validations. Just testing if it works as i want. GO BACK TO OLD METHOD IF CONFUSED!!!!
            if (productMap.isEmpty()) {
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Please select a product.");
            } else {
                statusCount++;
            }
            if (details.isBlank()) {
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Please enter stock details.");
            } else {
                statusCount++;
            }
            if (qty == 0) {
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Quantity should be higher to add to stock.");
            } else {
                statusCount++;
            }
            if (barcode.isBlank()) {
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Please scan or generate a barcode for the product.");
            } else {
                statusCount++;
            }
            if (size.equals("null")) {
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Please select a size for the product stock.");
            } else {
                statusCount++;
            }
            if (String.valueOf(mfd).equals("null") || String.valueOf(exp).equals("null")) {
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Date of manufacture and expiry should not be kept empty.");
            } else if (mfd.isAfter(exp)) {
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Manufactured date should be a date before the date of expiry.");
            } else {
                statusCount++;
            }
            if (statusCount >= 6) {

                if (buyingPrice == 0.00 && sellingPrice == 0.00) {
                    int freePriceOption = JOptionPane.showConfirmDialog(this, "Buying and Selling prices are set to 0 making them free items."
                            + " Continue regardless?", "0 price items detected!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (freePriceOption == JOptionPane.YES_OPTION) {
                        statusCount++;
                    }

                } else if (buyingPrice == 0.00 && sellingPrice != 0.00) {
                    int charityPriceOption = JOptionPane.showConfirmDialog(this, "Buying price is set to 0."
                            + " Continue regardless?", "Dontations detected!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (charityPriceOption == JOptionPane.YES_OPTION) {
                        statusCount++;
                    }

                } else if (sellingPrice == 0.00 && buyingPrice != 0.00) {
                    int charityPriceOption = JOptionPane.showConfirmDialog(this, "Selling price is set to 0."
                            + " Continue regardless?", "Charity goods detected!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (charityPriceOption == JOptionPane.YES_OPTION) {
                        statusCount++;
                    }

                } else if (buyingPrice >= sellingPrice) {
                    int buyingPriceOption = JOptionPane.showConfirmDialog(this, "Buying price is set to equal or higher than the selling"
                            + " price. This would result in a loss of profit. Continue Regardless?", "Buying price is equal or higher than the selling price!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (buyingPriceOption == JOptionPane.YES_OPTION) {
                        statusCount++;
                    }
                } else {
                    statusCount++;
                }
                // check if nother row has the new details
                if (statusCount == 7) {
                    boolean duplicate = false;
                    int duplicateRow = 0;
                    for (StockTableObject stockTableObject : productVector) {
                        if (duplicateRow != row) {
                            if (stockTableObject.getPid().equals(productMap.get("pid"))
                                    && stockTableObject.getDetails().equals(details)
                                    && Double.parseDouble(stockTableObject.getBuyingPrice()) == buyingPrice
                                    && Double.parseDouble(stockTableObject.getSellingPrice()) == sellingPrice
                                    && mfd.isEqual(LocalDate.parse(stockTableObject.getMfd()))
                                    && exp.isEqual(LocalDate.parse(stockTableObject.getExp()))
                                    && size.equals(stockTableObject.getSize())) {
                                duplicate = true;
                                break;
                            }
                        }
                        duplicateRow++;

                    }
                    if (duplicate) {
                        JOptionPane.showMessageDialog(this, "A row with these details already exists.", "Duplicating action detected", JOptionPane.INFORMATION_MESSAGE);
                    } else if (details.equals(String.valueOf(jTable1.getValueAt(row, 4)))
                            && productMap.get("pid").equals(String.valueOf(jTable1.getValueAt(row, 0)))
                            && size.equals(String.valueOf(jTable1.getValueAt(row, 5)))
                            && String.valueOf(qty).equals(String.valueOf(jTable1.getValueAt(row, 6)))
                            && buyingPrice == Double.parseDouble(String.valueOf(jTable1.getValueAt(row, 7)))
                            && sellingPrice == Double.parseDouble(String.valueOf(jTable1.getValueAt(row, 8)))
                            && mfd.isEqual(LocalDate.parse(String.valueOf(jTable1.getValueAt(row, 9))))
                            && exp.isEqual(LocalDate.parse(String.valueOf(jTable1.getValueAt(row, 10))))
                            && barcode.equals(String.valueOf(jTable1.getValueAt(row, 11)))) {
                        int option = JOptionPane.showConfirmDialog(this, "No changes detected. Abort editing?", "No changes detected!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        if (option == JOptionPane.YES_OPTION) {
                            refresh();

                        }
                    } else {
                        StockTableObject stockElement = productVector.get(row);

                        stockElement.setPid(productMap.get("pid"));
                        stockElement.setName(productMap.get("name"));
                        stockElement.setStockID(stockID);
                        stockElement.setBrand(productMap.get("brand"));
                        stockElement.setDetails(details);
                        stockElement.setSize(size);
                        stockElement.setQty(String.valueOf(qty));
                        stockElement.setBuyingPrice(String.valueOf(buyingPrice));
                        stockElement.setSellingPrice(String.valueOf(sellingPrice));
                        stockElement.setMfd(String.valueOf(mfd));
                        stockElement.setExp(String.valueOf(exp));
                        stockElement.setBarcode(barcode);
                        stockElement.setCategory(productMap.get("category"));
                        refresh();
                        loadTable();
                    }

                    System.out.println(statusCount);

                }

            }
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        jButton7.setEnabled(false);
        jButton11.setEnabled(true);
        jTable1.setEnabled(false);

        int row = jTable1.getSelectedRow();
        StockTableObject stockObject = productVector.get(row);

        jTextField1.setText(stockObject.getName());
        jTextField2.setText(stockObject.getDetails());
        jTextField3.setText(stockObject.getBarcode());
        jTextField5.setText("");

        jLabel14.setText(stockObject.getCategory());
        jLabel15.setText(stockObject.getBrand());

        productMap.clear();
        productMap.put("pid", stockObject.getPid());
        productMap.put("name", stockObject.getName());
        productMap.put("brand", stockObject.getBrand());
        productMap.put("category", stockObject.getCategory());

        loadSizes();
        jFormattedTextField1.setText(stockObject.getQty());
        jFormattedTextField2.setText(stockObject.getBuyingPrice());
        jFormattedTextField3.setText(stockObject.getSellingPrice());
        datePicker1.setDate(LocalDate.parse(stockObject.getMfd()));
        datePicker2.setDate(LocalDate.parse(stockObject.getExp()));

    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        String employee = SignIn.getEmployeeName();
        String employeeID = SignIn.getEmplyeeID();

        String supplier = supplierMap.get("name");
        String supplierID = supplierMap.get("mobile");

        double totalPrice = Double.parseDouble(jFormattedTextField4.getText());
        double payment = Double.parseDouble(jFormattedTextField5.getText());

        boolean allowUPdate = true;

        if (jTextField4.getText().isBlank()) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Please select a supplier first.");
        } else {
            int option = JOptionPane.showConfirmDialog(this, "Confirm stock update?", "Update Stock", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
// insert to stock table
                int rowCount = jTable1.getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    try {
                        ResultSet checkStockID = MySQL.executeSearch("SELECT `stock_id` FROM `stock` WHERE `stock_id` = '" + String.valueOf(jTable1.getValueAt(i, 0)) + "' ");

                        if (checkStockID.next()) {
                            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Stock ID Already exists in the database. Please redo the update.");
                            allowUPdate = false;
                            break;
                        }

                    } catch (Exception e) {
                        SplashScreen.exceptionRecords.log(Level.SEVERE, "Database Error.", e);
                        e.printStackTrace();
                        Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "There is a problem with the database. Please check you connection and try again.");
                    }

                }

                if (allowUPdate) {
                    for (int j = 0; j < rowCount; j++) {
                        sizeMap.clear();
                        StockTableObject stockObject = productVector.get(j);
                        productMap.put("pid", stockObject.getPid());
                        productMap.put("name", stockObject.getName());
                        productMap.put("brand", stockObject.getBrand());
                        productMap.put("category", stockObject.getCategory());
                        try {
                            ResultSet sizeSet = MySQL.executeSearch("SELECT * FROM `productsizes` WHERE `product_pid` = '" + String.valueOf(jTable1.getValueAt(j, 0)) + "' ");
                            while (sizeSet.next()) {
                                sizeMap.put(sizeSet.getString("size"), sizeSet.getString("sizeID"));
                            }

                        } catch (Exception e) {
                            SplashScreen.exceptionRecords.log(Level.SEVERE, "Couldn't load sizes in stock adding process.", e);
                            e.printStackTrace();
                            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "There is a problem with the database. Please check you connection and try again.");
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void refresh() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField5.setText("");

        jLabel14.setText("");
        jLabel15.setText("");

        jFormattedTextField1.setText("0");
        jFormattedTextField2.setText("0.00");
        jFormattedTextField3.setText("0.00");
        datePicker1.clear();
        datePicker2.clear();
        productMap.clear();
        loadSizes();

        jTable1.setEnabled(true);
        jButton7.setEnabled(true);
        jButton11.setEnabled(false);
        jTable1.clearSelection();
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        FlatMacLightLaf.setup();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AddNewStock().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.github.lgooddatepicker.components.DatePicker datePicker1;
    private com.github.lgooddatepicker.components.DatePicker datePicker2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JFormattedTextField jFormattedTextField3;
    private javax.swing.JFormattedTextField jFormattedTextField4;
    private javax.swing.JFormattedTextField jFormattedTextField5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    // End of variables declaration//GEN-END:variables

    public static boolean validateDate(String date) {
        if (date.isBlank()) {
            System.out.println("blank");
            return false;
        } else if (date.matches(Validation.DATE.validation())) {

            return true;
        }
        return false;
    }

    private void loadTable() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        for (StockTableObject element : productVector) {
            Vector<String> vector = new Vector<>();
            vector.add(element.getPid());
            vector.add(element.getName());
            vector.add(element.getStockID());
            vector.add(element.getBrand());
            vector.add(element.getDetails());
            vector.add(element.getSize());
            vector.add(element.getQty());
            vector.add(element.getBuyingPrice());
            vector.add(element.getSellingPrice());
            vector.add(element.getMfd());
            vector.add(element.getExp());
            vector.add(element.getBarcode());

            model.addRow(vector);

            double qty = Double.parseDouble(element.getQty());
            double buyingPrice = Double.parseDouble(element.getBuyingPrice());
            double total = qty * buyingPrice;
            this.total += total;
            jFormattedTextField4.setText(String.valueOf(this.total));
        }
    }

    private double total = 0.00;
}
