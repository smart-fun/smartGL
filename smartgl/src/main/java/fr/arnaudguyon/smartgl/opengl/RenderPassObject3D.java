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
 * Created by arnaud on 20/11/2016.
 */

public class RenderPassObject3D extends RenderPass {

    public enum ShaderType {
        SHADER_TEXTURE,
        SHADER_TEXTURE_AMBIANT,
        SHADER_TEXTURE_LIGHTS,
        SHADER_COLOR
    }

    public RenderPassObject3D() {
        super(true, true);
        setShader(ShaderType.SHADER_TEXTURE);
    }
    public RenderPassObject3D(ShaderType shaderType, boolean useZBuffer, boolean clearZBuffer) {
        super(useZBuffer, clearZBuffer);
        setShader(shaderType);
    }
    // TODO: see if change shader during renderpass...

    private void setShader(ShaderType shaderType) {
        switch (shaderType) {
            case SHADER_TEXTURE:
                ShaderTexture shaderTexture = new ShaderTexture();
                setShader(shaderTexture);
                break;
            case SHADER_TEXTURE_AMBIANT:
                ShaderTextureAmbiant shaderTextureAmbiant = new ShaderTextureAmbiant();
                setShader(shaderTextureAmbiant);
                break;
            case SHADER_TEXTURE_LIGHTS:
                ShaderTextureLights shaderTextureLights = new ShaderTextureLights();
                setShader(shaderTextureLights);
                break;
            case SHADER_COLOR:
                ShaderColor shaderColor = new ShaderColor();
                setShader(shaderColor);
                break;
        }
    }

    public void addObject(Object3D object3D) {
        getRenderObjects().add(object3D);
    }
}
