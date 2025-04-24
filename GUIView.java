package reversi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class GUIView implements IView {

    IModel model;
    IController controller;
    JFrame white;
    JFrame black;
    JLabel blStatus;
    JLabel whStatus;
    Grid wGrid;
    Grid bGrid;

    public GUIView () {
        white = new JFrame();
        black = new JFrame();
        blStatus = new JLabel();
        whStatus = new JLabel();
    }

    @Override
    public void initialise(IModel model, IController controller) {
        this.model=model;
        this.controller=controller;

        //Black is true, white is false
        wGrid = new Grid(1,controller,model);
        bGrid = new Grid(2,controller,model);

		white.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		black.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		white.setTitle("Reversi - White Player");
		white.setLocationRelativeTo(null);
		black.setTitle("Reversi - Black Player");
		black.setLocationRelativeTo(null);
    
        white.setLayout(new BorderLayout());
        black.setLayout(new BorderLayout());

        black.add(blStatus,BorderLayout.NORTH);

        white.add(whStatus,BorderLayout.NORTH);
        
        white.add(wGrid,BorderLayout.CENTER);
        black.add(bGrid,BorderLayout.CENTER);

        Button whiteAI = new Button("Greedy AI (play white)"); 
        whiteAI.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                controller.doAutomatedMove(1);
            }
        });

        Button blackAI = new Button("Greedy AI (play black)");
        blackAI.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                controller.doAutomatedMove(2);
            }
        });

        MouseAdapter restart = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                controller.startup();
                refreshView();
            }
        };

        Button restartW = new Button("Restart");
        restartW.addMouseListener(restart);
        Button restartB = new Button("Restart");
        restartB.addMouseListener(restart);
        
        JPanel whitePanel = new JPanel();
        whitePanel.setLayout(new GridLayout(2,1));
        whitePanel.add(whiteAI);
        whitePanel.add(restartW);

        JPanel blackPanel = new JPanel();
        blackPanel.setLayout(new GridLayout(2,1));
        blackPanel.add(blackAI);
        blackPanel.add(restartB);

        white.add(whitePanel,BorderLayout.SOUTH);
        black.add(blackPanel,BorderLayout.SOUTH);

		white.pack();
		black.pack();

		white.setVisible(true);
		black.setVisible(true);
    }

    @Override
    public void refreshView() {
        wGrid.refresh();
        bGrid.refresh();    
    }

    @Override
    public void feedbackToUser(int player, String message) {
        if (player==2){
            whStatus.setText(message);
            white.revalidate();
        }else if (player==1){
            blStatus.setText(message);
            black.revalidate();
        }
    }

}
