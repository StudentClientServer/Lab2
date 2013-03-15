package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class Group implements Serializable {
    
    private static final long serialVersionUID = 2843451388903919498L;
    
    private String fakulty;
    private String number;  
    private List<Student> students = new ArrayList<Student>();
    private Element group;
    
    public Group(String fakulty, String number, int id,
            ArrayList<Student> students) {
        setFakulty(fakulty);
        setNumber(number);    
        setStudents(students);
    }

    public Group(String fakulty, String number, int id) {
        setFakulty(fakulty);
        setNumber(number);       
    }
    
    public Group (Node node) throws ServerException {
        try {
            group = (Element) node;
            setFakulty(group.getAttribute("fakulty"));
            setNumber(group.getAttribute("ID"));
            NodeList students = group.getElementsByTagName("student");
            for(int i = 0; i < students.getLength(); i++) {
                this.students.add(new Student (students.item(i)));
            }
            
        } catch(NumberFormatException e){
            throw new ServerException("Can not create a group! Something wrong with id!",e);
        }
    }
    
    public void addStudent (Student student, Document document) {
        students.add(student);
        Element studentNode = document.createElement("student");
        studentNode.setAttribute("ID", new Integer(student.getId()).toString());
        studentNode.setAttribute("fio", student.getFio());
        studentNode.setAttribute("groupnumber", student.getGroupNumber());
        studentNode.setAttribute("enrolled", student.getEnrolled());                                  
        group.appendChild(studentNode);
    }
    
    public void removeStudent (int id, Document document) throws ServerException {
        for(Student s : students) {
            if(s.getId() == id) {
                students.remove(s);
                NodeList studentsNodes = group.getElementsByTagName("student");
                for(int i = 0; i < studentsNodes.getLength(); i++) {
                    if(studentsNodes.item(i).getAttributes().getNamedItem("ID").getNodeValue().equals(new Integer(id).toString())) {                        
                        System.out.println(studentsNodes.item(i).getAttributes().getNamedItem("ID").getNodeValue());                                                
                        if(removeStudentFromDocument(studentsNodes.item(i), document))
                            return;
                    }                        
                }                
            }               
        }
        throw new ServerException("There is no student with id = " + id);
    }
    
    private boolean removeStudentFromDocument(Node student, Document document) {
        Element root = document.getDocumentElement();
        NodeList groups = root.getElementsByTagName("group");
        for(int i = 0; i < groups.getLength(); i++) {
            if(groups.item(i).getAttributes().getNamedItem("ID").getNodeValue().equals(getNumber())) {
                groups.item(i).removeChild(student);
                return true;
            }
        }
        return false;
    }
    
    public void addToDocument(Document document) {
        Element root = document.getDocumentElement();
        Element groupNode = document.createElement("group");
        groupNode.setAttribute("fakulty",getFakulty());
        groupNode.setAttribute("ID", getNumber());
        groupNode.setTextContent(" ");
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
    
    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }
    
    public List<Student> getStudents() {
        return students;
    }
    
    public boolean containsStudent (int id) {
        for(Student s : students) {
            if(s.getId() == id)
                return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result;
        result = prime * result + ((fakulty == null) ? 0 : fakulty.hashCode());
        result = prime * result + ((number == null) ? 0 : number.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Group other = (Group) obj;
        if (fakulty == null) {
            if (other.fakulty != null)
                return false;
        } else if (!fakulty.equals(other.fakulty))
            return false;
        if (number == null) {
            if (other.number != null)
                return false;
        } else if (!number.equals(other.number))
            return false;
        return true;
    }
    
    
}
