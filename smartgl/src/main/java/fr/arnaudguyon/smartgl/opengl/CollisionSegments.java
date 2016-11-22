/*
    Copyright 2016 Arnaud Guyon

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
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
