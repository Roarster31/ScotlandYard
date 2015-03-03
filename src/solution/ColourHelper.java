package solution;

import scotlandyard.Colour;

/**
 * Created by rory on 03/03/15.
 */
public class ColourHelper {

	public static Colour nextColour(Colour colour){
		switch (colour){
			case Black:
				return Colour.Blue;
			case Blue:
				return Colour.White;
			case White:
				return Colour.Green;
			case Green:
				return Colour.Red;
			case Red:
				return Colour.Yellow;
			case Yellow:
				return Colour.Black;
			default:
				return Colour.Black;
		}
	}
}
