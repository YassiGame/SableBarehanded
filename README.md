# Sable: Barehanded

### Grab and rotate Sable sub-levels using your bare hands.

## For Developers

Other mods can use the API to intercept, force, or react to player interactions.

### Setup (build.gradle)

Add the Modrinth Maven repository and the Sable: Grab dependency to your `build.gradle`:

```gradle
repositories {
    maven {
        name = "Modrinth"
        url = "https://api.modrinth.com/maven"
        content {
            includeGroup "maven.modrinth"
        }
    }
}

dependencies {
    compileOnly "maven.modrinth:sablebarehanded:<version>"
}
```

### Examples using the API

```
import dev.juaanp.sablebarehanded.api.SableBarehandedAPI;

// Check if a player is grabbing something
boolean isGrabbing = SableBarehandedAPI.isPlayerGrabbing(player);

// Get the currently grabbed sub-level
ServerSubLevel subLevel = SableBarehandedAPI.getGrabbedSubLevel(player);

// Force the player to drop the object
SableBarehandedAPI.forceDrop(player);
```

#### Cancel a grab

```
SableBarehandedEvents.onBeforeGrab((player, subLevel) -> {
if (subLevel.getMassTracker().getMass() > 50000) {
player.sendSystemMessage(Component.literal("This object is too heavy to grab."));
return false; // Cancels the grab
}
return true; // Allows the grab
});
```
#### React to Grab / Release

```
SableBarehandedEvents.onGrab((player, subLevel) -> {
System.out.println(player.getName().getString() + " grabbed a sub-level!");
});

SableBarehandedEvents.onRelease((player, subLevel) -> {
System.out.println("Sub-level released.");
});
```
