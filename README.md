# QuickGame
A quickly developed [Roguelike](https://en.wikipedia.org/wiki/Roguelike)-inspired game.

## License
This project is licensed under the [MIT license](LICENSE.txt).

![Level 5](https://github.com/aaroncarsonart/QuickGame/blob/master/screenshots/level5.png?raw=true "Level 5")

--------------------------------------------------------------------------------------

## Build
Build the project with [Maven](https://maven.apache.org/):

```bash
mvn install
```

## Run
Run the output jar file:

```bash
java -jar target/QuickGame-0.1-SNAPSHOT.jar
```

## How the Game Works

### Objective
Collect all yellow treasure `$` tiles to advance to the next depth. The game ends
when you run out of health or energy.  Treasure is depleted when energy runs out.
See how deep you can get!

### Nagivation and Rules
Navigate the map using the arrow keys.
- Each step consumes 1 energy point, so be strategic about how you move.
- Collect green grass tiles `"` to gain 5 energy.
- Collect blue water tiles `~` to gain a random amount of health. It costs 3 energy to
  move through water tiles.
- Collect white bomb tiles `!` to hit all enemies with a strong attack.
- Bump into red enemies `A` etc to attack them.  Enemies reduce your health!  The higher
  the letter, the stronger the enemy.
- Defeating enemies yields exp.  Gain enough exp to level up, increasing your health
  and attack power.

--------------------------------------------------------------------------------------

![Level 1](https://github.com/aaroncarsonart/QuickGame/blob/master/screenshots/level1.png?raw=true "Level 1")
