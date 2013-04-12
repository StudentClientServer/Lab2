package client;

import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import exceptions.*;
import model.*;

///////////////////////////////
public class Client {
	private int serverPort;
	private String address;
	private DataOutputStream out;
	private DataInputStream in;
	private String serverAnswer;
	private String stackTrace;
	private Socket socket;
	private List<Group> updateList;
	private List<Student> showList;
	private static final DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
	private String xmlResult;

    /**
     * Reading address and port from configuration file (config.ini)
     * throws ClientException if some problem with reading
     */
	public Client() throws ClientException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("config.ini"));
            while (reader.readLine() != null) {
                if (reader.readLine().equals("Address")) {
                    address = reader.readLine();
                }
                if (reader.readLine().equals("Port")) {
                    serverPort = Integer.parseInt(reader.readLine());
                }
            }
        } catch (IOException e) {
            throw new ClientException("Something wrong with config file!",e);
        } finally {
            try {
                if (reader!=null)
                reader.close();
            } catch (IOException e) {
                throw new ClientException("Something wrong with config file!",e);
            }
        }
	}

	/**
	 * Send message to server
	 * open socket here and close it in method reading() after server answer
	 */
	private void sendMessage(String message) throws ClientException {
		try {
            InetAddress ipAddress = InetAddress.getByName(address);
            socket = new Socket(ipAddress, serverPort);
			out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF(message);
		} catch (IOException e) {
			throw new ClientException(e);
		} //finally {
			/*if (!(out == null)) {
				try {
					out.close();
				} catch (IOException e) {
                    throw new ClientException(e);
				}
			} else {
                try {
                    if (!socket.isClosed() && socket!=null) {
                        socket.close();
                    }
                } catch (IOException e) {
                    throw new ClientException(e);
                }
            }
		}*/
	}

	/**
	 * Create new message according to ACTION
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
	 * Reading answer from server
     * close socket
	 */
	private String reading() throws ServerException {
		try {
            in = new DataInputStream(socket.getInputStream());
			xmlResult = in.readUTF();
        } catch (IOException e) {
            throw new ServerException(e);
        } finally {
            try {
                if (in!=null) {
                    in.close();
                }
            } catch (IOException e) {
                 throw new ServerException(e);
            }
            try {
                if (!socket.isClosed() && socket!=null) {
                    socket.close();
                }
            } catch (IOException e) {
                throw new ServerException(e);
            }
        }
    return xmlResult;
}

    /**
     * Parsing server answer according to ACTION
    */
    private void parsingAnswer(String xmlResult) throws ServerException {
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlResult));
        try{
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        NodeList items = doc.getDocumentElement().getChildNodes();
        String action = items.item(0).getChildNodes().item(0).getFirstChild().getNodeValue();
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
        sendMessage(createMessage("UPDATE", "", "", "", "", null, null));
        parsingAnswer(reading());
        return updateList;
    }

    /**
     * Return list of students
     */
    public List<Student> getShow(String fakulty, String group) throws ServerException,ClientException {
        sendMessage(createMessage("SHOW", fakulty, group, "", "", null, null));
        parsingAnswer(reading());
        return showList;
    }

    /**
     * Remove student from group with by id
     */
    public String removeStudent( String group, Integer studentID) throws ServerException, ClientException{
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
        sendMessage(createMessage("REMOVEGroup", fakulty, group, "", "", null, null));
        parsingAnswer(reading());
        if ("Exception".equals(serverAnswer)) {
            throw new ServerException(stackTrace);
        }
        return serverAnswer;
    }
}