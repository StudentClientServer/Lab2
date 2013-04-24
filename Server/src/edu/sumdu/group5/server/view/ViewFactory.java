package edu.sumdu.group5.server.view;

import edu.sumdu.group5.server.controller.ControllerException;
import edu.sumdu.group5.server.model.ServerException;

/**
 * A factory for creating View objects.
 */
public class ViewFactory {

    /**
     * Return instance of View, according to the passed parameter.
     * 
     * @return the view
     * @throws ServerException if specified view type is unknown
     */
    private View resultView;

    public Object newInstance(String view) throws ControllerException, ServerException {
        if ("console".equals(view)) {
            resultView = new ServerView();
        } else {
            throw new ServerException("Undefined view: " + view);
        }
        return resultView;
    }
}
