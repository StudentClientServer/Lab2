package model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The Class Student.
 */
public class Student {

    /** The id. */
    private int id;

    /** The first name. */
    private String firstName;

    /** The last name. */
    private String lastName;

    /** The group number. */
    private String groupNumber;

    /** The enrolled date. */
    private String enrolled; // зачислен

    @Override
    public String toString() {
	return "Student [id=" + id + ", firstName=" + firstName + ", lastName="
		+ lastName + ", groupNumber=" + groupNumber + ", enrolled="
		+ enrolled + "]";
    }

    /** The Constant format. */
    private static final DateFormat format = new SimpleDateFormat("dd.MM.yyyy");

    /**
     * Gets the last name.
     * 
     * @return the last name
     */
    public String getLastName() {
	return lastName;
    }

    /**
     * Sets the last name.
     * 
     * @param lastName
     *            the new last name
     */
    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public int getId() {
	return id;
    }

    /**
     * Sets the id.
     * 
     * @param id
     *            the new id
     */
    public void setId(int id) {
	this.id = id;
    }

    /**
     * Gets the first name.
     * 
     * @return the first name
     */
    public String getFirstName() {
	return firstName;
    }

    /**
     * Sets the first name.
     * 
     * @param firstName
     *            the new first name
     */
    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    /**
     * Gets the group number.
     * 
     * @return the group number
     */
    public String getGroupNumber() {
	return groupNumber;
    }

    /**
     * Sets the group number.
     * 
     * @param groupNumber
     *            the new group number
     */
    public void setGroupNumber(String groupNumber) {
	this.groupNumber = groupNumber;
    }

    /**
     * Gets the enrolled date.
     * 
     * @return the enrolled
     */
    public String getEnrolled() {
	return enrolled;
    }

    /**
     * Sets the enrolled date.
     * 
     * @param enrolled
     *            the new enrolled
     * @throws ParseException 
     */
    public void setEnrolled(String enrolled) throws ParseException {
        format.parse(enrolled);
        this.enrolled = enrolled;
    }

    /**
     * Instantiates a new student.
     * 
     * @param node
     *            the node
     * @throws ServerException
     *             if can't read the xml file or data format is wrong
     */
    public Student(Node node) throws ServerException {
	try {
	    setId(Integer.parseInt(node.getAttributes().getNamedItem("id")
		    .getNodeValue()));
	    setFirstName(node.getAttributes().getNamedItem("firstname")
		    .getNodeValue());
	    setLastName(node.getAttributes().getNamedItem("lastname")
		    .getNodeValue());
	    setGroupNumber(node.getAttributes().getNamedItem("groupnumber")
		    .getNodeValue());
	    setEnrolled(node.getAttributes()
		    .getNamedItem("enrolled").getNodeValue());
	} catch (NumberFormatException e) {
	    throw new ServerException(
		    "Can not create a student! Something wrong with id!", e);
	} catch (DOMException e) {
	    throw new ServerException(
		    "Can not create a student! DOMException - something wrong with XML-file!",
		    e);
	} catch (ParseException e) {
	    throw new ServerException(
		    "Can not create a student! Something wrong with date of enroll at the University, Date fromat must be dd.MM.yyyy!",
		    e);
	}
    }

    /**
     * Creates the student node.
     *
     * @param document the document
     * @param group that contains this student
     */
    public void createNode(Document document, Element group) {
	Element studentNode = document.createElement("student");
	studentNode.setAttribute("id", new Integer(getId()).toString());
	studentNode.setAttribute("firstname", getFirstName());
	studentNode.setAttribute("lastname", getLastName());
	studentNode.setAttribute("groupnumber", getGroupNumber());
	studentNode.setAttribute("enrolled", getEnrolled());
	group.appendChild(studentNode);
    }

    /**
     * Instantiates a new student.
     *
     * @param id the id
     * @param firstName the first name
     * @param lastName the last name
     * @param groupNumber the group number
     * @param enrolled the enrolled date
     * @throws ServerException 
     */
    public Student(int id, String firstName, String lastName,
	    String groupNumber, String enrolled) throws ServerException {
	setId(id);
	setFirstName(firstName);
	setLastName(lastName);
	setGroupNumber(groupNumber);
	try {
        setEnrolled(enrolled);
    } catch (ParseException e) {
        throw new ServerException("Wrong date format!");
    }
    }

}
