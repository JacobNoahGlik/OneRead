package com.oneread


import com.oneread.crypto.Crypto
import java.nio.file.Files
import kotlin.test.assertContentEquals
import org.junit.jupiter.api.Test


class CryptoTest {
@Test
fun roundtrip() {
val tmp = Files.createTempFile("keyset", ".json")
val aead = Crypto.loadOrCreateAead(tmp)
val pt = "hello".toByteArray()
val ct = aead.encrypt(pt, byteArrayOf())
val got = aead.decrypt(ct, byteArrayOf())
assertContentEquals(pt, got)
}
}
