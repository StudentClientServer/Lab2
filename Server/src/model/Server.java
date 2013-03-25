package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * The Class Server, using XML as DB.
 */
public class Server implements ServerModel {

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
    public String getDtdPath() {
        return dtdPath;
    }

    /**
     * Sets the dtd path.
     * 
     * @param dtdPath
     *            the new dtd path
     */
    public void setDtdPath(String dtdPath) {
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
    public Server(String xmlPath, String dtdPath) throws ServerException {
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
    }

    /**
     * Gets the xml path.
     * 
     * @return the xml path
     */
    public String getXmlPath() {
        return xmlPath;
    }

    /**
     * Sets the xml path.
     * 
     * @param xmlPath
     *            the new xml path
     */
    public void setXmlPath(String xmlPath) {
        this.xmlPath = xmlPath;

    }

    /**
     * Read document using xml path.
     * 
     * @throws ServerException
     *             if can't not read xml file
     */
    public void readDocument() throws ServerException {
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
        } catch (ParserConfigurationException e) {
            ServerException ex = new ServerException("Can not read xml-file", e);
            writeToLog("In method readDocument() ", ex);
            throw ex;
        } catch (IOException e) {
            ServerException ex = new ServerException("Can not read xml-file", e);
            writeToLog("In method readDocument() ", ex);
            throw ex;
        } catch (SAXException e) {
            ServerException ex = new ServerException("Can not read xml-file", e);
            writeToLog("In method readDocument() ", ex);
            throw ex;
        }
    }

    /**
     * Return the list of students for specified group.
     * 
     * @param group
     *            the group
     * @return the students
     * @throws ServerException
     *             if can't get list of students for specified group
     */
    public List<Student> getStudents(Group group) throws ServerException {
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
        List<Group> groups = new ArrayList<Group>();
        Element root = document.getDocumentElement();
        NodeList groupsNodes = root.getElementsByTagName("group");
        for (int i = 0; i < groupsNodes.getLength(); i++) {
            groups.add(new Group(groupsNodes.item(i), document));
        }
        return groups;
    }
    
    public Group getGroup(String number) throws ServerException {
        List<Group> groups = getGroups();
        for(Group gr : groups) {
            if (gr.getNumber().equals(number))
                return gr;
        }
        throw new ServerException("There is no group with number " + number);
    }

    /**
     * Removes the student from DB.
     * 
     * @param id
     *            the id
     * @throws ServerException
     *             if a student with specified id does not exist
     */
    public void removeStudent(Integer id) throws ServerException {
        List<Group> groups = getGroups();
        for (Group g : groups) {
            if (g.containsStudent(id)) {
                g.removeStudent(id);
                saveXML("UTF-8");
                writeToLog("Removed student with id ", new Integer(id));
                return;
            }
        }
        ServerException ex = new ServerException("Student with ID " + id
                + " does not exist");
        writeToLog("In method removeStudent(Integer id) ", ex);
        throw ex;
    }

    /**
     * Removes the group from DB.
     * 
     * @param groupNumber
     *            the group name
     * @throws ServerException
     *             if can't remove the group or group with specified number does
     *             not exist
     */
    public void removeGroup(String groupNumber) throws ServerException {
        try {
            NodeList groups = document.getElementsByTagName("group");
            for (int i = 0; i < groups.getLength(); i++) {
                if (groups.item(i).getAttributes().getNamedItem("number")
                        .getNodeValue().equals(groupNumber)) {
                    Element root = document.getDocumentElement();
                    root.removeChild(groups.item(i));
                    saveXML("UTF-8");
                    writeToLog("Removed group with number ", groupNumber);
                    return;
                }
            }
            ServerException ex = new ServerException("Group with name "
                    + groupNumber + " does not exist");
            writeToLog("In method removeGroup(String groupNumber) ", ex);
            throw ex;
        } catch (DOMException e) {
            ServerException ex = new ServerException(
                    "Can't remove this group!", e);
            writeToLog("In method removeGroup(String groupNumber) ", ex);
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
        List<Group> groups = getGroups();
        for (Group g : groups) {
            if (g.containsStudent(student.getId())) {
                ServerException ex = new ServerException(
                        "Can't add the student! Student with ID "
                                + student.getId() + " is already exist!");
                writeToLog("In method addStudent(Student student) ", ex);
                throw ex;
            }
        }
        for (Group g : groups) {
            if (g.getNumber().equals(student.getGroupNumber())) {
                g.addStudent(student);
                saveXML("UTF-8");
                writeToLog("Added student ", student);
                return;
            }
        }
        ServerException ex = new ServerException(
                "Error! Can not add student, because group with name '"
                        + student.getGroupNumber() + "' does not exist");
        writeToLog("In method addStudent(Student student) ", ex);
        throw ex;
    }

    /**
     * Adds the group, check group number for uniqueness.
     * 
     * @param group
     *            the group
     * @throws ServerException
     *             if group number is not unique
     */
    public void addGroup(Group group) throws ServerException {

        List<Group> groups = getGroups();
        for (Group g : groups) {
            if (g.getNumber().equals(group.getNumber())){
                ServerException ex = new ServerException(
                        "A group with number "
                                + g.getNumber() + "is alredy exist");
                writeToLog("In method addGroup(Group group) ", ex);
                throw ex;
            }
        }
        group.setDocument(document);
        group.addToDocument();
        saveXML("UTF-8");
        writeToLog("Added group ", group);
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
     * Gets the group by number.
     * 
     * @param number
     *            the number
     * @return the group by number
     * @throws ServerException
     *             when can not find a group with specified number.
     */
    public Group getGroupByNumber(String number) throws ServerException {
        List<Group> groups = getGroups();
        for (Group g : groups) {
            if (g.getNumber().equals(number))
                return g;
        }
        ServerException ex = new ServerException(
                "Can't find group with number " + number);
        writeToLog("In method getGroupByNumber(String number) ", ex);
        throw ex;
    }

    /**
     * Update group, change faculty.
     * 
     * @param group
     *            the group
     * @throws ServerException
     *             if can not remove or add group.
     */
    public void updateGroup(Group group) throws ServerException {
        try {
            List<Student> students = getGroupByNumber(group.getNumber())
                    .getStudents();
            removeGroup(group.getNumber());
            addGroup(group);
            for (Student s : students) {
                addStudent(s);
            }
            writeToLog("Update group, change faculty ", group);
        } catch (ServerException e) {
            ServerException ex = new ServerException(
                    "Can not update group. Maybe someone removed it.", e);
            writeToLog("In method updateGroup(Group group) ", ex);
            throw ex;
        }
    }

    /**
     * Update group, change group number.
     * 
     * @param group
     *            the group
     * @param newNumber
     *            number you want to set
     * @throws ServerException
     *             if can not remove or add group.
     */
    public void updateGroup(Group group, String newNumber)
            throws ServerException {
        try {
            List<Student> students = getGroupByNumber(group.getNumber())
                    .getStudents();
            removeGroup(group.getNumber());
            addGroup(new Group(group.getFakulty(), newNumber));
            for (Student s : students) {
                s.setGroupNumber(newNumber);
                addStudent(s);
            }
            writeToLog("Updated group, new number ", group);
        } catch (ServerException e) {
            ServerException ex = new ServerException(
                    "Can not update group. Maybe someone removed it or new number is already exist.",
                    e);
            writeToLog("In method updateGroup(Group group, String newNumber) ",
                    ex);
            throw ex;
        }
    }

    /**
     * Update student.
     * 
     * @param student
     *            the student you want to update.
     * @throws ServerException
     *             if can not remove or add student.
     */
    public void updateStudent(Student student) throws ServerException {
        try {
            getGroupByNumber(student.getGroupNumber()); // check if the
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
            writeToLog("In method updateStudent(Student student) ", ex);
            throw ex;
        }
    }

    /**
     * Save changes to DB.
     * 
     * @param charSet
     *            the char set
     * @throws ServerException
     *             if can not save data to file
     */
    private void saveXML(String charSet) throws ServerException {
        try {
            Writer target = new OutputStreamWriter(
                    new FileOutputStream(xmlPath), charSet);
            Source source = new DOMSource(document);
            StreamResult dest = new StreamResult(target);
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "groups.dtd");
            t.setOutputProperty(OutputKeys.ENCODING, charSet);
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(source, dest);
            target.flush();
            target.close();
        } catch (Exception e) {
            ServerException ex = new ServerException(
                    "Can't save xml!");
            writeToLog("In method saveXML(String charSet) ", ex);
            throw ex;
        }
    }

    /**
     * Write to log.
     * 
     * @param action
     *            what happened
     * @param object
     *            was changed
     */
    private void writeToLog(String action, Object object) {
        try {
            Writer out = new FileWriter("log.txt", true);
            out.write(new Date() + ". " + action + object + "\n");
            out.close();
        } catch (IOException e) {
            // If it is unable to log - just continue working
        }
    }
}
