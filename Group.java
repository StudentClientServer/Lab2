import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class Group implements Serializable {
    
    private static final long serialVersionUID = 2843451388903919498L;
    
    private String fakulty;
    private String number;
    private int id;    
    private ArrayList<Student> students = new ArrayList<Student>();
    
    public Group(String fakulty, String number, int id,
            ArrayList<Student> students) {
        setFakulty(fakulty);
        setNumber(number);
        setId(id);        
    }

    public Group (Node node) throws ServerException {
        try {
            Element group = (Element) node;
            setId(Integer.parseInt(group.getAttribute(("id"))));
            setFakulty(group.getAttribute("fakulty"));
            setNumber(group.getAttribute("number"));
            NodeList students = group.getElementsByTagName("student");
            for(int i = 0; i < students.getLength(); i++) {
                this.students.add(new Student (students.item(i)));
            }
            
        } catch(NumberFormatException e){
            throw new ServerException("Can not create a group! Something wrong with id!",e);
        }
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    
    public List<Student> getStudents() {
        return students;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
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
        if (id != other.id)
            return false;
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
