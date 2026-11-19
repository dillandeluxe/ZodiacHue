package com.android.zodiachue.helper

import com.android.zodiachue.R
import com.android.zodiachue.data.ZodiacData

object ZodiacProvider {
    fun getZodiacDataForSign(zodiacSign: String): ZodiacData {
        return when (zodiacSign) {
            "Aries" -> ZodiacData(
                colorName = "Crimson Red",
                colorHex = "#D32F2F",
                description = "Crimson Red represents Aries' fiery, passionate, and courageous nature. This bold color ignites action, fueling ambition and assertiveness.\n\n✨ Mood Influence: Amplifies confidence, fuels ambition, and fosters boldness.\n🎨 Aesthetic to Wear: Red garments such as jackets, shirts, or bold accessories paired with black or white.\n🚫 Avoid: Soft, muted colors that could diminish your dynamic presence.",
                reading = "Zodiac Reading: The dynamic crimson red will empower your leadership and assertiveness. It's a day to take charge and shine with fiery energy.",
                mainImageRes = R.drawable.aries_color_of_the_day,
                galleryImages = intArrayOf(R.drawable.aries_img1, R.drawable.aries_img2)
            )
            "Taurus" -> ZodiacData(
                colorName = "Verdant Green",
                colorHex = "#388E3C",
                description = "Verdant Green mirrors Taurus’ strong connection to nature and its earthy, reliable qualities. This refreshing color embodies growth, stability, and prosperity.\n\n✨ Mood Influence: Invites tranquility, nurtures prosperity, and strengthens resilience.\n🎨 Aesthetic to Wear: Green tones such as emerald, moss, or deep forest paired with earthy neutrals.\n🚫 Avoid: Harsh or neon colors that disrupt the calming effect of nature.",
                reading = "Zodiac Reading: Verdant green will bring you a sense of calm and balance today. Focus on growth, both personal and material, and trust the steady rhythm of your efforts.",
                mainImageRes = R.drawable.taurus_color_of_the_day,
                galleryImages = intArrayOf(R.drawable.taurus_img1, R.drawable.taurus_img2)
            )
            "Gemini" -> ZodiacData(
                colorName = "Radiant Amber",
                colorHex = "#FFB300",
                description = "Radiant Amber embodies Gemini's lively, ever-changing energy and inquisitive nature. This vibrant, golden-yellow hue brings warmth, creativity, and mental stimulation.\n\n✨ Mood Influence: Enhances curiosity, sparks enthusiasm, and promotes open-mindedness.\n🎨 Aesthetic to Wear: Bright amber accents, like scarves, jewelry, or shoes, paired with neutral tones.\n🚫 Avoid: Dark, heavy colors that may dampen your natural vibrancy and intellectual spark.",
                reading = "Zodiac Reading: Radiant amber will amplify your creativity and communication today, Gemini. Embrace new opportunities to learn and connect with others, as this color stimulates your intellectual and social energies.",
                mainImageRes = R.drawable.gemini_color_of_the_day,
                galleryImages = intArrayOf(R.drawable.gemini_img1, R.drawable.gemini_img2)
            )
            "Cancer" -> ZodiacData(
                colorName = "Moonstone Silver",
                colorHex = "#B0BEC5",
                description = "Moonstone Silver reflects Cancer’s emotional depth and nurturing nature. This calm, reflective color promotes emotional clarity and intuition.\n\n✨ Mood Influence: Calms the mind, fosters intuition, and enhances emotional balance.\n🎨 Aesthetic to Wear: Soft silvers and cool tones to create an atmosphere of peace.\n🚫 Avoid: Dark or intense colors that might overwhelm your sensitivity.",
                reading = "Zodiac Reading: Moonstone silver will guide your emotional clarity today, Cancer. Trust your instincts and allow your natural intuition to steer your decisions.",
                mainImageRes = R.drawable.cancer_color_of_the_day,
                galleryImages = intArrayOf(R.drawable.cancer_img1, R.drawable.cancer_img2)
            )
            "Leo" -> ZodiacData(
                colorName = "Golden Yellow",
                colorHex = "#FFD700",
                description = "Golden Yellow radiates confidence, vitality, and charisma—traits deeply connected with the Leo zodiac. This color energizes the Leo spirit, boosting mood and inspiring creativity and boldness.\n\n✨ Mood Influence: Elevates positivity, sparks joy, and fuels ambition.\n\uD83C\uDFA8 Aesthetic to Wear: Flowing outfits, statement pieces, or accessories in golden yellow paired with warm neutral colors.\n\uD83D\uDEAB Avoid: Dull or overly dark tones like charcoal or navy that can dim your shine.",
                reading = "Zodiac Reading: As a fire sign ruled by the Sun, Leo thrives under golden hues. Today, golden yellow helps amplify your natural magnetism and attract abundance. Let your presence shine as brightly as your ruling star.",
                mainImageRes = R.drawable.leo_color_of_the_day,
                galleryImages = intArrayOf(R.drawable.leo_img1, R.drawable.leo_img2)
            )
            "Virgo" -> ZodiacData(
                colorName = "Earthy Moss Green",
                colorHex = "#8B8C3D",
                description = "Earthy Moss Green reflects Virgo's grounded and practical nature. This calming color symbolizes nature, growth, and balance, which resonates with Virgo's analytical traits.\n\n✨ Mood Influence: Enhances focus, stability, and helps you stay grounded.\n🎨 Aesthetic to Wear: Soft green hues paired with neutral, earthy tones.\n🚫 Avoid: Bright or neon colors that may disrupt Virgo's need for calm and order.",
                reading = "Zodiac Reading: Earthy moss green will support your analytical mindset today. Stay focused, grounded, and patient with your tasks to achieve success.",
                mainImageRes = R.drawable.virgo_color_of_the_day,
                galleryImages = intArrayOf(R.drawable.virgo_img1, R.drawable.virgo_img2)
            )
            "Libra" -> ZodiacData(
                colorName = "Soft Coral Pink",
                colorHex = "#FF6F61",
                description = "Soft Coral Pink reflects Libra's harmonious, balanced, and beauty-seeking nature. This gentle color promotes peace, love, and balance.\n\n✨ Mood Influence: Enhances relationships, calms emotions, and nurtures your inner peace.\n🎨 Aesthetic to Wear: Coral pink accessories, dresses, or gentle hues to create a harmonious atmosphere.\n🚫 Avoid: Aggressive or overly bright colors that disturb your sense of equilibrium.",
                reading = "Zodiac Reading: Soft coral pink will enhance your sense of balance today, Libra. Use this color to foster peaceful connections and create harmony around you.",
                mainImageRes = R.drawable.libra_color_of_the_day,
                galleryImages = intArrayOf(R.drawable.libra_img1, R.drawable.libra_img2)
            )
            "Scorpio" -> ZodiacData(
                colorName = "Deep Burgundy",
                colorHex = "#800020",
                description = "Deep Burgundy represents Scorpio’s passionate, intense, and transformative nature. This dark, rich color reflects their powerful will and emotional depth.\n\n✨ Mood Influence: Elevates strength, determination, and enhances focus.\n🎨 Aesthetic to Wear: Bold burgundy outfits and accessories that represent your powerful aura.\n🚫 Avoid: Lighter shades that might detract from your intensity and inner strength.",
                reading = "Zodiac Reading: Deep burgundy amplifies your strength and resilience today. Harness this power to overcome challenges and transform obstacles into opportunities.",
                mainImageRes = R.drawable.scorpio_color_of_the_day,
                galleryImages = intArrayOf(R.drawable.scorpio_img1, R.drawable.scorpio_img2)
            )
            "Sagittarius" -> ZodiacData(
                colorName = "Vibrant Violet",
                colorHex = "#8A2BE2",
                description = "Vibrant Violet represents Sagittarius' adventurous and philosophical nature. This intense color symbolizes expansion, exploration, and creative energy.\n\n✨ Mood Influence: Sparks creativity, broadens horizons, and boosts optimism.\n🎨 Aesthetic to Wear: Bold violet garments or accessories to express your free-spirited personality.\n🚫 Avoid: Stagnant or dull tones that may hold you back from exploring new ideas.",
                reading = "Zodiac Reading: Vibrant violet will fuel your adventurous spirit today, Sagittarius. Embrace new experiences and ideas that help you expand your knowledge and worldview.",
                mainImageRes = R.drawable.sagittarius_color_of_the_day,
                galleryImages = intArrayOf(R.drawable.sagittarius_img1, R.drawable.sagittarius_img2)
            )
            "Capricorn" -> ZodiacData(
                colorName = "Steely Gray",
                colorHex = "#607D8B",
                description = "Steely Gray reflects Capricorn's disciplined, methodical, and determined nature. This strong, neutral color symbolizes stability, responsibility, and resilience.\n\n✨ Mood Influence: Enhances focus, persistence, and helps in organizing your tasks.\n🎨 Aesthetic to Wear: Gray tones paired with structured pieces for a sharp, professional look.\n🚫 Avoid: Bright or unstructured colors that detract from your disciplined focus.",
                reading = "Zodiac Reading: Steely gray today will sharpen your focus and determination. Use this color to organize your efforts and move forward with steadfast resolve.",
                mainImageRes = R.drawable.capricorn_color_of_the_day,
                galleryImages = intArrayOf(R.drawable.capricorn_img1, R.drawable.capricorn_img2)
            )
            "Aquarius" -> ZodiacData(
                colorName = "Electric Cyan",
                colorHex = "#00BCD4",
                description = "Electric Cyan symbolizes Aquarius’ innovative, eccentric, and intellectual spirit. This striking color represents clarity, vision, and creative thinking.\n\n✨ Mood Influence: Sparks inventive ideas, promotes independence, and enhances your futuristic thinking.\n🎨 Aesthetic to Wear: Electric cyan accessories or clothing to highlight your unique and forward-thinking personality.\n🚫 Avoid: Traditional colors that may stifle your originality.",
                reading = "Zodiac Reading: Electric cyan will energize your creative endeavors today, Aquarius. It’s the perfect time to break free from convention and embrace your visionary ideas.",
                mainImageRes = R.drawable.aquarius_color_of_the_day,
                galleryImages = intArrayOf(R.drawable.aquarius_img1, R.drawable.aquarius_img2)
            )
            "Pisces" -> ZodiacData(
                colorName = "Ocean Blue",
                colorHex = "#1E88E5",
                description = "Ocean Blue reflects Pisces' dreamy, intuitive, and compassionate nature. This serene color embodies emotional depth, tranquility, and spiritual connection.\n\n✨ Mood Influence: Enhances empathy, fosters peace, and nurtures creativity.\n🎨 Aesthetic to Wear: Soft blue tones, flowy garments, and accessories that reflect your peaceful energy.\n🚫 Avoid: Harsh or overly bright colors that may disrupt your calm and creative flow.",
                reading = "Zodiac Reading: Ocean blue will inspire your creativity and empathy today, Pisces. Let the tranquility of the sea guide you to new insights and connections.",
                mainImageRes = R.drawable.pisces_color_of_the_day,
                galleryImages = intArrayOf(R.drawable.pisces_img1, R.drawable.pisces_img2)
            )
            else -> throw IllegalArgumentException("Unknown zodiac sign: $zodiacSign")
        }
    }
}
