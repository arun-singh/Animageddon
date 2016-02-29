Animageddon
-----------
-----------

This is a multiplayer game created during my second year of study as part of a team project.

I was responsible for the GUI (located in Animageddon/src/shared/GUI/).

We were not allowed to use any concrete GUI libraries (Java Swing) so I had to build the features from scratch. 

I decided to model the GUI off of Swing, where every GUI feature is inherited from a Component. This Component consisted of an x,y values and a width and height. The Component class could then be used to build rectangles, which in turn were used for creating buttons and menus.

Consistency across menus was key; each menu is inherited from a template, which adheres to a certain colour scheme. This made the overall theme much more professional. 
