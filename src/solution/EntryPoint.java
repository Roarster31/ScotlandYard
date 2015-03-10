package solution;

import solution.controllers.GameController;
import solution.views.MainFrame;

/**
 * This is the main entry point to the app.
 * I thought it would be nice to have this - it shouldn't be too big
 */
public class EntryPoint {

	public static void main(String[] args) {

		MainFrame mainFrame = new MainFrame();

        GameController gameController = new GameController(mainFrame);



	}

}
