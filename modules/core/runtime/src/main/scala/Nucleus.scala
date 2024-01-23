
package seer
package nucleus


/**
 * The central and most important part of an object, movement, or group, forming the basis for its activity and growth.
 *
 * A membrane-bound organelle that contains the cell's chromosomes.
 * 
 * The nucleolus is the largest structure in the nucleus of eukaryotic cells.
 * It is best known as the site of ribosome biogenesis, which is the synthesis of ribosomes.
 * The nucleolus also participates in the formation of signal recognition particles and plays a role in the cell's response to stress.
 */

/**
 * When at all possible, I will be using living metaphors as inspiration for organization of this system.
 * In Seer, the nucleus binds together separate modalities/affordances through RuntimeModules, spawning services at runtime,
 * allowing for scalable and distributed interactive audiovisual systems to be built up interactively.
 * 
 * Nucleus' goal is to be as simple as possible,
 * utilize exisitng message passing apis to create perforated and distributed
 * interconnected systems.
 * 
 * We will be ussing apache pekko (a.k.a. akka) to open remote actor ports that will flexibly respond to
 * system creation requests. ie:
 *   - spawing window+graphics context
 *   - spawning webgl/webgpu server+graphics context?
 *   - spawn audio context using availble backend driver
 *   - spawn audio context using available spatialization context
 *   - running arbitrary scala code w/permission
 *   - running arbitrary ssh+bash script w/ i/o forwarding
 *   - forward state sync to appropriate actor
 * 
 * Use case 1: thin daemon, create window, compile/run code to populate window 
 * Use case 2: thin launcher, ssh+run bash, stream stdin/stdout/stderr
 * Use case 3: seer dekstop/workbench/composer/studio -- service always runnig -- quickly capture audio/video images resources into code based narrative
 * Use caes 4: distributed system - nucleus.conf/.seer/.scala - config file/script specify available affordances and their configurations
 *             run on one of many * nodes -- cluster aware
 * 
 * Distributed Roles: -- not even necessary? or makes sense if defined centrally, and a node claims a role, configuration in one place? vs config on each node
 * simulator
 * graphics
 * audio
 * interface
 */



