package edu.sumdu.group5.server.view;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.*;

import org.apache.log4j.Logger;

import edu.sumdu.group5.server.model.ServerException;
import edu.sumdu.group5.server.model.ServerModel;

public class ServerView implements View {
    private static final Logger log = Logger.getLogger(ServerView.class);
    private ActionListener controller;
    private ServerModel model;
    private ServerViewSocket serverViewSocket;

    /**
     * Set model
     */
    public void setModel(ServerModel model) {
        if (log.isDebugEnabled())
            log.debug("Method call");
        this.model = model;
    }

    /**
     * Set controller
     */
    public void setController(ActionListener controller) {
        if (log.isDebugEnabled())
            log.debug("Method call");
        this.controller = controller;
    } 
    
    /**
     * Reading port from configuration file (servConfig.ini) throws
     * ServerException if some problem with reading
     */
    public int readProp() throws ServerException {
        if (log.isDebugEnabled())
            log.debug("Method call");
        Properties prop = new Properties();
        InputStream input = null;
        try {         
            input = new FileInputStream("servConfig.properties");
            prop.load(input);
            port = Integer.parseInt(prop.getProperty("port"));
        } catch (IOException e) {
            ServerException ex = new ServerException(e);
            log.error("Exception", ex);
            throw ex;
        } catch(NumberFormatException e) {
            ServerException ex = new ServerException("Specified port is not correct, using port 7070",e);
            log.error("Exception", ex);            
        } finally {
            try { 
                if (input != null)
                    input.close();               
                
            } catch (IOException e) {
                ServerException ex = new ServerException(e);
                log.error("Exception", ex);
                throw ex;
            }
        }
        return port;
    }
    
    /**
     * Starting looking for connection read and parse Clients message throw
     * connection exception
     */
    public void starting() throws ServerException {    
        if (log.isDebugEnabled())
            log.debug("Method call");    
        ServerSocket s = null;
        try {
            s = new ServerSocket(readProp());
            while (true) {
                Socket socket = s.accept();
                try {
                    serverViewSocket = new ServerViewSocket(socket, controller, model);
                }
                catch (IOException e) {
                    socket.close();
                }
            }
        } catch (IOException e) {
            log.error("Error", e);
            ServerException ex = new ServerException (e);
            throw ex;
        } finally {
            try {
                if (s != null)
                s.close();
            } catch (IOException e) {
                log.error("Error", e);
                ServerException ex = new ServerException (e);
                throw ex;
            }
        }
    }

    /**
     * Creating exception message to answer
     */
    public void exceptionHandling(Exception ex) {
        if (log.isDebugEnabled())
            log.debug("Method call. Arguments: " + ex);
        serverViewSocket.exceptionHandling(ex);
    }

}
