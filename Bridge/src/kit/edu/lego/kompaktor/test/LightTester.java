package kit.edu.lego.kompaktor.test;
import kit.edu.lego.kompaktor.model.Kompaktor;

public class LightTester {

	public static void main(String[] args) {

		Kompaktor.setFloodlight(true);
		
		//ligthSensor.setFloodlight(false);
		
		while(true){
			if(Kompaktor.isTouched()){
				System.out.println("light: " + Kompaktor.LIGHT_SENSOR.readValue());
			}
		}
		
//		while(true){
//			while(!Kompaktor.isTouched());
//			
//			int[] vals;
//			try {
//				vals = Kompaktor.readLightDifferenceArr();
//				
//				System.out.println("light: " + vals[0]);
//				System.out.println("on="+vals[1] + "  off="+vals[2]);
//				
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			
//			
//			
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//			}
//		}

	}

}
