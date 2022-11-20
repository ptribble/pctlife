This is a trivial hacked together in half an hour implementation of
John Conway's game of Life.

Just run the life script. If you would like to change the parameters
(such as the size of the simulation) then edit the code and recompile.

Updated version 1.1 adds the menu bar, allowing you to exit, start
over, and change the colours.

Updated version 1.2 allows the import of a life pattern, from the
command line (simply supply the filename as an argument to the
script). You can also modify the speed of the simulation, the
default speed is faster, and performance is much improved. A collection
of Life patterns (grab the lifep.zip file), and alternative applications,
are available from the following URL:

http://www.ibiblio.org/lifepatterns/

Updated version 1.3 allows running as a java applet - see the
lifeapplet.html file.

Version 1.4 is fully packaged, including a custom application icon and
desktop file

The applet has now been removed, as nothing on the modern web will run it.

You can adjust the size of the cells, the size of the board, and the gap
between cells. This will be necessary to read some of the larger patterns.
For example, if you want to load THINRAKE.LIF from the lifep.zip file, try

./life -s 1 -g 0 -b 1800 THINRAKE.LIF

Where the flags set the size of each cell to 1 pixel, the gap between cells
to zero, and the board to be 1800 cells square.
