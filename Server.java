import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


public class Server {
    
    private Document document = null;
    private String xmlPath;
    private String dtdPath;
    
    public String getDtdPath() {
        return dtdPath;
    }

    public void setDtdPath(String dtdPath) {
        this.dtdPath = dtdPath;
    }

    public Server (String xmlPath, String dtdPath) {
        setXmlPath(xmlPath);
        setDtdPath(dtdPath);
    }
    
    public String getXmlPath() {
        return xmlPath;
    }

    public void setXmlPath(String xmlPath) {
        this.xmlPath = xmlPath;
        
    }

    public void readDocument () throws ServerException {        
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(true);
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            docBuilder.setErrorHandler(new org.xml.sax.ErrorHandler() {

                public void fatalError(SAXParseException exception) throws SAXException {
                    System.out.println("Parsing error:  "+exception.getMessage());
                    System.out.println("Cannot continue.");
                    System.exit(1);
                }

                public void warning(SAXParseException err) throws SAXParseException {
                    System.out.println(err.getMessage());
                    System.exit(3);
                }

                public void error(SAXParseException e) throws SAXParseException {
                    System.out.println("Error at " + e.getLineNumber() + " line.");
                    System.out.println(e.getMessage());
                    System.exit(2);
                }
            }); 
            setDocument(docBuilder.parse(new File(xmlPath)));
        }  catch (ParserConfigurationException e) {
            throw new ServerException("Can not read xml-file",e);
        } catch (IOException e) {
            throw new ServerException("Can not read xml-file",e);
        } catch (SAXException e) {
            throw new ServerException("Can not read xml-file",e);
        }       
    }
    
    public List<Student> getStudents (Group group) {        
        return group.getStudents();
    }
    
    public Group getStudentsGroup(Student student) throws ServerException {
        Group group = null;
        ArrayList<Group> groups = (ArrayList<Group>) getGroups();
        for(Group g : groups) {
            if(g.getNumber().equals(student.getGroupNumber()) && g.getStudents().contains(student)) {
                return g;
            }
        }
        return group;
    }
    
    public List<Group> getGroups () throws ServerException {
        ArrayList<Group> groups = new ArrayList<Group>();
        Element root = document.getDocumentElement();
        NodeList groupsNodes = root.getElementsByTagName("group");
        for(int i = 0; i < groupsNodes.getLength(); i++) {
            groups.add(new Group(groupsNodes.item(i)));
        }
        return groups;
    }
    
    public void remove (int ID) throws ServerException {
        try{
            String id = new Integer(ID).toString();
            document.removeChild(document.getElementById(id));
        } catch(DOMException e) {
            throw new ServerException("Can't remove this element!", e);
        }
    }
    
    /**
     * 
     * @param ids
     * @param hashs
     * @return can return keys "changed", "deleted", "added"
     */
    public Map<String, Object> isUpdated (List<Integer> ids, List<Integer> hashs) throws ServerException {
        return null;        
    }
    
    public void addStudent (Student student) throws ServerException {
        String studentsGroupID = new Integer(getStudentsGroup(student).getId()).toString();
        Element group = document.getElementById(studentsGroupID);
        if(group == null)
            throw new ServerException(("There is no group! Please, create group first"), new NullPointerException());        
        Element studNode = document.createElement("student");
        String id = new Integer(student.getId()).toString();
        studNode.setAttribute("id", id);
        studNode.setAttribute("fio", student.getFio());
        studNode.setAttribute("groupnumber", student.getGroupNumber());
        group.appendChild(studNode);
        saveXML("UTF-8");
    }
    
    public void addGroup (Group group) throws ServerException {
        Element root = document.getDocumentElement();
        if(group == null)
            throw new ServerException("There is no root element!", new NullPointerException());
        Element groupNode = document.createElement("group");
        groupNode.setAttribute("fakulty", group.getFakulty());
        groupNode.setAttribute("number", group.getNumber());
        String id = new Integer(group.getId()).toString();
        groupNode.setAttribute("id", id);
        groupNode.setTextContent(" ");
        root.appendChild(groupNode);
        saveXML("UTF-8");
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
    
    /*public static void main (String[] args) throws ServerException {
        Server server = new Server("groups.xml","groups.dtd");
        server.readDocument();
        System.out.println("xml readed succesfully");
        ArrayList<Group> gr = (ArrayList<Group>) server.getGroups();
        for(Group g : gr) {
           System.out.println(g.getFakulty());
           System.out.println(g.getNumber());
           System.out.println(g.getId());
           ArrayList<Student> st = (ArrayList<Student>) g.getStudents();
           for(Student s : st) {
               System.out.println(s.getFio());
               System.out.println(s.getGroupNumber());
               System.out.println(s.getId());
               System.out.println(s.getEnrolled());
           }
        }
        server.addGroup(new Group("Med", "pata01", 4, null));
    }*/
    
    private void saveXML(String charSet) {
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
            throw new ServerException("Can't save the XML!", e);
        }
    }
    
}
