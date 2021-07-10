package bubolo.graphics;

import com.badlogic.gdx.graphics.Color;

public enum PlayerColor {
	Blue(Color.valueOf("5989D0FF")),
	Cyan(Color.valueOf("00CCCCFF")),
	Green(Color.valueOf("31A500FF")),
	Purple(Color.valueOf("B677D8FF")),
	Red(Color.valueOf("E54949FF")),
	Pink(Color.valueOf("FF8EA1FF")),
	Orange(Color.valueOf("FF7700FF")),
	Yellow(Color.valueOf("CCC833FF")),
	Gold(Color.valueOf("B49B57FF")),
	Tan(Color.valueOf("D8BA93FF")),
	Brown(Color.valueOf("A55E08FF")),
	Black(Color.valueOf("7F7F7FFF"));

	public final Color color;

	PlayerColor(Color color) {
		this.color = color;
	}
}
