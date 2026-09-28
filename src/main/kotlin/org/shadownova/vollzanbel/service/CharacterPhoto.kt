package org.shadownova.vollzanbel.service

import java.util.Base64
import javax.imageio.ImageIO
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

/** Photos arrive already resized and JPEG-compressed, then live in the gzip snapshot. */
fun validateCharacterPhoto(avatar: String) {
    if (!avatar.startsWith("data:")) {
        if (avatar.length > 128) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid avatar")
        return
    }
    try {
        require(avatar.startsWith("data:image/jpeg;base64,") && avatar.length <= 550_000)
        val bytes = Base64.getDecoder().decode(avatar.substringAfter(','))
        ImageIO.createImageInputStream(bytes.inputStream()).use { input ->
            val readers = ImageIO.getImageReaders(input)
            require(readers.hasNext())
            val reader = readers.next()
            try {
                reader.input = input
                require(reader.formatName.equals("JPEG", true))
                require(reader.getWidth(0) in 1..512 && reader.getHeight(0) in 1..512)
                require(reader.read(0) != null)
            } finally { reader.dispose() }
        }
    } catch (e: Exception) {
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload a compressed JPEG photo no larger than 512 pixels", e)
    }
}
