package penInteraction;


import javax.swing.UIManager;



public class GroundTruthGraphView {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new PaintFrame("JavaPainter").setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	
		
	}
}

