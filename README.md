seer
========

Audiovisual and embodied worldmaking in scala.

**seer** aims to provide expressive tools for the rapid development of audiovisual interactive distributed installations and performances. On top of audio and graphics apis, **seer** prioritizes interactivity and improvisational instruments building as a primary focus. Live-coding apis as well as interaction libraries and improvisational content performance / creation pipelines are included as high level tools for creativity support. **seer** aims to support a number of platforms including desktop, mobile, and browser based experiences.

**seer** was created as a toolset to develop performance / installation works as part of my dissertation research entitled *[Embodied Worldmaking](https://escholarship.org/uc/item/66h114tg)*.

-------

## Getting Started

install simple build tool (sbt): [http://www.scala-sbt.org/](http://www.scala-sbt.org/)

On macOS using homebrew: `brew install sbt`

Running an example:
~~~~
cd seer
sbt
project examples
run
~~~~

then choose an example from list, or specify directly with:

~~~~
runMain seer.examples.HelloWorld
~~~~


