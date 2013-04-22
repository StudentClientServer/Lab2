package view;

import java.net.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import model.ServerException;
import model.ServerModel;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import model.Student;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import model.Group;

import org.apache.log4j.Logger;

import controller.Controller;

public class ServerView implements View {
    private Socket socket;
    private Thread thread;
    private int port = 7070; //default port
    private String xmlMessage;
    private DataOutputStream out;
    private ActionListener controller;
    private ServerModel model;
    private String exceptMessage = null;
    private DataInputStream in;
    private static final Logger log = Logger.getLogger(ServerView.class);

    /**
     * Reading port from configuration file (servConfig.ini)
     * throws ServerException if some problem with reading
     */
    public ServerView() throws ServerException {
        log.info("Method call");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("servConfig.ini"));
            while (reader.readLine() != null) {
                if (reader.readLine().equals("Port")) {
                    port = Integer.parseInt(reader.readLine());
                }
            }
        } catch (IOException e) {
            ServerException ex = new ServerException(e);
            log.error("Exception", ex);
            throw ex;
        } finally {
            try {
                if (reader!=null)
                    reader.close();
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
        log.info("Method call");
        this.model = model;
    }

    /**
     * Set controller
     */
    public void setController(ActionListener controller) {
        log.info("Method call");
        this.controller = controller;
    }

    /**
     * Starting looking for connection
     * read and parse Clients message
     * throw connection exception
     */
    public void starting() throws IOException {
        log.info("Method call");
        ServerSocket ss = new ServerSocket(port);
        while (true) {
            System.out.println("Waiting for client");
            socket = ss.accept();
            System.out.println("Someone connected!");
            thread = new Thread(new Thread() {
                public void run() {
                    try {
                        reading();
                        parsing(xmlMessage);
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
        log.info("Method call. Arguments: " + ex);
        exceptMessage = ex.toString();
    }

    /**
     * Getting message from client
     * throw InputStream exception
     */
    private void reading() throws IOException {
        log.info("Method call");
        in = new DataInputStream(socket.getInputStream());
        try {
            xmlMessage = in.readUTF();
        } catch (IOException e) {
            log.error("Exception", e);
            throw new IOException(e);
        }
    }

    /**
     * Parsing client message according to action
     * @throws ServerException
     */
    private void parsing(String xmlMessage) throws ParserConfigurationException, IOException, SAXException, ServerException {
        log.info("Method call. Arguments: " + xmlMessage);
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlMessage));
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        NodeList items = doc.getDocumentElement().getChildNodes();
        String action = items.item(0).getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
        out = new DataOutputStream(socket.getOutputStream());
        try {
            if ("UPDATE".equals(action)) {
                out.writeUTF(updateMessage(model.getGroups()));
            } else {
                String fakyltet = items.item(0).getChildNodes().item(1).getFirstChild().getNodeValue();
                String group = items.item(0).getChildNodes().item(2).getFirstChild().getNodeValue();
                if ("REMOVEGroup".equals(action)) {
                    fireAction(group, "RemoveGroup");
                } else if ("SHOW".equals(action)) {
                    out.writeUTF(showeMessage(model.getStudents(model.getGroup(group))));
                } else if ("ADDGroup".equals(action)) {
                    fireAction(new Group(fakyltet, group), "AddGroup");
                } else if ("REMOVE".equals(action)) {
                    String studentID = items.item(1).getChildNodes().item(0).getFirstChild().getNodeValue();
                    fireAction(Integer.parseInt(studentID), "RemoveStudent");
                } else if ("ADD".equals(action)) {
                    String studentName = items.item(1).getChildNodes().item(0).getFirstChild().getNodeValue();
                    String studentLastname = items.item(1).getChildNodes().item(1).getFirstChild().getNodeValue();
                    String enrolledDate = items.item(1).getChildNodes().item(2).getFirstChild().getNodeValue();
                    Integer studentID = Integer.parseInt(items.item(1).getChildNodes().item(3).getFirstChild().getNodeValue());
                    fireAction(new Student(studentID, studentName, studentLastname, group, enrolledDate), "AddStudent");
                } else if ("CHANGE".equals(action)) {
                    String studentName = items.item(1).getChildNodes().item(0).getFirstChild().getNodeValue();
                    String studentLastname = items.item(1).getChildNodes().item(1).getFirstChild().getNodeValue();
                    String enrolledDate = items.item(1).getChildNodes().item(2).getFirstChild().getNodeValue();
                    Integer studentID = Integer.parseInt(items.item(1).getChildNodes().item(3).getFirstChild().getNodeValue());
                    fireAction(new Student(studentID, studentName, studentLastname, group, enrolledDate), "UpdateStudent");
                }
                out.writeUTF(resultMessage());
            }
        } catch (ServerException e) {
            log.error("Exception",e);
            throw new ServerException(e);
        } finally {
            if (out!=null) {
                out.close();
            }
            if (in!=null){
                in.close();
            }
        }
    }

    /**
     * Creating action and send it to controller
     */
    private void fireAction(Object source, String command) {
        log.info("Method call " + command + " " + source);
        ActionEvent event = new ActionEvent(source, 0, command);
        controller.actionPerformed(event);
    }

    /**
     * Creating request for update command
     */
    private String updateMessage(List<Group> groups) {
        log.info("Method call");
        StringBuilder builder = new StringBuilder();
        builder.append("<envelope><header><action>UPDATE</action></header><body>");
        for (Group group : groups) {
            builder.append("<fakulty>");
            builder.append(group.getFakulty());
            builder.append("</fakulty>");
            builder.append("<group>");
            builder.append(group.getNumber());
            builder.append("</group>");
        }
        builder.append("</body></envelope>");
        return builder.toString();
    }

    /**
     * Creating request for show command
     */
    private String showeMessage(List<Student> students) {
        log.info("Method call");
        StringBuilder builder = new StringBuilder();
        builder.append("<envelope><header><action>SHOW</action></header><body>");
        for (Student student : students) {
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
        }
        builder.append("</body></envelope>");
        return builder.toString();
    }

    /**
     * Creating request according to result
     */
    private String resultMessage() {
        log.info("Method call");
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
