# Battle Cats Ultimate++
#### A fork of BCU that extends it by adding more custom mechanics and quality-of-life changes.
## What does this branch has that the original doesn't?
### Mechanics
- Money Steal ability for enemies (Used by giving the enemy "2x money" proc)
- Strong Against/Massive Damage/Resist and their insane counterparts now usable for enemies
- Settable Traits for attacks to target entities independently from the attacker's traits
- Custom Soul Animations
- Minimum Respawn Time for units
- AI Ability
- Weakening/Strengthening Aura
- Descriptions, Icons, and Banners for packs
- ***Multiple Attack Animations for one entity***
- ***Units can have more than one of each specific conditional attack (Revenge, Resurrection, etc (Excluding Counter))***
- Everywhere Door Parameter on stages that lets enemies spawn in any given point of the battlefield (Set by percentage)
- Remote Shield that reduces damage taken from attacks if the attacker is standing at a specific range from the attacked Entity
- RandomUnit (Currently only usable for summon)
- Summon more than one entity in an attack
- Surge Blocker
- ***Wave Mitigator (Which is just a merging Wave Immunity and Wave Block into a single proc, with the proc blocker params)***
- Restrictions can now only restrict a specific form rather than all forms
- NAND/NOR operations in find pages
- Custom SFX for attacks
- Entry animations
- ***Custom Background Effects***
- Row 2 only restriction
- Unit Bases
- TBA applied on spawn (Using a negative number)
### QoL changes
- A much slicker UI (Thanks to Hect0x1 for it)
- Can't deploy any more text line when you reach deploy limit
- Units now actually flip to face the right way when their speed ends up being a negative number
### Bugs Fixed here that remain unfixed in the original repo
- Strong Against/Massive Dmg/etc don't work via adv. trait targetting
- Cursed/Sealed units become unaffected by trait-targetting enemies
- Procs from older packs aren't distributed properly on attacks without common proc

## What does the original BCU repo branch has that this fork doesn't?
- Annoying pop-up when right-clicking on buttons
- Button Filter
- Android Support

*Bolded ones imply that implementing these required restructures that make them uncompatible with official BCU branch*
# *PACKS MADE OR SAVED USING THIS FORK WILL BE INCOMPATIBLE TO USERS USING THE MAIN BCU*
## But packs built from the main repo can be converted into this one, so it's a one-way road.
