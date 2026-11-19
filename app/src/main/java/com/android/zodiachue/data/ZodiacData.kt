package com.android.zodiachue.data

data class ZodiacData(
    val sign: String,
    val emoji: String,
    val dateRange: String,
    val element: String,
    val colorName: String,
    val colorHex: String,
    val overview: String,
    val moodInfluence: String,
    val aestheticToWear: String,
    val avoid: String,
    val reading: String,
    val mainImageRes: Int,
    val galleryImages: IntArray
) {
    /** Combined description preserved for compatibility and sharing. */
    val description: String
        get() = "$overview\n\n✨ Mood Influence: $moodInfluence\n🎨 Aesthetic to Wear: $aestheticToWear\n🚫 Avoid: $avoid"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ZodiacData) return false

        return sign == other.sign &&
            emoji == other.emoji &&
            dateRange == other.dateRange &&
            element == other.element &&
            colorName == other.colorName &&
            colorHex == other.colorHex &&
            overview == other.overview &&
            moodInfluence == other.moodInfluence &&
            aestheticToWear == other.aestheticToWear &&
            avoid == other.avoid &&
            reading == other.reading &&
            mainImageRes == other.mainImageRes &&
            galleryImages contentEquals other.galleryImages
    }

    override fun hashCode(): Int {
        var result = sign.hashCode()
        result = 31 * result + colorName.hashCode()
        result = 31 * result + colorHex.hashCode()
        result = 31 * result + overview.hashCode()
        result = 31 * result + reading.hashCode()
        result = 31 * result + mainImageRes
        result = 31 * result + galleryImages.contentHashCode()
        return result
    }
}
