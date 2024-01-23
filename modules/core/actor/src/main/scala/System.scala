
package seer
package actor

import akka.actor._
import akka.actor.Props
import com.typesafe.config.ConfigFactory
// import akka.event.Logging
// import akka.actor.ActorSystem

import collection.mutable.ListBuffer

object System {
  val remotes = ListBuffer[Address]()  
  var system:ActorSystem = _
  def apply() = {
    if(system == null) system = ActorSystemManager.default()
    system
  }
  def update(s:ActorSystem) = system = s

  def address = System().asInstanceOf[akka.actor.ExtendedActorSystem].provider.getDefaultAddress

  def broadcast(msg:Any) = apply().actorSelection("/user/live.*") ! msg
  def send(name:String, msg:Any) = apply().actorSelection(s"/user/live.$name.*") ! msg
  def broadcastRemote(a:Address)(msg:Any) = apply().actorSelection(s"$a/user/live.*") ! msg
  def sendRemote(a:Address)(name:String, msg:Any) = apply().actorSelection(s"$a/user/live.$name.*") ! msg
  def broadcastRemotes(as:Seq[Address])(msg:Any) = as.foreach{ case a => apply().actorSelection(s"$a/user/live.*") ! msg}
  def sendRemotes(as:Seq[Address])(name:String, msg:Any) = as.foreach{ case a => apply().actorSelection(s"$a/user/live.$name.*") ! msg}
  def broadcastAll(msg:Any) = remotes.foreach{ case a => apply().actorSelection(s"$a/user/live.*") ! msg }
  def sendAll(name:String, msg:Any) = remotes.foreach{ case a => apply().actorSelection(s"$a/user/live.$name.*") ! msg }

}

object ActorSystemManager {

  def apply(address:Address) = {
    address.protocol match {
      case "akka" => artery(address.host.get,address.port.get,address.system)
      case "akka.tcp" => tcp(address.host.get,address.port.get,address.system)
      case "akka.udp" => udp(address.host.get,address.port.get,address.system)
      case _ => default(address.system)
    }
  }

  def default(system:String = "seer") = ActorSystem(system, ConfigFactory.load(config))
  def artery(hn:String=Hostname(), port:Int=2552, system:String="seer") = ActorSystem(system, ConfigFactory.load(config_artery(hn,port)))
  def tcp(hn:String=Hostname(), port:Int=2552, system:String="seer") = ActorSystem(system, ConfigFactory.load(config_tcp(hn,port)))
  def udp(hn:String=Hostname(), port:Int=2552, system:String="seer") = ActorSystem(system, ConfigFactory.load(config_udp(hn,port)))



  def config_artery(hostname:String=Hostname(), port:Int=2552) = ConfigFactory.parseString(s"""
    akka {
      actor {
        provider = remote
        allow-java-serialization = off
        $kryo
        serializers {
          kryo = "io.altoo.akka.serialization.kryo.KryoSerializer"
        }
        serialization-bindings {
          "java.io.Serializable" = kryo
          "seer.spatial.Vec3" = kryo
          "seer.openni.User" = kryo
        }
      }
      remote {
        artery {
          enabled = on
          transport = aeron-udp
          canonical.hostname = "$hostname"
          canonical.port = $port
          advanced {
            # Maximum serialized message size, including header data.
            # maximum-frame-size = 256 KiB
            maximum-frame-size = 10 MiB
          }
        }
      }
      extensions = ["com.romix.akka.serialization.kryo.KryoSerializationExtension$$"]
    }
  """)

  def config_tcp(hostname:String=Hostname(), port:Int=2552) = ConfigFactory.parseString(s"""
    akka {
      actor {
        provider = remote
      }
      remote {
        enabled-transports = ["akka.remote.netty.tcp"]
        netty.tcp {
          send-buffer-size = 20MiB
          receive-buffer-size = 20MiB
          maximum-frame-size = 10MiB
          hostname = "$hostname"
          port = $port
        }
        compression-scheme = "zlib"
        zlib-compression-level = 1
     }
    }
  """)
    // akka.actor.serializers {
    //   kryo = "com.twitter.chill.akka.AkkaSerializer"
    // }
    // akka.actor.serialization-bindings {
    //  "seer.spatial.Vec3" = kryo
    // }
  // """)

  def config_udp(hostname:String=Hostname(), port:Int=2552) = ConfigFactory.parseString(s"""
    akka {
      actor {
        provider = remote
      }
      remote {
        enabled-transports = ["akka.remote.netty.udp"]
        netty.udp {
          send-buffer-size = 20MiB
          receive-buffer-size = 20MiB
          maximum-frame-size = 10MiB
          hostname = "$hostname"
          port = $port
        }
        compression-scheme = "zlib"
        zlib-compression-level = 1
     }
    }
    akka.actor.serializers {
      kryo = "com.twitter.chill.akka.AkkaSerializer"
    }
    akka.actor.serialization-bindings {
      "scala.Product" = kryo
      "scala.collection.mutable.ArrayBuffer" = kryo
      "seer.graphics.MeshLike" = kryo
      "seer.spatial.Vec3" = kryo
    }
  """)

  val config = ConfigFactory.parseString("""
    seer-dispatcher {
      type = "Dispatcher"
      executor = "seer.actor.SeerEventThreadExecutorServiceConfigurator"
      throughput = 1
    }
  """)



  val kryo = """
  akka-kryo-serialization {
  # Possibles values for type are: graph or nograph
  # graph supports serialization of object graphs with shared nodes
  # and cyclic references, but this comes at the expense of a small overhead
  # nograph does not support object graphs with shared nodes, but is usually faster
  type = "graph"

  # Possible values for id-strategy are:
  # default, explicit, incremental, automatic
  #
  # default - slowest and produces bigger serialized representation. Contains fully-
  # qualified class names (FQCNs) for each class
  #
  # explicit - fast and produces compact serialized representation. Requires that all
  # classes that will be serialized are pre-registered using the "mappings" and "classes"
  # sections. To guarantee that both sender and receiver use the same numeric ids for the same
  # classes it is advised to provide exactly the same entries in the "mappings" section
  #
  # incremental - fast and produces compact serialized representation. Support optional
  # pre-registering of classes using the "mappings" and "classes" sections. If class is
  # not pre-registered, it will be registered dynamically by picking a next available id
  # To guarantee that both sender and receiver use the same numeric ids for the same
  # classes it is advised to pre-register them using at least the "classes" section
  #
  # automatic - Contains fully-qualified class names (FQCNs) for each class that is not
  # pre-registered in the "mappings" and "classes" section
  id-strategy = "default"

  # Define a default size for byte buffers used during serialization
  buffer-size = 4096

  # The serialization byte buffers are doubled as needed until they exceed
  # maxBufferSize and an exception is thrown. Can be -1 for no maximum.
  # must be < akka.remote.artery.advanced.maximum-frame-size
  max-buffer-size = -1

  # To use a custom queue the [[io.altoo.akka.serialization.kryo.DefaultQueueBuilder]]
  # can be extended and registered here.
  queue-builder = "io.altoo.akka.serialization.kryo.DefaultQueueBuilder"

  # If set, akka uses manifests to put a class name
  # of the top-level object into each message
  use-manifests = false

  # If set it will use the UnsafeInput and UnsafeOutput
  # Kyro IO instances. Please note that there is no guarantee
  # for backward/forward compatibility of unsafe serialization.
  # It is also not compatible with the safe-serialized values
  use-unsafe = false

  # The transformations that have be done while serialization
  # Supported transformations: compression and encryption
  # accepted values(comma separated if multiple): off | lz4 | deflate | aes
  # Transformations occur in the order they are specified on serialization
  # and reverse order on deserialization. For example: "lz4,aes"
  # lz4 usually offers a good middle ground between size and performance.
  post-serialization-transformations = "off"

  # Settings for aes encryption, if included in transformations AES
  # algo mode, key and custom key class can be specified AES algo mode.
  # The configured key provider class `io.altoo.akka.serialization.kryo.DefaultKeyProvider`
  # derives a key from the configured password and salt.
  # To dynamically provide an aes key extend the `io.altoo.akka.serialization.kryo.DefaultKeyProvider`
  # and configure it here.
  #
  # Example configuration:
  # encryption {
  #   aes {
  #     key-provider = "io.altoo.akka.serialization.kryo.DefaultKeyProvider"
  #     mode = "AES/GCM/NoPadding"
  #     iv-length = 12
  #     # password/salt properties are only required when using the default key provider
  #     password = j68KkRjq21ykRGAQ
  #     salt = pepper
  #   }
  # }

  # Log implicitly registered classes. Useful, if you want to know all classes
  # which are serialized
  implicit-registration-logging = true

  # If enabled, Kryo logs a lot of information about serialization process.
  # Useful for debugging and low-level tweaking
  kryo-trace = true

  # If enabled, Kryo uses internally a map detecting shared nodes.
  # This is a preferred mode for big object graphs with a lot of nodes.
  # For small object graphs (e.g. below 10 nodes) set it to false for
  # better performance.
  kryo-reference-map = true

  # For more advanced customizations the [[io.altoo.akka.serialization.kryo.DefaultKryoInitializer]]
  # can be subclassed and configured here.
  # The preInit can be used to change the default field serializer.
  # The postInit can be used to register additional serializers and classes.
  kryo-initializer = "io.altoo.akka.serialization.kryo.DefaultKryoInitializer"

  # If enabled, allows Kryo to resolve subclasses of registered Types.
  #
  # This is primarily useful when id-strategy is set to "explicit". In this
  # case, all classes to be serialized must be explicitly registered. The
  # problem is that a large number of common Scala and Akka types (such as
  # Map and ActorRef) are actually traits that mask a large number of
  # specialized classes that deal with various situations and optimizations.
  # It isn't straightforward to register all of these, so you can instead
  # register a single supertype, with a serializer that can handle *all* of
  # the subclasses, and the subclasses get serialized with that.
  #
  # Use this with care: you should only rely on this when you are confident
  # that the superclass serializer covers all of the special cases properly.
  resolve-subclasses = false

  # Define mappings from a fully qualified class name to a numeric id.
  # Using ids instead of FQCN leads to smaller sizes of serialized representations
  # and faster serialization.
  #
  # This section is mandatory for idstartegy=explicit
  # This section is optional  for idstartegy=incremental
  # This section is ignored   for idstartegy=default
  #
  # The smallest possible id should start at 20 (or even higher), because
  # ids below it are used by Kryo internally e.g. for built-in Java and
  # Scala types.
  #
  # Some helpful mappings are provided through `supplied-basic-mappings`
  # and can be added/extended by:
  #
  # mappings = ${akka-kryo-serialization.optional-basic-mappings} {
  #   fully.qualified.classname1 = id1
  #   fully.qualified.classname2 = id2
  # }
  #
  mappings {
    # fully.qualified.classname1 = id1
    # fully.qualified.classname2 = id2
  }

  # Define a set of fully qualified class names for
  # classes to be used for serialization.
  # The ids for those classes will be assigned automatically,
  # but respecting the order of declaration in this section
  #
  # This section is optional  for idstartegy=incremental
  # This section is ignored   for idstartegy=default
  # This section is optional  for idstartegy=explicit
  classes = [
    # fully.qualified.classname1
    # fully.qualified.classname2
  ]

  # Note: even though only to be helpful, these mappings are considered
  # part of the api and changes are to be considered breaking the api
  optional-basic-mappings {
    // java
    "java.util.UUID" = 30

    "java.time.LocalDate" = 31
    "java.time.LocalDateTime" = 32
    "java.time.LocalTime" = 33
    "java.time.ZoneOffset" = 34
    "java.time.ZoneRegion" = 35
    "java.time.ZonedDateTime" = 36
    "java.time.Instant" = 37
    "java.time.Duration" = 38

    // scala
    "scala.Some" = 50
    "scala.None$" = 51
    "scala.util.Left" = 52
    "scala.util.Right" = 53
    "scala.util.Success" = 54
    "scala.util.Failure" = 55

    "scala.Tuple2" = 60
    "scala.Tuple3" = 61
    "scala.Tuple4" = 62
    "scala.Tuple5" = 63
    "scala.Tuple6" = 64
    "scala.Tuple7" = 65
    "scala.Tuple8" = 66
  }

  optional-scala2_12-mappings = {
    "scala.collection.immutable.Nil$" = 70
    "scala.collection.immutable.$colon$colon" = 71
    "scala.collection.immutable.Map$EmptyMap$" = 72
    "scala.collection.immutable.Map$Map1" = 73
    "scala.collection.immutable.Map$Map2" = 74
    "scala.collection.immutable.Map$Map3" = 75
    "scala.collection.immutable.Map$Map4" = 76
    "scala.collection.immutable.Set$EmptySet$" = 77
    "scala.collection.immutable.Set$Set1" = 78
    "scala.collection.immutable.Set$Set2" = 79
    "scala.collection.immutable.Set$Set3" = 80
    "scala.collection.immutable.Set$Set4" = 81
    "scala.collection.immutable.ArraySeq$ofRef" = 82
    "scala.collection.immutable.ArraySeq$ofInt" = 83
    "scala.collection.immutable.ArraySeq$ofDouble" = 84
    "scala.collection.immutable.ArraySeq$ofLong" = 85
    "scala.collection.immutable.ArraySeq$ofFloat" = 86
    "scala.collection.immutable.ArraySeq$ofChar" = 87
    "scala.collection.immutable.ArraySeq$ofByte" = 88
    "scala.collection.immutable.ArraySeq$ofShort" = 89
    "scala.collection.immutable.ArraySeq$ofBoolean" = 90
    "scala.collection.immutable.ArraySeq$ofUnit" = 91
  }

  # note: Vector is only available from 2.13.2 and above - for 2.13.0 or 2.13.1 use the optional-scala2_12-mappings
  optional-scala2_13-mappings = {
    "scala.collection.immutable.Nil$" = 70
    "scala.collection.immutable.$colon$colon" = 71
    "scala.collection.immutable.Map$EmptyMap$" = 72
    "scala.collection.immutable.Map$Map1" = 73
    "scala.collection.immutable.Map$Map2" = 74
    "scala.collection.immutable.Map$Map3" = 75
    "scala.collection.immutable.Map$Map4" = 76
    "scala.collection.immutable.Set$EmptySet$" = 77
    "scala.collection.immutable.Set$Set1" = 78
    "scala.collection.immutable.Set$Set2" = 79
    "scala.collection.immutable.Set$Set3" = 80
    "scala.collection.immutable.Set$Set4" = 81
    "scala.collection.immutable.ArraySeq$ofRef" = 82
    "scala.collection.immutable.ArraySeq$ofInt" = 83
    "scala.collection.immutable.ArraySeq$ofDouble" = 84
    "scala.collection.immutable.ArraySeq$ofLong" = 85
    "scala.collection.immutable.ArraySeq$ofFloat" = 86
    "scala.collection.immutable.ArraySeq$ofChar" = 87
    "scala.collection.immutable.ArraySeq$ofByte" = 88
    "scala.collection.immutable.ArraySeq$ofShort" = 89
    "scala.collection.immutable.ArraySeq$ofBoolean" = 90
    "scala.collection.immutable.ArraySeq$ofUnit" = 91
    "scala.collection.immutable.Vector0$" = 92
    "scala.collection.immutable.Vector1" = 93
    "scala.collection.immutable.Vector2" = 94
    "scala.collection.immutable.Vector3" = 95
    "scala.collection.immutable.Vector4" = 96
    "scala.collection.immutable.Vector5" = 97
    "scala.collection.immutable.Vector6" = 98
  }
} 
  """



  val kryo_old = """
    kryo  {
      type = "graph"

      # Possible values for idstrategy are:
      # default, explicit, incremental, automatic
      #
      # default - slowest and produces bigger serialized representation.
      # Contains fully-qualified class names (FQCNs) for each class. Note
      # that selecting this strategy does not work in version 0.3.2, but
      # is available from 0.3.3 onward.
      #
      # explicit - fast and produces compact serialized representation.
      # Requires that all classes that will be serialized are pre-registered
      # using the "mappings" and "classes" sections. To guarantee that both
      # sender and receiver use the same numeric ids for the same classes it
      # is advised to provide exactly the same entries in the "mappings"
      # section.
      #
      # incremental - fast and produces compact serialized representation.
      # Support optional pre-registering of classes using the "mappings"
      # and "classes" sections. If class is not pre-registered, it will be
      # registered dynamically by picking a next available id To guarantee
      # that both sender and receiver use the same numeric ids for the same
      # classes it is advised to pre-register them using at least the "classes" section.
      #
      # automatic -  use the pre-registered classes with fallback to FQCNs
      # Contains fully-qualified class names (FQCNs) for each non pre-registered
      # class in the "mappings" and "classes" sections. This strategy was
      # added in version 0.4.1 and will not work with the previous versions

      #idstrategy = "incremental"
      idstrategy = "automatic"

      # Define a default queue builder, by default ConcurrentLinkedQueue is used.
      # Create your own queue builder by implementing the trait QueueBuilder,
      # useful for paranoid GC users that want to use JCtools MpmcArrayQueue for example.
      #
      # If you pass a bounded queue make sure its capacity is equal or greater than the
      # maximum concurrent remote dispatcher threads your application will ever have
      # running; failing to do this will have a negative performance impact:
      #
      # custom-queue-builder = "a.b.c.KryoQueueBuilder"

      # Define a default size for byte buffers used during serialization
      buffer-size = 4096

      # The serialization byte buffers are doubled as needed until they
      # exceed max-buffer-size and an exception is thrown. Can be -1
      # for no maximum.
      max-buffer-size = -1

      # If set, akka uses manifests to put a class name
      # of the top-level object into each message
      use-manifests = false

      # If set it will use the UnsafeInput and UnsafeOutput
      # Kyro IO instances. Please note that there is no guarantee
      # for backward/forward compatibility of unsafe serialization.
      # It is also not compatible with the safe-serialized values.
      # The unsafe IO usually creates bugger payloads but is faster
      # for some types, e.g. native arrays.
      use-unsafe = false

      # The transformations that have be done while serialization
      # Supported transformations: compression and encryption
      # accepted values(comma separated if multiple): off | lz4 | deflate | aes
      # Transformations occur in the order they are specified
      # post-serialization-transformations = "lz4,aes"
      post-serialization-transformations = "lz4"

      # Settings for aes encryption, if included in transformations AES
      # algo mode, key and custom key class can be specified AES algo mode
      # defaults to 'AES/CBC/PKCS5Padding' and key to 'ThisIsASecretKey'.
      # If custom key class is provided, Kryo will use the class specified
      # by a fully qualified class name to get custom AES key. Such a
      # class should define the method 'kryoAESKey'. This key overrides 'key'.
      # If class doesn't contain 'kryoAESKey' method, specified key is used.
      # If this is not present, default key is used
      #encryption {
      #    aes {
      #        mode = "AES/CBC/PKCS5Padding"
      #        key = j68KkRjq21ykRGAQ
      #        IV-length = 16
      #        custom-key-class = "CustomAESKeyClass"
      #    }
      #}

      # Log implicitly registered classes. Useful, if you want to know all
      # classes which are serialized. You can then use this information in
      # the mappings and/or classes sections
      implicit-registration-logging = true

      # If enabled, Kryo logs a lot of information about serialization process.
      # Useful for debugging and lowl-level tweaking
      kryo-trace = false

      # If proviced, Kryo uses the class specified by a fully qualified
      # class name to perform a custom initialization of Kryo instances in
      # addition to what is done automatically based on the config file.
      # kryo-custom-serializer-init = "CustomKryoSerializerInitFQCN"

      # If enabled, allows Kryo to resolve subclasses of registered Types.
      #
      # This is primarily useful when idstrategy is set to "explicit". In this
      # case, all classes to be serialized must be explicitly registered. The
      # problem is that a large number of common Scala and Akka types (such as
      # Map and ActorRef) are actually traits that mask a large number of
      # specialized classes that deal with various situations and optimizations.
      # It isn't straightforward to register all of these, so you can instead
      # register a single supertype, with a serializer that can handle *all* of
      # the subclasses, and the subclasses get serialized with that.
      #
      # Use this with care: you should only rely on this when you are confident
      # that the superclass serializer covers all of the special cases properly.
      resolve-subclasses = false

      # Define mappings from a fully qualified class name to a numeric id.
      # Smaller ids lead to smaller sizes of serialized representations.
      #
      # This section is:
      # - mandatory for idstrategy="explicit"
      # - ignored   for idstrategy="default"
      # - optional  for incremental and automatic
      #
      # The smallest possible id should start at 20 (or even higher), because
      # ids below it are used by Kryo internally e.g. for built-in Java and
      # Scala types
      #mappings {
      #    "package1.name1.className1" = 20,
      #    "package2.name2.className2" = 21
      #}

      # Define a set of fully qualified class names for
      # classes to be used for serialization.
      # The ids for those classes will be assigned automatically,
      # but respecting the order of declaration in this section
      #
      # This section is ignored for idstrategy="default" and optional for
      # all other.
      classes = [
        "scala.Tuple2",
        "seer.openni.User",
        "scala.collection.mutable.ArrayBuffer",
        "seer.spatial.Vec3",
        "seer.openni.Skeleton",
        "scala.collection.mutable.HashMap",
        "seer.openni.Bone",
        "seer.spatial.Quat"
      ]
  }
  """

  // load the normal config stack (system props, then application.conf, then reference.conf)
  // val regularConfig = ConfigFactory.load();
  // override regular stack with config
  // val combined = config.withFallback(regularConfig);
  // put the result in between the overrides (system props) and defaults again
  // val complete = ConfigFactory.load(combined);

}

