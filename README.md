This is a simple implementation of John Conway's game of Life, written
in Java.

Just run the life script. If you would like to change the parameters
(such as the size of the simulation) then edit the code and recompile.

The menu bar allows you to exit, start over, and change the colours and
speed.

You can import a life pattern from the command line (simply supply the
filename as an argument to the script). A collection of Life patterns
(grab the lifep.zip file), and alternative applications,
are available from the following URL:

https://www.ibiblio.org/lifepatterns/

You can adjust the size of the cells, the size of the board, and the gap
between cells. This will be necessary to read some of the larger patterns.
For example, if you want to load THINRAKE.LIF from the lifep.zip file, try

./life -s 1 -g 0 -b 1800 THINRAKE.LIF

Where the flags set the size of each cell to 1 pixel, the gap between cells
to zero, and the board to be 1800 cells square.
