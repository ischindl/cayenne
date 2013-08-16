cayenne
=======

Mirror of Apache Cayenne

Created new branch "eclipse-plugin" from trunk
- added eclipse directory with eclipse-plugin projects 
- added p2site directory - contains maven file for build eclipse p2-site from libraries needed by cayenne
                         - use p2-maven-plugin from http://projects.reficio.org/p2-maven-plugin/1.0.0/manual.html

For build eclipse plugin are needed 2 steps:
1. build cayenne - "mvn clean install" from root directory - build of p2site is added into this step
2. build eclipse plugin - "mvn clean install" from eclipse directory - build plugin, feature and p2 repository

After this two steps in directory eclipse/cayenne-modeler-eclipse-repository/target/repository 
is p2-site with cayenne-modeler-eclipse-plugin version 3.2M1-SNAPSHOT

This directory structure was added into STABLE-3.1 branch too.
