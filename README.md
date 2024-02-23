# Juice Bottler

This program is a simulation of a multi-threaded orange-processing plant creator. Several plants are created for the purpose of processing oranges for bottling. There are 5 tasks necessary to process an orange.
As such, there are 5 workers created in each plant, with each of these workers operating on a separate thread. After a designated length of time, the plant creator shuts the plants down before displaying the
number of oranges processed by each of the plants.

This program builds using Apache Ant, which may be installed [here](https://ant.apache.org/bindownload.cgi). Once Ant has been installed on your machine, navigate to the main directory of this project and run *ant run* to start the program.
