package view;

import java.net.*;
import java.util.*;
import java.io.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Client {
    private int serverPort;
    private String address;
    private DataOutputStream out;
    private DataInputStream in;
    private String serverAnswer;
    private String stackTrace;
    private Socket socket;
    private List<String> updateList;
    private List<String> showList;
   
    public Client() throws IOException {
        serverPort = 7070;
        address = "127.0.0.1";
        connection();
    }
    
    public Client(String address, int serverPort) throws IOException {
        this.serverPort = serverPort;
        this.address = address;
        connection();
    }
    
    private void connection() throws IOException {
        InetAddress ipAddress = InetAddress.getByName(address);
        socket = new Socket(ipAddress, serverPort);
    }
    
    private void sendMessage(String message) throws IOException {       
        out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF(message);    
        if (!(out==null)) {
            try {
                out.flush();
            } catch (IOException e) {
                //never been (hope)
            }
        }
    }
    
    private String createMessage(String ACTION, String fakulty, String group, String studentName,
            String studentLastname, Date enrolledDate, Integer studentID) {
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
                message.append(enrolledDate.getTime());
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
    
    private String reading() throws IOException {               
        in = new DataInputStream(socket.getInputStream());
        String xmlResult = in.readUTF();        
        return xmlResult;
    }
    
    private void parsingAnswer(String xmlResult) throws SAXException, IOException, ParserConfigurationException {        
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlResult));
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        NodeList items = doc.getDocumentElement().getChildNodes();
        String action = items.item(0).getChildNodes().item(0).getFirstChild().getNodeValue();
        if ("UPDATE".equals(action)) {
            updateList = new ArrayList<Group>();
            for (int i=0; i<items.item(1).getChildNodes().getLength(); i++) {
                String fakultet = (items.item(1).getChildNodes().item(i).getFirstChild().getNodeValue());
                String group = (items.item(1).getChildNodes().item(++i).getFirstChild().getNodeValue());
                updateList.add(new Group(fakultet, group))
            }
        } else if ("SHOW".equals(action)) {
            updateList = new ArrayList<Student>();
            for (int i=0; i<items.item(1).getChildNodes().getLength(); i++) {
                String id = (items.item(1).getChildNodes().item(i).getFirstChild().getNodeValue());
                String firstName = (items.item(1).getChildNodes().item(++i).getFirstChild().getNodeValue());
                String lastName = (items.item(1).getChildNodes().item(++i).getFirstChild().getNodeValue());
                String group = (items.item(1).getChildNodes().item(++i).getFirstChild().getNodeValue());
                String enrolledDate = (items.item(1).getChildNodes().item(++i).getFirstChild().getNodeValue());
                Student student = new Student(firstName, lastName, group, enrolledDate);
                student.setId(Integer.parseInt(id));
                showList.add(student)
            }
        } else {
            serverAnswer = action;
            if ("Exception".equals(action)) {
                stackTrace = items.item(1).getChildNodes().item(0).getFirstChild().getNodeValue();
            }
        }        
    }
    
    public List<Group> getUpdate() throws IOException, SAXException, ParserConfigurationException {
        sendMessage(createMessage("UPDATE", "", "", "", "", "", null));
        parsingAnswer(reading());
        return updateList;
    }
    
    public List<Student> getShow(String fakulty, String group) throws IOException, SAXException, ParserConfigurationException {
        sendMessage(createMessage("SHOW", fakulty, group, "", "", "", null));
        parsingAnswer(reading());
        return showList;
    }

    public String removeStudent(String fakulty, String group, Integer studentID) throws ServerException, IOException, SAXException, ParserConfigurationException {
        sendMessage(createMessage("REMOVE", fakulty, group, "", "", "", studentID));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new ServerException(stackTrace);
        }
        return serverAnswer;
    }
    
    public String addStudent(String ACTION, String fakulty, String group, String studentName,
            String studentLastname, Date enrolledDate) throws ServerException, IOException, SAXException, ParserConfigurationException {
        sendMessage(createMessage("ADD", fakulty, group, studentName, studentLastname, enrolledDate, null));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new ServerException(stackTrace);
        }
        return serverAnswer;
    }
    
    public String changeStudent(String ACTION, String fakulty, String group, String studentName,
            String studentLastname, Date enrolledDate, Integer studentID) throws ServerException, IOException, SAXException, ParserConfigurationException {
        sendMessage(createMessage("ADD", fakulty, group, studentName, studentLastname, enrolledDate, studentID));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new ServerException(stackTrace);
        }
        return serverAnswer;
    }
}

