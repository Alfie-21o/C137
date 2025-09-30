/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Passengers;

import Public.Connect;
import javax.swing.JOptionPane;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author sqmson
 */
public class BrowseFlights extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(BrowseFlights.class.getName());

    Connect con;
    /**
     * Creates new form BrowseFlights
     */
    public BrowseFlights() {
        initComponents();
        
        con = new Connect();
        
        loadAllFlights();
        loadAirlines();
        loadAirports();
        addTableListeners();
    }
    
    private void loadAirlines() {
        try {
            con.pst = con.con.prepareStatement("SELECT AirlineID, Name FROM airlines");
            con.rs = con.pst.executeQuery();
            cmbAirline.removeAllItems();
            cmbAirline.addItem("SELECT AIRLINE");
            while (con.rs.next()) {
                cmbAirline.addItem(con.rs.getInt("AirlineID") + " - " + con.rs.getString("Name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading airlines: " + e.getMessage());
        }
    }
    
    private void loadAirports() {
        try {
            con.pst = con.con.prepareStatement("SELECT AirportID, Name FROM airports");
            con.rs = con.pst.executeQuery();
            cmbAirport.removeAllItems();
            cmbAirport.addItem("SELECT AIRPORT");
            while (con.rs.next()) {
                cmbAirport.addItem(con.rs.getInt("AirportID") + " - " + con.rs.getString("Name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading airports: " + e.getMessage());
        }
    }
    
    private void loadAllFlights() {
        try {
            con.query = """
                SELECT f.FlightID, f.FlightNumber, f.DepartureTime, f.ArrivalTime, f.Status,
                       a.Model AS AircraftModel, al.Name AS AirlineName, ap.Name AS ArrivalAirport
                FROM flights f
                JOIN aircrafts a ON f.AircraftID = a.AircraftID
                JOIN airlines al ON a.Airline = al.AirlineID
                JOIN airports ap ON f.ArrivalAirportID = ap.AirportID
            """;

            con.pst = con.con.prepareStatement(con.query);
            con.rs = con.pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel(
                new String[]{"FlightID", "FlightNumber", "Departure", "Arrival", "Status", "Aircraft", "Airline", "Destination"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // make table read-only
                }
            };

            while (con.rs.next()) {
                model.addRow(new Object[]{
                    con.rs.getInt("FlightID"),
                    con.rs.getString("FlightNumber"),
                    con.rs.getString("DepartureTime"),
                    con.rs.getString("ArrivalTime"),
                    con.rs.getString("Status"),
                    con.rs.getString("AircraftModel"),
                    con.rs.getString("AirlineName"),
                    con.rs.getString("ArrivalAirport")
                });
            }

            tblFlights.setModel(model);
            tblFlights.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF); // allow horizontal scrolling

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading flights: " + e.getMessage());
        }
    }
    
    private void addTableListeners() {
        // enables view flights details button when a flight is selected
        tblFlights.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnViewDetails.setEnabled(tblFlights.getSelectedRow() != -1);
            }
        });

        // opens flights details when flight is double-clicked
        tblFlights.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && tblFlights.getSelectedRow() != -1) {
                    openFlightDetails();
                }
            }
        });
        
//        tblFlights.setModel(model);
        tblFlights.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);

        // distribute columns evenly
        int tableWidth = tblFlights.getParent().getWidth(); // jScrollPane width
        int colCount = tblFlights.getColumnCount();
        for (int i = 0; i < colCount; i++) {
            tblFlights.getColumnModel().getColumn(i).setPreferredWidth(tableWidth / colCount);
        }
    }
    
    private void openFlightDetails() {
        int row = tblFlights.getSelectedRow();
        if (row == -1) return;

        int flightId = (int) tblFlights.getValueAt(row, 0);
        PaymentsFrame detailsFrame = new PaymentsFrame(flightId, con);
        this.dispose();
        detailsFrame.setVisible(true);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmbAirline = new javax.swing.JComboBox<>();
        cmbAirport = new javax.swing.JComboBox<>();
        btnSearch = new javax.swing.JButton();
        btnViewDetails = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblFlights = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cmbAirline.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(cmbAirline, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 40, 310, -1));

        cmbAirport.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(cmbAirport, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 80, 310, -1));

        btnSearch.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        getContentPane().add(btnSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 120, -1, -1));

        btnViewDetails.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnViewDetails.setText("Proceed to Book Flight Ticket");
        btnViewDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewDetailsActionPerformed(evt);
            }
        });
        getContentPane().add(btnViewDetails, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 560, -1, -1));

        jScrollPane2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(153, 153, 153), null, null));

        tblFlights.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tblFlights.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblFlights.setEditingColumn(0);
        tblFlights.setEditingRow(0);
        jScrollPane2.setViewportView(tblFlights);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 161, 1026, 390));

        jLabel3.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Flight Dashboard");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(-1, 6, 1040, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        try {
            String airline = cmbAirline.getSelectedItem().toString().split(" - ")[0];
            String airport = cmbAirport.getSelectedItem().toString().split(" - ")[0];

            con.query = """
                SELECT f.FlightID, f.FlightNumber, f.DepartureTime, f.ArrivalTime, f.Status,
                       a.Model AS AircraftModel, al.Name AS AirlineName, ap.Name AS ArrivalAirport
                FROM flights f
                JOIN aircrafts a ON f.AircraftID = a.AircraftID
                JOIN airlines al ON a.Airline = al.AirlineID
                JOIN airports ap ON f.ArrivalAirportID = ap.AirportID
                WHERE a.Airline = ? AND f.ArrivalAirportID = ?
            """;

            con.pst = con.con.prepareStatement(con.query);
            con.pst.setInt(1, Integer.parseInt(airline));
            con.pst.setInt(2, Integer.parseInt(airport));
            con.rs = con.pst.executeQuery();

            DefaultTableModel model = new DefaultTableModel(
                new String[]{"FlightID", "FlightNumber", "Departure Time", "Arrival Time", "Flight Status", "Aircraft", "Airline", "Destination Airport"}, 0
            );

            while (con.rs.next()) {
                model.addRow(new Object[]{
                    con.rs.getInt("FlightID"),
                    con.rs.getString("FlightNumber"),
                    con.rs.getString("DepartureTime"),
                    con.rs.getString("ArrivalTime"),
                    con.rs.getString("Status"),
                    con.rs.getString("AircraftModel"),
                    con.rs.getString("AirlineName"),
                    con.rs.getString("ArrivalAirport")
                });
            }

            tblFlights.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage());
        }
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnViewDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewDetailsActionPerformed
        // TODO add your handling code here:
        openFlightDetails();
    }//GEN-LAST:event_btnViewDetailsActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new BrowseFlights().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnViewDetails;
    private javax.swing.JComboBox<String> cmbAirline;
    private javax.swing.JComboBox<String> cmbAirport;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblFlights;
    // End of variables declaration//GEN-END:variables
}
