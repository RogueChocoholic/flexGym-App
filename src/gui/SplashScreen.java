package gui;


import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.Color;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import model.CustomLoginLogFormat;
import model.FormatDate;
import model.LogoSettting;
import model.MySQL;

/**
 *
 * @author kovid
 */
public class SplashScreen extends javax.swing.JFrame {
    
    private static SplashScreen splash;
    
    private static String loginPath;
    public static Logger exceptionRecords = Logger.getLogger("exceptionRecords.txt");
    public static Logger loginRecords = Logger.getLogger("loginRecords.txt");
    
    public SplashScreen() {
        initComponents();
        init();
        splashProgress();
    }
    
    private void init() {
        setBackground(new Color(0, 0, 0, 0));
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/logo.png")));
        jPanel1.setOpaque(false);
        jPanel2.setOpaque(false);
        splashimage.setOpaque(false);
        LogoSettting logo = new LogoSettting();
        logo.setLogo(jLabel1);
        
        try {
            
            String userHome = System.getProperty("user.home");
            Path logDir = Paths.get(userHome, "FlexGymLogs");
            // Create the directory if it doesn't exist
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }
            
            Path exceptionPath = logDir.resolve("flexGym.log");
            
            FileHandler excepionHandler = new FileHandler(exceptionPath.toString(), true);
            excepionHandler.setFormatter(new SimpleFormatter());
            exceptionRecords.addHandler(excepionHandler);
//            exceptionRecords.setUseParentHandlers(false);

            Path loginsPath = logDir.resolve("login.log");
            loginPath = loginsPath.toString();
            FileHandler loginHandler = new FileHandler(loginPath, true);
            loginHandler.setFormatter(new CustomLoginLogFormat());
            loginRecords.addHandler(loginHandler);
            
        } catch (IOException e) {
            exceptionRecords.log(Level.SEVERE, "Logger Creation went ");
            
        }
    }
    
    public static String readLoginLog(String filePath) {
        String lastLine = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                lastLine = currentLine;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lastLine;
    }
    
    private void splashProgress() {
        Thread t = new Thread(new Runnable() {
            boolean openLogin = true;
            boolean logChecks = true;
            
            @Override
            public void run() {
                Random random = new Random();
                int progress = 0;
                
                while (progress < 100) {
                    progress += random.nextInt(5) + 1;
                    if (progress > 100) {
                        progress = 100;
                        
                    }
                    
                    splashProgressBar.setValue(progress);
                    if (progress >= 20 && progress < 40) {
                        progressText.setText("Creating Database Connection...");
                        if (progress > 25) {
                            if (openLogin) {
                                
                                try {
                                    MySQL.createConnection();
                                    
                                } catch (Exception e) {
                                    JOptionPane.showMessageDialog(splash, "An error occured when Connecting to the database, Please try again later", "Database Connection Failed.", JOptionPane.ERROR_MESSAGE);
                                    exceptionRecords.log(Level.SEVERE, "Database Connection Failed");
                                    openLogin = false;
                                    break;
                                }
                            }
                            
                        }
                    } else if (progress >= 40 && progress < 55) {
                        progressText.setText("Creating Loggers...");
                    } else if (progress >= 55 && progress < 75) {
                        progressText.setText("Database Connection successful!");
                    } else if (progress >= 75 && progress < 95) {
                        progressText.setText("Finalizing Security Checks...");
                        if (logChecks) {
                            String lastLine = readLoginLog(loginPath);
                            if (lastLine != null) {
                                if (lastLine.contains("Logout")) {
                                    System.out.println("App was properly logged out last time.");
                                } else {
                                    JOptionPane.showMessageDialog(splash, "The application was not safely logged out of the previous session. This will be reported to the owner. You can continue with logging into the new session now", "Unsafe logout detected.", JOptionPane.WARNING_MESSAGE);
                                    exceptionRecords.log(Level.WARNING, "Unsafe logout from the system detected.");
                                    try {
                                        Date dateTime = new Date();
                                        String notifyDate = FormatDate.getDate(dateTime, "yyyy-MM-dd HH:mm:ss");
                                        
                                        MySQL.executeIUD("INSERT INTO `error_notifications` (`notification`,`date`) "
                                                + "VALUES ('Unsafe logout from the system. last recorded log is as follows" + lastLine + "','" + notifyDate + "') ");
                                    } catch (Exception e) {
                                        exceptionRecords.log(Level.WARNING, "Error notification informing faild. Error failed to report: " + lastLine);
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(splash, "Log file is empty or could not be read.", "Log file not detected", JOptionPane.WARNING_MESSAGE);
                                exceptionRecords.log(Level.WARNING, "Log file is empty or could not be read.");
                                System.out.println("Log file is empty or could not be read.");
                            }
                            logChecks = false;
                        }
                    } else if (progress >= 95) {
                        progressText.setText("Openning application now!");
                        splashProgressBar.setForeground(new java.awt.Color(127, 200, 248));
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                splash.dispose();
                if (openLogin) {
                    SignIn signIn = new SignIn();
                    signIn.setVisible(true);

                }

            }
            
        });
        t.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        splashimage = new javax.swing.JLabel();
        splashProgressBar = new javax.swing.JProgressBar();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        progressText = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(102, 204, 255));
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        splashimage.setBackground(new java.awt.Color(255, 255, 255));
        splashimage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/splashimg.png"))); // NOI18N

        splashProgressBar.setBackground(new java.awt.Color(46, 59, 78));
        splashProgressBar.setForeground(new java.awt.Color(255, 160, 64));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 77, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(splashimage)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(splashProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(66, 66, 66))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(splashimage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(splashProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(277, 0, -1, -1));

        jLabel3.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(46, 59, 78));
        jLabel3.setText("Where Fitness Begins");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(39, 229, -1, 13));

        jLabel4.setFont(new java.awt.Font("Poppins", 1, 48)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(46, 59, 78));
        jLabel4.setText("Flex Gym");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(39, 242, -1, 52));

        progressText.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        progressText.setForeground(new java.awt.Color(255, 111, 0));
        progressText.setText("Loading...");
        jPanel1.add(progressText, new org.netbeans.lib.awtextra.AbsoluteConstraints(39, 430, -1, -1));
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(39, 22, 195, 195));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/roundBG.png"))); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 760, 480));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 794, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        FlatMacLightLaf.setup();
//FlatArcIJTheme.setup();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                splash = new SplashScreen();
                splash.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel progressText;
    private javax.swing.JProgressBar splashProgressBar;
    private javax.swing.JLabel splashimage;
    // End of variables declaration//GEN-END:variables
}
