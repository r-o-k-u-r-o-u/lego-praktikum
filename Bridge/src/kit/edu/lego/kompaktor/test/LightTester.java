package kit.edu.lego.kompaktor.test;
import kit.edu.lego.kompaktor.model.Kompaktor;
import lejos.nxt.Sound;

public class LightTester {

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		Kompaktor.setFloodlight(false);
		
		//ligthSensor.setFloodlight(false);
		
		while(true){
			while(!Kompaktor.isTouched());
			
			try {
				boolean onLED = Kompaktor.onRedLED();
				
				if (onLED) {
					Sound.beepSequenceUp();
				}
			} catch (InterruptedException e1) {
				System.out.println("OMFG!\nSTFU!");
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

	}
}
 