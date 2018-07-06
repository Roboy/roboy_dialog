.. _system_constraints:

Project Constraints
======================

Technical Constraints
----------------------

.. csv-table:: Operating System Constraints
  :header: "Constraint Name", "Description"
  :widths: 20, 40

  "Ubuntu => 16.04", "Default Roboy OS"
  "Java => 1.8.0", "Reasonably recent and stable Java release. Sun.audio was removed with JDK9, thus it will not compile for any newer Java versions."

.. csv-table:: Programming Constraints
  :header: "Constraint Name", "Description"
  :widths: 20, 60

  "IntelliJ IDEA", "Difficulties with importing the project to NetBeans and Eclipse"
  "rosjava", "Due to using both Java and ros"