package fr.arnaudguyon.smartgl.opengl;

import java.util.ArrayList;

import fr.arnaudguyon.smartgl.math.Vector2D;

/**
 * Created by Arnaud Guyon on 10/05/2015.
 */
public class CollisionSegments extends Collision {

    private ArrayList<Vector2D> mRawSegments = new ArrayList<>();
    private ArrayList<Vector2D> mGlobalSegments = new ArrayList<>();

    public CollisionSegments(Sprite owner) {
        super(owner);
    }

    public ArrayList<Vector2D> getSegments() {
        return mGlobalSegments;
    }

    public void addSegment(Vector2D vector) {
        mRawSegments.add(vector);

        Vector2D global = new Vector2D(vector);
        mGlobalSegments.add(global);
    }

    @Override
    public void computePositionAndSize() {
        for(int i=0; i<mRawSegments.size(); ++i) {
            Vector2D raw = mRawSegments.get(i);
            Vector2D global = mGlobalSegments.get(i);
            global.move(mOwner.getPosX() - global.getXStart() + raw.getXStart(), mOwner.getPosY() - global.getYStart() + raw.getYStart());
        }
    }

    @Override
    public boolean collide(Collision other) {
        return false;
    }
}
