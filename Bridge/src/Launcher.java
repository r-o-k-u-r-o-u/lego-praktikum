
public class Launcher {

	
	ParcoursSegment currentSegment = null;
	ParcoursSegment labyrinthSegment = new LabyrinthSegment();
	ParcoursSegment lineSegment = new LineSegment();
	ParcoursSegment bridgeSegment = new BridgeSegment();
	
	public static void main(String[] args) {
		new Launcher();
	}
	
	public Launcher() {
		currentSegment = labyrinthSegment;
		
		start();
	}
	
	private void start() {
		currentSegment.run();
	}
}
