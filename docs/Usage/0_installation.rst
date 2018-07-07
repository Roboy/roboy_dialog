************
Installation
************

We use Apache Maven build automation tool.

Using command line
==================

Install Maven on the computer.
``sudo apt install maven``

Clone the Dialog Manager repository.
``git clone https://github.com/Roboy/roboy_dialog``

Navigate to the root module.
``cd roboy_dialog``

Download roboy parser.
``git submodule update --init ./roboy_parser``

Install roboy parser as described in the roboy_parser docs or the 'semantic parser' page in 'INTERFACES AND SCOPE'.

Compile the project - Maven will take care of the rest.
``mvn compile``

Execute the project.
``mvn exec:java -Dexec.mainClass="roboy.dialog.ConversationManager"``


Using IDE (Eclipse, Intellij IDEA)
==================================

Clone the Dialog Manager repository.
``git clone https://github.com/Roboy/roboy_dialog``

Now, import Dialog System as a Maven project into the IDE of your choice. Build and execute using ``roboy.dialog.ConversationManager`` as the main class.


Using a telegram bot for I/O
============================

Register a bot as described on the `telegram website`_.
.._telegram website: https://core.telegram.org/bots#3-how-do-i-create-a-bot

Place your telegram-bot authentification token in a JSON-File structured like this:
.. code:: json
    {
    "TOKEN":"AAAAAAAAA:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
    "BOT_USERNAME":"Botname"
    }

Provide the path to this file to the Dialog System via ``TELEGRAM_API_TOKENS_FILE: "/path/to/example.json"`` in ``config.properties``.