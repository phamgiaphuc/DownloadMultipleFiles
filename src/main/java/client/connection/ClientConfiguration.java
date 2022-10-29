package client.connection;

import client.utilities.ClientUtilities;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

@Slf4j
public class ClientConfiguration {
    // Functions
    static Scanner sc = new Scanner(System.in);
    // Variables
    private static String[] client_configValues;

    /**
     * Stage 1 : Get and check client values.
     */
    public static void main(String[] args) {
        getValuesFromConfiguration();
        checkBeforeClientConnection();
        String inputCommand = sc.nextLine().trim();
        if (inputCommand.equals("")) {
            //Checking stages
            if (!checkConfigurationValues() || !checkPort()) {
                log.info("<ClientConfiguration - main>: Please check the client's configuration in the config.properties file again.");
                System.exit(0);
            }
            checkFolderExist();
            String client_name = enterClientName();
            ClientConnection clientConnection = new ClientConnection(client_configValues, client_name);
            clientConnection.connectToServerCommand();
        } else {
            main(args);
        }
    }

    /**
     * Getting values from the config.properties file for checking stages.
     */
    private static void getValuesFromConfiguration() {
        Properties properties = new Properties();
        try {
            properties.load(ClientConfiguration.class.getClassLoader().getResourceAsStream("configuration/config.properties"));
            String portCommand = properties.getProperty("portCommand").trim(); // 5000
            String portFile = properties.getProperty("portFile").trim(); // 5001
            String clientIPAddress = properties.getProperty("clientIPAddress").trim(); // 127.0.0.1
            File fileDownload = new File(properties.getProperty("rootDownload").trim());
            String rootDownload = fileDownload.getAbsolutePath(); // download folder
            client_configValues = new String[]{portCommand, portFile, clientIPAddress, rootDownload};
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<ClientConfiguration - getValuesFromConfiguration>: Can not read the config.properties file.");
        }
    }

    /**
     * Checking stages.
     */
    // Checking the client configuration before connecting to the server
    private static void checkBeforeClientConnection() {
        String[] client_announcement = ClientUtilities.cBCC_announcement;
        System.out.println(client_announcement[0]);
        System.out.println(client_announcement[1]);
        System.out.println(client_announcement[2] + client_configValues[0]); // 5000
        System.out.println(client_announcement[3] + client_configValues[1]); // 5001
        System.out.println(client_announcement[4] + client_configValues[2]); // 127.0.0.1
        System.out.println(client_announcement[5] + client_configValues[3]); // download folder
        System.out.println(client_announcement[6]);
        System.out.print(client_announcement[7]);
    }

    // Entering the user name
    public static String enterClientName() {
        System.out.print("Enter your name here to connect to the server: ");
        String name = sc.nextLine().trim();
        if (name.equals("")) {
            System.out.print("Please try again!.");
            enterClientName();
        }
        return name;
    }

    // Checking if the "download" folder exists.
    private static void checkFolderExist() {
        File file = new File(client_configValues[3]);
        if (file.mkdir()) {
            System.out.println("Folder created: " + file.getName());
        }
    }

    // Checking the number of values in the configuration.
    private static boolean checkConfigurationValues() {
        return client_configValues.length == 4;
    }

    // Checking the ports if they are numbers.
    private static boolean checkPort() {
        try {
            Integer.parseInt(client_configValues[0]);
            Integer.parseInt(client_configValues[1]);
            return true;
        } catch (NumberFormatException e) {
            log.info(e.getMessage());
            log.info("<ClientConfiguration - checkPort>: Incorrect data type in portCommand and portFile");
        }
        return false;
    }
}
