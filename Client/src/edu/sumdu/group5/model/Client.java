package edu.sumdu.group5.model;

import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.apache.log4j.Logger;

import edu.sumdu.group5.exception.*;
import edu.sumdu.group5.model.*;


public class Client {
	/** The logger. */
    private static final Logger log = Logger.getLogger(Client.class);
    private DataOutputStream out;
    private DataInputStream in;
    private String serverAnswer;
    private String stackTrace;
    
    /** List of groups. */
    private List<Group> updateList;
    
    /** List of students. */
    private List<Student> showList;
    private static final DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
    
    /** Answer from server side. */
    private String xmlResult;   

    /**
     * Send message to server
     */
    private void sendMessage(String message) throws ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called send message");
        }
        try { 
            out = new DataOutputStream(SocketSingleton.getSocket().getOutputStream());
            out.writeUTF(message);
        } catch (IOException e) {
            throw new ClientException(e);
        } 
    }

    /**
     * Create new message according to ACTION
     */
    private String createMessage(String ACTION, String fakulty, String group,
            String studentName, String studentLastname, String  enrolledDate,Integer studentID) {
        if (log.isDebugEnabled()){
            log.debug("Creating SOAP message called");
        }
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
        if (!"UPDATE".equals(ACTION) && !"SHOW".equals(ACTION) && !"REMOVEGroup".equals(ACTION)) {
            if (!"REMOVE".equals(ACTION)) {
                message.append("<studentName>");
                message.append(studentName);
                message.append("</studentName><studentLastname>");
                message.append(studentLastname);
                message.append("</studentLastname><enrolledDate>");
                message.append(enrolledDate);
                message.append("</enrolledDate>");
            }
            if ("ADD".equals(ACTION) || "REMOVE".equals(ACTION)|| "CHANGE".equals(ACTION)) {
                message.append("<studentID>");
                message.append(studentID);
                message.append("</studentID>");
            }
        }
        message.append("</body></envelope>");
        return message.toString();
    }

    /**
     * Reading answer from server
     */
    private String reading() throws ServerException {
        if (log.isDebugEnabled()){
            log.debug("Reading stream called");
        }
        try {
            in = new DataInputStream(SocketSingleton.getSocket().getInputStream());
            xmlResult = in.readUTF();
        } catch (Exception e) {
            throw new ServerException(e);
        }
    return xmlResult;
}

    /**
     * Parsing server answer according to ACTION
    */
    private void parsingAnswer(String xmlResult) throws ServerException {
        if (log.isDebugEnabled()){
            log.debug("Parsing answer called");
        }
        try{
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlResult));
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);    
            XPathFactory factory = XPathFactory.newInstance();
            XPath xPath = factory.newXPath();
            XPathExpression expr = xPath.compile("//envelope/*");
        
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList xDoc = (NodeList) result;
        
            NodeList xHeader = (NodeList) xDoc.item(0);
            NodeList xBody = (NodeList) xDoc.item(1).getFirstChild();
            String action = xPath.evaluate("//action", xHeader);
        
            if ("UPDATE".equals(action)) {
                updateList = new ArrayList<Group>();
            
                XPathExpression expr2 = xPath.compile("//envelope/body/groups/*");
                NodeList xGroups = (NodeList) expr2.evaluate(doc, XPathConstants.NODESET);
                for (int i=0;i<xGroups.getLength();i++) {
                    String fakultet = xGroups.item(i).getLastChild().getTextContent();
                    String group = xGroups.item(i).getFirstChild().getTextContent();
                    updateList.add(new Group(fakultet, group));
                }
            } else if ("SHOW".equals(action)) {
                showList = new ArrayList<Student>();
                XPathExpression expr3 = xPath.compile("//envelope/body/students/student");
                NodeList xStudents = (NodeList) expr3.evaluate(doc, XPathConstants.NODESET);           
                for (int i=0; i<xStudents.getLength(); i++) {
                    String str = "//envelope/body/students/student";
                    XPathExpression expr4 = xPath.compile(str);
                    NodeList xStudents2 = (NodeList) expr4.evaluate(doc, XPathConstants.NODESET);
                
                    String id = xPath.evaluate("id", xStudents2.item(i));
                    String firstName = xPath.evaluate("firstname", xStudents2.item(i));
                    String lastName = xPath.evaluate("lastname", xStudents2.item(i));
                    String group = xPath.evaluate("groupnumber", xStudents2.item(i));
                    String enrolledDate = xPath.evaluate("enrolled", xStudents2.item(i));
                    showList.add(new Student(Integer.parseInt(id),firstName, lastName, group, enrolledDate));
                }
            } else {
                serverAnswer = action;
                if ("Exception".equals(action)) {
                    NodeList xException = (NodeList) xPath.evaluate("//envelope/body", is, XPathConstants.NODESET);
                    stackTrace = xPath.evaluate("//stackTrace", xException);
                }
            }
        }catch(XPathExpressionException e){
            throw new ServerException(e);
        }catch(ParserConfigurationException e){
            throw new ServerException(e);
        }catch(SAXException e){
            throw new ServerException(e);
        }catch(IOException e){
        throw new ServerException(e);
        }
    }

    /**
     * Return list of groups
     */
    public List<Group> getUpdate() throws ServerException,ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called get update");
        }
        sendMessage(createMessage("UPDATE", "", "", "", "", null, null));
        parsingAnswer(reading());
        return updateList;
    }

    /**
     * Return list of students
     */
    public List<Student> getShow(String fakulty, String group) throws ServerException,ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called method get show");
        }
        sendMessage(createMessage("SHOW", fakulty, group, "", "", null, null));
        parsingAnswer(reading());
        return showList;
    }

    /**
     * Remove student from group with by id
     */
    public String removeStudent( String group, Integer studentID) throws ServerException, ClientException{
        if (log.isDebugEnabled()){
            log.debug("Called remove student");
        }
        sendMessage(createMessage("REMOVE", null, group, "", "", null, studentID));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new ServerException(stackTrace);
        }
        return serverAnswer;
    }

    /**
     * Add new student
     */
    public String addStudent( String group, String studentName,
            String studentLastname, String enrolledDate, Integer studentID) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called adding student");
        }
        sendMessage(createMessage("ADD", null, group, studentName, studentLastname, enrolledDate, studentID));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new ServerException(stackTrace);
        }
        return serverAnswer;
    }

    /**
     * Change student by id
     */
    public String changeStudent(String fakulty, String group, String studentName,
            String studentLastname, String enrolledDate, Integer studentID) throws ServerException, ClientException{
        if (log.isDebugEnabled()){    
            log.debug("Called change student");
        }
        sendMessage(createMessage("CHANGE", null, group, studentName, studentLastname, enrolledDate, studentID));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new ServerException(stackTrace);
        }
        return serverAnswer;
    }

    /**
     * Add new group
     */
    public String addGroup(String fakulty, String group) throws ServerException, ClientException {
        if (log.isDebugEnabled()){
            log.debug("Called adding group");
        }
        sendMessage(createMessage("ADDGroup", fakulty, group, "", "", null, null));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new ServerException(stackTrace);
        }
        return serverAnswer;
    }

    /**
     * Remove group
     */
    public String removeGroup(String fakulty, String group) throws ServerException, ClientException{
        if (log.isDebugEnabled()){
            log.debug("Called remove group");
        }
        sendMessage(createMessage("REMOVEGroup", fakulty, group, "", "", null, null));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new ServerException(stackTrace);
        }
        return serverAnswer;
    }
    
    /**
	 * Close.
	 */
	public void close() throws ClientException{
		sendMessage(createMessage("EXIT", "", "", "", "", null, null));
	}
}