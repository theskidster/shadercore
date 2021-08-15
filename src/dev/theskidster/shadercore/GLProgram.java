package dev.theskidster.shadercore;

import dev.theskidster.jlogger.JLogger;
import static dev.theskidster.shadercore.BufferType.*;
import static dev.theskidster.shadercore.ShaderCore.MODULE_NAME;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Apr 19, 2021
 */

/**
 * Represents a completed shader program comprised of multiple {@link Shader} objects that specify how data sent to the GPU will be processed at different 
 * rendering stages while the program is active.
 */
public class GLProgram {

    final int handle;
    
    public final String name;
    
    private final Map<String, Uniform> uniforms = new HashMap<>();
    private static final Map<BufferType, Integer> bufferSizes;
    
    static {
        bufferSizes = new HashMap<>() {{
            put(VEC2, 2);
            put(VEC3, 3);
            put(VEC4, 4);
            put(MAT2, 8);
            put(MAT3, 12);
            put(MAT4, 16);
        }};
    }
    
    /**
     * Creates a new shader program with the code supplied from the compiled .glsl source files.
     * 
     * @param shaders the objects representing various stages of the rendering pipeline
     * @param name    the name used to identify the program should it fail to link properly
     */
    public GLProgram(LinkedList<Shader> shaders, String name) {
        this.name = name;
        
        handle = glCreateProgram();
        shaders.forEach(shader -> glAttachShader(handle, shader.handle));
        glLinkProgram(handle);
        
        if(glGetProgrami(handle, GL_LINK_STATUS) != GL_TRUE) {
            JLogger.setModule(MODULE_NAME);
            JLogger.logSevere("Failed to link shader program: \"" + name + "\" " + glGetShaderInfoLog(handle), null);
        } else {
            JLogger.setModule(MODULE_NAME);
            JLogger.logInfo("Shader program: \"" + name + "\" linked successfully.");
            JLogger.setModule(null);
        }
    }
    
    /**
     * Generates a new {@link Uniform} object.
     * 
     * @param name   the unique name used to identify the uniform variable as it appears in the .glsl source file
     * @param buffer the data buffer that stores the value which will be supplied to the GPU at runtime
     * @return a new uniform object
     */
    private Uniform createUniform(String name, Buffer buffer) {
        return new Uniform(glGetUniformLocation(handle, name), buffer);
    }
    
    /**
     * Creates an association between a CPU-stored data buffer holding the value of a {@linkplain Uniform uniform variable} and its corresponding memory 
     * location on the GPU.
     * <br><br>
     * More specifically this method allocates a new data buffer on the CPU with a size corresponding to the GLSL data type specified, then locates the 
     * memory address of the buffer on the GPU that holds the value of the uniform variable and provides that information in an object which will wrap this
     * state for the ShaderCore library to use during rendering operations. 
     * 
     * @param type the GLSL data type of the uniform variable
     * @param name the unique name used to identify the uniform variable as it appears in the .glsl source file
     */
    public void addUniform(BufferType type, String name) {
        if(glGetUniformLocation(handle, name) == -1) {
            JLogger.setModule(MODULE_NAME);
            JLogger.logSevere("Failed to find uniform: \"" + name + "\" check " + 
                              "variable name or GLSL source file where it is declared.", 
                              null);
        }
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            switch(type) {
                case INT              -> uniforms.put(name, createUniform(name, stack.mallocInt(1)));
                case FLOAT            -> uniforms.put(name, createUniform(name, stack.mallocFloat(1)));
                case VEC2, VEC3, VEC4 -> uniforms.put(name, createUniform(name, stack.mallocFloat(bufferSizes.get(type))));
                case MAT2, MAT3, MAT4 -> uniforms.put(name, createUniform(name, stack.mallocFloat(bufferSizes.get(type))));
            }
        }
    }
    
    /**
     * Sets this as the current shader program the GPU will use for all subsequent rendering operations.
     */
    public void use() {
        glUseProgram(handle);
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name  the unique name used to identify the uniform variable as it appears in the .glsl source file
     * @param value the new value of the uniform variable
     */
    public void setUniform(String name, int value) {
        glUniform1i(uniforms.get(name).location, value);
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name  the unique name used to identify the uniform variable as it appears in the .glsl source file
     * @param value the new value of the uniform variable
     */
    public void setUniform(String name, float value) {
        glUniform1f(uniforms.get(name).location, value);
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name  the unique name used to identify the uniform variable as it appears in the .glsl source file
     * @param value the new value of the uniform variable
     */
    public void setUniform(String name, Vector2f value) {
        glUniform2fv(
                uniforms.get(name).location,
                value.get(uniforms.get(name).asFloatBuffer()));
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name  the unique name used to identify the uniform variable as it appears in the .glsl source file
     * @param value the new value of the uniform variable
     */
    public void setUniform(String name, Vector3f value) {
        glUniform3fv(
                uniforms.get(name).location,
                value.get(uniforms.get(name).asFloatBuffer()));
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name  the unique name used to identify the uniform variable as it appears in the .glsl source file
     * @param value the new value of the uniform variable
     */
    public void setUniform(String name, Vector4f value) {
        glUniform4fv(
                uniforms.get(name).location,
                value.get(uniforms.get(name).asFloatBuffer()));
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name      the unique name used to identify the uniform variable as it appears in the .glsl source file
     * @param transpose if true, the matrix data provided in the value parameter will be transposed before it is read
     * @param value     the new value of the uniform variable
     */
    public void setUniform(String name, boolean transpose, Matrix2f value) {
        glUniformMatrix2fv(
                uniforms.get(name).location,
                transpose,
                value.get(uniforms.get(name).asFloatBuffer()));
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name      the unique name used to identify the uniform variable as it appears in the .glsl source file
     * @param transpose if true, the matrix data provided in the value parameter will be transposed before it is read
     * @param value     the new value of the uniform variable
     */
    public void setUniform(String name, boolean transpose, Matrix3f value) {
        glUniformMatrix3fv(
                uniforms.get(name).location,
                transpose,
                value.get(uniforms.get(name).asFloatBuffer()));
    }
    
    /**
     * Supplies the specified uniform variable with a new value.
     * 
     * @param name      the unique name used to identify the uniform variable as it appears in the .glsl source file
     * @param transpose if true, the matrix data provided in the value parameter will be transposed before it is read
     * @param value     the new value of the uniform variable
     */
    public void setUniform(String name, boolean transpose, Matrix4f value) {
        glUniformMatrix4fv(
                uniforms.get(name).location,
                transpose,
                value.get(uniforms.get(name).asFloatBuffer()));
    }
    
}