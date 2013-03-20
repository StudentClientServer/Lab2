package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import view.ServerView;
import view.View;

import model.*;

import controller.ControllerException;

public class Controller implements ActionListener {
	private Server server;
	private View view;
	private String xmlpath;
	private String dtdpath;
	
	public Controller(Server server, View view) {
		this.server = server;
		this.view = view;
	}

	private void readServerSources() throws ControllerException {
	    try {
		Scanner input = new Scanner (new File("params.txt"));
		input.nextLine();
		dtdpath = input.nextLine();
		System.out.println(dtdpath);
		input.nextLine();
		xmlpath = input.nextLine();
		System.out.println(xmlpath);
		input.close();
	    } catch (FileNotFoundException e) {
		throw new ControllerException("Can't read the file with connection parameters.");
	    }
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
		    view.exceptionHandling(new ControllerException(ex));
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    	View view = new ViewFactory().newInstance();
		try {		    
		    Server server = new Server();  
		    Controller controller = new Controller(server, view);
		    controller.readServerSources();
		    server.setXmlPath(controller.getXmlpath());
		    server.setDtdPath(controller.getDtdpath());
		    server.readDocument();
		} catch (ServerException ex) {
		    view.exceptionHandling(new ControllerException(ex));
		} catch (ControllerException e) {
		    view.exceptionHandling(e);
		}
	}

	public String getXmlpath() {
	    return xmlpath;
	}

	public void setXmlpath(String xmlpath) {
	    this.xmlpath = xmlpath;
	}

	public String getDtdpath() {
	    return dtdpath;
	}

	public void setDtdpath(String dtdpath) {
	    this.dtdpath = dtdpath;
	}
	
}
