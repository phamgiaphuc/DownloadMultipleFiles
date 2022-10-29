package client.connection;

import client.operation.ClientCommand;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@Slf4j
public class ClientConnection {
    // Variables
    private final int portCommand; // 5000
    private final int portFile; // 5001
    private final String clientIPAddress; // 127.0.0.1
    private final String rootDownload; // download folder
    private final String clientName; // client_name
    private Socket clientSocketCommand;
    private String receiveFromServer;

    // Functions
    DataInputStream disClient;
    DataOutputStream dosClient;

    public ClientConnection(String[] client_configValues, String clientName) {
        this.portCommand = Integer.parseInt(client_configValues[0]);
        this.portFile = Integer.parseInt(client_configValues[1]);
        this.clientIPAddress = client_configValues[2];
        this.rootDownload = client_configValues[3];
        this.clientName = clientName;
    }

    /**
     * Stage 2: Starting a command stage for sending client's command to the server
     */
    public void connectToServerCommand() {
        try {
            if (clientSocketCommand == null) {
                clientSocketCommand = new Socket(clientIPAddress, portCommand);
            }
            System.out.println("Connected to Acus server: " + clientSocketCommand + " - Client name: " + clientName);
            // Sending the user name to the server
            sendClientNameToServer();
            // Displaying the server's service menu
            receiveMenuFromServer();
            while (true) {
                clientMenu();
                ClientCommand clientCommand = new ClientCommand(portFile, clientIPAddress, rootDownload, clientSocketCommand, clientName);
                String result = clientCommand.proceed();
                if (result.equals("exit")) {
                    break;
                }
            }
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<ClientCommand - connectToServerCommand>: Can not connect to the sever.");
        } finally {
            try {
                dosClient.close();
                disClient.close();
                if (clientSocketCommand != null) {
                    try {
                        clientSocketCommand.close();
                    } catch (IOException e) {
                        log.info(e.getMessage());
                        log.info("<ClientCommand - connectToServerCommand>: Can not close to the clientSocketCommand.");
                    }
                }
            } catch (IOException e) {
                log.info(e.getMessage());
                log.info("<ClientCommand - connectToServerCommand>: Can not close to the disClient and dosClient.");
            }
        }
    }

    // Sending the client name to the server
    private void sendClientNameToServer() {
        try {
            dosClient = new DataOutputStream(clientSocketCommand.getOutputStream());
            dosClient.writeUTF(clientName);
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<ClientConnection - sendClientNameToServer>: can not send the client name to the server.");
        }
    }

    // Receiving menu from the server
    private void receiveMenuFromServer() {
        try {
            disClient = new DataInputStream(clientSocketCommand.getInputStream());
            receiveFromServer = disClient.readUTF();
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<ClientConnection - receiveMenuFromServer>: can not receive the service menu.");
        }
    }

    private void clientMenu() {
        String[] menu = receiveFromServer.split("@");
        for (int i = 0; i < menu.length - 1; i++) {
            System.out.println(menu[i]);
        }
        System.out.print(menu[menu.length - 1]);
    }
}

