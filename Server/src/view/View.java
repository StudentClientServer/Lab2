package view;

import java.awt.event.ActionListener;

import model.ServerModel;

public interface View {

    public void setModel(ServerModel model);    
    public void setController(ActionListener controller);    
    public void exceptionHandling(Exception ex);
    public void starting() throws IOException;

}
