package model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class Group {
    
    private String fakulty;
    private String number;  
    private Element group;
    private Document document;

    public Group(String fakulty, String number) {
        setFakulty(fakulty);
        setNumber(number);       
    }
    
    public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Group (Node node, Document document) throws ServerException {
        try {
        	setDocument(document);
            group = (Element) node;
            setFakulty(group.getAttribute("fakulty"));
            setNumber(group.getAttribute("number"));         
        } catch(NumberFormatException e){
            throw new ServerException("Can not create a group! Something wrong with id!",e);
        }
    }
    
    public void addStudent (Student student) {
    	student.createNode(document, group);
    }
    
    public void removeStudent (int id) throws ServerException {
                NodeList studentsNodes = group.getElementsByTagName("student");
                for(int i = 0; i < studentsNodes.getLength(); i++) {
                    if(studentsNodes.item(i).getAttributes().getNamedItem("id").getNodeValue().equals(new Integer(id).toString())) {                                                                     
                        if(removeStudentFromDocument(studentsNodes.item(i)))
                            return;
                    }                        
                }                             
        throw new ServerException("There is no student with id = " + id);
    }
    
    private boolean removeStudentFromDocument(Node student) {
        Element root = document.getDocumentElement();
        NodeList groups = root.getElementsByTagName("group");
        for(int i = 0; i < groups.getLength(); i++) {
            if(groups.item(i).getAttributes().getNamedItem("number").getNodeValue().equals(getNumber())) {
                groups.item(i).removeChild(student);
                return true;
            }
        }
        return false;
    }
    
    public void addToDocument() {
        Element root = document.getDocumentElement();
        Element groupNode = document.createElement("group");
        groupNode.setAttribute("fakulty",getFakulty());
        groupNode.setAttribute("number", getNumber());
        root.appendChild(groupNode);
    }       
    
    public String getFakulty() {
        return fakulty;
    }
    public void setFakulty(String fakulty) {
        this.fakulty = fakulty;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    
    public boolean containsStudent (int id) throws ServerException {
        List <Student> students = getStudents();
        for(Student student : students) {
        	if(student.getId() == id)
        		return true;
        }
        return false;
    }

	public List<Student> getStudents() throws ServerException {
		List<Student> students = new ArrayList <Student>();
		NodeList studentsNodes = group.getElementsByTagName("student");
        	for(int i = 0; i < studentsNodes.getLength(); i++) 
        		students.add(new Student (studentsNodes.item(i)));
        return students;
	}
      
}
