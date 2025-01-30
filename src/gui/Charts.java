package gui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import model.MySQL;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultKeyedValuesDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import raven.toast.Notifications;

public class Charts extends javax.swing.JDialog {

    private String monthStart;
    private String employee = SignIn.getEmployeeName();
    private String today = String.valueOf(LocalDate.now());

    public Charts(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
    }

    private void init() {
        jPanel3.putClientProperty(FlatClientProperties.STYLE, "arc:90");
        jPanel5.putClientProperty(FlatClientProperties.STYLE, "arc:90");
        jPanel11.putClientProperty(FlatClientProperties.STYLE, "arc:90");
        jPanel8.putClientProperty(FlatClientProperties.STYLE, "arc:90");
        jPanel14.putClientProperty(FlatClientProperties.STYLE, "arc:90");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-01");
        monthStart = sdf.format(new Date());

        loadSessionChart();
        loadSessionCountChart();
        loadMemberRegChart();
        loadInvoiceCountChart();
        loadSalesChart();
        
    }

    private void loadSessionChart() {

        try {

            JFreeChart pieChart;
            ResultSet countSet = MySQL.executeSearch("SELECT \n"
                    + "    'Scheduled' AS status_status_id,\n"
                    + "    COUNT(*) AS count\n"
                    + "FROM session_schedule\n"
                    + "	WHERE session_schedule.date > '2025-01-01'\n"
                    + "UNION ALL\n"
                    + "SELECT \n"
                    + "    'Ongoing' AS status_status_id,\n"
                    + "    COUNT(*) AS count\n"
                    + "FROM session_schedule\n"
                    + "WHERE status_status_id = 5 AND session_schedule.date > '" + monthStart + "'\n"
                    + "UNION ALL\n"
                    + "SELECT \n"
                    + "    'Ended' AS status_status_id,\n"
                    + "    COUNT(*) AS count\n"
                    + "FROM session_schedule\n"
                    + "WHERE status_status_id = 6 AND session_schedule.date > '" + monthStart + "'\n"
                    + "UNION ALL\n"
                    + "SELECT \n"
                    + "    'Cancelled' AS status_status_id,\n"
                    + "    COUNT(*) AS count\n"
                    + "FROM session_schedule\n"
                    + "WHERE status_status_id = 3 AND session_schedule.date > '" + monthStart + "'");

            DefaultPieDataset dataSet = new DefaultKeyedValuesDataset();

            while (countSet.next()) {
                dataSet.setValue(countSet.getString("status_status_id"), countSet.getInt("count"));
            }

            pieChart = ChartFactory.createPieChart("Scheduled Sessions", dataSet, true, true, true);

            // Customize the plot
            PiePlot plot = (PiePlot) pieChart.getPlot();
            plot.setSectionPaint("Scheduled", new Color(102, 178, 255));  // Light Blue
            plot.setSectionPaint("Ongoing", new Color(255, 153, 102));    // Soft Orange
            plot.setSectionPaint("Ended", new Color(153, 102, 255));      // Soft Purple
            plot.setSectionPaint("Cancelled", new Color(255, 102, 102));  // Soft Red

            pieChart.getTitle().setFont(new java.awt.Font("Poppins", 4, 30));
            pieChart.getLegend().setItemFont(new java.awt.Font("Poppins", 5, 12));

            // Set the plot to be a donut (create a hole in the middle)
            plot.setInteriorGap(0.02);  // Adjust this value to make the hole bigger or smaller
            plot.setOutlineVisible(false);

            // Remove default label generation and legend formatting
            plot.setLabelFont(new java.awt.Font("Poppins", 5, 12));
//            plot.setLabelGenerator(null);
            plot.setShadowPaint(null);
            // Center text (the total value)
            String centralValue = "457";
            plot.setSimpleLabels(true);
            plot.setCircular(true);

            // Customize chart appearance
            pieChart.setBackgroundPaint(Color.white);

            plot.setBackgroundPaint(new java.awt.Color(252, 252, 252));
            pieChart.setBackgroundPaint(new java.awt.Color(252, 252, 252));

            // Create ChartPanel
            ChartPanel chartPanel = new ChartPanel(pieChart);
            chartPanel.setPreferredSize(new Dimension(800, 400));

            jPanel2.setLayout(new BorderLayout());
            jPanel2.add(chartPanel, BorderLayout.CENTER);
            jPanel2.validate();
        } catch (Exception e) {
            e.printStackTrace();
            SplashScreen.exceptionRecords.log(Level.WARNING, "Couldn't load session chart");
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Couldn't load session chart");
        }

    }

    private void loadSalesChart() {
        try {
            JFreeChart chart;
            ResultSet registrationSet = MySQL.executeSearch("SELECT \n"
                    + "    DATE_FORMAT(date, '%Y-%m') AS month,\n"
                    + "    SUM(paid_amount) AS total_sales\n"
                    + "FROM invoice\n"
                    + "GROUP BY DATE_FORMAT(date, '%Y-%m')\n"
                    + "ORDER BY month;");

            TimeSeries series = new TimeSeries("Sales");

            while (registrationSet.next()) {
                String monthStr = registrationSet.getString("month");
                int count = registrationSet.getInt("total_sales");

                int year = Integer.parseInt(monthStr.substring(0, 4));
                int month = Integer.parseInt(monthStr.substring(5, 7));

                series.add(new Month(month, year), count);
            }

            TimeSeriesCollection seriesSet = new TimeSeriesCollection();
            seriesSet.addSeries(series);
            XYDataset dataSet = seriesSet;

            chart = ChartFactory.createTimeSeriesChart("Sales per month", "Date", "Sales", dataSet, false, true, false);
            XYPlot plot = (XYPlot) chart.getPlot();
            plot.setDomainPannable(true);
            plot.setRangePannable(true);

            chart.setBackgroundPaint(Color.WHITE);
            chart.getTitle().setFont(new java.awt.Font("Poppins", 4, 20));

            Font axisFont = new Font("Poppins", Font.PLAIN, 10);
            Font axisFont2 = new Font("Poppins", Font.PLAIN, 16);
            plot.getDomainAxis().setLabelFont(axisFont2);  // X-axis label
            plot.getDomainAxis().setTickLabelFont(axisFont); // X-axis ticks
            plot.getRangeAxis().setLabelFont(axisFont2);   // Y-axis label
            plot.getRangeAxis().setTickLabelFont(axisFont); // Y-axis ticks
            // Remove dark plot background
            plot.setBackgroundPaint(Color.WHITE);
            plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
            plot.setOutlineVisible(false);

            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
            renderer.setSeriesPaint(0, Color.BLUE);
            renderer.setSeriesStroke(0, new BasicStroke(2.0f));
            plot.setRenderer(renderer);

            DateAxis dateAxis = (DateAxis) plot.getDomainAxis();
            dateAxis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(800, 450));
//            setContentPane(chartPanel);[869, 490]

            jPanel15.setLayout(new BorderLayout());
            jPanel15.add(chartPanel, BorderLayout.CENTER);
            jPanel15.validate();
        } catch (Exception e) {
            e.printStackTrace();
            SplashScreen.exceptionRecords.log(Level.WARNING, "Couldn't load session chart");
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Couldn't load session chart");
        }
    }

    private void loadSessionCountChart() {
        try {
            JFreeChart chart;
            ResultSet registrationSet = MySQL.executeSearch("SELECT DATE_FORMAT(`date`, '%Y-%m') "
                    + "AS month, COUNT(*) AS COUNT FROM session_schedule GROUP BY month ORDER BY month;");

            TimeSeries series = new TimeSeries("Sessions");

            while (registrationSet.next()) {
                String monthStr = registrationSet.getString("month");
                int count = registrationSet.getInt("count");

                int year = Integer.parseInt(monthStr.substring(0, 4));
                int month = Integer.parseInt(monthStr.substring(5, 7));

                series.add(new Month(month, year), count);
            }

            TimeSeriesCollection seriesSet = new TimeSeriesCollection();
            seriesSet.addSeries(series);
            XYDataset dataSet = seriesSet;

            chart = ChartFactory.createTimeSeriesChart("Sessions scheduled per month", "Date", "Number of Sessions", dataSet, false, true, false);
            XYPlot plot = (XYPlot) chart.getPlot();
            plot.setDomainPannable(true);
            plot.setRangePannable(true);

            chart.setBackgroundPaint(Color.WHITE);
            chart.getTitle().setFont(new java.awt.Font("Poppins", 4, 20));

            Font axisFont = new Font("Poppins", Font.PLAIN, 10);
            Font axisFont2 = new Font("Poppins", Font.PLAIN, 14);
            plot.getDomainAxis().setLabelFont(axisFont2);  // X-axis label
            plot.getDomainAxis().setTickLabelFont(axisFont); // X-axis ticks
            plot.getRangeAxis().setLabelFont(axisFont2);   // Y-axis label
            plot.getRangeAxis().setTickLabelFont(axisFont); // Y-axis ticks
            // Remove dark plot background
            plot.setBackgroundPaint(Color.WHITE);
            plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
            plot.setOutlineVisible(false);

            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
            renderer.setSeriesPaint(0, Color.BLUE);
            renderer.setSeriesStroke(0, new BasicStroke(2.0f));
            plot.setRenderer(renderer);

            DateAxis dateAxis = (DateAxis) plot.getDomainAxis();
            dateAxis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(800, 450));
//            setContentPane(chartPanel);[869, 490]

            jPanel9.setLayout(new BorderLayout());
            jPanel9.add(chartPanel, BorderLayout.CENTER);
            jPanel9.validate();
        } catch (Exception e) {
            e.printStackTrace();
            SplashScreen.exceptionRecords.log(Level.WARNING, "Couldn't load session chart");
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Couldn't load session chart");
        }
    }

    private void loadInvoiceCountChart() {
        try {
            JFreeChart chart;
            ResultSet registrationSet = MySQL.executeSearch("SELECT DATE_FORMAT(date, '%Y-%m') "
                    + "AS month, COUNT(*) AS COUNT FROM invoice GROUP BY month ORDER BY month;");

            TimeSeries series = new TimeSeries("Invoices");

            while (registrationSet.next()) {
                String monthStr = registrationSet.getString("month");
                int count = registrationSet.getInt("count");

                int year = Integer.parseInt(monthStr.substring(0, 4));
                int month = Integer.parseInt(monthStr.substring(5, 7));

                series.add(new Month(month, year), count);
            }

            TimeSeriesCollection seriesSet = new TimeSeriesCollection();
            seriesSet.addSeries(series);
            XYDataset dataSet = seriesSet;

            chart = ChartFactory.createTimeSeriesChart("Invoices joined per month", "Date", "Number of Invoices", dataSet, true, true, true);
            XYPlot plot = (XYPlot) chart.getPlot();
            plot.setDomainPannable(true);
            plot.setRangePannable(true);

            chart.setBackgroundPaint(Color.WHITE);
            chart.getTitle().setFont(new java.awt.Font("Poppins", 4, 20));

            Font axisFont = new Font("Poppins", Font.PLAIN, 10);
            Font axisFont2 = new Font("Poppins", Font.PLAIN, 14);

            plot.getDomainAxis().setLabelFont(axisFont2);  // X-axis label
            plot.getDomainAxis().setTickLabelFont(axisFont); // X-axis ticks
            plot.getRangeAxis().setLabelFont(axisFont2);   // Y-axis label
            plot.getRangeAxis().setTickLabelFont(axisFont); // Y-axis ticks
            // Remove dark plot background
            plot.setBackgroundPaint(Color.WHITE);
            plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
            plot.setOutlineVisible(false);

            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
            renderer.setSeriesPaint(0, Color.BLUE);
            renderer.setSeriesStroke(0, new BasicStroke(2.0f));
            plot.setRenderer(renderer);

            DateAxis dateAxis = (DateAxis) plot.getDomainAxis();
            dateAxis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(800, 450));
//            setContentPane(chartPanel);[869, 490]

            jPanel12.setLayout(new BorderLayout());
            jPanel12.add(chartPanel, BorderLayout.CENTER);
            jPanel12.validate();
        } catch (Exception e) {
            e.printStackTrace();
            SplashScreen.exceptionRecords.log(Level.WARNING, "Couldn't load session chart");
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000l, "Couldn't load session chart");
        }
    }

    private void loadMemberRegChart() {
        try {
            JFreeChart chart;
            ResultSet registrationSet = MySQL.executeSearch("SELECT DATE_FORMAT(registered_date, '%Y-%m') "
                    + "AS month, COUNT(*) AS count FROM member GROUP BY month ORDER BY month;");

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            while (registrationSet.next()) {
                String monthStr = registrationSet.getString("month");
                int count = registrationSet.getInt("count");

                dataset.addValue(count, "Members", monthStr);
            }

            chart = ChartFactory.createBarChart(
                    "Members Joined Per Month", // Chart title
                    "Month", // X-Axis Label
                    "Number of Members", // Y-Axis Label
                    dataset, // Dataset
                    PlotOrientation.VERTICAL, // Orientation
                    false, // Legend
                    true, // Tooltips
                    false // URLs
            );

            CategoryPlot plot = chart.getCategoryPlot();

            // Set background color
            chart.setBackgroundPaint(Color.WHITE);
            plot.setBackgroundPaint(Color.WHITE);
            plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
            plot.setOutlineVisible(false);

            // Set title font
            chart.getTitle().setFont(new java.awt.Font("Poppins", Font.BOLD, 20));

            // Customize axes
            Font axisFont = new Font("Poppins", Font.PLAIN, 12);
            Font axisFont2 = new Font("Poppins", Font.PLAIN, 14);

            plot.getDomainAxis().setLabelFont(axisFont2);  // X-axis label
            plot.getDomainAxis().setTickLabelFont(axisFont); // X-axis ticks
            plot.getRangeAxis().setLabelFont(axisFont2);   // Y-axis label
            plot.getRangeAxis().setTickLabelFont(axisFont); // Y-axis ticks

            // Set bar color
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
//        renderer.setSeriesPaint(0, Color.BLUE);
            Random rand = new Random();
            for (int i = 0; i < dataset.getColumnCount(); i++) {
                
                renderer.setSeriesPaint(i, new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
            }

            renderer.setBarPainter(new StandardBarPainter()); // Removes gradient effect
            renderer.setItemMargin(0.1); // Adjust bar spacing

            // Set category axis labels (rotate for better visibility)
            CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(800, 450));

            jPanel7.setLayout(new BorderLayout());
            jPanel7.add(chartPanel, BorderLayout.CENTER);
            jPanel7.validate();
        } catch (Exception e) {
            e.printStackTrace();
            SplashScreen.exceptionRecords.log(Level.WARNING, "Couldn't load member registration chart");
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_CENTER, 3000L, "Couldn't load member registration chart");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Charts | FlexGym");
        setResizable(false);

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 792, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 422, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(61, 61, 61))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62))
        );

        jTabbedPane1.addTab("Sessions", jPanel1);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 869, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 490, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );

        jTabbedPane1.addTab("Session Count", jPanel6);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 869, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 490, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );

        jTabbedPane1.addTab("Member Registration", jPanel4);

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 869, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 490, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );

        jTabbedPane1.addTab("Invoice Count", jPanel10);

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 869, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 490, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30, 30, 30))
        );

        jTabbedPane1.addTab("Sales", jPanel13);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        FlatMacLightLaf.setup();

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Charts dialog = new Charts(new javax.swing.JFrame(), true);
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
