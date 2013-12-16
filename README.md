# ModJam


Repository for ModJam December 2013

## Ideas to fix bugs
- Lighting of ArcadeTop is messed up:
  In addition to emitting a light level, check the following methods:
  - getAmbientOcclusionLightValue
  - isAmbientOcclusionEnabled
  - getAoBrightness
  - renderStandardBlockWithAmbientOcclusionPartial



## Advanced Game Ideas
- Make a game which teleports the player into the game
  - custom dimension (flat)
  - rendered top-down as pixely graphics on the arcade machine
    which are visible to other players
  - do something special if player dies in the game
  - Rendering on player transition:
    - Fade player into a bunch of particles which then
      fly into the arcade screen at a random order
    - same in reverse when player is getting out
  - "Cursed Arcade Machine" as rare dungeon loot for that matter?
    - boolean flag "isCursed"
    - maybe use enemies and blocks for leveldesign from GhostMod if it is installed?
