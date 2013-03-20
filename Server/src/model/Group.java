package model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The Class Group.
 */
public class Group {

    /** The faculty. */
    private String fakulty;

    /** The number of group, unique. */
    private String number;

    /** The xml element, associated with group. */
    private Element group;

    /** The xml document, that contains this group. */
    private Document document;

    /**
     * Instantiates a new group.
     * 
     * @param fakulty
     *            the faculty
     * @param number
     *            the number
     */
    public Group(String fakulty, String number) {
	setFakulty(fakulty);
	setNumber(number);
    }

    /**
     * Gets the document.
     * 
     * @return the document
     */
    public Document getDocument() {
	return document;
    }

    /**
     * Sets the document.
     * 
     * @param document
     *            the new document
     */
    public void setDocument(Document document) {
	this.document = document;
    }

    /**
     * Instantiates a new group.
     * 
     * @param node
     *            the node
     * @param document
     *            the document
     * @throws ServerException
     *             the server exception
     */
    public Group(Node node, Document document) throws ServerException {
	try {
	    setDocument(document);
	    group = (Element) node;
	    setFakulty(group.getAttribute("fakulty"));
	    setNumber(group.getAttribute("number"));
	} catch (NumberFormatException e) {
	    throw new ServerException(
		    "Can not create a group! Something wrong with id!", e);
	}
    }

    /**
     * Adds the student.
     * 
     * @param student
     *            the student
     */
    public void addStudent(Student student) {
	student.createNode(document, group);
    }

    /**
     * Removes the student by id.
     * 
     * @param id
     *            the id
     * @throws ServerException
     *             the server exception
     */
    public void removeStudent(int id) throws ServerException {
	NodeList studentsNodes = group.getElementsByTagName("student");
	for (int i = 0; i < studentsNodes.getLength(); i++) {
	    if (studentsNodes.item(i).getAttributes().getNamedItem("id")
		    .getNodeValue().equals(new Integer(id).toString())) {
		if (removeStudentFromDocument(studentsNodes.item(i)))
		    return;
	    }
	}
	throw new ServerException("There is no student with id = " + id);
    }

    /**
     * Removes the student from xml-document.
     * 
     * @param student
     *            the student
     * @return true, if successful
     */
    private boolean removeStudentFromDocument(Node student) {
	Element root = document.getDocumentElement();
	NodeList groups = root.getElementsByTagName("group");
	for (int i = 0; i < groups.getLength(); i++) {
	    if (groups.item(i).getAttributes().getNamedItem("number")
		    .getNodeValue().equals(getNumber())) {
		groups.item(i).removeChild(student);
		return true;
	    }
	}
	return false;
    }

    /**
     * Adds the group to document.
     */
    public void addToDocument() {
	Element root = document.getDocumentElement();
	Element groupNode = document.createElement("group");
	groupNode.setAttribute("fakulty", getFakulty());
	groupNode.setAttribute("number", getNumber());
	root.appendChild(groupNode);
    }

    /**
     * Gets the faculty.
     * 
     * @return the faculty
     */
    public String getFakulty() {
	return fakulty;
    }

    /**
     * Sets the fakulty.
     * 
     * @param fakulty
     *            the new faculty
     */
    public void setFakulty(String fakulty) {
	this.fakulty = fakulty;
    }

    /**
     * Gets the number.
     * 
     * @return the number
     */
    public String getNumber() {
	return number;
    }

    /**
     * Sets the number.
     * 
     * @param number
     *            the new number
     */
    public void setNumber(String number) {
	this.number = number;
    }

    /**
     * Checks if the group contains a student with specified id.
     * 
     * @param id
     *            the id
     * @return true, if successful
     * @throws ServerException
     *             the server exception
     */
    public boolean containsStudent(int id) throws ServerException {
	List<Student> students = getStudents();
	for (Student student : students) {
	    if (student.getId() == id)
		return true;
	}
	return false;
    }

    /**
     * Gets the List of students.
     * 
     * @return the students
     * @throws ServerException
     *             the server exception
     */
    public List<Student> getStudents() throws ServerException {
	List<Student> students = new ArrayList<Student>();
	NodeList studentsNodes = group.getElementsByTagName("student");
	for (int i = 0; i < studentsNodes.getLength(); i++)
	    students.add(new Student(studentsNodes.item(i)));
	return students;
    }

}
