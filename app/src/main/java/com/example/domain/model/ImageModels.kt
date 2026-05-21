package com.example.domain.model

data class GeneratedArtwork(
    val id: Long = 0,
    val prompt: String,
    val negativePrompt: String? = null,
    val style: ImageStyle,
    val aspectRatio: AspectRatio,
    val quality: ImageQuality,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: String, // Can be a local absolute path or standard file Uri
    val isFavorite: Boolean = false,
    val isDownloaded: Boolean = false
)

enum class ImageStyle(val displayName: String, val promptEnhancement: String, val iconResName: String) {
    REALISTIC(
        "Realistic",
        "extremely detailed, photorealistic, 8k resolution, highly polished, cinematic lighting, dramatic shadows, professional photography",
        "ic_style_realistic"
    ),
    ANIME(
        "Anime",
        "beautiful modern anime style, dynamic pose, vibrant digital illustration, gorgeous anime key art, detailed line art, aesthetic cel shading",
        "ic_style_anime"
    ),
    CARTOON(
        "Cartoon",
        "charming 2D traditional cartoon style, bold outlines, friendly characters, expressive design, vibrant playful color palette, whimsical backdrop",
        "ic_style_cartoon"
    ),
    CYBERPUNK(
        "Cyberpunk",
        "cyberpunk aesthetic, glow neon lighting, rain-slicked dark futuristic streets, high-tech gadgets, hologram elements, purple and teal cinematic colors",
        "ic_style_cyberpunk"
    ),
    FANTASY(
        "Fantasy",
        "magical fantasy painting, ethereal glow, mythical creatures, breathtaking landscape, epic scale, soft magical light, intricate details, fairy tale concept art",
        "ic_style_fantasy"
    ),
    THREE_D_RENDER(
        "3D Render",
        "stunning 3D octanerender graphic, soft modeling, clay style, miniature toy world aesthetic, volumetric lighting, beautiful materials, sleek glossy textures",
        "ic_style_3d"
    );

    companion object {
        fun fromName(name: String): ImageStyle = entries.find { it.name == name } ?: REALISTIC
    }
}

enum class AspectRatio(val displayName: String, val ratio: Float, val wLabel: Int, val hLabel: Int) {
    RATIO_1_1("1:1", 1.0f, 1, 1),
    RATIO_9_16("9:16", 9f / 16f, 9, 16),
    RATIO_16_9("16:9", 16f / 9f, 16, 9),
    RATIO_4_3("4:3", 4f / 3f, 4, 3);

    companion object {
        fun fromName(name: String): AspectRatio = entries.find { it.name == name } ?: RATIO_1_1
    }
}

enum class ImageQuality(val displayName: String) {
    STANDARD("Standard"),
    HD("HD");

    companion object {
        fun fromName(name: String): ImageQuality = entries.find { it.name == name } ?: STANDARD
    }
}

enum class ImageReferenceMode(val displayName: String, val emoji: String, val description: String) {
    INSPIRE("Inspire", "✨", "Generate new image inspired by style"),
    EDIT("Edit Image", "✏️", "Transform / edit the uploaded image");

    companion object {
        fun fromName(name: String): ImageReferenceMode = entries.find { it.name == name } ?: INSPIRE
    }
}
