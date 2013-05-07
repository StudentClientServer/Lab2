package edu.sumdu.group5.server.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.apache.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.*;

import edu.sumdu.group5.server.model.*;

/**
 * Thread that accepts connections and communicates with the client 
 */
public class ServerViewThread extends Thread
{
  private boolean connection;
    private ActionListener controller;
    private ServerModel model;
    private Socket socket;
    private static final Logger log = Logger.getLogger(ServerView.class);
    private String exceptMessage = null;
    private String xmlMessage;
        
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
     * Starting new thread for every client
     */
    public ServerViewThread(Socket s, ActionListener controller, ServerModel model) throws IOException, ServerException {
        if (log.isDebugEnabled())
                log.debug("Method call");
        setController(controller);
        setModel(model);
        socket = s;
        start();
    }
    
    @Override
    public void run() {
        try {
            connection = true;
            while(connection) {
                reading();
                parsing(xmlMessage);
            }
        } catch (Exception exc) {
            DataOutputStream out = null;
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
        DataOutputStream out = null;
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
                    out.writeUTF(resultMessage());
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
