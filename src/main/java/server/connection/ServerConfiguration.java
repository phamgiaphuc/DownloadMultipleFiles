package server.connection;

import lombok.extern.slf4j.Slf4j;
import server.utilities.ServerUtilities;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public class ServerConfiguration {
    // Variables
    private static String[] serverConfigValues;

    /**
     * Stage 1 : Get and check server values.
     */
    public static void main(String[] args) {
        getValuesFromConfiguration();
        checkBeforeServerConnection();
        if (!checkConfigurationValues() || !checkPort()) {
            log.info("<ServerConfiguration - main>: Please check the server's configuration in the config.properties file again.");
            System.exit(0);
        }
        ServerConnection serverConnection = new ServerConnection(serverConfigValues);
        serverConnection.startServerCommand();
    }

    /**
     * Getting values from the config.properties file for checking stages
     */
    private static void getValuesFromConfiguration() {
        Properties properties = new Properties();
        try {
            properties.load(ServerConfiguration.class.getClassLoader().getResourceAsStream("configuration/config.properties"));
            String portCommand = properties.getProperty("portCommand").trim(); // 6000
            String portFile = properties.getProperty("portFile").trim(); // 6001
            String serverIPAddress = properties.getProperty("serverIPAddress").trim(); // 127.0.0.1
            File downloadFile = new File(properties.getProperty("rootFile").trim());
            String rootFile = downloadFile.getAbsolutePath(); // file folder
            serverConfigValues = new String[]{portCommand, portFile, serverIPAddress, rootFile};
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<ServerConfiguration - getValuesFromConfiguration>: Can not read the config.properties file");
        }
    }

    /**
     * Checking stages.
     */
    // Checking the server configuration before accepting connection from the client
    private static void checkBeforeServerConnection() {
        String[] server_announcement = ServerUtilities.cBSC_announcement;
        System.out.println(server_announcement[0]);
        System.out.println(server_announcement[1]);
        System.out.println(server_announcement[2] + serverConfigValues[0]); // 6000
        System.out.println(server_announcement[3] + serverConfigValues[1]); // 6001
        System.out.println(server_announcement[4] + serverConfigValues[2]); // 127.0.0.1
        System.out.println(server_announcement[5] + serverConfigValues[3]); // file folder
        System.out.println(server_announcement[6]);
    }

    // Checking the number of values in the configuration.
    private static boolean checkConfigurationValues() {
        return serverConfigValues.length == 4;
    }

    // Checking the ports if they are numbers.
    private static boolean checkPort() {
        try {
            Integer.parseInt(serverConfigValues[0]);
            Integer.parseInt(serverConfigValues[1]);
            return true;
        } catch (NumberFormatException e) {
            log.info(e.getMessage());
            log.info("<ServerConfiguration - checkPort>: Incorrect data type in portCommand and portFile");
        }
        return false;
    }
}
