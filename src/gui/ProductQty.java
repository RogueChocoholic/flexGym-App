package gui;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Vector;
import raven.toast.Notifications;

public class ProductQty extends javax.swing.JDialog {

    NewInvoice invoiceFrame;
    HashMap<String, String> productDetails = new HashMap<>();

    public ProductQty(java.awt.Frame parent, boolean modal, HashMap qtyDetails) {
        super(parent, modal);
        initComponents();
        productDetails = qtyDetails;
        invoiceFrame = (NewInvoice) parent;
        init();
    }

    private void init() {
        jTextField1.setText(String.valueOf(productDetails.get("name")));
        jTextField2.setText(String.valueOf(productDetails.get("price")));
        jTextField3.setText(String.valueOf(productDetails.get("size")));
        int qty = (int) Double.parseDouble(productDetails.get("qty"));
        jTextField5.setText(String.valueOf(qty));

        jFormattedTextField1.grabFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select Quantity");

        jPanel1.setBackground(new java.awt.Color(255, 160, 64));

        jTextField1.setEditable(false);
        jTextField1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTextField1.setFocusable(false);

        jLabel1.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel1.setText("Select Quantity");

        jLabel2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel2.setText("Product");

        jLabel3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel3.setText("Price");

        jTextField2.setEditable(false);
        jTextField2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTextField2.setFocusable(false);

        jLabel4.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel4.setText("Size");

        jTextField3.setEditable(false);
        jTextField3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTextField3.setFocusable(false);

        jLabel5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel5.setText("Quantity");

        jButton1.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton1.setText("-");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton2.setText("+");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel6.setText("Remaining Stock");

        jTextField5.setEditable(false);
        jTextField5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTextField5.setFocusable(false);

        jFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        jFormattedTextField1.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jFormattedTextField1.setText("0");
        jFormattedTextField1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jFormattedTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jFormattedTextField1KeyPressed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jButton3.setText("Select Qty");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jFormattedTextField1)
                                .addGap(18, 18, 18)
                                .addComponent(jButton2)))))
                .addGap(59, 59, 59))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3))
                .addContainerGap(41, Short.MAX_VALUE))
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

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        plusClick();

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        minusClick();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        confirmQty();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jFormattedTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField1KeyPressed
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                plusClick();
                break;
            case KeyEvent.VK_LEFT:
                minusClick();
                break;
            case KeyEvent.VK_ENTER:
                confirmQty();
                evt.consume();
                break;
            default:
                break;
        }
    }//GEN-LAST:event_jFormattedTextField1KeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField5;
    // End of variables declaration//GEN-END:variables

    private void plusClick() {
        int qty = Integer.parseInt(jFormattedTextField1.getText());
        int maxqty = Integer.parseInt(jTextField5.getText());
        if (qty >= maxqty) {
            qty = maxqty;
        } else {
            qty += 1;
        }
        jFormattedTextField1.setText(String.valueOf(qty));
    }

    private void minusClick() {
        int qty = Integer.parseInt(jFormattedTextField1.getText());

        if (qty == 0.00) {
            jFormattedTextField1.setText("0");
        } else {
            qty -= 1;
            jFormattedTextField1.setText(String.valueOf(qty));

        }
    }

    private void confirmQty() {
        int qty = Integer.parseInt(jFormattedTextField1.getText());
        int update = 0;
        int row = 0;
        if (qty != 0) {
            if (!invoiceFrame.cartVector.isEmpty()) {
                update += 1;
                int index = -1;
                for (cartObjects existingObjects : invoiceFrame.cartVector) {
                    index += 1;
                    String stockID = existingObjects.getStockID();
                    String size = existingObjects.getSize();
                    String price = existingObjects.getPrice();
                    String details = existingObjects.getDetails();
                    String name = existingObjects.getProductName();
                    System.out.println(name);
                    if (stockID.equals(productDetails.get("stockID"))
                            && size.equals(productDetails.get("size"))
                            && price.equals(productDetails.get("price"))
                            && details.equals(productDetails.get("details"))
                            && name.equals(productDetails.get("name"))) {
                        System.out.println(productDetails.get("size"));
                        update += 1;
                        row = index;
                    }
                }
            }
            if (update != 2) {
                cartObjects cartObject = new cartObjects();
                cartObject.setStockID(productDetails.get("stockID"));
                cartObject.setProductName(productDetails.get("name"));
                cartObject.setSize(productDetails.get("size"));
                cartObject.setQty(jFormattedTextField1.getText());
                cartObject.setPrice(productDetails.get("price"));
                cartObject.setDetails(productDetails.get("details"));

                invoiceFrame.getCartVector().add(cartObject);
            } else {
                System.out.println("same same");
                cartObjects cartObject = invoiceFrame.cartVector.get(row);
                cartObject.setQty(String.valueOf(qty));
            }
            System.out.println(row + " index   " + update + " update");
            invoiceFrame.loadItems();
            this.dispose();
            System.out.println("doesn't loop");
        } else {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 3000l, "Quantity should not be zero");
        }
    }
}
