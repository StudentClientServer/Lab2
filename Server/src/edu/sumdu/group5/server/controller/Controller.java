package edu.sumdu.group5.server.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import edu.sumdu.group5.server.view.*;

import edu.sumdu.group5.server.model.*;

import edu.sumdu.group5.server.controller.ControllerException;

import org.apache.log4j.Logger;

/**
 * The Class Controller.
 */
public class Controller implements ActionListener {

    /** The logger */
    private static final Logger log = Logger.getLogger(Controller.class);

    /** The server. */
    private ServerModel server;

    /** The view. */
    private View view;

    /** The xmlpath. */
    private String xmlpath;

    /** The dtdpath. */
    private String dtdpath;

    /**
     * Instantiates a new controller.
     * 
     * @param server
     *            the server
     * @param view
     *            the view
     */
    public Controller(ServerModel server, View view) {
        if (log.isDebugEnabled())
            log.debug("Constructor call");
        this.server = server;
        this.view = view;
    }

    /**
     * Read pathes to xml and dtd files.
     * 
     * @throws ControllerException
     *             if can't read data from file
     */
    private void readServerSources() throws ControllerException {
        if (log.isDebugEnabled())
            log.debug("Method call");
        InputStream input = null;
        Properties prop = new Properties();
        try {
            input = new FileInputStream("params.properties");
            prop.load(input);
            dtdpath = prop.getProperty("dtdFile");
            xmlpath = prop.getProperty("xmlFile");

        } catch (IOException e) {
            ControllerException ex = new ControllerException(
                    "Can't read the file with connection parameters.", e);
            log.error("Exception", ex);
            throw ex;
        } finally {
            try {
                if (input != null)
                    input.close();
            } catch (IOException e) {
                ControllerException ex = new ControllerException(
                        "Problem with reading property file.", e);
                log.error("Exception", ex);
                throw ex;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent e) {
        if (log.isDebugEnabled())
            log.debug("Method call. Arguments: " + e.getActionCommand() + " "
                    + e.getSource());
        try {
            if ("AddGroup".equals(e.getActionCommand()))
                server.addGroup((Group) e.getSource());
            if ("AddStudent".equals(e.getActionCommand()))
                server.addStudent((Student) e.getSource());
            if ("RemoveGroup".equals(e.getActionCommand()))
                server.removeGroup((String) e.getSource());
            if ("RemoveStudent".equals(e.getActionCommand()))
                server.removeStudent((Integer) e.getSource());
            if ("UptadeGroup".equals(e.getActionCommand()))
                server.updateGroup((Group) e.getSource());
            if ("UpdateStudent".equals(e.getActionCommand()))
                server.updateStudent((Student) e.getSource());
            if ("UpdateGroupNumber".equals(e.getActionCommand())) {
                List<Object> params = (List<Object>) e.getSource();
                server.updateGroup((String) params.get(0),
                        (String) params.get(1));
            }
        } catch (ServerException ex) {
            ControllerException e1 = new ControllerException(ex);
            log.error("Exception", e1);
            view.exceptionHandling(e1);
        }

    }

    /**
     * Start thread, which will save data to xml-file every 1 minute
     */
    public void startSavingThread() {
        if (log.isDebugEnabled())
            log.debug("Method call");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    server.upLoadData();
                } catch (ServerException e) {
                    view.exceptionHandling(new ControllerException(e));
                }
            }

        }, 60000, 60000);
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        if (log.isDebugEnabled())
            log.debug("Method call");
        View view = null;
        try {
            view = (View) new ViewFactory().newInstance("console");
            ServerModel server = new Server();
            Controller controller = new Controller(server, view);
            controller.readServerSources();
            server.setXmlPath(controller.getXmlpath());
            server.setDtdPath(controller.getDtdpath());
            server.readDocument();
            view.setController(controller);
            view.setModel(server);
            controller.startSavingThread();
            controller.exitHandler();
            view.starting();
        } catch (ServerException ex) {
            ControllerException e = new ControllerException(ex);
            log.error("Exception", e);
            if (view != null)
                view.exceptionHandling(e);
        } catch (ControllerException e) {
            ControllerException ex = new ControllerException(e);
            log.error("Exception", ex);
            if (view != null)
                view.exceptionHandling(ex);
        } catch (IOException e) {
            ControllerException ex = new ControllerException(e);
            log.error("Exception", ex);
            if (view != null)
                view.exceptionHandling(ex);
        }
    }

    /**
     * Saves data in xml file on exit
     */
    private void exitHandler() {
        if (log.isDebugEnabled())
            log.debug("Method call");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    if (log.isDebugEnabled())
                        log.debug("Upload on exit");
                    server.upLoadData();
                } catch (ServerException e) {
                    log.error("Exception on exit", e);
                }
            }
        });
    }

    /**
     * Gets the xmlpath.
     * 
     * @return the xmlpath
     */
    public String getXmlpath() {
        return xmlpath;
    }

    /**
     * Sets the xmlpath.
     * 
     * @param xmlpath
     *            the new xmlpath
     */
    public void setXmlpath(String xmlpath) {
        this.xmlpath = xmlpath;
    }

    /**
     * Gets the dtdpath.
     * 
     * @return the dtdpath
     */
    public String getDtdpath() {
        return dtdpath;
    }

    /**
     * Sets the dtdpath.
     * 
     * @param dtdpath
     *            the new dtdpath
     */
    public void setDtdpath(String dtdpath) {
        this.dtdpath = dtdpath;
    }

}
