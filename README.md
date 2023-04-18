# Battle Cats Ultimate++
#### A fork of BCU that extends it by adding more custom mechanics and quality-of-life changes.
## What does this branch has that the original doesn't?
### Mechanics
- Money Steal ability for enemies (Used by giving the enemy "2x money" proc)
- Strong Against/Massive Damage/Resist and their insane counterparts now usable for enemies
- Settable Traits for attacks to target entities independently from the attacker's traits (This replaces Consider/Ignore traits toggle, doing it's purpose with more flexibility)
- Minimum Respawn Time for units on stages
- AI Ability
- Weakening/Strengthening Aura that affects all enemies/units in a given range
- Descriptions, Icons, and Banners for packs
- ***Multiple Attack Animations for one entity***
- ***Units can have more than one of each specific conditional attack (Revenge, Resurrection, etc (Excluding Counter))***
- Everywhere Door Parameter on stages that lets enemies spawn in any given point of the battlefield (Set by percentage)
- Remote Shield that reduces damage taken from attacks if the attacker is standing at a specific range from the attacked Entity
- RandomUnit
- Summon can now summon more than one entity in an attack, and prices from the summoner can be passed to summoned entities
- Surge Blocker/Mitigator
- ***Wave Mitigator (Which is just merging Wave Immunity and Wave Block into a single proc, with the proc blocker params)***
- Restrictions can now only restrict a specific form rather than all forms
- Custom SFX for attacks
- Entry animations
- ***Custom Background Effects***
- Row 2 only restriction
- Higher enemy limit cap for custom stages (100)
- Unit Bases
- TBA applied on spawn (Using a negative number)
- Rage (Enraged entities' attacks hit both enemies and allies on the attack area)
- Hypnosis (Entity will turn against its allies)
- Super Talents for custom units with flexible level setting
- Target Only surge attackers have their surges restrained to hit the trait they target only
- Hardcap unit's total max level to 200 (Levels above 200 don't increase stats due to level curve anyway)
- Increases flexibility of Dojo timer, allowing you to also set how many seconds should a trial last
### QoL changes
- A much slicker UI (Thanks to Hect0x1 for it)
- Can't deploy any more text line when you reach deploy limit
- Units now actually flip to face the right way when their speed ends up being a negative number
- Multiple lines are easier to set up in descrpitions, and are no longer limited to 4
- Manual frame change on Maanim Edit Page, auto-update MaEP slider when increasing/decreasing anim length
- Animations mapped to editable packs can be modified in the editor
- Unrestricted Base Image size (128x256 is still recommended though)
- Packs can have Icons and Banners
- Easier access to multi-language description changing to localize packs faster
- Display brackets [] on unit's HP to indicate their barrier is active
- Time Alive on entity statistics, Amount spawned on total unit statistics
- Redo undone changes
- Overhaul Advanced Anim Edit page, adding an option to polish, trim, and add/substract keyframes to animations
- Add X/Y values on Position/Pivot/Scale columns in mamodel editor so they can be edited together
- Add Amount Spawned to Total Damage Table, Time Alive to Entity Statistics Table, and Total Enemy Statistics table (Also improves small screen battle page)
- Allow the user to register enemies and units as favorites and filter them as such
- Replace allow custom button with specific pack filter, doing what it does and more
- (Unstable) Pack-Merging feature
- Redo, Hotkeys for Undo/Redo (Ctrl+z/Ctrl+y)
- Don't display + level for units whose max + level is 0 when adjusting level
- NAND/NOR operations in find pages
### Bugs Fixed here that remain unfixed in the original repo
- Cursed/Sealed units become unaffected by trait-targetting enemies
- Procs from older packs aren't distributed properly on attacks without common proc
- Enemy descriptions get cut
- Pasting Mamodel parts breaks parent modification in maanims
- Actual Z-Order value display actually isn't the actual Z-Order
- Damage dealt to bases is not added to entity's damage output
- Typing more than 20 values to a custom level curve crashes BCU
- Followup stage chance field doesn't get updated when equalizing followup chances
- Replays with deleted pack stage crash BCU
- Defeat theme doesn't play if losing after using a continue
- UI Bugs when moving through replays:
- - Entity data tables don't get updated to reflect the data of the frame the replay was set to
- - Enemy spawn table doesn't re-add enemies if going back to a frame occurring before they spawned
- - Total Damage Table bricks itself (also happens when continuing battle from a replay)
- Config saves even when it is chosen not to save
- The value of "Use Pack's Catcombos" is not stored
- Armor Break damage isn't properly calculated when adding to entity's damage dealt statistic
- Non-Latin Characters didn't save as intended on replays
- BCU crashes when entering without an internet connection with the update old music toggle off
- Attack frame fill in show axis stays on for less than a frame, and vanishes even when the battle is paused
## What does the original BCU repo branch has that this fork doesn't?
- Annoying pop-up when right-clicking on buttons
- Button Filter
- Android Support
- Name Edit Mode and Tooltip Edit Mode
- BC-Accurate surge for target only units (aka Backhoe TF)

*Bolded ones imply that implementing these required restructures that make them uncompatible with official BCU branch*
# *PACKS MADE OR SAVED USING THIS FORK WILL BE INCOMPATIBLE TO USERS USING THE MAIN BCU*
## But packs built from the main repo can be converted into this one, so it's a one-way road.
### Also replays made on this fork will not display correct results on main BCU branch
