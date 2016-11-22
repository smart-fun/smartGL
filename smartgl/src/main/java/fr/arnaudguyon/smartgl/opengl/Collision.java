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
