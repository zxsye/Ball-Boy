
=== Features Implemented ===

1. Level transition
2. Squarecat
3. Score - each enemy killed by squarecat will yield 100 pts.
However, if the configuration file does not specify the colours being tracked in each level,
then an enemy might not yield points.
- RED, BLUE, GREEN point systems for their corresponding enemy colours.
4. Save/Load - there may be slight lags when pressing 'q' and 's' for loading and saving, respectively.


=== Design patterns ===

1. Memento: 
/* memento was implemented also using Prototype design pattern, since the
 * copy() method was useful to save the current state of the game
 */

- Memento.java
- GameCaretaker.java
- LevelMemento (nested inside LevelImpl class)
- GameEngineMemento.java

2. Prototype
- Prototype.java
- Classes that implemented: 
	- KinematicState
	- Vector2D
	- AxisAlignedBoundingBoxImpl
	* all entity classes
	* all behaviour strategy classes
	* all collision strategy classes

3. Observer
- EntityListener.java: implemented by LevelImpl
- EntityNotifier.java: implemented by DynamicEntity

4. Strategy
- SquarecatBehaviourStrategy.java
- SquarecatState.java
- EnemyCollisionStrategy.java

5. Factory
- SquarecatFactory.java

=== Using the configuration file ===
To change the configuration file, you must edit the App.java class. 
Change the string in line 42 to whichever file you want to use instead.

- Each level configuration file requires the same as the provided code with additional:

1. "squarecat" within each level, e.g.:
"squarecat": {
        "type": "squarecat",
        "heroX": 150.0,
        "heroY": 300.0,
        "height": 20.0,
        "image": "squarecat.png"
      },

2. "scoreColours" within each level, indicating which RBG colours to track. e.g.:
"scoreColours": ["RED", "GREEN", "BLUE"]
