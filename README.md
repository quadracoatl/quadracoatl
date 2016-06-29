Quadracoatl
===========

Quadracoatl is an engine for moddable, multiplayer bloxel games created with
Lua.

Its goal is to provide a feature rich platform for creating and powering
bloxel games.


License
-------

Quadracoatl is licensed under the Lesser GNU General Public License version 3 or
later.


Components
----------

Quadracoatl is powered by the following components:

 * [Java 1.8+][java], main platform
 * [jMonkeyEngine][j-monkey-engine], main engine
   * [LWJGL][lwjgl-github], OpenGL layer
 * [KryoNet][kryonet-github], Network layer
   * [Kryo][kryo-github], serialization system
 * [LuaJ][luaj-sourceforge], Scripting system


Components licenses
-------------------

The licenses of all libraries are as follows (please refer to the attached
license file of each library for detailed information):

 * KryoNet
   * asm, BSD-like
   * jsonbeans, BSD-like
   * kryo, BSD-like
   * kryonet, BSD-like
   * minlog, BSD-like
   * objenesis, BSD-like
   * reflect-asm, BSD-like
 * jMonkeyEngine
   * jme, BSD-like
   * lwjgl, BSD-like
 * jopt-simple  
   * jopt-simple, MIT
 * LuaJ
   * bcel, Apache
   * luaj, BSD-like
 * RSyntaxTextArea
   * rsyntaxtextarea, BSD-like
 * Tinylog
   * tinylog, Apache
 * Trove
   * trove, LGPL 2.1


Terminology
-----------

    Cosmos -> Realm(s) -> Chunks -> Blocks
    
    Game -> Mod(s) -> Scripts
                   -> Resources (textures, models, etc.)

### Block

A block is the smallest unit of the engine, it resembles exactly one block in
the game world.

### Chunk

A chunk is a collection of blocks within the realm.

### Cosmos

The cosmos is the over-arching data structure, it can contain multiple realms.

### Realm

A realm defines one world and contains chunks.



 [java]: https://www.java.com/
 [j-monkey-engine]: https://jmonkeyengine.org/
 [kryo-github]: https://github.com/EsotericSoftware/kryo
 [kryonet-github]: https://github.com/EsotericSoftware/kryonet
 [luaj-sourceforge]: https://sourceforge.net/projects/luaj/
 [lwjgl-github]: https://github.com/LWJGL/lwjgl3

