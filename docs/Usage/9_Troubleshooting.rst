Troubleshooting
===========================

Dialog Specific Exceptions
---------------------------------------------------------------------------------------------------------

Exception in thread "main" java.lang.OutOfMemoryError: GC overhead limit exceeded
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This means that Java does not have enough heap space to execute. One can resolve this by allowing java to use more heapspace.

If you are running the class via Maven, add ``export MAVEN_OPTS="-Xmx6g"`` to your ``.bashrc`` or equivalent file.

If you are running via IntelliJ and running ``DialogSystem`` as an application, add ``-Xmx6g`` to the VM Options. (Likely a similar process for other IDEs)

.. note:: 6g can be replaced with a larger/smaller number. The project has been confirmed to work with 3000 mb (3000m) as one's parameter.

Gnutls Handshake Fails
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If you get an error similar to that below, it means that your internet is working. Please check that your internet connection works.

.. code-block:: bash
  
  --- exec-maven-plugin:1.6.0:exec (Pull dependencies) @ roboy-parser-nonmaven-deps ---
  ===== Downloading roboy: Roboy extra utilities (need to compile)
  Cloning into 'fig'...
  fatal: unable to access 'https://github.com/percyliang/fig/': gnutls_handshake() failed: Error in the pull function.
  [ERROR] Command execution failed.
          org.apache.commons.exec.ExecuteException: Process exited with an error: 1 (Exit value: 1)
          at org.apache.commons.exec.DefaultExecutor.executeInternal(DefaultExecutor.java:404)

          .................................

          at org.codehaus.plexus.classworlds.launcher.Launcher.main(Launcher.java:356)
  [INFO] ------------------------------------------------------------------------
  [INFO] Reactor Summary:
  [INFO] 
  [INFO] Sempre External Dependencies ....................... FAILURE [  0.892 s]
  [INFO] Roboy Semantic Parser .............................. SKIPPED
  [INFO] Roboy Semantic Parser Modules ...................... SKIPPED
  [INFO] Roboy Memory ....................................... SKIPPED
  [INFO] Roboy Dialog System ................................ SKIPPED
  [INFO] Roboy Dialog System Modules ........................ SKIPPED
  [ERROR] Failed to execute goal org.codehaus.mojo:exec-maven-plugin:1.6.0:exec (Pull dependencies) on project roboy-parser-nonmaven-deps: Command execution failed. Process exited with an error: 1 (Exit value: 1) -> [Help 1]


Building Sempre External Dependencies 1.0.0 Fails to Build
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If this comes up in combination with a ``java.lang.ClassNotFoundException: roboy.dialog.DialogSystem``, then you are likely using the old maven command to build the project. Please use the one found on the installation page. 

ContextIntegrationTest Fails
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

ContextIntegrationTest should have been deprecated and ignored during the testing phase in Maven. If for some reason you have this error, make sure that you are on the latest version of your branch. 

Memory Specific Exceptions
--------------------------------------

Memory has it's own `page <http://roboy-memory.readthedocs.io/en/latest/Usage/3_troubleshooting.html#possible-common-exceptions>`_ regarding exceptions. 

Github
--------------------------------

If you encounter an error that has not been addressed, feel free to check the issues section of `Dialog and NLU <https://github.com/Roboy/roboy_dialog/issues>`_, as well as that of `Memory <https://github.com/Roboy/roboy_memory/issues>`_, on Github. There, one can check if the issue has been already been reported and if there is a fix. If you still can't find an answer to your question, create a Github issue, so that the dev team is notified and can help.
