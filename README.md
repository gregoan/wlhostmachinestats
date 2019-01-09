WLHostMachineStats
------------------

 * Copyright (c) 2016 GREGOIRE Alain
 * Version:  0.3
 * Licence:  "3-clause" BSD based Licence - refer to the files 'LICENSE'
 * Last updated:  01-Jan-2019
 * Home Page:  https://github.com/gregoan/wlhostmachinestats

Introduction
------------
WLHostMachineStats is a small agent (JMX MBean) that runs in every WebLogic Server in a WebLogic domain. It is used to collect statistics (O.S. CPU/Memory/Network usage) from the machine hosting each WebLogic Server instance.
WLHostMachineStats is also useful when employed in conjunction with DomainHealth (https://github.com/ccristian/domainhealth) enabling historical graphs of host machine operating system statistics to be displayed alongside regular WebLogic Server statistics.
WLHostMachineStats is a deployable JEE web-application (WAR archive). It is only supported for WebLogic versions 10.3 or greater, and on host machines running the following operating systems only: 

 * Linux x86 64-bit
 * Linux x86 32-bit
 * Solaris SPARC 64-bit
 * Solaris SPARC 32-bit
 * Solaris x86 64-bit
 * Solaris x86 32-bit

Quick Installation How To
-------------------------
   1. Navigate to this project's 'Download' page and download the zip file: wlhostmachinestats-nn.zip
   2. Unpack the zip file to a temporary directory.
   3. In the "setDomainEnv.sh" and "startNodeManager.sh" scripts of your WebLogic domain, add the following line near the top of both files to ensure that the SIGAR C Library is on the system path whenever the WebLogic servers are started (changing <domainpath> for the real path of your domain).
   
          export LD_LIBRARY_PATH=<domainpath>/lib:$LD_LIBRARY_PATH   
   
   4. Start (or re-start) your WebLogic domain's servers, so that the previous steps, above, take affect. 
   5. From the unzipped directory, deploy wlhostmachinestats-nn.war Web Application to your WebLogic domain, targeted to all the servers in the domain (including both Admin and Managed Servers).
   6. Use a JMX client aware tool to view the host machine statistics exposed by your deployed WLHostMachineStats MBean. For example:
     a) Use WLST with the custom() command and traverse to the MBean using ls() and cd()
     b) Use the JConsole utility shipped in the JDK that WebLogic bundles 
     c) Use DomainHealth (https://github.com/ccristian/domainhealth) to 
     view graphs of these host machine statistics over time. 

NOTE:
-----

WLHostMachineStatistics will do its best to identify the primary network interface on the host machine that is being used by WebLogic, to then monitor and collect network related statistics for. Depending on your host environment, a non-desirable network interface may be picked. To more accurately define what interface to choose, change the values of the "preferred_net_interface_names" field in "WEB-INF/web.xml" file of the WAR application and then re-deploy the web application.

Building From Source
--------------------

This project includes a Maven buildfile in the root directory to enable the project to be completely re-built from source and modified and enhanced where necessary.

The project also includes an Eclipse (OEPE) '.project' Project file, enabling developers to optionally use Eclipse to modify the source (just import WLHostMachineStats as an existing project into Eclipse). 

To re-build the project, first ensure the Java SDK and Maven is installed and their 'bin' directories are present in PATH environment variable, then check the values in the pom.xml file in the project's root directory to ensure this reflects your local WebLogic environment settings. 

Run the following commands to clean the project, compile the source code and build the WAR web-application:

 > mvn clean package
 
Installation
------------

LD_LIBRARY_PATH should be updated adding a directory containing the "*.so" files

For Mac, the property "**-Djava.library.path=<DIRECTORY>**" should be used as LD_LIBRARY_PATH doesn't seem to be working
Edit bin/startWebLogic.sh file and add

  LD_LIBRARY_PATH=${DOMAIN_HOME}/ld_library:${LD_LIBRARY_PATH}
  DYLD_LIBRARY_PATH={LD_LIBRARY_PATH}
          
  export LD_LIBRARY_PATH
  export DYLD_LIBRARY_PATH
  
  ...
  echo "Starting WLS with line:"
  ...
  ${JAVA_HOME}/bin/java ${JAVA_VM} ${MEM_ARGS} **-Djava.library.path=${LD_LIBRARY_PATH}** -Dweblogic.Name=${SERVER_NAME} -Djava.security.policy=${WLS_POLICY_FILE} ${JAVA_OPTIONS} ${PROXY_SETTINGS} ${SERVER_CLASS}
  ...

On Linux, you can simply add in startWebLogic.sh file :

  LD_LIBRARY_PATH=${DOMAIN_HOME}/ld_library:${LD_LIBRARY_PATH}
  DYLD_LIBRARY_PATH={LD_LIBRARY_PATH}
  
  export LD_LIBRARY_PATH
  export DYLD_LIBRARY_PATH

Project Contact
---------------
GREGOIRE Alain (send email to the "gmail.com" email address for gmail user 'gregoire.alain')