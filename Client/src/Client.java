package client;

import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


// TODO: Auto-generated Javadoc
/**
 * The Class Client.
 */
public class Client {
	
	/** The server port. */
	private int serverPort;
	
	/** The address. */
	private String address;
	
	/** The out. */
	private DataOutputStream out;
	
	/** The in. */
	private DataInputStream in;
	
	/** The server answer. */
	private String serverAnswer;
	
	/** The stack trace. */
	private String stackTrace;
	
	/** The socket. */
	private Socket socket;
	
	/** The update list. */
	private List<Group> updateList;
	
	/** The show list. */
	private List<Student> showList;
	
	/** The Constant format. */
	private static final DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
	
	/** The xml result. */
	private String xmlResult;

	/**
	 * Instantiates a new client.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Client() throws IOException {
		serverPort = 7070;
		address = "127.0.0.1";
		//connection();
	}

	/**
	 * Instantiates a new client.
	 *
	 * @param address the address
	 * @param serverPort the server port
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClientException 
	 */
	public Client(String address, int serverPort) throws IOException, ClientException {
		this.serverPort = serverPort;
		this.address = address;
		//connection();
	}

	/**
	 * Connection.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClientException 
	 */
	private void connection() throws IOException, ClientException {
		try {
			InetAddress ipAddress = InetAddress.getByName(address);
			socket = new Socket(ipAddress, serverPort);
		} catch (Exception x) {
			throw new ClientException(x);
		}
	}
	
	public void close() throws ClientException{
		try {
			socket.close();
		} catch (IOException e) {
			throw new ClientException(e);
		}
	}

	/**
	 * Send message.
	 *
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void sendMessage(String message) throws IOException {
		try {
			System.out.println("socket.getOutputStream() "+message);
			out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF(message);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (!(out == null)) {
				try {
					out.flush();
					//out.close();
				} catch (IOException e) {
					// never been (hope)
				}
			}
		}
	}

	/**
	 * Creates the message.
	 *
	 * @param ACTION the action
	 * @param fakulty the fakulty
	 * @param group the group
	 * @param studentName the student name
	 * @param studentLastname the student lastname
	 * @param enrolledDate the enrolled date
	 * @param studentID the student id
	 * @return the string
	 */
	private String createMessage(String ACTION, String fakulty, String group,
			String studentName, String studentLastname, String  enrolledDate,
			Integer studentID) {
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
	 * Reading.
	 *
	 * @return the string
	 * @throws ClientException the client exception
	 */
	private String reading() throws ClientException {
		try {
			in = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			throw new ClientException(e);	
		}
		try {
			System.out.println("xmlResult = in.readUTF();");
			xmlResult = in.readUTF();
			if (!xmlResult.equals("")){
				
				return xmlResult;
			}
			System.out.println("xmlResult = in.readUTF();");
		} catch (IOException e) {
			
			e.printStackTrace();
		} 
		System.out.println("xmlResult "+xmlResult);
		
        return xmlResult;
        
	}

	/**
	 * Parsing server answer according to ACTION.
	 *
	 * @param xmlResult the xml result
	 * @throws SAXException the sAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	    private void parsingAnswer(String xmlResult) throws SAXException, IOException, ParserConfigurationException {
	    	
	        InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(xmlResult));
	        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
	        NodeList items = doc.getDocumentElement().getChildNodes();
	        String action = items.item(0).getChildNodes().item(0).getFirstChild().getNodeValue();
	        System.out.println("SHOW"+action);
	        if ("UPDATE".equals(action)) {
	            updateList = new ArrayList<Group>();
	            for (int i=0; i<items.item(1).getChildNodes().getLength(); i++) {
	                String fakultet = (items.item(1).getChildNodes().item(i).getFirstChild().getNodeValue());
	                String group = (items.item(1).getChildNodes().item(++i).getFirstChild().getNodeValue());
	                updateList.add(new Group(fakultet, group));
	            }
	        } else if ("SHOW".equals(action)) {
	        	
	        	showList = new ArrayList<Student>();
	            for (int i=0; i<items.item(1).getChildNodes().getLength(); i++) {
	                String id = (items.item(1).getChildNodes().item(i).getFirstChild().getNodeValue());
	                String firstName = (items.item(1).getChildNodes().item(++i).getFirstChild().getNodeValue());
	                String lastName = (items.item(1).getChildNodes().item(++i).getFirstChild().getNodeValue());
	                String group = (items.item(1).getChildNodes().item(++i).getFirstChild().getNodeValue());
	                String enrolledDate = (items.item(1).getChildNodes().item(++i).getFirstChild().getNodeValue());                
	                showList.add(new Student(Integer.parseInt(id),firstName, lastName, group, enrolledDate));
	            }
	        } else {
	            serverAnswer = action;
	            if ("Exception".equals(action)) {
	                stackTrace = items.item(1).getChildNodes().item(0).getFirstChild().getNodeValue();
	            }
	        }        
	    }
	    
	    /**
    	 * Return list of groups.
    	 *
    	 * @return the update
    	 * @throws IOException Signals that an I/O exception has occurred.
    	 * @throws SAXException the sAX exception
    	 * @throws ParserConfigurationException the parser configuration exception
	     * @throws ClientException 
    	 */
	    public List<Group> getUpdate() throws IOException, SAXException, ParserConfigurationException, ClientException {
	    	connection();
	    	sendMessage(createMessage("UPDATE", "", "", "", "", null, null));
	        try {
				parsingAnswer(reading());
			} catch (ClientException e) {
				throw new ClientException(e);
			}
	        
	        socket.close();
	        return updateList;
	    }
	    
	    /**
    	 * Return list of students.
    	 *
    	 * @param fakulty the fakulty
    	 * @param group the group
    	 * @return the show
    	 * @throws IOException Signals that an I/O exception has occurred.
    	 * @throws SAXException the sAX exception
    	 * @throws ParserConfigurationException the parser configuration exception
	     * @throws ClientException 
    	 */
	    public List<Student> getShow(String fakulty, String group) throws IOException, SAXException, ParserConfigurationException, ClientException {
	        
	        connection();
	    	sendMessage(createMessage("SHOW", fakulty, group, "", "", null, null));
	        
	        parsingAnswer(reading());
	        
	        socket.close();
	        return showList;
	    }
	    
	    /**
    	 * Remove student from group with by id.
    	 *
    	 * @param group the group
    	 * @param studentID the student id
    	 * @return the string
    	 * @throws ServerException the server exception
    	 * @throws IOException Signals that an I/O exception has occurred.
    	 * @throws SAXException the sAX exception
    	 * @throws ParserConfigurationException the parser configuration exception
	     * @throws ClientException 
    	 */
	    public String removeStudent( String group, Integer studentID) throws ServerException, IOException, SAXException, ParserConfigurationException, ClientException {
	    	connection();
	    	sendMessage(createMessage("REMOVE", null, group, "", "", null, studentID));
	        parsingAnswer(reading());
	        if ("Exception".equals(serverAnswer)) {
	            throw new ServerException(stackTrace);
	        }
	        socket.close();
	        return serverAnswer;
	    }
	    
	    /**
    	 * Add new student.
    	 *
    	 * @param group the group
    	 * @param studentName the student name
    	 * @param studentLastname the student lastname
    	 * @param enrolledDate the enrolled date
    	 * @param studentID the student id
    	 * @return the string
    	 * @throws ServerException the server exception
    	 * @throws IOException Signals that an I/O exception has occurred.
    	 * @throws SAXException the sAX exception
    	 * @throws ParserConfigurationException the parser configuration exception
	     * @throws ClientException 
    	 */
	    public String addStudent( String group, String studentName,
	            String studentLastname, String enrolledDate, Integer studentID) throws ServerException, IOException, SAXException, ParserConfigurationException, ClientException {
	    	connection();
	    	sendMessage(createMessage("ADD", null, group, studentName, studentLastname, enrolledDate, studentID));
	        parsingAnswer(reading());
	        if ("Exception".equals(serverAnswer)) {
	            throw new ServerException(stackTrace);
	        }
	        socket.close();
	        return serverAnswer;
	    }
	    
	    /**
    	 * Change student by id.
    	 *
    	 * @param fakulty the fakulty
    	 * @param group the group
    	 * @param studentName the student name
    	 * @param studentLastname the student lastname
    	 * @param enrolledDate the enrolled date
    	 * @param studentID the student id
    	 * @return the string
    	 * @throws ServerException the server exception
    	 * @throws IOException Signals that an I/O exception has occurred.
    	 * @throws SAXException the sAX exception
    	 * @throws ParserConfigurationException the parser configuration exception
	     * @throws ClientException 
    	 */
	    public String changeStudent(String fakulty, String group, String studentName,
	            String studentLastname, String enrolledDate, Integer studentID) throws ServerException, IOException, SAXException, ParserConfigurationException, ClientException {
	    	connection();
	    	sendMessage(createMessage("CHANGE", null, group, studentName, studentLastname, enrolledDate, studentID));
	        parsingAnswer(reading());
	        if ("Exception".equals(serverAnswer)) {
	            throw new ServerException(stackTrace);
	        }
	        socket.close();
	        return serverAnswer;
	    }
	    
	    /**
    	 * Add new group.
    	 *
    	 * @param fakulty the fakulty
    	 * @param group the group
    	 * @return the string
    	 * @throws ServerException the server exception
    	 * @throws IOException Signals that an I/O exception has occurred.
    	 * @throws SAXException the sAX exception
    	 * @throws ParserConfigurationException the parser configuration exception
	     * @throws ClientException 
    	 */
	    public String addGroup(String fakulty, String group) throws ServerException, IOException, SAXException, ParserConfigurationException, ClientException {
	    	connection();
	    	sendMessage(createMessage("ADDGroup", fakulty, group, "", "", null, null));
	        parsingAnswer(reading());
	        if ("Exception".equals(serverAnswer)) {
	            throw new ServerException(stackTrace);
	        }
	        socket.close();
	        return serverAnswer;
	    }
	    
	    /**
    	 * Remove group.
    	 *
    	 * @param fakulty the fakulty
    	 * @param group the group
    	 * @return the string
    	 * @throws ServerException the server exception
    	 * @throws IOException Signals that an I/O exception has occurred.
    	 * @throws SAXException the sAX exception
    	 * @throws ParserConfigurationException the parser configuration exception
	     * @throws ClientException 
    	 */
	    public String removeGroup(String fakulty, String group) throws ServerException, IOException, SAXException, ParserConfigurationException, ClientException {
	    	connection();
	    	sendMessage(createMessage("REMOVEGroup", fakulty, group, "", "", null, null));
	        parsingAnswer(reading());
	        if ("Exception".equals(serverAnswer)) {
	            throw new ServerException(stackTrace);
	        }
	        socket.close();
	        return serverAnswer;
	    }
}
