*********
Resources
*********

For the ``config.properties`` resource, please see :ref:`Configuration`.

.. _JSON Resources:

JSON Resources
==============

- personality files - contain the description of Roboy's personality::

    {
      "initialState": "stateIdentifier",
      "states": [
        {
          "identifier": "stateIdentifier",
          "implementation": "roboy.dialog.states.StateImplementation",
          "transitions": {
            "stateTransition": "anotherStateIdentifier"
          },
          "parameters": {
            "stateParameter": "someStringParameter"
          }
        }
      ]
    }

- question asking lists - contain the personal questions in the following form::

    "INTENT": {
      "Q": [
         "Question phrasing 1",
         "Question phrasing 2",
         "Question phrasing 3"
      ],
      "A": {
         "SUCCESS": [
            "Possible answer on success 1",
            "Possible answer on success 2"
            ],
            "FAILURE": [
               "Possible answer on failure"
            ]
      }
      "FUP": {
        "Q": [
          "Possible question to update the existing information"
        ],
        "A": [
          "Possible answer to the input"
        ]
      }
    }

- telegram tokens - contain tokens for registered telegram bots to allow the Dialog System to connect to the telegram API::

    .. code:: json
        {
        "TOKEN":"AAAAAAAAA:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
        "BOT_USERNAME":"Botname"
        }


CSV Resources
=============

- trivia - funny facts Roboy would love to tell you in the following form:

    keyword;Reddit;The sentence contining the particular fact with regard to the keyword

.. warning::

    There is no positive or negative evidence that the trivia facts work when omitting "Reddit" in the middle!


BIN Resources
=============

- BIN files contain the models for the Roboy Semantic Parser

XML Resources
=============

- contains the configuration for the Roboy Dialog Logger where you can set the logger scope and the means of output

