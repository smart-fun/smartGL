package fr.arnaudguyon.smartgl.opengl;

/**
 * Created by Arnaud on 07/05/2015.
 */
public abstract class Collision {

    protected Sprite mOwner;

    public Collision(Sprite owner) {
        mOwner = owner;
    }

    public abstract void computePositionAndSize(); // TODO: rename with updateCollisionWithOwner (?)
   //abstract float[] getPosition();
    //abstract float getWidth();
    //abstract float getHeight();

    public abstract boolean collide(Collision other);
    //public abstract boolean isInside(Collision other);
    //public abstract boolean isCenterInside(Collision other);
    //public abstract float getPosX();
    //public abstract float getPosY();
    public boolean displayAtScreen() {
        return (mOwner.isVisible() && (mOwner.getAlpha() > 0));
    }
}
