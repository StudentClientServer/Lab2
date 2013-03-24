package view;

import java.net.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import model.ServerException;
import model.ServerModel;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import model.Student;

public class ServView implements View {
    private Socket socket;
    private Thread thread;f
    private int port = 7070;
    private String xmlMessage;
    private DataInputStream in;
    private DataOutputStream out;
    private ActionListener controller;
    private ServerModel model;
    private String ExceptMessage = null;
    
    public void setModel(ServerModel model) {
        this.model = model;
    }
    
    public void setController(ActionListener controller) {
        this.controller = controller;
    }
    
    public void starting() {        
        try {
            ServerSocket ss = new ServerSocket(port);             
            while (true) {
                socket = ss.accept();             
                thread = new Thread(new Thread() {   
                    public void run() {         
                    try {
                        reading();
                        parsing(xmlMessage);
                    } catch(Exception exc) {}
                    }
                });
                thread.start();               
            }      
        } catch(Exception x) { 
            x.printStackTrace(); 
        }
    }
    
    public void exceptionHandling(Exception ex) {
        ExceptMessage = ex.getMessage();
    }
    
    private void reading() { 
        try {
            in = new DataInputStream(socket.getInputStream());            
            xmlMessage = in.readUTF();
            System.out.println("Have a line "+xmlMessage);            
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    private void parsing(String xmlMessage) throws IOException {
        try {
            InputSource is = new InputSource();        
            is.setCharacterStream(new StringReader(xmlMessage));
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            NodeList items = doc.getDocumentElement().getChildNodes();
            String action = items.item(0).getChildNodes().item(0).getFirstChild().getNodeValue();
            out = new DataOutputStream(socket.getOutputStream());
            if ("UPDATE".equals(action)) {
                updateMessage(model.getGroups());
            } else if ("SHOW".equals(action)) {
                out.writeUTF(showeMessage(model.getStdents()));
            } else {
                String fakyltet = items.item(0).getChildNodes().item(1).getFirstChild().getNodeValue();
                String group = items.item(0).getChildNodes().item(2).getFirstChild().getNodeValue();
                if ("REMOVE".equals(action)) {
                    String studentID = items.item(1).getChildNodes().item(0).getFirstChild().getNodeValue();
                    fireAction(Integer.parseInt(studentID), "RemoveStudent");
                } else {                    
                    String studentName = items.item(1).getChildNodes().item(0).getFirstChild().getNodeValue();
                    String studentLastname = items.item(1).getChildNodes().item(1).getFirstChild().getNodeValue();
                    String enrolledDate = items.item(1).getChildNodes().item(2).getFirstChild().getNodeValue(); 
                    fireAction(new Student(studentName, studentLastname, group, enrolledDate), "AddStudent");
                    if ("CHANGE".equals(action)) {
                        String studentID = items.item(1).getChildNodes().item(3).getFirstChild().getNodeValue();
                        fireAction(new Student(studentName, studentLastname, group, enrolledDate), "UpdateStudent");
                    }                    
                } 
                out.writeUTF(resultMessage());
            }
        } catch (Exception e) {
            exceptionHandling(e);
            out.writeUTF(resultMessage();            
        } finally {
            if (!(out==null)) {
                out.flush();
            }
        }
    }
    
    private void fireAction(Object source, String command) {
        ActionEvent event = new ActionEvent(source, 0, command);
        controller.actionPerformed(event);
    }
    
    private String updateMessage(List<Group> elements) {
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
    
    private String showeMessage(List<Student> elements) {
        StringBuilder builder = new StringBuilder();
        builder.append("<envelope><header><action>SHOW</action></header><body>");
        for (Student student : students) {
            builder.append("<id>");
            builder.append(student.getId());
            builder.append("</id>");
            builder.append("<firstname>");
            builder.append(student.getFirstName()());
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
    
    private String resultMessage() {
        StringBuilder builder = new StringBuilder();
        String result;
        if (ExceptMessage != null) {
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
