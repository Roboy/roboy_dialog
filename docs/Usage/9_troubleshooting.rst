Troubleshooting
===========================

Exception in thread "main" java.lang.OutOfMemoryError: GC overhead limit exceeded
---------------------------------------------------------------------------------------------------------

This means that Java does not have enough heap space to execute. One can resolve this by allowing java to use more heapspace.

If you are running the class via Maven, add ``export MAVEN_OPTS="-Xmx6g"`` to your ``.bashrc`` or equivalent file.

If you are running via IntelliJ and running ``DialogSystem`` as an application, add ``-Xmx6g`` to the VM Options. (Likely a similar process for other IDEs)

> Note: 6g can be replaced with a larger/smaller number. The project has been confirmed to work with 3000 mb (3000m) as one's parameter.


Building Sempre External Dependencies 1.0.0 Fails to Build
-------------------------------------------------------------------------------------------------------------

If this comes up in combination with a ``java.lang.ClassNotFoundException: roboy.dialog.DialogSystem``, then you are likely using the old maven command to build the project. Please use the one found on the installation page. 

ContextIntegrationTest Fails
-------------------------------------------------------------------------------------------------------------

ContextIntegrationTest should have been deprecated and ignored during the testing phase in Maven. If for some reason you have this error, make sure that you are on the latest version of your branch. 

Memory Specific Exceptions
--------------------------------------

Memory has it's own `page`<http://roboy-memory.readthedocs.io/en/latest/Usage/3_troubleshooting.html#possible-common-exceptions> regarding exceptions. 