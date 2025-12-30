# Waila for BTW

## 2.0.4
* Adapted to 3.0.0 Release
* Greatly improved the accuracy of mod names
* ModMenu adaptation has been added
* Added possession info by Inf1nlty
* Temporarily fixed an issue where the siding blocks name was displayed incorrectly
* Fixed an issue where the sky color was incorrect when pointing to sparse loose grass block

---

## 2.0.3 by Inf1nlty
* Both the Brick Oven and Loose Brick Oven now display their burn times. (Also supports food burnt time display added by Nightmare Mode).
* Added a display for zombie villager conversion time.
* Added a display for chicken egg-laying time.
* Added a prompt when wolves are well-fed and can poop.
* Added display of animal hunger levels.
* Added breeding cooldown and growth time displays for animals.

---

## 2.0.2
* Now supports fetching the mod name from the resource domain, and displays "Minecraft" if the fetch fails.
* Modified the logging implementation, which no longer relies on third-party libraries.
* Fixed an issue with client-server communication.

---

## 2.0.1
* Separate the content of the client, so that the server will not crash when running this mod
* Added show mods configuration item
* Added show icon configuration item
* Added lerp factor configuration item (only for editing in the configuration file, 100 is for the off animation)
* Added show armor (configurable)
* Added show attack (configurable)

---

## 2.0.0
* Re port from[GTNH Waila](https://github.com/GTNewHorizons/waila)
* Adapted BTW and removed adaptation to other mods
* Added EMI adaptation and removed NEI adaptation
* Added some tooltip animation effects
* Added destruction progress bar
* Added campfire, oven, torch information display
* Added spider message display
* Due to the lack of Namespace, the mod name will only display "Better Than Wolves" for the time being.
* When the boss's health bar appears, the tooltip will be automatically moved down