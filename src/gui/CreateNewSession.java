/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.CreateTrainingSessionBeen;
import model.FrameStorage;
import model.ModifyTables;
import model.MySQL;
import raven.toast.Notifications;


public class CreateNewSession extends javax.swing.JFrame {

    HashMap<Integer, CreateTrainingSessionBeen> sessTableMap = new HashMap<>();
    HashMap<String, String> specMap = new HashMap<>();
    HashMap<String, Vector> sessionTypeMap = new HashMap<>();
    private Home home;
    private boolean purchase = true;

    public void getHome(Home home) {
        this.home = home;
    }

    public CreateNewSession() {
        Notifications.getInstance().setJFrame(this);

        initComponents();
        init();
        sessionType();
        loadSpecs();
        loadTrainers();
        generateMemId();
    }

    private void init() {
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/logo.png")));
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);


ModifyTables modifyTables = new ModifyTables();
modifyTables.modifyTables(jTable1, jScrollPane1, false);
modifyTables.modifyTables(jTable2, jScrollPane2, false);
    }

    private void loadSessionTable() {
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);

        for (CreateTrainingSessionBeen sess : sessTableMap.values()) {
            Vector<String> vector = new Vector<>();
            vector.add(sess.getSess_id());
            vector.add(sess.getTrainer_id());
            vector.add(sess.getDate());
            vector.add(sess.getTime());
            vector.add(sess.getDuration());
            vector.add(sess.getFee());
            vector.add(sess.getType());
            vector.add(sess.getSpec());

            model.addRow(vector);
        }
    }

    private void loadSpecs() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `trainer_specializations`");
            Vector<String> vec = new Vector<>();
            while (resultSet.next()) {
                vec.add(resultSet.getString("spec_name"));
                specMap.put(resultSet.getString("spec_name"), resultSet.getString("spec_id"));
            }
            DefaultComboBoxModel model = new DefaultComboBoxModel(vec);
            jComboBox2.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sessionType() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `session_types`");
            Vector<String> vec = new Vector<>();
            while (resultSet.next()) {
                vec.add(resultSet.getString("sess_type"));
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("sess_type_id"));
                vector.add(resultSet.getString("price"));
                sessionTypeMap.put(resultSet.getString("sess_type"), vector);
            }
            DefaultComboBoxModel model = new DefaultComboBoxModel(vec);
            jComboBox1.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTrainers() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `trainers` INNER JOIN `gender` ON"
                    + " `gender`.`gender_id` = `trainers`.`gender_gender_id` INNER JOIN `trainer_specializations` ON "
                    + "`trainer_specializations`.`spec_id` = `trainers`.`trainer_specializations_spec_id` INNER JOIN `status` ON"
                    + " `trainers`.`status_status_id` = `status`.`status_id`");
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            while (resultSet.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("trainer_id"));
                vector.add(resultSet.getString("fname") + " " + resultSet.getString("lname"));
                vector.add(resultSet.getString("mobile"));
                vector.add(resultSet.getString("experience_years"));
                vector.add(resultSet.getString("weekly_payment"));
                vector.add(resultSet.getString("spec_name"));
                vector.add(resultSet.getString("gender_name"));
                vector.add(resultSet.getString("status"));

                model.addRow(vector);
            }

            jTable1.setModel(model);

        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.SEVERE, "Couldn't connect to db at loadMembers", e);

        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        datePicker1 = new com.github.lgooddatepicker.components.DatePicker();
        jLabel4 = new javax.swing.JLabel();
        timePicker1 = new com.github.lgooddatepicker.components.TimePicker();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();

        jPopupMenu1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        jMenuItem3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-delete-20.png"))); // NOI18N
        jMenuItem3.setText("Delete");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem3);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Create Session | FlexGym");
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setText("Create Training Session");
        jLabel1.setFont(new java.awt.Font("Poppins", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(46, 59, 78));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Trainer ID", "Name", "Mobile", "Experience", "H/rate", "Specialization", "Gender", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel2.setText("Trainer ");
        jLabel2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jTextField1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel3.setText("Date");
        jLabel3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        datePicker1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel4.setText("Time");
        jLabel4.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        timePicker1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel5.setText("Duration");
        jLabel5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel6.setText("hours");
        jLabel6.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel7.setText("Session Type");
        jLabel7.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel8.setText("Specialization");
        jLabel8.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Session ID", "Trainer ID", "Date", "Start Time", "End Time", "fee", "Type", "Specialization"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setComponentPopupMenu(jPopupMenu1);
        jTable2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTable2.getTableHeader().setReorderingAllowed(false);
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jButton1.setText("Add Session");
        jButton1.setBackground(new java.awt.Color(255, 111, 0));
        jButton1.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(249, 249, 249));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Clear All");
        jButton2.setBackground(new java.awt.Color(255, 160, 64));
        jButton2.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(249, 249, 249));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Create Session");
        jButton3.setBackground(new java.awt.Color(255, 160, 64));
        jButton3.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(249, 249, 249));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        jFormattedTextField1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel9.setText("Session ID");
        jLabel9.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jTextField2.setEditable(false);
        jTextField2.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(datePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(timePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel8)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(111, 111, 111)
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(26, 26, 26))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel9)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(datePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(timePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(jButton2))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jMenu2.setText("Window");

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-refresh-20.png"))); // NOI18N
        jMenuItem4.setText("Refresh");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-exit-20.png"))); // NOI18N
        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, 0));
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-fullscreen-20.png"))); // NOI18N
        jMenuItem2.setText("Exit Fullscreen");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

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

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        this.dispose();
        home.refreshHome();
        FrameStorage.createSessionFrame = null;
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
            setExtendedState(JFrame.MAXIMIZED_BOTH);

        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int row = jTable1.getSelectedRow();
        if (evt.getClickCount() == 2) {
            jTextField1.setText(String.valueOf(jTable1.getValueAt(row, 0)));

        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        CreateTrainingSessionBeen trainingSession = new CreateTrainingSessionBeen();
        String sess_id = jTextField2.getText();
        String trn_id = jTextField1.getText();
        LocalDate sesDate = datePicker1.getDate();
        String date = String.valueOf(sesDate);
        LocalDate today = LocalDate.now();
        LocalTime sesTime = timePicker1.getTime();
        String time = String.valueOf(sesTime);
        String endTime = "";
        if (!jFormattedTextField1.getText().equals("")) {
            int duration = Integer.parseInt(jFormattedTextField1.getText());
            LocalTime sessionTime = sesTime.plusHours(duration);
            endTime = String.valueOf(sessionTime);
            System.out.println(endTime);
        }
        String sessType = String.valueOf(jComboBox1.getSelectedItem());
        String spec = String.valueOf(jComboBox2.getSelectedItem());

        int row = jTable2.getRowCount();

        Vector<String> sess_prices = sessionTypeMap.get(sessType);
        String sessionFee = sess_prices.get(1);

        if (trn_id.equals("")) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 3000l, "Please Select a trainer first");
        } else if (date.equals("null")) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 3000l, "Please choose a date");
        } else if (sesDate.isBefore(today)) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 3000l, "Please choose a future date");

        } else if (time.equals("null")) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 3000l, "Please choose a time");
        } else if (endTime.equals("")) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 3000l, "Please set a duration");
        } else {

            trainingSession.setSess_id(sess_id);
            trainingSession.setTrainer_id(trn_id);
            trainingSession.setDate(date);
            trainingSession.setTime(time);
            trainingSession.setDuration(endTime);
            trainingSession.setFee(sessionFee);
            trainingSession.setType(sessType);
            trainingSession.setSpec(spec);

            sessTableMap.put(row, trainingSession);

            loadSessionTable();

            reset();
            jButton1.setEnabled(false);
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {
            jPopupMenu1.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTable2MouseClicked

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        sessTableMap.clear();
        loadSessionTable();
        jButton1.setEnabled(true);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (purchase) {
            purchase = false;
            if (sessTableMap.isEmpty()) {
                System.out.println("emptry bro");
            } else {
                try {

                    CreateTrainingSessionBeen session = sessTableMap.get(0);
                    Vector<String> sessionType = sessionTypeMap.get(session.getType());
                    String sessType = sessionType.get(0);

                    ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `session_schedule` WHERE"
                            + " `trainers_trainer_id` = '" + session.getTrainer_id() + "' AND `date` = '" + session.getDate() + "'  AND (( '" + session.getTime() + "' BETWEEN `start_time` AND `end_time` ) OR ( '" + session.getDuration() + "' BETWEEN `start_time` AND `end_time` )"
                            + " OR (`start_time` BETWEEN  '" + session.getTime() + "' AND '" + session.getDuration() + "' ) OR ( `end_time` BETWEEN  '" + session.getTime() + "' AND '" + session.getDuration() + "' ) ) ");

                    if (resultSet.next()) {
                        JOptionPane.showMessageDialog(this, "The Trainer has another session scheduled for this time slot. Please choose another trainer or a time slot", "Trainer cannot be scheduled.", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        jButton3.setText("Please Wait");
//                    System.out.println(SignIn.getEmplyeeID());
                        int option = JOptionPane.showConfirmDialog(this, "Confirm creating session?", "Create Session?", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        if (option == JOptionPane.YES_OPTION) {
                            MySQL.executeIUD("INSERT INTO `session_schedule` VALUES ('" + session.getSess_id() + "','" + session.getTrainer_id() + "',"
                                    + "'" + session.getDate() + "','" + session.getTime() + "','" + session.getDuration() + "','" + session.getFee() + "','" + sessType + "','" + specMap.get(session.getSpec()) + "','1','" + SignIn.getEmplyeeID() + "') ");

                            ResultSet resultSet2 = MySQL.executeSearch("SELECT * FROM `trainer_performance` "
                                    + " WHERE `trainers_trainer_id` = '" + session.getTrainer_id() + "' ");

                            if (resultSet2.next()) {
                                System.out.println("there is one");
                                MySQL.executeIUD("UPDATE `trainer_performance` SET `scheduled` = `scheduled`+1"
                                        + " WHERE `trainers_trainer_id` = '" + session.getTrainer_id() + "' ");
                            } else {
                                System.out.println("there is not one");
                                MySQL.executeIUD("INSERT INTO `trainer_performance` (`scheduled`,`completed`,`cancelled`,`trainers_trainer_id`)"
                                        + " VALUES ('1','0','0','" + session.getTrainer_id() + "') ");
                            }

                            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, 3000l, "Session Created Successfully");
                            loadTrainers();
                            this.dispose();
                            home.refreshHome();
                            FrameStorage.createSessionFrame = null;
                        }

                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Couldn't create session. Check your network connection", "Error", JOptionPane.ERROR_MESSAGE);
                    SplashScreen.exceptionRecords.log(Level.SEVERE, "Couldn't Create Session. Problem with the databse connection", e);
                }
            }
            purchase = true;
            jButton3.setText("Create Session");
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        reset();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        loadTrainers();
        sessTableMap.clear();
        loadSessionTable();
        reset();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        home.setEnabled(true);
    }//GEN-LAST:event_formWindowClosing

    private String generateMemId() {
        Date date = new Date();
        Random random = new Random();
        int random3Digit = 100 + random.nextInt(900);

        String empSuffix = "CSN";

        String memberID = empSuffix + formatDate("yy", date) + formatDate("MM", date) + formatDate("dd", date) + formatDate("mm", date) + formatDate("HH", date) + formatDate("ss", date) + String.valueOf(random3Digit);

        try {

            ResultSet checkMemID = MySQL.executeSearch("SELECT * FROM `session_schedule` WHERE `session_id` = '" + memberID + "' ");

            if (checkMemID.next()) {
                memberID = generateMemId();
            }
        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.WARNING, "Unable to create session id", e);
        }
        jTextField2.setText(memberID);
        return memberID;
    }

    private String formatDate(String format, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        FlatMacLightLaf.setup();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CreateNewSession().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.github.lgooddatepicker.components.DatePicker datePicker1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private com.github.lgooddatepicker.components.TimePicker timePicker1;
    // End of variables declaration//GEN-END:variables

    private void reset() {
        jTextField1.setText("");
        datePicker1.clear();
        timePicker1.clear();
        jFormattedTextField1.setText("");
        jTable1.clearSelection();
        jComboBox1.setSelectedIndex(0);
        jComboBox2.setSelectedIndex(0);
        generateMemId();
    }
}
