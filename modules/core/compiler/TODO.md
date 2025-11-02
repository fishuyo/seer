# compiler modules

- this module wraps basic runtime compilation functionality of "com.eed3si9n.eval" %% "eval" library
- the goal is to provide a friendly api for a hot swappable live coding component system

- goals
  - component code structure that can be compiled both at runtime and compile time, how can we abstract away the diffrences between classpath / dependency / package import ish
  - components registered into some kind of map registry that we can access at runtime, and hot swap with new code at runtime.
  - pekko actor api / osc api for microservicing? or should that integration happen at a higher level.. maybs
  - this module would be deployed as part of server/service that needs to compile scala code in relationship with flow/seer apis and may need cooperate in a distributed system
    - mutli node cluster renderer for example - each renderer running seer with compiler module -- we send code updates based on node roles that will then compile and swap new modules in?
    
- basic api
  - code (text) --> runnable (eval does this)
  - 
  - code from file system (monitor changes)
  - code from network (pekko / osc / http)
  - script api (extensible ie seerActor vs flowActor vs ...)
  - script registry (hot swappable)
  - code cached to local filesystem? (compile time build / registration -- or specify repo, built on startup how slow?)
    - 
  - vscode plugin?


## todo



## dynamic code-base design
- perhaps we have a part of our code tree -- we specify this special directory to our compiler modules, and it is also added to our sbt source directory to be a part of compilation -- 
- now this directory is initially empty, now we add a file at runtime and the compiler module watches for changes in this directory -- loads and compiles the script that uses a syntax defining a component or module that is registered somehow --
-  next time we build and run the system it now builds this script file they syntax somehow takes care of instantiating the class somehow to register it as component or module and it can be referenced through the module system or directly --- when we edit the script it is still being monitored and recompiles it at runtime and overrides the registered module with the new version -- so the system uses the new version on next time through system loop ---
- the syntax is clean and minimal -- how do we implement this? special trait we inherit from implements it, or speciall annotation syntax like @live class, do we need metaprogramming?