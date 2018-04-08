

Resources
=========

Resources are located in resources folder and comprise important text files containing necessary inputs for the RDS.

JSON Resources
--------------

- personality files - contain the description of Roboy's personality
- question asking lists - contain the personal questions in the following form:

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

CSV Resources
-------------

- trivia - funny facts Roboy would love to tell you