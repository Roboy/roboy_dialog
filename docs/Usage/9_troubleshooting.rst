Troubleshooting
===========================

Exception in thread "main" java.lang.OutOfMemoryError: GC overhead limit exceeded
---------------------------------------------------------------------------------------------------------

This means that Java does not have enough heap space to execute. One can resolve this by allowing java to use more heapspace.

If you are running the class via Maven, add ``export MAVEN_OPTS="-Xmx3000m"`` to your ``.bashrc`` or equivalent file.

If you are running via IntelliJ and running ``DialogSystem`` as an application, add ``-Xmx3000m`` to the VM Options. (Likely a similar process for other IDEs)

> Note: 3000 can be replaced with a larger/smaller number. Everything however has been confirmed to work with 3000. 


Building Sempre External Dependencies 1.0.0 Fails to Build
-------------------------------------------------------------------------------------------------------------

If this comes up in combination with a ``java.lang.ClassNotFoundException: roboy.dialog.DialogSystem``, then you are likely using the old maven command to build the project. Please use the one found on the installation page. 

