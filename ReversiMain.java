package reversi;

public class ReversiMain
{
	IModel model;
	IView view;
	IController controller;

	ReversiMain()
	{
		// Choose ONE of the models
		model = new SimpleModel();
		//model = new SimpleTestModel();
		
		// Choose ONE of the views
		//view = new TextView();
		//view = new FakeTextView();
		view = new GUIView(); 
		//view = new TestYourController();
		
		// Choose one controller
		//controller = new SimpleController();
		controller = new ReversiController(); // You need to implement this class yourself!
		
		// Don't change the lines below here, which connect things together
		
		// To remove dependencies upon creation order, first all of the objects are created, 
		// then they are given object references to each other, using the initialise() functions.

		// Initialise everything... (connect objects together)
		model.initialise(8, 8, view, controller);
		controller.initialise(model, view);
		view.initialise(model, controller);
		
		// Now start the game - set up the board
		controller.startup();
	}
	
	public static void main(String[] args)
	{
		new ReversiMain();
	}
}
