Timeline OSC.

Requires:
* J2sdk 1.4.2 or 1.5.0 (others untested)
* a recent version of ant (>1.5 )

Notes: 
* There are problems running this under compiz on linux - you will have to turn compiz off to make it work.
* Please refer to the site for instructions.

Unpack:
* unpack Timeline_*.zip  to INSTALL_DIR (arbitrary).
* unpack timelinerc.tar.gz to CONFIG_DIR (recommended (~/.timelinerc)).
* to use compiler, change the classpath variable in config.xml in CONFIG_DIR to point to INSTALL_DIR/timeline/Timeline/classes

Compiling:
* cd to INSTALL_DIR.
* modify build.xml properties at the top to point config.dir to CONFIG_DIR and javac.lib to point to java_home/lib/tools.jar
* type: 
ant compile 

Running:
* type:
ant run

Feedback / Contributors welcome :)
Feel free to contact me at timeline@silicontransit.com or leave comment on the site.

regards,
Rob Munro.
