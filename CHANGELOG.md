# Changelog

All notable changes to this project will be documented in this file.

## [1.3.1]

### Fixed
- **Block Interaction:** Fixed a bug where interactable blocks with graphical user interfaces (such as Chests and Barrels) could not be grabbed because their UI would open instead.

## [1.3.0]

### Added
- **Encumbrance System:** Object weight now realistically restricts player movement speed, prevents jumping, and reduces camera sensitivity.
- **Exhaustion System:** Holding, dragging, and pulling heavy objects dynamically drains hunger based on physical effort and tension.
- **New Config Options:** Added multiple parameters in the config menu to fine-tune penalties, hunger drain, and grab physics.

### Improved
- **Grab Realism:** Refined applied force physics, improving the sense of weight, elastic tension (tether), and inertia of heavy structures.
- **Client-Server Communication:** More robust network synchronization.