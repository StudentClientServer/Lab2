package client;

import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import client.Client;
import client.Group;
import client.JTableModel;


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

        try {
            client = new Client();
            groups = client.getUpdate();
            new JTableModel(groups, client);
        }catch (ClientException e){
            logger.log(Level.SEVERE,"Connection timed out.",e);
            JOptionPane.showMessageDialog(frame, "Connection timed out!" );
        }catch (ServerException e){
            logger.log(Level.SEVERE,"Connection timed out.",e);
            JOptionPane.showMessageDialog(frame, "Connection timed out!" );
        }

    }

}
