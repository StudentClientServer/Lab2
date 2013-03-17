package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import view.ServerView;
import view.View;

import model.Group;
import model.Server;
import model.ServerException;
import model.Student;

public class Controller implements ActionListener {
	private Server server;
	private View view;
	
	public Controller(Server server, View view) {
		this.server = server;
		this.view = view;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if("AddGroup".equals(e.getActionCommand()))
			    server.addGroup((Group)e.getSource());
			if("AddStudent".equals(e.getActionCommand()))
			    server.addStudent((Student)e.getSource());
			if("RemoveGroup".equals(e.getActionCommand()))
			    server.removeGroup((String)e.getSource());
			if("RemoveStudent".equals(e.getActionCommand()))
			    server.removeStudent((Integer)e.getSource());
			if("UptadeGroup".equals(e.getActionCommand()))
			    server.updateGroup((Group)e.getSource());
			if("UpdateStudent".equals(e.getActionCommand()))
			    server.updateStudent((Student)e.getSource());
			if("UpdateGroupNimber".equals(e.getActionCommand())){
			    List<Object> params = (List<Object>) e.getSource();
			    server.updateGroup((Group) params.get(0), (String) params.get(1));
			}			    			    
		} catch (ServerException ex) {
		    view.exceptionHandling(ex);
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    	View view = new ServerView();
		try {		    
		    Server server = new Server("groups.xml", "groups.dtd");    		
		    Controller controller = new Controller(server, view);
		} catch (ServerException ex) {
		    view.exceptionHandling(ex);
		}
	}
	
}
