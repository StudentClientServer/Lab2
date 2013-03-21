package view;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Client {
    private int serverPort;
    private String address; 
    private DataOutputStream out;
    private DataInputStream in;
    private String serverAnswer;
    private String stackTrace;
    private Socket socket;
    private ArrayList<String> updateList;
   
    public Client() {
        serverPort = 7070;
        address = "127.0.0.1";
        connection();
    }
    
    public Client(String address, int serverPort) {
        this.serverPort = serverPort;
        this.address = address;
        connection();
    }
    
    private void connection() {
        try {
            InetAddress ipAddress = InetAddress.getByName(address);            
            socket = new Socket(ipAddress, serverPort);            
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
    
    private void sendMessage(String message) {
        try {            
            out = new DataOutputStream(socket.getOutputStream());                    
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!(out==null)) {
                try {
                    out.flush();
                } catch (IOException e) {
                    //never been (hope)
                }
            }
        }
    }
    
    private String createMessage(String ACTION, String fakulty, String group, String studentName, 
            String studentLastname, String enrolledDate, Integer studentID) {
        StringBuilder message = new StringBuilder();        
        message.append("<envelope><header><action>");
        message.append(ACTION);
        message.append("</action>");
        if (!"UPDATE".equals(ACTION)) {
            message.append("<fakulty>");
            message.append(fakulty);
            message.append("</fakulty><group>");
            message.append(group);
            message.append("</group>");
        }
        message.append("</header><body>");
        if (!"UPDATE".equals(ACTION) && !"SHOW".equals(ACTION)) {
            if (!"REMOVE".equals(ACTION)) {
                message.append("<studentName>");
                message.append(studentName);
                message.append("</studentName><studentLastname>");
                message.append(studentLastname);
                message.append("</studentLastname><enrolledDate>");
                message.append(enrolledDate);
                message.append("</enrolledDate>");
            }
            if (!"ADD".equals(ACTION)) {
                message.append("<studentID>");
                message.append(studentID);
                message.append("</studentID>"); 
            }
        }
        message.append("</body></envelope>");        
        return message.toString();        
    }
    
    private String reading() {
        String xmlResult = null;
        try {
            in = new DataInputStream(socket.getInputStream());            
            xmlResult = in.readUTF(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xmlResult;       
    }   
    
    private void parsingAnswer(String xmlResult) {        
        try {
            InputSource is = new InputSource();        
            is.setCharacterStream(new StringReader(xmlResult));
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            NodeList items = doc.getDocumentElement().getChildNodes();
            String action = items.item(0).getChildNodes().item(0).getFirstChild().getNodeValue();   
            if ("UPDATE".equals(action)) {
                updateList = new ArrayList<String>();
                for (int i=0; i<items.item(1).getChildNodes().getLength(); i++) {
                    updateList.add(items.item(1).getChildNodes().item(i).getFirstChild().getNodeValue());
                }
            } else {
                serverAnswer = action;        
                stackTrace = items.item(1).getChildNodes().item(0).getFirstChild().getNodeValue();
            }  
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage());
        } catch (Exception e) {
            
        }
    }
    
    public ArrayList<String> getUpdate() {
        sendMessage(createMessage("UPDATE", "", "", "", "", "", null));
        parsingAnswer(reading());
        return updateList;
    }
    
    public ArrayList<String> getShow(String fakulty, String group) {
        sendMessage(createMessage("SHOW", fakulty, group, "", "", "", null));
        parsingAnswer(reading());
        return updateList;
    }

    public String removeStudent(String fakulty, String group, Integer studentID) throws Exception {
        sendMessage(createMessage("REMOVE", fakulty, group, "", "", "", studentID));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new Exception(stackTrace);
        }
        return serverAnswer;
    }
    
    public String addStudent(String ACTION, String fakulty, String group, String studentName, 
            String studentLastname, String enrolledDate) throws Exception {
        sendMessage(createMessage("ADD", fakulty, group, studentName, studentLastname, enrolledDate, null));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new Exception(stackTrace);
        }
        return serverAnswer;
    }
    
    public String changeStudent(String ACTION, String fakulty, String group, String studentName, 
            String studentLastname, String enrolledDate, Integer studentID) throws Exception {
        sendMessage(createMessage("ADD", fakulty, group, studentName, studentLastname, enrolledDate, studentID));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new Exception(stackTrace);
        }
        return serverAnswer;
    }    
}
