import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import javax.swing.*;

class Client {
    private static JTextArea responseArea; 
    private static JTextField commandField; 
    private static Socket mySocket; 
    private static PrintWriter outStream; 
    private static BufferedReader inStream; 

    public static void main(String[] args) {
        // Create the GUI frame
        JFrame frame = new JFrame("Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 450);
        frame.setUndecorated(false); // Classic window decorations
        frame.getRootPane().setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, new Color(139, 0, 0))); // Deep red border

        // Create a panel to hold components and set layout
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(15, 15)); 
        panel.setBackground(new Color(255, 240, 245)); 

        
        Font elegantFont = new Font("Serif", Font.PLAIN, 18);

        // Text area to display server responses
        responseArea = new JTextArea();
        responseArea.setEditable(false);
        responseArea.setFont(elegantFont); 
        responseArea.setForeground(new Color(139, 0, 0)); 
        responseArea.setBackground(new Color(255, 228, 225)); 
        responseArea.setBorder(BorderFactory.createLineBorder(new Color(205, 92, 92), 2)); 
        responseArea.setMargin(new Insets(12, 12, 12, 12)); 
        JScrollPane scrollPane = new JScrollPane(responseArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER); // Add scrollable text area

        // Text field to enter commands
        commandField = new JTextField();
        commandField.setFont(elegantFont); 
        commandField.setForeground(Color.WHITE); 
        commandField.setBackground(new Color(139, 0, 0)); 
        commandField.setCaretColor(Color.WHITE); 
        commandField.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.add(commandField, BorderLayout.SOUTH); // Input field at the bottom

        // Button to send the command
        JButton sendButton = new JButton("Send Command");
        sendButton.setFont(new Font("Serif", Font.BOLD, 16)); 
        sendButton.setBackground(new Color(255, 69, 0));
        sendButton.setForeground(Color.black); // White text
        sendButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
        sendButton.setFocusPainted(false); // Remove focus outline for a clean look
        panel.add(sendButton, BorderLayout.EAST); // Add button on the right side

        frame.add(panel);
        frame.setVisible(true);

        // Action listener for the send button
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = commandField.getText().trim(); // Get command from text field
                sendCommand(command); // Send command to the server
                commandField.setText(""); // Clear the text field
            }
        });

        // Connect to the server
        try {
            mySocket = new Socket("127.0.0.1", 9128);
            outStream = new PrintWriter(mySocket.getOutputStream(), true);
            inStream = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));

            // Display supported commands in the response area
            displaySupportedCommands();
        } catch (IOException e) {
            responseArea.append("Error connecting to server: " + e.getMessage() + "\n");
        }
    }

    // Method to display supported commands in the response area
    private static void displaySupportedCommands() {
        responseArea.append("Supported Commands:\n" +
                "1. Add\n" +
                "2. Remove\n" +
                "3. Clear\n" +
                "4. Get_Summation\n" +
                "5. Get_Minimum\n" +
                "6. Get_Maximum\n" +
                "7. Display_Content\n" +
                "8. Exit\n\n");
    }

    // Method to send command and receive response from the server
    private static void sendCommand(String command) {
        if (command.isEmpty()) {
            return; // Ignore empty commands
        }

        // Send command to server
        outStream.println("Sender: Client_A; Receiver: Server_A; Payload: " + command);
        responseArea.append("Sent to server: " + command + "\n"); // Display sent command

        try {
            // Receive response from server
            String response = inStream.readLine();
            responseArea.append("Server: " + response + "\n"); // Display server response
        } catch (IOException e) {
            responseArea.append("Error receiving response: " + e.getMessage() + "\n");
        }
    }
}
