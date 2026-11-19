package com.android.zodiachue.helper

import com.android.zodiachue.data.ColorSwatch
import com.android.zodiachue.data.DailyHueSnapshot
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

object DailyHueEngine {

    private data class HuePreset(
        val name: String,
        val hex: String,
        val accentName: String,
        val accentHex: String,
        val baseName: String,
        val baseHex: String,
        val avoidName: String,
        val avoidHex: String,
        val tagline: String,
        val mood: String,
        val wear: String,
        val avoidNote: String,
        val prompt: String
    )

    private val fireHues = listOf(
        HuePreset("Golden Yellow", "#FFD700", "Warm Cream", "#FFF3C4", "Obsidian", "#1B1E2E", "Dull Charcoal", "#4A4E5A", "Radiate warmth and effortless leadership.", "Elevates optimism, sparks joy, and fuels bold momentum.", "Statement gold accents, sunlit knitwear, or warm neutral layering.", "Muddy gray or overly muted neutrals that dim your spark.", "Where did you feel most confident and alive today?"),
        HuePreset("Crimson Ember", "#E53935", "Champagne", "#F7E7CE", "Midnight Navy", "#131936", "Pale Mint", "#C8E6C9", "Channel decisive courage into action.", "Ignites initiative, sharpens drive, and projects poise.", "Tailored crimson pieces paired with crisp black or ivory.", "Washed-out pastels that soften your momentum.", "What bold step are you ready to take this week?"),
        HuePreset("Sunlit Amber", "#FFA726", "Sandstone", "#E6D3B3", "Espresso", "#3E2723", "Ice Slate", "#90A4AE", "Spark creative energy and warm connections.", "Boosts spontaneity, storytelling, and creative clarity.", "Amber accessories, terracotta layers, or warm tortoise details.", "Cold metallic tones that feel overly rigid.", "What creative idea deserves your attention right now?"),
        HuePreset("Coral Flame", "#FF7043", "Soft Ivory", "#FAF3E0", "Deep Slate", "#263238", "Ashen Brown", "#6D4C41", "Move forward with playful confidence.", "Lifts social warmth and invites spontaneous collaboration.", "Coral tops, warm blush accents, and light linen textures.", "Heavy, somber tones that weigh down your mood.", "Who brought positive energy into your day today?"),
        HuePreset("Copper Glow", "#D87A3A", "Pearl White", "#F4F1EA", "Charcoal Ink", "#1F2430", "Neon Lime", "#C6FF00", "Anchor ambition with grounded charisma.", "Balances fiery energy with patient execution.", "Bronze-toned hardware, camel outerwear, or copper details.", "Jarring neon shades that scatter your focus.", "What goal feels worth your steady persistence?"),
        HuePreset("Tangerine Spark", "#FB8C00", "Warm Linen", "#EFE6D5", "Ink Black", "#161822", "Muted Olive", "#6B705C", "Invite fresh enthusiasm into routines.", "Refreshes motivation and encourages bright curiosity.", "Warm citrus pops alongside clean monochrome staples.", "Overly drab tones when you need a mental reset.", "What small win made today feel brighter?"),
        HuePreset("Ruby Velvet", "#C62828", "Rose Quartz", "#F8BBD0", "Jet Black", "#141418", "Dusty Beige", "#BCAAA4", "Own your presence with quiet intensity.", "Deepens passion, focus, and magnetic self-assurance.", "Rich burgundy-red layers with sleek dark silhouettes.", "Flat, uninspired neutrals without contrast.", "What boundary or standard served you well today?")
    )

    private val earthHues = listOf(
        HuePreset("Verdant Green", "#388E3C", "Warm Oat", "#EADBC8", "Espresso", "#36261E", "Electric Neon", "#FF1744", "Stay rooted while welcoming steady growth.", "Invites calm clarity, abundance, and grounded resilience.", "Forest, moss, or emerald staples paired with earthy neutrals.", "Harsh neon tones that disrupt your natural balance.", "What habit is quietly compounding in your favor?"),
        HuePreset("Sage Mist", "#7CB342", "Chalk Cream", "#F5F2EB", "Deep Taupe", "#4E453D", "Stark Magenta", "#D81B60", "Breathe clarity into busy plans.", "Soothes mental clutter and restores deliberate focus.", "Soft sage shirts, olive trousers, and matte textures.", "Loud, clashing accents that pull your attention away.", "Where can you simplify your schedule tomorrow?"),
        HuePreset("Terracotta Clay", "#C86D51", "Sand Linen", "#EDE0D4", "Charcoal", "#2A2D34", "Icy Blue", "#B3E5FC", "Build something lasting with patience.", "Strengthens warmth, practicality, and tactile comfort.", "Rust, clay, and tan tones with organic cotton or suede.", "Overly sterile cool shades that feel detached.", "What made you feel grounded and at home today?"),
        HuePreset("Earthy Moss", "#8B8C3D", "Ivory Stone", "#EFECE6", "Dark Walnut", "#3B2F2F", "Bright Fuchsia", "#EC407A", "Organize details with calm precision.", "Supports analytical flow, balance, and quiet certainty.", "Olive-moss tailoring paired with crisp neutral layers.", "Distracting hyper-saturated tones.", "What task gave you the cleanest sense of progress?"),
        HuePreset("Steely Slate", "#607D8B", "Cloud Gray", "#CFD8DC", "Deep Navy", "#141D38", "Sloppy Neon", "#EEFF41", "Lead with quiet structure and focus.", "Sharpens discipline, reliability, and clear boundaries.", "Structured slate, stone gray, and tailored silhouettes.", "Unstructured brights that weaken your focus.", "What responsibility did you handle well today?"),
        HuePreset("Emerald Pine", "#2E7D32", "Warm Gold", "#E6C25A", "Off-Black", "#1A1D24", "Washed Lavender", "#D1C4E9", "Trust steady progress over shortcuts.", "Cultivates patience, poise, and long-term vision.", "Deep pine greens with subtle gold or brass details.", "Faded pastels without structure.", "What are you building that deserves more patience?"),
        HuePreset("Warm Ochre", "#D4A03E", "Bone White", "#F6F1E7", "Umber", "#4A3525", "Cold Silver", "#B0BEC5", "Turn practical ideas into tangible comfort.", "Nurtures resourcefulness and steady warmth.", "Mustard-ochre knitwear or warm camel accessories.", "Overly icy metallics.", "What small comfort made today feel restorative?")
    )

    private val airHues = listOf(
        HuePreset("Radiant Amber", "#FFB300", "Sky Tint", "#B3E5FC", "Graphite", "#262B38", "Heavy Maroon", "#5D1A2B", "Stay curious and spark fresh conversations.", "Quickens wit, openness, and creative brainstorming.", "Bright amber accents with crisp whites or denim.", "Overly heavy, dense tones that feel confining.", "What idea or conversation sparked your curiosity today?"),
        HuePreset("Electric Cyan", "#00BCD4", "Silver Mist", "#E0F7FA", "Deep Indigo", "#131C42", "Drab Brown", "#5D4037", "Think ahead and share original ideas.", "Amplifies mental clarity, invention, and independent flair.", "Cyan accessories, cool blues, and sharp minimalist lines.", "Traditional dull tones that mute your originality.", "What fresh perspective surprised you today?"),
        HuePreset("Soft Coral", "#FF6F61", "Blush Cream", "#FFEBE6", "Slate Navy", "#1E2749", "Harsh Rust", "#8D3B24", "Create harmony in every connection.", "Softens tension, boosts diplomacy, and lifts mood.", "Coral accents, balanced silhouettes, and airy fabrics.", "Aggressive, high-contrast combos that feel rushed.", "Where did balance or kindness show up in your day?"),
        HuePreset("Periwinkle Sky", "#7986CB", "Cloud White", "#F1F4FF", "Midnight", "#121735", "Muddy Olive", "#555B3E", "Let your thoughts flow lightly and clearly.", "Calms overthinking while keeping inspiration high.", "Soft indigo-blue layers with silver or white sneakers.", "Heavy, cluttered patterns.", "What helped clear your head today?"),
        HuePreset("Mint Breeze", "#26A69A", "Cool Pearl", "#E0F2F1", "Charcoal Blue", "#232B3E", "Burnt Umber", "#6D3B24", "Refresh your routine with light agility.", "Encourages adaptability, calm speech, and fresh energy.", "Teal-mint tones with crisp neutral basics.", "Overly dark or rigid palettes.", "What changed in your plans today—and how did you adapt?"),
        HuePreset("Cerulean Pulse", "#29B6F6", "Ice White", "#F4FAFF", "Steel Gray", "#37474F", "Dull Mustard", "#9E8226", "Communicate clearly and listen deeply.", "Sharpens articulation and social magnetism.", "Clean sky blues paired with light gray or navy.", "Flat earthy tones that feel sluggish.", "What message or thought do you want to express clearly?"),
        HuePreset("Lavender Haze", "#9575CD", "Moon White", "#F3EFFE", "Deep Plum", "#261B47", "Stark Orange", "#FF6D00", "Blend imagination with effortless grace.", "Inspires lateral thinking and gentle focus.", "Soft violet or lilac touches over dark neutrals.", "Clashing bright orange that breaks your flow.", "What daydream or future plan felt exciting today?")
    )

    private val waterHues = listOf(
        HuePreset("Ocean Blue", "#1E88E5", "Seafoam", "#B2EBF2", "Deep Navy", "#0F1A3D", "Neon Red", "#FF1744", "Trust your intuition and creative flow.", "Deepens empathy, calm imagination, and emotional poise.", "Ocean blues, fluid layers, and relaxed silhouettes.", "Harsh, jarring reds that disturb inner calm.", "What feeling or intuition is guiding you right now?"),
        HuePreset("Moonstone Silver", "#B0BEC5", "Frost Blue", "#E1F5FE", "Midnight Ink", "#10162F", "Blazing Orange", "#FF5722", "Protect your peace and listen within.", "Soothes sensitivity and sharpens quiet perception.", "Cool silver, soft gray, and clean minimalist jewelry.", "Overwhelmingly loud tones.", "What helped you protect your peace today?"),
        HuePreset("Deep Burgundy", "#9E1B3B", "Rose Mist", "#F4D0D9", "Obsidian", "#141522", "Washed Pastel", "#E0E0E0", "Channel emotional depth into resilience.", "Strengthens determination, intuition, and quiet power.", "Rich burgundy outerwear or accents with dark denim.", "Pale, washed-out tones that feel underpowered.", "What challenge showed you how resilient you are?"),
        HuePreset("Seafoam Teal", "#00897B", "Pearl Mist", "#E0F2F1", "Slate Indigo", "#1A2547", "Heavy Rust", "#793524", "Restore balance between heart and mind.", "Promotes emotional renewal and gentle clarity.", "Teal knits or soft aqua highlights with cool neutrals.", "Abrasive contrast colors.", "What refreshed your spirit today?"),
        HuePreset("Amethyst Indigo", "#7E57C2", "Silver Lilac", "#EDE7F6", "Midnight Navy", "#0D1333", "Sharp Chartreuse", "#D4E157", "Honor quiet reflection and creative dreams.", "Deepens insight, artistry, and peaceful focus.", "Muted plum or indigo layers with silver accents.", "Electric yellow-green that pulls focus outward.", "What quiet moment did you appreciate most today?"),
        HuePreset("Sapphire Tide", "#1565C0", "Cloud Mist", "#E3F2FD", "Charcoal", "#1D2433", "Faded Tan", "#C7B299", "Move with calm, steady confidence.", "Anchors emotions while supporting expressive depth.", "Rich cobalt or sapphire tones with crisp white shirts.", "Flat beige without depth.", "Where did you show patience with yourself today?"),
        HuePreset("Twilight Plum", "#8E24AA", "Soft Ivory", "#F8F3FA", "Night Slate", "#1B1E32", "Bright Lime", "#AEEA00", "Transform reflection into quiet creativity.", "Encourages introspection, warmth, and artistic vision.", "Deep berry-plum tones paired with soft neutrals.", "Hyper-bright tones when unwinding.", "What do you want to carry forward into tomorrow?")
    )

    private val powerWindows = listOf(
        "8:00 AM – 11:00 AM",
        "10:00 AM – 1:00 PM",
        "1:00 PM – 4:00 PM",
        "3:00 PM – 6:00 PM",
        "5:30 PM – 8:30 PM",
        "7:00 PM – 10:00 PM"
    )

    fun todayEpochDay(): Long {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        return year * 366L + dayOfYear
    }

    fun getDailyHue(sign: String, dayOffset: Int = 0, baseEpochDay: Long = todayEpochDay()): DailyHueSnapshot {
        val zodiac = ZodiacProvider.getZodiacDataForSign(sign)
        val signIndex = ZodiacProvider.allZodiacData.indexOfFirst {
            it.sign.equals(zodiac.sign, ignoreCase = true)
        }.coerceAtLeast(0)

        val targetEpochDay = baseEpochDay + dayOffset
        val pool = when (zodiac.element) {
            "Fire" -> fireHues
            "Earth" -> earthHues
            "Air" -> airHues
            else -> waterHues
        }

        // Day 0 uses a deterministic rotation by sign + day so today varies across days;
        // if preset 0 matches the sign's signature color, it rotates cleanly through all 7 hues.
        val presetIndex = abs((targetEpochDay + signIndex).toInt()) % pool.size
        val preset = pool[presetIndex]

        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, dayOffset) }
        val fullDateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        val shortFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dateLabel = when (dayOffset) {
            0 -> "Today • ${fullDateFormat.format(cal.time)}"
            1 -> "Tomorrow • ${fullDateFormat.format(cal.time)}"
            else -> fullDateFormat.format(cal.time)
        }
        val shortLabel = if (dayOffset == 0) "Today" else shortFormat.format(cal.time)

        val seed = abs((targetEpochDay * 31L + signIndex * 17L).toInt())
        val energy = 64 + (seed % 34)
        val focus = 62 + ((seed / 3 + 11) % 36)
        val social = 65 + ((seed / 7 + 19) % 33)
        val luckyNumber = 1 + (seed % 99)
        val powerWindow = powerWindows[seed % powerWindows.size]

        val reading = "${preset.name} aligns with ${zodiac.sign}'s ${zodiac.element.lowercase()} nature today. ${preset.tagline} ${preset.mood}"

        val harmony = listOf(
            ColorSwatch("Wear", preset.name, preset.hex),
            ColorSwatch("Accent", preset.accentName, preset.accentHex),
            ColorSwatch("Base", preset.baseName, preset.baseHex),
            ColorSwatch("Avoid", preset.avoidName, preset.avoidHex)
        )

        return DailyHueSnapshot(
            sign = zodiac.sign,
            emoji = zodiac.emoji,
            dateRange = zodiac.dateRange,
            element = zodiac.element,
            dayOffset = dayOffset,
            dateLabel = dateLabel,
            shortDayLabel = shortLabel,
            primaryColorName = preset.name,
            primaryColorHex = preset.hex,
            tagline = preset.tagline,
            harmonyPalette = harmony,
            energyScore = energy,
            focusScore = focus,
            socialScore = social,
            luckyNumber = luckyNumber,
            powerWindow = powerWindow,
            moodInfluence = preset.mood,
            aestheticToWear = preset.wear,
            avoid = preset.avoidNote,
            dailyReading = reading,
            journalPrompt = preset.prompt,
            mainImageRes = zodiac.mainImageRes,
            galleryImages = zodiac.galleryImages
        )
    }

    fun getSevenDayForecast(sign: String, baseEpochDay: Long = todayEpochDay()): List<DailyHueSnapshot> {
        return (0..6).map { offset ->
            getDailyHue(sign = sign, dayOffset = offset, baseEpochDay = baseEpochDay)
        }
    }
}
