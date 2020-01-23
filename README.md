#### TrackView A Java ADS-B Track Display Program using MySQL
This is a simple viewer that allows you to see the tracks visually on a 2D display. When started, the program reads a configuration file, and a map file in the directory it is run from. It then connects to the database, and decodes and displays the data. The database is populated with the ```ADSBMySQL``` application.

![My image](https://raw.githubusercontent.com/srsampson/TrackView/master/screenshot.png)

The ```map.dat``` file is Basestation compatible. So you can use Jordan's Outline Maker Web Page to create maps:

```http://acme.com/jef/outlines/```

There is one caveat though, that the data has to have a ```.0``` and not just a ```.``` which I think causes me problems, and I note it here so I won't forget.

Also, note that due to the mapping algorithm used, that you can't put in worldwide data points. Try to limit your maps to the local area. This is because the map globe is transparent. If you have data points on the opposite side of the Earth, they will plot backwards on your side of the world.

A track is made-up of a speed vector showing which way the track is heading, and the length is where the target will be in X minutes if it doesn't maneuver. X being a configuration parameter. Also, some echo dots showing where it has been for the last X minutes (also configurable). The track data block first line shows the six (6) hex-digit Mode-S ICAO code followed by the callsign in parenthesis if it has been received. The second line shows the Flight Level or QNH altitude, followed by the groundspeed. Depending on the Transition Altitude configured, the Altitude will have an ‘F’ for flight level, or an ‘A’ for QNH altitude. If the groundspeed has a ‘*’ after it, then the groundspeed and groundtrack were both computed internally, because the aircraft did not transmit it. The third line contains the Mode-3A squawk of the track, if it has been received, and then the vertical velocity.

The vertical velocity is + for up and - for down, and is reported only if it is greater than 192 feet per minute or greater. This is to cut down on the up and down during turbulence.

The little diamond connected to the speed vector, is the last position received. If the target fades-out, then the track block will dim after 1.5 minutes, and then be deleted after 2 minutes (adjustable). The diamond will go gray if the track quality goes below 3. If a target lands, then + sign for present position is shown, and the groundspeed is placed to the right of that. No other data is shown while on the ground.

If a track has an alert, then an orange symbol is put above the diamond. A ‘P’ symbol stands for “Position Ident,” a ‘E’ symbol stands for “Emergency,” and a ‘C’ symbol stands for “Mode-3A Code Change.” With the 'E' symbol the track block will also display a blinking 'HIJAC' for 7500, 'RADIO' for 7600, and 'EMERG' for 7700 squawks.

You can have multiple config files, and select a map at startup, that are different from the default. These files should still be in your home directory though. To use another config file use the “-c” command line option, and for a different map, use the “-m” option.

For example, I have one viewer setup for 12,000 feet and above, and another viewer set for targets below
12,000 feet. This is one of the main concepts, that you can have multiple windows open, and all are listening to the database updates.

##### Options
If you turn off the Track Block data, then when you mouse over the target it will display the Track Data. If you Left click on the target, the track block will move counter-clockwise to each of the quadrants, and then back to automatic positioning again.

Right mouse click centers on that point. Mouse wheel controls zoom in and out.

The Conflict Alert is probably not very useful below 6000 feet, as aircraft take-off and land, and are close too each other.

![My image](https://raw.githubusercontent.com/srsampson/TrackView/master/conflict.png)

##### Display Options
It might not be obvious, but some of the buttons act like toggle switches. If you left mouse click the bottom half of the button, the number decreases, and the top half causes an increase. The Map pulls up three options (OBJ, NAM, VEC) but only the VEC (vectors) has been mechanized so far. The other two are going to be Special Points and Navigation Objects, and an option to turn their Name on.

##### Basestation Map Data Format
```
{Danger Areas}
$TYPE=11
; EGD001
50.32167+-5.51167 50.40000+-5.65000
50.53333+-5.56667 50.65833+-5.40000
50.71667+-5.20833 50.64167+-5.07500
50.32167+-5.51167 -1
```
We throw out the comments (';' lines), and labels "{" "}" ```Danger Areas``` for example, convert the ```$Type``` to a color mapping, and then store each lat/lon as a line segment.

The ```$Type``` command is a pen-down, while the -1 is a pen-up.

A line is drawn between each coordinate.
