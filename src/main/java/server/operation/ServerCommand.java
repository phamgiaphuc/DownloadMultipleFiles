package server.operation;

import lombok.extern.slf4j.Slf4j;
import server.utilities.ServerUtilities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

@Slf4j
public class ServerCommand implements Runnable {
    // Variables
    private final int portFile; // 5001
    private final String server_IPAddress; // 127.0.0.1
    private final String rootFile; // file folder
    private final Socket commandSocket;
    private String clientName;
    String[] listFile;

    // Functions
    DataOutputStream dosServer;
    DataInputStream disServer;

    public ServerCommand(int portFile, String server_IPAddress, String rootFile, Socket commandSocket) {
        this.portFile = portFile;
        this.server_IPAddress = server_IPAddress;
        this.rootFile = rootFile;
        this.commandSocket = commandSocket;
    }

    /**
     * Stage 3: Receiving the client's command to operate the server's services
     */
    @Override
    public void run() {
        String result = "run";
        receiveClientName();
        sendMenuToClient();
        try {
            // disServer is already declared in receiveClientName()
            // dosServer is already declared in sendMenuToClient()
            do {
                String commandFromClient = disServer.readUTF();
                System.out.println("Command from client " + clientName + ": " + commandFromClient);
                switch (commandFromClient) {
                    case "1" -> {
                        dosServer.writeUTF(ServerUtilities.LISTFILE_1);
                        File file = new File(rootFile);
                        listFile = file.list();
                        StringBuilder listFiles = new StringBuilder();
                        assert listFile != null;
                        for (String temp : listFile) {
                            listFiles.append(temp).append("@");
                        }
                        dosServer.writeUTF(listFiles.toString());
                        dosServer.writeUTF(ServerUtilities.LISTFILE_2);
                        // Stage 4 - stage download
                        ServerDownload serverDownload = new ServerDownload(portFile, server_IPAddress, rootFile);
                        serverDownload.run();
                    }
                    case "2" -> {
                        // Stage 5 - stage exit
                        result = "exit";
                        log.info("Disconnect the connection from client " + clientName + ".");
                        dosServer.writeUTF("Bye " + clientName);
                    }
                }
            } while (!result.equals("exit"));
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<ServerCommand - run>: can not receive command from client " + clientName + ".");
        } finally {
            try {
                disServer.close();
                dosServer.close();
                if (result.equals("exit")) {
                    commandSocket.close();
                }
            } catch (IOException e) {
                log.info(e.getMessage());
                log.info("<ServerCommand - run>: Can not close the functions.");
            }
        }
    }

    // Receiving the client's name
    private void receiveClientName() {
        try {
            disServer = new DataInputStream(commandSocket.getInputStream());
            clientName = disServer.readUTF();
            log.info("Start receiving command from client " + clientName + " - " + commandSocket.getPort());
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<ServerCommand - receiveClientName>: can not receive name of the client.");
        }
    }

    // Sending a menu service to the client
    private void sendMenuToClient() {
        try {
            dosServer = new DataOutputStream(commandSocket.getOutputStream());
            StringBuilder stringBuilder = new StringBuilder();
            for (String temp : ServerUtilities.MENU) {
                stringBuilder.append(temp).append("@");
            }
            dosServer.writeUTF(stringBuilder.toString());
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<ServerCommand - sendMenuToClient>: can not send the service menu to client.");
        }
    }
}
