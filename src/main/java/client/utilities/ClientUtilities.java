package client.utilities;

public class ClientUtilities {
    // cBBC = checkBeforeClientConnection
    public final static String[] cBCC_announcement = {
            "--------------------------Check the client's configuration-------------------------",
            "Make sure the argument values in the configuration is correct.",
            "First value - command port (port for sending command to the server): ",
            "Second value - file ports (port for receiving file from the server): ",
            "Third value - client's IP address: 'localhost' or your IP address: ",
            "Fourth value - direction for 'download' folder: ",
            "-----------------------------------------------------------------------------------",
            "If the config.properties is correct, please press 'enter' to continue."
    };
}
