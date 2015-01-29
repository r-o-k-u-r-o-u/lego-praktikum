package kit.edu.lego.kompaktor.threading;


public class Launcher {

	
	ParcoursRunner currentRunner = null;
	
	public static void main(String[] args) {
		new Launcher();
	}
	
	public Launcher() {

		
		start();
	}
	
	private void start() {
		currentRunner.run();
	}
}
