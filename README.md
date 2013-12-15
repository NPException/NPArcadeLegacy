ModJam
======

Repository for ModJam December 2013

TODO:
- implement a game
- play game on client, send screen image to server, distribute screen image to clients who are not the player on the arcade
- some some kind of victory state or an onVictory() method, so that some particles could be fired or custom code could be run
- leaderboards? (send player name to game)
- count amount of damage to increase possibility of failing
- fillWithRain(world, x, y, z) -> increase damage if it stands in the rain for too long
- on high damage: distort picture (white noise), miss inputs
- (make it as configurable as possible - render distance and screen receiving distance f.e.)
- make more parts, so you actually need to craft the arcade piece by piece. should be more or less expensive
- make arcade machines dungeonloot
- make arcade machine use coins (custom item)