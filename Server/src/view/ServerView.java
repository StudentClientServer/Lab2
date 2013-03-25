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

public class ServerView implements View {
    private Socket socket;
    private Thread thread;
    private int port = 7070;
    private String xmlMessage;
    private DataOutputStream out;
    private ActionListener controller;
    private ServerModel model;
    private String ExceptMessage = null;
    
    /**
    * Set model
    */
    public void setModel(ServerModel model) {
        this.model = model;
    }
    
    /**
    * Set controller
    */
    public void setController(ActionListener controller) {
        this.controller = controller;
    }
    
    /**
     * Starting looking for connection throw connection exception
     */
    public void starting() throws IOException {
        ServerSocket ss = new ServerSocket(port);
        while (true) {
            System.out.println("Waiting for a client...");
            socket = ss.accept();
            System.out.println("Client connected");
            thread = new Thread(new Thread() {
                public void run() {
                    try {
                        reading();
                        parsing(xmlMessage);
                    } catch (Exception exc) {
                        try {
                            out = new DataOutputStream(socket.getOutputStream());
                            exceptionHandling(exc);
                            out.writeUTF(resultMessage());
                            if (!(out == null)) {
                                out.flush();
                            }
                        } catch (IOException x) {
                            x.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        }
    }

    public void exceptionHandling(Exception ex) {
        ExceptMessage = ex.toString();
    }
    
    /**
    * Getting message from client
    * throw InputStream exception
    */
    private void reading() throws IOException {        
        DataInputStream in = new DataInputStream(socket.getInputStream());            
        xmlMessage = in.readUTF();
        System.out.println("Have a line "+xmlMessage);
    }
    
    /**
    * Parsing client message according to action
     * @throws ServerException 
    */
    private void parsing(String xmlMessage) throws SAXException, ParserConfigurationException, IOException, ServerException {        
        InputSource is = new InputSource();        
        is.setCharacterStream(new StringReader(xmlMessage));
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        NodeList items = doc.getDocumentElement().getChildNodes();
        String action = items.item(0).getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
        out = new DataOutputStream(socket.getOutputStream());        
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
            } else {                    
                String studentName = items.item(1).getChildNodes().item(0).getFirstChild().getNodeValue();
                String studentLastname = items.item(1).getChildNodes().item(1).getFirstChild().getNodeValue();
                String enrolledDate = items.item(1).getChildNodes().item(2).getFirstChild().getNodeValue(); 
                Integer studentID = Integer.parseInt(items.item(1).getChildNodes().item(3).getFirstChild().getNodeValue());
                fireAction(new Student(studentID, studentName, studentLastname, group, enrolledDate), "AddStudent");
                if ("CHANGE".equals(action)) {                    
                    fireAction(new Student(studentID, studentName, studentLastname, group, enrolledDate), "UpdateStudent");
                }                    
            } 
            out.writeUTF(resultMessage()); 
        }    
        if (!(out==null)) {
            out.flush();
        }
    }
    
    /**
    * Creating action and send it to controller
    */
    private void fireAction(Object source, String command) {
        ActionEvent event = new ActionEvent(source, 0, command);
        controller.actionPerformed(event);
    }
    
    /**
    * Creating request for update command
    */
    private String updateMessage(List<Group> groups) {
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
        StringBuilder builder = new StringBuilder();
        String result;
        if (ExceptMessage == null) {
            result = "Success";
        } else {
            result = "Exception";
        }
        builder.append("<envelope><header><action>");
        builder.append(result);
        builder.append("</action></header><body>");
        if (ExceptMessage != null) {
            builder.append("<stackTrace>");
            builder.append(ExceptMessage);
            ExceptMessage = null;
            builder.append("</stackTrace>");
        }
        builder.append("</body></envelope>");
        return builder.toString();
    }

}
