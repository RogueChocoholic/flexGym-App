/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.Color;
import java.awt.Toolkit;
import javax.swing.JFrame;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.FrameStorage;
import model.LogoSettting;
import model.ModifyTables;
import model.MySQL;
import raven.toast.Notifications;

public class StaffRegistration extends javax.swing.JFrame {

    private String employeeEmail;
    private String searchIterate = "";

    public String getEmployeeEmail() {
        return employeeEmail;
    }
    private HashMap<String, String> genderMap = new HashMap<>();
    private HashMap<String, String> staffTypeMap = new HashMap<>();

    private Home home;

    public void getHome(Home home) {
        this.home = home;
    }

    public StaffRegistration() {
        initComponents();
        Notifications.getInstance().setJFrame(this);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        init();
        loadStaff();
        loadGender();
        loadStaffTypes();
    }

    private void init() {

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/logo.png")));

        LogoSettting logo = new LogoSettting();
        logo.setLogo(jLabel2);

        jButton1.putClientProperty(FlatClientProperties.STYLE, "arc:999");
        jButton2.putClientProperty(FlatClientProperties.STYLE, "arc:999");
        jButton3.putClientProperty(FlatClientProperties.STYLE, "arc:999");
        jTextField6.putClientProperty(FlatClientProperties.STYLE, "arc:999");
        jToggleButton1.putClientProperty(FlatClientProperties.STYLE, "arc:999");

        ModifyTables modifyTables = new ModifyTables();
        modifyTables.modifyTables(jTable2, jScrollPane2, false);
    }

    private String generateStaffId() {
        Date date = new Date();
        Random random = new Random();
        int random3Digit = 100 + random.nextInt(900);

        String empSuffix = "";

        if (String.valueOf(jComboBox2.getSelectedItem()).equals("Cashier")) {
            empSuffix = "EMP";
        } else if (String.valueOf(jComboBox2.getSelectedItem()).equals("Administrator")) {
            empSuffix = "ADM";
        }

        return empSuffix + formatDate("yy", date) + formatDate("MM", date) + formatDate("dd", date) + String.valueOf(random3Digit);
    }

    private void loadStaff() {
        searchIterate = "";
        try {
            String idSearch = jTextField6.getText();
            String roleType = String.valueOf(jComboBox4.getSelectedItem());

            if (!idSearch.equals("")) {
                searchIterate += " WHERE `staff_id` LIKE '%" + idSearch + "%' ";
                if (!roleType.equals("Show All")) {
                    System.out.println("what");
                    searchIterate += " AND `employee_type_type_id` = '" + staffTypeMap.get(roleType) + "' ";
                }
            } else {
                if (!roleType.equals("Show All")) {
                    searchIterate += " WHERE `employee_type_type_id` = '" + staffTypeMap.get(roleType) + "'   ";
                }
            }

            String sortBy = String.valueOf(jComboBox3.getSelectedItem());
            searchIterate += " ORDER BY ";
            if (sortBy.equals("Name ASC")) {
                searchIterate += " `fname` ASC ";
            } else if (sortBy.equals("Name DESC")) {
                searchIterate += " `fname` DESC ";
            } else if (sortBy.equals("Date ASC")) {
                searchIterate += " `reg_date` ASC ";
            } else if (sortBy.equals("Date DESC")) {
                searchIterate += " `reg_date` DESC ";
            } else if (sortBy.equals("Type ASC")) {
                searchIterate += " `employee_type`.`type_name` ASC ";
            } else if (sortBy.equals("Type DESC")) {
                searchIterate += " `employee_type`.`type_name` DESC ";
            }

            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0);
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `staff` INNER JOIN `gender` ON "
                    + "`gender`.`gender_id` = `staff`.`gender_gender_id` INNER JOIN `employee_type` ON"
                    + " `employee_type`.`type_id` = `staff`.`employee_type_type_id` " + searchIterate + " ");
            while (resultSet.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("staff_id"));
                vector.add(resultSet.getString("fname") + " " + resultSet.getString("lname"));
                vector.add(resultSet.getString("email"));
                vector.add(resultSet.getString("password"));
                vector.add(resultSet.getString("mobile"));
                vector.add(resultSet.getString("nic"));
                vector.add(resultSet.getString("gender_name"));
                vector.add(resultSet.getString("type_name"));
                vector.add(resultSet.getString("reg_date"));

                model.addRow(vector);
            }
        } catch (Exception e) {
            e.printStackTrace();
            SplashScreen.exceptionRecords.log(Level.SEVERE, e.toString());
        }
    }

    private void loadGender() {
        try {

            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `gender`");
            Vector<String> vector = new Vector<>();
            vector.add("Select");

            while (resultSet.next()) {
                vector.add(resultSet.getString("gender_name"));
                genderMap.put(resultSet.getString("gender_name"), resultSet.getString("gender_id"));
            }
            DefaultComboBoxModel model = new DefaultComboBoxModel(vector);
            jComboBox1.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStaffTypes() {
        try {

            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `employee_type`");
            Vector<String> vector = new Vector<>();
            Vector<String> vector1 = new Vector<>();
            vector.add("Select");
            vector1.add("Show All");
            while (resultSet.next()) {
                vector.add(resultSet.getString("type_name"));
                vector1.add(resultSet.getString("type_name"));
                staffTypeMap.put(resultSet.getString("type_name"), resultSet.getString("type_id"));
            }
            DefaultComboBoxModel model1 = new DefaultComboBoxModel(vector);
            DefaultComboBoxModel model2 = new DefaultComboBoxModel(vector1);
            jComboBox2.setModel(model1);
            jComboBox4.setModel(model2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jToggleButton1 = new javax.swing.JToggleButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox4 = new javax.swing.JComboBox<>();
        jTextField6 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Staff Registration | FlexGym");
        setMinimumSize(new java.awt.Dimension(1182, 617));
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(46, 59, 78));
        jLabel1.setText("Staff Registration");

        jLabel3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel3.setText("First Name");

        jLabel4.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel4.setText("Last Name");

        jLabel5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel5.setText("Email");

        jLabel6.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel6.setText("Password");

        jLabel7.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel7.setText("NIC");

        jLabel8.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel8.setText("Gender");

        jLabel9.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel9.setText("Employee Type");

        jTextField1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jTextField2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jTextField3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jTextField4.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jTextField5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jComboBox1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton1.setBackground(new java.awt.Color(255, 111, 0));
        jButton1.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Create Account");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(255, 111, 0));
        jButton2.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Update Account");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(255, 160, 64));
        jButton3.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Clear All");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel13.setText("Mobile");

        jPasswordField1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jToggleButton1.setBackground(new java.awt.Color(246, 246, 246));
        jToggleButton1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-eye-24.png"))); // NOI18N
        jToggleButton1.setToolTipText("Show Password");
        jToggleButton1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons8-eye-24 (3).png"))); // NOI18N
        jToggleButton1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jToggleButton1ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 98, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(78, 78, 78))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addComponent(jLabel6))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextField1)
                                .addComponent(jTextField2)
                                .addComponent(jTextField3)
                                .addComponent(jTextField4)
                                .addComponent(jTextField5)
                                .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBox2, 0, 227, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(jPasswordField1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jToggleButton1))))
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToggleButton1)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel10.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel10.setText("Type");

        jComboBox3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name ASC", "Name DESC", "Date ASC", "Date DESC", "Type ASC", "Type DESC" }));
        jComboBox3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox3ItemStateChanged(evt);
            }
        });

        jComboBox4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Show All", "Admin", "Cashier" }));
        jComboBox4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox4ItemStateChanged(evt);
            }
        });

        jTextField6.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jTextField6.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField6KeyReleased(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel11.setText("Staff ID");

        jLabel12.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel12.setText("Sort By");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 150, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel12))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTable2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Email", "Password", "Mobile", "NIC", "Gender", "Type ", "Registered Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.getTableHeader().setReorderingAllowed(false);
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 738, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );

        jMenu1.setText("Window");

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
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        FrameStorage.staffRegistration = null;
    }//GEN-LAST:event_formWindowClosing

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked

        int row = jTable2.getSelectedRow();

        String[] name = String.valueOf(jTable2.getValueAt(row, 1)).split(" ");

        jTextField6.setText(String.valueOf(jTable2.getValueAt(row, 0)));
        jTextField1.setText(name[0]);
        jTextField2.setText(name[1]);
        jTextField3.setText(String.valueOf(jTable2.getValueAt(row, 2)));
        jTextField3.setEditable(false);
        jTextField6.setEditable(false);
        jPasswordField1.setText(String.valueOf(jTable2.getValueAt(row, 3)));
        jTextField4.setText(String.valueOf(jTable2.getValueAt(row, 4)));
        jTextField5.setText(String.valueOf(jTable2.getValueAt(row, 5)));
        jComboBox1.setSelectedItem(String.valueOf(jTable2.getValueAt(row, 6)));
        jComboBox2.setSelectedItem(String.valueOf(jTable2.getValueAt(row, 7)));

        jButton2.setEnabled(true);
        jButton1.setEnabled(false);
    }//GEN-LAST:event_jTable2MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        reset();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String fname = jTextField1.getText();
        String lname = jTextField2.getText();
        jPasswordField1.setEchoChar('\u2022');
        String email = jTextField3.getText();
        String password = String.valueOf(jPasswordField1.getPassword());
        String mobile = jTextField4.getText();
        String nic = jTextField5.getText();
        String gender = String.valueOf(jComboBox1.getSelectedItem());
        String type = String.valueOf(jComboBox2.getSelectedItem());

        String staffID = jTextField6.getText();

        if (fname.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Please Enter the First Name");

        } else if (lname.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Please Enter the Last Name");

        } else if (password.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Please Enter the Password");

        } else if (password.length() < 8) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Password must have at least 8 characters.");

        } else if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 6000l, "Password must container at least one; Uppercase, Lowercase, Numberical and Special Character each.");

        } else if (mobile.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Please Enter a Mobile Number");

        } else if (!mobile.matches("^(?:0|94|\\+94|0094)?(?:(11|21|23|24|25|26|27|31|32|33|34|35|36|37|38|41|45|47|51|52|54|55|57|63|65|66|67|81|91)(0|2|3|4|5|7|9)|7(0|1|2|4|5|6|7|8)\\d)\\d{6}$")) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Invalid Mobile Number");

        } else if (nic.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Please Enter the National Identity Card Number");

        } else if (!nic.matches("^(([5,6,7,8,9]{1})([0-9]{1})([0,1,2,3,5,6,7,8]{1})([0-9]{6})([v|V|x|X]))|(([1,2]{1})([0,9]{1})([0-9]{2})([0,1,2,3,5,6,7,8]{1})([0-9]{7}))")) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Invalid NIC.");

        } else if (gender.equals("Select")) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Please Select a Gender");

        } else if (type.equals("Select")) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Please Select an Employee Type");

        } else {
            int row = jTable2.getSelectedRow();
            fname = fname.substring(0, 1).toUpperCase() + fname.substring(1).toLowerCase();
            lname = lname.substring(0, 1).toUpperCase() + lname.substring(1).toLowerCase();

            try {
                ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `staff` WHERE (`nic` = '" + nic + "' OR `mobile` = '" + mobile + "') AND `staff_id` != '" + staffID + "' ");

                if (resultSet.next()) {
                    JOptionPane.showMessageDialog(this, "NIC or Mobile number already exists in another account.", "Employee Exists.", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    if ((fname + " " + lname).equals(String.valueOf(jTable2.getValueAt(row, 1)))
                            && password.equals(String.valueOf(jTable2.getValueAt(row, 3)))
                            && mobile.equals(String.valueOf(jTable2.getValueAt(row, 4)))
                            && nic.equals(String.valueOf(jTable2.getValueAt(row, 5)))
                            && type.equals(String.valueOf(jTable2.getValueAt(row, 7)))
                            && gender.equals(String.valueOf(jTable2.getValueAt(row, 6)))) {

                        Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 3000l, "No Changes Detected. Please do the necessary changes and click Update");

                    } else {
                        MySQL.executeIUD("UPDATE `staff` SET `fname` = '" + fname + "', `lname` = '" + lname + "',"
                                + "`password` = '" + password + "', `mobile` = '" + mobile + "', `nic` = '" + nic + "',"
                                + " `gender_gender_id` = '" + genderMap.get(gender) + "', "
                                + "`employee_type_type_id` = '" + staffTypeMap.get(type) + "' WHERE `staff_id` = '" + staffID + "' ");

                        Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, 3000l, "Employee Details Updated Successfully!");
                        loadStaff();
                        reset();

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 5000l, "Error! Couldn't Create New Employee Account. Please Check Your Network and Try Again");
            }

        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String fname = jTextField1.getText();
        String lname = jTextField2.getText();
        jPasswordField1.setEchoChar('\u2022');
        String email = jTextField3.getText();
        String password = String.valueOf(jPasswordField1.getPassword());
        String mobile = jTextField4.getText();
        String nic = jTextField5.getText();
        String gender = String.valueOf(jComboBox1.getSelectedItem());
        String type = String.valueOf(jComboBox2.getSelectedItem());

        Date date = new Date();

        String staffID = generateStaffId();

        if (fname.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_LEFT, 3000l, "Please Enter the First Name");

        } else if (lname.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_LEFT, 3000l, "Please Enter the Last Name");

        } else if (email.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_LEFT, 3000l, "Please Enter the Email");

        } else if (!email.matches("^(?=.{1,64}@)[A-Za-z0-9\\+_-]+(\\.[A-Za-z0-9\\+_-]+)*@[^-][A-Za-z0-9\\+-]+(\\.[A-Za-z0-9\\+-]+)*(\\.[A-Za-z]{2,})$")) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_LEFT, 3000l, "Email Invalid");

        } else if (password.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_LEFT, 3000l, "Please Enter the Password");

        } else if (password.length() < 8) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_LEFT, 3000l, "Password must have at least 8 characters.");

        } else if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_LEFT, 6000l, "Password must container at least one; Uppercase, Lowercase, Numberical and Special Character each.");

        } else if (mobile.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_LEFT, 3000l, "Please Enter a Mobile Number");

        } else if (!mobile.matches("^(?:0|94|\\+94|0094)?(?:(11|21|23|24|25|26|27|31|32|33|34|35|36|37|38|41|45|47|51|52|54|55|57|63|65|66|67|81|91)(0|2|3|4|5|7|9)|7(0|1|2|4|5|6|7|8)\\d)\\d{6}$")) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_LEFT, 3000l, "Invalid Mobile Number");

        } else if (nic.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_LEFT, 3000l, "Please Enter the National Identity Card Number");

        } else if (!nic.matches("^(([5,6,7,8,9]{1})([0-9]{1})([0,1,2,3,5,6,7,8]{1})([0-9]{6})([v|V|x|X]))|(([1,2]{1})([0,9]{1})([0-9]{2})([0,1,2,3,5,6,7,8]{1})([0-9]{7}))")) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_LEFT, 3000l, "Invalid NIC.");

        } else if (gender.equals("Select")) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_LEFT, 3000l, "Please Select a Gender");

        } else if (type.equals("Select")) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_LEFT, 3000l, "Please Select an Employee Type");

        } else {

            fname = fname.substring(0, 1).toUpperCase() + fname.substring(1).toLowerCase();
            lname = lname.substring(0, 1).toUpperCase() + lname.substring(1).toLowerCase();

            try {
                ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `staff` WHERE `email` = '" + email + "' OR `nic` = '" + nic + "' OR `mobile` = '" + mobile + "' ");

                if (resultSet.next()) {
                    JOptionPane.showMessageDialog(this, "An Account Already Exists with these Details.", "Employee Exists.", JOptionPane.INFORMATION_MESSAGE);
                } else {

                    ResultSet resultSet2 = MySQL.executeSearch("SELECT * FROM `staff` WHERE `staff_id` = '" + staffID + "'");

                    if (resultSet2.next()) {
                        JOptionPane.showMessageDialog(this, "System has run into the same stock id. Please close this window and add the employee again.");
                    } else {

                        MySQL.executeIUD("INSERT INTO `staff` VALUES ('" + staffID + "','" + fname + "','" + lname + "','" + email + "'"
                                + ",'" + password + "','" + mobile + "','" + nic + "','" + formatDate("yyyy-MM-dd", date) + "',"
                                + "'" + genderMap.get(gender) + "','" + staffTypeMap.get(type) + "','1') ");

                        Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.BOTTOM_LEFT, 3000l, "Employee Added");
                        loadStaff();
                        reset();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_LEFT, 5000l, "Error! Couldn't Create New Employee Account. Please Check Your Network and Try Again");
            }

        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jToggleButton1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jToggleButton1ItemStateChanged
        if (jToggleButton1.isSelected()) {
            jPasswordField1.setEchoChar('\0');
        } else {
            jPasswordField1.setEchoChar('\u2022');
        }
        jPasswordField1.grabFocus();
    }//GEN-LAST:event_jToggleButton1ItemStateChanged

    private void jTextField6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyReleased
        loadStaff();
    }//GEN-LAST:event_jTextField6KeyReleased

    private void jComboBox4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox4ItemStateChanged
        loadStaff();
    }//GEN-LAST:event_jComboBox4ItemStateChanged

    private void jComboBox3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox3ItemStateChanged
        loadStaff();
    }//GEN-LAST:event_jComboBox3ItemStateChanged

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        this.dispose();
        FrameStorage.staffRegistration = null;
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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        FlatMacLightLaf.setup();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StaffRegistration().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables

    private void reset() {
        jTextField6.setText("");
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jPasswordField1.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jComboBox1.setSelectedIndex(0);
        jComboBox2.setSelectedIndex(0);

        jTextField3.setEditable(true);
        jTextField6.setEditable(true);

        jButton1.setEnabled(true);
        jButton2.setEnabled(false);
        jTable2.clearSelection();

        loadStaff();
    }

    private String formatDate(String format, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
}
