# ArcadeMod

A small mod, which brings an arcade machine into the game.
Said arcade machine is able to run user created games and programs.

For example Mr_okushamas excellent implementation of a NES emulator!
Play Super Mario in Minecraft yay :D


## Advanced Game Ideas
- Make a game which teleports the player into the game
  - custom dimension (flat)
  - rendered top-down as pixely graphics on the arcade machine
    which are visible to other players.
    Two possible ways to do that:
    - either generate custom sprites for each Block, Item and Entity
      (would be cooler IMO)
    - or just use GL11 rendering code for a top down view
  - More on the sprite/tile thing:
    - generate them on the fly just as they need to be rendered first.
      (would possibly lead to very long startup times if many mods are installed)
  - do something special if player dies in the game
  - Rendering on player transition:
    - Fade player into a bunch of particles which then
      fly into the arcade screen at a random order
    - same in reverse when player is getting out
  - "Cursed Arcade Machine" as rare dungeon loot for that matter, or suck player in when hitting arcade to often?
    - boolean flag "isCursed"
    - maybe use enemies and blocks for leveldesign from GhostMod if it is installed?
