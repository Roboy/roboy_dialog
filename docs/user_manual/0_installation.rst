.. _Installation:

************
Installation
************

In order to install the dialog system, one must clone it from GitHub::

    git clone https://github.com/Roboy/roboy_dialog -b master --recursive

A working version of Maven is required to build and resolve dependencies. Please refer to the ``README.md`` of `master <https://github.com/Roboy/roboy_dialog/blob/master/README.md>`_ or `devel <https://github.com/Roboy/roboy_dialog/blob/devel/README.md#installation>`_ for further installation instructions.

Changing the default configuration, for example the input/output is explained in :ref:`Configuration`.


.. _Optional steps:

Optional steps
==============

Telegram deployment
-------------------

1. Register a bot as described on the `telegram website <https://core.telegram.org/bots#3-how-do-i-create-a-bot>`_.

Place your telegram-bot authentification token in a JSON-File as described in :ref:`JSON Resources`.

Configure the Dialog System to use file and to interact with the world via telegram as described in :ref:`Configuration`.

