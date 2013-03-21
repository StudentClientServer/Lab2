package view;

import java.net.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import model.Student;

public class ServView {
    private Socket socket;
    private Thread thread;f
    private int port = 7070;
    private String xmlMessage;
    private DataInputStream in;
    private DataOutputStream out;
    private ActionListener controller;
    private ServerModel model;
    
    public ServerView(ActionListener controller, ServerModel model) {
        this.model = model;
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
    
    private void reading() {
        System.out.println("Get connection!!!!!"); 
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
            } else {
                String fakyltet = items.item(0).getChildNodes().item(1).getFirstChild().getNodeValue();
                String group = items.item(0).getChildNodes().item(2).getFirstChild().getNodeValue();
                if ("REMOVE".equals(action)) {
                    String studentID = items.item(1).getChildNodes().item(0).getFirstChild().getNodeValue();
                    fireAction(Integer.parseInt(studentID), "RemoveStudent");
                } else if (!"SHOW".equals(action)) {                    
                        String studentName = items.item(1).getChildNodes().item(0).getFirstChild().getNodeValue();
                        String studentLastname = items.item(1).getChildNodes().item(1).getFirstChild().getNodeValue();
                        String enrolledDate = items.item(1).getChildNodes().item(2).getFirstChild().getNodeValue(); 
                        fireAction(new Student(studentName, studentLastname, enrolledDate), "AddStudent");
                        if ("CHANGE".equals(action)) {
                            String studentID = items.item(1).getChildNodes().item(3).getFirstChild().getNodeValue();
                            fireAction(new Student(studentName, studentLastname, enrolledDate, (Integer.parseInt(studentID)), "ChangeStudent");
                        }                    
                } else {
                   out.writeUTF(updateMessage(model.getStdents()));                    
                }
                out.writeUTF(resultMessage("Success", ""));
            }
        } catch (Exception e) {
            out.writeUTF(resultMessage("Exception", e.getMessage()));            
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
    
    private String updateMessage(List<String> elements) {
        StringBuilder builder = new StringBuilder();
        builder.append("<envelope><header><action>UPDATE</action></header><body>");
        for (String element : elements) {
            builder.append("<element>");
            builder.append(element);
            builder.append("</element>");
        }
        builder.append("</body></envelope>");
        return builder.toString();
    }
    
    private String resultMessage(String result, String stackTrace) {
        StringBuilder builder = new StringBuilder();
        builder.append("<envelope><header><action>");
        builder.append(result);
        builder.append("</action></header><body>");
        if ("Exception".equals(result)) {
            builder.append("<stackTrace>");
            builder.append(stackTrace);
            builder.append("</stackTrace>");
        }
        builder.append("</body></envelope>");
        return builder.toString();
    }    
}
