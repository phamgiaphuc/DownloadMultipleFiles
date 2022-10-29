package server.connection;

import lombok.extern.slf4j.Slf4j;
import server.operation.ServerCommand;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ServerConnection {
    // Variables
    private final int portCommand; // 6000
    private final int portFile; // 6001
    private final String serverIPAddress; // 127.0.0.1
    private final String rootFile; // file folder
    private static final int NUM_OF_THREAD = 4;
    private ServerSocket commandServerSocket;

    public ServerConnection(String[] server_configValues) {
        this.portCommand = Integer.parseInt(server_configValues[0]);
        this.portFile = Integer.parseInt(server_configValues[1]);
        this.serverIPAddress = server_configValues[2];
        this.rootFile = server_configValues[3];
    }

    /**
     * Stage 2: Starting a command stage to receive client's message
     */
    public synchronized void startServerCommand() {
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_OF_THREAD);
        try {
            // Waiting the client's connection
            log.info("Starting Acus server!");
            log.info("Binding to port command " + portCommand + ", please wait......");
            // Creating a new command server socket
            commandServerSocket = new ServerSocket(portCommand);
            log.info("Command server start: " + commandServerSocket);
            log.info("Waiting for client's connection......");
            while(true) {
                try {
                    // Accepting the client's connection
                    Socket commandSocket = commandServerSocket.accept();
                    log.info("Client accepted: " + commandSocket);
                    // Creating a thread for each client's connection, maximum is serving 4 clients at a time
                    ServerCommand serverCommand = new ServerCommand(portFile, serverIPAddress, rootFile, commandSocket);
                    executorService.execute(serverCommand);
                } catch (IOException e) {
                    log.info(e.getMessage());
                    log.info("<ServerConnection - startServerCommand>: Can not connect to the client.");
                }
            }
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<ServerConnection - startServerCommand>: Can not create the commandServerSocket.");
        } finally {
            executorService.shutdown();
            try {
                if (commandServerSocket != null) {
                    commandServerSocket.close();
                }
            } catch (IOException e) {
                log.info(e.getMessage());
                log.info("<ServerConnection - startServerCommand>: Can not close the commandServerSocket.");
            }
        }
    }
}
