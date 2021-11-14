
=== Features ===

1. Level transition
2. Squarecat
3. Score
4. Save/Load


=== Design patterns ===

1. Memento: 
/* memento was implemented also using Prototype design pattern, since the
 * copy() method was useful to save the current state of the game
 */

- Memento
- GameCaretaker
- LevelMemento (nested inside LevelImpl class)
- GameEngineMemento

2. Prototype
- Prototype
- Classes that implemented: 
	- KinematicState
	- Vector2D
	- AxisAlignedBoundingBoxImpl
	* all entity classes
	* all behaviour strategy classes
	* all collision strategy classes

3. Observer
- EntityListener: LevelImpl
- EntityNotifier: DynamicEntity

3. Strategy
- SquarecatBehaviourStrategy
- SquarecatState

4. Factory
- SquarecatFactory