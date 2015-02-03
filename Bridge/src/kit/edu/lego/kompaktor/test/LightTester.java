package kit.edu.lego.kompaktor.test;
import kit.edu.lego.kompaktor.model.Kompaktor;

public class LightTester {

	public static void main(String[] args) {

//		Kompaktor.setFloodlight(false);
		
		//ligthSensor.setFloodlight(false);
		
		while(true){
			while(!Kompaktor.isTouched());
			
			System.out.println("val="+Kompaktor.readLightValue());
			
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

	}
}
 