package kit.edu.lego.kompaktor.test;
import kit.edu.lego.kompaktor.model.Kompaktor;

public class LightTester {

	public static void main(String[] args) {

		Kompaktor.setFloodlight(false);
		
		while(true){
			while(!Kompaktor.isTouched());
			
			int[] vals = Kompaktor.readLightDifferenceArr();
			
			System.out.println("light: " + vals[0]);
			System.out.println("on="+vals[1] + "  off="+vals[2]);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

	}

}
