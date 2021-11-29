
Just Enough Effect Descriptions of JEED is a JEI plugin that provides useful information regarding status effects.

The mod adds a new Effects recipe category accessed either by clicking any of the newly added status effect icons on the JEI item screen or by clicking an active effect from its box on the inventory screen.

Here you'll be able to view information regarding such status effect such as items that can provide it or its description as well as which mod it's from or its effect color.


The mod automatically adds all registered status effect to its page, however modded effects won't had a description.

Modders can easily add such descriptions to their effect by adding the string "effect.[mod_id].[effect_name].description" to their language file



Since the mod can only automatically detect food items as effect providing ones, mod developer can also easily add support to their custom effect or effect providing items by adding some recipes into their mod.


To do so one can simply use one of the two newly added recipes categoris:
- "jeed:effect_provider", which allows one to specify an item that can provide an effect in the following format:
{
    "type": "jeed:effect_provider",
    "effect":{
        "id": "minecraft:haste"
    },
    "providers": [
        {
            "item": "minecraft:beacon"
        }
    ]
}

- "jeed:potion_provider", which allows one to specify an item that can contain any registered potion like tipped arrows for example:
{
    "type": "jeed:potion_provider",
    "providers": [
        {
            "item": "minecraft:splash_potion"
        },
        {
            "item": "minecraft:tipped_arrow"
        }
    ]
}
with this last one you can also specify an optional Potion parameter to have your items be registered for only that particular potion like this
"potions": [
    {
        "id": "minecraft:slowness"
    }
]



