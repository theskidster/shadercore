package dev.theskidster.shadercore;

/**
 * @author J Hoffman
 * Created: Apr 19, 2021
 */

/**
 * Static class which provides the implementing application with an interface it can use to supply the ShaderCore library with whatever environment variables 
 * it requires to function properly.
 */
public class ShaderCore {

    static final String MODULE_NAME = "shadercore";
    private static String filepath;
    
    /**
     * Specifies the filepath the ShaderCore library will use when searching for .glsl source files during the creation of {@link Shader} objects.
     * 
     * @param value the relative path within the applications .jar to the package containing its .glsl source files
     */
    public static void setFilepath(String value) {
        filepath = value;
    }
    
    /**
     * Supplies the filepath which the implementing application uses to store its .glsl source files.
     * 
     * @return the relative path within the applications .jar to the package containing its .glsl source files
     */
    static String getFilepath() {
        return filepath;
    }
    
}