package gui;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.ModifyTables;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JasperViewer;
import raven.toast.Notifications;
import java.sql.ResultSet;
import model.FrameStorage;
import model.MySQL;

public class CheckoutDialog extends javax.swing.JDialog {

    private NewInvoice parentFrame;
    private Vector<cartObjects> cartObjects;
    HashMap<String, String> invoiceDetails = new HashMap<>();

    public CheckoutDialog(java.awt.Frame parent, boolean modal, Vector cartObjects, HashMap details) {
        super(parent, modal);
        parentFrame = (NewInvoice) parent;
        initComponents();
        this.cartObjects = cartObjects;
        this.invoiceDetails = details;
        init();
    }

    private void init() {
        ModifyTables modifyTable = new ModifyTables();
        modifyTable.modifyTables(jPanel2, jTable1, jScrollPane1, false);
        loadTable();
        setLabels();
    }

    private void setLabels() {
        memID.setText(invoiceDetails.get("memberID"));
        memName.setText(invoiceDetails.get("member"));
        if (memID.getText().equals("Member ID")) {
            memID.setText("Guest ID");
            memName.setText("Guest Customer");

        }
        totalField.setText(invoiceDetails.get("total"));
        discountField.setText(invoiceDetails.get("discount"));
        paymethodField.setText(invoiceDetails.get("paymethod"));
        paymentField.setText(invoiceDetails.get("payment"));
        balancefield.setText(invoiceDetails.get("balance"));
    }

    private void loadTable() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        for (cartObjects cartObject : cartObjects) {
            Vector<String> row = new Vector();
            row.add(cartObject.getStockID());
            row.add(cartObject.getProductName());
            row.add(cartObject.getDetails());
            row.add(cartObject.getSize());
            row.add(cartObject.getQty());
            row.add(cartObject.getPrice());
            model.addRow(row);
        }
        jTable1.setModel(model);
    }

//    Saves the invoice data to the database
    private void saveInvoice() {
        boolean withMember = true;
        String invID = invoiceDetails.get("invoiceID");
        String memberID = invoiceDetails.get("memberID");
        if (memID.getText().equals("Guest ID")) {
            withMember = false;
        }
        String date = invoiceDetails.get("date");
        String payment = invoiceDetails.get("payment");
        String discount = invoiceDetails.get("discount");
        String payMethod = "1";
        if (!invoiceDetails.get("paymethod").equals("Cash")) {
            payMethod = "2";
        }
        String employee = SignIn.getEmplyeeID();
        try {
            ResultSet invoice_set = MySQL.executeSearch("SELECT * FROM `invoice` WHERE `invoice_id` = '" + invID + "' ");
            if (invoice_set.next()) {
                Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 3000l, "Invoice with the same invoice  id already exists. Please Try again.");
            } else {
                if (withMember) {
                    MySQL.executeIUD("INSERT INTO `invoice` VALUES ('" + invID + "','" + date + "','" + payment + "','" + discount + "','" + payMethod + "',"
                            + " '" + memberID + "','" + employee + "') ");
                } else {
                    MySQL.executeIUD("INSERT INTO `invoice` (`invoice_id`,`date`,`paid_amount`,`discount`,`payment_method_method_id`,`staff_staff_id`)"
                            + " VALUES ('" + invID + "','" + date + "','" + payment + "','" + discount + "','" + payMethod + "',"
                            + "'" + employee + "') ");
                }

                for (cartObjects cartObject : cartObjects) {
                    String stock_id = cartObject.getStockID();
                    String qty = cartObject.getQty();
                    MySQL.executeIUD("INSERT INTO `invoice_item` (`invoice_invoice_id`,`stock_stock_id`,`qty`) "
                            + "VALUES ('" + invID + "','" + stock_id + "','" + qty + "')");

                    MySQL.executeIUD("UPDATE `stock` SET `qty` = `qty`-'" + cartObject.getQty() + "' WHERE `stock_id` = '" + cartObject.getStockID() + "' ");
                }
                Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, 3000l, "Invoice Added Successfully!");

            }
        } catch (Exception e) {
            e.printStackTrace();
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Error saving invoice data");
            SplashScreen.exceptionRecords.log(Level.SEVERE, "Error saving invoice to the database");
        }
    }

    private void printInvoice() {
        InputStream input = this.getClass().getResourceAsStream("/reports/flexGymInvoice.jasper");
        HashMap<String, Object> params = new HashMap<>();
        params.put("Parameter1", invoiceDetails.get("invoiceID"));
        params.put("Parameter2", invoiceDetails.get("member"));
        params.put("Parameter3", SignIn.getEmployeeName());
        params.put("Parameter4", invoiceDetails.get("date"));
        params.put("Parameter5", invoiceDetails.get("total"));
        params.put("Parameter6", invoiceDetails.get("discount"));
        params.put("Parameter7", invoiceDetails.get("paymethod"));
        params.put("Parameter8", invoiceDetails.get("payment"));
        params.put("Parameter9", invoiceDetails.get("balance"));

        JRTableModelDataSource dataSource = new JRTableModelDataSource(jTable1.getModel());

        try {
            JasperPrint jasper = JasperFillManager.fillReport(input, params, dataSource);
            int option = JOptionPane.showConfirmDialog(this, "View Report?", "View", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
//            if (option == JOptionPane.YES_OPTION) {
//                JasperViewer.viewReport(jasper, true);
//            }
            JasperPrintManager.printReport(jasper, false);

        } catch (Exception e) {
            e.printStackTrace();
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Error Printing Report!");
            SplashScreen.exceptionRecords.log(Level.SEVERE, "Error Printing invoice");
        }
//parentFrame.setAlwaysOnTop(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        totalField = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        discountField = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        paymethodField = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        paymentField = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        balancefield = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        memID = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        memName = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Checkout ~ FlexGym");

        jLabel1.setFont(new java.awt.Font("Poppins", 1, 36)); // NOI18N
        jLabel1.setText("Checkout");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Stock ID", "Product", "Description", "Size", "Quantity", "Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setFocusable(false);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1)
                .addGap(18, 18, 18))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        jLabel13.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        jLabel13.setText("Total");

        totalField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        totalField.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        totalField.setText("null");

        jLabel14.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        jLabel14.setText("Discount");

        discountField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        discountField.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        discountField.setText("null");

        jLabel16.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        jLabel16.setText("Payment Method");

        paymethodField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        paymethodField.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        paymethodField.setText("null");

        jLabel18.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        jLabel18.setText("Payment");

        paymentField.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        paymentField.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        paymentField.setText("null");

        jLabel20.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        jLabel20.setText("Balance");

        balancefield.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        balancefield.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        balancefield.setText("null");

        jLabel10.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        jLabel10.setText("Member ID");

        memID.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        memID.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        memID.setText("Member ID");

        jLabel22.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        jLabel22.setText("Name");

        memName.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        memName.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        memName.setText("null");

        jButton1.setBackground(new java.awt.Color(255, 111, 0));
        jButton1.setFont(new java.awt.Font("Poppins SemiBold", 0, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Proceed");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(memName, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(memID, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 293, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(totalField, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(discountField, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(paymethodField, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(paymentField, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(balancefield, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(memID))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22)
                            .addComponent(memName))
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(totalField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(discountField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(paymethodField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(paymentField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20)
                            .addComponent(balancefield))))
                .addGap(46, 46, 46))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(46, 46, 46))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        saveInvoice();
        printInvoice();
        FrameStorage.newInvoiceFrame.dispose();
        NewInvoice newInvoice = new NewInvoice();
        FrameStorage.newInvoiceFrame = newInvoice;
        FrameStorage.newInvoiceFrame.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        FlatMacLightLaf.setup();

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CheckoutDialog dialog = new CheckoutDialog(new javax.swing.JFrame(), true, new Vector(), new HashMap());
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel balancefield;
    private javax.swing.JLabel discountField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel memID;
    private javax.swing.JLabel memName;
    private javax.swing.JLabel paymentField;
    private javax.swing.JLabel paymethodField;
    private javax.swing.JLabel totalField;
    // End of variables declaration//GEN-END:variables
}
