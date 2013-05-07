package edu.sumdu.group5.server.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.apache.log4j.Logger;

/**
 * The Class Server, using XML as DB.
 */
public class Server implements ServerModel {
	/** The logger. */
	private static final Logger log = Logger.getLogger(Server.class);

	/** The document. */
	private Document document = null;

	/** The xml path. */
	private String xmlPath;

	/** The dtd path. */
	private String dtdPath;

	/**
	 * Gets the dtd path.
	 * 
	 * @return the dtd path
	 */
	private String getDtdPath() {
		if (log.isDebugEnabled())
			log.debug("Method call");
		return dtdPath;
	}

	/**
	 * Local copy of data, which used to upload changes
	 */
	private ArrayList<Group> groupsLocalCopy = new ArrayList<Group>();

	/**
	 * List of groups
	 */
	private ArrayList<Group> groups = new ArrayList<Group>();

	/**
	 * Sets the dtd path.
	 * 
	 * @param dtdPath
	 *            the new dtd path
	 */
	public void setDtdPath(String dtdPath) {
		if (log.isDebugEnabled())
			log.debug("Method call");
		this.dtdPath = dtdPath;
	}

	/**
	 * Instantiates a new server.
	 * 
	 * @param xmlPath
	 *            the path to xml file
	 * @param dtdPath
	 *            the path to dtd file
	 * @throws ServerException
	 *             if can not read xml file
	 */
	private Server(String xmlPath, String dtdPath) throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Construktor call. Arguments: " + xmlPath + " " + dtdPath);
		setXmlPath(xmlPath);
		setDtdPath(dtdPath);
		readDocument();
	}

	/**
	 * Instantiates a new server.
	 * 
	 * @throws ServerException
	 *             the server exception
	 */
	public Server() throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Construktor call");
	}

	/**
	 * Gets the xml path.
	 * 
	 * @return the xml path
	 */
	private String getXmlPath() {
		if (log.isDebugEnabled())
			log.debug("Method call");
		return xmlPath;
	}

	/**
	 * Sets the xml path.
	 * 
	 * @param xmlPath
	 *            the new xml path
	 */
	public void setXmlPath(String xmlPath) {
		if (log.isDebugEnabled())
			log.debug("Method call");
		this.xmlPath = xmlPath;

	}

	/**
	 * Return the local copy of data
	 * 
	 * @return the groupsLocalCopy
	 */
	private ArrayList<Group> getGroupsLocalCopy() {
		if (log.isDebugEnabled())
			log.debug("Method call");
		return groupsLocalCopy;
	}

	/**
	 * Make a copy of data
	 * 
	 * @param groups
	 *            the groups to set
	 */
	private void setGroupsLocalCopy(ArrayList<Group> groups) {
		if (log.isDebugEnabled())
			log.debug("Method call");
		this.groupsLocalCopy = new ArrayList<Group>();
		for (Group gr : groups) {
			this.groupsLocalCopy.add((Group) gr.clone());
		}
	}

	/**
	 * Sets groups
	 * 
	 * @param groups
	 */
	private void setGroups(List<Group> groups) {		
		if (log.isDebugEnabled())
			log.debug("Method call");
		this.groups = (ArrayList<Group>) groups;
	}

	/**
	 * Reads document using xml path.
	 * 
	 * @throws ServerException
	 *             if can't not read or parse xml file
	 */
	public void readDocument() throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call");
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(true);
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			docBuilder.setErrorHandler(new org.xml.sax.ErrorHandler() {

				public void fatalError(SAXParseException exception)
						throws SAXException {
					throw new SAXParseException("Errors in xml-file. Line "
							+ exception.getLineNumber() + ", clolumn "
							+ exception.getColumnNumber() + ". "
							+ exception.getMessage(), null);
				}

				public void warning(SAXParseException err)
						throws SAXParseException {
					throw new SAXParseException("Errors in xml-file. Line "
							+ err.getLineNumber() + ", clolumn "
							+ err.getColumnNumber() + ". " + err.getMessage(),
							null);
				}

				public void error(SAXParseException e) throws SAXParseException {
					throw new SAXParseException("Errors in xml-file. Line "
							+ e.getLineNumber() + ", clolumn "
							+ e.getColumnNumber() + ". " + e.getMessage(), null);
				}
			});
			setDocument(docBuilder.parse(new File(xmlPath)));
			readGroups();
		} catch (ParserConfigurationException e) {
			ServerException ex = new ServerException("Can not read xml-file", e);
			log.error("Exception", ex);
			throw ex;
		} catch (IOException e) {
			ServerException ex = new ServerException("Can not read xml-file", e);
			log.error("Exception", e);
			throw ex;
		} catch (SAXException e) {
			ServerException ex = new ServerException("Can not read xml-file", e);
			log.error("Exception", e);
			throw ex;
		}
	}

	/**
	 * Returns the list of students for specified group.
	 * 
	 * @param group
	 *            the group
	 * @return the students
	 * @throws ServerException
	 *             if can't get list of students for specified group
	 */
	public List<Student> getStudents(Group group) throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call. Arguments: " + group);
		return group.getStudents();
	}

	/**
	 * Gets the list of groups.
	 * 
	 * @return the groups
	 * @throws ServerException
	 *             if can't get list of group
	 */
	public List<Group> getGroups() throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call");
		return groups;
	}

	/**
	 * Removes the student.
	 * 
	 * @param id
	 *            the id
	 * @throws ServerException
	 *             if a student with specified id does not exist
	 */
	public void removeStudent(Integer id) throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call. Arguments: " + id);
		for (Group g : groups) {
			if (g.containsStudent(id)) {
				g.removeStudent(id);
				return;
			}
		}
		ServerException ex = new ServerException("Student with ID " + id
				+ " does not exist");
		log.error("Exception", ex);
		throw ex;
	}

	/**
	 * Removes the group.
	 * 
	 * @param groupNumber
	 *            the group name
	 * @throws ServerException
	 *             if can't remove the group or group with specified number does
	 *             not exist
	 */
	public void removeGroup(String groupNumber) throws ServerException {
		try {
			if (log.isDebugEnabled())
				log.debug("Method call. Arguments: " + groupNumber);
			groups.remove(getGroup(groupNumber));
		} catch (ServerException e) {
			ServerException ex = new ServerException("Can not remove a group",
					e);
			log.error("Exception", ex);
			throw ex;
		}
	}

	/**
	 * Adds the student, check ID for uniqueness.
	 * 
	 * @param student
	 *            the student
	 * @throws ServerException
	 *             if id is not unique or if group with specified number does
	 *             not exist
	 */
	public void addStudent(Student student) throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call. Arguments: " + student);
		for (Group g : groups) {
			if (g.containsStudent(student.getId())) {
				ServerException ex = new ServerException(
						"Can't add the student! Student with ID "
								+ student.getId() + " is already exist!");
				log.error("Exception", ex);
				throw ex;
			}
			if (g.getNumber().equals(student.getGroupNumber())) {
				g.addStudent(student);
				;
				return;
			}
		}
		ServerException ex = new ServerException(
				"Error! Can not add student, because group with name '"
						+ student.getGroupNumber() + "' does not exist");

		log.error("Exception", ex);
		throw ex;
	}

	/**
	 * Adds the group.
	 * 
	 * @param group
	 *            the group
	 * @throws ServerException
	 *             if group number is not unique
	 */
	public void addGroup(Group group) throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call. Arguments: " + group);
		checkGroupNumberForUnique(group.getNumber());
		groups.add(group);
	}

	/**
	 * Checks group number for uniqueness.
	 * 
	 * @param groupNumber
	 *            number to check
	 * @throws ServerException
	 *             if group number is not unique
	 */
	private void checkGroupNumberForUnique(String groupNumber)
			throws ServerException {
		for (Group g : groups) {
			if (g.getNumber().equals(groupNumber)) {
				ServerException ex = new ServerException("A group with number "
						+ g.getNumber() + " is alredy exist");
				log.error("Exception", ex);
				throw ex;
			}
		}
	}

	/**
	 * Gets the document.
	 * 
	 * @return the document
	 */
	private Document getDocument() {
		if (log.isDebugEnabled())
			log.debug("Method call");
		return document;
	}

	/**
	 * Sets the document.
	 * 
	 * @param document
	 *            the new document
	 */
	private void setDocument(Document document) {
		if (log.isDebugEnabled())
			log.debug("Method call");
		this.document = document;
	}

	/**
	 * Gets the group by number.
	 * 
	 * @param number
	 *            the number
	 * @return the group by number
	 * @throws ServerException
	 *             if can not find a group with specified number.
	 */
	public Group getGroup(String number) throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call. Arguments: " + number);
		for (Group g : groups) {
			if (g.getNumber().equals(number))
				return g;
		}
		ServerException ex = new ServerException("Group with number " + number
				+ " does not exist.");
		log.error("Exception", ex);
		throw ex;
	}

	/**
	 * Update group, change faculty.
	 * 
	 * @param group
	 *            the group
	 * @throws ServerException
	 *             if group is removed.
	 */
	public void updateGroup(Group newGroup) throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call. Arguments: " + newGroup);
		try {
			Group group = getGroup(newGroup.getNumber());
			group.setFakulty(newGroup.getFakulty());
		} catch (ServerException e) {
			ServerException ex = new ServerException(
					"Can not update group. Maybe someone removed it.", e);
			log.error("Exception", ex);
			throw ex;
		}
	}

	/**
	 * Updates group, change group number.
	 * 
	 * @param group
	 *            the group
	 * @param newNumber
	 *            number you want to set
	 * @throws ServerException
	 *             if group is removed or new number not unique.
	 */
	public void updateGroup(String oldNumber, String newNumber)
			throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call. Arguments: " + oldNumber + " " + newNumber);
		try {
			checkGroupNumberForUnique(newNumber);
			Group group = getGroup(oldNumber);
			group.setNumber(newNumber);
		} catch (ServerException e) {
			ServerException ex = new ServerException(
					"Can not update group. Maybe someone removed it or new number is already exist.",
					e);
			log.error("Exception", ex);
			throw ex;
		}
	}

	/**
	 * Updates student.
	 * 
	 * @param student
	 *            the student you want to update.
	 * @throws ServerException
	 *             if can not remove or add student.
	 */
	public void updateStudent(Student student) throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call. Arguments: " + student);
		try {
			getGroup(student.getGroupNumber());
			// check if the
			// specified group
			// exist. If no -
			// throws
			// an exception.
			removeStudent(student.getId());
			addStudent(student);
		} catch (ServerException e) {
			ServerException ex = new ServerException(
					"Can not update student. Maybe someone removed it or specified group is not exist.",
					e);
			log.error("Exception", ex);
			throw ex;
		}
	}

	/**
	 * Saves changes to DB.
	 * 
	 * @param charSet
	 *            the char set
	 * @throws ServerException
	 *             if can not save data to file
	 */
	private void saveXML() throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call");
		Writer target = null;
		try {
			target = new OutputStreamWriter(new FileOutputStream(xmlPath),
					"UTF-8");
			Source source = new DOMSource(document);
			StreamResult dest = new StreamResult(target);
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "groups.dtd");
			t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(source, dest);
			target.flush();
		} catch (Exception e) {
			ServerException ex = new ServerException("Can't save xml!", e);
			log.error("Exception", e);
			throw ex;
		} finally {
			try {
				if (target != null)
					target.close();
			} catch (IOException e) {
				ServerException ex = new ServerException(e);
				log.error("Can't save data to xml file");
				throw ex;
			}
		}
	}

	/**
	 * Reads groups from xml file and save to groups object
	 * 
	 * @throws ServerException
	 *             if can't read groups
	 */
	private void readGroups() throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call");
		Element root = document.getDocumentElement();
		NodeList groupsNodes = root.getElementsByTagName("group");
		for (int i = 0; i < groupsNodes.getLength(); i++) {
			NamedNodeMap attributes = groupsNodes.item(i).getAttributes();
			Group currentGroup = new Group(attributes.getNamedItem("fakulty")
					.getTextContent(), attributes.getNamedItem("number")
					.getTextContent());
			groups.add(currentGroup);
			readStudents((Element) groupsNodes.item(i), currentGroup);
		}
		setGroups(groups);
		setGroupsLocalCopy(groups);
	}

	/**
	 * Reads students from xml file and save to appropriate group object
	 * 
	 * @param node
	 *            for which we read students
	 * @param group
	 *            in wich we save students
	 * @throws ServerException
	 *             if can't read students
	 */
	private void readStudents(Element node, Group group) throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call. Arguments: " + group);
		Element studentsNode = (Element) node.getElementsByTagName("students")
				.item(0);
		if (studentsNode == null) {
			ServerException ex = new ServerException(
					"Can't read student from group " + group.getNumber());
			log.error("Exception", ex);
			throw ex;
		}
		NodeList students = studentsNode.getElementsByTagName("student");
		try {
			for (int i = 0; i < students.getLength(); i++) {
				NamedNodeMap attributes = students.item(i).getAttributes();
				Student student = new Student();
				student.setEnrolled(attributes.getNamedItem("enrolled")
						.getTextContent());
				student.setFirstName(attributes.getNamedItem("firstname")
						.getTextContent());
				student.setGroupNumber(attributes.getNamedItem("groupnumber")
						.getTextContent());
				student.setId(new Integer(attributes.getNamedItem("id")
						.getTextContent()));
				student.setLastName(attributes.getNamedItem("lastname")
						.getTextContent());
				group.addStudent(student);
			}
		} catch (NumberFormatException e) {
			ServerException ex = new ServerException("Wrong id in database", e);
			log.error("Exception", ex);
			throw ex;
		} catch (DOMException e) {
			ServerException ex = new ServerException("Database reading error!",
					e);
			log.error("Exception", ex);
			throw ex;
		} catch (ParseException e) {
			ServerException ex = new ServerException("Database reading error!",
					e);
			log.error("Exception", ex);
			throw ex;
		}
	}

	/**
	 * Adds group to DOM tree
	 * 
	 * @param group
	 *            we save
	 * @throws ServerException
	 *             if can't save.
	 */
	private void addGroupToDB(Group group) throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call. Arguments: " + group);
		Element root = document.getDocumentElement();
		Element groupNode = document.createElement("group");
		groupNode.setAttribute("fakulty", group.getFakulty());
		groupNode.setAttribute("number", group.getNumber());
		Element studentsNode = document.createElement("students");
		groupNode.appendChild(studentsNode);
		root.appendChild(groupNode);
	}

	/**
	 * Adds student to DOM tree
	 * 
	 * @param student
	 *            to add
	 * @throws ServerException
	 *             if can't add
	 */
	private void addStudentToDB(Student student) throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call. Arguments: " + student);
		NodeList groups = document.getElementsByTagName("group");
		Element group = null;
		Element students = null;
		for (int i = 0; i < groups.getLength(); i++) {
			if (groups.item(i).getAttributes().getNamedItem("number")
					.getNodeValue().equals(student.getGroupNumber())) {
				group = (Element) groups.item(i);
				students = (Element) group.getElementsByTagName("students")
						.item(0);
				break;
			}
		}
		if (group == null) {
			ServerException ex = new ServerException("Group with number "
					+ student.getGroupNumber() + " does not exist");
			log.error("Exception", ex);
			throw ex;
		} else if (students == null) {
			ServerException ex = new ServerException(
					"Can not add student to group with number "
							+ student.getGroupNumber());
			log.error("Exception", ex);
			throw ex;
		}
		Element studentNode = document.createElement("student");
		studentNode.setAttribute("id", new Integer(student.getId()).toString());
		studentNode.setAttribute("firstname", student.getFirstName());
		studentNode.setAttribute("lastname", student.getLastName());
		studentNode.setAttribute("groupnumber", student.getGroupNumber());
		studentNode.setAttribute("enrolled", student.getEnrolled());
		group.appendChild(students);
		students.appendChild(studentNode);
	}

	/**
	 * Get node which associated with specified number
	 * 
	 * @param groups
	 *            NodelList of groups
	 * @param groupNumber
	 *            number of group
	 * @return Element, associated with specified number or null if group with
	 *         specified number does not exist
	 */
	private Element getGroupNodeByNumber(NodeList groups, String groupNumber) {
		if (log.isDebugEnabled())
			log.debug("Method call");
		for (int i = 0; i < groups.getLength(); i++) {
			if (groups.item(i).getAttributes().getNamedItem("number")
					.getNodeValue().equals(groupNumber)) {
				return (Element) groups.item(i);
			}
		}
		return null;
	}

	/**
	 * Removes the student from DOM tree.
	 * 
	 * @param id
	 *            the id
	 * @throws ServerException
	 *             if a student with specified id does not exist
	 */
	private void removeStudentFromDB(Student student) throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call. Arguments: " + student);
		try {
			NodeList groups = document.getElementsByTagName("group");
			Element group = getGroupNodeByNumber(groups,
					student.getGroupNumber());
			if (group == null) {
				ServerException ex = new ServerException(
						"Can not remove student from DB");
				log.error("Exception", ex);
				throw ex;
			}
			Element studentsNode = (Element) group.getElementsByTagName(
					"students").item(0);
			if (studentsNode == null) {
				ServerException ex = new ServerException("Can't remove student");
				log.error("Exception", ex);
				throw ex;
			}
			NodeList students = studentsNode.getChildNodes();
			for (int i = 0; i < students.getLength(); i++) {
				if (students.item(i).getAttributes() != null) {
					if (students.item(i).getAttributes().getNamedItem("id")
							.getNodeValue()
							.equals(new Integer(student.getId()).toString())) {
						studentsNode.removeChild(students.item(i));
						break;
					}
				}
			}
		} catch (DOMException e) {
			ServerException ex = new ServerException("Can't remove student "
					+ student + " from DB", e);
			log.error(ex);
			throw ex;
		}
	}

	/**
	 * Removes group from DOM tree
	 * 
	 * @param group
	 *            to remove
	 * @throws ServerException
	 *             if can not remove group or group does not exist
	 */
	private void removeGroupFromDB(Group group) throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call. Arguments: " + group);
		try {
			NodeList groups = document.getElementsByTagName("group");
			Element groupNode = getGroupNodeByNumber(groups, group.getNumber());
			if (groupNode == null) {
				ServerException ex = new ServerException(
						"Can not remove group from DB. Group with number "
								+ group.getNumber() + " does not exist");
				log.error("Exception", ex);
				throw ex;
			}
			Element root = document.getDocumentElement();
			root.removeChild(groupNode);
		} catch (DOMException e) {
			ServerException ex = new ServerException(
					"Can't remove this group!", e);
			log.error("Exception", ex);
			throw ex;
		}
	}

	/**
	 * Updates group in DOM tree
	 * 
	 * @param group
	 *            to update
	 * @throws ServerException
	 *             if can't update group
	 */
	private void updateGroupInDB(Group group) throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call. Arguments: " + group);
		NodeList groups = document.getElementsByTagName("group");
		Element groupNode = getGroupNodeByNumber(groups, group.getNumber());
		if (groupNode == null) {
			ServerException ex = new ServerException(
					"Can not update group in DB");
			log.error("Exception", ex);
			throw ex;
		}
		groupNode.setAttribute("fakulty", group.getFakulty());
	}

	/**
	 * Updates student in DOM tree
	 * 
	 * @param student
	 *            to update
	 * @throws ServerException
	 *             if can't update
	 */
	private void updateStudentInDB(Student student) throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call. Arguments: " + student);
		NodeList groups = document.getElementsByTagName("group");
		Element group = getGroupNodeByNumber(groups, student.getGroupNumber());
		if (group == null) {
			ServerException ex = new ServerException(
					"Can not update student in DB");
			log.error("Exception", ex);
			throw ex;
		}
		Element studentsNode = (Element) group.getElementsByTagName("students")
				.item(0);
		if (studentsNode == null) {
			ServerException ex = new ServerException(
					"Can not update student in DB");
			log.error("Exception", ex);
			throw ex;
		}
		NodeList students = studentsNode.getChildNodes();
		for (int i = 0; i < students.getLength(); i++) {
			if (students.item(i).getAttributes() != null) {
				NamedNodeMap attrs = students.item(i).getAttributes();
				if (attrs.getNamedItem("id").getNodeValue()
						.equals(new Integer(student.getId()).toString())) {
					Element studentNode = (Element) students.item(i);
					studentNode.setAttribute("enrolled", student.getEnrolled());
					studentNode.setAttribute("firstname",
							student.getFirstName());
					studentNode.setAttribute("groupnumber",
							student.getGroupNumber());
					studentNode.setAttribute("id",
							new Integer(student.getId()).toString());
					studentNode.setAttribute("lastname", student.getLastName());
				}
			}
		}
	}

	/**
	 * Check new data for changes and changes DOM tree if necessary, deletes old
	 * groups. Than saves DOM tree to xml file
	 * 
	 * @throws ServerException
	 *             can't change DOM tree
	 */
	public void upLoadData() throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call");
		for (Group oldGroup : groupsLocalCopy) {
			int i = 0;
			for (Group newGroup : groups) {
				if (oldGroup.equals(newGroup)) {
					checkStudents(newGroup, oldGroup);
					break;
				}
				if (!oldGroup.equals(newGroup)
						&& oldGroup.getNumber().equals(newGroup.getNumber())) {
					updateGroupInDB(newGroup);
					checkStudents(newGroup, oldGroup);
					break;
				}
				i++;
			}
			if (i == groups.size())
				removeGroupFromDB(oldGroup);
		}
		for (Group newGroup : groups) {
			checkGroupForNew(newGroup);
		}
		saveXML();
		setGroupsLocalCopy(groups);
	}

	/**
	 * Checks if the group is new. If yes - add group to DOM
	 * 
	 * @param newGroup
	 *            to check
	 * @throws ServerException
	 *             if can't add group to DOM
	 */
	private void checkGroupForNew(Group newGroup) throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call. Arguments: " + newGroup);
		for (Group oldGroup : groupsLocalCopy) {
			if (oldGroup.getNumber().equals(newGroup.getNumber()))
				return;
		}
		addGroupToDB(newGroup);
		for (Student newStudent : newGroup.getStudents()) {
			addStudentToDB(newStudent);
		}
	}

	/**
	 * Check students for changes and changes DOM tree if necessary. Deletes old
	 * students
	 * 
	 * @param newGroup
	 *            group in new data
	 * @param oldGroup
	 *            group in old data
	 * @throws ServerException
	 *             if can not change DOM tree
	 */
	private void checkStudents(Group newGroup, Group oldGroup)
			throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call. Arguments: " + newGroup + " " + oldGroup);
		for (Student oldStudent : oldGroup.getStudents()) {
			int j = 0;
			for (Student newStudent : newGroup.getStudents()) {
				if (!oldStudent.equals(newStudent)
						&& oldStudent.getId() == newStudent.getId()) {
					updateStudentInDB(newStudent);
					break;
				}
				if (oldStudent.equals(newStudent)) {
					break;
				}
				j++;
			}
			if (j == newGroup.getStudents().size()) {
				removeStudentFromDB(oldStudent);
			}
		}
		for (Student newStudent : newGroup.getStudents()) {
			checkStudentForNew(newStudent, oldGroup);
		}
	}

	/**
	 * Checks if the student is new. If yes - add student to DOM
	 * 
	 * @param newStudent
	 *            to check
	 * @param oldGroup
	 *            with students from old group we compare specified student
	 * @throws ServerException
	 *             if can't add group to DOM
	 */
	private void checkStudentForNew(Student newStudent, Group oldGroup)
			throws ServerException {
		if (log.isDebugEnabled())
			log.debug("Method call. Arguments: " + newStudent + " " + oldGroup);
		for (Student oldSt : oldGroup.getStudents()) {
			if (oldSt.getId() == newStudent.getId())
				return;
		}
		addStudentToDB(newStudent);
	}

}
