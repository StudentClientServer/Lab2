package model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import exceptions.*;

/**
 * The Class Group.
 */
public class Group {
	
	/** The fakulty. */
	private String fakulty;	
    
    /** The number. */
    private String number;  
    
    /** The group. */
    private Element group;


    /**
     * Instantiates a new group.
     *
     * @param fakulty the fakulty
     * @param number the number
     */
    public Group(String fakulty, String number) {
        setFakulty(fakulty);
        setNumber(number);       
    }
	
	/**
	 * Gets the fakulty.
	 *
	 * @return the fakulty
	 */
	public String getFakulty() {
        return fakulty;
    }
    
    /**
     * Sets the fakulty.
     *
     * @param fakulty the new fakulty
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
     * @param number the new number
     */
    public void setNumber(String number) {
        this.number = number;
    }
    
    /**
     * Gets the students.
     *
     * @return the students
     * @throws Exception the exception
     */
    public List<Student> getStudents() throws Exception {
		List<Student> students = new ArrayList <Student>();
		NodeList studentsNodes = group.getElementsByTagName("student");
        	for(int i = 0; i < studentsNodes.getLength(); i++) 
        		students.add(new Student (studentsNodes.item(i)));
        return students;
	}
    
    /**
     * Contains student.
     *
     * @param id the id
     * @return true, if successful
     * @throws Exception the exception
     */
    public boolean containsStudent (int id) throws Exception {
        List <Student> students = getStudents();
        for(Student student : students) {
        	if(student.getId() == id)
        		return true;
        }
        return false;
    }
}
