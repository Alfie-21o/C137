/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Passengers;

import Public.Connect;
import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 *
 * @author sqmson
 */
public class PaymentsFrame extends javax.swing.JFrame {
    
//    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PaymentsFrame.class.getName());
    private final Connect con;
    private final int flightId;
    private final int passengerID;
    /**
     * Creates new form FlightDetails
     * @param flightId
     * @param con
     * @param passengerID
     */
    public PaymentsFrame(int flightId, Connect con, int passengerID) {
        this.flightId = flightId;
        this.con = con;
        this.passengerID = passengerID;
        
        initComponents();
        loadFlightDetails();
        loadFlightClasses();
    }
    
    private void loadFlightDetails() {
        try {
            String sql = """
                SELECT f.FlightNumber, f.DepartureTime, f.ArrivalTime, f.Status,
                       a.Model AS AircraftModel, al.Name AS AirlineName,
                       dep.Name AS DepartureAirport, arr.Name AS ArrivalAirport
                FROM flights f
                JOIN aircrafts a ON f.AircraftID = a.AircraftID
                JOIN airlines al ON a.Airline = al.AirlineID
                JOIN airports dep ON f.DepartureAirportID = dep.AirportID
                JOIN airports arr ON f.ArrivalAirportID = arr.AirportID
                WHERE f.FlightID = ?
            """;

            con.pst = con.con.prepareStatement(sql);
            con.pst.setInt(1, flightId);
            con.rs = con.pst.executeQuery();

            if (con.rs.next()) {
                lblAirline.setText("Airline: " + con.rs.getString("AirlineName"));
                lblAircraft.setText("Aircraft: " + con.rs.getString("AircraftModel"));
                lblRoute.setText("Route: " + con.rs.getString("DepartureAirport") + " → " + con.rs.getString("ArrivalAirport"));
                lblTimes.setText("Times: " + con.rs.getString("DepartureTime") + " → " + con.rs.getString("ArrivalTime"));
                lblStatus.setText("Status: " + con.rs.getString("Status"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading flight details: " + e.getMessage());
        }
    }
    
    private void loadFlightClasses() {
        try {
            String sql = "SELECT DISTINCT ClassName FROM flight_classes WHERE FlightID = ?";
            con.pst = con.con.prepareStatement(sql);
            con.pst.setInt(1, flightId);
            con.rs = con.pst.executeQuery();

            cmbClass.removeAllItems();
            while (con.rs.next()) {
                cmbClass.addItem(con.rs.getString("ClassName"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading flight classes: " + e.getMessage());
        }
    }
    
    private String getAmountForClass(String cls) {
        return switch (cls.toLowerCase()) {
            case "economy" -> "100";
            case "business" -> "300";
            case "first" -> "500";
            default -> "150";
        };
    }
    
    private void handleBooking() {
//        if (cmbClass.getSelectedItem() == null) {
//            JOptionPane.showMessageDialog(this, "Please select a class!");
//            return;
//        }
//
//        String selectedClass = cmbClass.getSelectedItem().toString();
//
//        try {
//            // Insert booking record
//            String insertBooking = "INSERT INTO bookings(UserID, FlightID, Class, Status) VALUES(?, ?, ?, ?)";
//            con.pst = con.con.prepareStatement(insertBooking);
//            con.pst.setInt(1, userId);
//            con.pst.setInt(2, flightId);
//            con.pst.setString(3, selectedClass);
//            con.pst.setString(4, "Pending Payment");
//            int rows = con.pst.executeUpdate();
//
//            if (rows > 0) {
//                // show a payment dialog
//                String amount = getAmountForClass(selectedClass);
//                JOptionPane.showMessageDialog(this, "Booking successful!\nPlease pay: $" + amount);
//
//                // update booking status
//                String updateBooking = "UPDATE bookings SET Status = 'Paid' WHERE UserID = ? AND FlightID = ?";
//                con.pst = con.con.prepareStatement(updateBooking);
//                con.pst.setInt(1, userId);
//                con.pst.setInt(2, flightId);
//                con.pst.executeUpdate();
//
//                JOptionPane.showMessageDialog(this, "Payment confirmed! Ticket booked.");
//                this.dispose();
//            } else {
//                JOptionPane.showMessageDialog(this, "Booking failed!");
//            }
//
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error booking ticket: " + e.getMessage());
//        }

        String selectedClass = cmbClass.getSelectedItem().toString();
        String amount = getAmountForClass(selectedClass);

        try {
            // 1. Insert ticket (booking)
            String insertTicket = """
                INSERT INTO tickets(FlightID, PassengerID, SeatNumber, Class, BookingDate)
                VALUES(?, ?, ?, ?, NOW())
            """;
            con.pst = con.con.prepareStatement(insertTicket, Statement.RETURN_GENERATED_KEYS);
            con.pst.setInt(1, flightId);
            con.pst.setInt(2, passengerID);
            con.pst.setString(3, "AUTO"); // seat assignment logic can be improved
            con.pst.setString(4, selectedClass);
            con.pst.executeUpdate();

            ResultSet rsKeys = con.pst.getGeneratedKeys();
            int ticketId = 0;
            if (rsKeys.next()) ticketId = rsKeys.getInt(1);

            // 2. Insert payment for the ticket
            String insertPayment = """
                INSERT INTO payments(TicketID, Amount, Method, PaymentDate)
                VALUES(?, ?, ?, NOW())
            """;
            con.pst = con.con.prepareStatement(insertPayment);
            con.pst.setInt(1, ticketId);
            con.pst.setInt(2, Integer.parseInt(amount));
            con.pst.setString(3, "Cash"); // you can extend this to allow user to pick
            con.pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Booking successful!\nClass: " 
                + selectedClass + "\nAmount: " + amount);
            
            this.dispose();
            new TicketsFrame(passengerID).setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error completing booking: " + e.getMessage());
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblAirline = new javax.swing.JLabel();
        lblAircraft = new javax.swing.JLabel();
        lblTimes = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        lblRoute = new javax.swing.JLabel();
        cmbClass = new javax.swing.JComboBox<>();
        btnBook = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblAirline.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblAirline.setText("Airline: ");

        lblAircraft.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblAircraft.setText("Aircraft: ");

        lblTimes.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTimes.setText("Times:");

        lblStatus.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblStatus.setText("Class");

        lblRoute.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblRoute.setText("Route");

        cmbClass.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbClass.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnBook.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnBook.setText("Make Payment");
        btnBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBookActionPerformed(evt);
            }
        });

        btnCancel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Required Information");

        jLabel2.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 204));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Complete Ticket Booking");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblAirline, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblAircraft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTimes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblRoute, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(112, 112, 112)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cmbClass, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 192, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnBook)
                        .addGap(228, 228, 228))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnCancel)
                        .addGap(263, 263, 263))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(lblAirline)
                .addGap(24, 24, 24)
                .addComponent(lblAircraft)
                .addGap(24, 24, 24)
                .addComponent(lblTimes)
                .addGap(18, 18, 18)
                .addComponent(lblRoute)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbClass))
                .addGap(18, 18, 18)
                .addComponent(btnBook)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCancel)
                .addGap(26, 26, 26))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBookActionPerformed
        // TODO add your handling code here:
        handleBooking();
    }//GEN-LAST:event_btnBookActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
//        new BrowseFlights().setVisible(true);
    }//GEN-LAST:event_btnCancelActionPerformed

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
//            logger.log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(() -> new PaymentsFrame().setVisible(true));
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBook;
    private javax.swing.JButton btnCancel;
    private javax.swing.JComboBox<String> cmbClass;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblAircraft;
    private javax.swing.JLabel lblAirline;
    private javax.swing.JLabel lblRoute;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTimes;
    // End of variables declaration//GEN-END:variables
}
