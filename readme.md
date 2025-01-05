# Minesweeper Project

This project is part of my learning in Kotlin.

For now, the game happens through inputs in the console, and the Minesweeper field is displayed in the console. In a future version, I hope to create a fully functional app.

I made some adjustments compared to the original game. For example, I created a routine called randomBombsPlacements() that prevents a bomb from being triggered on the first move and ensures that none of the spaces around the first move contain a bomb. This allows for a larger area to be explored on the first move.

Developing this game was a great opportunity to practice some Kotlin concepts and also some well-known algorithms, such as Breadth First Search, which was responsible for creating the flood effect when the player uncovers a cell that has neither a bomb nor a hint.