package setup;

/**
 * Data for one button in the controller config screen
 */
public class ControllerButton {
	public int upperLimit, lowerLimit;

	public ControllerButton(int setLower, int setUpper) {
		upperLimit = setUpper;
		lowerLimit = setLower;
	}
}
