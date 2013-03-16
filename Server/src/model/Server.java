package model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * The Class Server, using XML as DB.
 */
public class Server {
    
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
     * @param dtdPath the new dtd path
     */
    public void setDtdPath(String dtdPath) {
        this.dtdPath = dtdPath;
    }

    /**
     * Instantiates a new server.
     *
     * @param xmlPath the xml path
     * @param dtdPath the dtd path
     * @throws ServerException 
     */
    public Server (String xmlPath, String dtdPath) throws ServerException {
        setXmlPath(xmlPath);
        setDtdPath(dtdPath);
        readDocument();
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
     * @param xmlPath the new xml path
     */
    public void setXmlPath(String xmlPath) {
        this.xmlPath = xmlPath;
        
    }

    /**
     * Read document.
     *
     * @throws ServerException the server exception
     */
    public void readDocument () throws ServerException {        
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(true);
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            setDocument(docBuilder.parse(new File(xmlPath)));
        }  catch (ParserConfigurationException e) {
            throw new ServerException("Can not read xml-file",e);
        } catch (IOException e) {
            throw new ServerException("Can not read xml-file",e);
        } catch (SAXException e) {
            throw new ServerException("Can not read xml-file",e);
        }       
    }
    
    /**
     * Return the list of students for specified group.
     *
     * @param group the group
     * @return the students
     * @throws ServerException 
     */
    public List<Student> getStudents (Group group) throws ServerException {        
        return group.getStudents();
    }
    
    /**
     * Gets the groups.
     *
     * @return the groups
     * @throws ServerException the server exception
     */
    public List<Group> getGroups () throws ServerException {
        List<Group> groups = new ArrayList<Group>();
        Element root = document.getDocumentElement();
        NodeList groupsNodes = root.getElementsByTagName("group");
        for(int i = 0; i < groupsNodes.getLength(); i++) {
            groups.add(new Group(groupsNodes.item(i), document));
        }
        return groups;
    }
    
    /**
     * Removes the student from DB.
     *
     * @param id the id
     * @throws ServerException the server exception
     */
    public void removeStudent (int id) throws ServerException {
        List<Group> groups = getGroups();
        for(Group g : groups) {
            if(g.containsStudent(id)) {
                g.removeStudent(id);
                saveXML("UTF-8");
                return;
            }
        }
        throw new ServerException("Student with ID " + id + " does not exist");
    }
    
    /**
     * Removes the group from DB.
     *
     * @param groupNnumber the group name
     * @throws ServerException the server exception
     */
    public void removeGroup (String groupNnumber) throws ServerException {
        try{
            NodeList groups = document.getElementsByTagName("group");
            for(int i = 0; i < groups.getLength(); i++) {
                if (groups.item(i).getAttributes().getNamedItem("number").getNodeValue().equals(groupNnumber)){
                    Element root = document.getDocumentElement();
                    root.removeChild(groups.item(i));
                    saveXML("UTF-8");
                    return;
                }
            }
            throw new ServerException("Group with name " + groupNnumber + " does not exist");
        } catch(DOMException e) {
            throw new ServerException("Can't remove this group!", e);
        }
    }
    
    /**
     * Adds the student, check ID for uniqueness.
     *
     * @param student the student
     * @throws ServerException the server exception
     */
    public void addStudent(Student student) throws ServerException {
        List<Group> groups = getGroups();
        for(Group g : groups) {
            if(g.containsStudent(student.getId())) 
                throw new ServerException("Can't add the student! Student with ID " + student.getId() + " is already exist!");
        }
        for(Group g : groups) {
            if(g.getNumber().equals(student.getGroupNumber())) {                
                g.addStudent(student);
                saveXML("UTF-8");
                return;
            }
        }     
        throw new ServerException ("Error! Can not add student, because group with name '" + student.getGroupNumber() + "' does not exist");
    }
    
    /**
     * Adds the group, check group number for uniqueness.
     *
     * @param group the group
     * @throws ServerException the server exception
     */
    public void addGroup (Group group) throws ServerException {
        List <Group> groups = getGroups();
        for(Group g : groups) {
            if (g.getNumber().equals(group.getNumber()))
                throw new ServerException("A group with number " + g.getNumber() + "is alredy exist");
        }
        group.setDocument(document);
        group.addToDocument();
        saveXML("UTF-8");
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
     * @param document the new document
     * @throws ServerException 
     */
    public void setDocument(Document document) throws ServerException {
    	this.document = document;
    }
    
    /**
     * Save changes to DB.
     *
     * @param charSet the char set
     * @throws ServerException the server exception
     */
    private void saveXML(String charSet) throws ServerException {
        try {
            Writer target = new OutputStreamWriter(new FileOutputStream(xmlPath), charSet);
            Source source = new DOMSource(document);
            StreamResult dest = new StreamResult(target);
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "groups.dtd");
            t.setOutputProperty(OutputKeys.ENCODING, charSet);
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(source, dest);
            target.flush();
            target.close();
        }
        catch (Exception e) {
            throw new ServerException("Can't save xml!");
        }
    }
    
}
