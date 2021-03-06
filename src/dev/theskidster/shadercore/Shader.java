package dev.theskidster.shadercore;

import dev.theskidster.jlogger.JLogger;
import static dev.theskidster.shadercore.ShaderCore.MODULE_NAME;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author J Hoffman
 * Created: Apr 19, 2021
 */

/**
 * Encapsulates an OpenGL shader object. These objects contain the compiled results of a .glsl source file and provide it as a single stage of a much larger
 * {@link GLProgram}.
 */
public final class Shader {

    final int handle;
    
    /**
     * Parses a .glsl source file then utilizes the stage specified to produce a new OpenGL shader object.
     * 
     * @param filename the name of the .glsl file to parse. Expects the file extension to be included.
     * @param stage    the stage of the shader process this object will describe. One of: {@link org.lwjgl.opengl.GL30#GL_VERTEX_SHADER GL_VERTEX_SHADER}, 
     *                 {@link org.lwjgl.opengl.GL30#GL_FRAGMENT_SHADER GL_FRAGMENT_SHADER}, 
     *                 {@link org.lwjgl.opengl.GL32#GL_GEOMETRY_SHADER GL_GEOMETRY_SHADER}, 
     *                 {@link org.lwjgl.opengl.GL40#GL_TESS_CONTROL_SHADER GL_TESS_CONTROL_SHADER}, 
     *                 {@link org.lwjgl.opengl.GL40#GL_TESS_EVALUATION_SHADER GL_TESS_EVALUATION_SHADER}, or 
     *                 {@link org.lwjgl.opengl.GL43#GL_COMPUTE_SHADER GL_COMPUTE_SHADER}. 
     */
    public Shader(String filename, int stage) {
        if(ShaderCore.getFilepath() == null) {
            JLogger.setModule(MODULE_NAME);
            JLogger.logSevere("Uninitialized filepath. Use ShaderCore.setFilepath() and try again.", null);
        }
        
        StringBuilder output = new StringBuilder();
        InputStream file     = Shader.class.getResourceAsStream(ShaderCore.getFilepath() + filename);
        
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(file, "UTF-8"))) {
            String line;
            while((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch(Exception e) {
            JLogger.setModule(MODULE_NAME);
            JLogger.logSevere("Failed to parse GLSL file: \"" + filename + "\"", e);
        }

        CharSequence sourceCode = output.toString();

        handle = glCreateShader(stage);
        glShaderSource(handle, sourceCode);
        glCompileShader(handle);

        if(glGetShaderi(handle, GL_COMPILE_STATUS) != GL_TRUE) {
            JLogger.setModule(MODULE_NAME);
            JLogger.logSevere("Failed to compile GLSL file: \"" + filename + "\" " + glGetShaderInfoLog(handle), null);
        }
    }
    
}