package client.operation;

import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

@Slf4j
public class ClientCommand {
    // Variables
    private final int portFile; // 5001
    private final String clientIPAddress; // 127.0.0.1
    private final String rootDownload; // download folder
    private final Socket clientSocketCommand;
    private final String clientName;
    String receiveFromServer;
    String[] listFile;
    Set<String> listFileToDownload;
    String file;

    // Functions
    Scanner sc = new Scanner(System.in);
    DataOutputStream dosClient;
    DataInputStream disClient;

    public ClientCommand(int portFile, String clientIPAddress, String rootDownload, Socket clientSocketCommand, String clientName) {
        this.portFile = portFile;
        this.clientIPAddress = clientIPAddress;
        this.rootDownload = rootDownload;
        this.clientSocketCommand = clientSocketCommand;
        this.clientName = clientName;
    }

    /**
     * Stage 3: Sending command to the server
     */
    public String proceed() {
        String result = "proceed";
        String inputCommand = sc.nextLine().trim();
        inputCommand = inputCommand.toLowerCase().trim();
        try {
            disClient = new DataInputStream(clientSocketCommand.getInputStream());
            dosClient = new DataOutputStream(clientSocketCommand.getOutputStream());
            switch (inputCommand) {
                // case for function "Download files from the server"
                case "1", "download", "d" -> {
                    dosClient.writeUTF("1");
                    receiveFromServer = disClient.readUTF();
                    System.out.println(receiveFromServer);
                    receiveFromServer = disClient.readUTF();
                    listFile = receiveFromServer.split("@");
                    int i = 1;
                    for (String file : listFile) {
                        System.out.println(i + ". " + file);
                        i++;
                    }
                    receiveFromServer = disClient.readUTF();
                    System.out.println(receiveFromServer);
                    enterFileName();
                    showDownloadFile();
                    // Stage 4 - stage download
                    ClientDownload clientDownload = new ClientDownload(portFile, clientIPAddress, rootDownload, clientName, file);
                    clientDownload.proceed();
                }
                // case for function "Quit the program"
                case "2" -> {
                    // Stage 5 - stage exit
                    dosClient.writeUTF("2");
                    receiveFromServer = disClient.readUTF();
                    System.out.println(receiveFromServer);
                    result = "exit";
                }
                default -> System.out.println("Please choose an option to send to the server.");
            }
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<ClientCommand - proceed>: can not send the command to the server.");
        }
        return result;
    }

    // Clients enter the files that they want to download from the server
    private void enterFileName() {
        if (listFileToDownload == null) {
            listFileToDownload = new HashSet<>();
        }
        System.out.println("Please enter file names to download or type '@file' to show your download files.");
        System.out.print("Input download files: ");
        String input = sc.nextLine().trim();
        if (input.equals("")) {
            System.out.println("Please enter again!");
            enterFileName();
        } else if (input.equals("@file")) {
            showDownloadFile();
            enterFileName();
        } else {
            String[] temp_1 = input.split(",");
            for (String temp_2 : temp_1) {
                int check = checkFileExist(temp_2.trim());
                if (check == 1) {
                    listFileToDownload.add(temp_2.trim());
                } else {
                    System.out.println("The file name '" + temp_2.trim() + "' does not exist!");
                }
            }
            moreFilesToDownload();
        }
    }

    // Showing the existing list of downloading files
    private void showDownloadFile() {
        file = listFileToDownload.toString();
        file = file.substring(1, file.length() - 1);
        if (listFileToDownload.size() == 0) {
            System.out.println("Nothing to download!");
        } else if (listFileToDownload.size() == 1) {
            System.out.println("The file you want to download is " + file + ".");
        } else {
            System.out.println("The files you want to download are " + file + ".");
        }
    }

    // Checking if the downloading file is existed in the "file" server
    private int checkFileExist(String temp) {
        int check = 0;
        for (String file : listFile) {
            if (temp.equals(file)) {
                check = 1;
                return check;
            }
        }
        return check;
    }

    // Asking clients if they want to download more files
    private void moreFilesToDownload() {
        System.out.print("Do you want to download more? (Yes/No) ");
        String answer = sc.nextLine().trim();
        switch (answer) {
            case "Yes", "yes", "y" -> enterFileName();
            case "No", "no", "n" -> System.out.print("Please wait to process the download stage. ");
            default -> {
                System.out.println("It is not a right answer. Please try again!");
                moreFilesToDownload();
            }
        }
    }
}
