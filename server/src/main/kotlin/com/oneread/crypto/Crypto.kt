package com.oneread.crypto


import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.JsonKeysetReader
import com.google.crypto.tink.JsonKeysetWriter
import java.nio.file.Files
import java.nio.file.Path


object Crypto {
    init { AeadConfig.register() }
    
    
    fun loadOrCreateAead(keysetPath: Path): Aead {
        if (!Files.exists(keysetPath)) {
            Files.createDirectories(keysetPath.parent ?: Path.of("."))
            val handle = KeysetHandle.generateNew(KeyTemplates.get("AES256_GCM"))
            handle.write(JsonKeysetWriter.withPath(keysetPath), /* masterKey = */ null)
        }
        val handle = KeysetHandle.read(JsonKeysetReader.withPath(keysetPath), /* masterKey = */ null)
        return handle.getPrimitive(Aead::class.java)
    }
}
