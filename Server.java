import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

class Server {
    private static List<Integer> inputValues = new ArrayList<>(); // List to store integers
    private static JTextArea serverTextArea; 

    public static void main(String args[]) {
        // Setup the server GUI 
        JFrame frame = new JFrame("Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        // Create the panel and text area for server communications
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.BLACK); 

        // Text area for server communications, styled like a hacker terminal
        serverTextArea = new JTextArea();
        serverTextArea.setEditable(false);
        serverTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); 
        serverTextArea.setForeground(new Color(0, 255, 0)); 
        serverTextArea.setBackground(Color.BLACK); 
        serverTextArea.setBorder(BorderFactory.createLineBorder(new Color(0, 128, 0), 2)); 

        // Scroll pane for the text area to handle multiple lines of server logs
        JScrollPane scrollPane = new JScrollPane(serverTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // No visible border for the scroll pane
        panel.add(scrollPane, BorderLayout.CENTER);

        frame.add(panel);
        frame.setVisible(true);

        try {
            //Create server Socket that listens/bonds to port/endpoint address 6666 (any port id of your choice, should be >=1024, as other port addresses are reserved for system use)
			// The default maximum number of queued incoming connections is 50 (the maximum number of clients to connect to this server)
			// There is another constructor that can be used to specify the maximum number of connections
            ServerSocket mySocket = new ServerSocket(9128);
            serverTextArea.append("Server started on port 9128...\n");

            Socket connectedClient = mySocket.accept();
            serverTextArea.append("Connection established with client.\n");

            // BufferReader object to read data coming from the client
            BufferedReader br = new BufferedReader(new InputStreamReader(connectedClient.getInputStream()));
            PrintStream ps = new PrintStream(connectedClient.getOutputStream());

            String inputData;
            while (true) {
                inputData = br.readLine();

                // Exit condition: check if input is 'Exit' and break if so
                if (inputData == null || inputData.contains("Exit")) {
                    serverTextArea.append("Received 'Exit' command. Closing connection.\n");
                    ps.println("Sender: Server_A; Receiver: User_A; Payload: Connection closed by server.");
                    break; // Exit the loop when "Exit" command is received
                }

                serverTextArea.append("Received from client: " + inputData + "\n"); // Display the received message

                // Process the command and get the response
                String response = processCommand(inputData);
                ps.println("Sender: Server_A; Receiver: User_A; Payload: " + response);
                serverTextArea.append("Sent to client: " + response + "\n"); // Display the sent response
            }

            serverTextArea.append("Closing connection and sockets.\n");
            ps.close();
            br.close();
            mySocket.close();
            connectedClient.close();

        } catch (Exception exc) {
            serverTextArea.append("Error: " + exc.toString() + "\n");
        }
    }

    // Method to process commands
    private static String processCommand(String inputData) {
        String[] parts = inputData.split(";");
        String payload = parts[2].split(":")[1].trim();

        String response;
        try {
            if (payload.startsWith("Add ")) {
                int valueToAdd = Integer.parseInt(payload.split(" ")[1]);
                inputValues.add(valueToAdd);
                response = "added " + valueToAdd + " successfully";
            } else if (payload.startsWith("Remove ")) {
                int valueToRemove = Integer.parseInt(payload.split(" ")[1]);
                // Check if the value exists in the list before attempting to remove it
                if (inputValues.contains(valueToRemove)) {
                    inputValues.remove(Integer.valueOf(valueToRemove));
                    response = "removed " + valueToRemove + " successfully";
                } else {
                    response = "value " + valueToRemove + " not found in the list";
                }
            } else if (payload.equals("Clear")) {
                inputValues.clear();
                response = "cleared successfully";
            } else if (payload.equals("Get_Summation")) {
                int summation = inputValues.stream().mapToInt(Integer::intValue).sum();
                response = "The summation is " + (summation == 0 ? "null" : summation);
            } else if (payload.equals("Get_Minimum")) {
                Integer min = inputValues.stream().filter(value -> value != 0).min(Integer::compare).orElse(null);
                response = "The minimum is " + (min == null ? "null" : min);
            } else if (payload.equals("Get_Maximum")) {
                Integer max = inputValues.stream().filter(value -> value != 0).max(Integer::compare).orElse(null);
                response = "The maximum is " + (max == null ? "null" : max);
            } else if (payload.equals("Display_Content")) {
                response = "Current values: " + inputValues.toString();
            } else {
                response = "received an unsupported command";
            }
        } catch (NumberFormatException e) {
            response = "received an unsupported format: please enter a valid integer";
        }

        return response;
    }
}
