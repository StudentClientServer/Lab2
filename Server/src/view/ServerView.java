package edu.sumdu.group5.server.view;

import java.net.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import edu.sumdu.group5.server.model.ServerException;
import edu.sumdu.group5.server.model.ServerModel;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import edu.sumdu.group5.server.model.Student;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import edu.sumdu.group5.server.model.Group;

import org.apache.log4j.Logger;

import edu.sumdu.group5.server.controller.Controller;

public class ServerView implements View {
    private Socket socket;
    private Thread thread;
    private String xmlMessage;
    private DataOutputStream out;
    private ActionListener controller;
    private ServerModel model;
    private String exceptMessage = null;
    private static final Logger log = Logger.getLogger(ServerView.class);
    private boolean connection;

    /**
     * Reading port from configuration file (servConfig.ini) throws
     * ServerException if some problem with reading
     */
    public ServerView() throws ServerException {
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
    }

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
     * Starting looking for connection read and parse Clients message throw
     * connection exception
     */
    public void starting() throws IOException {
        if (log.isDebugEnabled())
            log.debug("Method call");
        final ServerSocket ss = new ServerSocket(port);
        while (true) {            
            socket = ss.accept();
            thread = new Thread(new Thread() {
                public void run() {
                    try {
                        connection = true;
                        while(connection) {
                            reading();
                            parsing(xmlMessage);
                        }
                    } catch (Exception exc) {
                        try {
                            log.error("Exception", exc);
                            out = new DataOutputStream(socket.getOutputStream());
                            exceptionHandling(exc);
                            out.writeUTF(resultMessage());
                        } catch (IOException e) {
                            log.error("Exception", e);
                        } finally {
                            if (!(out == null)) {
                                try {
                                    out.flush();
                                } catch (IOException e) {
                                    log.error("Exception", e);
                                }
                            }
                        }
                    }
                }
            });
            thread.start();
        }
    }

    /**
     * Creating exception message to answer
     */
    public void exceptionHandling(Exception ex) {
        if (log.isDebugEnabled())
            log.debug("Method call. Arguments: " + ex);
        exceptMessage = ex.toString();
    }

    /**
     * Getting message from client throw InputStream exception
     */
    private void reading() throws IOException {
        if (log.isDebugEnabled())
            log.debug("Method call");
        DataInputStream in = new DataInputStream(socket.getInputStream());
        try {
            xmlMessage = in.readUTF();
        } catch (IOException e) {
            log.error("Exception", e);
            throw new IOException(e);
        }
    }

    /**
     * Parsing client message according to action
     * 
     * @throws ServerException
     */
    private void parsing(String xmlMessage)
            throws ParserConfigurationException, IOException, SAXException,
            ServerException {
        if (log.isDebugEnabled())
            log.debug("Method call. Arguments: " + xmlMessage);
        try {
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlMessage));
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(is);

            XPathFactory factory = XPathFactory.newInstance();
            XPath xPath = factory.newXPath();
            XPathExpression expr2 = xPath.compile("//envelope/*");

            Object result = expr2.evaluate(doc, XPathConstants.NODESET);
            NodeList xDoc = (NodeList) result;
            Element xHeader = (Element) xDoc.item(0);
            Element xBody = (Element) xDoc.item(1);
            String action = xPath.evaluate("//action", xHeader);

            out = new DataOutputStream(socket.getOutputStream());

            if ("UPDATE".equals(action)) {
                out.writeUTF(updateMessage(model.getGroups()));
            } else if ("EXIT".equals(action)) {
                connection = false;
            } else {
                String fakyltet = xPath.evaluate("//fakulty", xHeader);
                String group = xPath.evaluate("//group", xHeader);
                String str = "//envelope/body";
                XPathExpression expr = xPath.compile(str);
                NodeList xStudents = (NodeList) expr.evaluate(doc,
                        XPathConstants.NODESET);
                Node currNode = xStudents.item(0);

                if ("REMOVEGroup".equals(action)) {
                    fireAction(group, "RemoveGroup");
                } else if ("SHOW".equals(action)) {                
                    out.writeUTF(showeMessage(model.getStudents(model
                            .getGroup(group))));                                                
                } else if ("ADDGroup".equals(action)) {
                    fireAction(new Group(fakyltet, group), "AddGroup");
                    out.writeUTF(resultMessage());
                } else if ("REMOVE".equals(action)) {
                    String studentID = xPath.evaluate("studentID", currNode);
                    fireAction(Integer.parseInt(studentID), "RemoveStudent");
                    out.writeUTF(resultMessage());
                } else if ("ADD".equals(action)) {
                    String studentName = xPath
                            .evaluate("studentName", currNode);
                    String studentLastname = xPath.evaluate("studentLastname",
                            currNode);
                    String enrolledDate = xPath.evaluate("enrolledDate",
                            currNode);
                    Integer studentID = Integer.parseInt(xPath.evaluate(
                            "studentID", currNode));
                    fireAction(new Student(studentID, studentName,
                            studentLastname, group, enrolledDate), "AddStudent");
                            out.writeUTF(resultMessage());
                } else if ("CHANGE".equals(action)) {
                    String studentName = xPath
                            .evaluate("studentName", currNode);
                    String studentLastname = xPath.evaluate("studentLastname",
                            currNode);
                    String enrolledDate = xPath.evaluate("enrolledDate",
                            currNode);
                    Integer studentID = Integer.parseInt(xPath.evaluate(
                            "studentID", currNode));
                    fireAction(new Student(studentID, studentName,
                            studentLastname, group, enrolledDate),
                            "UpdateStudent");
                            out.writeUTF(resultMessage());
                }
            }
        } catch (ServerException e) {
            log.error("Exception", e);
            throw new ServerException(e);
        } catch (Exception e) {
            log.error("Exception", e);
            throw new ServerException(e);
        } finally {
            if (!(out == null)) {
                out.flush();
            }
        }
    }

    /**
     * Creating action and send it to controller
     */
    private void fireAction(Object source, String command) {
        if (log.isDebugEnabled())
            log.debug("Method call " + command + " " + source);
        ActionEvent event = new ActionEvent(source, 0, command);
        controller.actionPerformed(event);
    }

    /**
     * Creating request for update command
     */
    private String updateMessage(List<Group> groups) {
        if (log.isDebugEnabled())
            log.debug("Method call");
        StringBuilder builder = new StringBuilder();
        builder.append("<envelope><header><action>UPDATE</action></header><body><groups>");
        for (Group group : groups) {
            builder.append("<group>");
            builder.append("<name>");
            builder.append(group.getNumber());
            builder.append("</name>");
            builder.append("<fakulty>");
            builder.append(group.getFakulty());
            builder.append("</fakulty>");
            builder.append("</group>");
        }
        builder.append("</groups>");
        builder.append("</body></envelope>");
        return builder.toString();
    }

    /**
     * Creating request for show command
     */
    private String showeMessage(List<Student> students) {
        if (log.isDebugEnabled())
            log.debug("Method call");
        StringBuilder builder = new StringBuilder();
        builder.append("<envelope><header><action>SHOW</action></header><body><students>");
        for (Student student : students) {
            builder.append("<student>");
            builder.append("<id>");
            builder.append(student.getId());
            builder.append("</id>");
            builder.append("<firstname>");
            builder.append(student.getFirstName());
            builder.append("</firstname>");
            builder.append("<lastname>");
            builder.append(student.getLastName());
            builder.append("</lastname>");
            builder.append("<enrolled>");
            builder.append(student.getEnrolled());
            builder.append("</enrolled>");
            builder.append("<groupnumber>");
            builder.append(student.getGroupNumber());
            builder.append("</groupnumber>");
            builder.append("</student>");
        }
        builder.append("</students>");
        builder.append("</body></envelope>");
        return builder.toString();
    }

    /**
     * Creating request according to result
     */
    private String resultMessage() {
        if (log.isDebugEnabled())
            log.debug("Method call");
        StringBuilder builder = new StringBuilder();
        String result;
        if (exceptMessage == null) {
            result = "Success";
        } else {
            result = "Exception";
        }
        builder.append("<envelope><header><action>");
        builder.append(result);
        builder.append("</action></header><body>");
        if (exceptMessage != null) {
            builder.append("<stackTrace>");
            builder.append(exceptMessage);
            exceptMessage = null;
            builder.append("</stackTrace>");
        }
        builder.append("</body></envelope>");
        return builder.toString();
    }
}
