package view;

import java.awt.event.ActionListener;

import model.ServerException;
import model.ServerModel;

public class ServerView implements View{

    private ServerModel model;
    
    private ActionListener controller;
    
    @Override
    public void exceptionHandling(Exception ex) {
	// TODO Auto-generated method stub
	
    }

    public ServerModel getModel() {
        return model;
    }

    public void setModel(ServerModel model) {
        this.model = model;
    }

    public ActionListener getController() {
        return controller;
    }

    public void setController(ActionListener controller) {
        this.controller = controller;
    }

}
