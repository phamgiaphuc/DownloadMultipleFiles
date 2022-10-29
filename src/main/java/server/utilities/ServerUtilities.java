package server.utilities;

public class ServerUtilities {
    /**
     * ServerConfiguration
     */
    // cBSC = checkBeforeServerConnection
    public static final String[] cBSC_announcement = {
            "--------------------------Check the server's configuration-------------------------",
            "Make sure the argument values in the configuration is correct.",
            "First value - command port (port for receiving command from the client): ",
            "Second value - file ports (port for sending file to the client): ",
            "Third value - server's IP address: 'localhost' or your IP address: ",
            "Fourth value - direction for 'file' folder: ",
            "-----------------------------------------------------------------------------------",
    };

    /**
     * ServerCommand
     */
    // Service menu
    public static final String[] MENU = {
            "----------------------------------Menu for command---------------------------------",
            "1. Download files from the server.",
            "2. Quit the program.",
            "-----------------------------------------------------------------------------------",
            "Input: "
    };

    // List of files on the server
    public static final String LISTFILE_1 = "-------------------------------List files on the server----------------------------";
    public static final String LISTFILE_2 = "-----------------------------------------------------------------------------------";
}
