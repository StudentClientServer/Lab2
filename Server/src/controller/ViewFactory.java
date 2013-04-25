package controller;

import view.ServerView;
import view.View;

/**
 * A factory for creating View objects.
 */
public class ViewFactory {
    
    /**
     * New instance.
     *
     * @return the view
     */
    public View newInstance() {
	return new ServerView();
    }
}
