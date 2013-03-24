package test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import model.Group;
import model.Server;
import model.ServerException;
import model.Student;

public class Test {
	
	private static final DateFormat format = new SimpleDateFormat("dd.MM.yyyy");  
	
	private static Server server;
	
	/**
	 * @param args
	 * @throws ServerException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ServerException, ParseException {
		// TODO Auto-generated method stub		
		server = new Server("groups.xml", "groups.dtd");
		//checkAddGroup(new Group("Elit", "IN-91"));
		//checkAddStudent (new Student(9, "Vasia", "Pupkin", "IN-91", format.parse("12.02.2001")));
		//checkRemoveStudent(9);
		checkRemoveGroup("IN-91");
		//checkUpdateGroup(new Group ("Med", "2"));
		//checkUpdateGroupNumber(new Group("Med", "2"), "Hir-01");
		//checkUpdateStudent(new Student(8, "Vasia", "Pupkin", "1", format.parse("12.02.2001")));
		checkGetters ();
	}	

	private static void checkGetters () throws ServerException {
		List<Group> groups = server.getGroups();
		for(Group g : groups) {
			System.out.println("Group number " + g.getNumber());
			System.out.println("Group fakulty " + g.getFakulty());
			List<Student> students = g.getStudents();
			for(Student s : students) {
				System.out.println("-- students id: " + s.getId());			
				System.out.println("------ students group number: " + s.getGroupNumber());
				System.out.println("------ students firstname: " + s.getFirstName());
				System.out.println("------ students lastname: " + s.getLastName());
			}
		}
	}

	private static void checkAddGroup(Group group) throws ServerException {
		server.addGroup(group);
	}
	
	private static void checkAddStudent (Student student) throws ServerException, ParseException {		
		server.addStudent(student);	
	}
	
	private static void checkRemoveStudent(int id) throws ServerException {
		server.removeStudent(id);
	}
	
	private static void checkRemoveGroup(String groupName) throws ServerException {
		server.removeGroup(groupName);
	}
	
	private static void checkUpdateGroup (Group group) throws ServerException {
		server.updateGroup(group);
	}
	
	private static void checkUpdateGroupNumber (Group group, String newNumber) throws ServerException {
		server.updateGroup(group, newNumber);
	}
	
	private static void checkUpdateStudent (Student student) throws ServerException {
		server.updateStudent(student);
	}
	
}
