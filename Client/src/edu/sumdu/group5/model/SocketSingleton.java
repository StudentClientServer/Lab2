package edu.sumdu.group5.model;

import java.io.*;
import edu.sumdu.group5.exception.*;
import java.net.*;
import java.util.Properties;

public class SocketSingleton {
    private static int serverPort;
    private static String address;
    private static Socket socket;
    private static Properties prop;
    
    private SocketSingleton() {
    }
    
    /**
     * Reading address and port from configuration file (config.ini)
     * throws ClientException if some problem with reading
     */
    
    private static void readConfigFile() throws ClientException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("config.properties"));
            prop = new Properties();
            prop.load(reader);
            address = prop.getProperty("address");
            serverPort = Integer.parseInt(prop.getProperty("port"));
        } catch (IOException e) {
            throw new ClientException("Something wrong with config file!",e);
        } finally {
            try {
                if (reader!=null)
                reader.close();
            } catch (IOException e) {
                throw new ClientException("Something wrong with config file!",e);
            }
        }        
    }

    /**
     * Create socket if it doesnt exist yet and return it
     */
    public static Socket getSocket() throws ClientException {
        if (socket==null) {
            readConfigFile();
            try{
                InetAddress ipAddress = InetAddress.getByName(address);
                socket = new Socket(ipAddress, serverPort);
            } catch (IOException e) {
                throw new ClientException("Somesthing wrong with socket",e);
            }
        }
        return socket;
    }
}