# ShaderCore
A lightweight rendering abstraction layer which allows java projects to make use of modern OpenGL paradigms.

## How To Use
Navigate to the <a href="https://github.com/theskidster/ShaderCore/releases">releases</a> section and download the latest ShaderCore-X.X.X.zip and unpack the .jar file into your 
projects dependencies. 

ShaderCore requires a few libraries of its own which can be found in the projects "lib" folder- make sure to include these in your project as well if 
they aren't already present.

## Example Code
```java
//First set the filepath the library will use when searching for .glsl files to compile.
ShaderCore.setFilepath("/dev/theskidster/project/shaders/...");
GLProgram uiProgram;

/*
Then initialize the files and provide their compiled objects to the GLProgram object.
Any additional uniform variables should be specified here as well.
*/
{
  var shaderSourceFiles = new LinkedList<Shader>() {{
    add(new Shader("vertexShader.glsl", GL_VERTEX_SHADER));
    add(new Shader("fragmentShader.glsl", GL_FRAGMENT_SHADER));
  }};
  
  uiProgram = new GLProgram(shaderSourceFiles, "ui shader");
  uiProgram.use();
  
  uiProgmra.addUniform(BufferType.INT,  "uIndex");
  uiProgram.addUniform(BufferType.VEC3, "uColor");
  uiProgram.addUniform(BufferType.MAT4, "uProjection");
  //uiProgram.addUni... etc.
}

/*
Finally utilize the completed GLProgram object elsewhere for rendering operations.
Remember to call uiProgram.use() if it isn't already the current shader program!
*/

//...
  glBindVertexArrays(vao);
  
  uiProgram.setUniform("uIndex",      3);
  uiProgram.setUniform("uColor",      colorVecValue);
  uiProgram.setUniform("uProjection", projMatrix);
  //uiProgram.setUni... etc.
  
  glDrawArrays(GL_TRIANGLES, 0, 6);
//...
```
