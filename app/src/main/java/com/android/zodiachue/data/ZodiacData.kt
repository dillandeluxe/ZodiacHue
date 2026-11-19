package com.android.zodiachue.data

data class ZodiacData(
    val colorName: String,
    val colorHex: String,
    val description: String,
    val reading: String,
    val mainImageRes: Int,
    val galleryImages: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ZodiacData) return false

        return colorName == other.colorName &&
                colorHex == other.colorHex &&
                description == other.description &&
                reading == other.reading &&
                mainImageRes == other.mainImageRes &&
                galleryImages contentEquals other.galleryImages
    }

    override fun hashCode(): Int {
        var result = colorName.hashCode()
        result = 31 * result + colorHex.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + reading.hashCode()
        result = 31 * result + mainImageRes
        result = 31 * result + galleryImages.contentHashCode()
        return result
    }
}
