.. _logpolicy:

***********************
Logging Policy
***********************

Loggers
=======================

Currently the system relies on three different logging systems. The end user may configure the level of logging in Dialog's ``Config.Properties`` file.

.. seealso:: 

Dialog
-----------------------

Dialog uses the Log4J, which is defined by the following levels. 

.. hlist::
    :columns: 3

    * OFF
    * FATAL
    * ERROR
    * WARN
    * INFO
    * DEBUG
    * TRACE
    * ALL

.. seealso:: 

    See `Log4J Level Page <https://logging.apache.org/log4j/2.x/manual/customloglevels.html>`_ for more information on these levels.

Parser
-----------------------

Parser uses a custom logging utility writen by `Percy Liang <https://github.com/percyliang/fig>`_. It has further been customized to have logging levels. Currently there are three levels.

.. hlist::
    :columns: 3

    * OFF
    * WARN
    * ALL

Memory
-----------------------

Memory uses the default Java Logger.

.. hlist::
    :columns: 3

    * SEVERE
    * WARNING
    * INFO
    * CONFIG
    * FINE
    * FINER
    * FINEST

.. seealso:: 

    See official `Javadoc levels  <https://docs.oracle.com/javase/8/docs/api/java/util/logging/Level.html>`_ page for more information on these levels.

Level Policy
=============================

Developers should use a standardized policy when it comes to defining the logging. This helps keep the log consistent.


============  ============  ============  ============  ============  ============  ===================================
   Dialog                               Memory                   Parser             Description
--------------------------  --------------------------  --------------------------  -----------------------------------
Level         Method        Level         Method        Level         Method        
============  ============  ============  ============  ============  ============  ===================================
FATAL         lg.fatal()    SEVERE        lg.severe()   FATAL         lg.fail()     Problem requiring module to shutdown immidiately
ERROR         lg.error()    -             Use WARNING   ERROR         lg.error()    Problem that requires recommends a shutdown, but is not forcibly required
WARN          lg.warn()     WARNING       lg.warning()  WARN          lg.warning()  Unexpected behaviour that should be corrected, but is handlable for the program
INFO          lg.info()     INFO          lg.info()     INFO          lg.log()      Information regarding the usage of module (e.g. Config Choices, Major Modules Loaded)
-             -             CONFIG        lg.config()   -             -             Avoid using this level
DEBUG         lg.debug()    FINE          lg.fine()     DEBUG         lg.dbg()      Information not directly relevant to execution of program, unless debugging. This includes tests and parsing details.

.. note:: Refrain from using any lower levels