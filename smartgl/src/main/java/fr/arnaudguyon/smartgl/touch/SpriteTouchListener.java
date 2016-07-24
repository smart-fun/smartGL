package fr.arnaudguyon.smartgl.touch;

import fr.arnaudguyon.smartgl.opengl.Sprite;

public interface SpriteTouchListener {
	boolean onTouch(Sprite sprite, float frameDuration, TouchHelperEvent event);
}
