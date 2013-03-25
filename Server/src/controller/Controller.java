package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import view.View;

import model.*;

import controller.ControllerException;

/**
 * The Class Controller.
 */
public class Controller implements ActionListener {
	
	/** The server. */
	private ServerModel server;
	
	/** The view. */
	private View view;
	
	/** The xmlpath. */
	private String xmlpath;
	
	/** The dtdpath. */
	private String dtdpath;
	
	/**
	 * Instantiates a new controller.
	 *
	 * @param server2 the server model
	 * @param view the view
	 */
	public Controller(ServerModel server2, View view) {
		this.server = server2;
		this.view = view;
	}

	/**
	 * Read pathes to xml and dtd files.
	 *
	 * @throws ControllerException the controller exception
	 */
	private void readServerSources() throws ControllerException {
	    try {
		Scanner input = new Scanner (new File("params.txt"));
		input.nextLine();
		dtdpath = input.nextLine();
		input.nextLine();
		xmlpath = input.nextLine();
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
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
	    	View view = new ViewFactory().newInstance();
		try {		    
		    ServerModel server = new Server();  
		    Controller controller = new Controller(server, view);
		    controller.readServerSources();
		    server.setXmlPath(controller.getXmlpath());
		    server.setDtdPath(controller.getDtdpath());
		    server.readDocument();
		    view.setController(controller);
		    view.setModel(server);
		    view.starting();
		} catch (ServerException ex) {
		    view.exceptionHandling(new ControllerException(ex));
		} catch (ControllerException e) {
		    view.exceptionHandling(e);
		} catch (IOException e) {
		    view.exceptionHandling(e);
                }
	}

	/**
	 * Gets the xmlpath.
	 *
	 * @return the xmlpath
	 */
	public String getXmlpath() {
	    return xmlpath;
	}

	/**
	 * Sets the xmlpath.
	 *
	 * @param xmlpath the new xmlpath
	 */
	public void setXmlpath(String xmlpath) {
	    this.xmlpath = xmlpath;
	}

	/**
	 * Gets the dtdpath.
	 *
	 * @return the dtdpath
	 */
	public String getDtdpath() {
	    return dtdpath;
	}

	/**
	 * Sets the dtdpath.
	 *
	 * @param dtdpath the new dtdpath
	 */
	public void setDtdpath(String dtdpath) {
	    this.dtdpath = dtdpath;
	}
	
}
