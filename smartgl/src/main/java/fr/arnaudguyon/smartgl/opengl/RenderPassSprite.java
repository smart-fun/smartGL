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

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

/**
 * Created by arnaud on 20/11/2016.
 */

public class RenderPassSprite extends RenderPass {

    public RenderPassSprite() {
        super(false, false);
        ShaderTexture shader = new ShaderTexture();
        setShader(shader);
    }

    public void addSprite(Sprite sprite) {
        getRenderObjects().add(sprite);
    }

    @Override
    void sortObjects() {
        Vector<RenderObject> objects = getRenderObjects();
        Collections.sort(objects, SPRITE_COMPARATOR);
    }

    private static final Comparator<RenderObject> SPRITE_COMPARATOR = new Comparator<RenderObject>() {
            @Override
            public int compare(RenderObject leftO, RenderObject rightO) {
                if (leftO instanceof Sprite) {
                    Sprite leftSprite = (Sprite) leftO;
                    if (rightO instanceof Sprite) {
                        Sprite rightSprite = (Sprite) rightO;
                        int diff = rightSprite.getDisplayPriority() - leftSprite.getDisplayPriority();
                        if (diff == 0) {
                            return 0;
                        } else if (diff > 0) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } else {
                        return -1;
                    }
                } else if (rightO instanceof Sprite) {
                    return 1;
                } else {
                    return 0;
                }
            }
    };

}
