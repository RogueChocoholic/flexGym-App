package gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.util.List;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.LogoSettting;
import model.MySQL;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JasperViewer;
import raven.toast.Notifications;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.time.temporal.TemporalAdjusters;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import model.CheckNumerical;
import model.FrameStorage;
import model.ModifyTables;
import model.Validation;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

public class Home extends javax.swing.JFrame {

    HashMap<String, String> membershipTypeMap = new HashMap<>();
    HashMap<String, String> specMap = new HashMap<>();
    HashMap<String, String> statusMap = new HashMap<>();
    HashMap<String, Vector> sessionTypeMap = new HashMap<>();
    HashMap<String, String> companyMap = new HashMap<>();
    HashMap<String, String> brandMAp = new HashMap<>();
    HashMap<String, String> categoryMap = new HashMap<>();

//    String search = "";
    private static final List<JButton> buttons = new ArrayList<>();
    private static final List<JTable> tables = new ArrayList<>();
    HashMap<JTable, JScrollPane> modifyTableMap = new HashMap<>();

    public Home(boolean notify) {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        initComponents();
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/logo.png")));
        Notifications.getInstance().setJFrame(this);
        refresh();
        init(notify);
    }

    public void refreshHome() {
        refresh();
    }
//
//    private void clearTables() {
//        DefaultTableModel model = (DefaultTableModel) jTable4.getModel();
//        model.setRowCount(0);
//        jTable4.setModel(model);
//        DefaultTableModel model2 = (DefaultTableModel) jTable9.getModel();
//        model2.setRowCount(0);
//        jTable9.setModel(model);
//    }

    private void refresh() {
        var refreshThread = new Thread(() -> {
            // Perform data loading in the background thread
            try {

                loadMemberships();
                loadMemberInvoices();
                loadStatusMap();
                loadSpecs();
                loadSessionSpecs();
                loadSessionType();
                membershipsLoadMembers();
                loadTrainers();
                loadSessions();
                loadDashboardSessions();
                loadDashboardMemberEXP();
                loadTrainerDashDetails();
                loadCompanies();
                loadSuppliers();
                loadInventoryBrandCategory();
                loadProducts();
                loadStock();
                loadMiniStock();
                loadSupplierGrn();
                loadMemInvoceList();
                loadErrorLogins();
                loadOverViewInfo();

            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        // After data is loaded, update the UI components on the EDT
        SwingUtilities.invokeLater(() -> {
            jTextField5.setText("");
            jTextField7.setText("");
            jComboBox7.setSelectedIndex(0);
            jComboBox8.setSelectedIndex(0);
            jButton8.setEnabled(false);

        });

        refreshThread.start();
    }

    void runLoadStock() {
        loadStock();
    }

    private void loadStock() {
        int sort = jComboBox9.getSelectedIndex();
        double sellingPriceMin = 0;
        double sellingPriceMax = 0;

        String search = " WHERE ";
        String productName = jTextField7.getText();
        search += " `name` LIKE '%" + productName + "%' ";

        String productID = jTextField5.getText();
        search += " AND `pid` LIKE '%" + productID + "%'";

        String brandText = String.valueOf(jComboBox7.getSelectedItem());

        if (!brandText.equals("All Brands")) {
            String brand = brandMAp.get(brandText);
            search += " AND `brand_brand_id` = '" + brand + "' ";
        }
        String categoryText = String.valueOf(jComboBox8.getSelectedItem());
        if (!categoryText.equals("All Categories")) {
            String category = categoryMap.get(categoryText);
            search += " AND `Category_cat_id` = '" + category + "' ";
        }
//        if (!jFormattedTextField3.getText().isBlank()) {
//            sellingPrice = Double.parseDouble(jFormattedTextField3.getText());
//        }
//        if (!jFormattedTextField4.getText().isBlank()) {
//            buyingPrice = Double.parseDouble(jFormattedTextField4.getText());
//        }
        if (CheckNumerical.isNumeric(jFormattedTextField3.getText())) {
            sellingPriceMin = Double.parseDouble(jFormattedTextField3.getText());
        }
        if (CheckNumerical.isNumeric(jFormattedTextField4.getText())) {
            sellingPriceMax = Double.parseDouble(jFormattedTextField4.getText());
        }

        // choose between min and max selling prices
        if (sellingPriceMin > 0 && sellingPriceMax > 0) {
            if (sellingPriceMin < sellingPriceMax) {
                search += " AND (`price` BETWEEN '" + sellingPriceMin + "' AND '" + sellingPriceMax + "') ";
            } else {
                search += " AND (`price` BETWEEN '" + sellingPriceMax + "' AND '" + sellingPriceMin + "') ";
            }
        } else if (sellingPriceMin > 0 && sellingPriceMax == 0) {
            search += " AND `price` > '" + sellingPriceMin + "' ";
        } else if (sellingPriceMin == 0 && sellingPriceMax > 0) {
            search += " AND `price` < '" + sellingPriceMax + "' ";
        }
        String expFrom = String.valueOf(datePicker4.getDate());
        boolean validExpFrom = validateDate(expFrom);
        String expTo = String.valueOf(datePicker5.getDate());
        boolean validExpTo = validateDate(expTo);

        if (validExpFrom && validExpTo) {
            search += " AND `exp` BETWEEN '" + expFrom + "' AND  '" + expTo + "'  ";
        } else if (validExpFrom && !validExpTo) {
            search += " AND `exp` >= '" + expFrom + "' ";
        } else if (validExpTo && !validExpFrom) {
            search += " AND `exp` <= '" + expTo + "' ";

        }

        String orderBy = "";

        switch (sort) {
            case 0:
                orderBy = "`stock_id` ASC";
                break;
            case 1:
                orderBy = "`stock_id` DESC";
                break;
            case 2:
                orderBy = "`pid` ASC";
                break;
            case 3:
                orderBy = "`pid` DESC";
                break;
            case 4:
                orderBy = "`brand_name` ASC";
                break;
            case 5:
                orderBy = "`brand_name` DESC";
                break;
            case 6:
                orderBy = "`name` ASC";
                break;
            case 7:
                orderBy = "`name` DESC";
                break;
            case 8:
                orderBy = "`price` ASC";
                break;
            case 9:
                orderBy = "`price` DESC";
                break;
            case 10:
                orderBy = "`qty` ASC";
                break;
            case 11:
                orderBy = "`qty` DESC";
                break;
            case 12:
                orderBy = "`exp` ASC";
                break;
            case 13:
                orderBy = "`exp` DESC";
                break;
            default:
                break;
        }

        try {
            ResultSet stockResult = MySQL.executeSearch("SELECT * FROM `stock` INNER JOIN `productsizes` "
                    + "ON `productsizes`.`sizeID` = `stock`.`productSizes_sizeID` INNER JOIN `product` ON "
                    + "`product`.`pid` = `productsizes`.`product_pid` INNER JOIN `brand` ON `brand`.`brand_id` = `product`.`brand_brand_id` "
                    + search + "AND `status_status_id` = '1' ORDER BY " + orderBy + "");

            DefaultTableModel model = (DefaultTableModel) jTable11.getModel();
            model.setRowCount(0);

            while (stockResult.next()) {
                Vector<String> tableRow = new Vector<>();
                tableRow.add(stockResult.getString("stock_id"));
                tableRow.add(stockResult.getString("details"));
                tableRow.add(stockResult.getString("brand_name"));
                tableRow.add(stockResult.getString("name"));
                tableRow.add(stockResult.getString("price"));
                tableRow.add(stockResult.getString("qty"));
                tableRow.add(stockResult.getString("mfd"));
                tableRow.add(stockResult.getString("exp"));
                model.addRow(tableRow);
            }
            jTable11.setModel(model);
        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.WARNING, "Unable to Load stock", e);
            e.printStackTrace();
        }
    }

    private boolean validateDate(String date) {
        if (!date.isBlank()) {
            if (date.matches(Validation.DATE.validation())) {
                return true;
            }
        }
        return false;
    }

    private void loadMiniStock() {
        try {
            ResultSet stockResult = MySQL.executeSearch("SELECT * FROM `stock` INNER JOIN `productsizes` "
                    + "ON `productsizes`.`sizeID` = `stock`.`productSizes_sizeID` INNER JOIN `product` ON "
                    + "`product`.`pid` = `productsizes`.`product_pid` WHERE `status_status_id` = '1' ORDER BY `qty` ASC");

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            while (stockResult.next()) {
                Vector<String> tableRow = new Vector<>();
                tableRow.add(stockResult.getString("stock_id"));
                tableRow.add(stockResult.getString("name"));
                tableRow.add(stockResult.getString("qty"));
                tableRow.add(stockResult.getString("price"));
                tableRow.add(stockResult.getString("mfd"));
                tableRow.add(stockResult.getString("exp"));
                model.addRow(tableRow);
            }
            jTable1.setModel(model);
        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.WARNING, "Unable to Load stock", e);
            e.printStackTrace();
        }
    }

    private void loadProducts() {
        String search = " WHERE ";
        String productName = jTextField7.getText();
        search += " name LIKE '%" + productName + "%' ";

        String productID = jTextField5.getText();
        search += " AND `pid` LIKE '%" + productID + "%'";

        String brandText = String.valueOf(jComboBox7.getSelectedItem());

        if (!brandText.equals("All Brands")) {
            String brand = brandMAp.get(brandText);
            search += " AND `brand_brand_id` = '" + brand + "' ";
        }
        String categoryText = String.valueOf(jComboBox8.getSelectedItem());
        if (!categoryText.equals("All Categories")) {
            String category = categoryMap.get(categoryText);
            search += " AND `Category_cat_id` = '" + category + "' ";
        }

        try {
            ResultSet productSet = MySQL.executeSearch("SELECT * FROM `product` INNER JOIN `category`"
                    + " ON `category`.`cat_id` = `product`.`Category_cat_id` INNER JOIN `brand` ON "
                    + " `brand`.`brand_id` = `product`.`brand_brand_id` " + search);

            DefaultTableModel model = (DefaultTableModel) jTable10.getModel();
            model.setRowCount(0);

            while (productSet.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(productSet.getString("pid"));
                vector.add(productSet.getString("name"));
                vector.add(productSet.getString("brand_name"));
                vector.add(productSet.getString("cat_name"));

                model.addRow(vector);
            }
            jTable10.setModel(model);
        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.WARNING, "Unable to Load products", e);
            e.printStackTrace();
        }
    }

    private void loadInventoryBrandCategory() {
        try {
            ResultSet brandlRs = MySQL.executeSearch("SELECT * FROM `brand`");
            Vector<String> brandVec = new Vector<>();
            brandVec.add("All Brands");
            while (brandlRs.next()) {
                brandVec.add(brandlRs.getString("brand_name"));
                brandMAp.put(brandlRs.getString("brand_name"), brandlRs.getString("brand_id"));
            }
            DefaultComboBoxModel brandModel = new DefaultComboBoxModel(brandVec);
            jComboBox7.setModel(brandModel);

            ResultSet catlRs = MySQL.executeSearch("SELECT * FROM `category`");
            Vector<String> catVec = new Vector<>();
            catVec.add("All Categories");
            while (catlRs.next()) {
                catVec.add(catlRs.getString("cat_name"));
                categoryMap.put(catlRs.getString("cat_name"), catlRs.getString("cat_id"));
            }
            DefaultComboBoxModel catModel = new DefaultComboBoxModel(catVec);
            jComboBox8.setModel(catModel);
        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.SEVERE, "Couldn't connect to db at loadSpecs", e);
            e.printStackTrace();
        }
    }

    private void loadSpecs() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `trainer_specializations`");
            Vector<String> vec = new Vector<>();
            vec.add("All Specializations");
            while (resultSet.next()) {
                vec.add(resultSet.getString("spec_name"));
                specMap.put(resultSet.getString("spec_name"), resultSet.getString("spec_id"));
            }
            DefaultComboBoxModel model = new DefaultComboBoxModel(vec);
            jComboBox6.setModel(model);
        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.SEVERE, "Couldn't connect to db at loadSpecs", e);
            e.printStackTrace();
        }
    }

    private void loadSessionType() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `session_types`");
            Vector<String> vec = new Vector<>();
            vec.add("All types");
            while (resultSet.next()) {
                vec.add(resultSet.getString("sess_type"));
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("sess_type_id"));
                vector.add(resultSet.getString("price"));
                sessionTypeMap.put(resultSet.getString("sess_type"), vector);
            }
            DefaultComboBoxModel model = new DefaultComboBoxModel(vec);
            jComboBox4.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSessionSpecs() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `trainer_specializations`");
            Vector<String> vec = new Vector<>();
            vec.add("All Specializations");
            while (resultSet.next()) {
                vec.add(resultSet.getString("spec_name"));
            }
            DefaultComboBoxModel model = new DefaultComboBoxModel(vec);
            jComboBox5.setModel(model);
        } catch (Exception e) {
        }
    }

    private void loadSessionMembers(int row) {
        try {
            String session = String.valueOf(jTable6.getValueAt(row, 0));

            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `session-members` INNER JOIN `member` "
                    + " ON `member`.`mem_id` = `session-members`.`member_mem_id` WHERE `session_schedule_session_id` = '" + session + "' ");
            DefaultTableModel model = (DefaultTableModel) jTable7.getModel();
            model.setRowCount(0);
            while (resultSet.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("mem_id"));
                vector.add(resultSet.getString("fname") + " " + resultSet.getString("lname"));
                vector.add(resultSet.getString("mobile"));

                model.addRow(vector);
            }
        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.SEVERE, "Couldn't connect to db at loadSpecs on sessions", e);
            e.printStackTrace();
        }
    }

    private void loadStatusMap() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `status`");
            while (resultSet.next()) {
                statusMap.put(resultSet.getString("status"), resultSet.getString("status_id"));
            }
        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.SEVERE, "Couldn't connect to db at loading status MAp", e);
        }
    }

    private void loadTrainers() {

        DefaultTableModel modelTrainerPerformance = (DefaultTableModel) jTable9.getModel();
        modelTrainerPerformance.setRowCount(0);
        jTable9.setModel(modelTrainerPerformance);

        jButton18.setEnabled(false);

        String search = "";
        String trainer_id = jTextField4.getText();
        String spec = String.valueOf(jComboBox6.getSelectedItem());
        String feeFrom = jFormattedTextField1.getText();
        String feeTo = jFormattedTextField2.getText();

        search += " WHERE `trainer_id` LIKE '%" + trainer_id + "%' ";

        if (!spec.equals("All Specializations")) {
            search += " AND `trainer_specializations_spec_id` = '" + specMap.get(spec) + "' ";
        }

        if (feeFrom.equals("")) {
            feeFrom = "0";
        }
        if (feeTo.equals("")) {
            feeTo = "0";
        }

        if (feeFrom.equals("0") && feeTo.equals("0")) {

        } else if (feeFrom.equals("0") && !feeTo.equals("0")) {
            search += " AND (`weekly_payment` < '" + feeTo + "') ";
        } else if (!feeFrom.equals("0") && feeTo.equals("0")) {
            search += " AND (`weekly_payment` > '" + feeFrom + "' ) ";
        } else if (Double.parseDouble(feeFrom) < Double.parseDouble(feeTo)) {
            search += " AND (`weekly_payment` BETWEEN '" + feeFrom + "' AND '" + feeTo + "') ";

        }

        int checkboxCount = 0;
        if (jCheckBox4.isSelected()) {
            checkboxCount += 1;
        }
        if (jCheckBox3.isSelected()) {
            checkboxCount += 2;
        }

        switch (checkboxCount) {
            case 1:
                search += " AND `gender_gender_id` = '1'";
                break;
            case 2:
                search += " AND `gender_gender_id` = '2'";
                break;
            case 3:
                search += " AND (`gender_gender_id` = '1' OR `gender_gender_id` = '2')";
                break;
            default:
                break;
        }

        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `trainers` INNER JOIN `gender` ON"
                    + " `gender`.`gender_id` = `trainers`.`gender_gender_id` INNER JOIN `trainer_specializations` ON "
                    + "`trainer_specializations`.`spec_id` = `trainers`.`trainer_specializations_spec_id` " + search);
            DefaultTableModel model = (DefaultTableModel) jTable8.getModel();
            model.setRowCount(0);

            while (resultSet.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("trainer_id"));
                vector.add(resultSet.getString("fname") + " " + resultSet.getString("lname"));
                vector.add(resultSet.getString("email"));
                vector.add(resultSet.getString("mobile"));
                vector.add(resultSet.getString("experience_years"));
                vector.add(resultSet.getString("spec_name"));
                vector.add(resultSet.getString("weekly_payment"));
                vector.add(resultSet.getString("gender_name"));
                vector.add(resultSet.getString("joined_date"));

                model.addRow(vector);
            }

            jTable8.setModel(model);

        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.SEVERE, "Couldn't connect to db at loadMembers", e);

        }
    }

    private void loadSuppliers() {
        String search;
        search = " WHERE `mobile` LIKE '%" + jTextField6.getText() + "%' AND `email` LIKE '%" + jTextField8.getText() + "%' ";
        String company = String.valueOf(jComboBox10.getSelectedItem());
        if (!company.equals("All Companies")) {
            search += " AND `companiy_com_id` = '" + companyMap.get(company) + "' ";

        }

        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `supplier` INNER JOIN `company` "
                    + "ON `supplier`.`companiy_com_id` = `company`.`com_id`" + search);
            DefaultTableModel model = (DefaultTableModel) jTable14.getModel();
            model.setRowCount(0);

            while (resultSet.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("mobile"));
                vector.add(resultSet.getString("first_name"));
                vector.add(resultSet.getString("last_name"));
                vector.add(resultSet.getString("email"));
                vector.add(resultSet.getString("name"));

                model.addRow(vector);
            }
            jTable14.setModel(model);

        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.SEVERE, "Couldn't connect to db at loading suppliers", e);

        }
    }

    private void loadCompanies() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `company`");
            Vector<String> vector = new Vector<>();
            vector.add("All Companies");
            while (resultSet.next()) {
                vector.add(resultSet.getString("name"));
                companyMap.put(resultSet.getString("name"), resultSet.getString("com_id"));

            }
            DefaultComboBoxModel model = new DefaultComboBoxModel(vector);
            jComboBox10.setModel(model);
        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.SEVERE, "Couldn't connect to db at loading comany combobox in dashboard", e);
        }
    }

    private void loadTrainerDashDetails() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT COUNT(*) AS `total_rows` , COUNT(CASE "
                    + "WHEN `status_status_id` =  4 then 1 END) AS `count_scheduled`, COUNT(CASE WHEN "
                    + "`status_status_id` IN (1,4) THEN 1 END) AS `count_active` FROM `trainers`");

            if (resultSet.next()) {
                jLabel35.setText(resultSet.getString("total_rows"));
                jLabel36.setText(resultSet.getString("count_active"));
                jLabel38.setText(resultSet.getString("count_scheduled"));

            }
        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.SEVERE, "Couldn't connect to db at loading trainer dashboard details", e);
        }
    }

    private void loadMemberships() {
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `memebrship_types`");
            Vector<String> vector = new Vector<>();
            vector.add("All Memberships");
            while (resultSet.next()) {
                vector.add(resultSet.getString("type_name"));
                membershipTypeMap.put(resultSet.getString("type_name"), resultSet.getString("type_id"));
            }

            DefaultComboBoxModel model = new DefaultComboBoxModel(vector);
            jComboBox1.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
            SplashScreen.exceptionRecords.log(Level.WARNING, "Unable to load cities", e);

        }
    }

    private void membershipsLoadMembers() {

        DefaultTableModel modelMemberInvoices = (DefaultTableModel) jTable4.getModel();
        modelMemberInvoices.setRowCount(0);
        jTable4.setModel(modelMemberInvoices);

        jButton23.setEnabled(false);

        String search = "";
        String memberID = jTextField1.getText();
        String mobile = jTextField2.getText();
        String memberType = String.valueOf(jComboBox1.getSelectedItem());

        search += " WHERE `mem_id` LIKE '%" + memberID + "%' AND `mobile` LIKE '%" + mobile + "%'  ";
        int checkboxCount = 0;
        if (jCheckBox1.isSelected()) {
            checkboxCount += 1;
        }
        if (jCheckBox2.isSelected()) {
            checkboxCount += 2;
        }

        switch (checkboxCount) {
            case 1:
                search += " AND `gender_gender_id` = '1'";
                break;
            case 2:
                search += " AND `gender_gender_id` = '2'";
                break;
            case 3:
                search += " AND (`gender_gender_id` = '1' OR `gender_gender_id` = '2')";
                break;
            default:
                break;
        }
        if (!memberType.equals("All Memberships")) {
            String memType = membershipTypeMap.get(memberType);
            search += " AND memebrship_types_type_id = '" + memType + "'";

        } else {

        }
        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `member` INNER JOIN `gender` ON `gender`.`gender_id` = `member`.`gender_gender_id` INNER JOIN `membership_records` "
                    + " ON `membership_records`.`member_mem_id` = `member`.`mem_id` INNER JOIN `memebrship_types` ON `memebrship_types`.`type_id` = `membership_records`.`memebrship_types_type_id` "
                    + " " + search + " ORDER BY `fname` asc");

            DefaultTableModel model = (DefaultTableModel) jTable5.getModel();
            model.setRowCount(0);

            while (resultSet.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("mem_id"));
                vector.add(resultSet.getString("fname") + " " + resultSet.getString("lname"));
                vector.add(resultSet.getString("mobile"));
                vector.add(resultSet.getString("email"));
                vector.add(resultSet.getString("gender_name"));
                vector.add(resultSet.getString("registered_date"));
                vector.add(resultSet.getString("type_name"));
                vector.add(resultSet.getString("expire_date"));

                model.addRow(vector);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadDashboardMemberEXP() {

        try {
            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `member` INNER JOIN `gender` ON `gender`.`gender_id` = `member`.`gender_gender_id` INNER JOIN `membership_records` "
                    + " ON `membership_records`.`member_mem_id` = `member`.`mem_id` INNER JOIN `memebrship_types` ON `memebrship_types`.`type_id` = `membership_records`.`memebrship_types_type_id` "
                    + "  ORDER BY `expire_date` asc");

            DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
            model.setRowCount(0);

            while (resultSet.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("mem_id"));
                vector.add(resultSet.getString("fname") + " " + resultSet.getString("lname"));
//                vector.add(resultSet.getString("mobile"));
//                vector.add(resultSet.getString("email"));
//                vector.add(resultSet.getString("gender_name"));
//                vector.add(resultSet.getString("registered_date"));
                vector.add(resultSet.getString("type_name"));
                vector.add(resultSet.getString("expire_date"));

                model.addRow(vector);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTrainerPerformance() {
        try {
            int row = jTable8.getSelectedRow();

            if (row != -1) {
                String trainer_id = String.valueOf(jTable8.getValueAt(row, 0));

                ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `trainer_performance` INNER JOIN `trainers` "
                        + " ON `trainers`.`trainer_id` = `trainer_performance`.`trainers_trainer_id` "
                        + "WHERE `trainers_trainer_id` = '" + trainer_id + "' ");

                DefaultTableModel model = (DefaultTableModel) jTable9.getModel();
                model.setRowCount(0);

                while (resultSet.next()) {
                    Vector<String> vector = new Vector<>();
                    vector.add(resultSet.getString("trainers_trainer_id"));
                    vector.add(resultSet.getString("fname") + " " + resultSet.getString("lname"));
                    vector.add(resultSet.getString("scheduled"));
                    vector.add(resultSet.getString("completed"));
                    vector.add(resultSet.getString("cancelled"));

                    model.addRow(vector);
                }

            }

        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.WARNING, "Network / Database error! Cannot Load Trainer Performance");
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Couldn't update session status");

            e.printStackTrace();
        }
    }

    private void loadSessions() {
        DefaultTableModel modelSessionMembers = (DefaultTableModel) jTable7.getModel();
        modelSessionMembers.setRowCount(0);
        jTable7.setModel(modelSessionMembers);

        jButton21.setEnabled(false);
        jButton15.setEnabled(false);
        String search = "";
//        
        String sessID = jTextField3.getText();
        String trainerID = jTextField9.getText();

        LocalDate sessionDate = datePicker1.getDate();
        String sessionDateStirng = String.valueOf(sessionDate);

        String sesType = String.valueOf(jComboBox4.getSelectedItem());
        String sesSpec = String.valueOf(jComboBox5.getSelectedItem());

        search += " WHERE `session_id` LIKE '%" + sessID + "%' AND `trainers_trainer_id` LIKE '%" + trainerID + "%' ";

        String today = String.valueOf(LocalDate.now());
        if (datePicker1.getDate() != null) {
            search += " AND `date` = '" + sessionDateStirng + "' ";
        } else {
            search += " AND `date` >= '" + today + "' ";
        }

        if (!sesType.equals("All types")) {
            Vector<String> vector = sessionTypeMap.get(sesType);

            search += " AND `session_types_sess_type_id` = '" + vector.get(0) + "'  ";

        }

        if (!sesSpec.equals("All Specializations")) {
            search += " AND `spec_id` = '" + specMap.get(sesSpec) + "'  ";
        }

        try {

            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `session_schedule` INNER JOIN"
                    + " `trainers` ON `trainers`.`trainer_id` = `session_schedule`.`trainers_trainer_id` INNER JOIN "
                    + "`trainer_specializations` ON `trainer_specializations`.`spec_id` =  `session_schedule`.`trainer_specializations_spec_id` "
                    + "INNER JOIN `session_types` ON `session_types`.`sess_type_id` =  `session_schedule`.`session_types_sess_type_id` "
                    + "INNER JOIN `status` ON `status`.`status_id` =  `session_schedule`.`status_status_id`" + search);

            DefaultTableModel model = (DefaultTableModel) jTable6.getModel();
            model.setRowCount(0);

            while (resultSet.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("session_id"));
                vector.add(resultSet.getString("fname") + " " + resultSet.getString("lname"));
                vector.add(resultSet.getString("session_schedule.date"));
                vector.add(resultSet.getString("start_time"));
                vector.add(resultSet.getString("end_time"));
                vector.add(resultSet.getString("sess_type"));
                vector.add(resultSet.getString("spec_name"));
                vector.add(resultSet.getString("price"));
                vector.add(resultSet.getString("status"));
                vector.add(resultSet.getString("trainer_id"));

                model.addRow(vector);
            }
            jTable6.setModel(model);

        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.SEVERE, "Couldn't connect to db at loadMembers", e);
            JOptionPane.showMessageDialog(this, "Network error! Couldn't load sessions.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDashboardSessions() {

        SimpleDateFormat nowFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        String todayDate = nowFormat.format(today);

        try {

            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `session_schedule` INNER JOIN"
                    + " `trainers` ON `trainers`.`trainer_id` = `session_schedule`.`trainers_trainer_id` INNER JOIN "
                    + "`trainer_specializations` ON `trainer_specializations`.`spec_id` =  `session_schedule`.`trainer_specializations_spec_id` "
                    + "INNER JOIN `session_types` ON `session_types`.`sess_type_id` =  `session_schedule`.`session_types_sess_type_id` "
                    + "INNER JOIN `status` ON `status`.`status_id` =  `session_schedule`.`status_status_id` "
                    + " WHERE `date` = '" + todayDate + "'  ORDER BY `start_time` ASC");

            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0);

            while (resultSet.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("fname") + " " + resultSet.getString("lname"));
                vector.add(resultSet.getString("start_time"));
                vector.add(resultSet.getString("sess_type"));
                vector.add(resultSet.getString("spec_name"));
                vector.add(resultSet.getString("price"));
                vector.add(resultSet.getString("status"));

                model.addRow(vector);
            }
            jTable2.setModel(model);

        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.SEVERE, "Couldn't connect to db at loadMembers", e);
            JOptionPane.showMessageDialog(this, "Network error! Couldn't load sessions.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMemberInvoices() {
        int row = jTable5.getSelectedRow();
        try {
            ResultSet resultSet = null;
            if (row == -1) {
                resultSet = MySQL.executeSearch("SELECT * FROM `invoice` INNER JOIN `member` ON"
                        + " `invoice`.`member_mem_id` = `member`.`mem_id` ");

            } else {
                String member = String.valueOf(jTable5.getValueAt(row, 0));
                resultSet = MySQL.executeSearch("SELECT * FROM `invoice` INNER JOIN `member` ON"
                        + " `invoice`.`member_mem_id` = `member`.`mem_id` WHERE `mem_id` = '" + member + "' ");
            }
            DefaultTableModel model = (DefaultTableModel) jTable4.getModel();
            model.setRowCount(0);

            while (resultSet.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(resultSet.getString("fname") + " " + resultSet.getString("lname"));
                vector.add(resultSet.getString("invoice_id"));
                vector.add(resultSet.getString("date"));
                vector.add(resultSet.getString("paid_amount"));
                vector.add(resultSet.getString("staff_staff_id"));

                model.addRow(vector);
            }

            jTable4.setModel(model);

        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.SEVERE, "Couldn't connect to db at loadMembers", e);

        }
    }

    private void init(boolean notify) {

        LogoSettting logo = new LogoSettting();
        logo.setLogo(jLabel3);
        buttons.add(jButton2);
        buttons.add(jButton3);
        buttons.add(jButton4);
        buttons.add(jButton5);
        buttons.add(jButton6);
        buttons.add(jButton16);
        buttons.add(jButton39);

        jButton7.putClientProperty(FlatClientProperties.STYLE, "arc:999");
        for (JButton button : buttons) {
            button.putClientProperty(FlatClientProperties.STYLE, "arc:500");
        }

        tables.add(jTable1);
        tables.add(jTable2);
        tables.add(jTable3);
        tables.add(jTable4);
        tables.add(jTable5);
        tables.add(jTable6);
        tables.add(jTable7);
        tables.add(jTable8);
        tables.add(jTable9);
        tables.add(jTable9);
        tables.add(jTable10);
        tables.add(jTable11);
        tables.add(jTable12);
        tables.add(jTable13);
        tables.add(jTable14);
        tables.add(jTable15);

        modifyTableMap.put(jTable1, jScrollPane1);
        modifyTableMap.put(jTable2, jScrollPane2);
        modifyTableMap.put(jTable3, jScrollPane3);
        modifyTableMap.put(jTable4, jScrollPane4);
        modifyTableMap.put(jTable5, jScrollPane5);
        modifyTableMap.put(jTable6, jScrollPane6);
        modifyTableMap.put(jTable7, jScrollPane7);
        modifyTableMap.put(jTable8, jScrollPane8);
        modifyTableMap.put(jTable9, jScrollPane9);
        modifyTableMap.put(jTable10, jScrollPane10);
        modifyTableMap.put(jTable11, jScrollPane11);
        modifyTableMap.put(jTable12, jScrollPane12);
        modifyTableMap.put(jTable13, jScrollPane13);
        modifyTableMap.put(jTable14, jScrollPane14);
        modifyTableMap.put(jTable15, jScrollPane15);

        jLabel5.setText(SignIn.getEmplyeeID());
        jLabel6.setText(SignIn.getEmployeeName());
        jLabel12.setText(SignIn.getemployeeType());
        jLabel10.setText(SignIn.getloginDate());
        jLabel8.setText(SignIn.getloginTime());

        if (notify) {
            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, 3000l, "Logged in Successfully");
        }

// setting custom table layouts
        ModifyTables modifyTables = new ModifyTables();
        for (JTable table : tables) {
            modifyTables.modifyTables(table, modifyTableMap.get(table), true);
        }
        if (!SignIn.getemployeeType().equals("Administrator")) {
            jButton1.setVisible(false);
            jScrollPane13.setVisible(false);
            jTable13.setVisible(false);
            jLabel62.setVisible(false);
            jButton46.setVisible(false);
            jButton38.setVisible(false);
            jButton24.setVisible(false);
            jButton26.setVisible(false);

        }
        dashButtonChanges(jButton2);
    }

    public JTextField getTrainerIDTextField() {
        return jTextField9;
    }

    public JButton getEditSessionButton() {
        return jButton15;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        statusActive = new javax.swing.JCheckBoxMenuItem();
        statusOngoing = new javax.swing.JCheckBoxMenuItem();
        statusCancelled = new javax.swing.JCheckBoxMenuItem();
        statusEnded = new javax.swing.JCheckBoxMenuItem();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton39 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jPanel23 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jPanel26 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator6 = new javax.swing.JSeparator();
        jPanel4 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jLabel18 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jLabel24 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        jLabel26 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable7 = new javax.swing.JTable();
        jLabel28 = new javax.swing.JLabel();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton45 = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JSeparator();
        datePicker1 = new com.github.lgooddatepicker.components.DatePicker();
        jLabel29 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox<>();
        jLabel32 = new javax.swing.JLabel();
        jComboBox5 = new javax.swing.JComboBox<>();
        jTextField9 = new javax.swing.JTextField();
        jButton44 = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTable8 = new javax.swing.JTable();
        jLabel40 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox<>();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jFormattedTextField2 = new javax.swing.JFormattedTextField();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTable9 = new javax.swing.JTable();
        jLabel49 = new javax.swing.JLabel();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jPanel19 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel65 = new javax.swing.JLabel();
        jComboBox10 = new javax.swing.JComboBox<>();
        jLabel66 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jScrollPane14 = new javax.swing.JScrollPane();
        jTable14 = new javax.swing.JTable();
        jLabel73 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jPanel21 = new javax.swing.JPanel();
        jScrollPane15 = new javax.swing.JScrollPane();
        jTable15 = new javax.swing.JTable();
        jLabel67 = new javax.swing.JLabel();
        jButton40 = new javax.swing.JButton();
        jButton41 = new javax.swing.JButton();
        jButton42 = new javax.swing.JButton();
        jButton43 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        jComboBox7 = new javax.swing.JComboBox<>();
        jLabel53 = new javax.swing.JLabel();
        jComboBox8 = new javax.swing.JComboBox<>();
        jLabel54 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTable10 = new javax.swing.JTable();
        jPanel17 = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTable11 = new javax.swing.JTable();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jComboBox9 = new javax.swing.JComboBox<>();
        jLabel57 = new javax.swing.JLabel();
        jFormattedTextField3 = new javax.swing.JFormattedTextField();
        jLabel58 = new javax.swing.JLabel();
        jFormattedTextField4 = new javax.swing.JFormattedTextField();
        jLabel59 = new javax.swing.JLabel();
        datePicker4 = new com.github.lgooddatepicker.components.DatePicker();
        jLabel60 = new javax.swing.JLabel();
        datePicker5 = new com.github.lgooddatepicker.components.DatePicker();
        jButton28 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jLabel61 = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        jTable12 = new javax.swing.JTable();
        jScrollPane13 = new javax.swing.JScrollPane();
        jTable13 = new javax.swing.JTable();
        jLabel62 = new javax.swing.JLabel();
        jButton32 = new javax.swing.JButton();
        jButton34 = new javax.swing.JButton();
        jButton35 = new javax.swing.JButton();
        jButton37 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jButton38 = new javax.swing.JButton();
        jButton46 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        profileImage = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();

        statusActive.setSelected(true);
        statusActive.setText("Active");
        statusActive.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        statusActive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusActiveActionPerformed(evt);
            }
        });
        jPopupMenu1.add(statusActive);

        statusOngoing.setText("Ongoing");
        statusOngoing.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        statusOngoing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusOngoingActionPerformed(evt);
            }
        });
        jPopupMenu1.add(statusOngoing);

        statusCancelled.setText("Cancelled");
        statusCancelled.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        statusCancelled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusCancelledActionPerformed(evt);
            }
        });
        jPopupMenu1.add(statusCancelled);

        statusEnded.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        statusEnded.setText("End");
        statusEnded.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusEndedActionPerformed(evt);
            }
        });
        jPopupMenu1.add(statusEnded);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("DashBoard | FlexGym");
        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(1557, 837));
        setUndecorated(true);
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-speedometer-30 white.png"))); // NOI18N
        jButton2.setText("Dashboard");
        jButton2.setBackground(new java.awt.Color(255, 111, 0));
        jButton2.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setOpaque(true);
        jButton2.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-speedometer-30 black.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-people-30.png"))); // NOI18N
        jButton3.setText("Memberships");
        jButton3.setBackground(new java.awt.Color(240, 240, 240));
        jButton3.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(46, 59, 78));
        jButton3.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-people-30-black.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-calendar-white.png"))); // NOI18N
        jButton4.setText("Sessions");
        jButton4.setBackground(new java.awt.Color(240, 240, 240));
        jButton4.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(46, 59, 78));
        jButton4.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-calendar-black.png"))); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-trolley-30.png"))); // NOI18N
        jButton5.setText("Inventory");
        jButton5.setBackground(new java.awt.Color(240, 240, 240));
        jButton5.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButton5.setForeground(new java.awt.Color(46, 59, 78));
        jButton5.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-trolley-30 black.png"))); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-report-white.png"))); // NOI18N
        jButton6.setText("Sales/Invoice");
        jButton6.setBackground(new java.awt.Color(240, 240, 240));
        jButton6.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(46, 59, 78));
        jButton6.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-report-black.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-logout-32.png"))); // NOI18N
        jButton7.setText("Logout");
        jButton7.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-personal-trainer-30 white.png"))); // NOI18N
        jButton16.setText("Trainers");
        jButton16.setBackground(new java.awt.Color(240, 240, 240));
        jButton16.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton16.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-personal-trainer-30 black.png"))); // NOI18N
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-supplier-30 white.png"))); // NOI18N
        jButton39.setText("Suppliers");
        jButton39.setBackground(new java.awt.Color(240, 240, 240));
        jButton39.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton39.setForeground(new java.awt.Color(46, 59, 78));
        jButton39.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-supplier-30 black.png"))); // NOI18N
        jButton39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton39ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(68, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 20, Short.MAX_VALUE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jButton39, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 22, Short.MAX_VALUE)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        jTabbedPane1.setAutoscrolls(true);
        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setEnabled(false);
        jTabbedPane1.setFont(new java.awt.Font("Poppins", 0, 10)); // NOI18N
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(302, 200));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(300, 300));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jButton1.setText("Staff Registration");
        jButton1.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(46, 59, 78));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Stock ID", "Product", "Quantity", "Selling Price", "MFD", "EXP"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        jLabel2.setText("Inventory");
        jLabel2.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(46, 59, 78));

        jLabel14.setText("Sessions Today");
        jLabel14.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(46, 59, 78));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Trainer", "Time", "Type", "Spec", "Fee", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTable2.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jTable2);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jPanel22.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 153, 0)));

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-people-30-black.png"))); // NOI18N
        jLabel15.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(46, 59, 78));

        jLabel25.setText("Total Members");
        jLabel25.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("150");
        jLabel30.setFont(new java.awt.Font("Poppins", 1, 36)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 153, 0));

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel22Layout.createSequentialGroup()
                .addContainerGap(51, Short.MAX_VALUE)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel25)
                .addContainerGap(51, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        jPanel23.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 153, 0)));
        jPanel23.setPreferredSize(new java.awt.Dimension(240, 103));

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-money-30.png"))); // NOI18N
        jLabel16.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(46, 59, 78));

        jLabel42.setText("Income this month");
        jLabel42.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setText("150");
        jLabel43.setFont(new java.awt.Font("Poppins", 1, 36)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(255, 153, 0));

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel42)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(18, 18, 18)
                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jPanel25.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 153, 0)));
        jPanel25.setPreferredSize(new java.awt.Dimension(240, 103));

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-calendar-white.png"))); // NOI18N
        jLabel21.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(46, 59, 78));

        jLabel69.setText("Sessions Held");
        jLabel69.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N

        jLabel70.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel70.setText("150");
        jLabel70.setFont(new java.awt.Font("Poppins", 1, 36)); // NOI18N
        jLabel70.setForeground(new java.awt.Color(255, 153, 0));

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addContainerGap(52, Short.MAX_VALUE)
                .addComponent(jLabel21)
                .addGap(18, 18, 18)
                .addComponent(jLabel69)
                .addContainerGap(51, Short.MAX_VALUE))
            .addComponent(jLabel70, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addGap(18, 18, 18)
                .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPanel26.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 153, 0)));
        jPanel26.setPreferredSize(new java.awt.Dimension(242, 103));

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-calendar-black.png"))); // NOI18N
        jLabel22.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(46, 59, 78));

        jLabel71.setText("Sessions Scheduled");
        jLabel71.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N

        jLabel72.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel72.setText("150");
        jLabel72.setFont(new java.awt.Font("Poppins", 1, 36)); // NOI18N
        jLabel72.setForeground(new java.awt.Color(255, 153, 0));

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel71)
                .addContainerGap(54, Short.MAX_VALUE))
            .addComponent(jLabel72, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel22)
                    .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel72, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                .addGap(18, 87, Short.MAX_VALUE)
                .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel22, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel25, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jLabel23.setText("Membership Exp dates");
        jLabel23.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(46, 59, 78));

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Membership ID", "Name", "Membership", "EXP"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTable3.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(jTable3);

        jButton10.setText("New Invoice");
        jButton10.setBackground(new java.awt.Color(255, 111, 0));
        jButton10.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("New GRN");
        jButton11.setBackground(new java.awt.Color(255, 111, 0));
        jButton11.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton11.setForeground(new java.awt.Color(255, 255, 255));
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator6, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jScrollPane3)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE))))
                        .addGap(12, 12, 12))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 713, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 713, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(11, 11, 11))
        );

        jTabbedPane1.addTab("Dashboard", jPanel3);

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Membe ID", "Name", "Mobile", "Email", "Gender", "Registered Date", "Membership", "EXP"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable5.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jTable5.getTableHeader().setReorderingAllowed(false);
        jTable5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable5MouseClicked(evt);
            }
        });
        jTable5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable5KeyPressed(evt);
            }
        });
        jScrollPane5.setViewportView(jTable5);

        jLabel18.setText("Member ID");
        jLabel18.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(46, 59, 78));

        jTextField1.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jLabel19.setText("Mobile");
        jLabel19.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(46, 59, 78));

        jTextField2.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
        });

        jCheckBox1.setText("Male");
        jCheckBox1.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jCheckBox1.setForeground(new java.awt.Color(46, 59, 78));
        jCheckBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox1ItemStateChanged(evt);
            }
        });
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jCheckBox2.setText("Female");
        jCheckBox2.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jCheckBox2.setForeground(new java.awt.Color(46, 59, 78));
        jCheckBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox2ItemStateChanged(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jLabel20.setText("Membership");
        jLabel20.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(46, 59, 78));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane5)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 324, Short.MAX_VALUE)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Member Name", "Invoice ID", "Date", "Payment", "Cashier"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable4.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jTable4.getTableHeader().setReorderingAllowed(false);
        jTable4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable4MouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jTable4);

        jLabel24.setText("Member Invoices");
        jLabel24.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(46, 59, 78));

        jButton9.setText("Add New Member");
        jButton9.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton9.setForeground(new java.awt.Color(46, 59, 78));
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton12.setText("Purchase Membership");
        jButton12.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton12.setForeground(new java.awt.Color(46, 59, 78));
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton8.setText("Send Emails");
        jButton8.setEnabled(false);
        jButton8.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton8.setForeground(new java.awt.Color(46, 59, 78));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton22.setText("Print Member Report");
        jButton22.setBackground(new java.awt.Color(255, 111, 0));
        jButton22.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton22.setForeground(new java.awt.Color(255, 255, 255));
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jButton23.setText("Print Selected Invoice");
        jButton23.setBackground(new java.awt.Color(255, 111, 0));
        jButton23.setEnabled(false);
        jButton23.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton23.setForeground(new java.awt.Color(255, 255, 255));
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 836, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
                            .addComponent(jButton22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(13, 13, 13))
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jSeparator5)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(7, 7, 7))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Membership Management", jPanel4);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Session ID", "Trainer", "Date ", "Start Time", "End Time", "Session Type", "Specification", "fee", "Status", "Trainer ID"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable6.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTable6.getTableHeader().setReorderingAllowed(false);
        jTable6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable6MouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(jTable6);

        jLabel26.setText("Session ID");
        jLabel26.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jTextField3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField3KeyReleased(evt);
            }
        });

        jLabel27.setText("Trainer");
        jLabel27.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(46, 59, 78));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        jTable7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Member ID", "Name", "Mobile"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable7.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTable7.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(jTable7);

        jLabel28.setText("Joined Members");
        jLabel28.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(46, 59, 78));

        jButton13.setText("Create New Session");
        jButton13.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton13.setForeground(new java.awt.Color(46, 59, 78));
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setText("Add Members");
        jButton14.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton14.setForeground(new java.awt.Color(46, 59, 78));
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setText("Edit Session");
        jButton15.setEnabled(false);
        jButton15.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton15.setForeground(new java.awt.Color(46, 59, 78));
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton21.setText("Print Session Report");
        jButton21.setBackground(new java.awt.Color(255, 111, 0));
        jButton21.setEnabled(false);
        jButton21.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton21.setForeground(new java.awt.Color(255, 255, 255));
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jButton45.setText("Print Sessions this month");
        jButton45.setBackground(new java.awt.Color(255, 111, 0));
        jButton45.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton45.setForeground(new java.awt.Color(255, 255, 255));
        jButton45.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton45ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1171, Short.MAX_VALUE)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel12Layout.createSequentialGroup()
                                        .addGap(20, 20, 20)
                                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jButton15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jButton14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel12Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jButton13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jButton45, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(jSeparator7))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton45, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        datePicker1.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        datePicker1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                datePicker1FocusLost(evt);
            }
        });
        datePicker1.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                datePicker1InputMethodTextChanged(evt);
            }
        });
        datePicker1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                datePicker1PropertyChange(evt);
            }
        });

        jLabel29.setText("Date");
        jLabel29.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(46, 59, 78));

        jLabel31.setText("Type");
        jLabel31.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(46, 59, 78));

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox4.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jComboBox4.setForeground(new java.awt.Color(46, 59, 78));
        jComboBox4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox4ItemStateChanged(evt);
            }
        });

        jLabel32.setText("Spec");
        jLabel32.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(46, 59, 78));

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jComboBox5.setForeground(new java.awt.Color(46, 59, 78));
        jComboBox5.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox5ItemStateChanged(evt);
            }
        });

        jTextField9.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTextField9.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField9KeyReleased(evt);
            }
        });

        jButton44.setText("Select");
        jButton44.setBackground(new java.awt.Color(255, 111, 0));
        jButton44.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jButton44.setForeground(new java.awt.Color(249, 249, 249));
        jButton44.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton44ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jScrollPane6)
                        .addContainerGap())
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel27)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton44)
                        .addGap(36, 36, 36)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(datePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox5, 0, 276, Short.MAX_VALUE))))
            .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton44)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel26)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel27)
                        .addComponent(datePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel29)
                        .addComponent(jLabel31)
                        .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel32)
                        .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(13, 13, 13))
        );

        jTabbedPane1.addTab("Training Sessions", jPanel1);

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));

        jLabel33.setText("Trainers");
        jLabel33.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(46, 59, 78));

        jLabel34.setText("Registered Trainers: ");
        jLabel34.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(46, 59, 78));

        jLabel35.setText("total trainers");
        jLabel35.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(46, 59, 78));

        jLabel36.setText("Active trainers");
        jLabel36.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(46, 59, 78));

        jLabel37.setText("Active : ");
        jLabel37.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(46, 59, 78));

        jLabel38.setText("Scheduled trainers");
        jLabel38.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(46, 59, 78));

        jLabel39.setText("Scheduled Trainers : ");
        jLabel39.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(46, 59, 78));

        jTable8.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Trainer ID", "Name", "Email", "Mobile", "Experience (Yrs)", "Specialization", "Fee", "Gender", "Joined Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable8.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTable8.setForeground(new java.awt.Color(46, 59, 78));
        jTable8.getTableHeader().setReorderingAllowed(false);
        jTable8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable8MouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(jTable8);

        jLabel40.setText("Trainer ID");
        jLabel40.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(46, 59, 78));

        jTextField4.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });
        jTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField4KeyReleased(evt);
            }
        });

        jLabel41.setText("Specialization");
        jLabel41.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(46, 59, 78));

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox6.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jComboBox6.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox6ItemStateChanged(evt);
            }
        });

        jLabel45.setText("Search Trainers");
        jLabel45.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(46, 59, 78));

        jLabel46.setText("Fee");
        jLabel46.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(46, 59, 78));

        jLabel47.setText("From");
        jLabel47.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel47.setForeground(new java.awt.Color(46, 59, 78));

        jLabel48.setText("To");
        jLabel48.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(46, 59, 78));

        jFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField1.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jFormattedTextField1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jFormattedTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jFormattedTextField1KeyReleased(evt);
            }
        });

        jFormattedTextField2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField2.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jFormattedTextField2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jFormattedTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jFormattedTextField2KeyReleased(evt);
            }
        });

        jCheckBox4.setText("Male");
        jCheckBox4.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jCheckBox4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox4ItemStateChanged(evt);
            }
        });

        jCheckBox3.setText("Female");
        jCheckBox3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jCheckBox3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox3ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel35)
                        .addGap(71, 71, 71)
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel36)
                        .addGap(76, 76, 76)
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel38)
                        .addContainerGap(464, Short.MAX_VALUE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jScrollPane8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel45)
                            .addComponent(jLabel46)
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel40, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextField4, javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel14Layout.createSequentialGroup()
                                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jComboBox6, 0, 160, Short.MAX_VALUE))
                                .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel14Layout.createSequentialGroup()
                                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel47)
                                        .addComponent(jLabel48))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jFormattedTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                                        .addComponent(jFormattedTextField1)))
                                .addGroup(jPanel14Layout.createSequentialGroup()
                                    .addComponent(jCheckBox4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jCheckBox3))))
                        .addGap(25, 25, 25))))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(jLabel34)
                    .addComponent(jLabel35)
                    .addComponent(jLabel37)
                    .addComponent(jLabel36)
                    .addComponent(jLabel39)
                    .addComponent(jLabel38))
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel45)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel41)
                            .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel46)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel47)
                            .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel48)
                            .addComponent(jFormattedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCheckBox3)
                            .addComponent(jCheckBox4))
                        .addContainerGap(141, Short.MAX_VALUE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));

        jTable9.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Trainer ID", "Name", "Scheduled Sessions", "Completed Sessions", "Cancelled Sessions"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable9.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTable9.setForeground(new java.awt.Color(46, 59, 78));
        jScrollPane9.setViewportView(jTable9);

        jLabel49.setText("Trainer Performance");
        jLabel49.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(46, 59, 78));

        jButton17.setText("Add New Trainer");
        jButton17.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton17.setForeground(new java.awt.Color(46, 59, 78));
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton18.setText("Edit Trainer Details");
        jButton18.setEnabled(false);
        jButton18.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton18.setForeground(new java.awt.Color(46, 59, 78));
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton20.setText("Print Trainer Report");
        jButton20.setBackground(new java.awt.Color(255, 111, 0));
        jButton20.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton20.setForeground(new java.awt.Color(255, 255, 255));
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel49))
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jButton17)
                                .addGap(18, 18, 18)
                                .addComponent(jButton18)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane9)))
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel49)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Trainers", jPanel13);

        jPanel19.setBackground(new java.awt.Color(255, 255, 255));

        jPanel20.setBackground(new java.awt.Color(255, 255, 255));

        jLabel63.setText("Suppliers");
        jLabel63.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel63.setForeground(new java.awt.Color(46, 59, 78));

        jLabel64.setText("Mobile");
        jLabel64.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jTextField6.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTextField6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField6KeyReleased(evt);
            }
        });

        jLabel65.setText("Company");
        jLabel65.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox10.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jComboBox10.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox10ItemStateChanged(evt);
            }
        });

        jLabel66.setText("Email");
        jLabel66.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jTextField8.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTextField8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField8KeyReleased(evt);
            }
        });

        jTable14.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mobile", "First Name", "Last Name", "Email", "Company"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable14.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTable14.getTableHeader().setReorderingAllowed(false);
        jTable14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable14MouseClicked(evt);
            }
        });
        jScrollPane14.setViewportView(jTable14);

        jLabel73.setText("Payments Due");
        jLabel73.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jTextField10.setEditable(false);
        jTextField10.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTextField10.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextField10.setText("0.00");
        jTextField10.setToolTipText("");
        jTextField10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField10KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane14)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jLabel63)
                        .addGap(50, 50, 50)
                        .addComponent(jLabel64)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(jLabel65)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel66)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel73)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel73)
                        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel63)
                        .addComponent(jLabel64)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel65)
                        .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel66)
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel21.setBackground(new java.awt.Color(255, 255, 255));

        jTable15.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "GRN ID", "Date", "Paid Amount", "Supplier", "Employee"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable15.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTable15.getTableHeader().setReorderingAllowed(false);
        jTable15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable15MouseClicked(evt);
            }
        });
        jScrollPane15.setViewportView(jTable15);

        jLabel67.setText("GRN");
        jLabel67.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N

        jButton40.setText("Add Supplier");
        jButton40.setBackground(new java.awt.Color(255, 111, 0));
        jButton40.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton40.setForeground(new java.awt.Color(240, 240, 240));
        jButton40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton40ActionPerformed(evt);
            }
        });

        jButton41.setText("Print Supplier List");
        jButton41.setBackground(new java.awt.Color(255, 111, 0));
        jButton41.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton41.setForeground(new java.awt.Color(240, 240, 240));
        jButton41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton41ActionPerformed(evt);
            }
        });

        jButton42.setText("Suppler GRNs report");
        jButton42.setBackground(new java.awt.Color(255, 111, 0));
        jButton42.setEnabled(false);
        jButton42.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton42.setForeground(new java.awt.Color(240, 240, 240));
        jButton42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton42ActionPerformed(evt);
            }
        });

        jButton43.setText("Edit Supplier");
        jButton43.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton43.setForeground(new java.awt.Color(46, 59, 78));
        jButton43.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton43ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel67)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jScrollPane15)
                        .addGap(28, 28, 28)
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton41, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                            .addComponent(jButton40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(25, 25, 25))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel67)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane15, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jButton40, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton43, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton41, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton42, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Suppliers", jPanel19);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));

        jLabel50.setText("Inventory");
        jLabel50.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel50.setForeground(new java.awt.Color(46, 59, 78));

        jLabel51.setText("Product ID");
        jLabel51.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel51.setForeground(new java.awt.Color(46, 59, 78));

        jTextField5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTextField5.setForeground(new java.awt.Color(46, 59, 78));
        jTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField5KeyReleased(evt);
            }
        });

        jLabel52.setText("Brand");
        jLabel52.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel52.setForeground(new java.awt.Color(46, 59, 78));

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox7.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jComboBox7.setForeground(new java.awt.Color(46, 59, 78));
        jComboBox7.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox7ItemStateChanged(evt);
            }
        });

        jLabel53.setText("Category");
        jLabel53.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel53.setForeground(new java.awt.Color(46, 59, 78));

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox8.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jComboBox8.setForeground(new java.awt.Color(46, 59, 78));
        jComboBox8.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox8ItemStateChanged(evt);
            }
        });

        jLabel54.setText("Product Name");
        jLabel54.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel54.setForeground(new java.awt.Color(46, 59, 78));

        jTextField7.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTextField7.setForeground(new java.awt.Color(46, 59, 78));
        jTextField7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField7KeyReleased(evt);
            }
        });

        jTable10.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Product Name", "Brand", "Category"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable10.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTable10.setForeground(new java.awt.Color(46, 59, 78));
        jTable10.getTableHeader().setReorderingAllowed(false);
        jTable10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable10MouseClicked(evt);
            }
        });
        jScrollPane10.setViewportView(jTable10);

        jPanel17.setBackground(new java.awt.Color(255, 255, 255));

        jTable11.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Stock ID", "Description", "Brand", "Name", "Selling Price", "Quantity", "MFD", "EXP"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable11.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTable11.getTableHeader().setReorderingAllowed(false);
        jScrollPane11.setViewportView(jTable11);

        jLabel55.setText("Stock");
        jLabel55.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel55.setForeground(new java.awt.Color(46, 59, 78));

        jLabel56.setText("Sort by: ");
        jLabel56.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel56.setForeground(new java.awt.Color(46, 59, 78));

        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Stock ID Lowest on top", "Stock ID Highest on top", "Product ID Lowest on top", "Product ID Highest on top", "Brand [A-Z]", "Brand [Z-A]", "Name [A-Z]", "Name [Z-A]", "Selling Price Lowest on top", "Selling Price Highest on top", "Quantity Lowest on top", "Quantity Highest on top", "EXP Closest on top", "EXP Furthest on top" }));
        jComboBox9.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jComboBox9.setForeground(new java.awt.Color(46, 59, 78));
        jComboBox9.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox9ItemStateChanged(evt);
            }
        });

        jLabel57.setText("Selling Price");
        jLabel57.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(46, 59, 78));

        jFormattedTextField3.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jFormattedTextField3.setForeground(new java.awt.Color(46, 59, 78));
        jFormattedTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jFormattedTextField3KeyReleased(evt);
            }
        });

        jLabel58.setText("To");
        jLabel58.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel58.setForeground(new java.awt.Color(46, 59, 78));

        jFormattedTextField4.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jFormattedTextField4.setForeground(new java.awt.Color(46, 59, 78));
        jFormattedTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jFormattedTextField4KeyReleased(evt);
            }
        });

        jLabel59.setText("EXP");
        jLabel59.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel59.setForeground(new java.awt.Color(46, 59, 78));

        datePicker4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        datePicker4.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                datePicker4PropertyChange(evt);
            }
        });

        jLabel60.setText("To");
        jLabel60.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel60.setForeground(new java.awt.Color(46, 59, 78));

        datePicker5.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        datePicker5.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                datePicker5PropertyChange(evt);
            }
        });

        jButton28.setText("Add Stock");
        jButton28.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton28.setForeground(new java.awt.Color(46, 59, 78));
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        jButton30.setText("Print Report");
        jButton30.setBackground(new java.awt.Color(255, 111, 0));
        jButton30.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton30.setForeground(new java.awt.Color(255, 255, 255));
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jScrollPane11)
                        .addGap(26, 26, 26)
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton30, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE))
                        .addGap(26, 26, 26))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(78, 78, 78)
                        .addComponent(jLabel56)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(81, 81, 81)
                        .addComponent(jLabel57)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jFormattedTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel58)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jFormattedTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(72, 72, 72)
                        .addComponent(jLabel59)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(datePicker4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel60)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(datePicker5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel55)
                    .addComponent(jLabel56)
                    .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57)
                    .addComponent(jFormattedTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel58)
                    .addComponent(jFormattedTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel59)
                    .addComponent(datePicker4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel60)
                    .addComponent(datePicker5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jButton28, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton30, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 119, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jButton25.setText("Add Product");
        jButton25.setBackground(new java.awt.Color(255, 111, 0));
        jButton25.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton25.setForeground(new java.awt.Color(255, 255, 255));
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        jButton19.setText("Generate PID");
        jButton19.setBackground(new java.awt.Color(255, 160, 64));
        jButton19.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        jButton19.setForeground(new java.awt.Color(249, 249, 249));
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel51)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel52)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel53)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel54)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel50)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52)
                    .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53)
                    .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton25, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, 1437, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Inventory Management", jPanel8);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jPanel18.setBackground(new java.awt.Color(255, 255, 255));

        jLabel61.setText("Invoice List");
        jLabel61.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel61.setForeground(new java.awt.Color(46, 59, 78));

        jTable12.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Invoice ID", "Date", "Paid Amount", "Discount", "Payment Method", "Customer", "Employee"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable12.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jTable12.setForeground(new java.awt.Color(46, 59, 78));
        jTable12.getTableHeader().setReorderingAllowed(false);
        jTable12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable12MouseClicked(evt);
            }
        });
        jScrollPane12.setViewportView(jTable12);

        jTable13.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Time", "Employee", "Employee ID"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable13.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jTable13.getTableHeader().setReorderingAllowed(false);
        jScrollPane13.setViewportView(jTable13);

        jLabel62.setText("Unsafe Logouts");
        jLabel62.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel62.setForeground(new java.awt.Color(46, 59, 78));

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 1110, Short.MAX_VALUE)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel61)
                            .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel61)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel62)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(23, 23, 23))
        );

        jButton32.setText("Member Report");
        jButton32.setBackground(new java.awt.Color(255, 111, 0));
        jButton32.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton32.setForeground(new java.awt.Color(249, 249, 249));
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton32ActionPerformed(evt);
            }
        });

        jButton34.setText("Sessions Report");
        jButton34.setBackground(new java.awt.Color(255, 111, 0));
        jButton34.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton34.setForeground(new java.awt.Color(249, 249, 249));
        jButton34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton34ActionPerformed(evt);
            }
        });

        jButton35.setText("Trainer Report");
        jButton35.setBackground(new java.awt.Color(255, 111, 0));
        jButton35.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton35.setForeground(new java.awt.Color(249, 249, 249));
        jButton35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton35ActionPerformed(evt);
            }
        });

        jButton37.setText("Stock Report");
        jButton37.setBackground(new java.awt.Color(255, 111, 0));
        jButton37.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton37.setForeground(new java.awt.Color(249, 249, 249));
        jButton37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton37ActionPerformed(evt);
            }
        });

        jButton24.setText("Database Setup");
        jButton24.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        jButton24.setForeground(new java.awt.Color(255, 160, 64));
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        jButton26.setText("Database Backup");
        jButton26.setFont(new java.awt.Font("Poppins SemiBold", 0, 14)); // NOI18N
        jButton26.setForeground(new java.awt.Color(255, 160, 64));
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });

        jButton38.setText("Charts");
        jButton38.setBackground(new java.awt.Color(255, 160, 64));
        jButton38.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton38.setForeground(new java.awt.Color(249, 249, 249));
        jButton38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton38ActionPerformed(evt);
            }
        });

        jButton46.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton46.setForeground(new java.awt.Color(255, 160, 64));
        jButton46.setText("Add Brands");
        jButton46.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton46ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(44, 44, 44)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton32, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton34, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton35, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton37, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton26, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                    .addComponent(jButton38, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(36, 36, 36))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addComponent(jButton32, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton35, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton46, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 186, Short.MAX_VALUE)
                .addComponent(jButton38, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton24)
                .addGap(31, 31, 31))
        );

        jTabbedPane1.addTab("Sales/Invoice", jPanel9);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jPanel5.setBackground(new java.awt.Color(204, 204, 204));

        jLabel1.setText("Dashboard");
        jLabel1.setFont(new java.awt.Font("Poppins", 1, 40)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(46, 59, 78));

        jLabel4.setText("Employee ID : ");
        jLabel4.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(46, 59, 78));

        jLabel5.setText("ADM240905001");
        jLabel5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(46, 59, 78));

        jLabel6.setText("Kovidha Subasinghe Nethsara");
        jLabel6.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(46, 59, 78));

        jLabel7.setText("Employee Name : ");
        jLabel7.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(46, 59, 78));

        jLabel8.setText("15:31");
        jLabel8.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(46, 59, 78));

        jLabel9.setText("Login at");
        jLabel9.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(46, 59, 78));

        jLabel10.setText("2024-09-13");
        jLabel10.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(46, 59, 78));

        jLabel11.setText("Date: ");
        jLabel11.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(46, 59, 78));

        jLabel12.setText("Administrator");
        jLabel12.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(46, 59, 78));

        jLabel13.setText("Role");
        jLabel13.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(46, 59, 78));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel1)
                .addGap(118, 118, 118)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jSeparator2)
                        .addGap(113, 113, 113))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(profileImage, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 563, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(91, 91, 91)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel12))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel6))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(profileImage, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel10))
                                .addGap(4, 4, 4)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel8))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jMenuBar1.setBackground(new java.awt.Color(255, 160, 64));

        jMenu1.setText("Window");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-refresh-20.png"))); // NOI18N
        jMenuItem1.setText("Refresh");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, 0));
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-fullscreen-20.png"))); // NOI18N
        jMenuItem2.setText("Exit Fullscreen");
        jMenuItem2.setEnabled(false);
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/icons8-exit-20.png"))); // NOI18N
        jMenuItem3.setText("Exit");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator1)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        if (FrameStorage.staffRegistration == null) {
            FrameStorage.staffRegistration = new StaffRegistration();
            FrameStorage.staffRegistration.getHome(this);
            FrameStorage.staffRegistration.setVisible(true);
        } else if (FrameStorage.staffRegistration.isVisible()) {
            FrameStorage.staffRegistration.toFront();

        } else {
            FrameStorage.staffRegistration.setVisible(true);
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        dashButtonChanges(jButton2);
        jTabbedPane1.setSelectedIndex(0);

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        dashButtonChanges(jButton3);
        jTabbedPane1.setSelectedIndex(1);

    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        dashButtonChanges(jButton4);
        jTabbedPane1.setSelectedIndex(2);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        dashButtonChanges(jButton5);
        jTabbedPane1.setSelectedIndex(5);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        dashButtonChanges(jButton6);
        jTabbedPane1.setSelectedIndex(6);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        logout("Logout");
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed

        refresh();
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

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        logout("Shutdown");
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        dashButtonChanges(jButton16);
        jTabbedPane1.setSelectedIndex(3);
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        String Employee = SignIn.getEmployeeName();
        LocalDate today = LocalDate.now();
        String date = String.valueOf(today);
        InputStream report = this.getClass().getResourceAsStream("/reports/FlexGymSessionReportBlank_A4.jasper");
        if (jTable6.getSelectedRowCount() == 1) {
            if (jTable7.getRowCount() > 0) {
                int row = jTable6.getSelectedRow();
                String session_id = String.valueOf(jTable6.getValueAt(row, 0));
                String trainer = String.valueOf(jTable6.getValueAt(row, 1));
                String sessDate = String.valueOf(jTable6.getValueAt(row, 2));
                String startTime = String.valueOf(jTable6.getValueAt(row, 3));
                String endTime = String.valueOf(jTable6.getValueAt(row, 4));
                String sessType = String.valueOf(jTable6.getValueAt(row, 5));
                String sessSpec = String.valueOf(jTable6.getValueAt(row, 6));
                String fee = String.valueOf(jTable6.getValueAt(row, 7));
                String status = String.valueOf(jTable6.getValueAt(row, 8));

                int memberCount = jTable7.getRowCount();
                double profit = Double.parseDouble(fee) * (double) memberCount;

                HashMap<String, Object> params = new HashMap<>();
                params.put("Parameter1", Employee);
                params.put("Parameter2", date);
                params.put("Parameter3", session_id);
                params.put("Parameter4", status);
                params.put("Parameter5", sessDate);
                params.put("Parameter6", trainer);
                params.put("Parameter7", startTime);
                params.put("Parameter8", endTime);
                params.put("Parameter9", sessType);
                params.put("Parameter10", sessSpec);
                params.put("Parameter11", fee);
                params.put("Parameter12", String.valueOf(memberCount));
                params.put("Parameter13", String.valueOf(profit));

//                JREmptyDataSource dataSource = new JREmptyDataSource();
                JRTableModelDataSource dataSource = new JRTableModelDataSource(jTable7.getModel());

                try {
                    JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, dataSource);
                    String[] options = new String[]{"Print", "View"};
                    int option = JOptionPane.showOptionDialog(this, "View or Print Invoice", "Choose option", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                    if (option == 0) {
                        JasperPrintManager.printReport(jasperPrint, false);
                    } else {
                        JasperViewer.viewReport(jasperPrint, false);
                    }

                } catch (JRException e) {
                    e.printStackTrace();
                    SplashScreen.exceptionRecords.log(Level.FINE, "JasperReport print cancelled.", e);
                }
            }
        }
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        PurchaseMembership pm = new PurchaseMembership(this, true, "");
        pm.setVisible(true);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton39ActionPerformed
        dashButtonChanges(jButton39);
        jTabbedPane1.setSelectedIndex(4);
    }//GEN-LAST:event_jButton39ActionPerformed

    private void jButton41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton41ActionPerformed
        try {
            String company = String.valueOf(jComboBox10.getSelectedItem());
            InputStream report = this.getClass().getResourceAsStream("/reports/flexGymSupplierList.jasper");

            String date = String.valueOf(LocalDate.now());
            String employee = SignIn.getEmployeeName();

            HashMap<String, Object> params = new HashMap<>();
            params.put("Parameter1", employee);
            params.put("Parameter2", date);
            params.put("Parameter3", company);

            JRTableModelDataSource dataSource = new JRTableModelDataSource(jTable14.getModel());

            JasperPrint jasperReport = JasperFillManager.fillReport(report, params, dataSource);
            String[] options = new String[]{"Print", "View"};
            int option = JOptionPane.showOptionDialog(this, "View or Print Invoice", "Choose option", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (option == 0) {
                JasperPrintManager.printReport(jasperReport, false);
            } else {
                JasperViewer.viewReport(jasperReport, false);
            }
//            JasperPrintManager.printReport(jasperReport, false);
//            int option = JOptionPane.showConfirmDialog(this, "View Report?", "View", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
//            if (option == JOptionPane.YES_OPTION) {
//                JasperViewer.viewReport(jasperReport, false);
//            }

        } catch (Exception e) {
            e.printStackTrace();
            SplashScreen.exceptionRecords.log(Level.FINE, "Failed to print or view suppier list.", e);
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Failed to Print Report");

        }
    }//GEN-LAST:event_jButton41ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        if (FrameStorage.addMemberFrame == null) {
            FrameStorage.addMemberFrame = new AddMember();
            FrameStorage.addMemberFrame.getHome(this);
            FrameStorage.addMemberFrame.setVisible(true);
        } else if (FrameStorage.addMemberFrame.isVisible()) {
            FrameStorage.addMemberFrame.toFront();

        } else {
            FrameStorage.addMemberFrame.setVisible(true);
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        membershipsLoadMembers();

    }//GEN-LAST:event_jTextField1KeyReleased

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyReleased
        membershipsLoadMembers();
    }//GEN-LAST:event_jTextField2KeyReleased

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        membershipsLoadMembers();
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jCheckBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox2ItemStateChanged
        membershipsLoadMembers();
    }//GEN-LAST:event_jCheckBox2ItemStateChanged

    private void jCheckBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox1ItemStateChanged
        membershipsLoadMembers();
    }//GEN-LAST:event_jCheckBox1ItemStateChanged

    private void jTable5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable5MouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            jButton23.setEnabled(false);
            jButton8.setEnabled(true);

            loadMemberInvoices();

        }

    }//GEN-LAST:event_jTable5MouseClicked

    private void jTable4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable4MouseClicked
        jButton23.setEnabled(true);
    }//GEN-LAST:event_jTable4MouseClicked
    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
//        AddTrainers at = new AddTrainers("Add", "");

        if (FrameStorage.addTrainers == null) {
            int row = jTable8.getSelectedRow();
            AddTrainers at = new AddTrainers("Add", "");
            at.getHome(this);
            at.setVisible(true);
            FrameStorage.addTrainers = at;
        } else {
            if (FrameStorage.addTrainers.isVisible()) {
                FrameStorage.addTrainers.toFront();
            } else {
                FrameStorage.addTrainers.setVisible(true);
            }

        }
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jTextField4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyReleased
        loadTrainers();
    }//GEN-LAST:event_jTextField4KeyReleased

    private void jComboBox6ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox6ItemStateChanged
        loadTrainers();
    }//GEN-LAST:event_jComboBox6ItemStateChanged

    private void jFormattedTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField1KeyReleased
        loadTrainers();
    }//GEN-LAST:event_jFormattedTextField1KeyReleased

    private void jFormattedTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField2KeyReleased
        loadTrainers();
    }//GEN-LAST:event_jFormattedTextField2KeyReleased

    private void jCheckBox4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox4ItemStateChanged
        loadTrainers();
    }//GEN-LAST:event_jCheckBox4ItemStateChanged

    private void jCheckBox3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox3ItemStateChanged
        loadTrainers();
    }//GEN-LAST:event_jCheckBox3ItemStateChanged


    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        if (FrameStorage.editTrainers == null) {
            int row = jTable8.getSelectedRow();
            AddTrainers at;
            if (row != -1) {
                String trainer_id = String.valueOf(jTable8.getValueAt(row, 0));
                at = new AddTrainers("Edit", trainer_id);

            } else {
                at = new AddTrainers("Edit", "");

            }
            at.getHome(this);
            at.setVisible(true);
            FrameStorage.editTrainers = at;
        } else {
            if (FrameStorage.editTrainers.isVisible()) {
                FrameStorage.editTrainers.toFront();
            } else {
                FrameStorage.editTrainers.setVisible(true);
            }

        }


    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        if (FrameStorage.createSessionFrame == null) {
            CreateNewSession cns = new CreateNewSession();
            cns.getHome(this);
            cns.setVisible(true);
            FrameStorage.createSessionFrame = cns;
        } else {
            if (FrameStorage.createSessionFrame.isVisible()) {
                FrameStorage.createSessionFrame.toFront();
            } else {
                FrameStorage.createSessionFrame.setVisible(true);
            }

        }

    }//GEN-LAST:event_jButton13ActionPerformed

    private void jTable6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable6MouseClicked

        int selectedCount = jTable6.getSelectedRowCount();
        if (selectedCount == 1) {
            int row = jTable6.getSelectedRow();

            if (evt.getButton() == MouseEvent.BUTTON1) {
                if (row != -1) {
                    loadSessionMembers(row);
                    jButton15.setEnabled(true);
                    jButton21.setEnabled(true);
                }
            }
            if (evt.getButton() == MouseEvent.BUTTON3) {
                if (row != -1) {
                    jPopupMenu1.show(evt.getComponent(), evt.getX(), evt.getY());
                    String status = String.valueOf(jTable6.getValueAt(row, 8));
                    if (status.equals("Active")) {
                        statusActive.setSelected(true);
                        statusCancelled.setSelected(false);
                        statusEnded.setSelected(false);
                        statusOngoing.setSelected(false);

                    } else if (status.equals("Cancelled")) {
                        statusActive.setSelected(false);
                        statusCancelled.setSelected(true);
                        statusEnded.setSelected(false);
                        statusOngoing.setSelected(false);

                    } else if (status.equals("Ongoing")) {
                        statusActive.setSelected(false);
                        statusCancelled.setSelected(false);
                        statusEnded.setSelected(false);
                        statusOngoing.setSelected(true);

                    } else if (status.equals("Ended")) {
                        statusActive.setSelected(false);
                        statusCancelled.setSelected(false);
                        statusEnded.setSelected(true);
                        statusOngoing.setSelected(false);

                    }
                }
            }
        }

    }//GEN-LAST:event_jTable6MouseClicked

    private void statusActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusActiveActionPerformed
        int row = jTable6.getSelectedRow();
        String status = String.valueOf(jTable6.getValueAt(row, 8));

        if (status.equals("Active")) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 3000l, "The session is already active");
        } else if (status.equals("Ended")) {
            if (SignIn.getemployeeType().equals("Administrator")) {
                try {
                    MySQL.executeIUD("UPDATE `session_schedule` SET `status_status_id` = '" + statusMap.get("Active") + "'"
                            + " WHERE `session_id` = '" + String.valueOf(jTable6.getValueAt(row, 0)) + "' ");
                    MySQL.executeIUD("UPDATE `trainer_performance` SET `completed` = `completed`-1"
                            + " WHERE `trainers_trainer_id` = '" + String.valueOf(jTable6.getValueAt(row, 9)) + "' ");

                    Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, 3000l, "Session status updated!");
                    loadSessions();
                } catch (Exception e) {
                    SplashScreen.exceptionRecords.log(Level.WARNING, "Network / Database error! Cannot change session status");
                    Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Couldn't update session status");

                    e.printStackTrace();
                }

            } else {
                JOptionPane.showMessageDialog(this, "This session status has been set to ended. You do not have the authority to change an ended session status."
                        + " Please inform an administrator to proceed with the task", "Unorthorized action!", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            try {

                int option = JOptionPane.showConfirmDialog(this, "Change session status to Active?", "Are you Sure", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (option == JOptionPane.YES_OPTION) {
                    MySQL.executeIUD("UPDATE `session_schedule` SET `status_status_id` = '" + statusMap.get("Active") + "'"
                            + " WHERE `session_id` = '" + String.valueOf(jTable6.getValueAt(row, 0)) + "' ");
                    if (status.equals("Cancelled")) {
                        MySQL.executeIUD("UPDATE `trainer_performance` SET `cancelled` = `cancelled`-1"
                                + " WHERE `trainers_trainer_id` = '" + String.valueOf(jTable6.getValueAt(row, 9)) + "' ");
                    }

                    Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, 3000l, "Session status updated!");
                    loadSessions();
                }

            } catch (Exception e) {
                SplashScreen.exceptionRecords.log(Level.WARNING, "Network / Database error! Cannot change session status");
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Couldn't update session status");

                e.printStackTrace();
            }
        }

    }//GEN-LAST:event_statusActiveActionPerformed

    private void statusOngoingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusOngoingActionPerformed
        int row = jTable6.getSelectedRow();
        String status = String.valueOf(jTable6.getValueAt(row, 8));

        if (status.equals("Ongoing")) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 3000l, "The session is already active");
        } else if (status.equals("Ended")) {
            if (SignIn.getemployeeType().equals("Administrator")) {
                try {
                    MySQL.executeIUD("UPDATE `session_schedule` SET `status_status_id` = '" + statusMap.get("Ongoing") + "'"
                            + " WHERE `session_id` = '" + String.valueOf(jTable6.getValueAt(row, 0)) + "' ");

                    MySQL.executeIUD("UPDATE `trainer_performance` SET `completed` = `completed`-1"
                            + " WHERE `trainers_trainer_id` = '" + String.valueOf(jTable6.getValueAt(row, 9)) + "' ");
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, 3000l, "Session status updated!");
                    loadSessions();
                } catch (Exception e) {
                    SplashScreen.exceptionRecords.log(Level.WARNING, "Network / Database error! Cannot change session status");
                    Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Couldn't update session status");

                    e.printStackTrace();
                }

            } else {
                JOptionPane.showMessageDialog(this, "This session status has been set to ended. You do not have the authority to change an ended session status."
                        + " Please inform an administrator to proceed with the task", "Unorthorized action!", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            try {
                int option = JOptionPane.showConfirmDialog(this, "Change session status to Ongoing?", "Are you Sure", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (option == JOptionPane.YES_OPTION) {
                    MySQL.executeIUD("UPDATE `session_schedule` SET `status_status_id` = '" + statusMap.get("Ongoing") + "'"
                            + " WHERE `session_id` = '" + String.valueOf(jTable6.getValueAt(row, 0)) + "' ");

                    if (status.equals("Cancelled")) {
                        MySQL.executeIUD("UPDATE `trainer_performance` SET `cancelled` = `cancelled`-1"
                                + " WHERE `trainers_trainer_id` = '" + String.valueOf(jTable6.getValueAt(row, 9)) + "' ");
                    }

                    Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, 3000l, "Session status updated!");
                    loadSessions();
                }
            } catch (Exception e) {
                SplashScreen.exceptionRecords.log(Level.WARNING, "Network / Database error! Cannot change session status");
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Couldn't update session status");

                e.printStackTrace();
            }
        }

    }//GEN-LAST:event_statusOngoingActionPerformed

    private void statusCancelledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusCancelledActionPerformed
        int row = jTable6.getSelectedRow();
        String status = String.valueOf(jTable6.getValueAt(row, 8));

        if (status.equals("Cancelled")) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 3000l, "The session is already active");
        } else if (status.equals("Ended")) {
            if (SignIn.getemployeeType().equals("Administrator")) {
                try {
                    MySQL.executeIUD("UPDATE `session_schedule` SET `status_status_id` = '" + statusMap.get("Cancelled") + "'"
                            + " WHERE `session_id` = '" + String.valueOf(jTable6.getValueAt(row, 0)) + "' ");

                    MySQL.executeIUD("UPDATE `trainer_performance` SET `completed` = `completed`-1"
                            + " WHERE `trainers_trainer_id` = '" + String.valueOf(jTable6.getValueAt(row, 9)) + "' ");
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, 3000l, "Session status updated!");
                    loadSessions();
                } catch (Exception e) {
                    SplashScreen.exceptionRecords.log(Level.WARNING, "Network / Database error! Cannot change session status");
                    Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Couldn't update session status");

                    e.printStackTrace();
                }

            } else {
                JOptionPane.showMessageDialog(this, "This session status has been set to ended. You do not have the authority to change an ended session status."
                        + " Please inform an administrator to proceed with the task", "Unorthorized action!", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            try {
                int option = JOptionPane.showConfirmDialog(this, "Change session status to Cancelled?", "Are you Sure", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (option == JOptionPane.YES_OPTION) {
                    MySQL.executeIUD("UPDATE `session_schedule` SET `status_status_id` = '" + statusMap.get("Cancelled") + "'"
                            + " WHERE `session_id` = '" + String.valueOf(jTable6.getValueAt(row, 0)) + "' ");

                    MySQL.executeIUD("UPDATE `trainer_performance` SET `cancelled` = `cancelled`+1"
                            + " WHERE `trainers_trainer_id` = '" + String.valueOf(jTable6.getValueAt(row, 9)) + "' ");
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, 3000l, "Session status updated!");
                    loadSessions();
                }
            } catch (Exception e) {
                SplashScreen.exceptionRecords.log(Level.WARNING, "Network / Database error! Cannot change session status");
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Couldn't update session status");

                e.printStackTrace();
            }
        }

    }//GEN-LAST:event_statusCancelledActionPerformed

    private void statusEndedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusEndedActionPerformed
        int row = jTable6.getSelectedRow();
        String status = String.valueOf(jTable6.getValueAt(row, 8));

        if (status.equals("Ended")) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 3000l, "The session is already active");
        } else {
            try {
                int option = JOptionPane.showConfirmDialog(this, "Change session status to Ended?", "Are you Sure", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (option == JOptionPane.YES_OPTION) {
                    MySQL.executeIUD("UPDATE `session_schedule` SET `status_status_id` = '" + statusMap.get("Ended") + "'"
                            + " WHERE `session_id` = '" + String.valueOf(jTable6.getValueAt(row, 0)) + "' ");

                    MySQL.executeIUD("UPDATE `trainer_performance` SET `completed` = `completed`+1"
                            + " WHERE `trainers_trainer_id` = '" + String.valueOf(jTable6.getValueAt(row, 9)) + "' ");
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, 3000l, "Session status updated!");
                    loadSessions();
                }
            } catch (Exception e) {
                SplashScreen.exceptionRecords.log(Level.WARNING, "Network / Database error! Cannot change session status");
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Couldn't update session status");

                e.printStackTrace();
            }
        }

    }//GEN-LAST:event_statusEndedActionPerformed

    private void jButton44ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton44ActionPerformed
        SelectTrainer st = new SelectTrainer(this, true);
        st.getHome(this);
        st.setVisible(true);
    }//GEN-LAST:event_jButton44ActionPerformed

    private void jTextField3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyReleased
        loadSessions();
    }//GEN-LAST:event_jTextField3KeyReleased

    private void jTextField9KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField9KeyReleased
        loadSessions();
    }//GEN-LAST:event_jTextField9KeyReleased

    private void jComboBox4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox4ItemStateChanged
        loadSessions();
    }//GEN-LAST:event_jComboBox4ItemStateChanged

    private void jComboBox5ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox5ItemStateChanged
        loadSessions();
    }//GEN-LAST:event_jComboBox5ItemStateChanged

    private void datePicker1InputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_datePicker1InputMethodTextChanged
        loadSessions();
    }//GEN-LAST:event_datePicker1InputMethodTextChanged

    private void datePicker1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_datePicker1FocusLost

    }//GEN-LAST:event_datePicker1FocusLost

    private void datePicker1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_datePicker1PropertyChange
        LocalDate newDate = datePicker1.getDate();
        if (newDate != null) {

            loadSessions();
        }
    }//GEN-LAST:event_datePicker1PropertyChange

    private void jTable8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable8MouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (jTable8.getSelectedRowCount() == 1) {
                loadTrainerPerformance();
                jButton18.setEnabled(true);

            }
        }
    }//GEN-LAST:event_jTable8MouseClicked

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        int row = jTable6.getSelectedRow();
        if (row != -1) {
            Vector<String> addToSessionDetails = new Vector<>();
            addToSessionDetails.add(String.valueOf(jTable6.getValueAt(row, 0)));
            addToSessionDetails.add(String.valueOf(jTable6.getValueAt(row, 5)));
            addToSessionDetails.add(String.valueOf(jTable6.getValueAt(row, 6)));
            addToSessionDetails.add(String.valueOf(jTable6.getValueAt(row, 1)));
            addToSessionDetails.add(String.valueOf(jTable6.getValueAt(row, 2)));
            addToSessionDetails.add(String.valueOf(jTable6.getValueAt(row, 3)));
            addToSessionDetails.add(String.valueOf(jTable6.getValueAt(row, 7)));

            AddToSession addToSession = new AddToSession(this, false, addToSessionDetails);
            this.setEnabled(false);
            addToSession.getHome(this);
            addToSession.setVisible(true);
        }


    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        int row = jTable6.getSelectedRow();
        if (row != -1) {
            Vector<String> sessionDetails = new Vector<>();
            sessionDetails.add(String.valueOf(jTable6.getValueAt(row, 0)));
            sessionDetails.add(String.valueOf(jTable6.getValueAt(row, 1)));
            sessionDetails.add(String.valueOf(jTable6.getValueAt(row, 2)));
            sessionDetails.add(String.valueOf(jTable6.getValueAt(row, 3)));

            String startDateString = String.valueOf(jTable6.getValueAt(row, 3));
            LocalTime startDate = LocalTime.parse(startDateString);
            String endDateString = String.valueOf(jTable6.getValueAt(row, 4));
            LocalTime endDate = LocalTime.parse(endDateString);
            long hoursBetween = ChronoUnit.HOURS.between(startDate, endDate);

            sessionDetails.add(String.valueOf(hoursBetween));
            sessionDetails.add(String.valueOf(jTable6.getValueAt(row, 5)));
            sessionDetails.add(String.valueOf(jTable6.getValueAt(row, 6)));
            sessionDetails.add(String.valueOf(jTable6.getValueAt(row, 9)));

            EditSession ES = new EditSession(this, true, sessionDetails);
            ES.getHome(this);
            ES.setVisible(true);

        }

    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton45ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton45ActionPerformed
        printMonthSessionsReport();
    }//GEN-LAST:event_jButton45ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        printTrainerReport();

    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton40ActionPerformed
        if (FrameStorage.addSupplierFrame == null) {
            AddSupplier adsupplier = new AddSupplier(true);
            adsupplier.setVisible(true);
            FrameStorage.addSupplierFrame = adsupplier;
        } else {
            if (FrameStorage.addSupplierFrame.isVisible()) {
                FrameStorage.addSupplierFrame.toFront();
            } else {
                FrameStorage.addSupplierFrame.setVisible(true);
            }

        }
    }//GEN-LAST:event_jButton40ActionPerformed

    private void jTextField6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyReleased
        loadSuppliers();
        DefaultTableModel model = (DefaultTableModel) jTable15.getModel();
        model.setRowCount(0);
    }//GEN-LAST:event_jTextField6KeyReleased

    private void jButton43ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton43ActionPerformed
//        AddSupplier adsupplier = new AddSupplier(false);

        if (FrameStorage.updateSupplierFrame == null) {
            AddSupplier adsupplier = new AddSupplier(false);
            adsupplier.setVisible(true);
            FrameStorage.updateSupplierFrame = adsupplier;
        } else {
            if (FrameStorage.updateSupplierFrame.isVisible()) {
                FrameStorage.updateSupplierFrame.toFront();
            } else {
                FrameStorage.updateSupplierFrame.setVisible(true);
            }

        }
    }//GEN-LAST:event_jButton43ActionPerformed

    private void jComboBox10ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox10ItemStateChanged
        loadSuppliers();
        DefaultTableModel model = (DefaultTableModel) jTable15.getModel();
        model.setRowCount(0);
    }//GEN-LAST:event_jComboBox10ItemStateChanged

    private void jTextField8KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField8KeyReleased
        loadSuppliers();
        DefaultTableModel model = (DefaultTableModel) jTable15.getModel();
        model.setRowCount(0);
    }//GEN-LAST:event_jTextField8KeyReleased

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        generateProdId();
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        if (jTable4.getSelectedRowCount() == 1) {
            int row = jTable4.getSelectedRow();
            if (row != -1) {
                String date = String.valueOf(LocalDate.now());
                String employee = SignIn.getEmployeeName();

                String invID = String.valueOf(jTable4.getValueAt(row, 1));
                if (invID.startsWith("IVM")) {
                    PrintMemberInvoice memberInvoice = new PrintMemberInvoice(this, false, invID);
                    memberInvoice.setVisible(true);

                } else if (invID.startsWith("IVP")) {
//                    report
                    printProductInvoceDB(employee, date, invID);
                } else {
                    Notifications.getInstance().show(Notifications.Type.INFO, 3000l, "Not a membership invoice");
                }

            }
        }
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        String pid = jTextField5.getText();
        String brandName = String.valueOf(jComboBox7.getSelectedItem());

        String catName = String.valueOf(jComboBox8.getSelectedItem());
        String productName = jTextField7.getText();

        if (pid.isBlank()) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 4000l, "Please generate a product ID if there is no barcode ID available.");
        } else if (productName.isBlank()) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 4000l, "Please enter the product name.");
        } else if (brandName.equals("All Brands")) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 4000l, "Please select the brand.");

        } else if (catName.equals("All Categories")) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, 4000l, "Please select the product Category.");

        } else {
            String brandId = brandMAp.get(brandName);
            String catId = categoryMap.get(catName);

            try {
                ResultSet PidResultSet = MySQL.executeSearch("SELECT `pid` FROM `product` WHERE `pid` = '" + pid + "'  ");

                if (PidResultSet.next()) {
                    Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 4000l, "Product ID already exists. Please use another ID.");
                } else {

                    ResultSet productResultSet = MySQL.executeSearch("SELECT `pid` FROM `product` INNER JOIN "
                            + " `brand` ON `brand`.`brand_id` = `product`.`brand_brand_id` INNER JOIN `category` ON"
                            + " `category`.`cat_id` = `product`.`Category_cat_id`  WHERE `name` = '" + productName + "'"
                            + " AND  `category`.`cat_name` = '" + catName + "' AND `brand`.`brand_name` = '" + brandName + "' ");

                    if (productResultSet.next()) {
                        Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 4000l, "Product with the same data already exists.");
                    } else {

                        AddProduct addProductDialog = new AddProduct(this, true);
                        addProductDialog.initDialog(brandName, brandId, catName, catId, pid, productName);

                        addProductDialog.setVisible(true);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                SplashScreen.exceptionRecords.log(Level.WARNING, "Couldn't add new product : ", e);
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Error adding new product. Please Check your connection and try again.");
            }

        }

    }//GEN-LAST:event_jButton25ActionPerformed

    private void jTextField7KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyReleased
        loadProducts();
        loadStock();
    }//GEN-LAST:event_jTextField7KeyReleased

    private void jComboBox7ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox7ItemStateChanged
        loadProducts();
        loadStock();
    }//GEN-LAST:event_jComboBox7ItemStateChanged

    private void jComboBox8ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox8ItemStateChanged
        loadProducts();
        loadStock();
    }//GEN-LAST:event_jComboBox8ItemStateChanged

    private void jTable10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable10MouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (evt.getClickCount() == 1) {
                if (jTable10.getSelectedRowCount() == 1) {
                    int row = jTable10.getSelectedRow();
                    jTextField5.setText(String.valueOf(jTable10.getValueAt(row, 0)));
                    loadStock();
                }
            }
        }
    }//GEN-LAST:event_jTable10MouseClicked

    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        if (FrameStorage.addNewStockFrame == null) {
            FrameStorage.addNewStockFrame = new AddNewStock();
            FrameStorage.addNewStockFrame.getHome(this);
            FrameStorage.addNewStockFrame.setVisible(true);

        } else if (FrameStorage.addNewStockFrame.isVisible()) {
            FrameStorage.addNewStockFrame.toFront();
        } else {
            FrameStorage.addNewStockFrame.setVisible(true);
        }


    }//GEN-LAST:event_jButton28ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        if (FrameStorage.addNewStockFrame == null) {
            FrameStorage.addNewStockFrame = new AddNewStock();
            FrameStorage.addNewStockFrame.setVisible(true);
            FrameStorage.addNewStockFrame.getHome(this);

        } else if (FrameStorage.addNewStockFrame.isVisible()) {
            FrameStorage.addNewStockFrame.toFront();
        } else {
            FrameStorage.addNewStockFrame.setVisible(true);
        }

    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        printMemberReport();
    }//GEN-LAST:event_jButton22ActionPerformed

    private void jComboBox9ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox9ItemStateChanged
        loadStock();
    }//GEN-LAST:event_jComboBox9ItemStateChanged

    private void jTable14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable14MouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            loadSupplierGrn();

        }
    }//GEN-LAST:event_jTable14MouseClicked

    private void jTextField10KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField10KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField10KeyReleased

    private void jTextField5KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyReleased
        int characterCount = jTextField5.getText().length();
        if (characterCount > 18) {
            jTextField5.setText("");
        }
        loadProducts();
        loadStock();

    }//GEN-LAST:event_jTextField5KeyReleased

    private void jFormattedTextField3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField3KeyReleased
        loadStock();
    }//GEN-LAST:event_jFormattedTextField3KeyReleased

    private void jFormattedTextField4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField4KeyReleased
        loadStock();
    }//GEN-LAST:event_jFormattedTextField4KeyReleased

    private void datePicker4PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_datePicker4PropertyChange
        loadStock();
    }//GEN-LAST:event_datePicker4PropertyChange

    private void datePicker5PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_datePicker5PropertyChange
        loadStock();
    }//GEN-LAST:event_datePicker5PropertyChange

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        if (FrameStorage.newInvoiceFrame == null) {
            FrameStorage.newInvoiceFrame = new NewInvoice();
//            FrameStorage.newInvoiceFrame.getHome(this);
            FrameStorage.newInvoiceFrame.setVisible(true);

        } else if (FrameStorage.newInvoiceFrame.isVisible()) {
            FrameStorage.newInvoiceFrame.toFront();
        } else {
            FrameStorage.newInvoiceFrame.setVisible(true);
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        if (FrameStorage.dbSetupFrame == null) {
            SetupDatabase setupDatabase = new SetupDatabase(false);
            setupDatabase.setVisible(true);
        } else if (FrameStorage.dbSetupFrame.isVisible()) {
            FrameStorage.dbSetupFrame.toFront();
        } else {
            FrameStorage.dbSetupFrame.setVisible(true);
        }
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        if (FrameStorage.dbBackupFrame == null) {
            BackupDatabase backupDatabase = new BackupDatabase(false);
            backupDatabase.setVisible(true);
        } else if (FrameStorage.dbBackupFrame.isVisible()) {
            FrameStorage.dbBackupFrame.toFront();
        } else {
            FrameStorage.dbBackupFrame.setVisible(true);
        }
    }//GEN-LAST:event_jButton26ActionPerformed

    private void jTable5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable5KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jButton23.setEnabled(false);

            loadMemberInvoices();

            evt.consume();
        }
    }//GEN-LAST:event_jTable5KeyPressed

    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton32ActionPerformed
        printMemberReport();
    }//GEN-LAST:event_jButton32ActionPerformed

    private void jTable15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable15MouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (evt.getClickCount() == 1) {
                jButton42.setEnabled(true);
            } else if (evt.getClickCount() == 2) {
                // open report
                printSupllierGRN(true);
            }
        }
    }//GEN-LAST:event_jTable15MouseClicked

    private void jButton42ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton42ActionPerformed
        if (jTable15.getSelectedRow() != -1) {
            printSupllierGRN(false);
        }
    }//GEN-LAST:event_jButton42ActionPerformed

    private void jTable12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable12MouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
            if (jTable12.getSelectedRowCount() == 1 && jTable12.getSelectedRow() != -1) {
                int row = jTable12.getSelectedRow();
                String invoiceID = String.valueOf(jTable12.getValueAt(row, 0));
                String date = String.valueOf(LocalDate.now());
                String employee = SignIn.getEmployeeName();
                printProductInvoceDB(employee, date, invoiceID);
            }
        }
    }//GEN-LAST:event_jTable12MouseClicked

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        if (jTable5.getSelectedRowCount() == 1 && jTable5.getSelectedRow() != -1) {
            int row = jTable5.getSelectedRow();
            String memID = String.valueOf(jTable5.getValueAt(row, 0));
            Emailing emailing = new Emailing(this, true, memID);
            emailing.setVisible(true);
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton34ActionPerformed
        printMonthSessionsReport();
    }//GEN-LAST:event_jButton34ActionPerformed

    private void jButton35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton35ActionPerformed
        printTrainerReport();
    }//GEN-LAST:event_jButton35ActionPerformed

    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30ActionPerformed
        printStockReport();
    }//GEN-LAST:event_jButton30ActionPerformed

    private void jButton37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton37ActionPerformed
        printStockReport();
    }//GEN-LAST:event_jButton37ActionPerformed

    private void jButton38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton38ActionPerformed
        Charts charts = new Charts(this, true);
        charts.setVisible(true);
    }//GEN-LAST:event_jButton38ActionPerformed

    private void jButton46ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton46ActionPerformed
        AddBrand addBrand = new AddBrand(this, true);
        addBrand.setVisible(true);
    }//GEN-LAST:event_jButton46ActionPerformed

    private void printSupllierGRN(boolean view) {
//        String grnID = jTable15.getrow
        if (jTable15.getSelectedRowCount() == 1 && jTable15.getSelectedRow() != -1) {
            int row = jTable15.getSelectedRow();
            String grnID = String.valueOf(jTable15.getValueAt(row, 0));
            String date = String.valueOf(LocalDate.now());
            String emp = SignIn.getEmployeeName();

            InputStream report = this.getClass().getResourceAsStream("/reports/FlexGymSupplierGRN.jasper");

            HashMap<String, Object> params = new HashMap<>();
            params.put("Parameter1", emp);
            params.put("Parameter2", date);
            params.put("Parameter3", grnID);

            try {
                JasperPrint jasperReport = JasperFillManager.fillReport(report, params, MySQL.getConnection());
                if (view) {
                    JasperViewer.viewReport(jasperReport, false);
                } else {

                    String[] options = new String[]{"Print", "View"};
                    int option = JOptionPane.showOptionDialog(this, "View or Print Invoice", "Choose option", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                    if (option == 0) {
                        JasperPrintManager.printReport(jasperReport, false);
                    } else {
                        JasperViewer.viewReport(jasperReport, false);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Print Cancelled");
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.github.lgooddatepicker.components.DatePicker datePicker1;
    private com.github.lgooddatepicker.components.DatePicker datePicker4;
    private com.github.lgooddatepicker.components.DatePicker datePicker5;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton43;
    private javax.swing.JButton jButton44;
    private javax.swing.JButton jButton45;
    private javax.swing.JButton jButton46;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox10;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JComboBox<String> jComboBox7;
    private javax.swing.JComboBox<String> jComboBox8;
    private javax.swing.JComboBox<String> jComboBox9;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JFormattedTextField jFormattedTextField3;
    private javax.swing.JFormattedTextField jFormattedTextField4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable10;
    private javax.swing.JTable jTable11;
    private javax.swing.JTable jTable12;
    private javax.swing.JTable jTable13;
    private javax.swing.JTable jTable14;
    private javax.swing.JTable jTable15;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable6;
    private javax.swing.JTable jTable7;
    private javax.swing.JTable jTable8;
    private javax.swing.JTable jTable9;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JLabel profileImage;
    private javax.swing.JCheckBoxMenuItem statusActive;
    private javax.swing.JCheckBoxMenuItem statusCancelled;
    private javax.swing.JCheckBoxMenuItem statusEnded;
    private javax.swing.JCheckBoxMenuItem statusOngoing;
    // End of variables declaration//GEN-END:variables

    private void dashButtonChanges(JButton button) {
        List<JButton> newButtons = new ArrayList<>();
        newButtons.addAll(buttons);

        button.setBackground(new java.awt.Color(255, 111, 0));
        button.setForeground(new java.awt.Color(255, 255, 255));

        int buttonWidth = button.getWidth();
        int buttonHeight = button.getHeight();

        Thread t = new Thread(
                () -> {
                    for (int i = buttonWidth; i <= 350; i += 1) {
                        int finall = i;
                        SwingUtilities.invokeLater(() -> {
                            button.setSize(finall, buttonHeight);

                        });

                        try {

                            Thread.sleep(2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
        );
        t.start();
        newButtons.remove(button);

        for (JButton unPressed : newButtons) {
            unPressed.setBackground(new java.awt.Color(240, 240, 240));
            unPressed.setForeground(new java.awt.Color(46, 59, 78));
            int unpressedWidth = unPressed.getWidth();
            Thread t2 = new Thread(
                    () -> {

                        for (int i = unpressedWidth; i >= 250; i -= 1) {
                            int finall = i;
                            SwingUtilities.invokeLater(() -> {
                                unPressed.setSize(finall, buttonHeight);

                            });

                            try {

                                Thread.sleep(2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
            );
            t2.start();

        }
    }

    private void logout(String logOrClose) {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout from the session?", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
            Date logouttime = new Date();
            SimpleDateFormat logouttimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SplashScreen.loginRecords.log(Level.SEVERE, "Logout : " + SignIn.getEmplyeeID() + " : "+SignIn.getEmployeeName()+" : at "+logouttimeFormat.format(logouttime)+"");

            if (logOrClose.equals("Logout")) {
                this.dispose();
                SignIn login = new SignIn();
                login.setVisible(true);
            } else {
                System.exit(0);
            }

            SignIn.setEmployeeEmail(null);
            SignIn.setEmployeeName(null);
            SignIn.setEmployeeType(null);
            SignIn.setEmplyeeID(null);
            SignIn.setLoginDate(null);
            SignIn.setLoginTime(null);
        }
    }

    private String generateProdId() {
        Date date = new Date();
        Random random = new Random();
        int random3Digit = 100 + random.nextInt(900);

        String empSuffix = "PRD";

        String pid = empSuffix + formatDate("yy", date) + formatDate("MM", date) + formatDate("dd", date) + formatDate("mm", date) + formatDate("HH", date) + formatDate("ss", date) + String.valueOf(random3Digit);

        try {

            ResultSet checkMemID = MySQL.executeSearch("SELECT * FROM `product` WHERE `pid` = '" + pid + "' ");

            if (checkMemID.next()) {
                pid = generateProdId();
            }
        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.WARNING, "Unable to create member id", e);
        }
        jTextField5.setText(pid);
        return pid;
    }

    private String formatDate(String format, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    private void loadSupplierGrn() {
        jButton42.setEnabled(false);
        DefaultTableModel model = (DefaultTableModel) jTable15.getModel();
        model.setRowCount(0);
        if (jTable14.getRowCount() != 0) {
            if (jTable14.getSelectedRowCount() == 1) {
                int row = jTable14.getSelectedRow();
                String mobile = String.valueOf(jTable14.getValueAt(row, 0));

                DecimalFormat deci = new DecimalFormat("0.00");
                double paid_amount = 0.00;
                double cost = 0.00;
                double paymentDue = 0.00;

                try {
                    ResultSet supplierGRNRs = MySQL.executeSearch("SELECT * FROM `grn` WHERE "
                            + " `supplier_mobile` = '" + mobile + "' ");
                    // get the paid amount of the respective grn

                    while (supplierGRNRs.next()) {
                        paid_amount = paid_amount + Double.parseDouble(supplierGRNRs.getString("paid_amount"));

                        // get the price of each item in the grn
                        ResultSet grnItemRs = MySQL.executeSearch("SELECT `price`,`qty` FROM `grn_items` WHERE "
                                + " `grn_grn_id` = '" + supplierGRNRs.getString("grn_id") + "' ");
                        while (grnItemRs.next()) {
                            double price = Double.parseDouble(grnItemRs.getString("price")) * Double.parseDouble(grnItemRs.getString("qty"));
                            cost = cost + price;
                        }
                        Vector<String> rows = new Vector<>();
                        rows.add(supplierGRNRs.getString("grn_id"));
                        rows.add(supplierGRNRs.getString("date"));
                        rows.add(supplierGRNRs.getString("paid_amount"));
                        rows.add(supplierGRNRs.getString("supplier_mobile"));
                        rows.add(supplierGRNRs.getString("staff_staff_id"));
                        model.addRow(rows);
                    }
                    // setting the payment due to textfield
                    paymentDue = cost - paid_amount;
                    jTextField10.setText(deci.format(paymentDue));
                    jTable15.setModel(model);

                } catch (Exception e) {
                    SplashScreen.exceptionRecords.log(Level.WARNING, "Unable to load supplier grn at dashboard", e);
                    Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Couldn't load goods received notes. Please check your connection and try again.");
                }
            }
        }
    }

//    sales/ invoce tab invoice table
    private void loadMemInvoceList() {
        try {
            ResultSet membershipRs = MySQL.executeSearch("SELECT `invoice`.`invoice_id`,`date`,`paid_amount`,`discount`,`method_name`,`member`.`fname`,`member`.`lname`,`staff`.`fname`,`staff`.`lname`   FROM `invoice` INNER JOIN `invoice_item` ON"
                    + "`invoice`.`invoice_id` = `invoice_item`.`invoice_invoice_id` INNER JOIN `payment_method` ON "
                    + "`payment_method`.`method_id` = `invoice`.`payment_method_method_id` INNER JOIN `member` ON"
                    + " `member`.`mem_id` = `invoice`.`member_mem_id` INNER JOIN `staff`"
                    + " ON `staff`.`staff_id` = `invoice`.`staff_staff_id`  GROUP BY `invoice`.`invoice_id` ");
            DefaultTableModel model = (DefaultTableModel) jTable12.getModel();
            model.setRowCount(0);
            while (membershipRs.next()) {
                String invID = membershipRs.getString("invoice_id");
                if (invID.startsWith("IVP")) {
                    Vector<String> membershipsVector = new Vector<>();
                    membershipsVector.add(membershipRs.getString("invoice_id"));
                    membershipsVector.add(membershipRs.getString("date"));
                    membershipsVector.add(membershipRs.getString("paid_amount"));
                    membershipsVector.add(membershipRs.getString("discount"));
                    membershipsVector.add(membershipRs.getString("method_name"));
                    membershipsVector.add(membershipRs.getString("member.fname") + " " + membershipRs.getString("member.lname"));
                    membershipsVector.add(membershipRs.getString("staff.fname") + " " + membershipRs.getString("staff.lname"));

                    model.addRow(membershipsVector);

                }
            }
        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.WARNING, "Unable to load supplier grn at dashboard", e);
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Couldn't load goods received notes. Please check your connection and try again.");
            e.printStackTrace();
        }
    }
//    admin login error notificaitons

    private void loadErrorLogins() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-01 00:00:00");
        String thisMonth = sdf.format(new Date());

        DefaultTableModel model = (DefaultTableModel) jTable13.getModel();
        model.setRowCount(0);
        try {
            ResultSet notificationSet = MySQL.executeSearch("SELECT * FROM `error_notifications` WHERE `date` > '" + thisMonth + "'");
            while (notificationSet.next()) {

                Vector<String> errorElements = seperateErrorElements(notificationSet.getString("notification"));
                if (!errorElements.isEmpty()) {
                    Vector<String> row = new Vector<>();
                    row.add(errorElements.get(3));
                    row.add(errorElements.get(2));
                    row.add(errorElements.get(1));
                    row.add(errorElements.get(0));
                    model.addRow(row);
                }
            }

        } catch (Exception e) {
            SplashScreen.exceptionRecords.log(Level.WARNING, "Unable to load supplier grn at dashboard", e);
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Couldn't load goods received notes. Please check your connection and try again.");
            e.printStackTrace();
        }
    }

    private Vector seperateErrorElements(String input) {
        Vector errrorElements = new Vector();
        String regex = "New Login :([A-Za-z0-9]+) : ([A-Za-z ]+) : at ([0-9]{2}:[0-9]{2}) : ([0-9]{4}-[0-9]{2}-[0-9]{2})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String id = matcher.group(1);
            String name = matcher.group(2);
            String time = matcher.group(3);
            String date = matcher.group(4);

            errrorElements.add(id);
            errrorElements.add(name);
            errrorElements.add(time);
            errrorElements.add(date);
        }

        return errrorElements;
    }

    private void printMemberReport() {
        if (jTable5.getRowCount() != 0) {
            InputStream memberReport = this.getClass().getResourceAsStream("/reports/flexGym_members_reports.jasper");

            HashMap<String, Object> params = new HashMap<>();
            params.put("Parameter1", SignIn.getEmployeeName());
            params.put("Parameter2", String.valueOf(LocalDate.now()));
            params.put("Parameter3", String.valueOf(jComboBox1.getSelectedItem()));

            JRTableModelDataSource dataSource = new JRTableModelDataSource(jTable5.getModel());
            try {
                JasperPrint report = JasperFillManager.fillReport(memberReport, params, dataSource);

                String[] options = new String[]{"Print", "View"};
                int option = JOptionPane.showOptionDialog(this, "View or Print Invoice", "Choose option", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                if (option == 0) {
                    JasperPrintManager.printReport(report, false);
                } else {
                    JasperViewer.viewReport(report, false);
                }
//                JasperPrintManager.printReport(report, false);
//                int option = JOptionPane.showConfirmDialog(this, "View Members Report?", "View Report?", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
//                if (option == JOptionPane.YES_OPTION) {
//                    JasperViewer.viewReport(report, false);
//
//                }
            } catch (Exception e) {
                SplashScreen.exceptionRecords.log(Level.WARNING, "Unable to load supplier grn at dashboard", e);
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Couldn't load goods received notes. Please check your connection and try again.");
                e.printStackTrace();
            }
        }
    }

    private void printProductInvoceDB(String employee, String date, String invID) {
        InputStream report = this.getClass().getResourceAsStream("/reports/productInvoiceDB.jasper");
        HashMap<String, Object> params = new HashMap<>();
        params.put("Parameter1", employee);
        params.put("Parameter2", date);
        params.put("Parameter3", invID);

        try {
            JasperPrint jasperReport = JasperFillManager.fillReport(report, params, MySQL.getConnection());
            String[] options = new String[]{"Print", "View"};
            int option = JOptionPane.showOptionDialog(this, "View or Print Invoice", "Choose option", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (option == 0) {
                JasperPrintManager.printReport(jasperReport, false);
            } else {
                JasperViewer.viewReport(jasperReport, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printMonthSessionsReport() {
        String employee = SignIn.getEmployeeName();
        LocalDate currentDate = LocalDate.now();
        String date = String.valueOf(currentDate);
        String firstDate = String.valueOf(currentDate.withDayOfMonth(1));
        String lastDate = String.valueOf(currentDate.with(TemporalAdjusters.lastDayOfMonth()));

        try {
            InputStream report = this.getClass().getResourceAsStream("/reports/monthSessionReport.jasper");
            InputStream subReport = this.getClass().getResourceAsStream("/reports/sessionReportSubreport.jasper");
            JasperReport subreport2 = (JasperReport) JRLoader.loadObject(subReport);
            HashMap<String, Object> params = new HashMap<>();
            params.put("Parameter1", employee);
            params.put("Parameter2", date);
            params.put("Parameter3", firstDate);
            params.put("Parameter4", lastDate);
            params.put("Parameter5", subreport2);

            JasperPrint jasperReport = JasperFillManager.fillReport(report, params, MySQL.getConnection());
            String[] options = new String[]{"Print", "View"};
            int option = JOptionPane.showOptionDialog(this, "View or Print Invoice", "Choose option", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (option == 0) {
                JasperPrintManager.printReport(jasperReport, false);
            } else {
                JasperViewer.viewReport(jasperReport, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            SplashScreen.exceptionRecords.log(Level.WARNING, "Unable to load session report", e);
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Unable to load session report");
        }
    }

    private void printTrainerReport() {
        try {
            InputStream mainReport = this.getClass().getResourceAsStream("/reports/FlexGymTrainerPerformance.jasper");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/flexgym_db", "root", "KOvi@6112");
            InputStream subReport = this.getClass().getResourceAsStream("/reports/FlexGymTrainerPerformanceSubReport.jasper");
            if (subReport == null) {
                System.out.println("Subreport file not found!");
            } else {
                System.out.println("Subreport file loaded successfully!");
            }
            String employee = SignIn.getEmployeeName();
            SimpleDateFormat todayFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat monthBegin = new SimpleDateFormat("yyyy-MM-01");
            Date date = new Date();
            String today = todayFormat.format(date);
            String monthFirst = monthBegin.format(date);
            HashMap<String, Object> params = new HashMap<>();
            params.put("SUBREPORT_CONNECTION", connection);
            params.put("SUBREPORT_LOCATION", subReport);
            params.put("Parameter1", employee);
            params.put("Parameter2", today);
            params.put("Parameter3", monthFirst);

            if (jTable8.getRowCount() > 0) {
                JRTableModelDataSource dataSource = new JRTableModelDataSource(jTable8.getModel());

                JasperPrint report = JasperFillManager.fillReport(mainReport, params, dataSource);
                String[] options = new String[]{"Print", "View"};
                int option = JOptionPane.showOptionDialog(this, "View or Print Invoice", "Choose option", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                if (option == 0) {
                    JasperPrintManager.printReport(report, false);
                } else {
                    JasperViewer.viewReport(report, false);
                }

            } else {
                JREmptyDataSource dataSource = new JREmptyDataSource();

                JasperPrint report = JasperFillManager.fillReport(mainReport, params, dataSource);
                String[] options = new String[]{"Print", "View"};
                int option = JOptionPane.showOptionDialog(this, "View or Print Invoice", "Choose option", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                if (option == 0) {
                    JasperPrintManager.printReport(report, false);
                } else {
                    JasperViewer.viewReport(report, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            SplashScreen.exceptionRecords.log(Level.WARNING, "Couldn't print report", e);
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Error printing report");
        }
    }

    private void printStockReport() {
        String date = String.valueOf(LocalDate.now());
        String employee = SignIn.getEmployeeName();
        String sellingPriceMin = jFormattedTextField3.getText();
        if (sellingPriceMin.isBlank()) {
            sellingPriceMin = "None";
        }
        String sellingPriceMax = jFormattedTextField4.getText();
        if (sellingPriceMax.isBlank()) {
            sellingPriceMax = "All";
        }
        String minExp = String.valueOf(datePicker4.getDate());
        String maxExp = String.valueOf(datePicker5.getDate());
        if (minExp.equals("null")) {
            minExp = "Today";
        }
        if (maxExp.equals("null")) {
            maxExp = "Forever";
        }

        InputStream report = this.getClass().getResourceAsStream("/reports/flexGymAvailableStockReport.jasper");
        HashMap<String, Object> params = new HashMap<>();
        params.put("Parameter1", employee);
        params.put("Parameter2", date);
        params.put("Parameter3", sellingPriceMin);
        params.put("Parameter4", sellingPriceMax);
        params.put("Parameter5", minExp);
        params.put("Parameter6", maxExp);

        JRTableModelDataSource dataSource = new JRTableModelDataSource(jTable11.getModel());

        try {
            JasperPrint jasperReport = JasperFillManager.fillReport(report, params, dataSource);

            String[] options = new String[]{"Print", "View"};
            int option = JOptionPane.showOptionDialog(this, "View or Print Invoice", "Choose option", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (option == 0) {
                JasperPrintManager.printReport(jasperReport, false);
            } else {
                JasperViewer.viewReport(jasperReport, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            SplashScreen.exceptionRecords.log(Level.WARNING, "Couldn't print Stock report", e);
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Error printing Stock report");
        }

    }

    private void loadOverViewInfo() {
        try {
            ResultSet totalMembers = MySQL.executeSearch("SELECT COUNT(`member`.`mem_id`) AS `totalCount` FROM `member`;");
            if (totalMembers.next()) {
                jLabel30.setText(totalMembers.getString("totalCount"));
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-01");

            String date = sdf.format(new Date());
            ResultSet totalSales = MySQL.executeSearch("SELECT * FROM `invoice` WHERE `invoice`.`date`  > '" + date + "'   ");
            double total = 0;

            while (totalSales.next()) {
                double paidAmount = totalSales.getDouble("paid_amount");
                double discount = totalSales.getDouble("discount");

                total += paidAmount - discount;
            }
            jLabel43.setText(String.valueOf(total));

            ResultSet heldSessions = MySQL.executeSearch("SELECT COUNT(session_schedule.session_id) AS heldCount FROM session_schedule WHERE session_schedule.date > '" + date + "' AND session_schedule.status_status_id = '6'");
            if (heldSessions.next()) {
                jLabel70.setText(heldSessions.getString("heldCount"));
            }
            ResultSet scheduledSessions = MySQL.executeSearch("SELECT COUNT(session_schedule.session_id) AS heldCount FROM session_schedule WHERE session_schedule.date > '" + date + "' AND session_schedule.status_status_id != '3'");
            if (scheduledSessions.next()) {
                jLabel72.setText(scheduledSessions.getString("heldCount"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            SplashScreen.exceptionRecords.log(Level.WARNING, "Couldn't Load Dashboard Info", e);
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Error Loading Dashboard Info");
        }
    }
}
