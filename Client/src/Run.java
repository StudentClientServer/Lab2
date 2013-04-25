package view;

import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import view.Group;
import view.JTableModel;
import view.Client;

/**
 */
public class Run {

	private static JFrame frame;
	private static Client client;
	private static List<Group> groups;

    public static void main (String[] args) throws ClientException{//test() throws Exception, SAXException, ParserConfigurationException {
    	
    	Logger logger = Logger.getLogger(Run.class.getName());
    	try {
    		
			HtmlFormatter htmlformatter = new HtmlFormatter();
			FileHandler htmlfile = new FileHandler("LogApp.html");
			
			htmlfile.setFormatter(htmlformatter);
			logger.addHandler(htmlfile);
			
			
		} catch (SecurityException e) {
			logger.log(Level.SEVERE,"Couldn't create log file because of security policy", e);
			JOptionPane.showMessageDialog(frame, "Couldn't create log file because of security policy!" );
		} catch (IOException e) {
			logger.log(Level.SEVERE,"Couldn't create log file because of input.",e);
			JOptionPane.showMessageDialog(frame, "Couldn't create log file because of input!" );
		}
    	
		try {//37.46.246.131
			client = new Client("127.0.0.1",7070);
		} catch (IOException e1) {
			logger.log(Level.SEVERE,"Couldn't create new  client.",e1);
			JOptionPane.showMessageDialog(frame, "Couldn't create new  client!" );
		}

		try {
			groups = client.getUpdate();
		}catch(NullPointerException e){
			logger.log(Level.SEVERE,"No Connection!",e);
			JOptionPane.showMessageDialog(frame, "No Connection!" );
		}catch (IOException e) {
			logger.log(Level.SEVERE,"IOException.",e);
			JOptionPane.showMessageDialog(frame, "Client input wrong!" );
		} catch (SAXException e) {
			logger.log(Level.SEVERE,"SAXException.",e);
			JOptionPane.showMessageDialog(frame, "Client input wrong!" );
		} catch (ParserConfigurationException e) {
			logger.log(Level.SEVERE,"ParserConfigurationException.",e);
			JOptionPane.showMessageDialog(frame, "Client input wrong!" );
		}

		try {
			new JTableModel(groups, client);
		} catch (ClientException e) {
			logger.log(Level.WARNING,"Couldn't create frame.",e);
			JOptionPane.showMessageDialog(frame, "No Connection!" );
		}

	}

}
