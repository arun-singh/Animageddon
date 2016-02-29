Animageddon
-----------
-----------

This is a multiplayer game (topdown shooter) created during my second year of study as part of a team project.

I was responsible for the GUI (located in Animageddon/src/shared/GUI/).

We were not allowed to use any concrete GUI libraries (Java Swing) so I had to build the features from scratch. 

I decided to model the GUI off of Swing, where every GUI feature is inherited from a Component. This Component consisted of x,y values and a width and height. The Component class could then be used to build rectangles, which in turn were used for creating buttons and menus.

Consistency across menus was key; each menu is inherited from a template, which adheres to a certain colour scheme. This made the overall theme much more professional. 

Main menu
---------
![Main menu](/res/screenshots/MainMenu.png)

Player select
-------------
![Player select](/res/screenshots/PlayerSelection.png)

In game menu
-------------
![In game menu](/res/screenshots/InGameMenu.png)

Instruction
-------------
![Instruction](/res/screenshots/InstructionMenu.png)

IP select
-------------
![Instruction](/res/screenshots/IpSelect.png)

How to run
----------

Using the jar:

Open animageddon-client.jar for a game client that can create and join games.
Open animageddon-server.jar for a dedicated server that can create games.

Manual compiling:

The main class for the client is shared.GameWindow.
The main class for the dedicated server is server.DedicatedServer.

==============
Requirements for compiling
==============

Build path libraries:

common-lang3-3.3jar
lwjgl-2.9.1/jar/lwjgl_util.jar
lwjgl-2.9.1/jar/lwjgl.jar
slick/lib/slick-util.jar

Natives (for LWJGL):

lwjgl-2.9.1/native/* (* = platform, e.g. windows, linux, macosx)

Main methods:

shared.GameWindow (for client)
server.DedicatedServer (for dedicated server)

==============
Starting a server
==============

Using animageddon-client.jar/GameWindow:

1) In the client, you can start a server using the Create Game option in the main menu.
2) You will have to go through two menus, one for class selection and one for the lobby.
3) These menus do not yet have a functional implementation so you can choose any class and click
ready straight away in the lobby to begin the game.
4) The game will start. You are now hosting a game server that other clients can join and you are yourself a client as well.

Using animageddon-server.jar/server.DedicatedServer:

1) Start the jar/execute the main method. The server will start automatically.

* We recommend running the jar from the command line to view debug messages.

==============
Joining a game
==============

Using animageddon-client.jar/GameWindow:

1) Click "Join Game" in the main menu.
2) Enter the IP of the server you would like to join.
3) Click "Continue" and wait.
4) If the connection is successful, you will be spawned in the server.

==========
Other
==========

* There is no mute functionality.
* Some errors will cause the game client to close. See console for details. (These aren't uncaught exceptions)
* As mentioned before, the class select and lobby menus are only visually functional at the moment.

==========
Credit
==========

http://nosoapradio.us - game music
http://freesound.org - game soounds
http://opengameart.org - character images, textures
