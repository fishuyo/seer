
# TODO



## system design

Flow aims to enable improvisational embodied worldmaking practice bridging the digital-technic and the somatic-organic-analog -- each medium, each body, holds a unique expression of sensory umvelt and actuation of diverse fluid expressive potential -- wound up like springs, elastic, rigid, growing, folding, pulsating, expanding, breathing, darting, loving, moving. We live with. We are not alone. We do not grow alone. We do not evolve alone. We do not live alone. We are, and are within, evolving living patterns. Flow aims to enable building living digital worlds and creative process that utilize instrumentation through hci devices, body based practices, physicality and simulara living systems, audiovisual, interactive,

- modules
  - core (roots)
    - runtime
    - datatypes - array, buffer
    - spatialmaths 
      - geometric algebra
    - parameter?
    
  - mid/core (stems/stalk/trunk)
    - graphics
      - window api
      - rendering api high-level (separate module?)
        - render pass node
        - render graph
        - built in shaders + render pass nodes
          - feedback, blur, ...
      - rendering api low-level interface
      - implementations
    - audio
    - remoting (akka) 
    - actor (akka)
      - parameterActor
    - resourceLoader

  - leaves
  - flowers
  - fruits

  - extensions (leaves / fruits / symbionts)




## where to start

- sketching modeling tool, mapping...
- if i could draw to start... draw the skeleton, thr roughn.
- i reference gesture as data, i see data entities -- i ref them by link or id hash first 3 / rename..



- working from flow let's simplify and build up a solid foundation.

  - lets integrate new seer modules here for now and build the whole deal
  

### seer foundation

If we redesign this as a scalable framework -- let's depend on pekko at base for services -- will need to understand how we separate js / native functionality --- maximizing scalable usability, and in-browser live coding, local servers and deployability.


- Flow enabling server first design -- 
  - basic server and SPA structure
  - live coding modules enabled by default
  - additional modules adding additional 
- Flow welcome site showing available modules -- modules contain server services and embedded interactive documentations
- basic interface offers:
  - IDE for text.. with feedback
  - graphical programming? no-code? low-code? embodied code?
  - xr dev environment?

- Runtime API/Server
  - pekko based system api for loading and running isolated modules?
  - Key functionality includes:
    - network APIs for modifying / configuring system (pekko-remote, OSC, http?, ws?)
    - spawning processes, managing processes, IO streams, modules

- Flow Live-coding API/Server
  - compiler <-> ECS? --> ECOS (entity component organism system ) organ / organism / landscape
  - filesystem access (local use)
  - userdata access --> projects, scripts, resources, data (connection to cloud providers)
  - some core trait shared library, modules can use to fit into this compiler ECOsystem.. 
  - Flavors
    - flow / seer scripts
    - p5.js
    - python
    - no-code
    - touch designer
  - Integrations
    - vst plugins
    - unity / unreal
    - touch designer

- Graphics API/Server
  - Process for rendering running openGL context / vulkan
  - Handle spawning windows, 
- Audio API/Serveer
  - super collider as model?
  - how to achieve native performance -- native server?
  - allolib audio server? <3


### baby steps

1. flow basic server and components to enable
  a. editing compiling running code in ide interface
  b. how code scripts then become members of ECOsystem, ideally if compiled at runtime -- maybe we need to compile everything runtime so some startup costs -- maybe future work we can enable compile time ecosystem compilation....
  c. how to load / save ecosystem workspaces?

2. create a window and draw to it
  a. draw 2d/3d shaded meshes
  b. clear window.
  c. animation loops.
  d. reload graphics context
  e. graphics server is process with 0+ windows

3. ecosystem objects
  a. representing over network, state, code etc (minimal representation)
  b. ...



### goals
- panorama viewer


- beebin text revist <3 -- let's go through my history and start finishing the ideas i had -- or at least giving them a proper go as a way of sharpening my chops..


