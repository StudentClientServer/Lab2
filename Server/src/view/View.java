package view;

import java.awt.event.ActionListener;

import controller.Controller;
import model.ServerException;
import model.ServerModel;

public interface View {

    void setModel(ServerModel model);
    ServerModel getModel();
    void setController(ActionListener controller);
    ActionListener getController ();
    void exceptionHandling(Exception ex);

}
